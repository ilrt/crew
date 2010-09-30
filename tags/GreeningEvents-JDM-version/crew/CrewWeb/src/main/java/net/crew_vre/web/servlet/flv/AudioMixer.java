/*
 * @(#)AudioMixer.java
 * Created: 18 Feb 2008
 * Version: 1.0
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.crew_vre.web.servlet.flv;

import java.io.IOException;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.UnsupportedFormatException;

import net.crew_vre.media.MemeticFileReader;
import net.crew_vre.media.processor.SimpleProcessor;

/**
 * A stream from which mixed audio is read
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class AudioMixer {

    private static final int SAMPLES_PER_BUFFER = 882;

    private static final double SAMPLE_RATE = 44100.0;

    private static final double NANOS_PER_SAMPLE = 1000000000.0 / SAMPLE_RATE;

    private static final long BUFFER_DURATION =
        (long) (SAMPLES_PER_BUFFER * NANOS_PER_SAMPLE);

    private MemeticFileReader[] sources = null;

    private SimpleProcessor[] processors = null;

    private Buffer[] buffers = null;

    private byte[][] convertBuffers = null;

    private boolean[] isFinished = null;

    private long[] offsets = null;

    private long minStartTime = 0;

    private AudioFormat outputFormat = null;

    private long startTimestamp = Long.MAX_VALUE;

    private long endTimestamp = -1;

    private byte[] data = null;

    private int bytesPerSample = 0;

    private int channels = 0;

    private int maxSample = 0;

    /**
     * Creates a new AudioMixer
     * @param sources The linear audio sources to mix
     * @throws UnsupportedFormatException
     */
    public AudioMixer(MemeticFileReader[] sources)
            throws UnsupportedFormatException {
        this.sources = sources;
        processors = new SimpleProcessor[sources.length];
        buffers = new Buffer[sources.length];
        isFinished = new boolean[sources.length];
        offsets = new long[sources.length];
        convertBuffers = new byte[sources.length][];
        minStartTime = sources[0].getStartTime();
        for (int i = 0; i < sources.length; i++) {
            if (sources[i].getStartTime() < minStartTime) {
                minStartTime = sources[i].getStartTime();
            }
        }
        for (int i = 0; i < sources.length; i++) {
            offsets[i] = sources[i].getStartTime() - minStartTime;
            sources[i].setTimestampOffset(offsets[i]);
        }
        init();
    }

    protected void streamSeek(long offset) throws IOException {
        for (int i = 0; i < sources.length; i++) {
            System.err.println("Seeking source " + i + " to offset " + (offset - offsets[i]));
            sources[i].streamSeek(offset - offsets[i]);
        }
    }

    protected long getStartTime() {
        return minStartTime;
    }

    protected long getTimestamp() {
        return startTimestamp;
    }

    protected void setTimestampOffset(long timestampOffset) {
        for (int i = 0; i < sources.length; i++) {
            sources[i].setTimestampOffset(offsets[i] + timestampOffset);
        }
    }

    protected Format getFormat() {
        return outputFormat;
    }

    private boolean readBuffer(int i) throws IOException {
        MemeticFileReader stream = sources[i];
        boolean read = false;
        if (stream.readNextPacket()) {
            read = true;
            buffers[i] = stream.getBuffer();
            Format format = stream.getFormat();
            if (!(format instanceof AudioFormat)) {
                throw new IOException(
                        "Only Audio Formats Supported: " + format);
            }

            if (processors[i] != null) {
                processors[i].process(buffers[i]);
                buffers[i] = processors[i].getOutputBuffer();
                format = buffers[i].getFormat();
            }

            AudioFormat af = (AudioFormat) format;
            if ((af.getSampleRate() < SAMPLE_RATE)
                    || (af.getEndian() != AudioFormat.LITTLE_ENDIAN)
                    || (af.getChannels() != channels)) {
                double ratio = af.getSampleRate() / SAMPLE_RATE;
                int inLength = buffers[i].getLength();
                int inOffset = buffers[i].getOffset();
                int inEnd = inLength + inOffset;
                byte[] inData = (byte[]) buffers[i].getData();
                int outLength = (int) (((inLength * SAMPLE_RATE)
                        / af.getSampleRate()) + 0.5);
                if ((convertBuffers[i] == null)
                        || (convertBuffers[i].length != outLength)) {
                    convertBuffers[i] = new byte[outLength];
                }
                int inPos = inOffset;
                int outPos = 0;
                double sum = 0.0;
                while (inPos < inEnd) {
                    for (int c = 0; c < channels; c++) {
                        int val = 0;
                        for (int j = 0; j < bytesPerSample; j++) {
                            int shift = (3 - (bytesPerSample - j)) * 8;
                            if (af.getEndian() == AudioFormat.LITTLE_ENDIAN) {
                                shift = j * 8;
                            }
                            int inArrayPos = inPos + (c * bytesPerSample);
                            val |= (inData[inArrayPos + j] & 0xFF) << shift;
                        }

                        while (sum < 1.0) {
                            if (outPos < outLength) {
                                int v = val;
                                for (int j = 0; j < bytesPerSample; j++) {
                                    convertBuffers[i][outPos++] =
                                        (byte) (v & 0xFF);
                                    v >>= 8;
                                }
                            }
                            sum += ratio;
                        }
                    }
                    sum -= 1.0;
                    inPos += bytesPerSample * channels;
                }
                buffers[i].setData(convertBuffers[i]);
                buffers[i].setLength(convertBuffers[i].length);
                buffers[i].setOffset(0);
            }
        }
        isFinished[i] = !read;
        return read;
    }

    private void init() throws UnsupportedFormatException {
        int maxChannels = 0;
        int maxBitsPerSample = 0;
        for (int i = 0; i < buffers.length; i++) {
            AudioFormat af = (AudioFormat) sources[i].getFormat();
            System.err.println("Source " + i + " has format " + af);
            if (!af.getEncoding().equals(AudioFormat.LINEAR)) {
                processors[i] = new SimpleProcessor(af,
                        new AudioFormat(AudioFormat.LINEAR));
                af = (AudioFormat) processors[i].getOutputFormat();
            }
            if (af.getChannels() > maxChannels) {
                maxChannels = af.getChannels();
            }
            if (af.getSampleSizeInBits() > maxBitsPerSample) {
                maxBitsPerSample = af.getSampleSizeInBits();
            }
        }

        outputFormat = new AudioFormat(AudioFormat.LINEAR, SAMPLE_RATE,
            maxBitsPerSample, maxChannels, AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);

        this.bytesPerSample = maxBitsPerSample / 8;
        this.channels = maxChannels;
        this.maxSample = (1 << (maxBitsPerSample - 1)) - 1;
        System.err.println("Max Sample = " + maxSample);

        data = new byte[SAMPLES_PER_BUFFER * (maxBitsPerSample / 8)
                        * maxChannels];
    }

    private boolean readSamples(int buffer) throws IOException {
        boolean dataAdded = false;
        if (!isFinished[buffer]) {
            long timestamp = buffers[buffer].getTimeStamp();
            if ((endTimestamp >= timestamp)) {
                int offset = buffers[buffer].getOffset();
                int length = buffers[buffer].getLength();
                int end = offset + length;
                int timestampOffset = (int) ((startTimestamp - timestamp)
                    / NANOS_PER_SAMPLE) * bytesPerSample * channels;
                byte[] inData = (byte[]) buffers[buffer].getData();

                for (int i = 0; i < SAMPLES_PER_BUFFER; i++) {
                    int pos = (bytesPerSample * channels * i);
                    int inPos = offset + timestampOffset + pos;
                    if (inPos >= offset) {
                        if (inPos >= end) {
                            if (readBuffer(buffer)) {
                                timestamp = buffers[buffer].getTimeStamp();
                                offset = buffers[buffer].getOffset();
                                length = buffers[buffer].getLength();
                                end = offset + length;
                                timestampOffset = (int)
                                    ((startTimestamp - timestamp)
                                        / NANOS_PER_SAMPLE)
                                        * bytesPerSample * channels;
                                inData = (byte[]) buffers[buffer].getData();
                                inPos = offset + timestampOffset + pos;
                            }
                        }
                        if (!isFinished[buffer] && (endTimestamp >= timestamp)) {
                            if (inPos >= offset) {
                                for (int j = 0; j < channels; j++) {
                                    int outArrayPos = pos + (j * bytesPerSample);
                                    int inArrayPos = inPos + (j * bytesPerSample);
                                    int outSample = 0;
                                    int inSample = 0;
                                    for (int k = 0; k < bytesPerSample; k++) {
                                        if ((inArrayPos + k) >= end) {
                                            inArrayPos = end - k - 1;
                                        }
                                        int shift = (4 - (bytesPerSample - k)) * 8;
                                        outSample |= (data[outArrayPos + k] & 0xFF) << shift;
                                        inSample |= (inData[inArrayPos + k] & 0xFF) << shift;
                                    }
                                    int shift = (4 - bytesPerSample) * 8;
                                    outSample = (outSample >> shift);
                                    inSample = (inSample >> shift);

                                    outSample += inSample;

                                    for (int k = 0; k < bytesPerSample; k++) {
                                        shift = k * 8;
                                        data[outArrayPos + k] = (byte)
                                                ((outSample >> shift) & 0xFF);
                                    }
                                    dataAdded = true;
                                }
                            }
                        } else {
                            i = SAMPLES_PER_BUFFER;
                        }
                    }
                }
                int pos = bytesPerSample * channels * SAMPLES_PER_BUFFER;
                int inPos = offset + timestampOffset + pos;
                if (inPos >= end) {
                    readBuffer(buffer);
                } else if (inPos > offset) {
                    buffers[buffer].setOffset(inPos);
                    buffers[buffer].setLength(length - pos);
                    buffers[buffer].setTimeStamp(endTimestamp);
                }
            }

            if (endTimestamp < timestamp) {
                return true;
            }
        }
        return dataAdded;
    }

    protected boolean readNextBuffer() throws IOException {
        for (int i = 0; i < data.length; i++) {
            data[i] = 0;
        }

        for (int i = 0; i < sources.length; i++) {
            if (buffers[i] == null) {
                readBuffer(i);
            }
        }

        // Work out the start timestamp using the buffers
        if (endTimestamp == -1) {
            startTimestamp = Long.MAX_VALUE;
            for (int i = 0; i < buffers.length; i++) {
                if (!isFinished[i]
                        && (buffers[i].getTimeStamp() < startTimestamp)) {
                    startTimestamp = buffers[i].getTimeStamp();
                }
            }
        } else {
            startTimestamp = endTimestamp;
        }
        endTimestamp = startTimestamp + BUFFER_DURATION;

        // Read data from each of the buffers
        boolean streamFinished = true;
        for (int i = 0; i < buffers.length; i++) {
            boolean dataAdded = readSamples(i);
            if (dataAdded) {
                streamFinished = false;
            }
        }
        return !streamFinished;
    }

    /**
     * Reads the next buffer
     * @return The next buffer
     */
    protected Buffer getBuffer() {
        Buffer buffer = new Buffer();
        buffer.setData(data);
        buffer.setOffset(0);
        buffer.setLength(data.length);
        buffer.setFormat(outputFormat);
        buffer.setTimeStamp(startTimestamp);
        buffer.setDuration(endTimestamp - startTimestamp);
        return buffer;
    }

    /**
     * Closes the mixer
     *
     */
    public void close() {
        for (int i = 0; i < sources.length; i++) {
            sources[i].close();
            sources[i] = null;
        }
        sources = null;
        for (int i = 0; i < processors.length; i++) {
            if (processors[i] != null) {
                processors[i].close();
                processors[i] = null;
            }
        }
        processors = null;
        for (int i = 0; i < convertBuffers.length; i++) {
            convertBuffers[i] = null;
        }
        convertBuffers = null;
        data = null;
    }
}
