/*
 * @(#)DatagramForwarder.java
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
import java.util.LinkedList;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;

import net.crew_vre.media.rtp.RTPHeader;
import net.crew_vre.media.rtptype.RtpTypeRepository;

/**
 * Forwards received datagrams
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class DatagramForwarder implements PushBufferStream {

    private LinkedList<byte[]> queue = new LinkedList<byte[]>();

    private boolean done = false;

    private BufferTransferHandler transferHandler = null;

    private Format format = null;

    private RtpTypeRepository typeRepository = null;

    /**
     * Creates a new DatagramForwarder
     * @param typeRepository The repository to get types from
     */
    public DatagramForwarder(RtpTypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    /**
     *
     * @see javax.media.protocol.PushBufferStream#read(javax.media.Buffer)
     */
    public void read(Buffer buffer) {
        if (done) {
            buffer.setDiscard(true);
            buffer.setEOM(true);
            return;
        }

        if (queue.isEmpty()) {
            buffer.setDiscard(true);
            buffer.setEOM(false);
            return;
        }

        byte[] data = queue.removeFirst();
        try {
            RTPHeader header = new RTPHeader(data, 0, data.length);
            format = typeRepository.findRtpType(
                    header.getPacketType()).getFormat();
            buffer.setData(data);
            buffer.setOffset(RTPHeader.SIZE);
            buffer.setLength(data.length - RTPHeader.SIZE);
            buffer.setTimeStamp(header.getTimestamp());
            buffer.setSequenceNumber(header.getSequence());
            buffer.setFormat(format);
            int flags = Buffer.FLAG_RTP_TIME;
            if (header.getMarker() == 1) {
                flags = flags | Buffer.FLAG_RTP_MARKER;
            }
            buffer.setFlags(flags);
        } catch (IOException e) {
            e.printStackTrace();
            buffer.setDiscard(true);
        }

    }

    /**
     *
     * @see javax.media.protocol.PushBufferStream#setTransferHandler(
     *     javax.media.protocol.BufferTransferHandler)
     */
    public void setTransferHandler(BufferTransferHandler transferHandler) {
        this.transferHandler = transferHandler;
    }

    /**
     *
     * @see javax.media.protocol.SourceStream#endOfStream()
     */
    public boolean endOfStream() {
        return done;
    }

    /**
     *
     * @see javax.media.protocol.SourceStream#getContentDescriptor()
     */
    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(ContentDescriptor.RAW_RTP);
    }

    /**
     *
     * @see javax.media.protocol.SourceStream#getContentLength()
     */
    public long getContentLength() {
        return LENGTH_UNKNOWN;
    }

    /**
     *
     * @see javax.media.Controls#getControl(java.lang.String)
     */
    public Object getControl(String arg0) {
        return null;
    }

    /**
     *
     * @see javax.media.Controls#getControls()
     */
    public Object[] getControls() {
        return new Object[0];
    }

    /**
     * Closes the stream
     *
     */
    public void close() {
        done = true;
    }

    /**
     * Adds a packet to the queue
     * @param packet The packet to add
     * @param time The time in the packet
     */
    public void handlePacket(DatagramPacket packet, long time) {
        if ((transferHandler != null) && !done) {
            byte[] data = new byte[packet.getLength()];
            System.arraycopy(packet.getData(),
                    packet.getOffset(), data, 0, data.length);
            queue.addLast(data);
            transferHandler.transferData(this);
        }
    }

    /**
     *
     * @see javax.media.protocol.PushBufferStream#getFormat()
     */
    public Format getFormat() {
        return format;
    }

}
