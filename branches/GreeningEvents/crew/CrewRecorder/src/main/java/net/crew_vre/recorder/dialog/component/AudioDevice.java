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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.media.CannotRealizeException;
import javax.media.Codec;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSourceException;
import javax.media.NoPlayerException;
import javax.media.NoProcessorException;
import javax.media.Processor;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.media.rtp.RTPConnector;
import javax.media.rtp.RTPManager;
import javax.media.rtp.SendStream;
import javax.media.rtp.rtcp.SourceDescription;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;

import net.crew_vre.media.effect.CloneEffect;
import net.crew_vre.media.protocol.sound.JavaSoundStream;
import net.crew_vre.recorder.domain.Device;
import net.crew_vre.recorder.utils.LocalStreamListener;

/**
 * An audio device
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class AudioDevice extends Device implements ControllerListener {

    private RTPManager sendManager = null;

    private Processor processor = null;

    private SendStream sendStream = null;

    private CloneEffect cloneEffect = null;

    private HashSet<String> lines = new HashSet<String>();

    private HashMap<String, FloatControl> volumeControls =
        new HashMap<String, FloatControl>();

    private HashMap<String, BooleanControl> selectControls =
        new HashMap<String, BooleanControl>();

    private BooleanControl originallySelectedPort = null;

    private HashMap<String, Float> originalVolumes =
        new HashMap<String, Float>();

    private DataSource dataSource = null;

    private boolean started = false;

    private boolean processorFailed = false;

    private Integer stateLock = new Integer(0);

    /**
     * Creates a new AudioDevice
     * @param name The name of the device
     */
    public AudioDevice(String name) {
        setName(name);
        setId(name);
    }

    private HashMap<String, Control> getControls(Control[] controls,
            HashMap<String, Control> currentControls) {
        for (int i = 0; i < controls.length; i++) {
            currentControls.put(controls[i].getType().toString(), controls[i]);
            if (controls[i] instanceof CompoundControl) {
                getControls(((CompoundControl) controls[i]).getMemberControls(),
                        currentControls);
            }
        }
        return currentControls;
    }

    /**
     * Prepares the audio device for use
     * @param rtpConnector The connector to send using
     * @param audioFormat The audio format to send using
     * @param audioRtpType The rtp type to send using
     * @throws IOException
     * @throws UnsupportedFormatException
     * @throws NoDataSourceException
     * @throws NoProcessorException
     * @throws CannotRealizeException
     * @throws LineUnavailableException
     */
    public void prepare(RTPConnector rtpConnector, AudioFormat audioFormat,
            int audioRtpType) throws UnsupportedFormatException, IOException,
            NoDataSourceException, NoProcessorException,
            CannotRealizeException, LineUnavailableException {

        Mixer mixer = JavaSoundStream.getPortMixer(getName());
        Line.Info[] infos = mixer.getSourceLineInfo();
        for (int j = 0; j < infos.length; j++) {
            String portName = ((Port.Info) infos[j]).getName();
            lines.add(portName);
            Line line = mixer.getLine(infos[j]);
            line.open();
            HashMap<String, Control> controls = getControls(line.getControls(),
                    new HashMap<String, Control>());
            FloatControl volume = (FloatControl) controls.get("Volume");
            BooleanControl select = (BooleanControl)
                controls.get("Select");
            volumeControls.put(portName, volume);
            selectControls.put(portName, select);
            if ((select != null) && select.getValue()) {
                originallySelectedPort = select;
            }
            if (volume != null) {
                originalVolumes.put(portName, volume.getValue());
            }
        }

        dataSource = Manager.createDataSource(
                new MediaLocator("sound://rate=44100&channels=1&bits=16&mixer="
                        + getName()));
                //new MediaLocator("javasound://44100/16/1"));

        PushBufferStream[] datastreams =
            ((PushBufferDataSource) dataSource).getStreams();

        // Configure the processor
        processor = javax.media.Manager.createProcessor(
                dataSource);
        processor.addControllerListener(this);
        processor.configure();
        processorFailed = false;
        while (!processorFailed
                && (processor.getState() < Processor.Configured)) {
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
                    "Could not configure processor for audio");
        }

        // Set to send in RTP
        sendManager = RTPManager.newInstance();
        sendManager.addFormat(audioFormat, audioRtpType);
        sendManager.initialize(rtpConnector);
        ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);
        processor.setContentDescriptor(cd);
        boolean opened = false;

        try {
            // Set the format of the transmission to the selected value
            TrackControl[] tracks = processor.getTrackControls();

            for (int j = 0; j < tracks.length; j++) {
                if (tracks[j].isEnabled()) {

                    // set codec chain -- add the change effect
                    if (datastreams[0].getFormat()
                            instanceof AudioFormat) {
                        if (tracks[j].setFormat(audioFormat) == null) {
                            throw new CannotRealizeException(
                                    "Format " + audioFormat
                                    + " unsupported by audio device");
                        }
                        try {
                            cloneEffect = new CloneEffect();
                            tracks[j].setCodecChain(new Codec[]{
                                    cloneEffect});
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            // Realise the processor
            processor.realize();
            processorFailed = false;
            while (!processorFailed
                    && (processor.getState() < Processor.Realized)) {
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
                        "Could not realize processor for audio");
            }

            DataSource data = processor.getDataOutput();
            sendStream = sendManager.createSendStream(data, 0);
            String cname = sendStream.getParticipant().getCNAME();
            sendStream.setSourceDescription(new SourceDescription[]{
                    new SourceDescription(SourceDescription.SOURCE_DESC_CNAME,
                        cname, 1, false),
                    new SourceDescription(SourceDescription.SOURCE_DESC_NAME,
                        "Audio - " + getName(), 3, false)
            });
            dataSource.disconnect();
            opened = true;
        } finally {
            if (!opened) {
                try {
                    processor.close();
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
     * Gets the lines of the mixer
     * @return The lines
     */
    public Set<String> getLines() {
        return lines;
    }

    /**
     * Sets the volume of a line
     * @param line The line name
     * @param setVolume The volume to set
     */
    public void setLineVolume(String line, float setVolume) {
        FloatControl volume = volumeControls.get(line);
        volume.setValue(setVolume);
    }

    /**
     * Gets the volume of the line
     * @param line The line
     * @return The volume, or -1 if not available
     */
    public float getLineVolume(String line) {
        FloatControl volume = volumeControls.get(line);
        if (volume != null) {
            return volume.getValue();
        }
        return -1;
    }

    /**
     * Selects the given line
     * @param line The line to select
     */
    public void selectLine(String line) {
        BooleanControl select = selectControls.get(line);
        if (select != null) {
            select.setValue(true);
        }
    }

    /**
     * Reset to the original volumes
     */
    public void resetToOriginalVolumes() {
        BooleanControl select = originallySelectedPort;
        if (select != null) {
            select.setValue(true);
        }

        for (String line : lines) {
            FloatControl volume = volumeControls.get(line);
            if (volume != null) {
                volume.setValue(originalVolumes.get(line));
            }
        }
    }

    /**
     * Starts the audio device
     * @param listener The listener to send to
     * @param line The line to start
     * @throws NoPlayerException
     * @throws CannotRealizeException
     * @throws IOException
     */
    public void start(LocalStreamListener listener, String line)
            throws NoPlayerException, CannotRealizeException, IOException {
        if (!started) {
            if (listener != null) {
                listener.addLocalAudio(getName(), cloneEffect,
                    volumeControls.get(line));
            }
            dataSource.connect();
            sendStream.start();
            processor.start();
            started = true;
        }
    }

    /**
     * Stops the audio device
     * @param listener The listener
     */
    public void stop(LocalStreamListener listener) {
        if (started) {
            if (listener != null) {
                listener.removeLocalAudio(cloneEffect);
            }
            processor.stop();
            try {
                sendStream.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
            dataSource.disconnect();
            started = false;
        }
    }
}
