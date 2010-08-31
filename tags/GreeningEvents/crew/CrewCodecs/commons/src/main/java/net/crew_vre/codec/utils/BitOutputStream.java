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

import java.lang.reflect.Field;

import sun.misc.Unsafe;

/**
 * Outputs bits to an output stream
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class BitOutputStream {

    // The bit buffer
    private int bb = 0;

    // The number of bits in the buffer
    private int nbb = 0;

    // The output array to write to
    private byte[] output = null;

    private Unsafe unsafe = null;

    private long outputOffset = 0;

    private long byteArrayOffset = 0;

    private long startOutput = 0;

    private long currentOffset = 0;

    /**
     * Creates a new bit output stream
     * @param output The byte array to output to
     * @param offset The offset to start writing at
     */
    public BitOutputStream(byte[] output, int offset) {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            outputOffset = unsafe.objectFieldOffset(
                BitOutputStream.class.getDeclaredField("output"));
            byteArrayOffset = unsafe.arrayBaseOffset(byte[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.output = output;
        startOutput = byteArrayOffset + offset;
        currentOffset = startOutput;
    }

    private void storeBits() {
        long currentOutput = unsafe.getLong(this, outputOffset) + currentOffset;
        unsafe.putByte(currentOutput++, (byte) ((bb >> 24) & 0xFF));
        unsafe.putByte(currentOutput++, (byte) ((bb >> 16) & 0xFF));
        unsafe.putByte(currentOutput++, (byte) ((bb >> 8) & 0xFF));
        unsafe.putByte(currentOutput++, (byte) (bb & 0xFF));
        currentOffset += 4;
    }

    /**
     * Writes the least significant count bits from bits.
     * The most significant bit is written first
     * @param bits The bits to write
     * @param count The number of bits to write from bits
     */
    public void add(int bits, int count) {
        nbb += count;
        if (nbb > 32) {
            int extra = nbb - 32;
            bb |= (bits >> extra);
            storeBits();
            bb = bits << (32 - extra);
            nbb = extra;
        } else {
            bb |= bits << (32 - nbb);
        }
    }

    /**
     * Returns the number of bits written
     * @return the number of bits written
     */
    public int noBits() {
        return (int) ((currentOffset - startOutput) * 8) + nbb;
    }

    /**
     * Gets the number of bytes written to the stream
     * @return The number of bytes written to the stream
     */
    public int getLength() {
        return (int) (currentOffset - startOutput);
    }

    /**
     * Forces any bits in the buffer to be written to the stream
     * Note that the last byte may not be filled
     *
     * @return The number of extra bits unused in the last byte
     */
    public int flush() {
        long currentOutput = unsafe.getLong(this, outputOffset) + currentOffset;
        if (nbb > 0) {
            unsafe.putByte(currentOutput++, (byte) ((bb >> 24) & 0xFF));
            nbb -= 8;
            currentOffset += 1;
        }
        if (nbb > 0) {
            unsafe.putByte(currentOutput++, (byte) ((bb >> 16) & 0xFF));
            nbb -= 8;
            currentOffset += 1;
        }
        if (nbb > 0) {
            unsafe.putByte(currentOutput++, (byte) ((bb >> 8) & 0xFF));
            nbb -= 8;
            currentOffset += 1;
        }
        if (nbb > 0) {
            unsafe.putByte(currentOutput++, (byte) (bb & 0xFF));
            nbb -= 8;
            currentOffset += 1;
        }
        int extra = -nbb;
        nbb = 0;
        return extra;
    }
}
