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

package net.crew_vre.recorder.dialog.data;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventParent;

/**
 * A table model that lists the events
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class EventTableModel extends AbstractTableModel {

    /**
     * The title column name
     */
    public static final String TITLE_COLUMN = "Title";

    /**
     * The hierarchy column name
     */
    public static final String HIERARCHY_COLUMN = "Parents";

    /**
     * The start column name
     */
    public static final String START_COLUMN = "Start";

    /**
     * The end column name
     */
    public static final String END_COLUMN = "End";

    private static final String[] COLUMNS = new String[]{
        TITLE_COLUMN, START_COLUMN, END_COLUMN
    };

    private Vector<Event> events = new Vector<Event>();

    /**
     *
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int column) {
        Event event = events.get(row);
        String columnName = getColumnName(column);
        if (columnName.equals(TITLE_COLUMN)) {
            String title = "";
            if (event.getPartOf() != null) {
                for (int i = 0; i < event.getPartOf().size(); i++) {
                    title += "  ";
                }
            }
            title += event.getTitle();
            return title;
        } else if (columnName.equals(HIERARCHY_COLUMN)) {
            String title = "";
            if (event.getPartOf() != null) {
                for (EventParent parent : event.getPartOf()) {
                    title = parent.getTitle() + " < " + title;
                }
            }
            return title;
        } else if (columnName.equals(START_COLUMN)) {
            String startDate = "Unknown";
            DateTimeFormatter dateTimeFormat = DateTimeFormat.mediumDateTime();
            DateTimeFormatter dateFormat = DateTimeFormat.mediumDate();
            if (event.getStartDateTime() != null) {
                startDate = event.getStartDateTime().toString(dateTimeFormat);
            } else if (event.getStartDate() != null) {
                startDate = event.getStartDate().toString(dateFormat);
            }
            return startDate;
        } else if (columnName.equals(END_COLUMN)) {
            String endDate = "Unknown";
            DateTimeFormatter dateTimeFormat = DateTimeFormat.mediumDateTime();
            DateTimeFormatter dateFormat = DateTimeFormat.mediumDate();
            if (event.getEndDateTime() != null) {
                endDate = event.getEndDateTime().toString(dateTimeFormat);
            } else if (event.getEndDate() != null) {
                endDate = event.getEndDate().toString(dateFormat);
            }
            return endDate;
        }
        return null;
    }

    /**
     * Adds an event to the table
     * @param event The event to add
     */
    public void addEvent(Event event) {
        int index = events.size();
        events.add(event);
        fireTableRowsInserted(index, index);
    }

    /**
     *
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return COLUMNS.length;
    }

    /**
     *
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public String getColumnName(int index) {
        return COLUMNS[index];
    }

    /**
     *
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return events.size();
    }

    /**
     *
     * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * Gets the event at the given row
     * @param row The row to get the event from
     * @return The event at the row
     */
    public Event getEvent(int row) {
        return events.get(row);
    }

    /**
     * Gets the row that contains the event
     * @param event The event
     * @return The row index or -1 if not in table
     */
    public int getEventRow(Event event) {
        return events.indexOf(event);
    }

    /**
     * Updates an event
     * @param event The new event
     */
    public void updateEvent(Event event) {
        int index = getEventRow(event);
        events.set(index, event);
        fireTableRowsUpdated(index, index);
    }

    /**
     * Deletes an event
     * @param event The event to delete
     */
    public void deleteEvent(Event event) {
        int index = getEventRow(event);
        events.remove(index);
        fireTableRowsDeleted(index, index);
    }
}
