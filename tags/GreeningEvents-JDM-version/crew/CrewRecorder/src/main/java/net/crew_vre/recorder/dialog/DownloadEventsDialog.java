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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.tree.TreePath;

import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventParent;
import net.crew_vre.events.domain.EventPart;
import net.crew_vre.events.rest.client.EventClient;
import net.crew_vre.events.rest.client.MainEventClient;
import net.crew_vre.recorder.dao.CrewServerDao;
import net.crew_vre.recorder.dialog.data.EventResolver;
import net.crew_vre.recorder.dialog.data.EventResolverRestImpl;
import net.crew_vre.recorder.dialog.data.EventTreeTableModel;
import net.crew_vre.recorder.dialog.data.EventTreeTableNode;
import net.crew_vre.rest.BasicFixedAuthenticationFilter;

import org.jdesktop.swingx.JXTreeTable;

/**
 * A Dialog for downloading events
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class DownloadEventsDialog extends JDialog implements ItemListener,
        ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // The width of the dialog
    private static final int DIALOG_WIDTH = 520;

    // The height of the dialog
    private static final int DIALOG_HEIGHT = 500;

    private List<String> servers = null;

    private JComboBox server = new JComboBox();

    private JTextField username = new JTextField();

    private JPasswordField password = new JPasswordField();

    private EventTreeTableModel eventModel = null;

    private JXTreeTable eventTable = null;

    private CrewServerDao serverDao = null;

    private String currentUrl = null;

    private boolean currentUrlInvalid = true;

    private EventClient eventClient = null;

    private EventResolver resolver = null;

    private boolean cancelled = true;

    /**
     * Creates a new DownloadEventsDialog
     * @param parent The parent dialog
     * @param serverDao The server data access
     */
    public DownloadEventsDialog(JDialog parent, CrewServerDao serverDao) {
        super(parent, true);
        setTitle("Select Events to Download");
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(content);

        this.serverDao = serverDao;
        servers = serverDao.getCrewServers();
        for (String url : servers) {
            server.addItem(url);
        }
        JPanel serverPanel = new JPanel();
        serverPanel.setLayout(new BoxLayout(serverPanel, BoxLayout.X_AXIS));
        serverPanel.add(server);
        serverPanel.add(Box.createHorizontalStrut(5));
        JButton listEvents = new JButton("List Events");
        listEvents.addActionListener(this);
        serverPanel.add(listEvents);
        serverPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        server.setEditable(true);
        server.addItemListener(this);
        JLabel serverLabel = new JLabel("Select server to download from:");
        serverPanel.setAlignmentX(LEFT_ALIGNMENT);
        serverLabel.setAlignmentX(LEFT_ALIGNMENT);
        content.add(serverLabel);
        content.add(serverPanel);
        content.add(Box.createVerticalStrut(2));

        JPanel usernamePanel = new JPanel();
        usernamePanel.setAlignmentX(LEFT_ALIGNMENT);
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
        usernamePanel.add(new JLabel("Username:"));
        usernamePanel.add(Box.createHorizontalStrut(5));
        usernamePanel.add(username);
        content.add(usernamePanel);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setAlignmentX(LEFT_ALIGNMENT);
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
        passwordPanel.add(new JLabel("Password:"));
        passwordPanel.add(Box.createHorizontalStrut(5));
        passwordPanel.add(password);
        content.add(passwordPanel);

        content.add(Box.createVerticalStrut(5));
        UIManager.put("Tree.line", Color.BLACK);
        UIManager.put("Tree.hash", Color.BLACK);
        eventModel = new EventTreeTableModel(null);
        eventTable = new JXTreeTable(eventModel);
        eventTable.setRootVisible(false);
        eventTable.setLeafIcon(null);
        eventTable.setOpenIcon(null);
        eventTable.setClosedIcon(null);
        eventTable.putClientProperty("JTree.lineStyle", "Angled");
        eventTable.setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane eventScroll = new JScrollPane(eventTable);
        eventScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel selectLabel = new JLabel(
                "Select one or more events to download:");
        selectLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(selectLabel);
        content.add(eventScroll);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        ok.addActionListener(this);
        cancel.addActionListener(this);
        buttonPanel.add(cancel);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(ok);
        content.add(Box.createVerticalStrut(5));
        content.add(buttonPanel);
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
                currentUrl = url;
            } catch (MalformedURLException error) {
                currentUrlInvalid = true;
                JOptionPane.showMessageDialog(this, "The URL is not valid",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     *
     * @see java.awt.event.ActionListener#actionPerformed(
     *     java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        String item = e.getActionCommand();
        if (item.equals("List Events")) {
            try {
                String url = currentUrl;
                if (url == null || currentUrlInvalid) {
                    JOptionPane.showMessageDialog(this, "The URL is not valid",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    BasicFixedAuthenticationFilter authentication = null;
                    if (!username.getText().equals("")) {
                        authentication = new BasicFixedAuthenticationFilter(
                                username.getText(),
                                new String(password.getPassword()));
                        authentication.setForceAuthorization(true);
                    }
                    eventClient = new EventClient(
                            authentication, url);
                    MainEventClient mainEventClient = new MainEventClient(
                            authentication, url);
                    eventModel.clear();
                    resolver = new EventResolverRestImpl(
                            eventClient);
                    eventModel.setResolver(resolver);
                    List<EventPart> events = mainEventClient.getMainEvents();
                    System.err.println("Found " + events.size() + " events");
                    for (EventPart part : events) {
                        System.err.println("Found event " + part.getTitle());
                        eventModel.addMainEvent(part);
                    }
                }
            } catch (Exception error) {
                error.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error getting events: " + error.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (item.equals("Cancel")) {
            cancelled = true;
            setVisible(false);
        } else if (item.equals("OK")) {
            cancelled = false;
            setVisible(false);
        }
    }

    /**
     *
     * @see java.awt.Dialog#setVisible(boolean)
     */
    public void setVisible(boolean visible) {
        if (visible) {
            cancelled = true;
        }
        super.setVisible(visible);
    }

    /**
     * Determines if the dialog was cancelled
     * @return true if cancelled, false otherwise
     */
    public boolean wasCancelled() {
        return cancelled;
    }

    /**
     * Gets the selected events
     * @return The selected events
     */
    public List<Event> getSelectedEvents() {
        TreePath[] paths =
            eventTable.getTreeSelectionModel().getSelectionPaths();
        Vector<Event> events = new Vector<Event>();
        if (paths != null) {
            for (int i = 0; i < paths.length; i++) {
                EventTreeTableNode eventNode = (EventTreeTableNode)
                    paths[i].getLastPathComponent();
                EventParent eventP = (EventParent) eventNode.getUserObject();
                Event event = null;
                if (eventP instanceof Event) {
                    event = (Event) eventP;
                } else {
                    event = resolver.findEvent(eventP);
                }
                ListIterator<EventParent> parentIterator =
                    event.getPartOf().listIterator();
                while (parentIterator.hasNext()) {
                    Event parent = (Event) eventModel.getNodeForEvent(
                            parentIterator.next(), null).getUserObject();
                    parentIterator.set(parent);
                }
                ListIterator<EventPart> partIterator =
                    event.getParts().listIterator();
                while (partIterator.hasNext()) {
                    EventPart part = (EventPart) eventModel.getNodeForEvent(
                            partIterator.next(), null).getUserObject();

                    partIterator.set(part);
                }
                event.setGraph(currentUrl);
                events.add(event);
            }
        }
        return events;
    }
}
