/*
 * @(#)Recording.java
 * Created: 18 Aug 2008
 * Version: 1.0
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
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
 */

package net.crew_vre.recordings.domain;

import java.util.List;

import net.crew_vre.domain.DomainObject;

/**
 * Represents a recording
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Recording extends DomainObject {

    // The id of the recording
    private String id = null;

    // The start time of the recording
    private long startTime = 0;

    // The end time of the recording
    private long endTime = 0;

    // The streams in the recording
    private List<Stream> streams = null;

    // The stream selected to be the video stream
    private Stream selectedVideoStream = null;

    // The stream selected to be the screen stream
    private Stream selectedScreenStream = null;

    // The directory holding the streams
    private String directory = null;

    // The event to which this recording is attached
    private String eventUri = null;

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
    public long getStartTime() {
        return startTime;
    }

    /**
     * Sets the startTime
     * @param startTime the startTime to set
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the endTime
     * @return the endTime
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Sets the endTime
     * @param endTime the endTime to set
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
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
    }

    /**
     * Returns the selectedVideoStream
     * @return the selectedVideoStream
     */
    public Stream getSelectedVideoStream() {
        return selectedVideoStream;
    }

    /**
     * Sets the selectedVideoStream
     * @param selectedVideoStream the selectedVideoStream to set
     */
    public void setSelectedVideoStream(Stream selectedVideoStream) {
        this.selectedVideoStream = selectedVideoStream;
    }

    /**
     * Returns the selectedScreenStream
     * @return the selectedScreenStream
     */
    public Stream getSelectedScreenStream() {
        return selectedScreenStream;
    }

    /**
     * Sets the selectedScreenStream
     * @param selectedScreenStream the selectedScreenStream to set
     */
    public void setSelectedScreenStream(Stream selectedScreenStream) {
        this.selectedScreenStream = selectedScreenStream;
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
}
