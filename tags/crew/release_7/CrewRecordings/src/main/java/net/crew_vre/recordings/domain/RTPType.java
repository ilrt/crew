/*
 * @(#)RTPType.java
 * Created: 18 Aug 2008
 * Version: 1.0
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
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
 */

package net.crew_vre.recordings.domain;

import net.crew_vre.domain.DomainObject;

/**
 * Represents an RTPType
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class RTPType extends DomainObject {

    private static final String DEFAULT_MEDIA_TYPE = "data";

    private static final String DEFAULT_ENCODING = "Unknown";

    private int id = 0;

    private String mediaType = DEFAULT_MEDIA_TYPE;

    private String encoding = DEFAULT_ENCODING;

    private int clockRate = 0;

    /**
     * Returns the id
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the mediaType
     * @return the mediaType
     */
    public String getMediaType() {
        return mediaType;
    }

    /**
     * Sets the mediaType
     * @param mediaType the mediaType to set
     */
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * Returns the encoding
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the encoding
     * @param encoding the encoding to set
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Returns the clockRate
     * @return the clockRate
     */
    public int getClockRate() {
        return clockRate;
    }

    /**
     * Sets the clockRate
     * @param clockRate the clockRate to set
     */
    public void setClockRate(int clockRate) {
        this.clockRate = clockRate;
    }
}
