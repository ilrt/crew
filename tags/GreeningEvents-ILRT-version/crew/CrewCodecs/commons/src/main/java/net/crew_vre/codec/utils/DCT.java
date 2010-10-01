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
import java.text.DecimalFormat;

import javax.media.Buffer;

import sun.misc.Unsafe;

/**
 * This class is used to perform the forward and inverse discrete cosine
 * transform (DCT) as specified by the CCITT TI.81 recommendation
 * (www.w3.org/Graphics/JPEG/itu-t81.pdf). The implementation of the IDCT and
 * FDCT algorithms are based on the jfdctflt.c and jidctflt.c implementations
 * written by Thomas G. Lane.
 */
public class DCT {

    private static final int BLOCK_SIZE = 8;

    private static final double R2 = Math.sqrt(2);

    private static final float _R2 = (float) R2;

    // these values are used in the IDCT
    private static final double[] scaleFactor = {1.0, // 1.0
            Math.cos(1 * Math.PI / 16) * R2, // 1.3870398453221475
            Math.cos(2 * Math.PI / 16) * R2, // 1.3065629648763766
            Math.cos(3 * Math.PI / 16) * R2, // 1.1758756024193588
            Math.cos(4 * Math.PI / 16) * R2, // 1.0
            Math.cos(5 * Math.PI / 16) * R2, // 0.7856949583871023
            Math.cos(6 * Math.PI / 16) * R2, // 0.5411961001461971
            Math.cos(7 * Math.PI / 16) * R2 }; // 0.2758993792829431

    private static final float[] _scaleFactor = { (float) scaleFactor[0],
            (float) scaleFactor[1], (float) scaleFactor[2],
            (float) scaleFactor[3], (float) scaleFactor[4],
            (float) scaleFactor[5], (float) scaleFactor[6],
            (float) scaleFactor[7] };

    private static final double K2 = 2 * Math.cos(Math.PI / 8); // 1.8477590650225735

    private static final double K6 = 2 * Math.sin(Math.PI / 8); // 0.7653668647301796

    private static final double M26 = K2 - K6; // 1.0823922002923938

    private static final double P26 = -(K2 + K6); // -2.613125929752753

    private static final float _K2 = (float) K2;

    private static final float _M26 = (float) M26;

    private static final float _P26 = (float) P26;

    // these values are used in the FDCT
    private static final double F0 = 1.0 / R2; // 0.7071067811865475

    private static final double F1 = Math.cos(1 * Math.PI / 16) / 2; // 0.4903926402016152

    private static final double F2 = Math.cos(2 * Math.PI / 16) / 2; // 0.46193976625564337

    private static final double F3 = Math.cos(3 * Math.PI / 16) / 2; // 0.4157348061512726

    private static final double F4 = Math.cos(4 * Math.PI / 16) / 2; // 0.3535533905932738

    private static final double F5 = Math.cos(5 * Math.PI / 16) / 2; // 0.27778511650980114

    private static final double F6 = Math.cos(6 * Math.PI / 16) / 2; // 0.19134171618254492

    private static final double F7 = Math.cos(7 * Math.PI / 16) / 2; // 0.09754516100806417

    private static final double D71 = F7 - F1; // -0.39284747919355106

    private static final double D35 = F3 - F5; // 0.13794968964147147

    private static final double D62 = F6 - F2; // -0.27059805007309845

    private static final double S71 = F7 + F1; // 0.5879378012096794

    private static final double S35 = F3 + F5; // 0.6935199226610738

    private static final double S62 = F6 + F2; // 0.6532814824381883

    private static final float _F0 = (float) F0;

    private static final float _F3 = (float) F3;

    private static final float _F4 = (float) F4;

    private static final float _F6 = (float) F6;

    private static final float _F7 = (float) F7;

    private static final float _D71 = (float) D71;

    private static final float _D35 = (float) D35;

    private static final float _D62 = (float) D62;

    private static final float _S71 = (float) S71;

