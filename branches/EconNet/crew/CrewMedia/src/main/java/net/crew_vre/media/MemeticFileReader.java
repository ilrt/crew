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

package net.crew_vre.media;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;

import net.crew_vre.constants.RecordingConstants;
import net.crew_vre.media.rtp.RTPHeader;
import net.crew_vre.media.rtptype.RTPType;
import net.crew_vre.media.rtptype.RtpTypeRepository;

/**
 * Provides standard methods for Memetic file reading
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class MemeticFileReader {

    private static final int BUFFER_SIZE = 10000;

    // The number of usecs per ms
    private static final int USECS_PER_MS = 1000;

    // The number of ms per second
    private static final int MS_PER_SEC = 1000;

    private static final HashMap<Integer, Integer> PREFRAME_MAP = new HashMap<Integer, Integer>();

    private DataInputStream input = null;

    private FileInputStream fileInput = null;

    private FileChannel channel = null;

    private byte[] data = new byte[BUFFER_SIZE];

    private int length = 0;

    private long firstOffset = 0;

    private long offset = 0;

    private long offsetShift = 0;

    private long sequence = 0;

    private long lastSequence = -1;

    private long timestamp = 0;

    private int flags = 0;

    private int type = 0;

    private String filename = null;

    private long firstTimestamp = -1;

    private long lastTimestamp = -1;

    private long maxTimestamp = -1;

    private long noTimestampLoops = 0;

    private long timestampOffset = 0;

    private long startTime = 0;

    private RTPHeader header = null;

    // A vector of positions of packets in the stream file
    private Vector<Long> packetPositions = new Vector<Long>();

    // A vector of offsets of packets in the stream file
    private Vector<Long> packetOffsets = new Vector<Long>();

    // An RTP type repository
    private RtpTypeRepository typeRepository = null;

    /**
     * Creates a new MemeticFileReader
     *
     * @param filename
     *            The file from which to read
     * @throws IOException
     */
    public MemeticFileReader(String filename, RtpTypeRepository typeRepository)
            throws IOException {
        this.filename = filename;
        this.typeRepository = typeRepository;
        if (!new File(filename).isFile()) {
            return;
        }
        fileInput = new FileInputStream(filename);
        input = new DataInputStream(fileInput);
        channel = fileInput.getChannel();
        readHeader();
        readIndexFile(filename);
        System.err.println("Positioning " + filename + " at start");
        streamSeek(0);
        readNextPacket();
        offsetShift = 0;
        if (PREFRAME_MAP.containsKey(type)) {
            offsetShift = PREFRAME_MAP.get(type);
        }
        Format format = getFormat();
        if (format != null) {
            if (format instanceof VideoFormat) {
                maxTimestamp = 4294967296000000000L / 90000;
            } else if (format instanceof AudioFormat) {
                AudioFormat audioFormat = (AudioFormat) format;
                maxTimestamp = (long) (4294967296000000000L / audioFormat.getSampleRate());
            }
        } else {
            maxTimestamp = 4294967296L;
        }
    }

    public boolean readNextPacket() throws IOException {
        // Find the nearest frame after the offset
        boolean read = false;
        try {
            while (!read) {
                length = input.readShort() & RTPHeader.USHORT_TO_INT_CONVERT;
                int type = input.readShort() & RTPHeader.USHORT_TO_INT_CONVERT;
                offset = input.readInt() & RTPHeader.UINT_TO_LONG_CONVERT;

                if ((type != RecordingConstants.RTP_PACKET)
                        || (length < RTPHeader.SIZE)) {
                    channel.position(channel.position() + length);
                } else {
                    if (data.length < length) {
                        data = new byte[length];
                    }
                    input.readFully(data, 0, length);

                    flags = 0;
                    header = new RTPHeader(data, 0, length);
                    this.type = header.getPacketType();
                    timestamp = header.getTimestamp();
                    sequence = header.getSequence();
                    if (lastSequence == -1) {
                        lastSequence = sequence - 1;
                    }
                    if (sequence < lastSequence) {
                        if (((lastSequence + 100) > 65535) && (sequence < 100)) {
                            lastSequence = 0 - (65536 - sequence);
                        }
                    }
                    if (sequence > lastSequence) {
                        Format format = getFormat();
                        if (format != null) {
                            if (format instanceof VideoFormat) {
                                timestamp = (timestamp * 1000000000L) / 90000;
                            } else if (format instanceof AudioFormat) {
                                AudioFormat audioFormat = (AudioFormat) format;
                                timestamp = (long) ((timestamp * 1000000000L) / audioFormat
                                        .getSampleRate());
                            }
                        } else {
                            flags = Buffer.FLAG_RTP_TIME;
                        }

                        if (lastTimestamp == -1) {
                            lastTimestamp = timestamp;
                        }
                        if (firstTimestamp == -1) {
                            firstTimestamp = timestamp;
                            noTimestampLoops = 0;
                            System.err.println(filename + ": timestamp = "
                                    + timestamp + " first = " + firstTimestamp
                                    + " offset = " + firstOffset + " tsOff = "
                                    + timestampOffset + " loops = "
                                    + noTimestampLoops + " max = "
                                    + maxTimestamp);
                        }

                        if (timestamp < lastTimestamp) {
                            noTimestampLoops += 1;
                        }

                        lastTimestamp = timestamp;
                        lastSequence = sequence;
                        read = true;
                    }
                }
            }
        } catch (EOFException e) {
            // Do Nothing
            System.err.println("End of " + filename);
        }
        return read;
    }

    /**
     * Searches through the stream for the first packet after a given time
     *
     * @param seek
     *            The time to seek to
     * @throws IOException
     */
    public void streamSeek(long seek) throws IOException {
        System.err.println("Seeking to " + seek);
        int offsetPos = Collections.binarySearch(packetOffsets, seek);
        if (offsetPos < 0) {
            offsetPos = (offsetPos + 1) * -1;
        }

        if (offsetPos >= packetPositions.size()) {
            channel.position(new File(filename).length());
            System.err.println(filename + " seek beyond end of file");
        } else {
            channel.position(packetPositions.get(offsetPos));
            firstTimestamp = -1;
            lastTimestamp = -1;
            lastSequence = -1;
            firstOffset = (packetOffsets.get(offsetPos) - seek) * 1000000;
            System.err.println(filename + " first Offset = " + firstOffset
                    + " pos = " + packetOffsets.get(offsetPos));
        }
    }

    protected int getNoPacketsBetween(long start, long end) {
        int startPos = Collections.binarySearch(packetOffsets, start);
        if (startPos < 0) {
            startPos = (-1 * startPos) + 1;
        }
        int endPos = Collections.binarySearch(packetOffsets, end);
        if (endPos < 0) {
            endPos = (-1 * endPos) + 1;
        }
        return endPos - startPos;
    }

    protected long rewindBy(int noFrames) throws IOException {
        long position = channel.position();
        int pos = Collections.binarySearch(packetPositions, position);
        if (pos < 0) {
            pos = (-1 * pos) + 1;
        }
        if (pos >= packetPositions.size()) {
            pos = packetPositions.size() - 1;
        }
        pos -= noFrames;
        if (pos < 0) {
            pos = 0;
        }
        channel.position(packetPositions.get(pos));
        return packetOffsets.get(pos);
    }

    protected long getTimestampAt(long seek) throws IOException {
        System.err.println("Finding timestamp for " + filename);
        streamSeek(seek);
        readNextPacket();
        return getTimestamp();
    }

    private void readIndexFile(String streamSpec) {
        try {

            // Open the index file
            String filename = streamSpec + RecordingConstants.STREAM_INDEX;
            if (!new File(filename).exists()) {
                filename = streamSpec + RecordingConstants.STREAM_INDEX2;
            }
            DataInputStream indexFile = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(filename)));

            try {
                while (true) {
                    long off = indexFile.readLong();
                    long pos = indexFile.readLong();

                    if ((packetOffsets.size() == 0)
                            || (off > packetOffsets
                                    .get(packetOffsets.size() - 1))) {
                        packetOffsets.add(new Long(off));
                        packetPositions.add(new Long(pos));
                    }
                }
            } catch (EOFException e) {
                indexFile.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readHeader() throws IOException {
        long seconds = (input.readInt() & RTPHeader.UINT_TO_LONG_CONVERT);
        long uSeconds = (input.readInt() & RTPHeader.UINT_TO_LONG_CONVERT);
        startTime = (seconds * MS_PER_SEC) + (uSeconds / USECS_PER_MS);
    }

    public long getOffset() {
        return offset;
    }

    public long getOffsetShift() {
        return offsetShift;
    }

    public Format getFormat() {
        RTPType rtptype = typeRepository.findRtpType(type);
        if (rtptype != null) {
            return rtptype.getFormat();
        }
        return null;
    }

    public long getTimestamp() {
        return (timestamp - firstTimestamp) + timestampOffset
                + (noTimestampLoops * maxTimestamp);
    }

    public long getStartTime() {
        return startTime;
    }

    public void setTimestampOffset(long timestampOffset) {
        this.timestampOffset = timestampOffset;
    }

    public byte[] getData() {
        return data;
    }

    public Buffer getBuffer() {

        // Read the frame into the buffer
        Buffer inputBuffer = new Buffer();
        inputBuffer.setData(data);
        inputBuffer.setOffset(RTPHeader.SIZE);
        inputBuffer.setLength(length - RTPHeader.SIZE);
        inputBuffer.setTimeStamp(getTimestamp());
        inputBuffer.setSequenceNumber(header.getSequence());
        if (header.getMarker() == 1) {
            flags |= Buffer.FLAG_RTP_MARKER;
        }
        inputBuffer.setFlags(flags);
        inputBuffer.setFormat(getFormat());
        return inputBuffer;
    }

    protected String getFilename() {
        return filename;
    }

    /**
     * Closes the reader
     *
     */
    public void close() {
        try {
            input.close();
            fileInput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = null;
    }
}
