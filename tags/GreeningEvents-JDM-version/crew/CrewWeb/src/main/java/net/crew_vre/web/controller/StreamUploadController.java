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

package net.crew_vre.web.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.crew_vre.constants.RecordingConstants;
import net.crew_vre.jena.vocabulary.Crew;
import net.crew_vre.media.rtptype.RtpTypeRepository;
import net.crew_vre.recordings.dao.RecordingDao;
import net.crew_vre.recordings.domain.Recording;
import net.crew_vre.recordings.domain.Stream;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * A controller for uploading streams
 *
 * @author Tobias Schiebeck
 * @version 1.0
 */
public class StreamUploadController implements Controller {

    // The size of the buffer
    private static final int BUFFER_SIZE = 8196;

    private RecordingDao recordingDao = null;

    private RtpTypeRepository rtpTypeRepository = null;

    /**
     * Creates a new StreamUploadController
     * @param recordingDao The recording DAO
     * @param rtpTypeRepository The type repository
     */
    public StreamUploadController(RecordingDao recordingDao,
            RtpTypeRepository rtpTypeRepository) {
        this.recordingDao = recordingDao;
        this.rtpTypeRepository = rtpTypeRepository;
    }

    private Stream getProperties(Properties properties) {
        Stream stream = new Stream(rtpTypeRepository);

        String value = null;

        // Setup the session
        value = properties.getProperty(Crew.HAS_SSRC.getLocalName());
        if (value != null) {
            stream.setSsrc(value);
        }
        value = properties.getProperty(Crew.HAS_BYTES.getLocalName());
        if (value != null) {
            stream.setBytes(Long.parseLong(value));
        }
        value = properties.getProperty(
                Crew.HAS_START_TIME.getLocalName());
        if (value != null) {
            stream.setStartTime(Long.parseLong(value));
        }
        value = properties.getProperty(
                Crew.HAS_END_TIME.getLocalName());
        if (value != null) {
            stream.setEndTime(Long.parseLong(value));
        }
        value = properties.getProperty(
                Crew.HAS_FIRST_TIMESTAMP.getLocalName());
        if (value != null) {
            stream.setFirstTimestamp(Long.parseLong(value));
        }
        value = properties.getProperty(
                Crew.HAS_PACKETS_SEEN.getLocalName());
        if (value != null) {
            stream.setPacketsSeen(Long.parseLong(value));
        }
        value = properties.getProperty(
                Crew.HAS_PACKETS_MISSED.getLocalName());
        if (value != null) {
            stream.setPacketsMissed(Long.parseLong(value));
        }
        value = properties.getProperty(
                Crew.HAS_RTP_TYPE.getLocalName());
        if (value != null) {
            stream.setRtpType(Integer.parseInt(value));
        }
        value = properties.getProperty(Crew.HAS_CNAME.getLocalName());
        if (value != null) {
            stream.setCname(value);
        }
        value = properties.getProperty(Crew.HAS_NAME.getLocalName());
        if (value != null) {
            stream.setName(value);
        }
        value = properties.getProperty(Crew.HAS_EMAIL.getLocalName());
        if (value != null) {
            stream.setEmail(value);
        }
        value = properties.getProperty(
                Crew.HAS_PHONE_NUMBER.getLocalName());
        if (value != null) {
            stream.setPhone(value);
        }
        value = properties.getProperty(
                Crew.HAS_LOCATION.getLocalName());
        if (value != null) {
            stream.setLocation(value);
        }
        value = properties.getProperty(Crew.HAS_TOOL.getLocalName());
        if (value != null) {
            stream.setTool(value);
        }
        value = properties.getProperty(Crew.HAS_NOTE.getLocalName());
        if (value != null) {
            stream.setNote(value);
        }
        return stream;
    }

    /**
     *
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(
     *     javax.servlet.http.HttpServletRequest,
     *     javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Recording recording = null;
        ZipInputStream inZip = null;

        if (ServletFileUpload.isMultipartContent(request)) {
            ServletFileUpload servletFileUpload =
                new ServletFileUpload(new DiskFileItemFactory());
            try {
                List< ? > fileItemsList =
                    servletFileUpload.parseRequest(request);
                Iterator< ? > it = fileItemsList.iterator();
                while (it.hasNext()) {
                    FileItem fileItem = (FileItem) it.next();
                    if (fileItem.getFieldName().equals("recordingUri")) {
                        String recordinguri = fileItem.getString();
                        recording = recordingDao.findRecordingByUri(
                                recordinguri);
                    } else if (fileItem.getFieldName().equals("streams")) {
                        inZip = new ZipInputStream(fileItem.getInputStream());
                    }
                }
            } catch (FileUploadException e) {
                e.printStackTrace();
            }
        }
        String outDir = recording.getDirectory();
        File dir = new File(outDir);

        // Work out the directory
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.exists()) {
            return null;
        }
        ZipEntry entry = null;

        Vector<String> allFiles = new Vector<String>();
        Vector<String> streams = new Vector<String>();
        Vector<String> indices = new Vector<String>();
        HashMap<String, Stream> metadata = new HashMap<String, Stream>();

        while ((entry = inZip.getNextEntry()) != null) {
            String name = entry.getName();
            System.err.println("Reading " + name);
            if (name.endsWith(RecordingConstants.STREAM_METADATA)) {
                String ssrc = name.substring(0, name.indexOf(
                        RecordingConstants.STREAM_METADATA));
                Properties properties = new Properties();
                properties.load(inZip);
                Stream stream = getProperties(properties);
                stream.setUri(recording.getUri() + "/stream/"
                        + stream.getSsrc());
                stream.setGraph(recording.getGraph());
                stream.setRecording(recording);
                metadata.put(ssrc, stream);
                if (!allFiles.contains(ssrc)) {
                    allFiles.add(ssrc);
                }
            } else {
                BufferedInputStream input = new BufferedInputStream(inZip);
                FileOutputStream output =
                    new FileOutputStream(new File(outDir, name));
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = 0;
                long totalBytes = 0;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }
                output.close();
                if (name.endsWith(RecordingConstants.STREAM_INDEX)) {
                    String ssrc = name.substring(0, name.indexOf(
                            RecordingConstants.STREAM_INDEX));
                    indices.add(ssrc);
                    if (!allFiles.contains(ssrc)) {
                        allFiles.add(ssrc);
                    }
                } else {
                    streams.add(name);
                    if (!allFiles.contains(name)) {
                        allFiles.add(name);
                    }
                }
            }
        }
        boolean uploadSuccess = true;
        String result = null;
        for (String ssrc : allFiles) {
            Stream stream = metadata.get(ssrc);
            if (stream == null) {
                result = "Missing metadata for stream " + ssrc;
                uploadSuccess = false;
                break;
            }

            if (!streams.contains(ssrc)) {
                result = "Missing stream for " + ssrc;
                uploadSuccess = false;
                break;
            }

            if (!indices.contains(ssrc)) {
                result = "Missing index for stream " + ssrc;
                uploadSuccess = false;
                break;
            }
        }

        if (!uploadSuccess) {
            for (String name : indices) {
                File file = new File(outDir,
                        name + RecordingConstants.STREAM_INDEX);
                file.delete();
            }
            for (String name : streams) {
                File file = new File(outDir, name);
                file.delete();
            }

            response.sendError(HttpServletResponse.SC_PARTIAL_CONTENT,
                    result);
        } else {
            PrintWriter out = response.getWriter();
            for (String ssrc : streams) {
                Stream stream = metadata.get(ssrc);
                recordingDao.addStream(stream);
                out.println(stream.getUri());
            }
        }

        return null;
    }

}
