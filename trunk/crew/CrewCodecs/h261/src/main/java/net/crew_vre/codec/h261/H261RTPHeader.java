/*
 * @(#)H261RTPHeader.java
 * Created: 29 Feb 2008
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

package net.crew_vre.codec.h261;

/**
 * An H.261 RTP Header
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class H261RTPHeader {

    private int sbit = 0;

    private int ebit = 0;

    private int gobn = 0;

    private int mbap = 0;

    /**
     * Creates a new H261RTPHeader
     * @param packet The packet containing the header
     */
    public H261RTPHeader(byte[] packet) {
        sbit = (packet[0] >> 5) & 0x7;
        ebit = (packet[0] >> 2) & 0x7;
        gobn = (packet[1] >> 4) & 0xF;
        mbap = (packet[1] & 0xF) | ((packet[2] >> 7) & 0x1);
    }

    /**
     * Returns the ebit
     * @return the ebit
     */
    public int getEbit() {
        return ebit;
    }

    /**
     * Returns the gobn
     * @return the gobn
     */
    public int getGobn() {
        return gobn;
    }

    /**
     * Returns the mbap
     * @return the mbap
     */
    public int getMbap() {
        return mbap;
    }

    /**
     * Returns the sbit
     * @return the sbit
     */
    public int getSbit() {
        return sbit;
    }
}
