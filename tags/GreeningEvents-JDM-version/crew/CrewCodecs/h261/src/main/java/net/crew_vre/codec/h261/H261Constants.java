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

package net.crew_vre.codec.h261;

/**
 * Constants for H261 Encoding and Decoding
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class H261Constants {

    /**
     * The maximum level in a run-level
     *
     */
    public static final int MAX_LEVEL = 128;

    /**
     * The minimum level in a run-level
     */
    public static final int MIN_LEVEL = -128;

    /**
     * The number of levels
     */
    public static final int NO_LEVELS = MAX_LEVEL - MIN_LEVEL;

    /**
     * The width of the video
     */
    public static final int WIDTH = 352;

    /**
     * The height of the video
     */
    public static final int HEIGHT = 288;

    /**
     * The number of bits in the DC Component of the DCT
     */
    public static final int DC_BITS = 8;

    /**
     * The number of bits in the End of Block code
     */
    public static final int EOB_BITS = 2;

    /**
     * The end of block code value
     */
    public static final int EOB = 2;

    /**
     * The number of bits in a byte
     */
    public static final int BITS_PER_BYTE = 8;

    /**
     * The number of GOBs in each row of a frame
     */
    public static final int GOBS_PER_ROW = 2;


    /**
     * The number of GOBs in each column of a frame
     */
    public static final int GOBS_PER_COL = 6;

    /**
     * The number of GOBS in a frame
     */
    public static final int NO_GOBS = GOBS_PER_ROW * GOBS_PER_COL;

    /**
     * The width of each GOB
     */
    public static final int GOB_WIDTH = WIDTH / GOBS_PER_ROW;

    /**
     * The height of each GOB
     */
    public static final int GOB_HEIGHT = HEIGHT / GOBS_PER_COL;

    /**
     * The number of MBs in each row of a GOB
     */
    public static final int MBS_PER_ROW = 11;


    /**
     * The number of MBs in each column of a GOB
     */
    public static final int MBS_PER_COL = 3;

    /**
     * The number of MBs in each GOB
     */
    public static final int NO_MBS = MBS_PER_ROW * MBS_PER_COL;

    /**
     * The width of each MB
     */
    public static final int MB_WIDTH = GOB_WIDTH / MBS_PER_ROW;

    /**
     * The height of each MB
     */
    public static final int MB_HEIGHT = GOB_HEIGHT / MBS_PER_COL;

    /**
     * The mask for converting a byte to an int
     */
    public static final int BYTE_TO_INT_MASK = 0xFF;

    /**
     * The ratio of Y packets to CR and CB packets
     */
    public static final int Y_TO_CRCB_RATIO = 2;

    /**
     * The width of the CR or CB part of the packet
     */
    public static final int CRCBWIDTH = WIDTH / Y_TO_CRCB_RATIO;

    /**
     * The height of the CR or CB part of the packet
     */
    public static final int CRCBHEIGHT = HEIGHT / Y_TO_CRCB_RATIO;

    /**
     * The offset of the Y values in the array
     */
    public static final int YSTART = 0;

    /**
     * The offset of the Cb values in the array
     */
    public static final int CBSTART = YSTART
        + (WIDTH * HEIGHT);

    /**
     * The offset of the Cr values in the array
     */
    public static final int CRSTART = CBSTART
        + (CRCBWIDTH * CRCBHEIGHT);

    /**
     * The line stride of the Y values
     */
    public static final int YSTRIDE = WIDTH;

    /**
     * The line stride of the Cr and Cb values
     */
    public static final int CRCBSTRIDE = CRCBWIDTH;

    /**
     * The data length of the YUV input
     */
    public static final int DATA_LENGTH = (H261Constants.WIDTH
            * H261Constants.HEIGHT)
            + (H261Constants.CRCBWIDTH
                * H261Constants.CRCBHEIGHT)
            + (H261Constants.CRCBWIDTH
                * H261Constants.CRCBHEIGHT);

    /**
     * The width or height of a block
     */
    public static final int BLOCK_SIZE = 8;

    /**
     * The Coded Block Pattern flag for CR
     */
    public static final int CBP_CR = 0x1;

    /**
     * The Coded Block Pattern flag for CB
     */
    public static final int CBP_CB = 0x2;

    /**
     * The Coded Block Pattern flag for the 4th Y block
     */
    public static final int CBP_Y4 = 0x4;

    /**
     * The Coded Block Pattern flag for the 3rd Y block
     */
    public static final int CBP_Y3 = 0x8;

    /**
     * The Coded Block Pattern flag for the 2nd Y block
     */
    public static final int CBP_Y2 = 0x10;

    /**
     * The Coded Block Pattern flag for the 1st Y block
     */
    public static final int CBP_Y1 = 0x20;

    /**
     * The MTYPE for Intra with TCOEFFs
     */
    public static final int INTRA_TCOEFF = 3;

    /**
     * The MTYPE for Inter with CBP and TCOEFFs
     */
    public static final int INTER_CBP_TCOEFF = 0;

    /**
     * The MTYPE for Inter with filtered MVD, CBP and TCOEFFs
     */
    public static final int INTER_FIL_MVD_CBP_TCOEFF = 1;

    /**
     * The MTYPE for Inter with filtered MVD
     */
    public static final int INTER_FIL_MVD = 2;

    /**
     * The MTYPE for Inter with MVD, CBP and TCOEFF
     */
    public static final int INTER_MVD_CBP_TCOEFF = 7;

    /**
     * The MTYPE for Inter with MVD
     */
    public static final int INTER_MVD = 8;

    /**
     * The MTYPE for Inter with filtered MVD, MQUANT, CBP and TCOEFF
     */
    public static final int INTER_FIL_MQUANT_MVD_CBP_TCOEFF = 5;

    /**
     * The MTYPE for Inter with MQUANT, MVD, CBP and TCOEFF
     */
    public static final int INTER_MQUANT_MVD_CBP_TCOEFF = 9;

    /**
     * The MTYPE for Inter with MQUANT, CBP and TCOEFF
     */
    public static final int INTER_MQUANT_CBP_TCOEFF = 4;

    /**
     * The MTYPE for Intra with MQUANT and TCOEFF
     */
    public static final int INTRA_MQUANT_TCOEFF = 6;

    /**
     * The number of bits in the INTRA with MQUANT and TCOEFF field
     */
    public static final int MTYPE_INTRA_MQUANT_TCOEFF_BITS = 7;

    /**
     * The number of bits in the INTRA with TCOEFF field
     */
    public static final int MTYPE_INTRA_TCOEFF_BITS = 4;

    /**
     * The number of bits used for the VMVD field
     */
    public static final int VMVD_BITS = 5;

    /**
     * The number of bits used for the HMVD field
     */
    public static final int HMVD_BITS = 5;

    /**
     * The number of bits used for the MBAP field
     */
    public static final int MBAP_BITS = 5;

    /**
     * The number of bits used to indicate the end padding
     */
    public static final int END_PADDING_BITS = 3;

    /**
     * The number of bits used to indicate the start padding
     */
    public static final int START_PADDING_BITS = 3;

    /**
     * The number of bits used to indicate the quantizer
     */
    public static final int QUANT_BITS = 5;

    /**
     * The number of bits in the GOB number
     */
    public static final int GOBN_BITS = 4;

    /**
     * The number of bits in the GOB start code
     */
    public static final int GOB_START_BITS = 16;

    /**
     * The GOB start code
     */
    public static final int GOB_START = 1;

    /**
     * The number of bits in each extension
     */
    public static final int EXTENSION_BITS = 8;

    /**
     * The value to indicate that there is no extension present
     */
    public static final int NO_EXTENSION = 0;

    /**
     * The number of bits in the PTYPE field
     */
    public static final int PTYPE_BITS = 6;

    /**
     * The PTYPE for CIF images
     */
    public static final int PTYPE_CIF = 6;

    /**
     * The number of bits in the TR field
     */
    public static final int TR_BITS = 5;

    /**
     * The quantizer scaling
     */
    public static final int QUANT_SCALE = 2;

    /**
     * The x position vector for zigzag
     */
    public static final int ZZX[] = new int[BLOCK_SIZE * BLOCK_SIZE];

    /**
     * The y position vector for zigzag
     */
    public static final int ZZY[] = new int[BLOCK_SIZE * BLOCK_SIZE];

    /**
     * The maximum DC value
     */
    public static final int DC_MAX = 254;

    // The midpoint of a zig zag array
    private static final int ZIGZAG_MIDPOINT = 35;

    static {

        // Set up the zig zag arrays
        int x = 0;
        int y = 0;
        int pos = 0;
        boolean up = true;
        boolean midpoint = false;
        while (pos < (BLOCK_SIZE * BLOCK_SIZE)) {
            H261Constants.ZZX[pos] = x;
            H261Constants.ZZY[pos] = y;
            if (pos == ZIGZAG_MIDPOINT) {
                midpoint = true;
            }
            if ((y == 0) && up && !midpoint) {
                x++;
                up = false;
            } else if ((x == 0) && !up && !midpoint) {
                y++;
                up = true;
            } else if ((y == (BLOCK_SIZE - 1)) && !up && midpoint) {
                x++;
                up = true;
            } else if ((x == (BLOCK_SIZE - 1)) && up && midpoint) {
                y++;
                up = false;
            } else if (up) {
                x++;
                y--;
            } else {
                y++;
                x--;
            }
            pos++;
        }
    }

    /**
     * MBA huffman table for encoding
     */
    public static final int[] MBAHUFF = new int[] {
        1, 1,
        3, 3,
        2, 3,
        3, 4,
        2, 4,
        3, 5,
        2, 5,
        7, 7,
        6, 7,
        11, 8,
        10, 8,
        9, 8,
        8, 8,
        7, 8,
        6, 8,
        23, 10,
        22, 10,
        21, 10,
        20, 10,
        19, 10,
        18, 10,
        35, 11,
        34, 11,
        33, 11,
        32, 11,
        31, 11,
        30, 11,
        29, 11,
        28, 11,
        27, 11,
        26, 11,
        25, 11,
        24, 11,
    };

    /**
     * Run level table for encoding
     */
    public static final int[] RUNLEVELHUFF = new int[16384 * 2];

    private static final void setCode(int run, int level, int code, int len) {
        int runLevel = (((level + MAX_LEVEL) & 0xFF) << 6) | run;
        RUNLEVELHUFF[(runLevel * 2)] = code;
        RUNLEVELHUFF[(runLevel * 2) + 1] = len;
    }

    static {

        // Set up the run/level huffman table
        setCode(0, 1, 0x6, 3);
        setCode(0, -1, 0x7, 3);
        setCode(0, 2, 0x8, 5);
        setCode(0, -2, 0x9, 5);
        setCode(0, 3, 0xA, 6);
        setCode(0, -3, 0xB, 6);
        setCode(0, 4, 0xC, 8);
        setCode(0, -4, 0xD, 8);
        setCode(0, 5, 0x4C, 9);
        setCode(0, -5, 0x4D, 9);
        setCode(0, 6, 0x42, 9);
        setCode(0, -6, 0x43, 9);
        setCode(0, 7, 0x14, 11);
        setCode(0, -7, 0x15, 11);
        setCode(0, 8, 0x3A, 13);
        setCode(0, -8, 0x3B, 13);
        setCode(0, 9, 0x30, 13);
        setCode(0, -9, 0x31, 13);
        setCode(0, 10, 0x26, 13);
        setCode(0, -10, 0x27, 13);
        setCode(0, 11, 0x20, 13);
        setCode(0, -11, 0x21, 13);
        setCode(0, 12, 0x34, 14);
        setCode(0, -12, 0x35, 14);
        setCode(0, 13, 0x32, 14);
        setCode(0, -13, 0x33, 14);
        setCode(0, 14, 0x30, 14);
        setCode(0, -14, 0x31, 14);
        setCode(0, 15, 0x2E, 14);
        setCode(0, -15, 0x2F, 14);
        setCode(1, 1, 0x6, 4);
        setCode(1, -1, 0x7, 4);
        setCode(1, 2, 0xC, 7);
        setCode(1, -2, 0xD, 7);
        setCode(1, 3, 0x4A, 9);
        setCode(1, -3, 0x4B, 9);
        setCode(1, 4, 0x18, 11);
        setCode(1, -4, 0x19, 11);
        setCode(1, 5, 0x36, 13);
        setCode(1, -5, 0x37, 13);
        setCode(1, 6, 0x2C, 14);
        setCode(1, -6, 0x2D, 14);
        setCode(1, 7, 0x2A, 14);
        setCode(1, -7, 0x2B, 14);
        setCode(2, 1, 0xA, 5);
        setCode(2, -1, 0xB, 5);
        setCode(2, 2, 0x8, 8);
        setCode(2, -2, 0x9, 8);
        setCode(2, 3, 0x16, 11);
        setCode(2, -3, 0x17, 11);
        setCode(2, 4, 0x28, 13);
        setCode(2, -4, 0x29, 13);
        setCode(2, 5, 0x28, 14);
        setCode(2, -5, 0x29, 14);
        setCode(3, 1, 0xE, 6);
        setCode(3, -1, 0xF, 6);
        setCode(3, 2, 0x48, 9);
        setCode(3, -2, 0x49, 9);
        setCode(3, 3, 0x38, 13);
        setCode(3, -3, 0x39, 13);
        setCode(3, 4, 0x26, 14);
        setCode(3, -4, 0x27, 14);
        setCode(4, 1, 0xC, 6);
        setCode(4, -1, 0xD, 6);
        setCode(4, 2, 0x1E, 11);
        setCode(4, -2, 0x1F, 11);
        setCode(4, 3, 0x24, 13);
        setCode(4, -3, 0x25, 13);
        setCode(5, 1, 0xE, 7);
        setCode(5, -1, 0xF, 7);
        setCode(5, 2, 0x12, 11);
        setCode(5, -2, 0x13, 11);
        setCode(5, 3, 0x24, 14);
        setCode(5, -3, 0x25, 14);
        setCode(6, 1, 0xA, 7);
        setCode(6, -1, 0xB, 7);
        setCode(6, 2, 0x3C, 13);
        setCode(6, -2, 0x3D, 13);
        setCode(7, 1, 0x8, 7);
        setCode(7, -1, 0x9, 7);
        setCode(7, 2, 0x2A, 13);
        setCode(7, -2, 0x2B, 13);
        setCode(8, 1, 0xE, 8);
        setCode(8, -1, 0xF, 8);
        setCode(8, 2, 0x22, 13);
        setCode(8, -2, 0x23, 13);
        setCode(9, 1, 0xA, 8);
        setCode(9, -1, 0xB, 8);
        setCode(9, 2, 0x22, 14);
        setCode(9, -2, 0x23, 14);
        setCode(10, 1, 0x4E, 9);
        setCode(10, -1, 0x4F, 9);
        setCode(10, 2, 0x20, 14);
        setCode(10, -2, 0x21, 14);
        setCode(11, 1, 0x46, 9);
        setCode(11, -1, 0x47, 9);
        setCode(12, 1, 0x44, 9);
        setCode(12, -1, 0x45, 9);
        setCode(13, 1, 0x40, 9);
        setCode(13, -1, 0x41, 9);
        setCode(14, 1, 0x1C, 11);
        setCode(14, -1, 0x1D, 11);
        setCode(15, 1, 0x1A, 11);
        setCode(15, -1, 0x1B, 11);
        setCode(16, 1, 0x10, 11);
        setCode(16, -1, 0x11, 11);
        setCode(17, 1, 0x3E, 13);
        setCode(17, -1, 0x3F, 13);
        setCode(18, 1, 0x34, 13);
        setCode(18, -1, 0x35, 13);
        setCode(19, 1, 0x32, 13);
        setCode(19, -1, 0x33, 13);
        setCode(20, 1, 0x2E, 13);
        setCode(20, -1, 0x2F, 13);
        setCode(21, 1, 0x2C, 13);
        setCode(21, -1, 0x2D, 13);
        setCode(22, 1, 0x3E, 14);
        setCode(22, -1, 0x3F, 14);
        setCode(23, 1, 0x3C, 14);
        setCode(23, -1, 0x3D, 14);
        setCode(24, 1, 0x3A, 14);
        setCode(24, -1, 0x3B, 14);
        setCode(25, 1, 0x38, 14);
        setCode(25, -1, 0x39, 14);
        setCode(26, 1, 0x36, 14);
        setCode(26, -1, 0x37, 14);

    }

    /**
     * Column zig zag array
     */
    public static final int[] COLZAG = {
        0, 8, 1, 2, 9, 16, 24, 17,
        10, 3, 4, 11, 18, 25, 32, 40,
        33, 26, 19, 12, 5, 6, 13, 20,
        27, 34, 41, 48, 56, 49, 42, 35,
        28, 21, 14, 7, 15, 22, 29, 36,
        43, 50, 57, 58, 51, 44, 37, 30,
        23, 31, 38, 45, 52, 59, 60, 53,
        46, 39, 47, 54, 61, 62, 55, 63,
        0,  0,  0,  0,  0,  0,  0,  0,
        0,  0,  0,  0,  0,  0,  0,  0
    };

    public static final int[] CPBHUFF = new int[]{
        11,  5,
        9,   5,
        13,  6,
        13,  4,
        23,  7,
        19,  7,
        31,  8,
        12,  4,
        22,  7,
        18,  7,
        30,  8,
        19,  5,
        27,  8,
        23,  8,
        19,  8,
        11,  4,
        21,  7,
        17,  7,
        29,  8,
        17,  5,
        25,  8,
        21,  8,
        17,  8,
        15,  6,
        15,  8,
        13,  8,
        3,   9,
        15,  5,
        11,  8,
        7,   8,
        7,   9,
        10,  4,
        20,  7,
        16,  7,
        28,  8,
        14,  6,
        14,  8,
        12,  8,
        2,   9,
        16,  5,
        24,  8,
        20,  8,
        16,  8,
        14,  5,
        10,  8,
        6,   8,
        6,   9,
        18,  5,
        26,  8,
        22,  8,
        18,  8,
        13,  5,
        9,   8,
        5,   8,
        5,   9,
        12,  5,
        8,   8,
        4,   8,
        4,   9,
        7,   3,
        10,  5,
        8,   5,
        12,  6
    };
}
