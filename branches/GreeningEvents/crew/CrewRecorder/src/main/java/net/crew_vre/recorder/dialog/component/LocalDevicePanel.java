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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Effect;
import javax.media.Format;
import javax.media.MediaLocator;
import javax.media.PackageManager;
import javax.media.format.AudioFormat;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.format.YUVFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.crew_vre.media.protocol.sound.JavaSoundStream;
import net.crew_vre.media.renderer.RGBRenderer;
import net.crew_vre.media.rtp.LocalRTPConnector;
import net.crew_vre.recorder.Config;
import net.crew_vre.recorder.dao.DeviceDao;
import net.crew_vre.recorder.domain.Device;
import net.crew_vre.recorder.recording.RecordArchiveManager;
import net.crew_vre.recorder.utils.LocalStreamListener;
import net.crew_vre.ui.ProgressDialog;

import com.lti.civil.CaptureException;
import com.lti.civil.CaptureSystem;
import com.lti.civil.CaptureSystemFactory;
import com.lti.civil.DefaultCaptureSystemFactorySingleton;

/**
 * A panel for selecting local devices
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class LocalDevicePanel extends JPanel implements ActionListener,
        ItemListener {

    private static final long serialVersionUID = 1L;

    private static final String LAST_DEVICES = "lastDevices";

    private static final String LAST_AUDIO_DEVICES = "lastAudioDevices";

    private static final String LAST_AUDIO_SELECTED = "lastAudioSelect";

    private static final String LAST_AUDIO_VOLUME = "lastAudioVolume";

    // The default height of the preview window
    private static final int PREVIEW_HEIGHT = 85;

    // The default width of the preview window
    private static final int PREVIEW_WIDTH = 104;

    // The width of the device listing box
    private static final int DEVICE_BOX_WIDTH = 585;

    // The height of the device listing box
    private static final int DEVICE_BOX_HEIGHT = 90;

    // The height of an audio device line
    private static final int AUDIO_DEVICE_HEIGHT = 20;

    private JPanel videoDeviceBox = new JPanel();

    private JPanel previewPanel = new JPanel();

    private HashSet<VideoDevice> videoDevices = new HashSet<VideoDevice>();

    private HashMap<VideoDevice, JCheckBox> videoSelected =
        new HashMap<VideoDevice, JCheckBox>();

    private HashMap<VideoDevice, Boolean> videoInitiallySelected =
        new HashMap<VideoDevice, Boolean>();

    private HashMap<VideoDevice, JButton> videoPreview =
        new HashMap<VideoDevice, JButton>();

    private JPanel audioDeviceBox = new JPanel();

    private HashSet<AudioDevice> audioDevices = new HashSet<AudioDevice>();

    private HashMap<AudioDevice, JCheckBox> audioSelected =
        new HashMap<AudioDevice, JCheckBox>();

    private HashMap<AudioDevice, Boolean> audioInitiallySelected =
        new HashMap<AudioDevice, Boolean>();

    private HashMap<AudioDevice, JComboBox> audioInputSelected =
        new HashMap<AudioDevice, JComboBox>();

    private HashMap<AudioDevice, Integer> audioInputInitiallySelected =
        new HashMap<AudioDevice, Integer>();

    private LocalRTPConnector localConnector = new LocalRTPConnector();

    private RGBRenderer previewRenderer = null;

    private DataSource previewDataSource = null;

    private VideoDevice previewDevice = null;

    private JDialog parent = null;

    private int videoRtpType = -1;

    private VideoFormat videoFormat = null;

    private int audioRtpType = -1;

    private AudioFormat audioFormat = null;

    private DeviceDao deviceDao = null;

    private Config configuration = null;

    private JButton redetectDevices = new JButton("Re-Detect");

    /**
     * Creates a new LocalDevicePanel
     * @param parent The parent
     * @param configuration The configuration
     * @param videoRtpType The video RTP type
     * @param videoFormat The video format
     * @param audioRtpType The audio RTP type
     * @param audioFormat The audio format
     * @param deviceDao The known devices
     */
    public LocalDevicePanel(JDialog parent, Config configuration,
            int videoRtpType, VideoFormat videoFormat, int audioRtpType,
            AudioFormat audioFormat, DeviceDao deviceDao) {
        this.parent = parent;
        this.videoRtpType = videoRtpType;
        this.videoFormat = videoFormat;
        this.audioRtpType = audioRtpType;
        this.audioFormat = audioFormat;
        this.deviceDao = deviceDao;
        this.configuration = configuration;

        detectDevices();

        setAlignmentX(Component.LEFT_ALIGNMENT);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        videoDeviceBox.setLayout(new BoxLayout(videoDeviceBox,
                BoxLayout.Y_AXIS));
        JScrollPane videoScroll = new JScrollPane(videoDeviceBox);
        JPanel videoPanel = new JPanel();
        videoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        videoPanel.setLayout(new BoxLayout(videoPanel, BoxLayout.X_AXIS));
        videoPanel.setMaximumSize(new Dimension(DEVICE_BOX_WIDTH,
                DEVICE_BOX_HEIGHT));
        videoPanel.setPreferredSize(new Dimension(DEVICE_BOX_WIDTH,
                DEVICE_BOX_HEIGHT));
        previewPanel.setLayout(null);
        Dimension previewSize = new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        previewPanel.setPreferredSize(previewSize);
        previewPanel.setMaximumSize(previewSize);
        previewPanel.setMinimumSize(previewSize);
        previewPanel.setBackground(Color.BLACK);
        videoPanel.add(videoScroll);
        videoPanel.add(previewPanel);
        add(new JLabel("Select video devices to use:"));
        add(videoPanel);

        audioDeviceBox.setLayout(new BoxLayout(audioDeviceBox,
                BoxLayout.Y_AXIS));
        JScrollPane audioScroll = new JScrollPane(audioDeviceBox);
        audioScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        audioScroll.setMaximumSize(new Dimension(
                DEVICE_BOX_WIDTH + PREVIEW_WIDTH, DEVICE_BOX_HEIGHT));
        audioScroll.setPreferredSize(new Dimension(
                DEVICE_BOX_WIDTH + PREVIEW_WIDTH, DEVICE_BOX_HEIGHT));
        add(new JLabel("Select audio devices to use:"));
        add(audioScroll);

        redetectDevices.addActionListener(this);
        add(redetectDevices);
    }

    private void detectDevices() {
        ProgressDialog progress = new ProgressDialog(parent,
                "Detecting Devices", false, true);
        progress.setMessage("Detecting Devices...");
        progress.setVisible(true);

        List<String> selectedCaptureDevices = Arrays.asList(
            configuration.getParameters(LAST_DEVICES));
        List<String> selectedAudioDevices = Arrays.asList(
            configuration.getParameters(LAST_AUDIO_DEVICES));
        List<String> selectedAudioInputs = Arrays.asList(
            configuration.getParameters(LAST_AUDIO_SELECTED));

        detectVideoDevices();
        detectAudioDevices();

        videoDeviceBox.removeAll();
        VideoDevice[] videoDevs = videoDevices.toArray(new VideoDevice[0]);
        Arrays.sort(videoDevs);

        for (int i = 0; i < videoDevs.length; i++) {
            try {
                videoDevs[i].prepare(localConnector, videoFormat, videoRtpType);
                JCheckBox checkBox = videoSelected.get(videoDevs[i]);
                checkBox.setAlignmentX(LEFT_ALIGNMENT);
                checkBox.setSelected(selectedCaptureDevices.contains(
                        videoDevs[i].getName()));
                JPanel devicePanel = new JPanel();
                devicePanel.setLayout(new BoxLayout(devicePanel,
                        BoxLayout.X_AXIS));
                JButton preview = videoPreview.get(videoDevs[i]);
                preview.addActionListener(this);
                devicePanel.add(checkBox);
                devicePanel.add(Box.createHorizontalGlue());
                devicePanel.add(preview);
                videoDeviceBox.add(devicePanel);
            } catch (Exception e) {
                e.printStackTrace();
                JPanel devicePanel = new JPanel();
                devicePanel.setLayout(new BoxLayout(devicePanel,
                        BoxLayout.X_AXIS));
                JLabel errorLabel = new JLabel(" Error starting device "
                        + videoDevs[i].getName());
                errorLabel.setForeground(Color.RED);
                errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                devicePanel.add(errorLabel);
                devicePanel.add(Box.createHorizontalGlue());
                videoDeviceBox.add(devicePanel);
            }
        }
        if (videoDevs.length == 0) {
            videoDeviceBox.add(new JLabel(" No Video Devices Detected"));
        }

        audioDeviceBox.removeAll();
        for (AudioDevice device : audioDevices) {
            try {
                device.prepare(localConnector, audioFormat, audioRtpType);
                JComboBox inputBox = audioInputSelected.get(device);
                String defaultLine = null;
                for (String line : device.getLines()) {
                    if (selectedAudioInputs.contains(
                            device.getName() + ":" + line)) {
                        defaultLine = line;
                    }
                    inputBox.addItem(line);
                    String lastVolume = configuration.getParameter(
                        LAST_AUDIO_VOLUME + ":" + device.getName() + ":" + line,
                        null);
                    if (lastVolume != null) {
                        device.setLineVolume(line, Float.parseFloat(
                                lastVolume));
                    }
                }
                inputBox.addItemListener(this);
                inputBox.setSelectedItem(defaultLine);

                JCheckBox checkBox = audioSelected.get(device);
                checkBox.setSelected(selectedAudioDevices.contains(
                        device.getName()));

                JPanel devicePanel = new JPanel();
                devicePanel.setLayout(new BoxLayout(devicePanel,
                        BoxLayout.X_AXIS));
                devicePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                        AUDIO_DEVICE_HEIGHT));
                devicePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                devicePanel.add(checkBox);
                devicePanel.add(Box.createHorizontalGlue());
                devicePanel.add(inputBox);
                audioDeviceBox.add(devicePanel);

            } catch (Exception e) {
                e.printStackTrace();
                JPanel devicePanel = new JPanel();
                devicePanel.setLayout(new BoxLayout(devicePanel,
                        BoxLayout.X_AXIS));
                JLabel errorLabel = new JLabel(" Error starting device "
                        + device);
                errorLabel.setForeground(Color.RED);
                errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                devicePanel.add(errorLabel);
                devicePanel.add(Box.createHorizontalGlue());
                audioDeviceBox.add(devicePanel);
            }
        }

        progress.setVisible(false);
    }


    /**
     * Resets the audio settings to the values they were set to when the program
     * was started
     * @param configuration The configuration to store values in
     */
    public void resetAudioToOriginalValues(Config configuration) {
        for (AudioDevice device : audioDevices) {
            for (String line : device.getLines()) {
                float volume = device.getLineVolume(line);
                if (volume != -1) {
                    configuration.setParameter(
                        LAST_AUDIO_VOLUME + ":" + device.getName() + ":" + line,
                        String.valueOf(volume));
                }
            }

            device.resetToOriginalVolumes();
        }
    }

    /**
     * Stores the initial values in case the dialog is cancelled
     */
    public void captureInitialValues() {
        for (VideoDevice device : videoDevices) {
            JCheckBox checkBox = videoSelected.get(device);
            JButton preview = videoPreview.get(device);
            videoInitiallySelected.put(device, checkBox.isSelected());
            preview.setEnabled(!device.isStarted());
        }
        for (AudioDevice device : audioDevices) {
            JCheckBox checkBox = audioSelected.get(device);
            audioInitiallySelected.put(device, checkBox.isSelected());
        }
    }

    /**
     * Resets the panel to how it was when captureInitialValues was last called
     */
    public void resetToInitialValues() {
        for (VideoDevice device : videoDevices) {
            JCheckBox checkBox = videoSelected.get(device);
            checkBox.setSelected(videoInitiallySelected.get(device));
        }
        for (AudioDevice device : audioDevices) {
            JCheckBox checkBox = audioSelected.get(device);
            checkBox.setSelected(audioInitiallySelected.get(device));
        }
    }

    /**
     * Stores the current configuration
     * @param configuration The config to store using
     */
    public void storeConfiguration(Config configuration) {
        Vector<String> lastDevices = new Vector<String>();
        for (VideoDevice device : videoDevices) {
            JCheckBox checkBox = videoSelected.get(device);
            if (checkBox.isSelected()) {
                lastDevices.add(device.getName());
            }
        }
        Vector<String> lastAudioDevices = new Vector<String>();
        Vector<String> lastAudioInputs = new Vector<String>();
        for (AudioDevice device : audioDevices) {
            JCheckBox checkBox = audioSelected.get(device);
            if (checkBox.isSelected()) {
                lastAudioDevices.add(device.getName());
            }
            JComboBox inputBox = audioInputSelected.get(device);
            lastAudioInputs.add(device.getName() + ":"
                    + (String) inputBox.getSelectedItem());
        }
        configuration.setParameters(LAST_DEVICES, lastDevices);
        configuration.setParameters(LAST_AUDIO_DEVICES, lastAudioDevices);
        configuration.setParameters(LAST_AUDIO_SELECTED, lastAudioInputs);
    }

    /**
     * Sets up the selected and deselected devices
     * @param listener Thje listener of the streams
     */
    public void changeDevices(LocalStreamListener listener) {
        for (VideoDevice device : videoDevices) {
            JCheckBox checkBox = videoSelected.get(device);
            if (checkBox.isSelected()) {
                startVideoDevice(device, listener);
            } else {
                stopVideoDevice(device, listener);
            }
        }
        for (AudioDevice device : audioDevices) {
            JCheckBox checkBox = audioSelected.get(device);
            if (checkBox.isSelected()) {
                startAudioDevice(device, listener);
            } else {
                stopAudioDevice(device, listener);
            }
        }
    }

    /**
     * Stops all the devices
     * @param listener the listener of the streams
     */
    public void stopDevices(LocalStreamListener listener) {
        for (VideoDevice device : videoDevices) {
            stopVideoDevice(device, listener);
        }
        for (AudioDevice device : audioDevices) {
            stopAudioDevice(device, listener);
        }
    }


    private void startVideoDevice(VideoDevice device,
            LocalStreamListener listener) {

        try {
            device.start(listener);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "There was an error starting the device "
                    + device.getName(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopVideoDevice(VideoDevice device,
            LocalStreamListener listener) {
        device.stop(listener);
    }

    private void startAudioDevice(AudioDevice device,
            LocalStreamListener listener) {
        try {
            JComboBox inputBox = audioInputSelected.get(device);
            String line = (String) inputBox.getSelectedItem();
            device.start(listener, line);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "There was an error starting the audio device "
                    + device.getName(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopAudioDevice(AudioDevice device,
            LocalStreamListener listener) {
        device.stop(listener);
    }

    private void detectAudioDevices() {
        Vector<String> devices = JavaSoundStream.getCompatibleMixers();
        for (String device : devices) {
            Device dev = new Device();
            dev.setId(device);
            if (!audioDevices.contains(dev)) {
                AudioDevice audioDev = new AudioDevice(device);
                audioDevices.add(audioDev);
                audioSelected.put(audioDev, new JCheckBox(device));
                audioInitiallySelected.put(audioDev, false);
                audioInputSelected.put(audioDev, new JComboBox());
                audioInputInitiallySelected.put(audioDev, 0);
            }
        }
    }

    private void detectVideoDevices() {

        // Screen devices
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        for (int i = 0; i < gs.length; i++) {
            CaptureDeviceInfo jmfInfo = new CaptureDeviceInfo(
                    "Local Screen - Monitor " + String.valueOf(i + 1),
                    new MediaLocator("screen://fullscreen:" + i),
                    new Format[]{new RGBFormat()});
            CaptureDeviceManager.removeDevice(jmfInfo);
            CaptureDeviceManager.addDevice(jmfInfo);
        }

        // Civil devices
        CaptureSystemFactory factory = DefaultCaptureSystemFactorySingleton
                .instance();
        try {
            CaptureSystem system = factory.createCaptureSystem();
            system.init();
            List<com.lti.civil.CaptureDeviceInfo> list =
                system.getCaptureDeviceInfoList();
            for (int i = 0; i < list.size(); ++i) {
                com.lti.civil.CaptureDeviceInfo civilInfo = list.get(i);
                String name = civilInfo.getDescription();
                CaptureDeviceInfo jmfInfo = new CaptureDeviceInfo(name,
                    new MediaLocator("civil:" + civilInfo.getDeviceID()),
                    new Format[]{new YUVFormat()});

                CaptureDeviceManager.removeDevice(jmfInfo);
                CaptureDeviceManager.addDevice(jmfInfo);
            }
        } catch (CaptureException e) {
            e.printStackTrace();
        }

        registerDataSource();

        List<Device> knownDevices = deviceDao.findDevices();
        HashMap<String, Device> nameMap = new HashMap<String, Device>();
        HashMap<String, Device> idMap = new HashMap<String, Device>();
        for (Device device : knownDevices) {
            nameMap.put(device.getName(), device);
            idMap.put(device.getId(), device);
        }

        Vector<CaptureDeviceInfo> devices = CaptureDeviceManager.getDeviceList(
                new VideoFormat(null));
        for (CaptureDeviceInfo device : devices) {
            Device dev = new Device();
            dev.setId(device.getLocator().toString());
            VideoDevice vidDev = null;
            if (!videoDevices.contains(dev)) {
                Device knownDev = idMap.get(device.getLocator().getRemainder());
                if (knownDev != null) {
                    CaptureDeviceInfo info = new CaptureDeviceInfo(
                            knownDev.getName(), device.getLocator(),
                            device.getFormats());
                    vidDev = new VideoDevice(info);
                    videoDevices.add(vidDev);
                } else {
                    String name = device.getName();
                    int count = 1;
                    while (nameMap.containsKey(name)) {
                        count += 1;
                        name = device.getName() + " #" + count;
                    }
                    CaptureDeviceInfo info = new CaptureDeviceInfo(
                            name, device.getLocator(),
                            device.getFormats());
                    vidDev = new VideoDevice(info);
                    deviceDao.addDevice(vidDev);
                    knownDevices.add(vidDev);
                    videoDevices.add(vidDev);
                }
                videoSelected.put(vidDev, new JCheckBox(vidDev.getName()));
                videoInitiallySelected.put(vidDev, false);
                videoPreview.put(vidDev, new JButton("Preview"));
            }
        }
    }

    private void registerDataSource() {

        // get registered prefixes
        Vector<String> prefixes = PackageManager.getProtocolPrefixList();

        // create new prefix
        String[] newPrefixes = new String[]{"net.sf.fmj",
                "net.crew_vre"};

        // Go through existing prefixes and if the new one isn't already there,
        // then add it
        for (int i = 0; i < newPrefixes.length; i++) {
            boolean protocolFound = prefixes.contains(newPrefixes[i]);
            if (!protocolFound) {
                prefixes.addElement(newPrefixes[i]);
                PackageManager.setProtocolPrefixList(prefixes);
            }
        }
    }

    /**
     * Stops any preview
     */
    public void stopPreview() {
        if (previewRenderer != null) {
            if (previewDataSource != null) {
                previewDataSource.disconnect();
            }
            previewPanel.removeAll();
            previewRenderer.stop();
            previewDevice.stop(null);
        }
        previewRenderer = null;
        previewDataSource = null;
        previewDevice = null;
    }

    /**
     * Verifies that the panel is OK
     * @return true if the correct devices are selected
     */
    public boolean verify() {
        boolean deviceSelected = false;
        for (VideoDevice device : videoDevices) {
            JCheckBox checkBox = videoSelected.get(device);
            if (checkBox.isSelected()) {
                deviceSelected = true;
            }
        }
        for (AudioDevice device : audioDevices) {
            JCheckBox checkBox = audioSelected.get(device);
            if (checkBox.isSelected()) {
                deviceSelected = true;
            }
        }
        if (!deviceSelected) {
            JOptionPane.showMessageDialog(this,
                    "No devices have been selected to record!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Disables the preview of any devices currently started
     */
    public void disablePreviewForRunningDevices() {
        for (VideoDevice device : videoDevices) {
            if (device.isStarted()) {
                JButton preview = videoPreview.get(device);
                preview.setEnabled(false);
            }
        }
    }

    /**
     *
     * @see java.awt.event.ActionListener#actionPerformed(
     *     java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Preview")) {
            stopPreview();
            for (VideoDevice device : videoDevices) {
                if (e.getSource() == videoPreview.get(device)) {
                    try {
                        DataSource dataSource = device.getDataSource();
                        dataSource.connect();
                        PushBufferStream[] datastreams =
                            ((PushBufferDataSource) dataSource).getStreams();
                        previewRenderer = new RGBRenderer(new Effect[]{});
                        previewRenderer.setDataSource(dataSource, 0);
                        previewRenderer.setInputFormat(
                                datastreams[0].getFormat());
                        previewDataSource = dataSource;
                        previewDevice = device;
                        Component c = previewRenderer.getComponent();
                        previewPanel.add(c);
                        c.setSize(new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT));
                        c.setVisible(true);
                        previewRenderer.start();
                        device.start(null);
                    } catch (Exception error) {
                        error.printStackTrace();
                        JOptionPane.showMessageDialog(this,
                            "Error displaying preview: " + error.getMessage(),
                            "Preview Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                }
            }
        } else if (e.getSource().equals(redetectDevices)) {
            stopPreview();
            detectDevices();
        }
    }

    /**
     *
     * @see java.awt.event.ItemListener#itemStateChanged(
     *     java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            for (AudioDevice device : audioDevices) {
                JComboBox inputBox = audioInputSelected.get(device);
                if (e.getSource() == inputBox) {
                    String line = (String) inputBox.getSelectedItem();
                    device.selectLine(line);
                }
            }
        }
    }

    /**
     * Sets the archive manager for recording
     * @param manager The manager to record using
     */
    public void setArchiveManager(RecordArchiveManager manager) {
        localConnector.setRTPSink(manager);
        localConnector.setRTCPSink(manager);
        for (VideoDevice device : videoDevices) {
            if (device.isStarted()) {
                device.doKeyFrame();
            }
        }
    }

}
