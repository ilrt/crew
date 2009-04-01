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

package net.crew_vre.recordings.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;

import net.crew_vre.domain.DomainObject;
import net.crew_vre.recordings.layout.Layout;
import net.crew_vre.recordings.layout.LayoutPosition;
import net.crew_vre.recordings.layout.LayoutRepository;

/**
 * Represents a layout for the recording Replay
 *
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class ReplayLayout extends DomainObject {

    private static final String POSITION_PATH = "/position/";

    // The recording the layout belongs to
    private Recording recording = null;

    // name of the layout
    private String name = null;

    // The time when the layout appears
    private Date time = new Date(0);

    private Map<String, ReplayLayoutPosition> replayPosition =
        new HashMap<String, ReplayLayoutPosition>();

    private LayoutRepository layoutRepository;

    /**
     * Creates a new ReplayLayout
     * @param layoutRepository The layout repository
     */
    public ReplayLayout(LayoutRepository layoutRepository) {
        this.layoutRepository = layoutRepository;
    }

    /**
     *
     * @see net.crew_vre.domain.DomainObject#setGraph(java.lang.String)
     */
    public void setGraph(String graph) {
        super.setGraph(graph);
        for (ReplayLayoutPosition position : replayPosition.values()) {
            position.setGraph(graph);
        }
    }

    /**
     *
     * @see net.crew_vre.domain.DomainObject#setUri(java.lang.String)
     */
    public void setUri(String uri) {
        super.setUri(uri);
        for (ReplayLayoutPosition position : replayPosition.values()) {
            position.setUri(uri + POSITION_PATH + position.getName());
        }
    }

    /**
     * Returns the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name and initializes the hashMap of layout locations to be filled
     * @param name the name to set
     */
    public void setName(String name) {
        Layout layout = layoutRepository.findLayout(name);
        if (layout == null) {
            throw new RuntimeException("Layout \"" + name + "\" not found");
        }
        List<LayoutPosition> streams = layout.getStreamPositions();
        Iterator<LayoutPosition> strIter = streams.iterator();
        while (strIter.hasNext()) {
            LayoutPosition pos = strIter.next();
            if (pos.isAssignable()) {
                ReplayLayoutPosition replayLayoutPosition =
                    new ReplayLayoutPosition();
                replayLayoutPosition.setGraph(getGraph());
                replayLayoutPosition.setUri(getUri() + POSITION_PATH
                        + pos.getName());
                replayLayoutPosition.setName(pos.getName());
                replayPosition.put(pos.getName(), replayLayoutPosition);
            }
        }
        this.name = name;

    }

    /**
     * Returns the stream for a given layout location
     * @param name the name to the layout location
     * @return the stream
     */
    public Stream getStream(String name) {
        return replayPosition.get(name).getStream();
    }

    /**
     * Sets the stream of a layout location
     * @param posName the name to the layout location to be filled
     * @param stream the stream to assign
     */
    public void setStream(String posName, Stream stream) {
        if (!replayPosition.containsKey(posName)) {
            throw new RuntimeException("Streamlocation \"" + posName
                    + "\" not defined for this Layout (\"" + name + "\")");
        }
        replayPosition.put(posName, new ReplayLayoutPosition(posName, stream));
    }

    /**
     * Gets the layout positions
     * @return The layout positions
     */
    public List<ReplayLayoutPosition> getLayoutPositions() {
        return new Vector<ReplayLayoutPosition>(replayPosition.values());
    }

    /**
     * Returns the time
     * @return the time
     */
    public Date getTime() {
        return time;
    }

    /**
     * Sets the time
     * @param time the time to set
     */
    public void setTime(long time) {
        this.time = new Date(time);
    }

    /**
     * Sets the time
     * @param time the time to set
     */
    public void setTime(XSDDateTime time) {
        this.time = time.asCalendar().getTime();
    }

   /**
     * Sets the time
     * @param time the time to set
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * Sets the recording
     * @param recording The recording to set
     */
    public void setRecording(Recording recording) {
        this.recording = recording;
    }

    /**
     * Gets the recording
     * @return The recording
     */
    public Recording getRecording() {
        return recording;
    }

}
