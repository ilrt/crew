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


package net.crew_vre.media.processor;

import javax.media.Time;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;

/**
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ProcessorSource extends PushBufferDataSource {

    private ProcessingThread thread = null;

    /**
     * Creates a new ProcessorSource
     * @param thread The thread to use as a stream
     */
    public ProcessorSource(ProcessingThread thread) {
        this.thread = thread;
    }

    /**
     * @see javax.media.protocol.PushBufferDataSource#getStreams()
     */
    public PushBufferStream[] getStreams() {
        return new PushBufferStream[]{thread};
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
        return null;
    }

    /**
     * @see javax.media.protocol.DataSource#getControl(java.lang.String)
     */
    public Object getControl(String clss) {
        return thread.getControl(clss);
    }

    /**
     * @see javax.media.protocol.DataSource#getControls()
     */
    public Object[] getControls() {
        return thread.getControls();
    }

    /**
     * @see javax.media.protocol.DataSource#getDuration()
     */
    public Time getDuration() {
        return DURATION_UNKNOWN;
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
        // Does Nothing
    }

}
