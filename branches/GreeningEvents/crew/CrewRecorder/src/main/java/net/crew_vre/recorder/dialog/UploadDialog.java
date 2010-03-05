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

import info.clearthought.layout.TableLayout;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import net.crew_vre.OtherThread;
import net.crew_vre.events.dao.EventDao;
import net.crew_vre.events.dao.MainEventDao;
import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventParent;
import net.crew_vre.events.domain.EventPart;
import net.crew_vre.events.rest.client.EventClient;
import net.crew_vre.recorder.dao.CrewServerDao;
import net.crew_vre.recorder.dialog.data.RecordingTableModel;
import net.crew_vre.recorder.dialog.data.StreamProgress;
import net.crew_vre.recorder.dialog.data.StreamsPart;
import net.crew_vre.recordings.dao.RecordingDao;
import net.crew_vre.recordings.domain.Recording;
import net.crew_vre.recordings.domain.ReplayLayout;
import net.crew_vre.recordings.domain.ReplayLayoutPosition;
import net.crew_vre.recordings.domain.Stream;
import net.crew_vre.rest.BasicFixedAuthenticationFilter;
import net.crew_vre.ui.ProgressDialog;

/**
 * A dialog for uploading recordings
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class UploadDialog extends JDialog implements ActionListener,
        ItemListener, StreamProgress {

    private static final long serialVersionUID = 1L;

    private static final int DIALOG_WIDTH = 500;

    private static final int DIALOG_HEIGHT = 415;

    private static final String RECORDING_UPLOAD_URL = "recordingUpload.do?"
        + "startTime=<startTime>&endTime=<endTime>&eventUri=<eventUri>&id=<id>";

    private static final String STREAM_UPLOAD_URL = "streamUpload.do";

    private static final String ANNOTATION_UPLOAD_URL = "annotationUpload.do";

    private static final String LAYOUT_UPLOAD_URL = "replayLayout.do?"
        + "streamUri=<streamUri>&timestamp=<timestamp>&layoutName=<layoutName>"
        + "&positionName=<positionName>";

    private static final int AFTER_EVENT_PROGRESS = 10;

    private static final int AFTER_RECORDING_PROGRESS = 15;

    private static final int AFTER_STREAM_PROGRESS = 80;

    private static final int AFTER_LAYOUT_PROGRESS = 85;

    private static final int AFTER_ANNOTATIONS_PROGRESS = 100;

    private JComboBox server = new JComboBox();

    private CrewServerDao serverDao = null;

    private List<String> servers = null;

    private RecordingTableModel recordingModel = new RecordingTableModel();

    private JTable recordingTable = new JTable(recordingModel);

    private JButton setRecordingEventButton =
        new JButton("<html><center>Assign Recording to Event</center></html>");

    private JButton uploadButton =
        new JButton("<html><center>Upload Selected Recordings</center></html>");

    private JButton deleteButton =
        new JButton("<html><center>Delete Selected Recordings</center></html>");

    private JButton okButton = new JButton("Close");

    private SelectEventDialog selectEventDialog = null;

    private boolean currentUrlInvalid = true;

    private RecordingDao recordingDao = null;

    private EventDao eventDao = null;

    private ProgressDialog progress = null;

    private File annotationDirectory = null;

    private JTextField username = new JTextField();

    private JPasswordField password = new JPasswordField();

    /**
     *
     * Creates a new UploadDialog
     * @param parent The parent frame
     * @param recordingDao The recording DAO
     * @param mainEventDao The main event DAO
     * @param eventDao The event DAO
     * @param serverDao The server DAO
     * @param annotationDirectory The directory where the annotations are stored
     */
    public UploadDialog(JFrame parent, RecordingDao recordingDao,
            MainEventDao mainEventDao, EventDao eventDao,
            CrewServerDao serverDao, File annotationDirectory) {
        super(parent, "Upload Recordings", true);
        this.serverDao = serverDao;
        this.recordingDao = recordingDao;
        this.eventDao = eventDao;
        this.annotationDirectory = annotationDirectory;
        servers = serverDao.getCrewServers();

        List<Recording> recordings = recordingDao.findRecordings();
        for (Recording recording : recordings) {
            String eventUri = recording.getEventUri();
            Event event = null;
            if (eventUri != null) {
                event = eventDao.findEventById(eventUri);
                if (event == null) {
                    recording.setEventUri(null);
                }
            }
            addRecording(recording, event);
        }

        selectEventDialog = new SelectEventDialog(this, eventDao, mainEventDao,
                serverDao);

        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocationRelativeTo(parent);
        setResizable(false);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(content);

        for (String url : servers) {
            server.addItem(url);
        }
        server.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        server.setAlignmentX(LEFT_ALIGNMENT);
        server.setEditable(true);
        server.addItemListener(this);
        if (!servers.isEmpty()) {
            server.setSelectedIndex(0);
            currentUrlInvalid = false;
        }
        JLabel serverLabel = new JLabel("Select server to upload to:");
        content.add(serverLabel);
        content.add(server);

        JPanel usernamePanel = new JPanel();
        usernamePanel.setAlignmentX(LEFT_ALIGNMENT);
        usernamePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
        usernamePanel.add(new JLabel("Username:"));
        usernamePanel.add(Box.createHorizontalStrut(5));
        usernamePanel.add(username);
        content.add(usernamePanel);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setAlignmentX(LEFT_ALIGNMENT);
        passwordPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
        passwordPanel.add(new JLabel("Password:"));
        passwordPanel.add(Box.createHorizontalStrut(5));
        passwordPanel.add(password);
        content.add(passwordPanel);

        content.add(Box.createVerticalStrut(5));

        JScrollPane scroller = new JScrollPane(recordingTable);
        Dimension scrollSize = new Dimension(DIALOG_WIDTH - 10, 210);
        scroller.setMinimumSize(scrollSize);
        scroller.setMaximumSize(scrollSize);
        scroller.setPreferredSize(scrollSize);
        scroller.setAlignmentX(LEFT_ALIGNMENT);
        JLabel recordingsLabel =
            new JLabel("Select the recordings to upload or delete:");
        recordingsLabel.setAlignmentX(LEFT_ALIGNMENT);
        content.add(recordingsLabel);
        content.add(scroller);
        content.add(Box.createVerticalStrut(5));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new TableLayout(
                new double[]{TableLayout.FILL, 10, TableLayout.FILL, 10,
                        TableLayout.FILL, 10, TableLayout.FILL},
                new double[]{55}));
        buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
        deleteButton.addActionListener(this);
        okButton.addActionListener(this);
        setRecordingEventButton.addActionListener(this);
        uploadButton.addActionListener(this);
        buttonPanel.add(setRecordingEventButton, "0, 0");
        buttonPanel.add(uploadButton, "2, 0");
        buttonPanel.add(deleteButton, "4, 0");
        buttonPanel.add(okButton, "6, 0");
        content.add(buttonPanel);

        recordingTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Point p = e.getPoint();
                    int row = recordingTable.rowAtPoint(p);
                    selectEventForRecording(row);
                }
            }
        });
    }

    /**
     * Adds a recording to the dialog
     * @param recording The recording to add
     * @param event The event recorded or null if unknown
     */
    public void addRecording(Recording recording, Event event) {
        recordingModel.addRecording(recording, event);
    }

    /**
     * Indicates that recording has started
     */
    public void startRecording() {
        recordingModel.startRecording(new Date());
    }

    /**
     * Indicates that recording has stopped
     * @param recording The recording that has stopped and is to be added
     */
    public void finishRecording(Recording recording) {
        recordingModel.finishRecording(recording);
    }

    private void selectEventForRecording(int index) {
        Recording recording = recordingModel.getRecording(index);
        String eventUri = null;
        if (recording != null) {
            eventUri = recording.getEventUri();
            selectEventDialog.setSelectedEvent(eventUri);
        } else {
            Event event = recordingModel.getCurrentRecordingEvent();
            selectEventDialog.setSelectedEvent(event);
            if (event != null) {
                eventUri = event.getId();
            }
        }

        selectEventDialog.setVisible(true);
        if (!selectEventDialog.wasCancelled()) {
            Event event = selectEventDialog.getEvent();
            if (recording != null) {
                recordingModel.setRecordingEvent(recording, event);
                recordingDao.setRecordingEvent(recording, event.getUri());
            } else {
                recordingModel.setCurrentRecordingEvent(event);
            }
        } else if (!selectEventDialog.containsEvent(eventUri)) {
            if (recording != null) {
                recordingModel.setRecordingEvent(recording, null);
                recordingDao.setRecordingEvent(recording, null);
            } else {
                recordingModel.setCurrentRecordingEvent(null);
            }
        }
    }

    /**
     *
     * @see java.awt.event.ActionListener#actionPerformed(
     *     java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            setVisible(false);
        } else if (e.getSource() == setRecordingEventButton) {
            int count = recordingTable.getSelectedRowCount();
            if (count > 1) {
                JOptionPane.showMessageDialog(this,
                    "Please ensure only one recording is selected for this"
                        + " operation",
                    "Error", JOptionPane.ERROR_MESSAGE);
            } else if (count < 1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a recording",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                int index = recordingTable.getSelectedRow();
                selectEventForRecording(index);
            }
        } else if (e.getSource() == deleteButton) {
            int[] indices = recordingTable.getSelectedRows();
            if (indices.length == 0) {
                JOptionPane.showMessageDialog(this,
                        "Please select one or more recordings to delete",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                if (JOptionPane.showConfirmDialog(this,
                    "Deleted recordings cannot be recovered.\n"
                    + "Are you sure that you want to delete the selected"
                    + " recordings?",
                    "Error", JOptionPane.YES_NO_OPTION)
                            == JOptionPane.YES_OPTION) {
                    recordingModel.deleteRecordings(
                            recordingModel.getRecordings(indices));
                }
            }
        } else if (e.getSource() == uploadButton) {
            int[] indices = recordingTable.getSelectedRows();
            if (indices.length == 0) {
                JOptionPane.showMessageDialog(this,
                        "Please select one or more recordings to upload",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Recording[] recordings = recordingModel.getRecordings(indices);
                boolean missingEvent = false;
                for (Recording recording : recordings) {
                    if (recording == null) {
                        if (recordings.length == 1) {
                            JOptionPane.showMessageDialog(this,
                                "The current recording cannot be uploaded "
                                    + "until it is finished!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } else if (recording.getEventUri() == null) {
                        missingEvent = true;
                    }
                }
                if (missingEvent) {
                    JOptionPane.showMessageDialog(this,
                        "You must select an event for each recording you wish"
                            + "to upload.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    if (currentUrlInvalid) {
                        throw new MalformedURLException();
                    }
                    String urlString = (String) server.getSelectedItem();
                    if ((urlString == null) || urlString.equals("")) {
                        throw new MalformedURLException();
                    }
                    if (!urlString.endsWith("/")) {
                        urlString += "/";
                    }
                    final String url = urlString;
                    for (final Recording recording : recordings) {
                        if (recording != null) {
                            progress = new ProgressDialog(this,
                                    "Uploading Event", true, false);
                            OtherThread<Throwable> worker =
                                new OtherThread<Throwable>() {
                                    public Throwable doInBackground() {
                                        try {
                                            uploadRecording(recording, url);
                                            progress.setVisible(false);
                                            return null;
                                        } catch (Throwable e) {
                                            progress.setVisible(false);
                                            return e;
                                        }
                                    }
                                };
                            worker.execute();
                            progress.setVisible(true);

                            Throwable error = worker.get();
                            if (error != null) {
                                throw error;
                            }
                        }
                    }
                    JOptionPane.showMessageDialog(this,
                            "Recordings Successfully Uploaded",
                            "Upload Complete", JOptionPane.INFORMATION_MESSAGE);
                } catch (MalformedURLException error) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter the URL of a CREW server to"
                                + " upload to",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Throwable error) {
                    error.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Error uploading recording: " + error.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // Uploads an event and all its parent events
    // Returns a string
    private void uploadEventChain(EventClient eventClient, Event event,
            EventDao eventDao) throws IOException {
        if (!event.getPartOf().isEmpty()) {
            EventParent parent = event.getPartOf().get(
                    event.getPartOf().size() - 1);
            if (!(parent instanceof Event)) {
                parent = eventDao.findEventById(parent.getId());
            }
            if (parent != null) {
                uploadEventChain(eventClient, (Event) parent, eventDao);
            }
        }

        String oldUri = event.getId();
        String error = eventClient.uploadEvent(event);
        String newUri = event.getId();
        event.setId(oldUri);
        eventDao.deleteEvent(event);
        event.setId(newUri);
        eventDao.addEvent(event);
        if (error != null) {
            throw new IOException(error);
        }
    }

    private void changeEventGraph(EventDao eventDao, Event event,
            String graph) {
        eventDao.deleteEvent(event);
        event.setGraph(graph);
        eventDao.addEvent(event);
        if (event.getParts() != null) {
            for (EventPart part : event.getParts()) {
                if (!(part instanceof Event)) {
                    part = eventDao.findEventById(part.getId());
                }
                if (part != null) {
                    changeEventGraph(eventDao, (Event) part, graph);
                }
            }
        }
    }

    private void uploadRecording(Recording recording, String urlString)
            throws IOException {
        progress.setMessage("Uploading Recording");
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username.getText(),
                        password.getPassword());
            }
        });
        BasicFixedAuthenticationFilter authorization = null;
        if (!username.getText().equals("")) {
            authorization = new BasicFixedAuthenticationFilter(
                    username.getText(),
                    new String(password.getPassword()));
            authorization.setForceAuthorization(true);
        }
        EventClient eventClient = new EventClient(authorization, urlString);
        Event event = (Event) recordingModel.getEvent(recording);
        String graph = event.getGraph();
        if (!graph.startsWith(eventClient.getLocalGraphUriPrefix())
               && !graph.startsWith(CreateEventDialog.EVENT_GRAPH_URI_PREFIX)) {
            Event remoteEvent = eventClient.getEvent(event.getId());
            if (remoteEvent == null) {
                throw new IOException("The event with which this recording is"
                        + " associated exists on a different server ("
                        + event.getGraph() + ").\nPlease upload this recording"
                        + " to this server.");
            } else if (!event.getTitle().equals(remoteEvent.getTitle())
                        || !event.getDescription().equals(
                                remoteEvent.getDescription())
                        || !event.getStartDateTime().equals(
                                remoteEvent.getStartDateTime())
                        || !event.getEndDateTime().equals(
                                remoteEvent.getEndDateTime())) {
                JOptionPane.showMessageDialog(this, "The event with which"
                        + " this recording is associated can only be edited"
                        + " on the server where it was originally"
                        + " downloaded from (" + event.getGraph()
                        + ").\nThe recording will be uploaded to this"
                        + " server, but changes made to the event data must"
                        + " be made on that server.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
        if (graph.startsWith(CreateEventDialog.EVENT_GRAPH_URI_PREFIX)) {
            Event topmost = event;
            if ((event.getPartOf() != null) && !event.getPartOf().isEmpty()) {
                EventParent parent = event.getPartOf().get(0);
                if (!(parent instanceof Event)) {
                    parent = eventDao.findEventById(parent.getId());
                }
                topmost = (Event) parent;
            }
            graph = graph.replace(CreateEventDialog.EVENT_GRAPH_URI_PREFIX,
                    eventClient.getLocalGraphUriPrefix());
            changeEventGraph(eventDao, topmost, graph);

        }
        uploadEventChain(eventClient, event, eventDao);
        recordingDao.setRecordingEvent(recording, event.getId());
        progress.setProgress(AFTER_EVENT_PROGRESS);

        progress.setMessage("Uploading Recording");
        String recUrl = RECORDING_UPLOAD_URL;
        recUrl = recUrl.replaceAll("<startTime>",
                String.valueOf(recording.getStartTime().getTime()));
        recUrl = recUrl.replaceAll("<endTime>",
                String.valueOf(recording.getEndTime().getTime()));
        recUrl = recUrl.replaceAll("<eventUri>", recording.getEventUri());
        recUrl = recUrl.replaceAll("<id>", recording.getId());
        URL recordingUpload = new URL(urlString + recUrl);
        URLConnection connection = recordingUpload.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        String recordingUri = reader.readLine();
        recordingDao.deleteRecording(recording);
        recording.setUri(recordingUri);
        recording.setGraph(urlString);
        recordingDao.addRecording(recording);
        progress.setProgress(AFTER_RECORDING_PROGRESS);

        progress.setMessage("Uploading Streams");
        String streamUrl = STREAM_UPLOAD_URL;
        URL streamUpload = new URL(urlString + streamUrl);
        HttpClient client = new HttpClient();
        client.getState().setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username.getText(),
                        new String(password.getPassword())));
        PostMethod httppost = new PostMethod(streamUpload.toString());
        StringPart recUriParam = new StringPart("recordingUri", recordingUri);
        StreamsPart streamsParam = new StreamsPart(recording.getStreams(),
                new File(recording.getDirectory()));
        streamsParam.setStreamProgress(this);
        MultipartRequestEntity multipart = new MultipartRequestEntity(
                new Part[]{recUriParam, streamsParam}, httppost.getParams());
        httppost.setRequestEntity(multipart);
        httppost.setContentChunked(true);
        client.executeMethod(httppost);
        if (httppost.getStatusCode() != HttpStatus.SC_OK) {
            throw new IOException("Error uploading streams: "
                    + httppost.getStatusText());
        }
        BufferedReader streamReader = new BufferedReader(
                new InputStreamReader(httppost.getResponseBodyAsStream()));
        for (int i = 0; i < recording.getStreams().size(); i++) {
            streamReader.readLine();
        }
        httppost.releaseConnection();
        progress.setProgress(AFTER_STREAM_PROGRESS);

        progress.setMessage("Uploading Layouts");
        for (ReplayLayout layout : recording.getReplayLayouts()) {
            for (ReplayLayoutPosition position : layout.getLayoutPositions()) {
                Stream stream = position.getStream();
                String layoutUrl = LAYOUT_UPLOAD_URL;
                layoutUrl = layoutUrl.replaceAll("<streamUri>",
                        stream.getUri());
                layoutUrl = layoutUrl.replaceAll("<timestamp>",
                        String.valueOf(layout.getTime().getTime()));
                layoutUrl = layoutUrl.replaceAll("<layoutName>",
                        layout.getName());
                layoutUrl = layoutUrl.replaceAll("<positionName>",
                        position.getName());
                URL layoutUpload = new URL(urlString + layoutUrl);
                HttpURLConnection conn = (HttpURLConnection)
                    layoutUpload.openConnection();
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new IOException("Error setting layout: "
                            + conn.getResponseMessage());
                }
            }
        }
        progress.setProgress(AFTER_LAYOUT_PROGRESS);

        File annotationFile = new File(annotationDirectory,
                recording.getId() + ".xml");
        if (annotationFile.exists()) {
            progress.setMessage("Uploading Annotations");
            String annotationUrl = ANNOTATION_UPLOAD_URL;
            URL annotationUpload = new URL(urlString + annotationUrl);
            client = new HttpClient();
            client.getState().setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username.getText(),
                            new String(password.getPassword())));
            httppost = new PostMethod(annotationUpload.toString());
            FilePart annotationsParam = new FilePart("annotations",
                    annotationFile);
            multipart = new MultipartRequestEntity(
                    new Part[]{recUriParam, annotationsParam},
                    httppost.getParams());
            httppost.setRequestEntity(multipart);
            httppost.setContentChunked(true);
            client.executeMethod(httppost);
            if (httppost.getStatusCode() != HttpStatus.SC_OK) {
                throw new IOException("Error uploading streams: "
                        + httppost.getStatusText());
            }
        }
        progress.setProgress(AFTER_ANNOTATIONS_PROGRESS);
    }

    /**
     *
     * @see java.awt.event.ItemListener#itemStateChanged(
     *     java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String url = (String) server.getSelectedItem();
            try {
                URL u = new URL(url);
                if ((u.getHost() == null) || u.getHost().equals("")) {
                    throw new MalformedURLException();
                }
                currentUrlInvalid = false;
                if (!servers.contains(url)) {
                    servers.add(url);
                    serverDao.addServer(url);
                }
            } catch (MalformedURLException error) {
                currentUrlInvalid = true;
                JOptionPane.showMessageDialog(this, "The URL is not valid",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     *
     * @see net.crew_vre.recorder.dialog.data.StreamProgress#updateProgress(
     *     long, long)
     */
    public void updateProgress(long totalBytes, long currentBytes) {
        float fraction = (float) currentBytes / (float) totalBytes;
        int extra = (int) ((AFTER_STREAM_PROGRESS - AFTER_RECORDING_PROGRESS)
            * fraction);
        progress.setProgress(AFTER_RECORDING_PROGRESS + extra);
    }
}
