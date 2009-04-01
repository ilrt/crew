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

package net.crew_vre.codec.ffmpeg;

import java.awt.Dimension;

import javax.media.format.JPEGFormat;

/**
 * An FFMPEG version of JPEG Video Format
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class FFMpegJPEGVideoFormat extends JPEGFormat
        implements FFMpegBasicVideoFormat {


    private static final long serialVersionUID = 1L;

    private long codecContext;

    /**
     * Creates a new FFMpegVideoFormat
     * @param v The video format to copy
     * @param codecContext A pointer to a codec context
     */
    public FFMpegJPEGVideoFormat(JPEGFormat v, long codecContext) {
        super();
        copy(v);
        this.codecContext = codecContext;
    }

    /**
     *
     * @see ffmpeg.FFMpegBasicVideoFormat#getCodecContext()
     */
    public long getCodecContext() {
        return codecContext;
    }

    /**
     * Sets the size of the format
     * @param size The new size
     */
    public void setSize(Dimension size) {
        this.size = size;
    }
}
