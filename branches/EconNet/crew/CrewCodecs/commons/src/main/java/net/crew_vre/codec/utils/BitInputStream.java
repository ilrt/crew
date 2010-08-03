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
 * An input stream for reading things a bit at a time
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class BitInputStream {

    private int bb = 0;

    private int nbb = 0;

    private byte[] input = null;

    private Unsafe unsafe = null;

    private long inputOffset = 0;

    private long byteArrayOffset = 0;

    private long shortSize = 0;

    private long currentOffset = 0;

    private long endInput = 0;

    /**
     * Creates a new BitInputStream
     * @param input The data array to read bits from
     * @param offset The offset to start reading from
     * @param length The length of the data in input
     */
    public BitInputStream(byte[] input, int offset, int length) {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            inputOffset = unsafe.objectFieldOffset(
                BitInputStream.class.getDeclaredField("input"));
            byteArrayOffset = unsafe.arrayBaseOffset(byte[].class);
            shortSize = unsafe.arrayIndexScale(short[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.input = input;
        currentOffset = byteArrayOffset + offset;
        endInput = currentOffset + length;
    }

    private void huffRQ() {
        bb <<= 16;
        long currentInput = unsafe.getLong(this, inputOffset) + currentOffset;
        bb |= (unsafe.getByte(currentInput++) & 0xFF) << 8;
        bb |=  unsafe.getByte(currentInput++) & 0xFF;
        currentOffset += 2;
    }

    /**
     * Looks at the next bit that will be read
     * @return The next bit
     */
    public int peekNextBit() {
        return bb >> (nbb - 1);
    }

    /**
     * Reads a set of bits from the input
     * @param n The number of bits to read
     * @return An integer containing the bits
     */
    public int readBits(int n) {
        nbb -= n;
        if (nbb < 0) {
            huffRQ();
            nbb += 16;
        }
        int val = (bb >> nbb) & ((1 << n) - 1);
        /*System.err.print("Reading " + n + " bits: ");
        for (int i = n - 1; i >= 0; i--) {
            if ((val & (1 << i)) > 0) {
                System.err.print("1");
            } else {
                System.err.print("0");
            }
        }
        System.err.println(); */
        return val;
    }

    /**
     * Decodes a huffman code
     * @param ht The address of a huffman table (shorts)
     * @param maxLen The maximum length of each code
     * @return The code found
     */
    public int huffDecode(long ht, int maxLen) {
        if (nbb < 16) {
            huffRQ();
            nbb += 16;
        }
        int s = maxLen;
        int v = (bb >> (nbb - s)) & ((1 << s) - 1);
        s = unsafe.getShort(ht + (v * shortSize));
        nbb -= (s & 0x1f);

        /*int len = (s & 0x1f);
        int code = v >> (maxLen - len);
        int val = (s >> 5);
        System.err.print("Huffman decoded " + len + " bits from " + H261ASDecoder.toBinaryString(v, maxLen) + ": ");
        for (int i = len - 1; i >= 0; i--) {
            if ((code & (1 << i)) > 0) {
                System.err.print("1");
            } else {
                System.err.print("0");
            }
        }
        System.err.println(" : " + val); */

        return (s >> 5);
    }

    /**
     * Gets the number of bits left to read
     * @return The number of bits left
     */
    public int bitsRemaining() {
        return ((int) (endInput - currentOffset) << 3) + nbb;
    }
}
