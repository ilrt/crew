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

import java.awt.Dimension;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.Multiplexer;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PullSourceStream;

import net.crew_vre.codec.utils.ByteArrayOutputStream;

/**
 * An FLV Multiplexer
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class JavaMultiplexer implements Multiplexer, PullSourceStream {

    /**
     * The flash content type
     */
    public static final String CONTENT_TYPE = "video/x-flv";

    private static final int INT_TO_2_BIT_MASK = 0x2;

    private static final int BIT_SHIFT_4 = 4;

    private static final int FLV_VIDEO_TAG = 0x9;

    private static final int FLV_AUDIO_CHANNELS_2 = 0x1;

    private static final int AUDIO_CHANNELS_2 = 2;

    private static final int FLV_AUDIO_CHANNELS_1 = 0x0;

    private static final int AUDIO_CHANNELS_1 = 1;

    private static final int FLV_BITS_PER_SAMPLE_16 = 0x2;

    private static final int FLV_BITS_PER_SAMPLE_8 = 0x0;

    private static final int BITS_PER_SAMPLE_16 = 16;

    private static final int BITS_PER_SAMPLE_8 = 8;

    private static final int FLV_AUDIO_RATE_44000_HZ = 0xC;

    private static final int FLV_AUDIO_RATE_22000_HZ = 0x8;

    private static final int FLV_AUDIO_RATE_11000_HZ = 0x4;

    private static final int FLV_AUDIO_RATE_5500_HZ = 0x0;

    private static final int AUDIO_RATE_44000_HZ = 44100;

    private static final int AUDIO_RATE_22000_HZ = 22050;

    private static final int AUDIO_RATE_11000_HZ = 11025;

    private static final int AUDIO_RATE_5500_HZ = 5500;

    private static final int FLV_AUDIO_IMA = 0x10;

    private static final int FLV_AUDIO_TAG = 0x8;

    private static final int NANO_TO_MILLIS = 1000000;

    private static final int BIT_SHIFT_24 = 24;

    private static final int BIT_SHIFT_0 = 0;

    private static final int BIT_SHIFT_8 = 8;

    private static final int BYTE_MASK = 0xFF;

    private static final int BIT_SHIFT_16 = 16;

    private static final int DATA_OFFSET = 0x9;

    private static final int VIDEO_FLAG = 0x1;

    private static final int AUDIO_FLAG = 0x4;

    private static final int FLV_VERSION = 0x1;

    private static final int FLV_DATA_TAG = 0x12;

    private static final String ON_METADATA = "onMetaData";

    private static final byte[] FLV_TYPE = new byte[]{0x46, 0x4C, 0x56};

    private final Format[] SUPPORTED_FORMATS = new Format[]{
        new VideoFormat("flv1"),
        new AudioFormat(AudioFormat.LINEAR, AUDIO_RATE_44000_HZ, 16, 1),
        new AudioFormat(AudioFormat.LINEAR, AUDIO_RATE_22000_HZ, 16, 1),
        new AudioFormat(AudioFormat.LINEAR, AUDIO_RATE_11000_HZ, 16, 1),
        new AudioFormat(AudioFormat.LINEAR, AUDIO_RATE_44000_HZ, 8, 1),
        new AudioFormat(AudioFormat.LINEAR, AUDIO_RATE_22000_HZ, 8, 1),
        new AudioFormat(AudioFormat.LINEAR, AUDIO_RATE_11000_HZ, 8, 1),
        new AudioFormat(AudioFormat.LINEAR, AUDIO_RATE_5500_HZ, 8, 1)
    };

    // The maximum number of tracks
    private static final int MAX_TRACKS = 2;

    // The datasource that holds the data
    private FLVDataSource dataSource = new FLVDataSource(this);

    // True if this is the first packet
    private boolean firstPacket = true;

    // The formats of the tracks
    private Format[] inputFormats = new Format[0];

    // The index of the audio track
    private int audioTrack = -1;

    // The index of the video track
    private int videoTrack = -1;

    // The size of the last tag written
    private int lastTagSize = 0;

    // The last timestamp written
    private long lastTimestamp = -1;

    // True if the multiplex is done
    private boolean done = false;

    // The current buffer being processed
    private Buffer buffer = null;

    // The object to use to synchronize processing an reading
    private Integer bufferSync = new Integer(0);

    // The result of the processing
    private int result = -1;

    // The duration of the file in milliseconds or -1 if unknown
    private long duration = -1;

    // The size of the video
    private Dimension size = null;

    // The last sequence number seen
    private long lastSequence = -1;

    // True if the first video packet has been seen
    private boolean firstVideoPacketSeen = false;

    // Queue of initial audio packets to write
    private LinkedList<Buffer> audioQueue = new LinkedList<Buffer>();

    // The offset of the timestamps
    private long timestampOffset = 0;

    /**
     * Sets the offset to add to timestamps
     * @param timestampOffset The offset to add
     */
    public void setTimestampOffset(long timestampOffset) {
        this.timestampOffset = timestampOffset;
    }

    /**
     * Sets the duration of the file
     * @param duration The duration in milliseconds
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     *
     * @see javax.media.Multiplexer#getDataOutput()
     */
    public DataSource getDataOutput() {
        return dataSource;
    }

    /**
     *
     * @see javax.media.Multiplexer#getSupportedInputFormats()
     */
    public Format[] getSupportedInputFormats() {
        return SUPPORTED_FORMATS;
    }

    /**
     *
     * @see javax.media.Multiplexer#getSupportedOutputContentDescriptors(
     *     javax.media.Format[])
     */
    public ContentDescriptor[] getSupportedOutputContentDescriptors(
            Format[] inputs) {
        boolean error = false;
        for (int i = 0; i < inputs.length; i++) {
            boolean formatFound = false;
            for (int j = 0; j < SUPPORTED_FORMATS.length; j++) {
                if (inputs[i].matches(SUPPORTED_FORMATS[j])) {
                    formatFound = true;
                }
            }
            if (!formatFound) {
                error = true;
            }
        }
        if (error) {
            return new ContentDescriptor[0];
        }
        return new ContentDescriptor[]{
            new ContentDescriptor(CONTENT_TYPE)
        };
    }

    // Converts an int into a 3-byte array
    private byte[] intTo24Bits(int value) {
        return new byte[]{(byte) ((value >> BIT_SHIFT_16) & BYTE_MASK),
                          (byte) ((value >> BIT_SHIFT_8)  & BYTE_MASK),
                          (byte) ((value >> BIT_SHIFT_0)  & BYTE_MASK)};
    }

    // Returns the value of the most significant 8 bits
    private byte high8Bits(int value) {
        return (byte) ((value >> BIT_SHIFT_24) & BYTE_MASK);
    }

    private int processBuffer(Buffer buf) {
        synchronized (bufferSync) {
            while ((buffer != null) && !done) {
                try {
                    bufferSync.wait();
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }

            if (!done) {
                result = -1;
                buffer = buf;
                bufferSync.notifyAll();
            }

            while ((result == -1) && !done) {
                try {
                    bufferSync.wait();
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }

            if (done) {
                return BUFFER_PROCESSED_OK;
            }
        }
        return result;
    }

    /**
     *
     * @see javax.media.Multiplexer#process(javax.media.Buffer, int)
     */
    public int process(Buffer buf, int trk) {

        if ((videoTrack != -1) && !firstVideoPacketSeen) {
            if (buf.getFormat() instanceof AudioFormat) {
                audioQueue.addLast((Buffer) buf.clone());
                return BUFFER_PROCESSED_OK;
            } else if (buf.getFormat() instanceof VideoFormat) {
                size = ((VideoFormat) buf.getFormat()).getSize();
                while (!audioQueue.isEmpty()) {
                    processBuffer(audioQueue.removeFirst());
                }
                firstVideoPacketSeen = true;
            }
        }
        return processBuffer(buf);
    }

    /**
     *
     * @see javax.media.Multiplexer#setContentDescriptor(
     *     javax.media.protocol.ContentDescriptor)
     */
    public ContentDescriptor setContentDescriptor(
            ContentDescriptor contentDescriptor) {
        if (contentDescriptor.getContentType().equals(CONTENT_TYPE)) {
            return contentDescriptor;
        }
        return null;
    }

    /**
     *
     * @see javax.media.Multiplexer#setInputFormat(javax.media.Format, int)
     */
    public Format setInputFormat(Format format, int track) {
        boolean formatSupported = false;
        for (int i = 0; i < SUPPORTED_FORMATS.length; i++) {
            if (format.matches(SUPPORTED_FORMATS[i])) {
                formatSupported = true;
            }
        }
        if (!formatSupported) {
            return null;
        }
        if (format instanceof VideoFormat) {
            if ((videoTrack == -1) || (track == videoTrack)) {
                videoTrack = track;
                inputFormats[track] = format;
                return format;
            }
        }
        if (format instanceof AudioFormat) {
            if ((audioTrack == -1) || (track == audioTrack)) {
                audioTrack = track;
                inputFormats[track] = format;
                return format;
            }
        }
        return null;
    }

    /**
     *
     * @see javax.media.Multiplexer#setNumTracks(int)
     */
    public int setNumTracks(int tracks) {
        if (tracks > MAX_TRACKS) {
            tracks = MAX_TRACKS;
        }
        inputFormats = new Format[tracks];
        return tracks;
    }

    /**
     *
     * @see javax.media.PlugIn#close()
     */
    public void close() {
        synchronized (bufferSync) {
            done = true;
            bufferSync.notifyAll();
        }
    }

    /**
     *
     * @see javax.media.PlugIn#getName()
     */
    public String getName() {
        return "Flash Multiplexer";
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
        done = false;
        firstPacket = true;
    }

    /**
     *
     * @see javax.media.Controls#getControl(java.lang.String)
     */
    public Object getControl(String s) {
        return null;
    }

    /**
     *
     * @see javax.media.Controls#getControls()
     */
    public Object[] getControls() {
        return new Object[0];
    }

    private void writeFLVHeader(DataOutputStream out)
            throws IOException {
        int metaDataArraySize = 0;
        int metaDataLength = 18;

        // Write the FLV header
        int avFlag = 0;
        if (audioTrack != -1) {
            avFlag |= AUDIO_FLAG;
        }
        if (videoTrack != -1) {
            avFlag |= VIDEO_FLAG;
            metaDataArraySize += 2;
            metaDataLength += 33;
        }
        if (duration != -1) {
            metaDataArraySize += 1;
            metaDataLength += 19;
        }

        out.write(FLV_TYPE);
        out.write(FLV_VERSION);
        out.write(avFlag);
        out.writeInt(DATA_OFFSET);

        // Write the initial meta tag
        int startSize = out.size();

        // Header
        out.writeInt(0);
        out.write(FLV_DATA_TAG);
        out.write(intTo24Bits(metaDataLength));
        out.writeInt(0);
        out.write(intTo24Bits(0));

        // OnMetaData
        out.write(2);
        out.writeUTF(ON_METADATA);

        // ECMA Array
        out.write(8);
        out.writeInt(metaDataArraySize);

        // Width and height
        if (videoTrack != -1) {
            out.writeUTF("width");
            out.write(0);
            out.writeDouble(size.getWidth());
            out.writeUTF("height");
            out.write(0);
            out.writeDouble(size.getHeight());
        }

        // Duration
        if (duration != -1) {
            out.writeUTF("duration");
            out.write(0);
            out.writeDouble(duration / 1000.0);
        }

        lastTagSize = out.size() - startSize;
    }

    private int writeAudioHeader(int length, long timestamp,
            AudioFormat format, DataOutputStream out) throws IOException {
        out.writeInt(lastTagSize);
        int startSize = out.size();
        out.write(FLV_AUDIO_TAG);
        out.write(intTo24Bits(length + 1));
        out.write(intTo24Bits((int) timestamp));
        out.write(high8Bits((int) timestamp));
        out.write(intTo24Bits(0));
        int audioHeader = 0;
        if (format.getEncoding().equals(
                AudioFormat.IMA4)) {
            audioHeader |= FLV_AUDIO_IMA;
        } else if (format.getEncoding().equals(
                AudioFormat.LINEAR)) {
            audioHeader |= 0;
        }
        switch ((int) format.getSampleRate()) {
        case AUDIO_RATE_5500_HZ:
            audioHeader |= FLV_AUDIO_RATE_5500_HZ;
            break;

        case AUDIO_RATE_11000_HZ:
            audioHeader |= FLV_AUDIO_RATE_11000_HZ;
            break;

        case AUDIO_RATE_22000_HZ:
            audioHeader |= FLV_AUDIO_RATE_22000_HZ;
            break;

        case AUDIO_RATE_44000_HZ:
            audioHeader |= FLV_AUDIO_RATE_44000_HZ;
            break;

        default:
            break;
        }
        switch (format.getSampleSizeInBits()) {
        case BITS_PER_SAMPLE_8:
            audioHeader |= FLV_BITS_PER_SAMPLE_8;
            break;

        case BITS_PER_SAMPLE_16:
            audioHeader |= FLV_BITS_PER_SAMPLE_16;
            break;

        default:
            break;
        }
        switch (format.getChannels()) {
        case AUDIO_CHANNELS_1:
            audioHeader |= FLV_AUDIO_CHANNELS_1;
            break;

        case AUDIO_CHANNELS_2:
            audioHeader |= FLV_AUDIO_CHANNELS_2;
            break;

        default:
            break;
        }
        out.write(audioHeader);
        return out.size() - startSize;
    }

    private int writeVideoHeader(int length, long timestamp,
            VideoFormat format, DataOutputStream out) throws IOException {
        out.writeInt(lastTagSize);
        int startSize = out.size();
        out.write(FLV_VIDEO_TAG);
        out.write(intTo24Bits(length + 1));
        out.write(intTo24Bits((int) timestamp));
        out.write(high8Bits((int) timestamp));
        out.write(intTo24Bits(0));
        if (format.getEncoding().toLowerCase().equals("flv1")) {
            SorensonH263Header header =
                new SorensonH263Header(
                        (byte[]) buffer.getData(),
                        buffer.getOffset(), buffer.getLength());
            out.write(((header.getPictureType() + 1)
                    << BIT_SHIFT_4)
                    | INT_TO_2_BIT_MASK);
        }
        return out.size() - startSize;
    }

    private int writeData(Buffer buffer, ByteArrayOutputStream bytes,
            DataOutputStream out) throws IOException {
        int startSize = out.size();
        Object dataObj = buffer.getData();
        int length = buffer.getLength();
        int offset = buffer.getOffset();
        Format format = buffer.getFormat();
        int toWrite = length;
        if (format.getDataType().equals(Format.byteArray)) {
            if (bytes.getSpace() < length) {
                toWrite = bytes.getSpace();
            }
            out.write((byte[]) dataObj, offset, toWrite);
        } else if (format.getDataType().equals(Format.shortArray)) {
            if (bytes.getSpace() < (length * 2)) {
                toWrite = bytes.getSpace() / 2;
            }
            short[] data = (short[]) dataObj;
            for (int i = offset; i < (offset + toWrite); i++) {
                out.writeShort(data[i]);
            }
        } else if (format.getDataType().equals(Format.intArray)) {
            if (bytes.getSpace() < (length * 4)) {
                toWrite = bytes.getSpace() / 4;
            }
            int[] data = (int[]) buffer.getData();
            for (int i = offset; i < (offset + toWrite); i++) {
                out.writeInt(data[i]);
            }
        }
        result = BUFFER_PROCESSED_OK;
        if (toWrite != length) {
            buffer.setOffset(offset + toWrite);
            buffer.setLength(length - toWrite);
            result = INPUT_BUFFER_NOT_CONSUMED;
        }
        return out.size() - startSize;
    }

    /**
     *
     * @see javax.media.protocol.PullSourceStream#read(byte[], int, int)
     */
    public int read(byte[] buf, int off, int len) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(
                buf, off, len);
        DataOutputStream out = new DataOutputStream(bytes);

        synchronized (bufferSync) {
            while ((buffer == null) && !done) {
                try {
                    bufferSync.wait();
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }

            if (buffer != null) {

                Format format = buffer.getFormat();
                int length = buffer.getLength();

                if (firstPacket) {
                    writeFLVHeader(out);
                    firstPacket = false;
                }

                int prevTagSize = 0;
                int headerSize = 0;
                int dataSize = 0;
                if (lastSequence != buffer.getSequenceNumber()) {

                    long timestamp = buffer.getTimeStamp() / NANO_TO_MILLIS;
                    if (lastTimestamp == -1) {
                        lastTimestamp = timestamp;
                    }
                    timestamp = timestamp - lastTimestamp;
                    timestamp += timestampOffset;
                    if (format instanceof AudioFormat) {
                        headerSize = writeAudioHeader(length, timestamp,
                                (AudioFormat) format, out);
                        dataSize = writeData(buffer, bytes, out);
                    } else if (format instanceof VideoFormat) {
                        headerSize = writeVideoHeader(length, timestamp,
                                (VideoFormat) format, out);
                        dataSize = writeData(buffer, bytes, out);
                    }
                } else {
                    prevTagSize = lastTagSize;
                    dataSize = writeData(buffer, bytes, out);
                }

                lastTagSize = prevTagSize + headerSize + dataSize;
                lastSequence = buffer.getSequenceNumber();
            } else if (done) {
                out.writeInt(lastTagSize);
                result = BUFFER_PROCESSED_OK;
            }
            buffer = null;
            bufferSync.notifyAll();
        }
        out.close();
        return bytes.getCount();
    }

    /**
     *
     * @see javax.media.protocol.PullSourceStream#willReadBlock()
     */
    public boolean willReadBlock() {
        return (buffer == null) && !done;
    }

    /**
     *
     * @see javax.media.protocol.SourceStream#endOfStream()
     */
    public boolean endOfStream() {
        return done;
    }

    /**
     *
     * @see javax.media.protocol.SourceStream#getContentDescriptor()
     */
    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(CONTENT_TYPE);
    }

    /**
     *
     * @see javax.media.protocol.SourceStream#getContentLength()
     */
    public long getContentLength() {
        return LENGTH_UNKNOWN;
    }

    /**
     * Indicates that the video should be resized to the given size
     * @param size The size to resize to
     */
    public void resizeVideoTo(Dimension size) {
        SUPPORTED_FORMATS[0] = new VideoFormat("FLV1", size,
                Format.NOT_SPECIFIED, Format.byteArray, Format.NOT_SPECIFIED);
    }

}
