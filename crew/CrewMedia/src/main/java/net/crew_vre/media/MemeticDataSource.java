/*
 * @(#)MemeticDataSource.java
 * Created: 2 Nov 2007
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

package net.crew_vre.media;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.HashMap;

import javax.media.Time;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;

import net.crew_vre.media.rtp.RTPHeader;
import net.crew_vre.media.rtptype.RtpTypeRepository;

/**
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class MemeticDataSource extends PushBufferDataSource {

    // A map of sequence numbers to times of receipt
    private HashMap<Long, Long> sequenceTimeMap = new HashMap<Long, Long>();

    // The rtp stream
    private DatagramForwarder rtpStream = null;;

    // The first time seen
    private long firstTime = -1;

    /**
     * Creates a new MemeticDataSource
     * @param typeRepository The repository to get RTP types from
     */
    public MemeticDataSource(RtpTypeRepository typeRepository) {
        rtpStream = new DatagramForwarder(typeRepository);
    }

    /**
     * @see javax.media.protocol.PushDataSource#getStreams()
     */
    public PushBufferStream[] getStreams() {
        return new PushBufferStream[]{rtpStream};
    }

    /**
     * @see javax.media.protocol.DataSource#connect()
     */
    public void connect() {

        // Does Nothing
    }

    /**
     * @see javax.media.protocol.DataSource#disconnect()
     */
    public void disconnect() {

        // Does Nothing
    }

    /**
     * @see javax.media.protocol.DataSource#getContentType()
     */
    public String getContentType() {
        return "raw";
    }

    /**
     * @see javax.media.protocol.DataSource#getControl(java.lang.String)
     */
    public Object getControl(String cls) {
        return rtpStream.getControl(cls);
    }

    /**
     * @see javax.media.protocol.DataSource#getControls()
     */
    public Object[] getControls() {
        return new Object[0];
    }

    /**
     * @see javax.media.protocol.DataSource#getDuration()
     */
    public Time getDuration() {
        return DURATION_UNBOUNDED;
    }

    /**
     * @see javax.media.protocol.DataSource#start()
     */
    public void start() {

        // Does Nothing
    }

    /**
     * @see javax.media.protocol.DataSource#stop()
     */
    public void stop() {
        rtpStream.close();
    }

    /**
     * Handles an RTP Packet
     * @param packet The packet to handle
     * @param time The time at which the packet was received
     */
    public void handleRTPPacket(DatagramPacket packet, long time) {
        try {
            if (firstTime == -1) {
                firstTime = time;
            }
            RTPHeader header = new RTPHeader(packet);
            sequenceTimeMap.put(new Long(header.getTimestamp()),
                    new Long(time - firstTime));
            rtpStream.handlePacket(packet, time);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the time of a recent sequence number
     * @param sequence The sequence number to get the time of
     * @return The time at which the packet was received
     */
    public long getTime(long sequence) {
        if (!sequenceTimeMap.containsKey(sequence)) {
            return -1;
        }
        return sequenceTimeMap.get(sequence);
    }
}
