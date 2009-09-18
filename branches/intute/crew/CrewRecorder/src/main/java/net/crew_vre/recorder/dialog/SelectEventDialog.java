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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXTreeTable;

import net.crew_vre.events.dao.EventDao;
import net.crew_vre.events.dao.MainEventDao;
import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventPart;
import net.crew_vre.recorder.dao.CrewServerDao;
import net.crew_vre.recorder.dialog.data.EventResolverDaoImpl;
import net.crew_vre.recorder.dialog.data.EventTreeTableModel;
import net.crew_vre.recorder.dialog.data.EventTreeTableNode;

/**
 * Opens an event for recording
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class SelectEventDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    // The width of the dialog
    private static final int DIALOG_WIDTH = 520;

    // The height of the dialog
    private static final int DIALOG_HEIGHT = 240;

    private boolean cancelled = true;

    private EventDao eventDao = null;

    private EventTreeTableModel eventModel = null;

    private JXTreeTable eventTable = null;

    private HashMap<String, Event> knownEvents = new HashMap<String, Event>();

    private CrewServerDao serverDao = null;

    /**
     * Creates an OpenDialog
     * @param parent The parent of the dialog
     * @param eventDao The data access for the events
     * @param mainEventDao The data access for top-level events
     * @param serverDao The data access for crew servers
     */
    public SelectEventDialog(JDialog parent, EventDao eventDao,
            MainEventDao mainEventDao, CrewServerDao serverDao) {
        super(parent, true);
        setTitle("Select an Event");
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(content);

        UIManager.put("Tree.line", Color.BLACK);
        UIManager.put("Tree.hash", Color.BLACK);
        eventModel = new EventTreeTableModel(new EventResolverDaoImpl(
                eventDao));
        eventTable = new JXTreeTable(eventModel);
        eventTable.setRootVisible(false);
        eventTable.setLeafIcon(null);
        eventTable.setOpenIcon(null);
        eventTable.setClosedIcon(null);
        eventTable.putClientProperty("JTree.lineStyle", "Angled");
        eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane eventScroll = new JScrollPane(eventTable);
        eventScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel selectLabel = new JLabel("Select Event:");
        selectLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(selectLabel);
        content.add(eventScroll);

        this.eventDao = eventDao;
        this.serverDao = serverDao;

        List<EventPart> mainEvnts = mainEventDao.findAllEvents();
        Iterator<EventPart> mainEventsIter = mainEvnts.iterator();
        while (mainEventsIter.hasNext()) {
            EventPart mainEvent = mainEventsIter.next();
            Event event = findEvents(mainEvent);
            eventModel.addMainEvent(event);
        }

        JPanel controlPanel = new JPanel();
        controlPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        JButton create = new JButton("Create New Event");
        JButton download = new JButton("Download Events");
        JButton edit = new JButton("Edit Event");
        JButton delete = new JButton("Delete Event");
        create.addActionListener(this);
        download.addActionListener(this);
        edit.addActionListener(this);
        delete.addActionListener(this);
        controlPanel.add(create);
        controlPanel.add(Box.createHorizontalGlue());
        controlPanel.add(download);
        controlPanel.add(Box.createHorizontalGlue());
        controlPanel.add(edit);
        controlPanel.add(Box.createHorizontalGlue());
        controlPanel.add(delete);
        content.add(Box.createVerticalStrut(5));
        content.add(controlPanel);

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
     * @see java.awt.Dialog#setVisible(boolean)
     */
    public void setVisible(boolean visible) {
        if (visible) {
            cancelled = true;
        }
        super.setVisible(visible);
    }

    private Event findEvents(EventPart part) {
        if (!knownEvents.containsKey(part.getId())) {
            Event event = eventDao.findEventById(part.getId());
            knownEvents.put(event.getId(), event);
            ListIterator<EventPart> iter = event.getParts().listIterator();
            while (iter.hasNext()) {
                EventPart eventPart = iter.next();
                Event partEvent = findEvents(eventPart);
                iter.set(partEvent);
            }
            return event;
        }
        return knownEvents.get(part.getId());
    }

    private void createEvent(Event event) {
        eventDao.addEvent(event);
        eventModel.addEvent(event);
        knownEvents.put(event.getUri(), event);
    }

    private void removeEvent(Event event) {
        if (event.getParts() != null) {
            for (EventPart eventPart : event.getParts()) {
                if (eventPart instanceof Event) {
                    removeEvent((Event) eventPart);
                }
            }
        }
        eventDao.deleteEvent(event);
        eventModel.deleteEvent(event);
        knownEvents.remove(event.getUri());
    }

    /**
     *
     * @see java.awt.event.ActionListener#actionPerformed(
     *     java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        String item = e.getActionCommand();
        if (item.equals("OK")) {
            if (eventTable.getTreeSelectionModel().isSelectionEmpty()) {
                JOptionPane.showMessageDialog(this, "No event selected!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                cancelled = false;
                setVisible(false);
            }
        } else if (item.equals("Cancel")) {
            cancelled = true;
            setVisible(false);
        } else if (item.equals("Create New Event")) {
            CreateEventDialog dialog = new CreateEventDialog(this,
                    knownEvents.values());
            dialog.setVisible(true);
            if (!dialog.wasCancelled()) {
                Event event = dialog.getEvent();
                createEvent(event);
            }
        } else if (item.equals("Edit Event")) {
            if (!eventTable.getTreeSelectionModel().isSelectionEmpty()) {
                Event event = getEvent();
                CreateEventDialog dialog = new CreateEventDialog(this,
                        knownEvents.values(), event);
                dialog.setAllowEditSubEvents(false);
                dialog.setParentEvent(CreateEventDialog.NULL_EVENT);
                dialog.setVisible(true);
                if (!dialog.wasCancelled()) {
                    event = dialog.getEvent();
                    eventModel.editEvent(event);
                    eventDao.replaceEvent(event);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "No event selected to edit", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (item.equals("Delete Event")) {
            if (!eventTable.getTreeSelectionModel().isSelectionEmpty()) {
                Event event = getEvent();
                if (JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this Event"
                        + " and all its parts?",
                        "Confirm Event Deletion", JOptionPane.YES_NO_OPTION)
                        == JOptionPane.YES_OPTION) {
                    removeEvent(event);
                }
                eventTable.getTreeSelectionModel().clearSelection();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No event selected to delete", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (item.equals("Download Events")) {
            DownloadEventsDialog dialog = new DownloadEventsDialog(this,
                    serverDao);
            dialog.setVisible(true);
            System.err.println("Dialog cancelled = " + dialog.wasCancelled());
            if (!dialog.wasCancelled()) {
                System.err.println("Downloading events");
                List<Event> selectedEvents = dialog.getSelectedEvents();
                for (Event event : selectedEvents) {
                    System.err.println("Adding event " + event);
                    eventModel.addEvent(event);
                    eventDao.addEvent(event);
                }
            }
        }
    }

    /**
     * Checks if an event is valid
     * @param eventUri The uri of the event to check
     * @return True if the event has been added, false otherwise
     */
    public boolean containsEvent(String eventUri) {
        return knownEvents.containsKey(eventUri);
    }

    /**
     * Determines if the cancel button was pressed
     * @return True if the dialog was cancelled, false otherwise
     */
    public boolean wasCancelled() {
        return cancelled;
    }

    /**
     * Gets the event selected
     * @return The event, or null if none selected
     */
    public Event getEvent() {
        if (!eventTable.getTreeSelectionModel().isSelectionEmpty()) {
            Event event = (Event)
                ((EventTreeTableNode) eventTable.getTreeSelectionModel()
                .getSelectionPath().getLastPathComponent()).getUserObject();
            return event;
        }
        return null;
    }

    /**
     * Selects the given event
     * @param event The event to select
     */
    public void setSelectedEvent(Event event) {
        if (event == null) {
            eventTable.clearSelection();
        } else {
            eventTable.getTreeSelectionModel().setSelectionPath(
                    eventModel.getSelectionPath(event));
        }
    }

    /**
     * Selects the given event
     * @param uri The uri of the event
     */
    public void setSelectedEvent(String uri) {
        if (knownEvents.containsKey(uri)) {
            setSelectedEvent(knownEvents.get(uri));
        }
    }
}
