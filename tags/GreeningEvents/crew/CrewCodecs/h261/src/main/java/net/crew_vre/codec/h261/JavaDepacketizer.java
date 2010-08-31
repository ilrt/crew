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

import java.awt.Dimension;
import java.io.IOException;

import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Format;
import javax.media.format.VideoFormat;

import net.crew_vre.codec.utils.ByteArrayOutputStream;

/**
 * Depacketizes H.261 Packets as per RFC 4587
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class JavaDepacketizer implements Codec {

    private static final int NUM_PACKETS = 30;

    private static final VideoFormat INPUT_FORMAT =
        new VideoFormat(VideoFormat.H261_RTP, null, Format.NOT_SPECIFIED,
                Format.byteArray, Format.NOT_SPECIFIED);

    private static final VideoFormat OUTPUT_FORMAT =
        new VideoFormat(VideoFormat.H261, null, Format.NOT_SPECIFIED,
                Format.byteArray, Format.NOT_SPECIFIED);

    private static final int[] MB_STUFFING = new int[]{
        0x01, 0xE0, 0x3C, 0x07, 0x80, 0xF0, 0x1E, 0x03, 0xC0, 0x78, 0x0F};

    private static final int[] EBIT_START = new int[]{
        MB_STUFFING.length, 6, 2, 9, 5, 1, 8, 4};

    private static final int[] SBIT_END = new int[]{
        0, 4, 8, 1, 5, 9, 2, 6};

    private static final int[] EBIT_AND = new int[]{
        0xFF, 0xFE, 0xFC, 0xF8, 0xF0, 0xE0, 0xC0, 0x80};

    private static final int[] SBIT_AND = new int[]{
        0xFF, 0x7F, 0x3F, 0x1F, 0x0F, 0x07, 0x03, 0x01};

    private static final int[] SBIT_MASK = new int[]{
        0, 0x1, 0x3, 0x7, 0xF, 0x1F, 0x3F, 0x7F};

    private long firstSequence = -1;

    private long lastSequence = -2;

    private int lastGobn = -1;

    private long currentTimestamp = 0;

    private byte[][] packet = null;

    private int highestOffset = 0;

    private int noFrames = 0;

    private byte[] data = new byte[352 * 288 * 4];



    /**
     *
     * @see javax.media.Codec#getSupportedInputFormats()
     */
    public Format[] getSupportedInputFormats() {
        return new VideoFormat[]{INPUT_FORMAT};
    }

    /**
     *
     * @see javax.media.Codec#getSupportedOutputFormats(javax.media.Format)
     */
    public Format[] getSupportedOutputFormats(Format input) {
        if (input == null) {
            return new Format[]{OUTPUT_FORMAT};
        }
        if (input.matches(INPUT_FORMAT)) {
            return new Format[]{OUTPUT_FORMAT};
        }
        return new Format[0];
    }

    /*private void print(byte[] data, int offset, int length) {
        System.err.print(offset + ": ");
        for (int i = offset; i < (offset + length); i++) {
            for (int j = 7; j >= 0; j--) {
                if ((data[i] & (0x1 << j)) > 0) {
                    System.err.print("1");
                } else {
                    System.err.print("0");
                }
            }
            System.err.print("|");
            if ((i + 1) % 8 == 0) {
                System.err.println();
                System.err.print((i + 1) + ": ");
            }
        }
        System.err.println();
    } */

    private int process(Buffer output) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(data, 0,
                data.length);
        boolean packetSkipped = false;
        boolean firstGobSeen = false;
        for (int i = 0; i <= highestOffset; i++) {
            if (packet[i] != null) {
                int l = packet[i].length;
                int sbit = (packet[i][0] >> 5) & 0x7;
                int ebit = (packet[i][0] >> 2) & 0x7;
                int gobn = (packet[i][1] >> 4) & 0xF;
                int mbap = (packet[i][1] & 0xF)
                    | ((packet[i][2] >> 7) & 0x1);

                try {
                    if (gobn < 12) {
                        if ((gobn == 0) && (mbap == 0)) {
                            firstGobSeen = true;
                        }
                        if (firstGobSeen) {
                            int packetStart = 4;
                            if (gobn != 0 || mbap != 0) {
                                if (sbit > 0) {
                                    for (int j = 0; j < SBIT_END[sbit]; j++) {
                                        out.write(MB_STUFFING[j]);
                                    }
                                    packet[i][4] &= SBIT_AND[sbit];
                                    packet[i][4] |= MB_STUFFING[SBIT_END[sbit]];
                                }
                            } else if (sbit > 0) {
                                for (int j = 0; j < 4; j++) {
                                    packet[i][j + 4] = (byte)
                                        (((packet[i][j + 4] & SBIT_MASK[8 - sbit]) << sbit)
                                        | ((packet[i][j + 5] >> (8 - sbit))
                                                & SBIT_MASK[sbit]));
                                }
                                packet[i][7] |= 0x1;
                                out.write(packet[i], packetStart, 4);
                                packetStart += 4;
                                for (int j = 0; j < sbit; j++) {
                                    int bite = (0x1 << (8 - j)) & 0xFF;
                                    out.write(bite);
                                }
                            }
                            packet[i][l - 1] &= EBIT_AND[ebit];
                            out.write(packet[i], packetStart, l - packetStart);

                            if (ebit > 0) {
                                for (int j = EBIT_START[ebit] + 1;
                                        j < MB_STUFFING.length; j++) {
                                    out.write(MB_STUFFING[j]);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return OUTPUT_BUFFER_NOT_FILLED;
                }
            } else {
                packetSkipped = true;
            }
        }
        if (!packetSkipped && firstGobSeen) {
            try {
                out.write(0);
                out.write(1);
                out.write(13 << 4);
                out.write(0x40);
                out.write(0x20);
                out.write(0x10);
                out.write(0x8);
                out.write(0x4);
                out.write(0x2);
                out.write(0x0);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int length = out.getCount();
            if (length > 0) {
                int width = 352;
                int height = 288;
                if ((data[3] & 0x8) == 0) {
                    width = 176;
                    height = 144;
                }
                output.setFormat(new VideoFormat(VideoFormat.H261,
                        new Dimension(width, height), Format.NOT_SPECIFIED,
                        Format.byteArray, Format.NOT_SPECIFIED));
                output.setData(data);
                output.setOffset(0);
                output.setLength(length);
                output.setTimeStamp(currentTimestamp);
                output.setSequenceNumber(noFrames);
                output.setDiscard(false);
                noFrames += 1;
                return BUFFER_PROCESSED_OK;
            }
        } else {
            output.setDiscard(true);
            return OUTPUT_BUFFER_NOT_FILLED;
        }
        currentTimestamp = -1;
        return OUTPUT_BUFFER_NOT_FILLED;
    }

    private void initQueue(long sequence, long timestamp, byte[] inData,
            int inOffset, int inLength) {
        packet = new byte[NUM_PACKETS][];
        firstSequence = sequence;
        currentTimestamp = timestamp;
        highestOffset = 0;
        lastSequence = -2;
        lastGobn = -1;
        packet[0] = new byte[inLength];
        System.arraycopy(inData, inOffset, packet[0], 0, inLength);
    }

    /**
     *
     * @see javax.media.Codec#process(javax.media.Buffer, javax.media.Buffer)
     */
    public int process(Buffer input, Buffer output) {
        byte[] inData = (byte[]) input.getData();
        int inOffset = input.getOffset();
        int inLength = input.getLength();
        long sequence = input.getSequenceNumber();
        long timestamp = input.getTimeStamp();

        H261RTPHeader header = new H261RTPHeader(inData);

        if (lastSequence == -2) {
            lastSequence = sequence - 1;
        }

        if ((sequence != (lastSequence + 1))
                || (input.getTimeStamp() != currentTimestamp)
                || (packet == null)) {
            initQueue(sequence, timestamp, inData, inOffset, inLength);
        } else if (header.getGobn() < lastGobn) {
            int result = process(output);
            initQueue(sequence, timestamp, inData, inOffset, inLength);
            return result;
        } else {
            int offset = (int) (sequence - firstSequence);
            packet[offset] = new byte[inLength];
            System.arraycopy(inData, inOffset, packet[offset], 0, inLength);
            if (offset > highestOffset) {
                highestOffset = offset;
            }
        }

        lastSequence = sequence;
        if (lastSequence >= 65536) {
            lastSequence = -1;
        }
        lastGobn = header.getGobn();

        if ((input.getFlags() & Buffer.FLAG_RTP_MARKER) != 0) {
            return process(output);
        }
        return OUTPUT_BUFFER_NOT_FILLED;
    }

    /**
     *
     * @see javax.media.Codec#setInputFormat(javax.media.Format)
     */
    public Format setInputFormat(Format format) {
        if (getSupportedOutputFormats(format).length > 0) {
            return format;
        }
        return null;
    }

    /**
     *
     * @see javax.media.Codec#setOutputFormat(javax.media.Format)
     */
    public Format setOutputFormat(Format format) {
        if (format.matches(OUTPUT_FORMAT)) {
            return format;
        }
        return null;
    }

    /**
     *
     * @see javax.media.PlugIn#close()
     */
    public void close() {
        // Does Nothing
    }

    /**
     *
     * @see javax.media.PlugIn#getName()
     */
    public String getName() {
        return "H261Depacketizer";
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
     * @see javax.media.Controls#getControl(java.lang.String)
     */
    public Object getControl(String controlType) {
        return null;
    }

    /**
     *
     * @see javax.media.Controls#getControls()
     */
    public Object[] getControls() {
        return new Object[0];
    }
}
