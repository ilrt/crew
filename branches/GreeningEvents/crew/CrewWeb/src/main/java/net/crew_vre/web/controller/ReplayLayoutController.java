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

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.crew_vre.media.Misc;
import net.crew_vre.media.rtptype.RtpTypeRepository;
import net.crew_vre.media.screencapture.ScreenChangeDetector;
import net.crew_vre.recordings.dao.RecordingDao;
import net.crew_vre.recordings.domain.Recording;
import net.crew_vre.recordings.domain.ReplayLayout;
import net.crew_vre.recordings.domain.Stream;
import net.crew_vre.recordings.layout.LayoutPosition;
import net.crew_vre.recordings.layout.LayoutRepository;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * A controller for setting the replay layout
 * @author Tobias Schiebeck
 * @version 1.0
 */
public class ReplayLayoutController implements Controller {

    private RecordingDao recordingDao = null;

    private LayoutRepository layoutRepository = null;

    private RtpTypeRepository typeRepository = null;

    /**
     * Creates a new ReplayLayoutController
     * @param recordingDao The recording DAO
     * @param layoutRepository The layout repository
     * @param typeRepository The repository to find RTP types in
     */
    public ReplayLayoutController(RecordingDao recordingDao,
            LayoutRepository layoutRepository,
            RtpTypeRepository typeRepository) {
        this.recordingDao = recordingDao;
        this.layoutRepository = layoutRepository;
        this.typeRepository = typeRepository;
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
        String streamUri = null;
        String positionName = null;
        ReplayLayout replayLayout = new ReplayLayout(layoutRepository);
        streamUri = request.getParameter("streamUri");
        Stream outStream = null;
        if (streamUri != null) {
            System.err.println("Finding recording for stream " + streamUri);
            recording = recordingDao.findRecordingByStreamUri(streamUri);
            Iterator<Stream> streams = recording.getStreams().iterator();
            while (streams.hasNext()) {
                Stream stream = streams.next();
                if (stream.getUri().equals(streamUri)) {
                    outStream = stream;
                    break;
                }
            }
        }
        if (recording != null) {
            replayLayout.setRecording(recording);
        }
        if (request.getParameter("timestamp") != null) {
            replayLayout.setTime(Long.parseLong(
                    request.getParameter("timestamp")));
        }
        if (request.getParameter("layoutName") != null) {
           replayLayout.setName(request.getParameter("layoutName"));
        }
        if (request.getParameter("positionName") != null) {
            positionName = request.getParameter("positionName");
            replayLayout.setStream(positionName, outStream);
            LayoutPosition position = layoutRepository.findLayout(
                    replayLayout.getName()).findStreamPosition(positionName);
            if (position.hasChanges()) {
                if (!Misc.isCodecsConfigured()) {
                    Misc.configureCodecs("/knownCodecs.xml");
                }
                ScreenChangeDetector changeDetector = new ScreenChangeDetector(
                        recording.getDirectory(), outStream.getSsrc(),
                        typeRepository);
                changeDetector.run();
                changeDetector.close();
            }
        }
        replayLayout.setGraph(recording.getGraph());
        replayLayout.setUri(recording.getUri() + "/layout/"
                + replayLayout.getTime().getTime() + "/"
                + replayLayout.getName());
        recordingDao.addReplayLayout(replayLayout);

        return null;
    }

}
