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

import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushSourceStream;
import javax.media.protocol.SourceTransferHandler;
import javax.media.rtp.OutputDataStream;
import javax.media.rtp.RTPConnector;

import net.sf.fmj.media.rtp.RTPSocketAdapter;

/**
 * An RTP Socket Adaptor that only sends data
 *
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class SendOnlyRTPSocketAdapter implements RTPConnector {

    // The input stream of the data channel
    private SockInputStream dataInStrm = null;

    // The input stream of the control channel
    private SockInputStream ctrlInStrm = null;

    // The sending socket
    private RTPSocketAdapter socket = null;

    /**
     * Creates a new SendOnlyRTPSocketAdaptor using an RTPSocketAdaptor
     *
     * @param s
     *            The RTPSocketAdaptor to send using
     */
    public SendOnlyRTPSocketAdapter(RTPSocketAdapter s) {
        socket = s;
    }

    /**
     * Returns an input stream to receive the RTP data.
     */
    public PushSourceStream getDataInputStream() {
        if (dataInStrm == null) {
            dataInStrm = new SockInputStream();
            dataInStrm.start();
        }
        return dataInStrm;
    }

    /**
     * Returns an output stream to send the RTP data.
     * @throws IOException
     */
    public OutputDataStream getDataOutputStream() throws IOException {
        return socket.getDataOutputStream();
    }

    /**
     * Returns an input stream to receive the RTCP data.
     */
    public PushSourceStream getControlInputStream() {
        if (ctrlInStrm == null) {
            ctrlInStrm = new SockInputStream();
            ctrlInStrm.start();
        }
        return ctrlInStrm;
    }

    /**
     * Returns an output stream to send the RTCP data.
     * @throws IOException
     */
    public OutputDataStream getControlOutputStream() throws IOException {
        return socket.getControlOutputStream();
    }

    /**
     * Close all the RTP, RTCP streams.
     */
    public void close() {
        if (dataInStrm != null) {
            dataInStrm.kill();
        }
        if (ctrlInStrm != null) {
            ctrlInStrm.kill();
        }
    }

    /**
     * Set the receive buffer size of the RTP data channel.
     * This is only a hint to the implementation.
     * The actual implementation may not be able to do
     * anything to this.
     */
    public void setReceiveBufferSize(int size) throws IOException {
        socket.setReceiveBufferSize(size);
    }

    /**
     * Get the receive buffer size set on the RTP data channel.
     * Return -1 if the receive buffer size is not applicable for
     * the implementation.
     */
    public int getReceiveBufferSize() {
        try {
            return socket.getReceiveBufferSize();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Set the send buffer size of the RTP data channel.
     * This is only a hint to the implementation.
     * The actual implementation may not be able to do
     * anything to this.
     */
    public void setSendBufferSize(int size) throws IOException {
        socket.setSendBufferSize(size);
    }

    /**
     * Get the send buffer size set on the RTP data channel.
     * Return -1 if the send buffer size is not applicable
     * for the implementation.
     */
    public int getSendBufferSize() {
        try {
            return socket.getSendBufferSize();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Return the RTCP bandwidth fraction.
     * This value is used to initialize the
     * RTPManager. Check RTPManager for more detauls. Return -1 to use the
     * default values.
     */
    public double getRTCPBandwidthFraction() {
        return -1;
    }

    /**
     * Return the RTCP sender bandwidth fraction. This value is used to
     * initialize the RTPManager. Check RTPManager for more detauls.
     * Return -1 to use the default values.
     */
    public double getRTCPSenderBandwidthFraction() {
        return -1;
    }

    /**
     * An inner class to return a null stream
     */
    private class SockInputStream extends Thread
                                  implements PushSourceStream {

        // Minimum transfer size
        private static final int MINIMUM_MTU = 2048;

        // True if the socket is finished with
        private boolean done = false;

        // True if any data was read in the last round
        private boolean dataRead = false;

        // A transfer handler to handle the transfer of data
        private SourceTransferHandler sth = null;

        /**
         * Creates a new SockInputStream
         */
        public SockInputStream() {
            // Does Nothing
        }

        /**
         * Returns -1
         *
         * @see javax.media.protocol.PushSourceStream#read(byte[], int, int)
         */
        public int read(byte[] buffer, int offset, int length) {
            return -1;
        }

        /**
         * Starts the socket reading
         *
         * @see java.lang.Thread#start()
         */
        public synchronized void start() {
            super.start();
            if (sth != null) {
                dataRead = true;
                notify();
            }
        }

        /**
         * Stops the socket reading
         */
        public synchronized void kill() {
            done = true;
            notify();
        }

        /**
         *
         * @see javax.media.protocol.PushSourceStream
         *                              #getMinimumTransferSize()
         */
        public int getMinimumTransferSize() {
            return MINIMUM_MTU;
        }

        /**
         *
         * @see javax.media.protocol.PushSourceStream
         *  #setTransferHandler(javax.media.protocol.SourceTransferHandler)
         */
        public synchronized void setTransferHandler(
                SourceTransferHandler sth) {
            this.sth = sth;
            dataRead = true;
            notify();
        }

        /**
         *
         * @see javax.media.protocol.SourceStream#getContentDescriptor()
         */
        public ContentDescriptor getContentDescriptor() {
            return null;
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
         * @see javax.media.protocol.SourceStream#endOfStream()
         */
        public boolean endOfStream() {
            return true;
        }

        /**
         *
         * @see javax.media.Controls#getControls()
         */
        public Object[] getControls() {
            return new Object[0];
        }

        /**
         *
         * @see javax.media.Controls#getControl(java.lang.String)
         */
        public Object getControl(String type) {
            return null;
        }

        /**
         * Loop and notify the transfer handler of new data.
         */
        public void run() {
            while (!done) {

                synchronized (this) {
                    while (!dataRead && !done) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            // Do Nothing
                        }
                    }
                    dataRead = false;
                }

                if ((sth != null) && !done) {
                    sth.transferData(this);
                }
            }
        }
    }
}
