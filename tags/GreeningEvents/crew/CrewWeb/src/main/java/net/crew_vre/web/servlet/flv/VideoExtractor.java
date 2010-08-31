/*
 * @(#)VideoExtractor.java
 * Created: 23 Nov 2007
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

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;

import javax.media.Buffer;
import javax.media.PlugIn;
import javax.media.PlugInManager;
import javax.media.ResourceUnavailableException;
import javax.media.format.AudioFormat;
import javax.media.format.UnsupportedFormatException;
import javax.media.format.YUVFormat;
import javax.media.protocol.ContentDescriptor;

import net.crew_vre.codec.controls.FrameFillControl;
import net.crew_vre.media.MemeticFileReader;
import net.crew_vre.media.processor.OutputStreamDataSink;
import net.crew_vre.media.processor.SimpleProcessor;
import net.crew_vre.media.rtptype.RtpTypeRepository;
import net.crew_vre.multiplexer.flv.JavaMultiplexer;

/**
 * Extracts video from a Memetic stream
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class VideoExtractor {

    private static final AudioFormat BLANK_AUDIO_FORMAT =
        new AudioFormat(AudioFormat.LINEAR, 5500, 8, 1);

    private SimpleProcessor videoProcessor = null;

    private SimpleProcessor audioProcessor = null;

    private JavaMultiplexer multiplexer = null;

    private MemeticFileReader videoReader = null;
    
    private  RtpTypeRepository rtpTypeRepository=null;
    
    private AudioMixer mixer = null;

    private long audioOffset = 0;

    private long videoOffset = 0;

    private int videoTrack = 0;

    private int audioTrack = 0;

    private double generationSpeed = 0;

    /**
     * Creates a new VideoExtractor
     *
     * @param videoFilename The file from which to extract video
     * @param audioFilenames The files from which to extract audio
     * @param syncFilenames The files that should be synched with
     * @throws IOException
     * @throws UnsupportedFormatException
     * @throws ResourceUnavailableException
     */
    public VideoExtractor(String videoFilename, String[] audioFilenames,
            String[] syncFilenames, Dimension size, RtpTypeRepository rtpTypeRepository)
            throws IOException, UnsupportedFormatException,
            ResourceUnavailableException {
    	this.rtpTypeRepository=rtpTypeRepository;
        multiplexer = new JavaMultiplexer();
        multiplexer.setContentDescriptor(new ContentDescriptor("flv"));
        multiplexer.resizeVideoTo(size);

        int numTracks = 0;
        if (videoFilename != null) {
            videoReader = new MemeticFileReader(videoFilename,rtpTypeRepository);
            videoTrack = numTracks;
            numTracks += 1;
        }
        MemeticFileReader[] audioReaders = null;
        if ((audioFilenames != null) && (audioFilenames.length > 0)) {
            audioReaders = new MemeticFileReader[audioFilenames.length];
            for (int i = 0; i < audioFilenames.length; i++) {
                audioReaders[i] = new MemeticFileReader(audioFilenames[i],rtpTypeRepository);
            }
        }

        // Add an audio track even if there isn't one
        audioTrack = numTracks;
        numTracks += 1;
        multiplexer.setNumTracks(numTracks);

        if (videoReader != null) {
            videoProcessor = new SimpleProcessor(videoReader.getFormat(),
                    multiplexer, videoTrack);
        }

        if (audioReaders != null) {
            mixer = new AudioMixer(audioReaders);
            audioProcessor = new SimpleProcessor(mixer.getFormat(),
                    multiplexer, audioTrack);
        } else {

            // If there is no audio track, make up a blank one at the lowest
            // bit rate
            multiplexer.setInputFormat(BLANK_AUDIO_FORMAT, audioTrack);
        }

        long earliestStart = Long.MAX_VALUE;
        if (syncFilenames != null) {
            for (int i = 0; i < syncFilenames.length; i++) {
                MemeticFileReader sync =
                    new MemeticFileReader(syncFilenames[i],rtpTypeRepository);
                if (sync.getStartTime() < earliestStart) {
                    earliestStart = sync.getStartTime();
                }
            }
        }

        if (mixer != null) {
            if (mixer.getStartTime() < earliestStart) {
                earliestStart = mixer.getStartTime();
            }
        }

        if (videoReader != null) {
            if (videoReader.getStartTime() < earliestStart) {
                earliestStart = videoReader.getStartTime();
            }
        }

        if (mixer != null) {
            audioOffset = (mixer.getStartTime() - earliestStart) * 1000000L;
        }

        if (videoReader != null) {
            videoOffset = (videoReader.getStartTime() - earliestStart)
                * 1000000L;
        }
    }

    /**
     * Transfers the first video frame to the stream
     * @param outputStream The output stream to transfer to
     * @param duration The duration of the whole stream
     * @throws IOException
     */
    public void transferFirstFrame(OutputStream outputStream, long duration)
            throws IOException {
        multiplexer.setDuration(duration);
        OutputStreamDataSink dataSink = new OutputStreamDataSink(
                multiplexer.getDataOutput(), 0, outputStream);
        dataSink.start();

        boolean isVideoData = videoReader.readNextPacket();
        int count = 0;
        while (isVideoData && (count < 2)) {
            int result = PlugIn.OUTPUT_BUFFER_NOT_FILLED;
            while (isVideoData && (result != PlugIn.BUFFER_PROCESSED_OK)) {
                Buffer inputBuffer = videoReader.getBuffer();
                result = videoProcessor.process(inputBuffer);
                inputBuffer.setData(null);
                isVideoData = videoReader.readNextPacket();
            }
            count += 1;
        }
        videoReader.close();
        videoProcessor.close();
        multiplexer.close();
        dataSink.close();
        System.err.println("First Frame transferred");
    }

    /**
     * Sets the speed of the generation of the flv
     *
     * A speed of <= 0 will generate the flv as fast as possible.
     * Otherwise, the number is the number of times faster than real-time.
     *
     * @param generationSpeed The generation speed
     */
    public void setGenerationSpeed(double generationSpeed) {
        this.generationSpeed = generationSpeed;
    }

    private long waitForNext(long startTime, long firstTimestamp,
            long timestamp) {
        if (generationSpeed <= 0) {
            return firstTimestamp;
        }
        timestamp = timestamp / 1000000;
        if (firstTimestamp == -1) {
            firstTimestamp = timestamp;
        }
        if ((timestamp - firstTimestamp) <= 10000) {
            return firstTimestamp;
        }
        long waitTime = (long) (((timestamp - firstTimestamp) - 10000)
                / generationSpeed);
        waitTime -= (long) ((System.currentTimeMillis() - startTime)
                / generationSpeed);
        if (waitTime > 0) {
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                // Do Nothing
            }
        }
        return firstTimestamp;
    }

    /**
     * Transfers the data read to an
     * @param outputStream The outputstream to write to
     * @param offset The offset to start from (in milliseconds)
     * @param duration The duration to write (in milliseconds)
     * @param delay The delay before the stream starts (in milliseconds)
     * @throws IOException
     */
    public void transferToStream(OutputStream outputStream, long offset,
            long duration, long delay, File firstFrame)
            throws IOException {
        System.err.println("Transfer to stream");
        multiplexer.setDuration(duration + delay);
        multiplexer.setTimestampOffset(offset);
        OutputStreamDataSink dataSink = new OutputStreamDataSink(
                multiplexer.getDataOutput(), 0, outputStream);
        dataSink.start();

        // Seek to the start of the video
        long audioEndTimestamp = (duration - offset) * 1000000L;
        long videoEndTimestamp = (duration - offset) * 1000000L;
        if ((videoReader != null) && (mixer != null)) {
            videoReader.setTimestampOffset(videoOffset);
            mixer.setTimestampOffset(audioOffset);
            videoReader.streamSeek(offset - (videoOffset / 1000000L)
                    - videoReader.getOffsetShift());
            mixer.streamSeek(offset - (audioOffset / 1000000L));
        } else if (videoReader != null) {
            videoReader.streamSeek(offset - videoReader.getOffsetShift());
        } else if (mixer != null) {
            mixer.streamSeek(offset);
        }

        boolean isAudioData = false;
        boolean isVideoData = false;

        // create new OutputBuffer
        FrameFillControl control = (FrameFillControl)
            videoProcessor.getControl("controls.FrameFillControl");
        if (control != null) {
            if (firstFrame != null) {
                if (firstFrame.exists()) {
                    ZipInputStream file = new ZipInputStream(
                            new FileInputStream(firstFrame));
                    file.getNextEntry();
                    byte[] data = new byte[1179648];
                    byte[] buffer = new byte[1024];
                    int bytesRead = 0;
                    boolean eof = false;
                    while (!eof) {
                        int n = file.read(buffer, 0, 1024);
                        if (n != -1) {
                            if (bytesRead + n >= data.length) {
                                byte[] tmp = new byte[data.length + 8196];
                                System.arraycopy(data, 0, tmp, 0, data.length);
                                data = tmp;
                            }
                            System.arraycopy(buffer, 0, data, bytesRead, n);
                            bytesRead += n;
                        } else {
                            eof = true;
                        }
                    }
                    file.close();
                    byte[]out = new byte[bytesRead];
                    System.arraycopy(data, 0, out, 0, bytesRead);
                    control.fillFrame(out);
                }
            }
        }

        // Output frames up to the start of the video,
        // so missing blocks are filled in
        if (videoReader != null) {
            Buffer lastFrame = null;
            boolean done = false;
            while (!done) {
                isVideoData = videoReader.readNextPacket();
                if (isVideoData) {
                    if (videoReader.getOffset() < offset) {
                        int result = PlugIn.OUTPUT_BUFFER_NOT_FILLED;
                        while (isVideoData
                                && (result != PlugIn.BUFFER_PROCESSED_OK)) {
                            Buffer inputBuffer = videoReader.getBuffer();
                            result = videoProcessor.process(inputBuffer, false);
                            isVideoData = videoReader.readNextPacket();
                        }
                        lastFrame = videoProcessor.getBuffer(new YUVFormat());
                    } else {
                        done = true;
                    }
                } else {
                    done = true;
                }
            }
            long timestamp = videoReader.getTimestamp();
            videoReader.setTimestampOffset(videoOffset - timestamp);
            timestamp = videoReader.getTimestamp();

            // Output repeats of the last video frame until the delay is done
            if (isVideoData && (delay > 0)) {
                try {
                    Buffer delayFrame = lastFrame;
                    int off = 0;
                    byte[] data = (byte[]) delayFrame.getData();
                    int len = data.length;
                    SimpleProcessor output =
                        new SimpleProcessor(delayFrame.getFormat(), multiplexer,
                                videoTrack);
                    long sequence = delayFrame.getSequenceNumber();
                    for (long i = delay; i >= 0; i -= 40) {
                        delayFrame.setOffset(off);
                        delayFrame.setLength(len);
                        delayFrame.setData(data);
                        delayFrame.setTimeStamp(timestamp - (i * 1000000));
                        delayFrame.setSequenceNumber(sequence - i / 40);
                        output.process(delayFrame);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        // Read the first audio buffer
        if (mixer != null) {
            isAudioData = mixer.readNextBuffer();
        }

        long startTime = System.currentTimeMillis();
        long firstTimestamp = -1;

        // Output the rest of the data
        while (isAudioData && isVideoData && !dataSink.isDone()) {
            long audioTimestamp = mixer.getTimestamp();
            long videoTimestamp = videoReader.getTimestamp();

            if (audioTimestamp < videoTimestamp) {
                if (mixer.getTimestamp() <= audioEndTimestamp) {
                    firstTimestamp = waitForNext(startTime, firstTimestamp,
                            audioTimestamp);
                    Buffer inputBuffer = mixer.getBuffer();
                    audioProcessor.process(inputBuffer);
                    mixer.readNextBuffer();
                } else {
                    isAudioData = false;
                }
            } else {
                if (videoReader.getTimestamp() <= videoEndTimestamp) {
                    firstTimestamp = waitForNext(startTime, firstTimestamp,
                            videoTimestamp);
                    Buffer inputBuffer = videoReader.getBuffer();
                    videoProcessor.process(inputBuffer);
                    isVideoData = videoReader.readNextPacket();
                } else {
                    isVideoData = false;
                }
            }
        }

        // Output audio remaining after the video has finished
        while (isAudioData && !dataSink.isDone()) {
            if (mixer.getTimestamp() <= audioEndTimestamp) {
                firstTimestamp = waitForNext(startTime, firstTimestamp,
                        mixer.getTimestamp());
                Buffer inputBuffer = mixer.getBuffer();
                audioProcessor.process(inputBuffer);
                isAudioData = mixer.readNextBuffer();
            } else {
                isAudioData = false;
            }
        }

        // Output video remaining after the audio has finished
        while (isVideoData && !dataSink.isDone()) {
            if (videoReader.getTimestamp() <= videoEndTimestamp) {
                firstTimestamp = waitForNext(startTime, firstTimestamp,
                        videoReader.getTimestamp());
                Buffer inputBuffer = videoReader.getBuffer();
                videoProcessor.process(inputBuffer);
                isVideoData = videoReader.readNextPacket();
            } else {
                isVideoData = false;
            }
        }

        if (videoReader != null) {
            videoReader.close();
            videoProcessor.close();
        }
        if (mixer != null) {
            mixer.close();
        }
        multiplexer.close();
        dataSink.close();
        System.err.println("Extractor finished");
    }
}
