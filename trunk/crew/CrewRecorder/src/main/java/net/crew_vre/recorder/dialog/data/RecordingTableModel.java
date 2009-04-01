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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventParent;
import net.crew_vre.recorder.recording.RecordArchiveManager;
import net.crew_vre.recordings.domain.Recording;
import net.crew_vre.recordings.domain.Stream;

/**
 * A model for displaying recordings in a table
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class RecordingTableModel extends AbstractTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The name of the start column
     */
    public static final String START_COLUMN = "Start";

    /**
     * The name of the end column
     */
    public static final String END_COLUMN = "End";

    /**
     * The name of the event column
     */
    public static final String EVENT_COLUMN = "Event";

    /**
     * The name of the uploaded column
     */
    public static final String UPLOADED_COLUMN = "Uploaded";

    private static final SimpleDateFormat DATE_FORMAT =
        new SimpleDateFormat("dd MMM yyyy 'at' HH:mm:ss");

    private static final String[] COLUMNS = new String[]{
        START_COLUMN, END_COLUMN, EVENT_COLUMN, UPLOADED_COLUMN};

    private Vector<Recording> recordings = new Vector<Recording>();

    private Vector<EventParent> events = new Vector<EventParent>();

    private Date recordingStart = null;

    private Event currentRecordingEvent = null;

    /**
     *
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return COLUMNS.length;
    }

    /**
     *
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return recordings.size() + 1;
    }

    /**
     *
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    /**
     *
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    public Class< ? > getColumnClass(int columnIndex) {
        if (getColumnName(columnIndex).equals(UPLOADED_COLUMN)) {
            return Boolean.class;
        }
        return String.class;
    }

    /**
     *
     * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }


    /**
     *
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        String column = getColumnName(columnIndex);
        if (rowIndex >= recordings.size()) {
            if (column.equals(START_COLUMN)) {
                if (recordingStart != null) {
                    return DATE_FORMAT.format(recordingStart);
                }
                return "Next Recording";
            } else if (column.equals(END_COLUMN)) {
                if (recordingStart != null) {
                    return "Recording...";
                }
                return "";
            } else if (column.equals(EVENT_COLUMN)) {
                if (currentRecordingEvent != null) {
                    return currentRecordingEvent.getTitle();
                }
                return "Double click to select Event";
            } else if (column.equals(UPLOADED_COLUMN)) {
                return false;
            }
            return null;
        }

        Recording recording = recordings.get(rowIndex);
        if (column.equals(START_COLUMN)) {
            return DATE_FORMAT.format(recording.getStartTime());
        } else if (column.equals(END_COLUMN)) {
            return DATE_FORMAT.format(recording.getEndTime());
        } else if (column.equals(EVENT_COLUMN)) {
            EventParent event = events.get(rowIndex);
            if (event != null) {
                return event.getTitle();
            }
            return "Double click to select Event";
        } else if (column.equals(UPLOADED_COLUMN)) {
            return !recording.getGraph().startsWith(
                    RecordArchiveManager.RECORDING_GRAPH_URI_PREFIX);
        }
        return null;
    }

    /**
     * Adds a recording to the table
     * @param recording The recording to add
     * @param event The event recorded, or null if unknown
     */
    public void addRecording(Recording recording, EventParent event) {
        int index = Collections.binarySearch(recordings, recording,
                new RecordingDateComparator());
        if (index < 0) {
            index = -index - 1;
        }

        recordings.insertElementAt(recording, index);
        events.insertElementAt(event, index);
        fireTableRowsInserted(index, index);
    }

    private void deleteRecordingFiles(Recording recording) {
        File directory = new File(recording.getDirectory());
        for (File file : directory.listFiles()) {
            file.delete();
        }
        directory.delete();
    }

    /**
     * Deletes one or more recordings including the files on disk
     * @param rec The recordings to delete
     */
    public void deleteRecordings(Recording... rec) {
        for (Recording recording : rec) {
            int index = recordings.indexOf(recording);
            if (index != -1) {
                deleteRecordingFiles(recording);
                recordings.remove(index);
                events.remove(index);
                fireTableRowsDeleted(index, index);
            }
        }
    }

    /**
     * Sets the event of the current recording
     * @param event The event to set
     */
    public void setCurrentRecordingEvent(Event event) {
        this.currentRecordingEvent = event;
        fireTableRowsUpdated(recordings.size(), recordings.size());
    }

    /**
     * Sets the event of a recording
     * @param recording The recording to set the event of
     * @param event The event to set
     */
    public void setRecordingEvent(Recording recording, EventParent event) {
        int index = recordings.indexOf(recording);
        if (index != -1) {
            if (event != null) {
                recordings.get(index).setEventUri(event.getId());
            } else {
                recordings.get(index).setEventUri(null);
            }
            events.set(index, event);
            fireTableRowsUpdated(index, index);
        }
    }

    /**
     * Sets a recording to being uploaded
     * @param recording The recording uploaded
     * @param url The URL uploaded to
     */
    public void setRecordingUploaded(Recording recording, String url) {
        int index = recordings.indexOf(recording);
        if (index != -1) {
            recording = recordings.get(index);
            recording.setGraph(url + recording.getId());
            for (Stream stream : recording.getStreams()) {
                stream.setGraph(recording.getGraph());
            }
        }
    }

    /**
     * Gets a recording
     * @param index The index of the recording
     * @return The recording
     */
    public Recording getRecording(int index) {
        if (index < recordings.size()) {
            return recordings.get(index);
        }
        return null;
    }

    /**
     * Gets an event
     * @param recording The recording that the event relates to
     * @return The event
     */
    public EventParent getEvent(Recording recording) {
        int index = recordings.indexOf(recording);
        if (index != -1) {
            return events.get(index);
        }
        return null;
    }

    /**
     * Gets a set of recordings
     * @param rows The rows to get the recordings of
     * @return The recordings
     */
    public Recording[] getRecordings(int[] rows) {
        Vector<Recording> recs = new Vector<Recording>();
        for (int row : rows) {
            if (row < recordings.size()) {
                recs.add(recordings.get(row));
            } else {
                recs.add(null);
            }
        }
        return recs.toArray(new Recording[0]);
    }

    /**
     * Indicates that a recording has started at a specific date
     * @param date The date of the start of the recording
     */
    public void startRecording(Date date) {
        recordingStart = date;
    }

    /**
     * Indicates that the current recording has stopped
     * @param recording The recording that has stopped and will be added
     */
    public void finishRecording(Recording recording) {
        addRecording(recording, currentRecordingEvent);
        recordingStart = null;
        currentRecordingEvent = null;
    }

    /**
     * Gets the event for the current recording
     * @return The event
     */
    public Event getCurrentRecordingEvent() {
        return currentRecordingEvent;
    }

    private class RecordingDateComparator implements Comparator<Recording> {

        public int compare(Recording r1, Recording r2) {
            return r1.getStartTime().compareTo(r2.getStartTime());
        }

    }
}
