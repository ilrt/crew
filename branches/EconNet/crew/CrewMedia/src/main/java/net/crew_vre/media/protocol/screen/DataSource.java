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


package net.crew_vre.media.protocol.screen;

import javax.media.Time;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;

/**
 * This DataSource captures live frames from the screen. You can specify the
 * location, size and frame rate in the URL string as follows:
 * screen://x,y,width,height/framespersecond screen://fullscreen:
 * <screen>/framespersecond Eg: screen://20,40,160,120/12.5
 * screen://fullscreen:0
 *
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class DataSource extends PushBufferDataSource {

    // True if the source has started
    private boolean started = false;

    // The content type of the source
    private String contentType = "raw";

    // True if the source has been connected
    private boolean connected = false;

    // The duration of the source
    private Time duration = DURATION_UNBOUNDED;

    // The streams of the source
    private LiveStream[] streams = null;

    // The stream of the source
    private LiveStream stream = null;

    /**
     * Creates a new Screen datasource
     */
    public DataSource() {
        // Does Nothing
    }

    /**
     * @see javax.media.protocol.DataSource#getContentType()
     */
    public String getContentType() {
        if (!connected) {
            System.err.println("Error: DataSource not connected");
            return null;
        }
        return contentType;
    }

    /**
     * @see javax.media.protocol.DataSource#connect()
     */
    public void connect() {
        if (connected) {
            return;
        }
        connected = true;
    }

    /**
     * @see javax.media.protocol.DataSource#disconnect()
     */
    public void disconnect() {
        if (started) {
            stop();
        }
        connected = false;
    }

    /**
     * @see javax.media.protocol.DataSource#start()
     */
    public void start() {
        // we need to throw error if connect() has not been called
        if (!connected) {
            throw new java.lang.Error(
                    "DataSource must be connected before it can be started");
        }
        if (started) {
            return;
        }
        started = true;
        stream.start(true);
    }

    /**
     * @see javax.media.protocol.DataSource#stop()
     */
    public void stop() {
        if ((!connected) || (!started)) {
            return;
        }
        started = false;
        stream.start(false);
    }

    /**
     * @see javax.media.Controls#getControls()
     */
    public Object[] getControls() {
        return stream.getControls();
    }

    /**
     * @see javax.media.Controls#getControl(java.lang.String)
     */
    public Object getControl(String controlType) {
        return stream.getControl(controlType);
    }

    /**
     * @see javax.media.Duration#getDuration()
     */
    public Time getDuration() {
        return duration;
    }

    /**
     * @see javax.media.protocol.PushBufferDataSource#getStreams()
     */
    public PushBufferStream[] getStreams() {
        if (streams == null) {
            streams = new LiveStream[1];
            streams[0] = new LiveStream(getLocator());
            stream = streams[0];
        }
        return streams;
    }
}
