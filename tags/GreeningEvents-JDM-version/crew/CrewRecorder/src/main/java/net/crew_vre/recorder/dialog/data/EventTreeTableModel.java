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

import java.util.HashMap;
import java.util.Vector;

import javax.swing.tree.TreePath;

import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventParent;
import net.crew_vre.events.domain.EventPart;

import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

/**
 * A TreeTableModel for events
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class EventTreeTableModel extends DefaultTreeTableModel {

    /**
     * The title column name
     */
    public static final String TITLE_COLUMN = "Title";

    /**
     * The start column name
     */
    public static final String START_COLUMN = "Start";

    /**
     * The end column name
     */
    public static final String END_COLUMN = "End";

    /**
     * The columns in the table
     */
    public static final String[] COLUMNS = new String[]{
        TITLE_COLUMN, START_COLUMN, END_COLUMN
    };

    private HashMap<String, EventTreeTableNode> nodes =
        new HashMap<String, EventTreeTableNode>();

    private EventResolver resolver = null;

    private Vector<EventPart> mainEvents = new Vector<EventPart>();

    private Event rootEvent = new Event();

    private EventTreeTableNode root = null;

    /**
     * Creates a new EventTreeTableModel
     * @param resolver The event resolver to use
     */
    public EventTreeTableModel(EventResolver resolver) {
        rootEvent.setTitle("Root Event");
        rootEvent.setParts(mainEvents);
        rootEvent.setUri("");
        this.root = new EventTreeTableNode(this, rootEvent, null);
        nodes.put(rootEvent.getUri(), root);
        this.resolver = resolver;
        setRoot(root);
    }

    /**
     * Resolves an event
     * @param parent The event to resolve
     * @return The resolved event
     */
    public Event resolveEvent(EventParent parent) {
        return resolver.findEvent(parent);
    }

    /**
     * Sets the resolver
     * @param resolver The new resolver
     */
    public void setResolver(EventResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * Removes all the events from the table
     */
    public void clear() {
        mainEvents.clear();
        nodes.clear();
        nodes.put(rootEvent.getUri(), root);
        modelSupport.fireTreeStructureChanged(new TreePath(root));
    }

    /**
     * Gets a node for the given event
     * @param e The event to get the node for
     * @return The node
     */
    public EventTreeTableNode getNodeForEvent(EventParent e,
            EventTreeTableNode parent) {
        if (nodes.containsKey(e.getUri())) {
            return nodes.get(e.getUri());
        }
        nodes.put(e.getUri(), new EventTreeTableNode(this, e, parent));
        return nodes.get(e.getUri());
    }

    /**
     *
     * @see org.jdesktop.swingx.treetable.DefaultTreeTableModel#getColumnCount()
     */
    public int getColumnCount() {
        return COLUMNS.length;
    }

    /**
     *
     * @see org.jdesktop.swingx.treetable.DefaultTreeTableModel#getColumnName(
     *     int)
     */
    public String getColumnName(int index) {
        return COLUMNS[index];
    }

    /**
     * Adds a top-level event
     * @param event The event to add
     */
    public void addMainEvent(EventPart event) {
        int index = 0;
        while ((index < mainEvents.size())
                && (event.compareTo(mainEvents.get(index)) >= 0)) {
            index += 1;
        }
        mainEvents.insertElementAt(event, index);
        TreePath path = new TreePath(root);
        modelSupport.fireChildAdded(path, index, root.getChildAt(index));
    }

    /**
     * Indicates that an event has been added to the tree
     * @param event The event to add, including sub-events
     */
    public void addEvent(Event event) {
        if (event.getPartOf().isEmpty()) {
            addMainEvent(event);
        } else {
            TreePath path = new TreePath(root);
            EventTreeTableNode parentNode = root;
            if (!rootEvent.getParts().contains(event.getPartOf().get(0))) {
                addMainEvent(event);
            } else {
                for (EventParent parent : event.getPartOf()) {
                    int index = parentNode.getEventIndex(parent);
                    parentNode = (EventTreeTableNode)
                        parentNode.getChildAt(index);
                    path = path.pathByAddingChild(parentNode);
                }
                int index = parentNode.getEventIndex(event);
                modelSupport.fireChildAdded(path, index,
                        parentNode.getChildAt(index));
            }
        }
    }

    /**
     * Indicates that an event has been changed
     * @param event The event that has been changed
     */
    public void editEvent(Event event) {
        TreePath path = new TreePath(root);
        EventTreeTableNode parentNode = root;
        for (EventParent parent : event.getPartOf()) {
            int index = parentNode.getEventIndex(parent);
            parentNode = (EventTreeTableNode) parentNode.getChildAt(index);
            path = path.pathByAddingChild(parentNode);
        }
        modelSupport.fireTreeStructureChanged(path);
    }

    /**
     * Indicates that an event has been deleted
     * @param event The event that has been deleted
     */
    public void deleteEvent(Event event) {
        TreePath path = new TreePath(root);
        EventTreeTableNode parentNode = root;
        for (EventParent parent : event.getPartOf()) {
            int i = parentNode.getEventIndex(parent);
            parentNode = (EventTreeTableNode) parentNode.getChildAt(i);
            path = path.pathByAddingChild(parentNode);
        }
        int index = parentNode.getIndex(getNodeForEvent(event, parentNode));
        ((Event) parentNode.getUserObject()).getParts().remove(event);
        nodes.remove(event.getUri());
        modelSupport.fireChildRemoved(path, index, event);
    }

    /**
     * Gets a path for selecting an event
     * @param event The event to select
     * @return The path of the event
     */
    public TreePath getSelectionPath(Event event) {
        TreePath path = new TreePath(root);
        EventTreeTableNode parentNode = root;
        for (EventParent parent : event.getPartOf()) {
            int index = parentNode.getEventIndex(parent);
            parentNode = (EventTreeTableNode) parentNode.getChildAt(index);
            path = path.pathByAddingChild(parentNode);
        }
        return path.pathByAddingChild(getNodeForEvent(event, parentNode));
    }

    /**
     * Finds the index of a column with the given name
     * @param name The name to find
     * @return The index or -1 if not found
     */
    public static int findColumn(String name) {
        for (int i = 0; i < COLUMNS.length; i++) {
            if (COLUMNS[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    protected void indicateResolved(Event event, TreeTableNode resolvingNode) {
        TreePath path = getSelectionPath(event);

        Object[] children = new Object[event.getParts().size()];
        int[] indices = new int[event.getParts().size()];
        int i = 0;
        for (EventPart part : event.getParts()) {
            indices[i] = i;
            children[i] = getNodeForEvent(part,
                    (EventTreeTableNode) path.getLastPathComponent());
            i++;
        }
        modelSupport.fireChildRemoved(path, 0, resolvingNode);
        if (children.length > 0) {
            modelSupport.fireChildrenAdded(path, indices, children);
        }
    }
}
