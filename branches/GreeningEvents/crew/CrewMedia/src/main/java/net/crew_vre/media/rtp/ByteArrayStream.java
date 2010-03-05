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

import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushSourceStream;
import javax.media.protocol.SourceTransferHandler;
import javax.media.rtp.OutputDataStream;

/**
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ByteArrayStream implements PushSourceStream,
        OutputDataStream {

    private static final int DEFAULT_MIN_TRANSFER_SIZE = 1500;

    private byte[] currentData = null;

    private int currentDataLength = 0;

    private int currentDataOffset = 0;

    private Integer dataSync = new Integer(0);

    private boolean closed = false;

    private SourceTransferHandler transferHandler = null;

    /**
     *
     * @see javax.media.protocol.PushSourceStream#getMinimumTransferSize()
     */
    public int getMinimumTransferSize() {
        synchronized (dataSync) {
            if (currentData != null) {
                return currentDataLength;
            }
            return DEFAULT_MIN_TRANSFER_SIZE;
        }
    }

    /**
     *
     * @see javax.media.protocol.PushSourceStream#read(byte[], int, int)
     */
    public int read(byte[] data, int offset, int length) {
        synchronized (dataSync) {
            while ((currentData == null) && !closed) {
                try {
                    dataSync.wait();
                } catch (InterruptedException e) {
                    // Does Nothing
                }
            }
            if (!closed) {
                int bytesToCopy = Math.min(length, currentDataLength);
                System.arraycopy(currentData, currentDataOffset, data, offset,
                        bytesToCopy);
                currentData = null;
                dataSync.notifyAll();
                return bytesToCopy;
            }
            return -1;
        }
    }

    /**
     *
     * @see javax.media.protocol.PushSourceStream#setTransferHandler(
     *     javax.media.protocol.SourceTransferHandler)
     */
    public void setTransferHandler(SourceTransferHandler transferHandler) {
        synchronized (dataSync) {
            this.transferHandler = transferHandler;
        }
    }

    /**
     *
     * @see javax.media.protocol.SourceStream#endOfStream()
     */
    public boolean endOfStream() {
        synchronized (dataSync) {
            return closed;
        }
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

    /**
     *
     * @see javax.media.rtp.OutputDataStream#write(byte[], int, int)
     */
    public int write(byte[] data, int offset, int length) {
        synchronized (dataSync) {
            if (transferHandler == null) {
                return length;
            }
            while ((currentData != null) && !closed) {
                try {
                    dataSync.wait();
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
            if (!closed) {
                currentData = data;
                dataSync.notifyAll();
                transferHandler.transferData(this);
                return length;
            }
        }
        return -1;
    }

    /**
     * Closes the stream
     */
    public void close() {
        synchronized (dataSync) {
            closed = true;
            dataSync.notifyAll();
        }
    }

}
