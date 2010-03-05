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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.swing.tree.TreeNode;

import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventParent;
import net.crew_vre.events.domain.EventPart;

import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Represents a node in an EventTreeTable
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class EventTreeTableNode implements TreeTableNode {

    // The model that uses these nodes
    private EventTreeTableModel model = null;

    // The event represented
    private EventParent event = null;

    // The node that resolves the event when requested
    private ResolvingNode resolvingNode = new ResolvingNode(this);

    // The synchronizer of the resolver
    private Integer resolveSync = new Integer(0);

    // The thread used to resolve the node
    private ResolveThread resolveThread = null;

    // The parent node
    private EventTreeTableNode parent = null;

    /**
     * Creates an EventTreeTableNode
     * @param model The model of the node
     * @param event The event to represent
     */
    public EventTreeTableNode(EventTreeTableModel model, EventParent event,
            EventTreeTableNode parent) {
        this.model = model;
        this.event = event;
        this.parent = parent;
    }

    private boolean resolved() {
        return event instanceof Event;
    }

    private void doResolve() {
        if (!(event instanceof Event)) {
            synchronized (resolveSync) {
                if (resolveThread == null) {
                    resolveThread = new ResolveThread();
                    resolveThread.start();
                }
            }
        }
    }

    private Event resolveEvent() {
        if (resolved()) {
            return (Event) event;
        }
        throw new RuntimeException("Event " + event + " " + event.getId()
                + " has not been resolved!");
    }

    /**
     *
     * @see org.jdesktop.swingx.treetable.TreeTableNode#children()
     */
    public Enumeration< ? extends TreeTableNode> children() {
        if (!resolved()) {
            return new ResolverEnumeration();
        }
        Event e = resolveEvent();
        return new EventEnumeration(e, this);
    }

    /**
     *
     * @see org.jdesktop.swingx.treetable.TreeTableNode#getChildAt(int)
     */
    public TreeTableNode getChildAt(int childIndex) {
        if (!resolved()) {
            return resolvingNode;
        }
        Event e = resolveEvent();
        return model.getNodeForEvent(e.getParts().get(childIndex), this);
    }

    /**
     *
     * @see org.jdesktop.swingx.treetable.TreeTableNode#getColumnCount()
     */
    public int getColumnCount() {
        return EventTreeTableModel.COLUMNS.length;
    }

    /**
     *
     * @see org.jdesktop.swingx.treetable.TreeTableNode#getParent()
     */
    public TreeTableNode getParent() {
        return parent;
    }

    /**
     *
     * @see org.jdesktop.swingx.treetable.TreeTableNode#getUserObject()
     */
    public Object getUserObject() {
        return event;
    }

    /**
     *
     * @see org.jdesktop.swingx.treetable.TreeTableNode#getValueAt(int)
     */
    public Object getValueAt(int column) {
        String columnName = EventTreeTableModel.COLUMNS[column];
        if (columnName.equals(EventTreeTableModel.TITLE_COLUMN)) {
            return event.getTitle();
        } else if (columnName.equals(EventTreeTableModel.START_COLUMN)) {
            String startDate = "Unknown";
            if (event instanceof EventPart) {
                EventPart part = (EventPart) event;
                DateTimeFormatter dateTimeFormat =
                    DateTimeFormat.mediumDateTime();
                DateTimeFormatter dateFormat = DateTimeFormat.mediumDate();
                if (part.getStartDateTime() != null) {
                    startDate = part.getStartDateTime().toString(
                            dateTimeFormat);
                } else if (part.getStartDate() != null) {
                    startDate = part.getStartDate().toString(dateFormat);
                }
            }
            return startDate;
        } else if (columnName.equals(EventTreeTableModel.END_COLUMN)) {
            String endDate = "Unknown";
            if (event instanceof EventPart) {
                EventPart part = (EventPart) event;
                DateTimeFormatter dateTimeFormat =
                    DateTimeFormat.mediumDateTime();
                DateTimeFormatter dateFormat = DateTimeFormat.mediumDate();
                if (part.getEndDateTime() != null) {
                    endDate = part.getEndDateTime().toString(dateTimeFormat);
                } else if (part.getEndDate() != null) {
                    endDate = part.getEndDate().toString(dateFormat);
                }
            }
            return endDate;
        }
        return null;
    }

    /**
     *
     * @see org.jdesktop.swingx.treetable.TreeTableNode#isEditable(int)
     */
    public boolean isEditable(int column) {
        return false;
    }

    /**
     *
     * @see org.jdesktop.swingx.treetable.TreeTableNode#setUserObject(
     *     java.lang.Object)
     */
    public void setUserObject(Object userObject) {
        this.event = (EventPart) userObject;
    }

    /**
     *
     * @see org.jdesktop.swingx.treetable.TreeTableNode#setValueAt(
     *     java.lang.Object, int)
     */
    public void setValueAt(Object value, int column) {
        // Does Nothing
    }

    /**
     *
     * @see javax.swing.tree.TreeNode#getAllowsChildren()
     */
    public boolean getAllowsChildren() {
        return true;
    }

    /**
     *
     * @see javax.swing.tree.TreeNode#getChildCount()
     */
    public int getChildCount() {
        if (!resolved()) {
            return 1;
        }
        Event e = resolveEvent();
        return e.getParts().size();
    }

    /**
     *
     * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
     */
    public int getIndex(TreeNode node) {
        if (node instanceof EventTreeTableNode) {
            Event e = resolveEvent();
            EventPart nodeEvent = (EventPart)
                ((EventTreeTableNode) node).getUserObject();
            return e.getParts().indexOf(nodeEvent);
        } else if (node instanceof ResolvingNode) {
            return 0;
        }
        return -1;
    }

    /**
     * Gets the index of an event (or part or parent)
     * @param event The event
     * @return The index
     */
    public int getEventIndex(EventParent event) {
        Event e = resolveEvent();
        return e.getParts().indexOf(event);
    }

    /**
     *
     * @see javax.swing.tree.TreeNode#isLeaf()
     */
    public boolean isLeaf() {
        if (!resolved()) {
            return false;
        }
        Event e = resolveEvent();
        return e.getParts().isEmpty();
    }

    private class EventEnumeration implements Enumeration<EventTreeTableNode> {

        private Iterator<EventPart> iterator = null;

        private EventTreeTableNode node = null;

        public EventEnumeration(Event event, EventTreeTableNode node) {
            iterator = event.getParts().iterator();
            this.node = node;
        }

        /**
         *
         * @see java.util.Enumeration#hasMoreElements()
         */
        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        /**
         *
         * @see java.util.Enumeration#nextElement()
         */
        public EventTreeTableNode nextElement() {
            return model.getNodeForEvent(iterator.next(), node);
        }
    }

    private class ResolvingNode extends AbstractMutableTreeTableNode {

        private EventTreeTableNode node = null;

        private ResolvingNode(EventTreeTableNode node) {
            this.node = node;
        }

        public int getColumnCount() {
            return EventTreeTableModel.COLUMNS.length;
        }

        public Object getValueAt(int column) {
            String columnName = EventTreeTableModel.COLUMNS[column];
            if (columnName.equals(EventTreeTableModel.TITLE_COLUMN)) {
                return "... Reading Event ...";
            }
            return "";
        }

        /**
         *
         * @see org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode#
         *     isLeaf()
         */
        public boolean isLeaf() {
            doResolve();
            return true;
        }

        /**
         *
         * @see org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode#
         *     getParent()
         */
        public TreeTableNode getParent() {
            return node;
        }
    }

    private class ResolverEnumeration implements Enumeration<ResolvingNode> {

        private boolean hasMore = true;

        /**
         *
         * @see java.util.Enumeration#hasMoreElements()
         */
        public boolean hasMoreElements() {
            return hasMore;
        }

        /**
         *
         * @see java.util.Enumeration#nextElement()
         */
        public ResolvingNode nextElement() {
            if (hasMore) {
                hasMore = false;
                return resolvingNode;
            }
            throw new NoSuchElementException();
        }
    }

    private class ResolveThread extends Thread {

        public synchronized void run() {
            if (!(event instanceof Event)) {
                System.err.println("Resolving " + event + " " + event.getId());
                event = model.resolveEvent(event);
                model.indicateResolved((Event) event, resolvingNode);
            }
        }
    }
}
