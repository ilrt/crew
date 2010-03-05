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

package net.crew_vre.media.rtp;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Represents and parses an RTCP header
 *
 * @author Andrew G D Rowley
 * @version 2-0-alpha
 */
public class RTCPHeader {

    /**
     * The length of the SDES length field in bytes
     */
    public static final int SDES_LENGTH_LENGTH = 1;

    /**
     * The length of the SDES type field in bytes
     */
    public static final int SDES_TYPE_LENGTH = 1;

    /**
     * The number of bytes in an int
     */
    public static final int BYTES_PER_INT = 4;

    /**
     * The encoding of an SDES item
     */
    public static final String SDES_ENCODING = "UTF-8";

    /**
     * RTCP APP Packet
     */
    public static final short PT_APP = 204;

    /**
     * RTCP BYE Packet
     */
    public static final short PT_BYE = 203;

    /**
     * RTCP SDES Packet
     */
    public static final short PT_SDES = 202;

    /**
     * RTCP RR Packet
     */
    public static final short PT_RR = 201;

    /**
     * RTCP SR Packet
     */
    public static final short PT_SR = 200;

    /**
     * The version in place of a byte
     */
    public static final byte VERSION_BYTE = -128;

    /**
     * The current RTP version
     */
    public static final int VERSION = 2;

    /**
     * The number of bytes to skip for a SDES header
     */
    public static final int SDES_SKIP = 8;

    /**
     * An SDES CNAME header
     */
    public static final int SDES_CNAME = 1;

    /**
     * An SDES NAME header
     */
    public static final int SDES_NAME = 2;

    /**
     * An SDES EMAIL header
     */
    public static final int SDES_EMAIL = 3;

    /**
     * An SDES PHONE header
     */
    public static final int SDES_PHONE = 4;

    /**
     * An SDES LOC header
     */
    public static final int SDES_LOC = 5;

    /**
     * An SDES TOOL header
     */
    public static final int SDES_TOOL = 6;

    /**
     * An SDES NOTE header
     */
    public static final int SDES_NOTE = 7;

    /**
     * The size of the header in bytes
     */
    public static final int SIZE = 8;

    /**
     * The mask to convert an int to a byte
     */
    public static final int INT_TO_BYTE = 0xFF;

    /**
     * The shift for the first 8 bytes of a short to convert to a byte
     */
    public static final int SHORT1_TO_BYTE_SHIFT = 8;

    /**
     * The shift for the second 8 bytes of a short to convert to a byte
     */
    public static final int SHORT2_TO_BYTE_SHIFT = 0;

    /**
     * The shift for the first 8 bytes of an int to convert to a byte
     */
    public static final int INT1_TO_BYTE_SHIFT = 24;

    /**
     * The shift for the second 8 bytes of an int to convert to a byte
     */
    public static final int INT2_TO_BYTE_SHIFT = 16;

    /**
     * The shift for the third 8 bytes of an int to convert to a byte
     */
    public static final int INT3_TO_BYTE_SHIFT = 8;

    /**
     * The shift for the fourth 8 bytes of an int to convert to a byte
     */
    public static final int INT4_TO_BYTE_SHIFT = 0;

    // Header 8th position
    private static final int POS_8 = 7;

    // Header 7th position
    private static final int POS_7 = 6;

    // Header 6th position
    private static final int POS_6 = 5;

    // Header 5th position
    private static final int POS_5 = 4;

    // Header 4th position
    private static final int POS_4 = 3;

    // Header 3rd position
    private static final int POS_3 = 2;

    // Header 2nd position
    private static final int POS_2 = 1;

    // Header 1st position
    private static final int POS_1 = 0;

    // The mask for the version from a byte
    private static final int VERSION_MASK = 0xC0;

    // The shift for the version from a byte
    private static final int VERSION_SHIFT = 6;

    // The mask for the padding from a byte
    private static final int PADDING_MASK = 0x20;

    // The shift for the padding from a byte
    private static final int PADDING_SHIFT = 5;

    // The mask for the reception count from a byte
    private static final int RCOUNT_MASK = 0x1f;

    // The shift for the reception count from a byte
    private static final int RCOUNT_SHIFT = 0;

    // The first 8 bits
    private short flags;

    // The type of the packet
    private short type;

    // The second 16 bits
    private int length;

    // The third and fourth 16 bits
    private long ssrc;

    /**
     * Creates a new RTCPHeader
     *
     * @param packet
     *            The packet from which to parse the header
     * @throws IOException
     */
    public RTCPHeader(DatagramPacket packet) throws IOException {
        this(packet.getData(), packet.getOffset(), packet.getLength());
    }

    /**
     * Creates a new RTCPHeader
     *
     * @param data
     *            The data to read the header from
     * @param offset
     *            The offset in the data to start
     * @param length
     *            The length of the data to read
     * @throws IOException
     */
    public RTCPHeader(byte[] data, int offset, int length) throws IOException {
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(
                data, offset, length));

