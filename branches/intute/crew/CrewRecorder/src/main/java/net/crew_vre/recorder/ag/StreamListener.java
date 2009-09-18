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

package net.crew_vre.recorder.ag;

import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;

/**
 * @author Andrew G D Rowley
 * @version 1.0
 */
public interface StreamListener {

    /**
     * Adds a video stream
     * @param ssrc The ssrc of the stream
     * @param dataSource The datasource of the stream
     * @param format The format of the stream
     */
    void addVideoStream(long ssrc, DataSource dataSource, VideoFormat format);

    /**
     * Sets the name of a video stream
     * @param ssrc The ssrc of the stream
     * @param name The name to set
     */
    void setVideoStreamName(long ssrc, String name);

    /**
     * Removes a video stream
     * @param ssrc The ssrc to remove
     */
    void removeVideoStream(long ssrc);


    /**
     * Adds an audio stream
     * @param ssrc The ssrc of the stream
     * @param dataSource The datasource of the stream
     * @param format The format of the stream
     */
    void addAudioStream(long ssrc, DataSource dataSource, AudioFormat format);

    /**
     * Sets the name of an audio stream
     * @param ssrc The ssrc of the stream
     * @param name The name to set
     */
    void setAudioStreamName(long ssrc, String name);

    /**
     * Removes an audio stream
     * @param ssrc The ssrc to remove
     */
    void removeAudioStream(long ssrc);
}