    private static final float _S35 = (float) S35;

    private static final float _S62 = (float) S62;

    private static final float B0 = 0.35355339059327376220f;

    private static final float B1 = 0.25489778955207958447f;

    private static final float B2 = 0.27059805007309849220f;

    private static final float B3 = 0.30067244346752264027f;

    private static final float B4 = 0.35355339059327376220f;

    private static final float B5 = 0.44998811156820785231f;

    private static final float B6 = 0.65328148243818826392f;

    private static final float B7 = 1.28145772387075308943f;

    private static final int FP_SCALE(float v) {
        return (int) ((double) v * (double) (1 << 15) + 0.5);
    }

    private int[] CROSS_STAGE = new int[] { FP_SCALE(B0 * B0),
            FP_SCALE(B0 * B1), FP_SCALE(B0 * B2), FP_SCALE(B0 * B3),
            FP_SCALE(B0 * B4), FP_SCALE(B0 * B5), FP_SCALE(B0 * B6),
            FP_SCALE(B0 * B7),

            FP_SCALE(B1 * B0), FP_SCALE(B1 * B1), FP_SCALE(B1 * B2),
            FP_SCALE(B1 * B3), FP_SCALE(B1 * B4), FP_SCALE(B1 * B5),
            FP_SCALE(B1 * B6), FP_SCALE(B1 * B7),

            FP_SCALE(B2 * B0), FP_SCALE(B2 * B1), FP_SCALE(B2 * B2),
            FP_SCALE(B2 * B3), FP_SCALE(B2 * B4), FP_SCALE(B2 * B5),
            FP_SCALE(B2 * B6), FP_SCALE(B2 * B7),

            FP_SCALE(B3 * B0), FP_SCALE(B3 * B1), FP_SCALE(B3 * B2),
            FP_SCALE(B3 * B3), FP_SCALE(B3 * B4), FP_SCALE(B3 * B5),
            FP_SCALE(B3 * B6), FP_SCALE(B3 * B7),

            FP_SCALE(B4 * B0), FP_SCALE(B4 * B1), FP_SCALE(B4 * B2),
            FP_SCALE(B4 * B3), FP_SCALE(B4 * B4), FP_SCALE(B4 * B5),
            FP_SCALE(B4 * B6), FP_SCALE(B4 * B7),

            FP_SCALE(B5 * B0), FP_SCALE(B5 * B1), FP_SCALE(B5 * B2),
            FP_SCALE(B5 * B3), FP_SCALE(B5 * B4), FP_SCALE(B5 * B5),
            FP_SCALE(B5 * B6), FP_SCALE(B5 * B7),

            FP_SCALE(B6 * B0), FP_SCALE(B6 * B1), FP_SCALE(B6 * B2),
            FP_SCALE(B6 * B3), FP_SCALE(B6 * B4), FP_SCALE(B6 * B5),
            FP_SCALE(B6 * B6), FP_SCALE(B6 * B7),

            FP_SCALE(B7 * B0), FP_SCALE(B7 * B1), FP_SCALE(B7 * B2),
            FP_SCALE(B7 * B3), FP_SCALE(B7 * B4), FP_SCALE(B7 * B5),
            FP_SCALE(B7 * B6), FP_SCALE(B7 * B7), };

    private static final int A1 = FP_SCALE(0.7071068f);

    private static final int A2 = FP_SCALE(0.5411961f);

    private static final int A3 = A1;

    private static final int A4 = FP_SCALE(1.3065630f);

    private static final int A5 = FP_SCALE(0.3826834f);

    private static final int FP_MUL(int a, int b) {
        return ((((a) >> 5) * ((b) >> 5)) >> 5);
    }

    private Unsafe unsafe = null;

    private long objectOffset = 0;

    private long inObjectOffset = 0;

    private long byteArrayOffset = 0;

    private long intArrayOffset = 0;

    private long intSize = 0;

    private long shortSize = 0;

    private Object object = null;

