/**
 * Copyright (c) 2008-2009, University of Bristol
 * Copyright (c) 2008-2009, University of Manchester
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

import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.crew_vre.constants.CrewConstants;
import net.crew_vre.recordings.dao.RecordingDao;
import net.crew_vre.recordings.domain.Recording;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 *
 * @author Tobias Schiebeck
 * @version 1.0
 */
public class RecordingUploadController implements Controller {

    private static final String URI_PREFIX =
        "http://www.crew-vre.net/recordings/";

    private RecordingDao recordingDao = null;


    /**
     * Creates a new RecordingUploadController
     * @param recordingDao The recording dao
     */
    public RecordingUploadController(RecordingDao recordingDao) {
        this.recordingDao = recordingDao;
    }

    /**
     *
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(
     *     javax.servlet.http.HttpServletRequest,
     *     javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Date startTime = new Date(0);
        Date endTime = new Date(0);
        String eventUri = null;

        if (request.getParameter("startTime") != null) {
            startTime = new Date(Long.parseLong(
                    request.getParameter("startTime")));
        }

        if (request.getParameter("endTime") != null) {
             endTime = new Date(Long.parseLong(
                     request.getParameter("endTime")));
        }

        if (request.getParameter("eventUri") != null) {
            eventUri = request.getParameter("eventUri");
        }

        String recordingId = String.valueOf(startTime.getTime())
            + (int) (Math.random() * CrewConstants.ID_NORMALIZATION);
        if (request.getParameter("id") != null) {
            recordingId = request.getParameter("id");
        }

        Recording recording = new Recording();
        recording.setStartTime(startTime);
        recording.setEndTime(endTime);
        recording.setEventUri(eventUri);
        recording.setGraph(URI_PREFIX + recordingId);
        recording.setId(recordingId);
        recording.setUri(URI_PREFIX + recordingId);

        recordingDao.addRecording(recording);
        PrintWriter out = response.getWriter();
        out.println(recording.getUri());

        return null;
    }

}
