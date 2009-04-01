/*
 * @(#)Stream.java
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

import net.crew_vre.domain.DomainObject;

/**
 * Represents one of the streams of the recording
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Stream extends DomainObject {

    // The RTP SSRC of the stream
    private String ssrc = "";

    // The recording containing this stream
    private Recording recording = null;

    // The start time in ms since the epoch
    private long startTime = 0;

    // The end time in ms since the epoch
    private long endTime = 0;

    // The first RTP timestamp contained in the stream
    private long firstTimestamp = 0;

    // The number of packets stored in the file
    private long packetsSeen = 0;

    // The number of packets that were missed when the recording took place
    private long packetsMissed = 0;

    // The number of bytes in the file
    private long bytes = 0;

    // The RTP type of the data
    private RTPType rtpType = null;

    // The stream CNAME
    private String cname = null;

    // The stream NAME
    private String name = null;

    // The stream EMAIL
    private String email = null;

    // The stream PHONE
    private String phone = null;

    // The stream LOC
    private String location = null;

    // The stream TOOL
    private String tool = null;

    // The stream NOTE
    private String note = null;

    // The file holding the stream
    private String file = null;

    /**
     * Returns the ssrc
     * @return the ssrc
     */
    public String getSsrc() {
        return ssrc;
    }

    /**
     * Sets the ssrc
     * @param ssrc the ssrc to set
     */
    public void setSsrc(String ssrc) {
        this.ssrc = ssrc;
    }

    /**
     * Returns the recording
     * @return the recording
     */
    public Recording getRecording() {
        return recording;
    }

    /**
     * Sets the recording
     * @param recording the recording to set
     */
    public void setRecording(Recording recording) {
        this.recording = recording;
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
     * Returns the firstTimestamp
     * @return the firstTimestamp
     */
    public long getFirstTimestamp() {
        return firstTimestamp;
    }

    /**
     * Sets the firstTimestamp
     * @param firstTimestamp the firstTimestamp to set
     */
    public void setFirstTimestamp(long firstTimestamp) {
        this.firstTimestamp = firstTimestamp;
    }

    /**
     * Returns the packetsSeen
     * @return the packetsSeen
     */
    public long getPacketsSeen() {
        return packetsSeen;
    }

    /**
     * Sets the packetsSeen
     * @param packetsSeen the packetsSeen to set
     */
    public void setPacketsSeen(long packetsSeen) {
        this.packetsSeen = packetsSeen;
    }

    /**
     * Returns the packetsMissed
     * @return the packetsMissed
     */
    public long getPacketsMissed() {
        return packetsMissed;
    }

    /**
     * Sets the packetsMissed
     * @param packetsMissed the packetsMissed to set
     */
    public void setPacketsMissed(long packetsMissed) {
        this.packetsMissed = packetsMissed;
    }

    /**
     * Returns the bytes
     * @return the bytes
     */
    public long getBytes() {
        return bytes;
    }

    /**
     * Sets the bytes
     * @param bytes the bytes to set
     */
    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    /**
     * Returns the rtpType
     * @return the rtpType
     */
    public RTPType getRtpType() {
        return rtpType;
    }

    /**
     * Sets the rtpType
     * @param rtpType the rtpType to set
     */
    public void setRtpType(RTPType rtpType) {
        this.rtpType = rtpType;
    }

    /**
     * Returns the cname
     * @return the cname
     */
    public String getCname() {
        return cname;
    }

    /**
     * Sets the cname
     * @param cname the cname to set
     */
    public void setCname(String cname) {
        this.cname = cname;
    }

    /**
     * Returns the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the email
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the phone
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the location
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns the tool
     * @return the tool
     */
    public String getTool() {
        return tool;
    }

    /**
     * Sets the tool
     * @param tool the tool to set
     */
    public void setTool(String tool) {
        this.tool = tool;
    }

    /**
     * Returns the note
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the note
     * @param note the note to set
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Returns the file
     * @return the file
     */
    public String getFile() {
        return file;
    }

    /**
     * Sets the file
     * @param file the file to set
     */
    public void setFile(String file) {
        this.file = file;
    }
}
