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

package net.crew_vre.recorder.utils;

import java.io.IOException;

import javax.media.CannotRealizeException;
import javax.media.NoPlayerException;
import javax.media.protocol.DataSource;
import javax.sound.sampled.FloatControl;

/**
 * A listener for local streams
 * @author Andrew G D Rowley
 * @version 1.0
 */
public interface LocalStreamListener {

    /**
     * Adds a local audio datasource
     * @param name The name of the audio source
     * @param dataSource The datasource
     * @param volumeControl The volume control or null of none
     * @throws IOException
     * @throws CannotRealizeException
     * @throws NoPlayerException
     */
    void addLocalAudio(String name, DataSource dataSource,
            FloatControl volumeControl) throws NoPlayerException,
            CannotRealizeException, IOException;

    /**
     * Removes the local audio playback
     * @param dataSource The datasource to remove
     */
    void removeLocalAudio(DataSource dataSource);


    /**
     * Adds a video datasource
     * @param name The name of the data source
     * @param dataSource The data source to add
     * @param ssrc The ssrc of the video
     * @throws IOException
     */
    void addVideo(String name, DataSource dataSource, long ssrc)
            throws IOException;


    /**
     * Removes the video
     *
     * @param dataSource The datasource to remove
     */
    void removeVideo(DataSource dataSource);
}
