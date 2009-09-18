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

package net.crew_vre.recorder.dialog.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.crew_vre.constants.RecordingConstants;
import net.crew_vre.jena.vocabulary.Crew;
import net.crew_vre.recordings.domain.Stream;

import org.apache.commons.httpclient.methods.RequestEntity;

/**
 * A request entity for a series of streams
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class StreamRequestEntity implements RequestEntity {

    private static final int BUFFER_SIZE = 8196;

    private List<Stream> streams = null;

    private File directory = null;

    /**
     * Creates a new StreamRequestEntity
     * @param streams The streams to put in the entity
     * @param directory The directory where the streams are held
     */
    public StreamRequestEntity(List<Stream> streams, File directory) {
        this.streams = streams;
        this.directory = directory;
    }

    /**
     *
     * @see org.apache.commons.httpclient.methods.RequestEntity#
     *     getContentLength()
     */
    public long getContentLength() {
        return -1;
    }

    /**
     *
     * @see org.apache.commons.httpclient.methods.RequestEntity#getContentType()
     */
    public String getContentType() {
        return "application/zip";
    }

    /**
     *
     * @see org.apache.commons.httpclient.methods.RequestEntity#isRepeatable()
     */
    public boolean isRepeatable() {
        return false;
    }

    private void writeFile(File file, ZipOutputStream out) throws IOException {
        BufferedInputStream instream = new BufferedInputStream(
                new FileInputStream(file));
        byte[] indata = new byte[BUFFER_SIZE];
        int bytesRead = instream.read(indata);

        ZipEntry entry = new ZipEntry(file.getName());
        entry.setSize(file.length());
        entry.setTime(file.lastModified());
        out.putNextEntry(entry);
        while (bytesRead > 0) {
            out.write(indata, 0, bytesRead);
            bytesRead = instream.read(indata);
        }
        instream.close();
        out.closeEntry();
    }

    /**
     *
     * @see org.apache.commons.httpclient.methods.RequestEntity#writeRequest(
     *     java.io.OutputStream)
     */
    public void writeRequest(OutputStream output) throws IOException {
        ZipOutputStream out = new ZipOutputStream(output);
        for (Stream stream : streams) {
            File streamFile = new File(directory, stream.getSsrc());
            File streamIndex = new File(directory, stream.getSsrc()
                    + RecordingConstants.STREAM_INDEX);
            writeFile(streamFile, out);
            writeFile(streamIndex, out);

            Properties properties = new Properties();
            properties.put(Crew.HAS_SSRC.getLocalName(), stream.getSsrc());
            properties.put(Crew.HAS_BYTES.getLocalName(), stream.getBytes());
            properties.put(Crew.HAS_START_TIME.getLocalName(),
                    stream.getStartTime().getTime());
            properties.put(Crew.HAS_END_TIME.getLocalName(),
                    stream.getEndTime().getTime());
            properties.put(Crew.HAS_FIRST_TIMESTAMP.getLocalName(),
                    stream.getFirstTimestamp());
            properties.put(Crew.HAS_PACKETS_SEEN.getLocalName(),
                    stream.getPacketsSeen());
            properties.put(Crew.HAS_PACKETS_MISSED.getLocalName(),
                    stream.getPacketsMissed());
            properties.put(Crew.HAS_RTP_TYPE.getLocalName(),
                    stream.getRtpType().getId());
            properties.put(Crew.HAS_CNAME.getLocalName(), stream.getCname());
            properties.put(Crew.HAS_NAME.getLocalName(), stream.getName());
            properties.put(Crew.HAS_EMAIL.getLocalName(), stream.getEmail());
            properties.put(Crew.HAS_PHONE_NUMBER.getLocalName(),
                    stream.getPhone());
            properties.put(Crew.HAS_LOCATION.getLocalName(),
                    stream.getLocation());
            properties.put(Crew.HAS_TOOL.getLocalName(), stream.getTool());
            properties.put(Crew.HAS_NOTE.getLocalName(), stream.getNote());

            ZipEntry entry = new ZipEntry(stream.getSsrc()
                    + RecordingConstants.STREAM_METADATA);
            out.putNextEntry(entry);
            properties.store(out, "");
            out.closeEntry();
        }
    }

}
