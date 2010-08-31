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

package net.crew_vre.multiplexer.flv;

import java.util.Vector;

import net.crew_vre.codec.utils.BitInputStream;

/**
 * A parser for the Sorenson H263 Header
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class SorensonH263Header {

    // The number of bits in the extra information
    private static final int EXTRA_INFO_BITS = 8;

    // The marker if there is extra information
    private static final int IS_EXTRA_INFO = 1;

    // The number of bits for the quantizer
    private static final int QUANTIZER_BITS = 5;

    // The number of bits in a flag
    private static final int FLAG_BITS = 1;

    // The number of bits in the picture type
    private static final int PICTURE_TYPE_BITS = 2;

    // The height of an NTSC QCIF image
    private static final int NTSC_QCIF_HEIGHT = 120;

    // The width of an NTSC QCIF image
    private static final int NTSC_QCIF_WIDTH = 160;

    // The height of an NTSC CIF image
    private static final int NTSC_CIF_HEIGHT = 240;

    // The width of and NTSC CIF image
    private static final int NTSC_CIF_WIDTH = 320;

    // The height of an SQCIF image
    private static final int SQCIF_HEIGHT = 96;

    // The width of an SQCIF image
    private static final int SQCIF_WIDTH = 128;

    // The height of a QCIF image
    private static final int QCIF_HEIGHT = 144;

    // The width of a QCIF image
    private static final int QCIF_WIDTH = 176;

    // The height of a CIF image
    private static final int CIF_HEIGHT = 288;

    // The width of a CIF image
    private static final int CIF_WIDTH = 352;

    // The indicator of a QCIF NTSC image
    private static final int PICTURE_SIZE_NTSC_QCIF = 6;

    // The indicator of a CIF NTSC image
    private static final int PICTURE_SIZE_NTSC_CIF = 5;

    // The indicator of a SQCIF image
    private static final int PICTURE_SIZE_SQCIF = 4;

    // The indicator of a QCIF image
    private static final int PICTURE_SIZE_QCIF = 3;

    // The indicator of a CIF image
    private static final int PICTURE_SIZE_CIF = 2;

    // The indicator of a 16-bit custom size image
    private static final int PICTURE_SIZE_CUSTOM_1 = 1;

    // The indicator of a 8-bit custom size image
    private static final int PICTURE_SIZE_CUSTOM_0 = 0;

    // The number of bits for a custom 1 image
    private static final int CUSTOM_SIZE_1_BITS = 16;

    // The number of bits for a custom 0 image
    private static final int CUSTOM_SIZE_0_BITS = 8;

    // The number of bits for the picture size indicator
    private static final int PICTURE_SIZE_BITS = 3;

    // The number of bits for the temporal reference
    private static final int TEMPORAL_REFERENCE_BITS = 8;

    // The number of bits for the version
    private static final int VERSION_BITS = 5;

    // The number of bits for the start code
    private static final int START_CODE_BITS = 17;

    // The version
    private byte version = 0;

    // The temporal reference
    private short temporalReference = 0;

    // The picture size indicator
    private byte pictureSize = 0;

    // The width of the image
    private int customWidth = 0;

    // The height of the image
    private int customHeight = 0;

    // The type of the picture (intra, inter, disposable)
    private byte pictureType = 0;

    // The deblocking flag
    private byte deblockingFlag = 0;

    // The quantizer
    private byte quantizer = 0;

    // Any extra information
    private Vector<Byte> extraInformation = new Vector<Byte>();

    /**
     * Reads the header
     *
     * @param data The data packet
     * @param offset The offset into the packet
     * @param length The length of the packet
     */
    public SorensonH263Header(byte[] data, int offset, int length) {
        BitInputStream in = new BitInputStream(data, offset, length);
        in.readBits(START_CODE_BITS);
        version = (byte) in.readBits(VERSION_BITS);
        temporalReference = (short) in.readBits(TEMPORAL_REFERENCE_BITS);
        pictureSize = (byte) in.readBits(PICTURE_SIZE_BITS);
        switch (pictureSize) {
        case PICTURE_SIZE_CUSTOM_0:
            customWidth = in.readBits(CUSTOM_SIZE_0_BITS);
            customHeight = in.readBits(CUSTOM_SIZE_0_BITS);
            break;

        case PICTURE_SIZE_CUSTOM_1:
            customWidth = in.readBits(CUSTOM_SIZE_1_BITS);
            customHeight = in.readBits(CUSTOM_SIZE_1_BITS);
            break;

        case PICTURE_SIZE_CIF:
            customWidth = CIF_WIDTH;
            customHeight = CIF_HEIGHT;
            break;

        case PICTURE_SIZE_QCIF:
            customWidth = QCIF_WIDTH;
            customHeight = QCIF_HEIGHT;
            break;

        case PICTURE_SIZE_SQCIF:
            customWidth = SQCIF_WIDTH;
            customHeight = SQCIF_HEIGHT;
            break;

        case PICTURE_SIZE_NTSC_CIF:
            customWidth = NTSC_CIF_WIDTH;
            customHeight = NTSC_CIF_HEIGHT;
            break;

        case PICTURE_SIZE_NTSC_QCIF:
            customWidth = NTSC_QCIF_WIDTH;
            customHeight = NTSC_QCIF_HEIGHT;
            break;

        default:
            break;
        }

        pictureType = (byte) in.readBits(PICTURE_TYPE_BITS);
        deblockingFlag = (byte) in.readBits(FLAG_BITS);
        quantizer = (byte) in.readBits(QUANTIZER_BITS);
        while (in.readBits(FLAG_BITS) == IS_EXTRA_INFO) {
            extraInformation.add(new Byte((byte) in.readBits(EXTRA_INFO_BITS)));
        }
    }

    /**
     * Returns the height
     * @return the height
     */
    public int getHeight() {
        return customHeight;
    }

    /**
     * Returns the width
     * @return the width
     */
    public int getWidth() {
        return customWidth;
    }

    /**
     * Returns the deblockingFlag
     * @return the deblockingFlag
     */
    public byte getDeblockingFlag() {
        return deblockingFlag;
    }

    /**
     * Returns the extraInformation
     * @return the extraInformation
     */
    public byte[] getExtraInformation() {
        byte[] extraInfo = new byte[extraInformation.size()];
        for (int i = 0; i < extraInformation.size(); i++) {
            Byte info = (Byte) extraInformation.get(i);
            extraInfo[i] = info.byteValue();
        }
        return extraInfo;
    }

    /**
     * Returns the pictureType
     * @return the pictureType
     */
    public byte getPictureType() {
        return pictureType;
    }

    /**
     * Returns the quantizer
     * @return the quantizer
     */
    public byte getQuantizer() {
        return quantizer;
    }

    /**
     * Returns the temporalReference
     * @return the temporalReference
     */
    public short getTemporalReference() {
        return temporalReference;
    }

    /**
     * Returns the version
     * @return the version
     */
    public byte getVersion() {
        return version;
    }


}
