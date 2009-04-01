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

package net.crew_vre.recorder.dialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.crew_vre.media.rtptype.RTPType;
import net.crew_vre.media.rtptype.RtpTypeRepository;
import net.crew_vre.recorder.Config;
import net.crew_vre.recorder.Recorder;
import net.crew_vre.recorder.ag.StreamListener;
import net.crew_vre.recorder.dao.DeviceDao;
import net.crew_vre.recorder.dao.impl.AGBridgeRegistryDaoImpl;
import net.crew_vre.recorder.dao.impl.AGVenueServerDaoImpl;
import net.crew_vre.recorder.dialog.component.AccessGridPanel;
import net.crew_vre.recorder.dialog.component.LocalDevicePanel;
import net.crew_vre.recorder.recording.RecordArchiveManager;

import org.caboto.jena.db.Database;

import ag3.interfaces.types.BridgeDescription;
import ag3.interfaces.types.Capability;
import ag3.interfaces.types.ClientProfile;

/**
 * A Dialog box that allows the user to choose the source of the recorded
 * streams i.e. local cameras or Access Grid venue
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class RecordingSourceDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final String LAST_SOURCE = "lastSource";

    private static final String LAST_SOURCE_NONE = "NONE";

    private static final String LAST_SOURCE_LOCAL = "LOCAL";

    private static final String LAST_SOURCE_AG = "AG";

    // The width of the dialog
    private static final int DIALOG_WIDTH = 620;

    // The height of the dialog
    private static final int DIALOG_HEIGHT = 565;

    // The width of the border
    private static final int BORDER_WIDTH = 5;

    // The video rtp type
    private static final int VIDEO_RTP_TYPE = 77;

    private static final int AUDIO_RTP_TYPE = 84;

    private final ClientProfile clientProfile = new ClientProfile();

    private static final Capability VIDEO_CAPABILITY =
        new Capability(Capability.CONSUMER, Capability.VIDEO, "H261",
            Capability.VIDEO_RATE, 1);

    private static final Capability AUDIO_CAPABILITY =
        new Capability(Capability.CONSUMER, Capability.AUDIO, "L16",
            Capability.AUDIO_16KHZ, 1);

    private static final BridgeDescription MULTICAST = new BridgeDescription();

    private LocalDevicePanel localDevicePanel = null;

    private JRadioButton localStreamsRadio = new JRadioButton(
            "Recording from Local Cameras");

    private boolean initialLocalStreamsRadio = false;

    private JRadioButton accessGridRadio = new JRadioButton(
            "Recording from Access Grid");

    private boolean initialAccessGridRadio = false;

    private AccessGridPanel accessGridPanel = null;

    private boolean cancelled = false;

    private Config configuration = null;

    private Recorder parent = null;

    private AGStreamListener streamListener = null;

    /**
     * Creates a new RecordingSourceDialog
     * @param parent The parent frame
     * @param configuration The configuration
     * @param typeRepository The RTP Type repository
     * @param deviceDao The known devices
     */
    public RecordingSourceDialog(Recorder parent, Config configuration,
            RtpTypeRepository typeRepository, DeviceDao deviceDao) {
        super(parent, true);
        this.parent = parent;

        accessGridPanel = new AccessGridPanel(this,
                true, new Capability[]{VIDEO_CAPABILITY},
                new Capability[]{AUDIO_CAPABILITY},
                clientProfile);
        RTPType audioRtpType = typeRepository.findRtpType(AUDIO_RTP_TYPE);
        RTPType videoRtpType = typeRepository.findRtpType(VIDEO_RTP_TYPE);
        localDevicePanel = new LocalDevicePanel(this, configuration,
                videoRtpType.getId(), (VideoFormat) videoRtpType.getFormat(),
                audioRtpType.getId(), (AudioFormat) audioRtpType.getFormat(),
                deviceDao);

        this.configuration = configuration;
        String source = configuration.getParameter(LAST_SOURCE,
                LAST_SOURCE_NONE);

        MULTICAST.setName("Use Multicast");
        MULTICAST.setServerType("multicast");

        setTitle("Select Recording Source");
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH));
        add(mainPanel);

        localStreamsRadio.setAlignmentX(0.1f);
        accessGridRadio.setAlignmentX(0.1f);
        localStreamsRadio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        accessGridRadio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(okButton);
        buttonPanel.setAlignmentX(0.1f);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(localStreamsRadio);
        mainPanel.add(localDevicePanel);
        mainPanel.add(accessGridRadio);
        mainPanel.add(accessGridPanel);
        mainPanel.add(buttonPanel);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(localStreamsRadio);
        buttonGroup.add(accessGridRadio);
        localStreamsRadio.addActionListener(this);
        accessGridRadio.addActionListener(this);

        localStreamsRadio.setSelected(source.equals(LAST_SOURCE_LOCAL));
        accessGridRadio.setSelected(source.equals(LAST_SOURCE_AG));
        setPanelEnabled(localDevicePanel, source.equals(LAST_SOURCE_LOCAL));
        setPanelEnabled(accessGridPanel, source.equals(LAST_SOURCE_AG));
    }

    /**
     * Initializes the panel
     * @param database The database to store values in
     * @param typeRepository The repository of types
     */
    public void init(Database database, RtpTypeRepository typeRepository) {
        accessGridPanel.init(configuration, new AGVenueServerDaoImpl(database),
                new AGBridgeRegistryDaoImpl(database));

        List<RTPType> rtpTypes = typeRepository.findRtpTypes();
        for (RTPType type : rtpTypes) {
            accessGridPanel.mapFormat(type.getId(), type.getFormat());
        }
    }

    /**
     *
     * @see java.awt.Dialog#setVisible(boolean)
     */
    public void setVisible(boolean visible) {
        stopPreview();

        if (visible) {
            initialAccessGridRadio = accessGridRadio.isSelected();
            accessGridPanel.captureInitialValues();
            localDevicePanel.captureInitialValues();
            initialLocalStreamsRadio = localStreamsRadio.isSelected();
            cancelled = true;
        } else if (cancelled) {
            accessGridRadio.setSelected(initialAccessGridRadio);
            setPanelEnabled(accessGridPanel, initialAccessGridRadio);
            accessGridPanel.resetToInitialValues();
            localDevicePanel.resetToInitialValues();
            localStreamsRadio.setSelected(initialLocalStreamsRadio);
            setPanelEnabled(localDevicePanel, initialLocalStreamsRadio);
        } else {
            storeConfiguration();

            if (localStreamsRadio.isSelected()) {
                accessGridPanel.stopConnection();
                localDevicePanel.changeDevices(parent);
            } else if (accessGridRadio.isSelected()) {
                localDevicePanel.stopDevices(parent);
            }
        }
        super.setVisible(visible);
    }

    private void storeConfiguration() {
        if (accessGridRadio.isSelected()) {
            configuration.setParameter(LAST_SOURCE, LAST_SOURCE_AG);
        } else if (localStreamsRadio.isSelected()) {
            configuration.setParameter(LAST_SOURCE, LAST_SOURCE_LOCAL);
        }
        localDevicePanel.storeConfiguration(configuration);
        accessGridPanel.storeConfiguration(configuration);
    }

    private void setPanelEnabled(Container panel, boolean enabled) {
        panel.setEnabled(enabled);
        Component[] components = panel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof Container) {
                setPanelEnabled((Container) components[i], enabled);
            } else {
                components[i].setEnabled(enabled);
            }
        }
    }

    private void stopPreview() {
        accessGridPanel.stopPreview();
        localDevicePanel.stopPreview();
    }

    /**
     *
     * @see java.awt.event.ActionListener#actionPerformed(
     *     java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if ((e.getSource() == localStreamsRadio)
                || (e.getSource() == accessGridRadio)) {
            setPanelEnabled(localDevicePanel, localStreamsRadio.isSelected());
            setPanelEnabled(accessGridPanel, accessGridRadio.isSelected());
            if (e.getSource() == localStreamsRadio) {
                localDevicePanel.disablePreviewForRunningDevices();
            }
            stopPreview();
        } else if (e.getActionCommand().equals("OK")) {
            if (localStreamsRadio.isSelected()) {
                if (localDevicePanel.verify()) {
                    cancelled = false;
                    setVisible(false);
                }
            } else if (accessGridRadio.isSelected()) {
                if (accessGridPanel.verify()) {
                    accessGridPanel.stopConnection();
                    if (streamListener != null) {
                        streamListener.removeAllStreams();
                    }
                    streamListener = new AGStreamListener();
                    try {
                        accessGridPanel.startConnection(streamListener);
                        cancelled = false;
                        setVisible(false);
                    } catch (Exception error) {
                        error.printStackTrace();
                        JOptionPane.showMessageDialog(this,
                            "Error connecting to venue: " + error.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "No source has been selected!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getActionCommand().equals("Cancel")) {
            cancelled = true;
            setVisible(false);
        }
    }


    /**
     * Determines if the dialog was cancelled
     * @return True if cancelled, false otherwise
     */
    public boolean wasCancelled() {
        return cancelled;
    }

    /**
     * Sets the manager to use to record
     * @param manager The manager
     */
    public void setArchiveManager(RecordArchiveManager manager) {
        localDevicePanel.setArchiveManager(manager);
        accessGridPanel.setArchiveManager(manager);
    }

    /**
     * Resets the audio to original values before the program started
     */
    public void resetAudioToOriginalValues() {
        localDevicePanel.resetAudioToOriginalValues(configuration);
    }

    private class AGStreamListener implements StreamListener {

        private Vector<DataSource> videoDataSources = new Vector<DataSource>();

        private void removeAllStreams() {
            for (int i = 0; i < videoDataSources.size(); i++) {
                parent.removeVideo(videoDataSources.get(i));
            }
        }

        /**
         * @see net.crew_vre.recorder.ag.StreamListener#addAudioStream(
         *     long, javax.media.protocol.DataSource,
         *     javax.media.format.AudioFormat)
         */
        public void addAudioStream(long ssrc, DataSource dataSource,
                AudioFormat format) {
            // Does Nothing
        }

        /**
         * @see net.crew_vre.recorder.ag.StreamListener#addVideoStream(
         *     long, javax.media.protocol.DataSource,
         *     javax.media.format.VideoFormat)
         */
        public void addVideoStream(long ssrc, DataSource dataSource,
                VideoFormat format) {
            try {
                System.err.println("Adding new video source");
                parent.addVideo("", dataSource, ssrc);
                videoDataSources.add(dataSource);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * @see net.crew_vre.recorder.ag.StreamListener#removeAudioStream(long)
         */
        public void removeAudioStream(long ssrc) {
            // Does Nothing
        }

        /**
         * @see net.crew_vre.recorder.ag.StreamListener#removeVideoStream(long)
         */
        public void removeVideoStream(long ssrc) {
            // Does Nothing
        }

        /**
         * @see net.crew_vre.recorder.ag.StreamListener#setAudioStreamName(long,
         *     java.lang.String)
         */
        public void setAudioStreamName(long ssrc, String name) {
            // Does Nothing
        }

        /**
         * @see net.crew_vre.recorder.ag.StreamListener#setVideoStreamName(long,
         *     java.lang.String)
         */
        public void setVideoStreamName(long ssrc, String name) {
            // Does Nothing
        }
    }
}
