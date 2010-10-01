/**
 * Copyright (c) 2008, University of Bristol
 * Copyright (c) 2008, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
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
 *
 */

package net.crew_vre.codec.linear;

import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Format;
import javax.media.format.AudioFormat;

/**
 * A packetizer for ULAW media
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Packetizer implements Codec {

    private static final double DEFAULT_DURATION = 0.02;

    private static final AudioFormat OUT_FORMAT = new AudioFormat(
            AudioFormat.LINEAR + "/RTP", Format.NOT_SPECIFIED, 16, 1,
            AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED, Format.NOT_SPECIFIED,
            Format.NOT_SPECIFIED, Format.byteArray);

    private double duration = DEFAULT_DURATION;

    private int packetSize = 0;

    private long nextTimestamp = 0;

    private long sequence = 0;

    private AudioFormat outputFormat = null;

    /**
     *
     * @see javax.media.Codec#getSupportedInputFormats()
     */
    public Format[] getSupportedInputFormats() {
        return new Format[]{
            new AudioFormat(AudioFormat.LINEAR, Format.NOT_SPECIFIED,
                    OUT_FORMAT.getSampleSizeInBits(), OUT_FORMAT.getChannels(),
                    OUT_FORMAT.getEndian(), OUT_FORMAT.getSigned(),
                    OUT_FORMAT.getFrameSizeInBits(),
                    Format.NOT_SPECIFIED, Format.byteArray)
        };
    }

    /**
     *
     * @see javax.media.Codec#getSupportedOutputFormats(javax.media.Format)
     */
    public Format[] getSupportedOutputFormats(Format format) {
        double sampleRate = Format.NOT_SPECIFIED;
        double frameRate = Format.NOT_SPECIFIED;
        if ((format != null) && (format instanceof AudioFormat)) {
            AudioFormat af = (AudioFormat) format;
            int sampleSize = af.getSampleSizeInBits();
            int channels = af.getChannels();
            int signed = af.getSigned();
            sampleRate = af.getSampleRate();
            frameRate = af.getFrameRate();
            if (!af.getEncoding().equals(AudioFormat.LINEAR)
                    || ((sampleSize != 16)
                          && (sampleSize != Format.NOT_SPECIFIED))
                    || ((channels != 1)
                          && (channels != Format.NOT_SPECIFIED))
                    || ((signed != AudioFormat.SIGNED)
                          && (signed != Format.NOT_SPECIFIED))) {
                return null;
            }
        }
        return new Format[]{
                new AudioFormat(OUT_FORMAT.getEncoding(), sampleRate,
                    OUT_FORMAT.getSampleSizeInBits(), OUT_FORMAT.getChannels(),
                    OUT_FORMAT.getEndian(), OUT_FORMAT.getSigned(),
                    OUT_FORMAT.getFrameSizeInBits(),
                    frameRate, Format.byteArray)
            };
    }

    /**
     *
     * @see javax.media.Codec#process(javax.media.Buffer, javax.media.Buffer)
     */
    public int process(Buffer input, Buffer output) {
        if (outputFormat == null) {
            AudioFormat inputFormat = (AudioFormat) input.getFormat();
            double sampleRate = inputFormat.getSampleRate();
            double frameRate = inputFormat.getFrameRate();
            outputFormat = new AudioFormat(OUT_FORMAT.getEncoding(), sampleRate,
                    OUT_FORMAT.getSampleSizeInBits(), OUT_FORMAT.getChannels(),
                    OUT_FORMAT.getEndian(), OUT_FORMAT.getSigned(),
                    OUT_FORMAT.getFrameSizeInBits(),
                    frameRate, Format.byteArray);
        }

        byte[] outData = (byte[]) output.getData();
        int outOffset = output.getOffset();
        if ((outData == null)
                || (outData.length < packetSize)) {
            outData = new byte[packetSize];
            outOffset = 0;
            output.setData(outData);
            output.setLength(0);
        }

        byte[] inData = (byte[]) input.getData();
        int inLength = input.getLength();
        int inOffset = input.getOffset();

        int bytesLeft = packetSize - output.getOffset();
        int bytesToCopy = Math.min(bytesLeft, inLength);
        System.arraycopy(inData, inOffset, outData, outOffset, bytesToCopy);

        if (bytesLeft > inLength) {

            // Not enough input to fill buffer
            output.setOffset(outOffset + bytesToCopy);
            //System.err.println("Out offset = " + output.getOffset());
            return OUTPUT_BUFFER_NOT_FILLED;
        }

        // Output is full
        output.setOffset(0);
        output.setLength(packetSize);
        output.setFormat(outputFormat);
        output.setDiscard(false);
        output.setTimeStamp(nextTimestamp);
        nextTimestamp += packetSize / (outputFormat.getSampleSizeInBits() / 8);
        output.setSequenceNumber(sequence++);
        output.setDuration((long) (duration * 1000000000L));
        output.setFlags(output.getFlags() | Buffer.FLAG_RTP_TIME);
        output.setFlags(output.getFlags() | Buffer.FLAG_NO_WAIT);
        output.setFlags(output.getFlags() | Buffer.FLAG_NO_DROP);
        output.setDiscard(false);
        output.setEOM(false);

        if (bytesLeft < inLength) {
            long extraTime = (((long) (duration * 1000) * bytesToCopy)
                    / packetSize) * 1000000;

            // Bytes still left in input
            input.setOffset(inOffset + bytesToCopy);
            input.setLength(inLength - bytesToCopy);
            input.setTimeStamp(input.getTimeStamp() + extraTime);

            return INPUT_BUFFER_NOT_CONSUMED;
        }
        // Exact match
        return BUFFER_PROCESSED_OK;
    }

    /**
     *
     * @see javax.media.Codec#setInputFormat(javax.media.Format)
     */
    public Format setInputFormat(Format format) {
        if ((format != null) && (format instanceof AudioFormat)) {
            AudioFormat af = (AudioFormat) format;
            int sampleSize = af.getSampleSizeInBits();
            int channels = af.getChannels();
            int signed = af.getSigned();
            if (af.getEncoding().equals(AudioFormat.LINEAR)
                    && ((sampleSize != 8)
                          || (sampleSize != Format.NOT_SPECIFIED))
                    && ((channels != 1)
                          || (channels != Format.NOT_SPECIFIED))
                    && ((signed != AudioFormat.SIGNED)
                          || (signed != Format.NOT_SPECIFIED))) {
                duration = DEFAULT_DURATION;
                packetSize = (int) (af.getSampleRate() * (sampleSize / 8)
                        * duration);
                while (packetSize > 1000) {
                    packetSize /= 2;
                    duration /= 2;
                }
                return format;
            }
        }
        return null;
    }

    /**
     *
     * @see javax.media.Codec#setOutputFormat(javax.media.Format)
     */
    public Format setOutputFormat(Format format) {
        if ((format != null) && (format instanceof AudioFormat)) {
            AudioFormat af = (AudioFormat) format;
            int sampleSize = af.getSampleSizeInBits();
            int channels = af.getChannels();
            int signed = af.getSigned();
            if (af.getEncoding().equals(AudioFormat.LINEAR + "/RTP")
                    && ((sampleSize != 16)
                          || (sampleSize != Format.NOT_SPECIFIED))
                    && ((channels != 1)
                          || (channels != Format.NOT_SPECIFIED))
                    && ((signed != AudioFormat.SIGNED)
                          || (signed != Format.NOT_SPECIFIED))) {
                return format;
            }
        }
        return null;
    }

    /**
     *
     * @see javax.media.PlugIn#close()
     */
    public void close() {
        // Does Nothing
    }

    /**
     *
     * @see javax.media.PlugIn#getName()
     */
    public String getName() {
        return "ULAW Packetizer";
    }

    /**
     *
     * @see javax.media.PlugIn#open()
     */
    public void open() {
        // Does Nothing
    }

    /**
     *
     * @see javax.media.PlugIn#reset()
     */
    public void reset() {
        // Does Nothing
    }

    /**
     *
     * @see javax.media.Controls#getControl(java.lang.String)
     */
    public Object getControl(String className) {
        return null;
    }

    /**
     *
     * @see javax.media.Controls#getControls()
     */
    public Object[] getControls() {
        return new Object[0];
    }

}
