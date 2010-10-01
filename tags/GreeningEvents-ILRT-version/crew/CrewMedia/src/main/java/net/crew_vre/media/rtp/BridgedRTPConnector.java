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
import java.util.HashMap;
import java.util.LinkedList;

import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushSourceStream;
import javax.media.protocol.SourceTransferHandler;
import javax.media.rtp.OutputDataStream;
import javax.media.rtp.RTPConnector;

import memetic.crypto.AESCrypt;
import memetic.crypto.DESCrypt;
import memetic.crypto.RTPCrypt;

import ag3.bridge.BridgeClient;
import ag3.bridge.BridgeClientCreator;
import ag3.interfaces.types.BridgeDescription;
import ag3.interfaces.types.MulticastNetworkLocation;
import ag3.interfaces.types.NetworkLocation;
import ag3.interfaces.types.UnicastNetworkLocation;

/**
 * An RTP Connector that connects to a bridge implementation
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class BridgedRTPConnector implements RTPConnector {

    private BridgeClient client = null;

    private boolean closed = false;

    private Input rtpInput = new Input();

    private Input rtcpInput = new Input();

    private Output rtpOutput = new Output(true);

    private Output rtcpOutput = new Output(false);

    private RTPCrypt encryption = null;

    private RTPPacketSink rtpPacketSink = null;

    private RTCPPacketSink rtcpPacketSink = null;

    /**
     * Creates a new BridgedRTPConnector
     * @param bridge The bridge to connect to
     * @param locations The locations to connect to
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public BridgedRTPConnector(BridgeDescription bridge,
            NetworkLocation[] locations)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, IOException {
        client = BridgeClientCreator.create(bridge);
        client.joinBridge(locations);
        rtpInput.start();
        rtcpInput.start();
        if (client.isSinglePacketStream()) {
            HashMap<NetworkLocation, Input> inputMap =
                new HashMap<NetworkLocation, Input>();
            for (int i = 0; i < locations.length; i++) {
                inputMap.put(locations[i], rtpInput);
                inputMap.put(getRtcpLocation(locations[i]), rtcpInput);
            }
            SingleStreamReader reader = new SingleStreamReader(inputMap);
            reader.start();
        } else {
            for (int i = 0; i < locations.length; i++) {
                MultiStreamReader reader = new MultiStreamReader(locations[i],
                        rtpInput, true);
                MultiStreamReader rtcpReader = new MultiStreamReader(
                        getRtcpLocation(locations[i]), rtcpInput, false);
                reader.start();
                rtcpReader.start();
            }
        }
    }

    /**
     * Sets the rtp packet sink
     * @param sink The sink to set
     */
    public void setRtpSink(RTPPacketSink sink) {
        this.rtpPacketSink = sink;
    }

    /**
     * Sets the rtcp packet sink
     * @param sink The sink to set
     */
    public void setRtcpSink(RTCPPacketSink sink) {
        this.rtcpPacketSink = sink;
    }

    private RTPCrypt getEncryption(String enc)
            throws UnsupportedEncryptionException {
        if ((enc != null) && !enc.equals("")) {
            String encType = DESCrypt.TYPE;
            int slash = enc.indexOf("/");
            if (slash != -1) {
                encType = enc.substring(0, slash);
                enc = enc.substring(slash + 1);
            }
            if (encType.equals(DESCrypt.TYPE)) {
                return new RTPCrypt(new DESCrypt(enc));
            } else if (encType.equals(AESCrypt.TYPE)) {
                return new RTPCrypt(new AESCrypt(enc));
            } else {
                throw new UnsupportedEncryptionException(
                        "Unsupported Encryption Type Specified: " + encType);
            }
        }
        return null;
    }

    /**
     * Sets the encryption key
     * @param key The key, or null to remove encryption
     * @throws UnsupportedEncryptionException
     */
    public void setEncryption(String key)
            throws UnsupportedEncryptionException {
        encryption = getEncryption(key);
    }

    private NetworkLocation getRtcpLocation(NetworkLocation rtpLocation) {
        NetworkLocation rtcpLocation = null;
        if (rtpLocation instanceof MulticastNetworkLocation) {
            rtcpLocation = new MulticastNetworkLocation();
            ((MulticastNetworkLocation) rtcpLocation).setTtl(
                    ((MulticastNetworkLocation) rtpLocation).getTtl());
        } else {
            rtcpLocation = new UnicastNetworkLocation();
        }
        rtcpLocation.setHost(rtpLocation.getHost());
        rtcpLocation.setPort(rtpLocation.getPort() + 1);
        return rtcpLocation;
    }

    /**
     *
     * @see javax.media.rtp.RTPConnector#close()
     */
    public void close() {
        try {
            closed = true;
            rtpInput.close();
            rtcpInput.close();
            client.leaveBridge();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @see javax.media.rtp.RTPConnector#getControlInputStream()
     */
    public PushSourceStream getControlInputStream() {
        return rtcpInput;
    }

    /**
     *
     * @see javax.media.rtp.RTPConnector#getControlOutputStream()
     */
    public OutputDataStream getControlOutputStream() {
        return rtcpOutput;
    }

    /**
     *
     * @see javax.media.rtp.RTPConnector#getDataInputStream()
     */
    public PushSourceStream getDataInputStream() {
        return rtpInput;
    }

    /**
     *
     * @see javax.media.rtp.RTPConnector#getDataOutputStream()
     */
    public OutputDataStream getDataOutputStream() {
        return rtpOutput;
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

    /**
     * Indicates where the given stream should be sent
     * @param ssrc The ssrc to send
     * @param location The location to send to
     */
    public void addStream(long ssrc, NetworkLocation location) {
        rtpOutput.addStream(ssrc, location);
        rtcpOutput.addStream(ssrc, getRtcpLocation(location));
    }

    private class Output implements OutputDataStream {

        private HashMap<Long, NetworkLocation> streamLocationMap =
            new HashMap<Long, NetworkLocation>();

        private boolean isRtp = true;

        private Output(boolean isRtp) {
            this.isRtp = isRtp;
        }

        private void addStream(long ssrc, NetworkLocation location) {
            streamLocationMap.put(ssrc, location);
        }

        private void encrypt(DatagramPacket packet, boolean isRtp) {
            if (encryption != null) {
                byte[] out = new byte[encryption.getEncryptOutputSize(
                        packet.getLength())];
                int length = 0;
                try {
                    if (isRtp) {
                        length = encryption.encryptData(packet.getData(),
                                packet.getOffset(), packet.getLength(), out, 0);
                    } else {
                        length = encryption.encryptCtrl(packet.getData(),
                                packet.getOffset(), packet.getLength(), out, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (length > 0) {
                    packet.setData(out, 0, length);
                } else {
                    packet.setLength(0);
                }
            }
        }

        /**
         *
         * @see javax.media.rtp.OutputDataStream#write(byte[], int, int)
         */
        public int write(byte[] data, int offset, int length) {
            try {
                long ssrc = 0;
                if (isRtp) {
                    RTPHeader header = new RTPHeader(data, offset, length);
                    ssrc = header.getSsrc();
                } else {
                    RTCPHeader header = new RTCPHeader(data, offset, length);
                    ssrc = header.getSsrc();
                }
                NetworkLocation location = streamLocationMap.get(ssrc);
                if (location != null) {
                    DatagramPacket packet = new DatagramPacket(data, offset,
                            length);
                    encrypt(packet, isRtp);
                    client.sendPacket(packet, location);
                    return length;
                }
                throw new Exception("Unknown stream " + ssrc);
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

    }

    private void decrypt(DatagramPacket packet, boolean isRtp) {
        if (encryption != null) {
            if ((packet.getLength() % encryption.getBlockSize()) != 0) {
                return;
            }
            byte[] out = new byte[
                encryption.getDecryptOutputSize(
                        packet.getLength())];
            int length = 0;

            try {
                if (isRtp) {
                    length = encryption.decryptData(packet.getData(),
                            packet.getOffset(), packet.getLength(),
                            out, 0);

                } else {
                    length = encryption.decryptCtrl(packet.getData(),
                            packet.getOffset(), packet.getLength(),
                            out, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (length > 0) {
                int maxlen = packet.getLength();
                if (length < packet.getLength()) {
                    maxlen = length;
                }
                System.arraycopy(out, 0, packet.getData(),
                        packet.getOffset(), maxlen);
                packet.setLength(maxlen);
            } else {
                packet.setLength(0);
            }
        }
    }

    private class MultiStreamReader extends Thread {

        private NetworkLocation location = null;

        private Input input = null;

        private boolean isRtp = false;

        private MultiStreamReader(NetworkLocation location, Input input,
                boolean isRtp) {
            this.location = location;
            this.input = input;
            this.isRtp = isRtp;
        }

        /**
         *
         * @see java.lang.Thread#run()
         */
        public void run() {
            while (!closed) {
                try {
                    DatagramPacket packet = client.receivePacket(location);
                    decrypt(packet, isRtp);
                    input.addPacket(packet);
                    if (isRtp) {
                        if (rtpPacketSink != null) {
                            rtpPacketSink.handleRTPPacket(packet);
                        }
                    } else {
                        if (rtcpPacketSink != null) {
                            rtcpPacketSink.handleRTCPPacket(packet);
                        }
                    }
                } catch (IOException e) {
                    // Does Nothing
                }
            }
        }
    }

    private class SingleStreamReader extends Thread {

        private HashMap<NetworkLocation, Input> inputMap =
            new HashMap<NetworkLocation, Input>();

        private SingleStreamReader(HashMap<NetworkLocation, Input> inputMap) {
            this.inputMap = inputMap;
        }

        /**
         *
         * @see java.lang.Thread#run()
         */
        public void run() {
            while (!closed) {
                try {
                    DatagramPacket packet = client.receivePacket();
                    NetworkLocation location = new NetworkLocation();
                    location.setHost(packet.getAddress().getHostAddress());
                    location.setPort(packet.getPort());
                    Input input = inputMap.get(location);
                    boolean isRtp = (packet.getPort() % 2) == 0;
                    decrypt(packet, isRtp);
                    input.addPacket(packet);
                    if (isRtp) {
                        if (rtpPacketSink != null) {
                            rtpPacketSink.handleRTPPacket(packet);
                        }
                    } else {
                        if (rtcpPacketSink != null) {
                            rtcpPacketSink.handleRTCPPacket(packet);
                        }
                    }
                } catch (IOException e) {
                    // Do Nothing
                }
            }
        }
    }

    private class Input extends Thread implements PushSourceStream {

        private SourceTransferHandler transferHandler = null;

        private boolean done = false;

        private LinkedList<DatagramPacket> queue =
            new LinkedList<DatagramPacket>();

        /**
         *
         * @see java.lang.Thread#run()
         */
        public void run() {
            while (!done) {
                synchronized (queue) {
                    while (!done && queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            // Do Nothing
                        }
                    }
                    if (!done) {
                        transferHandler.transferData(this);
                    }
                }
            }
        }

        private void addPacket(DatagramPacket packet) {
            if (transferHandler != null) {
                synchronized (queue) {
                    queue.addLast(packet);
                    queue.notifyAll();
                }
            }
        }

        private void close() {
            synchronized (queue) {
                done = true;
                queue.notifyAll();
            }
        }

        /**
         *
         * @see javax.media.protocol.PushSourceStream#getMinimumTransferSize()
         */
        public int getMinimumTransferSize() {
            return 1024;
        }

        public int read(byte[] data, int offset, int length) {
            DatagramPacket packet = queue.removeFirst();
            int bytesRead = Math.min(length, packet.getLength());
            System.arraycopy(packet.getData(), packet.getOffset(), data, offset,
                    bytesRead);
            return bytesRead;
        }

        /**
         *
         * @see javax.media.protocol.PushSourceStream#setTransferHandler(
         *     javax.media.protocol.SourceTransferHandler)
         */
        public void setTransferHandler(SourceTransferHandler transferHandler) {
            this.transferHandler = transferHandler;
        }

        /**
         *
         * @see javax.media.protocol.SourceStream#endOfStream()
         */
        public boolean endOfStream() {
            return false;
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
        public Object getControl(String className) {
            return null;
        }

        /**
         *
         * @see javax.media.Controls#getControls()
         */
        public Object[] getControls() {
            return new Object[0];
        }

    }

}
