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

package net.crew_vre.recorder.dialog.component;

import java.io.IOException;

import javax.media.CannotRealizeException;
import javax.media.CaptureDeviceInfo;
import javax.media.Codec;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSourceException;
import javax.media.NoProcessorException;
import javax.media.Processor;
import javax.media.control.KeyFrameControl;
import javax.media.control.TrackControl;
import javax.media.format.UnsupportedFormatException;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.media.rtp.RTPConnector;
import javax.media.rtp.RTPManager;
import javax.media.rtp.SendStream;
import javax.media.rtp.rtcp.SourceDescription;

import net.crew_vre.codec.controls.KeyFrameForceControl;
import net.crew_vre.media.effect.CloneEffect;
import net.crew_vre.recorder.domain.Device;
import net.crew_vre.recorder.utils.LocalStreamListener;

/**
 * An object containing a video device
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class VideoDevice extends Device implements ControllerListener,
        Comparable<VideoDevice> {

    private CaptureDeviceInfo device = null;

    private RTPManager sendManager = null;

    private Processor sendProcessor = null;

    private SendStream sendStream = null;

    private CloneEffect cloneEffect = null;

    private DataSource dataSource = null;

    private boolean deviceStarted = false;

    private KeyFrameForceControl keyFrameForce = null;

    private boolean processorFailed = false;

    private Integer stateLock = new Integer(0);

    /**
     * Creates a new VideoDevice
     * @param device The device
     */
    public VideoDevice(CaptureDeviceInfo device) {
        this.device = device;
        setName(device.getName());
        setId(device.getLocator().getRemainder());
    }

    /**
     * Prepares the device for sending
     * @param rtpConnector The connector to send using
     * @param videoFormat The video format to send with
     * @param videoRtpType The video RTP type to send with
     * @throws NoDataSourceException
     * @throws IOException
     * @throws NoProcessorException
     * @throws CannotRealizeException
     * @throws UnsupportedFormatException
     */
    public void prepare(RTPConnector rtpConnector, VideoFormat videoFormat,
            int videoRtpType) throws NoDataSourceException, IOException,
            NoProcessorException, CannotRealizeException,
            UnsupportedFormatException {
        String deviceName = device.getName();
        MediaLocator locator = device.getLocator();
        dataSource = Manager.createDataSource(locator);
        PushBufferStream[] datastreams =
            ((PushBufferDataSource) dataSource).getStreams();

        // Configure the processor
        sendProcessor = javax.media.Manager.createProcessor(dataSource);
        sendProcessor.addControllerListener(this);
        sendProcessor.configure();
        processorFailed = false;
        while (!processorFailed
                && (sendProcessor.getState() < Processor.Configured)) {
            synchronized (stateLock) {
                try {
                    stateLock.wait();
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
        }
        if (processorFailed) {
            throw new CannotRealizeException(
                    "Could not configure processor for device " + deviceName);
        }

        // Set to send in RTP
        sendManager = RTPManager.newInstance();
        sendManager.addFormat(videoFormat, videoRtpType);
        sendManager.initialize(rtpConnector);
        ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);
        sendProcessor.setContentDescriptor(cd);
        boolean opened = false;

        try {
            // Set the format of the transmission to the selected value
            TrackControl[] tracks = sendProcessor.getTrackControls();

            for (int i = 0; i < tracks.length; i++) {
                if (tracks[i].isEnabled()) {

                    // set codec chain -- add the change effect
                    if (datastreams[0].getFormat() instanceof VideoFormat) {
                        if (tracks[i].setFormat(videoFormat) == null) {
                            throw new CannotRealizeException(
                                    "Format " + videoFormat
                                    + " unsupported by device " + deviceName);
                        }
                        try {
                            cloneEffect = new CloneEffect();
                            tracks[i].setCodecChain(new Codec[]{
                                    cloneEffect});
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            // Realise the processor
            sendProcessor.realize();
            processorFailed = false;
            while (!processorFailed
                    && (sendProcessor.getState() < Processor.Realized)) {
                synchronized (stateLock) {
                    try {
                        stateLock.wait();
                    } catch (InterruptedException e) {
                        // Do Nothing
                    }
                }
            }
            if (processorFailed) {
                throw new CannotRealizeException(
                        "Could not realize processor for device "
                        + deviceName);
            }

            KeyFrameControl keyFrameControl = (KeyFrameControl)
                sendProcessor.getControl(
                        KeyFrameControl.class.getCanonicalName());
            if (keyFrameControl != null) {
                keyFrameControl.setKeyFrameInterval(250);
            }

            keyFrameForce = (KeyFrameForceControl)
            sendProcessor.getControl(
                    KeyFrameForceControl.class.getCanonicalName());

            DataSource data = sendProcessor.getDataOutput();
            sendStream = sendManager.createSendStream(data, 0);
            String cname = sendStream.getParticipant().getCNAME();
            sendStream.setSourceDescription(new SourceDescription[]{
                    new SourceDescription(SourceDescription.SOURCE_DESC_CNAME,
                            cname, 1, false),
                    new SourceDescription(SourceDescription.SOURCE_DESC_NAME,
                            deviceName, 3, false)
            });
            dataSource.disconnect();
            opened = true;
        } finally {
            if (!opened) {
                try {
                    sendProcessor.close();
                } catch (Throwable t) {
                    // Do Nothing
                }
            }
        }
    }

    /**
     * @see javax.media.ControllerListener
     *      #controllerUpdate(javax.media.ControllerEvent)
     */
    public void controllerUpdate(ControllerEvent ce) {
        // If there was an error during configure or
        // realize, the processor will be closed
        if (ce instanceof ControllerClosedEvent) {
            processorFailed = true;
        }

        // All controller events, send a notification
        // to the waiting thread in waitForState method.
        synchronized (stateLock) {
            stateLock.notifyAll();
        }
    }

    /**
     * Starts the device
     * @param listener The listener or null if none
     * @throws IOException
     */
    public void start(LocalStreamListener listener) throws IOException {
        if (!deviceStarted) {
            if (listener != null) {
                listener.addVideo(getName(), cloneEffect,
                        sendStream.getSSRC());
            }
            dataSource.connect();
            sendStream.start();
            sendProcessor.start();
            deviceStarted = true;
        }
    }

    /**
     * Stops the device
     * @param listener The listener or null if none
     */
    public void stop(LocalStreamListener listener) {
        if (deviceStarted) {
            if (listener != null) {
                listener.removeVideo(cloneEffect);
            }
            dataSource.disconnect();
            sendProcessor.stop();
            try {
                sendStream.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
            deviceStarted = false;
        }
    }

    /**
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(VideoDevice device) {
        return device.device.getName().compareTo(device.getName());
    }

    /**
     * Determines if the device has been started
     * @return True iff the device has been started
     */
    public boolean isStarted() {
        return deviceStarted;
    }
    /**
     * Gets the datasource
     * @return The datasource
     */
    public DataSource getDataSource() {
        return cloneEffect;
    }

    /**
     * Forces a key frame
     */
    public void doKeyFrame() {
        if (keyFrameForce != null) {
            keyFrameForce.nextFrameKey();
        }
    }
}
