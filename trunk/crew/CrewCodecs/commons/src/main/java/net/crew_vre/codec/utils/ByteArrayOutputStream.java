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


package net.crew_vre.codec.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A stream that writes to a byte array
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ByteArrayOutputStream extends OutputStream {

    private byte[] array = null;

    private int offset = 0;

    private int length = 0;

    private int count = 0;

    /**
     * Creates a new ByteArrayOutputStream
     * @param array The array to write to
     * @param offset The offset into the array to start at
     * @param length The maximum number of bytes to write to
     */
    public ByteArrayOutputStream(byte[] array, int offset, int length) {
        this.array = array;
        this.offset = offset;
        this.length = length;
    }

    /**
     *
     * @see java.io.OutputStream#write(int)
     */
    public void write(int b) throws IOException {
        if ((length < 1) || (offset >= array.length)) {
            throw new EOFException("End of byte array reached");
        }
        array[offset++] = (byte) b;
        length -= 1;
        count += 1;
    }

    /**
     *
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public void write(byte[] bytes, int off, int len) throws IOException {
        if ((length < len) || ((offset + len) > array.length)) {
            throw new EOFException("End of byte array reached: "
                    + length + ": " + len + ": " + offset + ": " + array.length);
        }
        System.arraycopy(bytes, off, array, offset, len);
        offset += len;
        length -= len;
        count += len;
    }

    /**
     * Returns the number of bytes written to the stream
     * @return The number of bytes written to the stream
     */
    public int getCount() {
        return count;
    }

    /**
     * Gets the remaining space available to write to
     * @return The number of bytes remaining
     */
    public int getSpace() {
        return length;
    }

}
