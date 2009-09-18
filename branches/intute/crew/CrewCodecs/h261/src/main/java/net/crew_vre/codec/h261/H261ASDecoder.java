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

import java.awt.Component;
import java.awt.Dimension;
import java.lang.reflect.Field;
import java.text.DecimalFormat;

import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Format;
import javax.media.format.VideoFormat;
import javax.media.format.YUVFormat;

import net.crew_vre.codec.controls.FrameFillControl;

import sun.misc.Unsafe;

import net.crew_vre.codec.utils.BitInputStream;
import net.crew_vre.codec.utils.DCT;

/**
 * A decoder for H261AS
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class H261ASDecoder implements Codec, FrameFillControl {

    private static final int SYM_ILLEGAL = -2;

    private static final int EOB = -1;

    private static final int ESCAPE = 0;

    private static final int MBA_STUFF = -2;

    private static final int GOB_HEADER = -1;

    private static final int MT_TCOEFF = 0x01;
    private static final int MT_CBP    = 0x02;
    private static final int MT_MVD    = 0x04;
    private static final int MT_MQUANT = 0x08;
    private static final int MT_FILTER = 0x10;
    private static final int MT_INTRA  = 0x20;

    private YUVFormat outputFormat = null;

    private long sequence = 0;

    private long qtable = 0;

    private Unsafe unsafe = null;

    private long shortSize = 0;

    private long intSize = 0;

    private long mbaHuff = 0;

    private int mbaHuffMaxLen = 0;

    private long runLevelHuff = 0;

    private int runLevelHuffMaxLen = 0;

    private long mtypeHuff = 0;

    private int mtypeHuffMaxLen = 10;

    private long blockPos = 0;

    private byte[] frameData = null;

    private long block = 0;

    private long qt = 0;

    private byte[] outputObject = null;

    private long byteArrayOffset = 0;

    private long outputOffset = 0;

    private DCT dct = new DCT();

    private long y1Offset = 0;

    private long y2Offset = 0;

    private long y3Offset = 0;

    private long y4Offset = 0;

    private long crOffset = 0;

    private long cbOffset = 0;

    /**
     * Creates a new H261ASDecoder
     *
     */
    public H261ASDecoder() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            shortSize = unsafe.arrayIndexScale(short[].class);
            intSize = unsafe.arrayIndexScale(int[].class);
            outputOffset = unsafe.objectFieldOffset(
                    H261ASDecoder.class.getDeclaredField("outputObject"));
            byteArrayOffset = unsafe.arrayBaseOffset(byte[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        qtable = unsafe.allocateMemory(32 * 256 * shortSize);

        for (int mq = 0; mq < 32; mq++) {
            long qt = qtable + ((mq << 8) * shortSize);
            for (int v = 0; v < 256; v++) {
                int s = (v << 24) >> 24;
                int val = 0;
                if (s > 0) {
                    val = (((s << 1) + 1) * mq) - (~mq & 1);
                } else {
                    val = (((s << 1) - 1) * mq) + (~mq & 1);
                }
                unsafe.putShort(qt + (v * shortSize), (short) val);
            }
        }

        mbaHuffMaxLen = 16;
        mbaHuff = makeHuff(H261Constants.MBAHUFF, mbaHuffMaxLen);
        addCode(0xf, 11,  MBA_STUFF, mbaHuff, mbaHuffMaxLen);
        addCode(0x1, 16, GOB_HEADER, mbaHuff, mbaHuffMaxLen);
        runLevelHuffMaxLen = getMaxLen(H261Constants.RUNLEVELHUFF);
        runLevelHuff = makeHuff(H261Constants.RUNLEVELHUFF,
                runLevelHuffMaxLen);
        addCode(0x2, 2,    EOB, runLevelHuff, runLevelHuffMaxLen);
        addCode(0x1, 6, ESCAPE, runLevelHuff, runLevelHuffMaxLen);

        mtypeHuff = unsafe.allocateMemory((1 << mtypeHuffMaxLen) * shortSize);
        addCode(0x1,  1, MT_CBP|MT_TCOEFF,
                mtypeHuff, mtypeHuffMaxLen);
        addCode(0x1,  2, MT_FILTER|MT_MVD|MT_CBP|MT_TCOEFF,
                mtypeHuff, mtypeHuffMaxLen);
        addCode(0x1,  3, MT_FILTER|MT_MVD,
                mtypeHuff, mtypeHuffMaxLen);
        addCode(0x1,  4, MT_INTRA|MT_TCOEFF,
                mtypeHuff, mtypeHuffMaxLen);
        addCode(0x1,  5, MT_MQUANT|MT_CBP|MT_TCOEFF,
                mtypeHuff, mtypeHuffMaxLen);
        addCode(0x1,  6, MT_MQUANT|MT_FILTER|MT_MVD|MT_CBP|MT_TCOEFF,
                mtypeHuff, mtypeHuffMaxLen);
        addCode(0x1,  7, MT_INTRA|MT_MQUANT|MT_TCOEFF,
                mtypeHuff, mtypeHuffMaxLen);
        addCode(0x1,  8, MT_MVD|MT_CBP|MT_TCOEFF,
                mtypeHuff, mtypeHuffMaxLen);
        addCode(0x1,  9, MT_MVD,
                mtypeHuff, mtypeHuffMaxLen);
        addCode(0x1, 10, MT_MQUANT|MT_CBP|MT_MVD|MT_TCOEFF,
                mtypeHuff, mtypeHuffMaxLen);

        block = unsafe.allocateMemory(64 * shortSize);

    }

    private int getMaxLen(int[] ht) {
        int maxLen = 0;
        for (int i = 0; i < ht.length; i += 2) {
            int length = ht[i + 1];

            if (length > maxLen) {
                maxLen = length;
            }
        }
        return maxLen;
    }

    public static String toBinaryString(int c, int len) {
        String s = "";
        for (int i = len - 1; i >= 0; i--) {
            if ((c & (1 << i)) > 0) {
                s += "1";
            } else {
                s += "0";
            }
        }
        return s;
    }

    private void addCode(int code, int length, int val, long huff, int maxLen) {
        int nbit = maxLen - length;
        int map = (val << 5) | length;
        code = (code & ((1 << maxLen) - 1)) << nbit;
        for (int n = (1 << nbit) - 1; n >= 0; --n) {
            int c = (code | n);
            unsafe.putShort(huff + (c * shortSize), (short) map);
        }
    }

    private long makeHuff(int[] ht, int maxLen) {
        int huffSize = 1 << maxLen;
        long huff = unsafe.allocateMemory(huffSize * shortSize);
        for (int i = 0; i < huffSize; ++i) {
            unsafe.putShort(huff + (i * shortSize),
                    (short) ((SYM_ILLEGAL << 5) | maxLen));
        }
        for (int i = 0; i < ht.length; i += 2) {
            int length = ht[i + 1];
            int code = ht[i];
            if (length != 0) {
                addCode(code, length, i / 2, huff, maxLen);
            }
        }
        return huff;
    }

    /**
     * @see javax.media.Codec#getSupportedInputFormats()
     */
    public Format[] getSupportedInputFormats() {
        return new Format[]{new VideoFormat("h261as/rtp", null,
                Format.NOT_SPECIFIED, Format.byteArray, Format.NOT_SPECIFIED)};
    }

    /**
     * @see javax.media.Codec#getSupportedOutputFormats(javax.media.Format)
     */
    public Format[] getSupportedOutputFormats(Format input) {
        if (input == null) {
            return new Format[]{new YUVFormat(YUVFormat.YUV_420)};
        }
        if (input.getEncoding().equals("h261as/rtp")) {
            VideoFormat format = (VideoFormat) input;
            Dimension size = format.getSize();
            if ((size == null) || (size.width < 1) || (size.height < 1)) {
                size = new Dimension(320, 240);
            }
            int ysize = size.width * size.height;
            int csize = (size.width / 2) * (size.height / 2);
            return new Format[]{new YUVFormat(size, Format.NOT_SPECIFIED,
                    Format.byteArray, format.getFrameRate(), YUVFormat.YUV_420,
                    0, ysize, ysize + csize, size.width, size.width / 2)};
        }
        return null;
    }

    private boolean readBlock(BitInputStream in, long offset,
            int stride) {
        for (int i = 0; i < 64; i++) {
            unsafe.putShort(block + (i * shortSize), (short) 0);
        }
        int dc = in.readBits(8);
        if (dc == 255) {
            dc = 128;
        }
        dc = dc << 3;
        unsafe.putShort(block, (short) (dc & 0xffff));
        int nc = 0;
        int k = 1;
        int m0 = 1;
        boolean eob = false;
        while (!eob) {
            int runLevel = in.huffDecode(runLevelHuff, runLevelHuffMaxLen);
            int run = 0;
            int level = 0;
            if (runLevel == EOB) {
                eob = true;

                /*DecimalFormat format = new DecimalFormat(" 0000;-0000");
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        System.err.print(format.format(unsafe.getShort(block + (((i * 8) + j) * shortSize))));
                        System.err.print(" ");
                    }
                    System.err.println();
                }
                System.err.println(); */

                if (nc == 0) {
                    int dcFillVal = (dc + 4) >> 3;
                    for (int i = 0; i < 8; i++) {
                        long outPos = unsafe.getLong(this, outputOffset)
                            + byteArrayOffset + offset + (i * stride);
                        unsafe.putByte(outPos + 0, (byte) (dcFillVal & 0xFF));
                        unsafe.putByte(outPos + 1, (byte) (dcFillVal & 0xFF));
                        unsafe.putByte(outPos + 2, (byte) (dcFillVal & 0xFF));
                        unsafe.putByte(outPos + 3, (byte) (dcFillVal & 0xFF));
                        unsafe.putByte(outPos + 4, (byte) (dcFillVal & 0xFF));
                        unsafe.putByte(outPos + 5, (byte) (dcFillVal & 0xFF));
                        unsafe.putByte(outPos + 6, (byte) (dcFillVal & 0xFF));
                        unsafe.putByte(outPos + 7, (byte) (dcFillVal & 0xFF));
                        outPos += stride;
                    }
                } else {
                    dct.rdct(block, m0, outputObject, offset, stride);
                }

                return true;
            } else if (runLevel == SYM_ILLEGAL) {
                System.err.println("Illegal code");
                return false;
            } else if (runLevel == ESCAPE) {
                run = in.readBits(6);
                level = in.readBits(8);
            } else {
                level = (runLevel >> 6) & 0xFF;
                run = runLevel & 0x1f;
            }

            if (!eob) {
                k += run;
                if (k >= 64) {
                    System.err.println("Run overflow");
                    return false;
                }
                int pos = H261Constants.COLZAG[k++];
                short realLevel = unsafe.getShort(qt + (level * shortSize));
                unsafe.putShort(block + (pos * shortSize), realLevel);
                nc++;
                m0 |= 1 << pos;
            }
        }

        return true;
    }

    /**
     * @see javax.media.Codec#process(javax.media.Buffer, javax.media.Buffer)
     */
    public int process(Buffer input, Buffer output) {

        BitInputStream in = new BitInputStream((byte[]) input.getData(),
                input.getOffset(), input.getLength());

        // Read the H261AS header
        int ebit = in.readBits(3);
        //ebit = in.readBits(3);
        int quant = in.readBits(5);
        int width = (in.readBits(12) + 1) << 4;
        int height = (in.readBits(12) + 1) << 4;
        int ysize = width * height;
        int csize = (width / 2) * (height / 2);
        int dataSize = ysize + (2 * csize);

        qt = qtable + ((quant << 8) * shortSize);

        outputObject = (byte[]) output.getData();
        int offset = output.getOffset();
        if ((outputObject == null) || (output.getLength() < dataSize)) {
            outputObject = new byte[dataSize];
            offset = 0;
            output.setData(outputObject);
            output.setOffset(offset);
            if (frameData != null) {
                System.arraycopy(frameData, 0, outputObject, 0,
                        Math.max(frameData.length, dataSize));
                frameData = null;
            }
        }
        output.setLength(dataSize);

        if (outputFormat == null) {
            VideoFormat inputFormat = (VideoFormat) input.getFormat();
            outputFormat = new YUVFormat(new Dimension(width, height),
                    dataSize, Format.byteArray,
                    inputFormat.getFrameRate(), YUVFormat.YUV_420,
                    width, width / 2, 0, ysize, ysize + csize);
            int nBlocksWidth = width / 16;
            int nBlocksHeight = height / 16;
            int nBlocks = nBlocksWidth * nBlocksHeight;
            int nGobs = nBlocks / 33;
            if ((nGobs * 33) < nBlocks) {
                nGobs += 1;
            }
            blockPos = unsafe.allocateMemory(nBlocks * intSize);
            for (int i = 0; i < nBlocks; i++) {
                int blocky = i / nBlocksWidth;
                int blockx = i % nBlocksWidth;
                int x = blockx * 16;
                int y = blocky * 16;
                unsafe.putInt(blockPos + (i * intSize),
                        ((x & 0xffff) << 16) | (y & 0xffff));
            }
            y1Offset = 0;
            y2Offset = 8;
            y3Offset = 8 * width;
            y4Offset = (8 * width) + 8;
            crOffset = ysize;
            cbOffset = ysize + csize;
        }

        int gob = 0;
        int mba = 0;
        while (in.bitsRemaining() > ebit) {
            int mbadiff = in.huffDecode(mbaHuff, mbaHuffMaxLen);
            if (mbadiff == -1) {

                // Read the rest of the GOB Header
                gob = ((in.readBits(10) & 0x3FF)) << 10
                    | (in.readBits(10) & 0x3FF);
                quant = in.readBits(5);
                qt = qtable + ((quant << 8) * shortSize);
                mba = 0;

            } else if (mbadiff >= 0) {

                // Read the rest of the Macroblock
                mba += mbadiff + 1;
                int mtype = in.huffDecode(mtypeHuff, mtypeHuffMaxLen);
                if ((mtype & MT_MQUANT) > 0) {
                    quant = in.readBits(5);
                    qt = qtable + ((quant << 8) * shortSize);
                }
                if ((mtype & MT_CBP) > 0) {
                    System.err.println("CBP unsupported!");
                    return BUFFER_PROCESSED_FAILED;
                }

                int blockNo = (gob * 33) + (mba - 1);
                int x = unsafe.getInt(blockPos + (blockNo * intSize));
                int y = x & 0xffff;
                x = (x >> 16) & 0xffff;
                long yOffset = y * width + x;
                long cOffset = ((y / 2) * (width / 2)) + (x / 2);

                if ((mtype & MT_TCOEFF) > 0) {
                    readBlock(in,
                            yOffset + y1Offset + output.getOffset(), width);
                    readBlock(in,
                            yOffset + y2Offset + output.getOffset(), width);
                    readBlock(in,
                            yOffset + y3Offset + output.getOffset(), width);
                    readBlock(in,
                            yOffset + y4Offset + output.getOffset(), width);
                    readBlock(in,
                            cOffset + crOffset + output.getOffset(), width / 2);
                    readBlock(in,
                            cOffset + cbOffset + output.getOffset(), width / 2);
                }
            } else if (mbadiff != MBA_STUFF) {

                // Only stuffing is recognised
                System.err.println("Unknown code");
                return BUFFER_PROCESSED_FAILED;
            }
        }
        if ((input.getFlags() & Buffer.FLAG_RTP_MARKER) > 0) {
            output.setFormat(outputFormat);
            output.setDiscard(false);
            output.setTimeStamp(input.getTimeStamp());
            output.setSequenceNumber(sequence++);
            return BUFFER_PROCESSED_OK;
        }
        return OUTPUT_BUFFER_NOT_FILLED;
    }

    /**
     * @see javax.media.Codec#setInputFormat(javax.media.Format)
     */
    public Format setInputFormat(Format format) {
        if (format.getEncoding().equals("h261as/rtp")) {
            return format;
        }
        return null;
    }

    /**
     * @see javax.media.Codec#setOutputFormat(javax.media.Format)
     */
    public Format setOutputFormat(Format format) {
        if (format instanceof YUVFormat) {
            YUVFormat yuv = (YUVFormat) format;
            YUVFormat testFormat = new YUVFormat(yuv.getSize(),
                    Format.NOT_SPECIFIED, Format.byteArray,
                    Format.NOT_SPECIFIED, YUVFormat.YUV_420,
                    Format.NOT_SPECIFIED, Format.NOT_SPECIFIED,
                    Format.NOT_SPECIFIED, Format.NOT_SPECIFIED,
                    Format.NOT_SPECIFIED);
            if (yuv.matches(testFormat)) {
                return yuv;
            }
        }
        return null;
    }

    /**
     * @see javax.media.PlugIn#close()
     */
    public void close() {
        unsafe.freeMemory(qtable);
        unsafe.freeMemory(mtypeHuff);
        unsafe.freeMemory(block);
        unsafe.freeMemory(blockPos);
        unsafe.freeMemory(mbaHuff);
        unsafe.freeMemory(runLevelHuff);
        dct.close();
    }

    /**
     * @see javax.media.PlugIn#getName()
     */
    public String getName() {
        return "H261ASDecoder";
    }

    /**
     * @see javax.media.PlugIn#open()
     */
    public void open() {
        // Does Nothing
    }

    /**
     * @see javax.media.PlugIn#reset()
     */
    public void reset() {
        // Does Nothing
    }

    /**
     * @see javax.media.Controls#getControl(java.lang.String)
     */
    public Object getControl(String className) {
        if (className.equals("controls.FrameFillControl")){
            return this;
        }
        return null;
    }

    /**
     * @see javax.media.Controls#getControls()
     */
    public Object[] getControls() {
        return new Object[]{this};
    }

    public static void main(String[] args) {
        H261ASEncoder encoder = new H261ASEncoder();
        H261ASDecoder decoder = new H261ASDecoder();

        int ysize = 16 * 16;
        int csize = 8 * 8;
        int dataSize = ysize + (2 * csize);
        YUVFormat yuv = new YUVFormat(new Dimension(16, 16), dataSize,
                Format.byteArray, Format.NOT_SPECIFIED, YUVFormat.YUV_420,
                16, 8, 0, ysize, ysize + csize);
        VideoFormat h261as = new VideoFormat("h261as/rtp");
        encoder.setInputFormat(yuv);
        encoder.setOutputFormat(h261as);
        decoder.setInputFormat(h261as);
        decoder.setOutputFormat(yuv);

        byte[] data = new byte[dataSize];
        for (int i = 0; i < dataSize; i++) {
            data[i] = (byte) ((int) (Math.random() * 255) & 0xFF);
        }

        Buffer inputBuffer = new Buffer();
        inputBuffer.setData(data);
        inputBuffer.setLength(dataSize);
        inputBuffer.setOffset(0);
        inputBuffer.setFormat(yuv);
        Buffer outputBuffer = new Buffer();
        outputBuffer.setData(null);
        outputBuffer.setLength(0);

        encoder.process(inputBuffer, outputBuffer);

        Buffer out = new Buffer();
        decoder.process(outputBuffer, out);

        byte[] outData = (byte[]) out.getData();

        DecimalFormat format = new DecimalFormat(" 000;-000");

        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 16; j++) {
                System.err.print(format.format(data[(i * 16) + j]) + " ");
            }
            System.err.print("    ");
            for (int j = 0; j < 16; j++) {
                System.err.print(format.format(outData[(i * 16) + j]) + " ");
            }
            System.err.println();
        }
        System.err.println();
    }

    public void fillFrame(byte[] frameData) {
        this.frameData = frameData;
    }

    public Component getControlComponent() {
        return null;
    }
}
