/*
 * @(#)ImageExtractorServlet.java
 * Created: 14 Nov 2007
 * Version: 1.0
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
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
 */

package net.crew_vre.web.servlet.flv;

import java.awt.Dimension;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.crew_vre.media.Misc;
import net.crew_vre.media.rtptype.RtpTypeRepository;
import net.crew_vre.media.rtptype.impl.RtpTypeRepositoryXmlImpl;

/**
 * A servlet for streaming out in FLV format
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class FlvServlet extends HttpServlet {

    private static final Pattern PATTERN = Pattern.compile(
            "/([^\\.].*).flv");

    private static final double DEFAULT_GENERATION_SPEED = 1.5;

    private static final String EXT = ".yuv.zip";

    private static final int EXT_LEN = EXT.length();

    private File recordingDirectory = null;

    private RtpTypeRepository rtpTypeRepository = null;


    /**
     *
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException {
        String path = getInitParameter("recordingDirectory");
        if (path == null) {
            throw new ServletException("recordingDirectory must be specified");
        }
        recordingDirectory = new File(path);
        String typeRepositoryfile = getInitParameter("rtpTypeRepository");
        if (typeRepositoryfile == null) {
            throw new ServletException("rtpTypeRepository must be specified");
        }
        try {
            rtpTypeRepository = new RtpTypeRepositoryXmlImpl(typeRepositoryfile);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        try {
            if (!Misc.isCodecsConfigured()) {
                Misc.configureCodecs("/knownCodecs.xml");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     *
     * @see javax.servlet.http.HttpServlet#doPost(
     *     javax.servlet.http.HttpServletRequest,
     *     javax.servlet.http.HttpServletResponse)
     */
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

    /**
     *
     * @throws IOException
     * @throws IOException
     * @see javax.servlet.http.HttpServlet#doGet(
     *     javax.servlet.http.HttpServletRequest,
     *     javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        String requestName = request.getPathInfo();
        Matcher matcher = PATTERN.matcher(requestName);
        String sessionId = null;
        File videoFile = null;
        if (matcher.matches()) {
            sessionId = matcher.group(1);
        } else {
            throw new IOException("Path format incorrect");
        }

        String off = request.getParameter("start");
        if (off == null) {
            off = "0.0";
        }
        long offset = (long) (Double.parseDouble(off) * 1000);
        String dur = request.getParameter("duration");
        long duration = (long) (Double.parseDouble(dur) * 1000);
        String videoStream = request.getParameter("video");
        String[] audioStreams = request.getParameterValues("audio");
        if (audioStreams == null) {
            audioStreams = new String[0];
        }
        String[] syncStreams = request.getParameterValues("sync");
        if (syncStreams == null) {
            syncStreams = new String[0];
        }
        File path = new File(recordingDirectory, sessionId);

        System.out.println("Recordings from " + path.getAbsolutePath());
        videoFile = new File(path, videoStream);
        for (int i = 0; i < audioStreams.length; i++) {
            audioStreams[i] = new File(path, audioStreams[i]).getAbsolutePath();
        }
        for (int i = 0; i < syncStreams.length; i++) {
            syncStreams[i] = new File(path, syncStreams[i]).getAbsolutePath();
        }

        String firstFrame = request.getParameter("firstframe");

        Dimension size = null;
        String width = request.getParameter("width");
        String height = request.getParameter("height");
        if ((width != null) && (height != null)) {
            size = new Dimension(Integer.parseInt(width),
                    Integer.parseInt(height));
        }

        double generationSpeed = DEFAULT_GENERATION_SPEED;
        String genSpeed = request.getParameter("genspeed");
        if (genSpeed != null) {
            generationSpeed = Double.parseDouble(genSpeed);
        }

        try {
            VideoExtractor extractor = new VideoExtractor(
                    videoFile.getAbsolutePath(),
                    audioStreams, syncStreams, size, rtpTypeRepository);
            extractor.setGenerationSpeed(generationSpeed);
            response.setContentType("video/x-flv");
            response.setStatus(HttpServletResponse.SC_OK);
            if ((firstFrame == null) || !firstFrame.equals("true")) {

                // Search for a file with name <videoStream>_<time>.yuv.zip
                // where <time> is <= offset
                File frameFile = null;
                File[] files = path.listFiles(
                        new StreamFileFilter(videoStream));
                if (files==null) {
                    throw new IOException("Recording does not exist in Directory: "+ path.getAbsolutePath());
                }
                if (files.length > 0) {
                    long[] times = new long[files.length];
                    for (int i = 0; i < times.length; i++) {
                        String fName = files[i].getName();
                        String fTime = fName.substring(videoStream.length() + 1,
                                fName.length() - EXT_LEN);
                        times[i] = Long.parseLong(fTime);
                    }
                    Arrays.sort(times);
                    int pos = Arrays.binarySearch(times, offset);
                    if (pos < 0) {
                        pos = -pos - 1;
                        if (pos >= times.length) {
                            pos = times.length - 1;
                        }
                    }
                    frameFile = new File(path, videoStream + "_"
                            + times[pos] + EXT);
                }
                // Generate the stream
                extractor.transferToStream(response.getOutputStream(), offset,
                        duration, 0, frameFile);
            } else {
                extractor.transferFirstFrame(response.getOutputStream(),
                        duration);
            }
            response.flushBuffer();
        } catch (EOFException e) {
            System.err.println("User disconnected");
        } catch (SocketException e) {
            System.err.println("User disconnected");
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    private class StreamFileFilter implements FileFilter {

        private String streamName = null;

        private StreamFileFilter(String streamName) {
            this.streamName = streamName + "_";
        }

        /**
         *
         * @see java.io.FileFilter#accept(java.io.File)
         */
        public boolean accept(File pathname) {
            if (pathname.getName().startsWith(streamName)
                    && pathname.getName().endsWith(EXT)) {
                return true;
            }
            return false;
        }

    }
}
