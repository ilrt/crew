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


package net.crew_vre.media.effect;

import javax.media.Buffer;
import javax.media.Effect;
import javax.media.Format;
import javax.media.Time;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;

/**
 * An effect to clone a datasource at some point in the tree
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class CloneEffect extends PushBufferDataSource
        implements Effect, PushBufferStream {

    // The format
    private Format format = null;

    // The buffer to read from
    private Buffer readBuffer = null;

    // The synchronizer of the buffer
    private Integer bufferSync = new Integer(0);

    // The handler of the transfers
    private BufferTransferHandler handler = null;

    private boolean done = false;

    /**
     *
     * @see javax.media.Codec#getSupportedInputFormats()
     */
    public Format[] getSupportedInputFormats() {
        return new Format[]{new VideoFormat(null), new AudioFormat(null)};
    }

    /**
     *
     * @see javax.media.Codec#getSupportedOutputFormats(javax.media.Format)
     */
    public Format[] getSupportedOutputFormats(Format format) {
        if (format == null) {
            return new Format[]{new VideoFormat(null), new AudioFormat(null)};
        }
        return new Format[]{format};
    }

    /**
     *
     * @see javax.media.Codec#process(javax.media.Buffer, javax.media.Buffer)
     */
    public int process(Buffer input, Buffer output) {
        if ((handler != null) && !done) {
            synchronized (bufferSync) {
                while ((readBuffer != null) && !done && (handler != null)) {
                    try {
                        bufferSync.wait();
                    } catch (InterruptedException e) {
                        // Does Nothing
                    }
                }
                readBuffer = input;
                bufferSync.notifyAll();
            }

            if (!done && (handler != null)) {
                handler.transferData(this);
            }
        }
        output.copy(input);
        return BUFFER_PROCESSED_OK;
    }

    /**
     *
     * @see javax.media.Codec#setInputFormat(javax.media.Format)
     */
    public Format setInputFormat(Format format) {
        this.format = format;
        return format;
    }

    /**
     *
     * @see javax.media.Codec#setOutputFormat(javax.media.Format)
     */
    public Format setOutputFormat(Format format) {
        this.format = format;
        return format;
    }

    /**
     *
     * @see javax.media.PlugIn#close()
     */
    public void close() {
        synchronized (bufferSync) {
            done = true;
            bufferSync.notifyAll();
        }
    }

    /**
     *
     * @see javax.media.PlugIn#getName()
     */
    public String getName() {
        return "Clone Effect";
    }

    /**
     *
     * @see javax.media.PlugIn#open()
     */
    public void open() {
        // Does Nothing
    }

    /**
     *
     * @see javax.media.PlugIn#reset()
     */
    public void reset() {
        // Does Nothing
    }

    /**
     *
     * @see javax.media.protocol.DataSource#getControl(java.lang.String)
     */
    public Object getControl(String s) {
        return null;
    }

    /**
     *
     * @see javax.media.protocol.DataSource#getControls()
     */
    public Object[] getControls() {
        return new Object[0];
    }

    /**
     *
     * @see javax.media.protocol.PushBufferStream#getFormat()
     */
    public Format getFormat() {
        return format;
    }

    /**
     *
     * @see javax.media.protocol.PushBufferStream#read(javax.media.Buffer)
     */
    public void read(Buffer buffer) {
        synchronized (bufferSync) {
            while ((readBuffer == null) && !done) {
                try {
                    bufferSync.wait();
                } catch (InterruptedException e) {
                    // Does Nothing
                }
            }
            buffer.copy(readBuffer);
            readBuffer = null;
            bufferSync.notifyAll();
        }
    }

    /**
     *
     * @see javax.media.protocol.PushBufferStream#setTransferHandler(
     *     javax.media.protocol.BufferTransferHandler)
     */
    public void setTransferHandler(BufferTransferHandler handler) {
        synchronized (bufferSync) {
            this.handler = handler;
            bufferSync.notifyAll();
        }
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
        return new ContentDescriptor(ContentDescriptor.RAW);
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
     * @see javax.media.protocol.PushBufferDataSource#getStreams()
     */
    public PushBufferStream[] getStreams() {
        return new PushBufferStream[]{this};
    }

    /**
     *
     * @see javax.media.protocol.DataSource#connect()
     */
    public void connect() {
        // Does Nothing
    }

    /**
     *
     * @see javax.media.protocol.DataSource#disconnect()
     */
    public void disconnect() {
        // Does Nothing
    }

    /**
     *
     * @see javax.media.protocol.DataSource#getContentType()
     */
    public String getContentType() {
        return null;
    }

    /**
     *
     * @see javax.media.protocol.DataSource#getDuration()
     */
    public Time getDuration() {
        return DURATION_UNBOUNDED;
    }

    /**
     *
     * @see javax.media.protocol.DataSource#start()
     */
    public void start() {
        // Does Nothing
    }

    /**
     *
     * @see javax.media.protocol.DataSource#stop()
     */
    public void stop() {
        // Does Nothing
    }

}
