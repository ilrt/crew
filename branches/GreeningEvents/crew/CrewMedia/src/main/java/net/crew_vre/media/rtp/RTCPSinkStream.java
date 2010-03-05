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
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

import javax.media.rtp.OutputDataStream;

/**
 * A stream for sending rtcp packets to a sink
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class RTCPSinkStream implements OutputDataStream {

    private RTCPPacketSink sink = null;

    private MulticastSocket socket = null;

    private InetSocketAddress address = null;

    /**
     * Sets the packet sink
     * @param sink The new packet sink, or null to stop sinking
     */
    public void setSink(RTCPPacketSink sink) {
        this.sink = sink;
    }

    /**
     * Sets an address to send to
     * @param address The RTP address to send to (port + 1 will be used)
     * @throws IOException
     */
    public void setAddress(InetSocketAddress address) throws IOException {
        this.address = new InetSocketAddress(address.getAddress(),
                address.getPort() + 1);
        socket = new MulticastSocket(address.getPort() + 1);
        socket.joinGroup(address.getAddress());
    }

    /**
     *
     * @see javax.media.rtp.OutputDataStream#write(byte[], int, int)
     */
    public int write(byte[] data, int offset, int length) {
        if (sink != null) {
            try {
                sink.handleRTCPPacket(new DatagramPacket(data, offset, length));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            DatagramPacket packet = new DatagramPacket(data, offset, length);
            packet.setSocketAddress(address);
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return length;
    }

}
