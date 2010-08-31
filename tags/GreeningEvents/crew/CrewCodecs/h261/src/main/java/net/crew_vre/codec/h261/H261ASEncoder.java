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

import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Format;
import javax.media.PlugIn;
import javax.media.control.KeyFrameControl;
import javax.media.control.QualityControl;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.format.YUVFormat;

import net.crew_vre.codec.controls.KeyFrameForceControl;
import net.crew_vre.codec.utils.BitOutputStream;
import net.crew_vre.codec.utils.ConditionalReplenishment;
import net.crew_vre.codec.utils.DCT;

import sun.misc.Unsafe;

/**
 * An encoder of H.261 video
 *
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class H261ASEncoder implements Codec, QualityControl, KeyFrameControl,
        KeyFrameForceControl {

    // The number of bits in the header
    private static final int HEADER_BITS = 32;

    // The maximum sending size of an RTP packet (in bits)
    private static final int MAX_SEND_SIZE = 960 * 8;

    // The DC quantization shift
    private static final int DC_QUANT_SHIFT = 3;

    // The size of the level map
    private static final int LEVEL_MAP_SIZE = 8196;

    // The amount to shift each level by in the level map (to take account
    // of the unsigned nature of the levels)
    private static final int LEVEL_MAP_SHIFT = 1024;

    // The threshold position in the zig zag array after which filtered
    // values will be used
    private static final int FILTER_THRESHOLD = 20;

    // The start position of filtered values in the level map
    private static final int LEVEL_MAP_FILTER_SHIFT = 4096;

    // The quantizer of the frames
    private int hq = 1;
    private int mq = 2;
    private int lq = 4;

    // The number of the frame in this sequence
    private long sequencenumber = 0;

    // The DCT values of the first Y block
    private long y1DCT = 0;

    // The DCT values of the second Y block
    private long y2DCT = 0;

    // The DCT values of the third Y block
    private long y3DCT = 0;

    // The DCT values of the fourth Y block
    private long y4DCT = 0;

    // The DCT values of the Cb block
    private long cbDCT = 0;

    // The DCT values of the Cr block
    private long crDCT = 0;

    // The level map
    private long levelmap[] = new long[31];

    // The level map for color
    private long levelmapc[] = new long[31];

    // The number of frames sent
    private int count = 0;

    // The width
    private int width = 0;

    // The height
    private int height = 0;

    private int yStart = 0;

    private int crStart = 0;

    private int cbStart = 0;

    private int yStride = 0;

    private int crcbStride = 0;

    private int nGobs = 0;

    private int nBlocksWidth = 0;

    private int nBlocksHeight = 0;

    private int nBlocks = 0;

    private DCT dct = new DCT();

    private ConditionalReplenishment cr = null;

    private int framesBetweenKey = 250;

    private int framesSinceLastKey = 0;

    private Unsafe unsafe = null;

    private long intSize = 0;

    private long mbaHuffEncOffset = 0;

    private long runLevelEncOffset = 0;

    private Format inputFormat = null;

    private Format outputFormat = null;

    private Format[] inputFormats = null;

    private Format[] outputFormats = null;

    /**
     * Creates a new H261 Encoder
     *
     */
    public H261ASEncoder() {

        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            intSize = unsafe.arrayIndexScale(int[].class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set up the input and output formats
        inputFormats = new Format[]{new YUVFormat(YUVFormat.YUV_420)};
        outputFormats = new VideoFormat[1];
        outputFormats[0] = new VideoFormat("h261as/rtp");

        y1DCT = unsafe.allocateMemory(64 * intSize);
        y2DCT = unsafe.allocateMemory(64 * intSize);
        y3DCT = unsafe.allocateMemory(64 * intSize);
        y4DCT = unsafe.allocateMemory(64 * intSize);
        crDCT = unsafe.allocateMemory(64 * intSize);
        cbDCT = unsafe.allocateMemory(64 * intSize);
        unsafe.setMemory(y1DCT, 64 * intSize, (byte) 0);
        unsafe.setMemory(y2DCT, 64 * intSize, (byte) 0);
        unsafe.setMemory(y3DCT, 64 * intSize, (byte) 0);
        unsafe.setMemory(y4DCT, 64 * intSize, (byte) 0);
        unsafe.setMemory(crDCT, 64 * intSize, (byte) 0);
        unsafe.setMemory(cbDCT, 64 * intSize, (byte) 0);

        mbaHuffEncOffset = unsafe.allocateMemory(
                H261Constants.MBAHUFF.length * intSize);
        for (int i = 0; i < H261Constants.MBAHUFF.length; i++) {
            unsafe.putInt(mbaHuffEncOffset + (i * intSize),
                    H261Constants.MBAHUFF[i]);
        }
        runLevelEncOffset = unsafe.allocateMemory(
                H261Constants.RUNLEVELHUFF.length * intSize);
        for (int i = 0; i < H261Constants.RUNLEVELHUFF.length; i++) {
            unsafe.putInt(runLevelEncOffset + (i * intSize),
                    H261Constants.RUNLEVELHUFF[i]);
        }

    }

    private long makeLevelMap(int quant, int fthresh) {

        long map = unsafe.allocateMemory(LEVEL_MAP_SIZE);
        long filterlevelmap = map + LEVEL_MAP_FILTER_SHIFT;

        // Set up the level map
        int i;
        unsafe.putByte(map + LEVEL_MAP_SHIFT, (byte) 0);
        unsafe.putByte(filterlevelmap + LEVEL_MAP_SHIFT, (byte) 0);
        int q = quant * H261Constants.QUANT_SCALE;
        for (i = 1; i < LEVEL_MAP_SHIFT; ++i) {
            byte l = (byte) (i / q);
            unsafe.putByte(map + i + LEVEL_MAP_SHIFT, l);
            unsafe.putByte(map - i + LEVEL_MAP_SHIFT, (byte) -l);

            if (l <= fthresh) {
                l = 0;
            }
            unsafe.putByte(filterlevelmap + i + LEVEL_MAP_SHIFT, l);
            unsafe.putByte(filterlevelmap - i + LEVEL_MAP_SHIFT, (byte) -l);
        }
        return map;
    }

    // Adds a block to the BitVector
    private void addBlock(BitOutputStream outputdata, long dctVals,
           long levelmap) {

        /*DecimalFormat format = new DecimalFormat(" 0000;-0000");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.err.print(format.format(unsafe.getInt(dctVals + (((i * 8) + j) * intSize))));
                System.err.print(" ");
            }
            System.err.println();
        }
        System.err.println(); */

        // Add the DC of the block
        int dc = unsafe.getInt(dctVals) >> DC_QUANT_SHIFT;

        // The DC cannot be less than 0 or more than 254
        if (dc <= 0) {
            dc = 1;
        } else if (dc > H261Constants.DC_MAX) {
            dc = H261Constants.DC_MAX;
        }
        outputdata.add(dc, H261Constants.DC_BITS);

        // Add the remaining AC values
        int n = 1;
        int filter = 0;
        while (n < (H261Constants.BLOCK_SIZE * H261Constants.BLOCK_SIZE)) {

            // Add up the number of times a value appears
            int run = 0;
            while ((n < (H261Constants.BLOCK_SIZE * H261Constants.BLOCK_SIZE))
                    && (unsafe.getByte(levelmap + (unsafe.getInt(dctVals + (((H261Constants.ZZY[n]
                                              * H261Constants.BLOCK_SIZE)
                                         + H261Constants.ZZX[n]) * intSize))
                            + LEVEL_MAP_SHIFT + filter)) == 0)) {
                n++;
                run++;
                if (n == FILTER_THRESHOLD) {
                    filter = LEVEL_MAP_FILTER_SHIFT;
                }
            }

            // Encode the run-level value
            if (n < (H261Constants.BLOCK_SIZE * H261Constants.BLOCK_SIZE)) {
                byte level = unsafe.getByte(levelmap + (unsafe.getInt(dctVals
                        + (((H261Constants.ZZY[n] * H261Constants.BLOCK_SIZE)
                        + H261Constants.ZZX[n]) * intSize))
                        + LEVEL_MAP_SHIFT));
                int runLevel =
                    (((level + H261Constants.MAX_LEVEL) & 0xFF) << 6) | run;
                int code = 0;
                int len = 0;
                if (level >= -15 && level <= 15) {
                    len = unsafe.getInt(
                        runLevelEncOffset + (((runLevel * 2) + 1) * intSize));
                    if (len != 0) {
                        code = unsafe.getInt(
                            runLevelEncOffset + ((runLevel * 2) * intSize));
                    }
                }

                if (len == 0) {
                    len = 20;
                    code = (0x4000) | ((run & 0x3f) << 8) | (level & 0xff);
                }
                outputdata.add(code, len);
                n++;
            }
        }

        // Add the end of the block marker
        outputdata.add(H261Constants.EOB, H261Constants.EOB_BITS);
    }

    private void finishBuffers(Buffer output, BitOutputStream outputdata,
            int startMquant, long timestamp) {
        // Finish this packet
        output.setFormat(outputFormat);
        int ebit = outputdata.flush();

        // Generate the header
        BitOutputStream header = new BitOutputStream((byte[]) output.getData(),
                output.getOffset());
        header.add(ebit, H261Constants.END_PADDING_BITS); // EBIT
        header.add(startMquant, H261Constants.QUANT_BITS); // QUANT
        header.add((width >> 4) - 1, 12); // WIDTH
        header.add((height >> 4) - 1, 12); // HEIGHT

        header.flush();

        output.setSequenceNumber(sequencenumber++);
        output.setTimeStamp(timestamp);
        output.setOffset(0);
        output.setLength(outputdata.getLength());
        output.setDiscard(false);
    }

    /**
     * Processes an RGB frame to convert it to H.261AS
     *
     * @see javax.media.Codec#process(javax.media.Buffer, javax.media.Buffer)
     */
    public int process(Buffer input, Buffer output) {

        try {
            VideoFormat ivf = (VideoFormat) input.getFormat();

            if (ivf instanceof YUVFormat) {
                if (cr == null) {
                    cr = new ConditionalReplenishment(width, height);
                }
                if (framesSinceLastKey >= framesBetweenKey) {
                    cr.reset();
                    framesSinceLastKey = 0;
                } else {
                    framesSinceLastKey += 1;
                }

                byte[] yuv = (byte[]) input.getData();
                int offset = input.getOffset();
                int currentX = offset % width;
                int currentY = offset / width;
                //System.err.println("Offset = " + offset + " x = " + currentX + " y = " + currentY);

                // Find the current gob number and macroblock
                int blockN = ((currentY / 16) * nBlocksWidth)
                    + (currentX / 16);
                int gob = blockN / 33;
                int mba = blockN % 33;

                // Create the packet
                byte[] bytes = (byte[]) output.getData();
                if (output.getLength() < (MAX_SEND_SIZE >> 2)) {
                    bytes = new byte[MAX_SEND_SIZE >> 2];
                    output.setData(bytes);
                    output.setOffset(0);
                }
                BitOutputStream outputdata = new BitOutputStream(bytes,
                        output.getOffset());

                // Add 32 bits to be used for the header later
                outputdata.add(0, HEADER_BITS);

                // Store the initial values
                int startMquant = lq;
                int mquant = lq;

                for (int gobn = gob; gobn < nGobs; gobn++) {

                    // Write the GOB header
                    int lastMba = -1;
                    outputdata.add(H261Constants.GOB_START, 16); // GBSC
                    outputdata.add(gobn, 20); // GN
                    outputdata.add(mquant, 5); // GQUANT

                    for (int mb = mba; (mb < 33) && (blockN < nBlocks); mb++) {
                        //System.err.println("Blockn = " + blockN + " x = " + currentX + " y = " + currentY);

                        // Decide whether to send this macroblock
                        boolean send = cr.send(blockN);

                        if (send) {

                            int quant = 0;
                            int how = cr.getCrState(blockN);
                            if (how == ConditionalReplenishment.CR_MOTION) {
                                quant = lq;
                            } else if (how == ConditionalReplenishment.CR_BG) {
                                quant = hq;
                            } else {
                                quant = mq;
                            }

                            // DCT the blocks
                            dct.FDCT(yuv, y1DCT, yStart, currentX, currentY,
                                    yStride);
                            dct.FDCT(yuv, y2DCT, yStart, currentX + 8, currentY,
                                    yStride);
                            dct.FDCT(yuv, y3DCT, yStart, currentX, currentY + 8,
                                    yStride);
                            dct.FDCT(yuv, y4DCT, yStart, currentX + 8,
                                    currentY + 8,  yStride);
                            dct.FDCT(yuv, cbDCT, cbStart, currentX / 2,
                                    currentY / 2, crcbStride);
                            dct.FDCT(yuv, crDCT, crStart, currentX / 2,
                                    currentY / 2, crcbStride);

                            // Check the quantizer is enough for the
                            // macroblock
                            int max = 0;
                            int min = 0;
                            for (int z = 1; z < 64; z++) {
                                max = Math.max(max,
                                        unsafe.getInt(y1DCT + (z * intSize)));
                                min = Math.min(min,
                                        unsafe.getInt(y1DCT + (z * intSize)));
                                max = Math.max(max,
                                        unsafe.getInt(y2DCT + (z * intSize)));
                                min = Math.min(min,
                                        unsafe.getInt(y2DCT + (z * intSize)));
                                max = Math.max(max,
                                        unsafe.getInt(y3DCT + (z * intSize)));
                                min = Math.min(min,
                                        unsafe.getInt(y3DCT + (z * intSize)));
                                max = Math.max(max,
                                        unsafe.getInt(y4DCT + (z * intSize)));
                                min = Math.min(min,
                                        unsafe.getInt(y4DCT + (z * intSize)));
                                max = Math.max(max,
                                        unsafe.getInt(crDCT + (z * intSize)));
                                min = Math.min(min,
                                        unsafe.getInt(crDCT + (z * intSize)));
                                max = Math.max(max,
                                        unsafe.getInt(cbDCT + (z * intSize)));
                                min = Math.min(min,
                                        unsafe.getInt(cbDCT + (z * intSize)));
                            }

                            // Need to requantize
                            if (-min > max) {
                                max = -min;
                            }
                            if (max / quant >= H261Constants.MAX_LEVEL) {
                                while (max / quant
                                        >= H261Constants.MAX_LEVEL) {
                                    quant += 1;
                                }
                            }

                            long map = levelmap[quant];

                            if (map == 0) {
                                levelmap[quant] = makeLevelMap(quant, 1);
                                levelmapc[quant] = makeLevelMap(quant, 2);
                                map = levelmap[quant];
                            }

                            // MB Header
                            int mdiff = mb - lastMba - 1;
                            lastMba = mb;

                            int code = unsafe.getInt(
                              mbaHuffEncOffset + ((mdiff * 2) * intSize));
                            int len = unsafe.getInt(
                              mbaHuffEncOffset + (((mdiff * 2) + 1) * intSize));
                            outputdata.add(code, len);

                            if (mquant != quant) {
                                outputdata.add(1, H261Constants.
                                        MTYPE_INTRA_MQUANT_TCOEFF_BITS); // MTYP
                                outputdata.add(quant,
                                        H261Constants.QUANT_BITS); // MQ
                                 mquant = quant;
                            } else {
                                outputdata.add(1,
                                        H261Constants.MTYPE_INTRA_TCOEFF_BITS);
                            }

                            // MB Data - Y
                            addBlock(outputdata, y1DCT, map);
                            addBlock(outputdata, y2DCT, map);
                            addBlock(outputdata, y3DCT, map);
                            addBlock(outputdata, y4DCT, map);

                            // MB Data - Cb
                            map = levelmapc[quant];
                            addBlock(outputdata, cbDCT, map);

                            // MB Data - Cr
                            addBlock(outputdata, crDCT, map);

                        }

                        // Work out the x and y of the next block
                        currentX += 16;
                        if (currentX >= width) {
                            currentX = 0;
                            currentY += 16;
                        }
                        blockN += 1;

                        // Determine if enough bits have been added to
                        // send this packet
                        if (outputdata.noBits() > MAX_SEND_SIZE) {
                            //System.err.println("Finished at Blockn = " + blockN + " x = " + currentX + " y = " + currentY);
                            finishBuffers(output, outputdata,
                                    startMquant, input.getTimeStamp());
                            output.setFlags(output.getFlags()
                                    & ~Buffer.FLAG_RTP_MARKER);
                            input.setOffset((currentY * width) +
                                    currentX);
                            return INPUT_BUFFER_NOT_CONSUMED;
                        }
                    }
                    mba = 0;
                }

                finishBuffers(output, outputdata,
                        startMquant, input.getTimeStamp());
                cr.replenish(yuv);
                output.setFlags(output.getFlags()
                        | Buffer.FLAG_RTP_MARKER);
                count++;
                return PlugIn.BUFFER_PROCESSED_OK;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PlugIn.BUFFER_PROCESSED_FAILED;
    }

    // Check that the input is in a good format
    private boolean verifyInputFormat(Format input) {
        if (input instanceof RGBFormat) {
            return true;
        } else if (input instanceof YUVFormat) {
            if (!input.matches(inputFormats[0])) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     *
     * @see javax.media.Codec#setInputFormat(javax.media.Format)
     */
    public Format setInputFormat(Format in) {
        inputFormat = in;
        Format format = in;
        if (format instanceof YUVFormat) {
            YUVFormat vfIn = (YUVFormat) format;
            Dimension size = vfIn.getSize();
            width = size.width;
            height = size.height;
            yStart = vfIn.getOffsetY();
            cbStart = vfIn.getOffsetU();
            crStart = vfIn.getOffsetV();
            yStride = vfIn.getStrideY();
            crcbStride = vfIn.getStrideUV();
            nBlocksWidth = width / 16;
            nBlocksHeight = height / 16;
            nBlocks = nBlocksWidth * nBlocksHeight;
            nGobs = nBlocks / 33;
            if ((nGobs * 33) < nBlocks) {
                nGobs += 1;
            }
        }
        return format;
    }

    /**
     * @see javax.media.Codec#getSupportedOutputFormats(javax.media.Format)
     */
    public Format[] getSupportedOutputFormats(Format in) {
        if (in == null) {
            return outputFormats;
        }
        if (!verifyInputFormat(in)) {
            return new Format[0];
        }
        VideoFormat vf = (VideoFormat) in;

        return new Format[]{new VideoFormat("h261as/rtp", vf.getSize(),
                Format.NOT_SPECIFIED, Format.byteArray, vf.getFrameRate())};
    }

    /**
     * @see javax.media.PlugIn#getName()
     */
    public String getName() {
        return "H.261 RTP Encoder";
    }

    /**
     *
     * @see javax.media.control.QualityControl#getPreferredQuality()
     */
    public float getPreferredQuality() {
        return 0.75f;
    }

    /**
     *
     * @see javax.media.control.QualityControl#getQuality()
     */
    public float getQuality() {
        return lq / 31;
    }

    /**
     *
     * @see javax.media.control.QualityControl
     *     #isTemporalSpatialTradeoffSupported()
     */
    public boolean isTemporalSpatialTradeoffSupported() {
        return false;
    }

    /**
     *
     * @see javax.media.control.QualityControl#setQuality(float)
     */
    public float setQuality(float f) {
        lq = (int) ((1 - f) * 31.0f);
        mq = lq / 2;
        hq = 1;
        return f;
    }

    /**
     *
     * @see javax.media.Control#getControlComponent()
     */
    public Component getControlComponent() {
        return null;
    }

    /**
     *
     * @see com.sun.media.BasicPlugIn#getControl(java.lang.String)
     */
    public Object getControl(String className) {
        if (className.equals(QualityControl.class.getCanonicalName())) {
            System.err.println("Quality");
            return this;
        }
        return null;
    }

    /**
     *
     * @see com.sun.media.BasicPlugIn#getControls()
     */
    public Object[] getControls() {
        return new Object[]{this};
    }

    /**
     *
     * @see javax.media.Codec#getSupportedInputFormats()
     */
    public Format[] getSupportedInputFormats() {
        return inputFormats;
    }

    /**
     *
     * @see javax.media.Codec#setOutputFormat(javax.media.Format)
     */
    public Format setOutputFormat(Format format) {
        outputFormat = format;
        return format;
    }

    /**
     *
     * @see javax.media.PlugIn#close()
     */
    public void close() {
        unsafe.freeMemory(y1DCT);
        unsafe.freeMemory(y2DCT);
        unsafe.freeMemory(y3DCT);
        unsafe.freeMemory(y4DCT);
        unsafe.freeMemory(crDCT);
        unsafe.freeMemory(cbDCT);
        unsafe.freeMemory(mbaHuffEncOffset);
        unsafe.freeMemory(runLevelEncOffset);
        for (int quant = 0; quant < levelmap.length; quant++) {
            if (levelmap[quant] != 0) {
                unsafe.freeMemory(levelmap[quant]);
                unsafe.freeMemory(levelmapc[quant]);
            }
        }
        cr.close();
        dct.close();
    }

    /**
     *
     * @see javax.media.PlugIn#open()
     */
    public void open() {
        // Does Nothing
    }

    /**
     *
     * @see javax.media.PlugIn#reset()
     */
    public void reset() {
        // Does Nothing
    }

    /**
     *
     * @see javax.media.control.KeyFrameControl#getKeyFrameInterval()
     */
    public int getKeyFrameInterval() {
        return framesBetweenKey;
    }

    /**
     *
     * @see javax.media.control.KeyFrameControl#getPreferredKeyFrameInterval()
     */
    public int getPreferredKeyFrameInterval() {
        return 250;
    }

    /**
     *
     * @see javax.media.control.KeyFrameControl#setKeyFrameInterval(int)
     */
    public int setKeyFrameInterval(int frames) {
        framesBetweenKey = frames;
        return frames;
    }

    /**
     *
     * @see controls.KeyFrameForceControl#nextFrameKey()
     */
    public void nextFrameKey() {
        framesSinceLastKey = framesBetweenKey;
    }
}
