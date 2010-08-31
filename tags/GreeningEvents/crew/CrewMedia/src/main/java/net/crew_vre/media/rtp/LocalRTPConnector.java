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

package net.crew_vre.media.rtp;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.media.protocol.PushSourceStream;
import javax.media.rtp.OutputDataStream;
import javax.media.rtp.RTPConnector;

/**
 * An RTP Connector that transfers data through byte arrays
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class LocalRTPConnector implements RTPConnector {

    private RTPSinkStream dataStream = new RTPSinkStream();

    private RTCPSinkStream ctrlStream = new RTCPSinkStream();

    private NullPushSourceStream nullStream = new NullPushSourceStream();

    /**
     * Sets the RTP Sink
     * @param sink The RTP Sink
     */
    public void setRTPSink(RTPPacketSink sink) {
        dataStream.setSink(sink);
    }

    /**
     * Sets the RTCP Sink
     * @param sink The RTCP Sink
     */
    public void setRTCPSink(RTCPPacketSink sink) {
        ctrlStream.setSink(sink);
    }

    /**
     * Sets an address to send packets to
     * @param address The address to send to
     * @throws IOException
     */
    public void setAddress(InetSocketAddress address) throws IOException {
        dataStream.setAddress(address);
        ctrlStream.setAddress(address);
    }

    /**
     *
     * @see javax.media.rtp.RTPConnector#close()
     */
    public void close() {
        // Does Nothing
    }

    /**
     *
     * @see javax.media.rtp.RTPConnector#getControlInputStream()
     */
    public PushSourceStream getControlInputStream() {
        return nullStream;
    }

    /**
     *
     * @see javax.media.rtp.RTPConnector#getControlOutputStream()
     */
    public OutputDataStream getControlOutputStream() {
        return ctrlStream;
    }

    /**
     *
     * @see javax.media.rtp.RTPConnector#getDataInputStream()
     */
    public PushSourceStream getDataInputStream() {
        return nullStream;
    }

    /**
     *
     * @see javax.media.rtp.RTPConnector#getDataOutputStream()
     */
    public OutputDataStream getDataOutputStream() {
        return dataStream;
    }

    /**
     *
     * @see javax.media.rtp.RTPConnector#getRTCPBandwidthFraction()
     */
    public double getRTCPBandwidthFraction() {
        return -1;
    }

    /**
     *
     * @see javax.media.rtp.RTPConnector#getRTCPSenderBandwidthFraction()
     */
    public double getRTCPSenderBandwidthFraction() {
        return -1;
    }

    /**
     *
     * @see javax.media.rtp.RTPConnector#getReceiveBufferSize()
     */
    public int getReceiveBufferSize() {
        return 1024;
    }

    /**
     *
     * @see javax.media.rtp.RTPConnector#getSendBufferSize()
     */
    public int getSendBufferSize() {
        return 1024;
    }

    /**
     *
     * @see javax.media.rtp.RTPConnector#setReceiveBufferSize(int)
     */
    public void setReceiveBufferSize(int arg0) {
        // Does Nothing
    }

    /**
     *
     * @see javax.media.rtp.RTPConnector#setSendBufferSize(int)
     */
    public void setSendBufferSize(int arg0) {
        // Does Nothing
    }

}
