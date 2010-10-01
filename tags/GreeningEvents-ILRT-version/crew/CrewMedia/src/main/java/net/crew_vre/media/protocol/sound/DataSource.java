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

package net.crew_vre.media.protocol.sound;

import java.io.IOException;

import javax.media.Time;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.sound.sampled.LineUnavailableException;

/**
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class DataSource extends PushBufferDataSource {

    private JavaSoundStream stream = new JavaSoundStream();

    /**
     * @see javax.media.protocol.PushBufferDataSource#getStreams()
     */
    public PushBufferStream[] getStreams() {
        return new PushBufferStream[]{stream};
    }

    /**
     * @see javax.media.protocol.DataSource#connect()
     */
    public void connect() throws IOException {
        try {
            stream.init(getLocator());
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            IOException exception = new IOException(e.getMessage());
            exception.initCause(e);
            throw exception;
        }
    }

    /**
     * @see javax.media.protocol.DataSource#disconnect()
     */
    public void disconnect() {
        stop();
        stream.uninit();
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
    public Object getControl(String name) {
        return stream.getControl(name);
    }

    /**
     * @see javax.media.protocol.DataSource#getControls()
     */
    public Object[] getControls() {
        return stream.getControls();
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
        stream.start();
    }

    /**
     * @see javax.media.protocol.DataSource#stop()
     */
    public void stop() {
        stream.stop();
    }

}