    private Object inObject = null;

    private long tmp = 0;

    private long cross_stage = 0;

    /**
     * Creates a new DCT object
     *
     */
    public DCT() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            objectOffset = unsafe.objectFieldOffset(DCT.class
                    .getDeclaredField("object"));
            inObjectOffset = unsafe.objectFieldOffset(DCT.class
                    .getDeclaredField("inObject"));
            byteArrayOffset = unsafe.arrayBaseOffset(byte[].class);
            intArrayOffset = unsafe.arrayBaseOffset(int[].class);
            intSize = unsafe.arrayIndexScale(int[].class);
            shortSize = unsafe.arrayIndexScale(short[].class);
            cross_stage = unsafe.allocateMemory(CROSS_STAGE.length * intSize);
            for (int i = 0; i < CROSS_STAGE.length; i++) {
                unsafe.putInt(cross_stage + (i * intSize), CROSS_STAGE[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tmp = unsafe.allocateMemory(64 * intSize);
    }

    public void FDCT(byte[] in, int[] out, int startoff, int xoff, int yoff,
            int stride) {
        object = out;
        long outOffset = unsafe.getLong(this, objectOffset) + intArrayOffset;
        FDCT(in, outOffset, startoff, xoff, yoff, stride);
    }

    /**
     * This method performs the forward discrete cosine transform (FDCT). The in
     * array is linearized.
     */
    public void FDCT(byte[] in, long out, int startoff, int xoff, int yoff,
            int stride) {
        float temp;
        float a0, a1, a2, a3, a4, a5, a6, a7;
        float b0, b1, b2, b3, b4, b5, b6, b7;

        inObject = in;
        long pin = unsafe.getLong(this, inObjectOffset) + byteArrayOffset
                + (startoff + (yoff * stride) + xoff);
        long pout = out;

        // Horizontal transform
        for (int i = 0; i < BLOCK_SIZE; i++) {
            b0 = (unsafe.getByte(pin + 0) & 0xFF)
                    + (unsafe.getByte(pin + 7) & 0xFF);
            b7 = (unsafe.getByte(pin + 0) & 0xFF)
                    - (unsafe.getByte(pin + 7) & 0xFF);
            b1 = (unsafe.getByte(pin + 1) & 0xFF)
                    + (unsafe.getByte(pin + 6) & 0xFF);
            b6 = (unsafe.getByte(pin + 1) & 0xFF)
                    - (unsafe.getByte(pin + 6) & 0xFF);
            b2 = (unsafe.getByte(pin + 2) & 0xFF)
                    + (unsafe.getByte(pin + 5) & 0xFF);
            b5 = (unsafe.getByte(pin + 2) & 0xFF)
                    - (unsafe.getByte(pin + 5) & 0xFF);
            b3 = (unsafe.getByte(pin + 3) & 0xFF)
                    + (unsafe.getByte(pin + 4) & 0xFF);
            b4 = (unsafe.getByte(pin + 3) & 0xFF)
                    - (unsafe.getByte(pin + 4) & 0xFF);

            a0 = b0 + b3;
            a1 = b1 + b2;
            a2 = b1 - b2;
            a3 = b0 - b3;
            a4 = b4;
            a5 = (b6 - b5) * _F0;
            a6 = (b6 + b5) * _F0;
            a7 = b7;
            unsafe.putInt(pout + (0 * intSize), (int) ((a0 + a1) * _F4));
            unsafe.putInt(pout + (4 * intSize), (int) ((a0 - a1) * _F4));

            temp = (a3 + a2) * _F6;
            unsafe.putInt(pout + (2 * intSize), (int) (temp - a3 * _D62));
            unsafe.putInt(pout + (6 * intSize), (int) (temp - a2 * _S62));

            b4 = a4 + a5;
            b7 = a7 + a6;
            b5 = a4 - a5;
            b6 = a7 - a6;

            temp = (b7 + b4) * _F7;
            unsafe.putInt(pout + (1 * intSize), (int) (temp - b7 * _D71));
            unsafe.putInt(pout + (7 * intSize), (int) (temp - b4 * _S71));

            temp = (b6 + b5) * _F3;
            unsafe.putInt(pout + (5 * intSize), (int) (temp - b6 * _D35));
            unsafe.putInt(pout + (3 * intSize), (int) (temp - b5 * _S35));

            pin += stride;
            pout += (BLOCK_SIZE * intSize);
        }

        // Vertical transform
        pout = out;
        for (int i = 0; i < BLOCK_SIZE; i++) {
            b0 = unsafe.getInt(pout + (0 * BLOCK_SIZE * intSize))
                    + unsafe.getInt(pout + (7 * BLOCK_SIZE * intSize));
            b7 = unsafe.getInt(pout + (0 * BLOCK_SIZE * intSize))
                    - unsafe.getInt(pout + (7 * BLOCK_SIZE * intSize));
            b1 = unsafe.getInt(pout + (1 * BLOCK_SIZE * intSize))
                    + unsafe.getInt(pout + (6 * BLOCK_SIZE * intSize));
            b6 = unsafe.getInt(pout + (1 * BLOCK_SIZE * intSize))
                    - unsafe.getInt(pout + (6 * BLOCK_SIZE * intSize));
            b2 = unsafe.getInt(pout + (2 * BLOCK_SIZE * intSize))
                    + unsafe.getInt(pout + (5 * BLOCK_SIZE * intSize));
            b5 = unsafe.getInt(pout + (2 * BLOCK_SIZE * intSize))
                    - unsafe.getInt(pout + (5 * BLOCK_SIZE * intSize));
            b3 = unsafe.getInt(pout + (3 * BLOCK_SIZE * intSize))
                    + unsafe.getInt(pout + (4 * BLOCK_SIZE * intSize));
            b4 = unsafe.getInt(pout + (3 * BLOCK_SIZE * intSize))
                    - unsafe.getInt(pout + (4 * BLOCK_SIZE * intSize));

            a0 = b0 + b3;
            a1 = b1 + b2;
            a2 = b1 - b2;
            a3 = b0 - b3;
            a4 = b4;
            a5 = (b6 - b5) * _F0;
            a6 = (b6 + b5) * _F0;
            a7 = b7;
            unsafe.putInt(pout + (0 * BLOCK_SIZE * intSize),
                    (int) ((a0 + a1) * _F4));
            unsafe.putInt(pout + (4 * BLOCK_SIZE * intSize),
                    (int) ((a0 - a1) * _F4));

            temp = (a3 + a2) * _F6;
            unsafe.putInt(pout + (2 * BLOCK_SIZE * intSize), (int) (temp - a3
                    * _D62));
            unsafe.putInt(pout + (6 * BLOCK_SIZE * intSize), (int) (temp - a2
                    * _S62));

            b4 = a4 + a5;
            b7 = a7 + a6;
            b5 = a4 - a5;
            b6 = a7 - a6;

            temp = (b7 + b4) * _F7;
            unsafe.putInt(pout + (1 * BLOCK_SIZE * intSize), (int) (temp - b7
                    * _D71));
            unsafe.putInt(pout + (7 * BLOCK_SIZE * intSize), (int) (temp - b4
                    * _S71));

            temp = (b6 + b5) * _F3;
            unsafe.putInt(pout + (5 * BLOCK_SIZE * intSize), (int) (temp - b6
                    * _D35));
            unsafe.putInt(pout + (3 * BLOCK_SIZE * intSize), (int) (temp - b5
                    * _S35));
            pout += intSize;
        }
    }

    /**
     * Inverse DCT
     */
    public void IDCT(long in, long out, int stride) {
        float tmp0, tmp1, tmp2, tmp3, tmp4, tmp5, tmp6;
        float tmp7, tmp10, tmp11, tmp12, tmp13;
        float z5, z10, z11, z12, z13;
        float[][] tmpout = new float[BLOCK_SIZE][BLOCK_SIZE];

        for (int i = 0; i < BLOCK_SIZE; i++) {
            if (unsafe.getShort(in + (((1 * BLOCK_SIZE) + i) * shortSize)) == 0
                    && unsafe.getShort(in
                            + (((2 * BLOCK_SIZE) + i) * shortSize)) == 0
                    && unsafe.getShort(in
                            + (((3 * BLOCK_SIZE) + i) * shortSize)) == 0
                    && unsafe.getShort(in
                            + (((4 * BLOCK_SIZE) + i) * shortSize)) == 0
                    && unsafe.getShort(in
                            + (((5 * BLOCK_SIZE) + i) * shortSize)) == 0
                    && unsafe.getShort(in
                            + (((6 * BLOCK_SIZE) + i) * shortSize)) == 0
                    && unsafe.getShort(in
                            + (((7 * BLOCK_SIZE) + i) * shortSize)) == 0) {
                float dc = unsafe.getShort(in
                        + (((0 * BLOCK_SIZE) + i) * shortSize));
                System.err.println("dc = " + dc);
                tmpout[0][i] = dc;
                tmpout[1][i] = dc;
                tmpout[2][i] = dc;
                tmpout[3][i] = dc;
                tmpout[4][i] = dc;
                tmpout[5][i] = dc;
                tmpout[6][i] = dc;
                tmpout[7][i] = dc;
                continue;
            }

            tmp0 = unsafe.getShort(in + (((0 * BLOCK_SIZE) + i) * shortSize));
            tmp1 = unsafe.getShort(in + (((2 * BLOCK_SIZE) + i) * shortSize));
            tmp2 = unsafe.getShort(in + (((4 * BLOCK_SIZE) + i) * shortSize));
            tmp3 = unsafe.getShort(in + (((6 * BLOCK_SIZE) + i) * shortSize));

            tmp10 = tmp0 + tmp2;
            tmp11 = tmp0 - tmp2;

            tmp13 = tmp1 + tmp3;
            tmp12 = (tmp1 - tmp3) * _R2 - tmp13;

            tmp0 = tmp10 + tmp13;
            tmp3 = tmp10 - tmp13;
            tmp1 = tmp11 + tmp12;
            tmp2 = tmp11 - tmp12;

            tmp4 = unsafe.getShort(in + (((1 * BLOCK_SIZE) + i) * shortSize));
            tmp5 = unsafe.getShort(in + (((3 * BLOCK_SIZE) + i) * shortSize));
            tmp6 = unsafe.getShort(in + (((5 * BLOCK_SIZE) + i) * shortSize));
            tmp7 = unsafe.getShort(in + (((7 * BLOCK_SIZE) + i) * shortSize));

            z13 = tmp6 + tmp5;
            z10 = tmp6 - tmp5;
            z11 = tmp4 + tmp7;
            z12 = tmp4 - tmp7;

            tmp7 = z11 + z13;
            tmp11 = (z11 - z13) * _R2;

            z5 = (z10 + z12) * _K2;
            tmp10 = _M26 * z12 - z5;
            tmp12 = _P26 * z10 + z5;

            tmp6 = tmp12 - tmp7;
            tmp5 = tmp11 - tmp6;
            tmp4 = tmp10 + tmp5;

            tmpout[0][i] = (tmp0 + tmp7);
            tmpout[7][i] = (tmp0 - tmp7);
            tmpout[1][i] = (tmp1 + tmp6);
            tmpout[6][i] = (tmp1 - tmp6);
            tmpout[2][i] = (tmp2 + tmp5);
            tmpout[5][i] = (tmp2 - tmp5);
            tmpout[4][i] = (tmp3 + tmp4);
            tmpout[3][i] = (tmp3 - tmp4);
        }

        for (int i = 0; i < BLOCK_SIZE; i++) {
            tmp10 = tmpout[i][0] + tmpout[i][4];
            tmp11 = tmpout[i][0] - tmpout[i][4];

            tmp13 = tmpout[i][2] + tmpout[i][6];
            tmp12 = (tmpout[i][2] - tmpout[i][6]) * _R2 - tmp13;

            tmp0 = tmp10 + tmp13;
            tmp3 = tmp10 - tmp13;
            tmp1 = tmp11 + tmp12;
            tmp2 = tmp11 - tmp12;

            z13 = tmpout[i][5] + tmpout[i][3];
            z10 = tmpout[i][5] - tmpout[i][3];
            z11 = tmpout[i][1] + tmpout[i][7];
            z12 = tmpout[i][1] - tmpout[i][7];

            tmp7 = z11 + z13;
            tmp11 = (z11 - z13) * _R2;

            z5 = (z10 + z12) * _K2;
            tmp10 = _M26 * z12 - z5;
            tmp12 = _P26 * z10 + z5;

            tmp6 = tmp12 - tmp7;
            tmp5 = tmp11 - tmp6;
            tmp4 = tmp10 + tmp5;

            int ypos = i * stride;
            unsafe.putByte(out + 0 + ypos, (byte) ((int) (tmp0 + tmp7) & 0xFF));
            unsafe.putByte(out + 7 + ypos, (byte) ((int) (tmp0 - tmp7) & 0xFF));
            unsafe.putByte(out + 1 + ypos, (byte) ((int) (tmp1 + tmp6) & 0xFF));
            unsafe.putByte(out + 6 + ypos, (byte) ((int) (tmp1 - tmp6) & 0xFF));
            unsafe.putByte(out + 2 + ypos, (byte) ((int) (tmp2 + tmp5) & 0xFF));
            unsafe.putByte(out + 5 + ypos, (byte) ((int) (tmp2 - tmp5) & 0xFF));
            unsafe.putByte(out + 4 + ypos, (byte) ((int) (tmp3 + tmp4) & 0xFF));
            unsafe.putByte(out + 3 + ypos, (byte) ((int) (tmp3 - tmp4) & 0xFF));
        }
    }

    private static final int FP_NORM(int v) {
        return (((v) + (1 << (15 - 1))) >> 15);
    }

    private static final float FP_FLOAT(int v) {
        return (float) v / (float) ((1 << 15) - 1);
    }

    private static final int LIMIT(int x) {
        int t = x;
        t &= ~(t >> 31);
        return (t | ~((t - 256) >> 31)) & 0xFF;
    }

    public void rdct(long bp, long m0, byte[] out, long offset, int stride) {

        inObject = out;
        unsafe.setMemory(tmp, 64 * intSize, (byte) 0);
        long tp = tmp;
        long qt = cross_stage;
        /*
         * First pass is 1D transform over the rows of the input array.
         */
        int i;
        for (i = 8; --i >= 0;) {
            if ((m0 & 0xfe) == 0) {
                /*
                 * All ac terms are zero.
                 */
                int v = 0;
                if (((m0 >> 0) & 0x1) > 0) {
                    v = unsafe.getInt(qt) * unsafe.getShort(bp);
                }
                unsafe.putInt(tp + (0 * intSize), v);
                unsafe.putInt(tp + (1 * intSize), v);
                unsafe.putInt(tp + (2 * intSize), v);
                unsafe.putInt(tp + (3 * intSize), v);
                unsafe.putInt(tp + (4 * intSize), v);
                unsafe.putInt(tp + (5 * intSize), v);
                unsafe.putInt(tp + (6 * intSize), v);
                unsafe.putInt(tp + (7 * intSize), v);
            } else {
                int t4 = 0, t5 = 0, t6 = 0, t7 = 0;
                if ((m0 & 0xaa) != 0) {
                    /* odd part */
                    if (((m0 >> 1) & 0x1) > 0)
                        t4 = unsafe.getInt(qt + (1 * intSize))
                                * unsafe.getShort(bp + (1 * shortSize));
                    if (((m0 >> 3) & 0x1) > 0)
                        t5 = unsafe.getInt(qt + (3 * intSize))
                                * unsafe.getShort(bp + (3 * shortSize));
                    if (((m0 >> 5) & 0x1) > 0)
                        t6 = unsafe.getInt(qt + (5 * intSize))
                                * unsafe.getShort(bp + (5 * shortSize));
                    if (((m0 >> 7) & 0x1) > 0)
                        t7 = unsafe.getInt(qt + (7 * intSize))
                                * unsafe.getShort(bp + (7 * shortSize));

                    int x0 = t6 - t5;
                    t6 += t5;
                    int x1 = t4 - t7;
                    t7 += t4;

                    t5 = FP_MUL(t7 - t6, A3);
                    t7 += t6;

                    t4 = FP_MUL(x1 + x0, A5);
                    t6 = FP_MUL(x1, A4) - t4;
                    t4 += FP_MUL(x0, A2);

                    t7 += t6;
                    t6 += t5;
                    t5 += t4;
                }
                int t0 = 0, t1 = 0, t2 = 0, t3 = 0;
                if ((m0 & 0x55) != 0) {
                    /* even part */
                    if (((m0 >> 0) & 0x1) > 0)
                        t0 = unsafe.getInt(qt + (0 * intSize))
                                * unsafe.getShort(bp + (0 * shortSize));
                    if (((m0 >> 2) & 0x1) > 0)
                        t1 = unsafe.getInt(qt + (2 * intSize))
                                * unsafe.getShort(bp + (2 * shortSize));
                    if (((m0 >> 4) & 0x1) > 0)
                        t2 = unsafe.getInt(qt + (4 * intSize))
                                * unsafe.getShort(bp + (4 * shortSize));
                    if (((m0 >> 6) & 0x1) > 0)
                        t3 = unsafe.getInt(qt + (6 * intSize))
                                * unsafe.getShort(bp + (6 * shortSize));

                    int x0 = FP_MUL(t1 - t3, A1);
                    t3 += t1;
                    t1 = t0 - t2;
                    t0 += t2;
                    t2 = t3 + x0;
                    t3 = t0 - t2;
                    t0 += t2;
                    t2 = t1 - x0;
                    t1 += x0;
                }
                unsafe.putInt(tp + (0 * intSize), t0 + t7);
                unsafe.putInt(tp + (1 * intSize), t1 + t6);
                unsafe.putInt(tp + (2 * intSize), t2 + t5);
                unsafe.putInt(tp + (3 * intSize), t3 + t4);
                unsafe.putInt(tp + (4 * intSize), t3 - t4);
                unsafe.putInt(tp + (5 * intSize), t2 - t5);
                unsafe.putInt(tp + (6 * intSize), t1 - t6);
                unsafe.putInt(tp + (7 * intSize), t0 - t7);
            }
            qt += (8 * intSize);
            tp += (8 * intSize);
            bp += (8 * shortSize);
            m0 >>= 8;
        }
        tp -= (64 * intSize);
        /*
         * Second pass is 1D transform over the rows of the temp array.
         */
        for (i = 0; i < 8; i++) {
            int t4 = unsafe.getInt(tp + (8 * 1 * intSize));
            int t5 = unsafe.getInt(tp + (8 * 3 * intSize));
            int t6 = unsafe.getInt(tp + (8 * 5 * intSize));
            int t7 = unsafe.getInt(tp + (8 * 7 * intSize));
            if ((t4 | t5 | t6 | t7) != 0) {
                /* odd part */
                int x0 = t6 - t5;
                t6 += t5;
                int x1 = t4 - t7;
                t7 += t4;

                t5 = FP_MUL(t7 - t6, A3);
                t7 += t6;

                t4 = FP_MUL(x1 + x0, A5);
                t6 = FP_MUL(x1, A4) - t4;
                t4 += FP_MUL(x0, A2);

                t7 += t6;
                t6 += t5;
                t5 += t4;
            }
            int t0 = unsafe.getInt(tp + (8 * 0 * intSize));
            int t1 = unsafe.getInt(tp + (8 * 2 * intSize));
            int t2 = unsafe.getInt(tp + (8 * 4 * intSize));
            int t3 = unsafe.getInt(tp + (8 * 6 * intSize));
            if ((t0 | t1 | t2 | t3) != 0) {
                /* even part */
                int x0 = FP_MUL(t1 - t3, A1);
                t3 += t1;
                t1 = t0 - t2;
                t0 += t2;
                t2 = t3 + x0;
                t3 = t0 - t2;
                t0 += t2;
                t2 = t1 - x0;
                t1 += x0;
            }

            long p = unsafe.getLong(this, inObjectOffset) + byteArrayOffset
                    + offset + (i * stride);
            unsafe.putByte(p + 0, (byte) (LIMIT(FP_NORM(t0 + t7))));
            unsafe.putByte(p + 1, (byte) (LIMIT(FP_NORM(t1 + t6))));
            unsafe.putByte(p + 2, (byte) (LIMIT(FP_NORM(t2 + t5))));
            unsafe.putByte(p + 3, (byte) (LIMIT(FP_NORM(t3 + t4))));
            unsafe.putByte(p + 4, (byte) (LIMIT(FP_NORM(t3 - t4))));
            unsafe.putByte(p + 5, (byte) (LIMIT(FP_NORM(t2 - t5))));
            unsafe.putByte(p + 6, (byte) (LIMIT(FP_NORM(t1 - t6))));
            unsafe.putByte(p + 7, (byte) (LIMIT(FP_NORM(t0 - t7))));

            tp += intSize;
        }
    }

    /**
     * This method applies the pre-scaling that the IDCT(float[][], float[][],
     * float[][]) method needs to work correctly. The table parameter should be
     * 8x8, non-zigzag order.
     */
    public void scaleQuantizationTable(float[][] table) {
        for (int i = 0; i < BLOCK_SIZE; i++)
            for (int j = 0; j < BLOCK_SIZE; j++)
                table[i][j] = table[i][j] * _scaleFactor[i] * _scaleFactor[j]
                        / 8;
    }

    private void test() {
        byte[] testArray = new byte[64];
        for (int i = 0; i < 64; i++) {
            testArray[i] = (byte) ((int) (Math.random() * 255) & 0xFF);
        }
        long out = unsafe.allocateMemory(64 * intSize);
        FDCT(testArray, out, 0, 0, 0, 8);

        long block = unsafe.allocateMemory(64 * shortSize);
        for (int i = 0; i < 64; i++) {
            unsafe.putShort(block + (i * shortSize), (short) unsafe.getInt(out
                    + (i * intSize)));
        }
        DecimalFormat format = new DecimalFormat(" 0000;-0000");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.err.print(format.format(unsafe.getInt(out
                        + (((i * 8) + j) * intSize)))
                        + " ");
            }
            System.err.print("    ");
            for (int j = 0; j < 8; j++) {
                System.err.print(format.format(unsafe.getShort(block
                        + (((i * 8) + j) * shortSize)))
                        + " ");
            }
            System.err.println();
        }
        System.err.println();

        byte[] testOutArray = new byte[64];
        rdct(block, 0xFFFFFFFFFFFFFFFFl, testOutArray, 0, 8);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.err.print(format.format(testArray[i * 8 + j]) + " ");
            }
            System.err.print("    ");
            for (int j = 0; j < 8; j++) {
                System.err.print(format.format(testOutArray[i * 8 + j]) + " ");
            }
            System.err.println();
        }
    }

    /**
     * Frees any resources used
     */
    public void close() {
        unsafe.freeMemory(cross_stage);
        unsafe.freeMemory(tmp);
    }

    public static void main(String[] args) {
        DCT dct = new DCT();
        dct.test();
    }

}
