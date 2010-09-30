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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.media.Buffer;
import javax.media.protocol.DataSource;

/**
 * A DataSink that outputs data to an output stream
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class OutputStreamDataSink extends DataSink {

    private DataOutputStream output = null;

    /**
     * Creates a new OuputStreamDataSink
     *
     * @param data The DataSource to read data from
     * @param track The track to read from
     * @param output The OutputStream to write data to
     */
    public OutputStreamDataSink(DataSource data, int track,
            OutputStream output) {
        super(data, track);
        this.output = new DataOutputStream(output);
    }

    /**
     *
     * @see DataSink#handleBuffer(javax.media.Buffer)
     */
    public void handleBuffer(Buffer buffer) throws IOException {
        Object dataObj = buffer.getData();
        int offset = buffer.getOffset();
        int length = buffer.getLength();
        if (dataObj instanceof byte[]) {
            output.write((byte[]) dataObj, offset, length);
        } else if (dataObj instanceof short[]) {
            short[] data = (short []) dataObj;
            for (int i = 0; i < length; i++) {
                output.writeShort(data[i + offset]);
            }
        } else if (dataObj instanceof int[]) {
            int[] data = (int []) dataObj;
            for (int i = 0; i < length; i++) {
                output.writeInt(data[i + offset]);
            }
        }
    }
}
