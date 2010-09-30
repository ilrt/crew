/*
 * @(#)ScreenChangeDetector.java
 * Created: 2 Nov 2007
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

package net.crew_vre.media.screencapture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.ResourceUnavailableException;
import javax.media.format.UnsupportedFormatException;
import javax.media.format.YUVFormat;

import net.crew_vre.media.MemeticFileReader;
import net.crew_vre.media.processor.SimpleProcessor;
import net.crew_vre.media.rtptype.RtpTypeRepository;

/**
 * Detects Screen Changes
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ScreenChangeDetector extends Thread implements CaptureChangeListener {

    // The delay before sending an update
    private static final int DELAY = 2000;

    // The time before a forced update is done
    private static final int TIME_BETWEEN_FORCED_UPDATES = 20000;

    // The processor
    private SimpleProcessor processor = null;

    // True if the detection is currently running
    private boolean started = false;

    // The last time an update was successful
    private long lastSuccessfulUpdateTime = 0;

    // The time for the next update
    private long nextUpdateTime = -1;

    private BufferedImage nextUpdateImage = null;

    // The base file name for stored images
    private String baseFileName = null;

    // The renderer
    private ChangeDetection renderer = null;

    private Buffer nextUpdateBuffer = null;

    private MemeticFileReader reader = null;

    // A map of sequence numbers to times of receipt
    private HashMap<Long, Long> sequenceTimeMap = new HashMap<Long, Long>();

    /**
     * Creates a new ScreenChangeDetector
     *
     * @param directory The directory to store images to
     * @param ssrc The ssrc of the stream
     * @param typeRepository The repository to find RTP types in
     * @throws UnsupportedFormatException
     * @throws ResourceUnavailableException
     * @throws IOException
     *
     */
    public ScreenChangeDetector(String directory, String ssrc,
            RtpTypeRepository typeRepository)
            throws UnsupportedFormatException, ResourceUnavailableException,
            IOException {
        String slash = System.getProperty("file.separator");
        this.reader = new MemeticFileReader(directory + slash + ssrc,
                typeRepository);
        renderer = new ChangeDetection();
        renderer.addScreenListener(this);
        this.processor = new SimpleProcessor(reader.getFormat(), renderer);
        this.baseFileName = directory + slash + ssrc + "_";
    }

    /**
     *
     * @see java.lang.Thread#run()
     */
    public void run() {
        if (!started) {
            started = true;
            try {
                reader.streamSeek(0);
                while (started && reader.readNextPacket()) {
                    Buffer inputBuffer = reader.getBuffer();
                    sequenceTimeMap.put(reader.getTimestamp(),
                            reader.getOffset());
                    processor.process(inputBuffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stops the detection
     *
     */
    public void close() {
        if (started) {
            started = false;
            renderer.close();
            reader.close();
            markUpdate();
        }
    }

    private void markUpdate() {
        if (nextUpdateTime != -1) {
            System.err.println("Marking update at " + nextUpdateTime);
            lastSuccessfulUpdateTime = nextUpdateTime;
            try {
                ImageIO.write(nextUpdateImage, "JPG",
                        new File(baseFileName + nextUpdateTime + ".jpg"));
                if (nextUpdateBuffer != null) {
                    byte[] data = (byte[]) nextUpdateBuffer.getData();
                    int length = nextUpdateBuffer.getLength();
                    int offset = nextUpdateBuffer.getOffset();
                    ZipEntry entry =
                        new ZipEntry(baseFileName + nextUpdateTime + ".yuv");
                    entry.setSize(length);
                    ZipOutputStream file = new ZipOutputStream(
                        new FileOutputStream(baseFileName + nextUpdateTime
                                + ".yuv.zip"));
                    file.putNextEntry(entry);
                    file.write(data, offset, length);
                    file.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            nextUpdateTime = -1;
        }
    }

    /**
     *
     * @see net.crew_vre.media.screencapture.CaptureChangeListener#captureDone(
     *     long)
     */
    public void captureDone(long sequence) {
        long time = sequenceTimeMap.get(sequence);
        if (time != -1) {
            if (nextUpdateTime != -1) {
                if ((time - nextUpdateTime) > DELAY) {
                    markUpdate();
                } else if ((time - lastSuccessfulUpdateTime)
                        > TIME_BETWEEN_FORCED_UPDATES) {
                    markUpdate();
                }
            }
            nextUpdateTime = time;
            nextUpdateBuffer = processor.getBuffer(
                    new YUVFormat(YUVFormat.YUV_420));
            if (nextUpdateBuffer != null) {
                nextUpdateBuffer = (Buffer) nextUpdateBuffer.clone();
            }
            nextUpdateImage = renderer.getImage();
        }
    }
}
