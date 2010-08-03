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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import org.joda.time.DateTime;

import net.crew_vre.constants.CrewConstants;
import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventParent;
import net.crew_vre.events.domain.EventPart;

import com.toedter.calendar.JDateChooser;

/**
 * A Dialog for creating new events
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class CreateEventDialog extends JDialog implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The prefix for local events
     */
    public static final String EVENT_GRAPH_URI_PREFIX =
        "http://localhost/events/";

    /**
     * The null parent event
     */
    public static final Event NULL_EVENT = new Event();
    static {
        NULL_EVENT.setTitle("None");
        NULL_EVENT.setId("");
    }

    private static final int DIALOG_WIDTH = 390;

    private static final int DIALOG_HEIGHT = 280;

    private static final String[] HOURS = new String[]{
        "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
        "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};

    private static final String[] MINS = new String[]{
        "00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"};


    private static final String EVENT_URI_PREFIX =
        "http://www.crew-vre.net/events/";

    private String id = "" + System.currentTimeMillis()
        + (int) (Math.random() * CrewConstants.ID_NORMALIZATION);

    private JComboBox parentEvent = null;

    private JTextField title = new JTextField("");

    private JTextArea description = new JTextArea(4, 20);

    private JDateChooser startDate = new JDateChooser();

    private JDateChooser endDate = new JDateChooser();

    private JComboBox startHour = new JComboBox(HOURS);

    private JComboBox startMinute = new JComboBox(MINS);

    private JComboBox endHour = new JComboBox(HOURS);

    private JComboBox endMinute = new JComboBox(MINS);

    private DefaultListModel subEvents = new DefaultListModel();

    private JList subEventList = new JList(subEvents);

    private JButton newEvent = new JButton("Add Event");

    private JButton editEvent = new JButton("Edit Event");

    private JButton deleteEvent = new JButton("Delete Event");

    private List<Event> existingEvents = null;

    private boolean cancelled = false;

    private Event originalEvent = null;

    /**
     * Creates a new EventDialog with no values
     * @param parent The parent window
     * @param existingEvents The existing events
     */
    public CreateEventDialog(Dialog parent, Collection<Event> existingEvents) {
        super(parent, "Edit Event");
        init(parent, existingEvents);
    }

    /**
     * Creates a new EventDialog with no values
     * @param parent The parent window
     * @param existingEvents The existing events
     */
    public CreateEventDialog(Frame parent, Collection<Event> existingEvents) {
        super(parent, "Edit Event");
        init(parent, existingEvents);
    }

    private void init(Window parent, Collection<Event> existingEvents) {
        Event event = new Event();
        event.setTitle("");
        event.setDescription("");
        event.setStartDateTime(new DateTime());
        Calendar end = Calendar.getInstance();
        end.add(Calendar.HOUR_OF_DAY, 1);
        event.setEndDateTime(new DateTime(end));
        init(parent, existingEvents, event);
    }

    /**
     * Creates a new EventDialog, filling in values from an existing event
     * @param parent The parent window
     * @param existingEvents The existing events
     * @param event The event to use to fill in the details
     */
    public CreateEventDialog(Dialog parent, Collection<Event> existingEvents,
            Event event) {
        super(parent, "Edit Event");
        init(parent, existingEvents, event);
        originalEvent = event;
    }


    /**
     * Creates a new EventDialog, filling in values from an existing event
     * @param parent The parent window
     * @param existingEvents The existing events
     * @param event The event to use to fill in the details
     */
    public CreateEventDialog(Frame parent, Collection<Event> existingEvents,
            Event event) {
        super(parent, "Edit Event");
        init(parent, existingEvents, event);
        originalEvent = event;
    }

    private class EventRenderer extends JLabel implements ListCellRenderer {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public Component getListCellRendererComponent(JList list,
                Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            EventPart event = (EventPart) value;
            setText(event.getTitle());
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }

    private void init(Window parent,
            Collection<Event> existingEvents,
            Event event) {
        this.existingEvents = new Vector<Event>(existingEvents);
        Collections.sort(this.existingEvents);
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocationRelativeTo(parent);
        setResizable(false);
        setModal(true);

        JPanel content = new JPanel();
        content.setLayout(new TableLayout(
            new double[]{100, 150, 120},
            new double[]{20, 5, 20, 5, 40, 5, 20, 5, 20, 5, 40, 5, 20, 5, 20}));
        content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(content);

        parentEvent = new JComboBox(this.existingEvents.toArray(new Event[0]));

        parentEvent.insertItemAt(NULL_EVENT, 0);
        EventParent superEvent = NULL_EVENT;
        if (!event.getPartOf().isEmpty()) {
            superEvent = event.getPartOf().get(event.getPartOf().size() - 1);
            parentEvent.setEnabled(false);
        }
        parentEvent.setSelectedItem(superEvent);

        content.add(new JLabel("Parent Event:"), "0, 0");
        content.add(parentEvent, "1, 0, 2, 0");
        content.add(new JLabel("Title:"), "0, 2");
        content.add(title, "1, 2, 2, 2");
        content.add(new JLabel("<html>Description (optional):</html>"),
                "0, 4, l, t");
        content.add(description, "1, 4, 2, 4");
        description.setBorder(BorderFactory.createEtchedBorder());

        content.add(new JLabel("Starting at:"), "0, 6");
        content.add(startDate, "1, 6");
        JPanel startTime = new JPanel();
        startTime.setLayout(new BoxLayout(startTime, BoxLayout.X_AXIS));
        startTime.add(Box.createHorizontalStrut(5));
        startTime.add(startHour);
        startTime.add(new JLabel(":"));
        startTime.add(startMinute);
        startTime.add(Box.createHorizontalGlue());
        content.add(startTime, "2, 6");

        content.add(new JLabel("Ending at:"), "0, 8");
        content.add(endDate, "1, 8");
        JPanel endTime = new JPanel();
        endTime.setLayout(new BoxLayout(endTime, BoxLayout.X_AXIS));
        endTime.add(Box.createHorizontalStrut(5));
        endTime.add(endHour);
        endTime.add(new JLabel(":"));
        endTime.add(endMinute);
        endTime.add(Box.createHorizontalGlue());
        content.add(endTime, "2, 8");

        content.add(new JLabel("Parts:"), "0, 10, l, t");
        subEventList.setCellRenderer(new EventRenderer());
        JScrollPane scroller = new JScrollPane(subEventList);
        scroller.setBorder(BorderFactory.createEtchedBorder());
        content.add(scroller, "1, 10, 2, 10");

        JPanel partsPanel = new JPanel();
        partsPanel.setLayout(new BoxLayout(partsPanel, BoxLayout.X_AXIS));

        newEvent.addActionListener(this);
        editEvent.addActionListener(this);
        deleteEvent.addActionListener(this);
        partsPanel.add(newEvent);
        partsPanel.add(Box.createHorizontalGlue());
        partsPanel.add(editEvent);
        partsPanel.add(Box.createHorizontalGlue());
        partsPanel.add(deleteEvent);
        content.add(partsPanel, "1, 12, 2, 12");

        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        ok.setSize(30, 20);
        cancel.setSize(30, 20);
        ok.addActionListener(this);
        cancel.addActionListener(this);
        content.add(cancel, "0, 14, l, b");
        content.add(ok, "2, 14, r, b");

        title.setText(event.getTitle());
        description.setText(event.getDescription());

        Calendar start = event.getStartDateTime().toGregorianCalendar();
        Calendar end = event.getEndDateTime().toGregorianCalendar();

        startDate.setDate(start.getTime());
        endDate.setDate(end.getTime());
        startHour.setSelectedIndex(start.get(Calendar.HOUR_OF_DAY));
        endHour.setSelectedIndex(end.get(Calendar.HOUR_OF_DAY));
        startMinute.setSelectedIndex(start.get(Calendar.MINUTE) / 5);
        endMinute.setSelectedIndex(end.get(Calendar.MINUTE) / 5);
        List<EventPart> parts = event.getParts();
        if ((parts != null) && !parts.isEmpty()) {
            Iterator<EventPart> iter = parts.iterator();
            while (iter.hasNext()) {
                EventPart subEvent = iter.next();
                subEvents.add(subEvents.size(), subEvent);
            }
        }
    }

    /**
     * Stops the parent event from being selected
     * @param event The event to fix the parent at
     */
    public void setParentEvent(Event event) {
        if (!existingEvents.contains(event)) {
            parentEvent.addItem(event);
        }
        parentEvent.setSelectedItem(event);
        parentEvent.setEnabled(false);
    }

    /**
     * Determines if subEvents can be edited in this dialog
     * @param allowEditSubEvents
     */
    public void setAllowEditSubEvents(boolean allowEditSubEvents) {
        newEvent.setEnabled(allowEditSubEvents);
        editEvent.setEnabled(allowEditSubEvents);
        deleteEvent.setEnabled(allowEditSubEvents);
    }

    /**
     * Determines if the dialog was cancelled
     * @return True if cancelled, false otherwise
     */
    public boolean wasCancelled() {
        return cancelled;
    }

    /**
     * Gets the created or edited event
     * @return The event
     */
    public Event getEvent() {
        Event event = new Event();
        if (originalEvent != null) {
            event = originalEvent;
        } else {
            event.setId(EVENT_URI_PREFIX + id);
        }

        event.setTitle(title.getText());
        event.setDescription(description.getText());
        Calendar start = Calendar.getInstance();
        start.setTime(startDate.getDate());
        start.set(Calendar.HOUR_OF_DAY, Integer.parseInt(
                (String) startHour.getSelectedItem()));
        start.set(Calendar.MINUTE, Integer.parseInt(
                (String) startMinute.getSelectedItem()));
        start.set(Calendar.SECOND, 0);
        event.setStartDateTime(new DateTime(start));
        Calendar end = Calendar.getInstance();
        end.setTime(endDate.getDate());
        end.set(Calendar.HOUR_OF_DAY, Integer.parseInt(
                (String) endHour.getSelectedItem()));
        end.set(Calendar.MINUTE, Integer.parseInt(
                (String) endMinute.getSelectedItem()));
        end.set(Calendar.SECOND, 0);
        event.setEndDateTime(new DateTime(end));

        if (originalEvent == null) {
            Event parent = (Event) parentEvent.getSelectedItem();
            if (parent != NULL_EVENT) {
                Vector<EventParent> parents = new Vector<EventParent>(
                        parent.getPartOf());
                parents.add(parent);
                event.setPartOf(parents);
                event.setGraph(parent.getGraph());

                List<EventPart> parts = parent.getParts();
                ListIterator<EventPart> iterator = parts.listIterator();
                boolean found = false;
                while (iterator.hasNext() && !found) {
                    if (event.compareTo(iterator.next()) < 0) {
                        found = true;
                        iterator.previous();
                        iterator.add(event);
                    }
                }
                if (!found) {
                    parts.add(event);
                }
            } else {
                event.setGraph(EVENT_GRAPH_URI_PREFIX + id);
            }
        }

        Vector<EventPart> parts = new Vector<EventPart>();
        for (int i = 0; i < subEvents.getSize(); i++) {
            parts.add((EventPart) subEvents.get(i));
        }
        Collections.sort(parts);
        event.setParts(parts);
        return event;
    }

    /**
     *
     * @see java.awt.event.ActionListener#actionPerformed(
     * java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        String item = e.getActionCommand();
        if (item.equals("OK")) {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            start.setTime(startDate.getDate());
            end.setTime(endDate.getDate());
            if (title.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(this,
                        "The title must not be empty",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!startDate.isValid()) {
                JOptionPane.showMessageDialog(this,
                        "The start date is not valid",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!endDate.isValid()) {
                JOptionPane.showMessageDialog(this,
                        "The end date is not valid",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                cancelled = false;
                setVisible(false);
            }
        } else if (item.equals("Cancel")) {
            cancelled = true;
            setVisible(false);
        } else if (item.equals("Add Event")) {
            CreateEventDialog dialog = new CreateEventDialog(this, existingEvents);
            dialog.setParentEvent(getEvent());
            dialog.setTitle("Add New Event Part");
            dialog.setVisible(true);
            if (!dialog.wasCancelled()) {
                Event event = dialog.getEvent();
                int index = 0;
                while ((index < subEvents.size())
                      && (event.compareTo((Event) subEvents.get(index)) >= 0)) {
                    index += 1;
                }
                subEvents.add(index, event);
            }
        } else if (item.equals("Edit Event")) {
            int index = subEventList.getSelectedIndex();
            if (index == -1) {
                JOptionPane.showMessageDialog(this,
                        "No Event selected to edit", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                CreateEventDialog dialog = new CreateEventDialog(this,
                        existingEvents,
                        (Event) subEvents.get(index));
                dialog.setParentEvent(getEvent());
                dialog.setTitle("Edit Event Part");
                dialog.setVisible(true);
                if (!dialog.wasCancelled()) {
                    Event event = dialog.getEvent();
                    subEvents.set(index, event);
                }
            }
        } else if (item.equals("Delete Event")) {
            int index = subEventList.getSelectedIndex();
            if (index == -1) {
                JOptionPane.showMessageDialog(this,
                        "No Event selected to delete", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                if (JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this Event"
                        + " and all its parts?",
                        "Confirm Event Deletion", JOptionPane.YES_NO_OPTION)
                        == JOptionPane.YES_OPTION) {
                    subEvents.remove(index);
                }
            }
        }
    }
}