        // Read the header values
        this.flags = (short) stream.readUnsignedByte();
        this.type = (short) stream.readUnsignedByte();
        this.length = stream.readUnsignedShort();
        this.ssrc = stream.readInt() & RTPHeader.UINT_TO_LONG_CONVERT;
    }

    /**
     * Creates a new RTCP Header
     * @param padding True if there is to be padding
     * @param rc The reception count
     * @param type The type of the packet
     * @param length The length of the packet
     * @param ssrc The ssrc of the packet
     */
    public RTCPHeader(boolean padding, int rc, short type, int length,
            long ssrc) {
        flags = (byte) ((VERSION << VERSION_SHIFT) & VERSION_MASK);
        if (padding) {
            flags |= (byte) PADDING_MASK;
        }
        flags |= (byte) ((rc << RCOUNT_SHIFT) & RCOUNT_MASK);
        this.type = type;
        this.length = length;
        this.ssrc = ssrc;
    }

    /**
     * Returns the header flags
     * @return The header flags
     */
    public int getFlags() {
        return flags;
    }

    /**
     * Returns the RTP version number
     * @return The RTP version implemented
     */
    public short getVersion() {
        return (short) ((getFlags() & VERSION_MASK) >> VERSION_SHIFT);
    }

    /**
     * Returns true if there is padding
     * @return true if the padding flag is set
     */
    public boolean isPadding() {
        return ((getFlags() & PADDING_MASK) >> PADDING_SHIFT) > 0;
    }

    /**
     * Return the RC Header field
     * @return The number of reception blocks in the packet
     */
    public short getReceptionCount() {
        return (short) ((getFlags() & RCOUNT_MASK) >> RCOUNT_SHIFT);
    }

    /**
     * Returns the RTCP packet type
     * @return The type of the RTCP packet (SR or RR)
     */
    public short getPacketType() {
        return type;
    }

    /**
     * Returns the length of the packet
     * @return The length of the RTCP packet
     */
    public int getLength() {
        return length;
    }

    /**
     * Returns the first ssrc
     * @return The ssrc being described
     */
    public long getSsrc() {
        return ssrc;
    }

    /**
     * Returns the bytes that make up the header
     * @return the header as a byte array
     */
    public byte[] getBytes() {
        byte[] header = new byte[SIZE];
        header[POS_1] = (byte) (flags & INT_TO_BYTE);
        header[POS_2] = (byte) (type & INT_TO_BYTE);
        header[POS_3] = (byte) ((length >> SHORT1_TO_BYTE_SHIFT) & INT_TO_BYTE);
        header[POS_4] = (byte) ((length >> SHORT2_TO_BYTE_SHIFT) & INT_TO_BYTE);
        header[POS_5] = (byte) ((ssrc >> INT1_TO_BYTE_SHIFT) & INT_TO_BYTE);
        header[POS_6] = (byte) ((ssrc >> INT2_TO_BYTE_SHIFT) & INT_TO_BYTE);
        header[POS_7] = (byte) ((ssrc >> INT3_TO_BYTE_SHIFT) & INT_TO_BYTE);
        header[POS_8] = (byte) ((ssrc >> INT4_TO_BYTE_SHIFT) & INT_TO_BYTE);
        return header;
    }

    /**
     * Adds the header to an array
     * @param header The array to add the bytes to
     * @param offset The offset in the array to add the bytes to
     */
    public void addBytes(byte[] header, int offset) {
        header[offset + POS_1] = (byte) (flags & INT_TO_BYTE);
        header[offset + POS_2] = (byte) (type & INT_TO_BYTE);
        header[offset + POS_3] =
            (byte) ((length >> SHORT1_TO_BYTE_SHIFT) & INT_TO_BYTE);
        header[offset + POS_4] =
            (byte) ((length >> SHORT2_TO_BYTE_SHIFT) & INT_TO_BYTE);
        header[offset + POS_5] =
            (byte) ((ssrc >> INT1_TO_BYTE_SHIFT) & INT_TO_BYTE);
        header[offset + POS_6] =
            (byte) ((ssrc >> INT2_TO_BYTE_SHIFT) & INT_TO_BYTE);
        header[offset + POS_7] =
            (byte) ((ssrc >> INT3_TO_BYTE_SHIFT) & INT_TO_BYTE);
        header[offset + POS_8] =
            (byte) ((ssrc >> INT4_TO_BYTE_SHIFT) & INT_TO_BYTE);
    }

    /**
     * Returns the RTCP Length from the length in bytes
     * @param length The length in bytes
     * @return The length in 32-bit words - 1
     */
    public static int getLength(int length) {
        return (length / BYTES_PER_INT) - 1;
    }
}
