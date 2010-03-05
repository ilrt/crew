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
import java.util.List;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;

import net.crew_vre.domain.DomainObject;

/**
 * Represents a recording
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Recording extends DomainObject {

    private static final String STREAM_PATH = "/stream/";

    private static final String LAYOUT_PATH = "/layout/";

    // The id of the recording
    private String id = null;

    // The start time of the recording
    private Date startTime = new Date(0);

    // The end time of the recording
    private Date endTime = new Date(0);

    // The streams in the recording
    private List<Stream> streams = null;

    private List<ReplayLayout> replayLayouts = null;

    // The directory holding the streams
    private String directory = null;

    // The event to which this recording is attached
    private String eventUri = null;

    /**
     *
     * @see net.crew_vre.domain.DomainObject#setGraph(java.lang.String)
     */
    public void setGraph(String graph) {
        super.setGraph(graph);
        if (streams != null) {
            for (Stream stream : streams) {
                stream.setGraph(graph);
            }
        }
        if (replayLayouts != null) {
            for (ReplayLayout layout : replayLayouts) {
                layout.setGraph(graph);
            }
        }
    }

    /**
     *
     * @see net.crew_vre.domain.DomainObject#setUri(java.lang.String)
     */
    public void setUri(String uri) {
        super.setUri(uri);
        if (streams != null) {
            for (Stream stream : streams) {
                stream.setUri(uri + STREAM_PATH + stream.getSsrc());
            }
        }
        if (replayLayouts != null) {
            for (ReplayLayout layout : replayLayouts) {
                layout.setUri(uri + LAYOUT_PATH + layout.getName() + "/"
                        + layout.getTime().getTime());
            }
        }
    }

    /**
     * Returns the directory
     * @return the directory
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * Sets the directory
     * @param directory the directory to set
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }

    /**
     * Returns the id
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the startTime
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Sets the startTime
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Sets the startTime
     * @param startTime the startTime to set
     */
    public void setStartTime(long startTime) {
        this.startTime = new Date(startTime);
    }

    /**
     * Sets the startTime
     * @param startTime the startTime to set
     */
    public void setStartTime(XSDDateTime startTime) {
        this.startTime = startTime.asCalendar().getTime();
    }

    /**
     * Returns the endTime
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * Sets the endTime
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * Sets the endTime
     * @param endTime the endTime to set
     */
    public void setEndTime(long endTime) {
        this.endTime = new Date(endTime);
    }

    /**
     * Sets the end time
     * @param endTime The end time to set
     */
    public void setEndTime(XSDDateTime endTime) {
        this.endTime = endTime.asCalendar().getTime();
    }

    /**
     * Returns the streams
     * @return the streams
     */
    public List<Stream> getStreams() {
        return streams;
    }

    /**
     * Sets the streams
     * @param streams the streams to set
     */
    public void setStreams(List<Stream> streams) {
        this.streams = streams;
        if (streams != null) {
            for (Stream stream : streams) {
                stream.setGraph(getGraph());
                stream.setRecording(this);
                stream.setUri(getUri() + STREAM_PATH + stream.getSsrc());
            }
        }
    }

    /**
     * Returns the eventUri
     * @return the eventUri
     */
    public String getEventUri() {
        return eventUri;
    }

    /**
     * Sets the eventUri
     * @param eventUri the eventUri to set
     */
    public void setEventUri(String eventUri) {
        this.eventUri = eventUri;
    }

    /**
     * Sets the replay layouts
     * @param replayLayouts The replay layouts
     */
    public void setReplayLayouts(List<ReplayLayout> replayLayouts) {
        this.replayLayouts = replayLayouts;
        if (replayLayouts != null) {
            for (ReplayLayout layout : replayLayouts) {
                layout.setGraph(getGraph());
                layout.setRecording(this);
                layout.setUri(getUri() + LAYOUT_PATH + layout.getName() + "/"
                        + layout.getTime().getTime());
            }
        }
    }

    /**
     * Gets the replay layouts
     * @return The replay layouts
     */
    public List<ReplayLayout> getReplayLayouts() {
        return replayLayouts;
    }
}
