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

import java.util.HashMap;
import java.util.List;

import net.crew_vre.constants.CrewConstants;
import net.crew_vre.recordings.dao.RecordingDao;
import net.crew_vre.recordings.domain.Recording;
import net.crew_vre.recordings.domain.ReplayLayout;
import net.crew_vre.recordings.layout.LayoutPosition;
import net.crew_vre.recordings.layout.LayoutRepository;
import net.crew_vre.web.facade.DisplayRecordingFacade;
import net.crew_vre.web.history.BrowseHistory;

import org.caboto.dao.AnnotationDao;
import org.caboto.domain.Annotation;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayRecordingController.java 1104 2009-03-17 13:34:17Z
 *           tschiebeck $
 */
public class DisplayRecordingController extends BrowseHistory implements Controller {

    private RecordingDao recordingDao = null;
    private AnnotationDao annotationDao = null;
    private LayoutRepository layoutRepository = null;

    public void setDisplayRecordingFacade(
            DisplayRecordingFacade displayRecordingFacade) {
        this.displayRecordingFacade = displayRecordingFacade;
    }

    public DisplayRecordingController(RecordingDao recordingDao,
            AnnotationDao annotationDao, LayoutRepository layoutRepository) {
        this.recordingDao = recordingDao;
        this.annotationDao = annotationDao;
        this.layoutRepository = layoutRepository;
    }

    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String eventId = null;
        String annotationId = null;
        String recordingId = null;
        int width = CrewConstants.CREW_PLAYER_WIDTH;
        int height = CrewConstants.CREW_PLAYER_HEIGHT;
        long startTime = 0;
        Recording recording = null;
        int noAnn = 1;

        if (request.getParameter("startTime") != null) {
            startTime = Long.parseLong(request.getParameter("startTime"));
        }

        if (request.getParameter("recordingId") != null) {
            recordingId = request.getParameter("recordingId");
            recording = recordingDao.findRecordingById(recordingId);
        }

        if (request.getParameter("annotationId") != null) {
            annotationId = request.getParameter("annotationId");
            Annotation annotation = annotationDao.getAnnotation(annotationId);
            if (!annotation.getType().equals("LiveAnnotation")) {
                recording = recordingDao.findRecordingByUri(annotation.getAnnotates());
                startTime = (annotation.getCreated().getTime()
                        - recording.getStartTime().getTime());
            }
        }

        if (recording == null) {
            throw (new RuntimeException("recording not found"));
        }

        recordingId = recording.getId();
        eventId = recording.getEventUri();

        List<Annotation> annotations = annotationDao.getAnnotations(recording
                .getUri());
        HashMap<String, String> atypes = new HashMap<String, String>();
        for (Annotation annotation : annotations) {
            if (annotation.getType().equals("LiveAnnotation")) {
                String type = annotation.getBody().get("liveAnnotationType");
                if (!type.equals(CrewConstants.SLIDE_TYPE)) {
                    atypes.put(type, type);
                }
            }
        }
        noAnn = atypes.size() + 1;
        noAnn *= CrewConstants.CREW_PLAYER_ANNOTATION_LINE_HEIGHT;
        List<ReplayLayout> replayLayouts = recording.getReplayLayouts();
        if (replayLayouts != null) {
            width = 0;
            height = 0;
        }
        for (ReplayLayout replayLayout : replayLayouts) {
            List<LayoutPosition> layoutPositions = layoutRepository.findLayout(
                    replayLayout.getName()).getStreamPositions();
            for (LayoutPosition layoutPosition : layoutPositions) {
                int w = layoutPosition.getWidth() + layoutPosition.getX();
                int h = layoutPosition.getHeight() + layoutPosition.getY();
                if (layoutPosition.getName().equals("Slider")) {
                    h = noAnn + layoutPosition.getY();
                }
                width = (w > width) ? w : width;
                height = (h > height) ? h : height;
            }
        }
        System.out.println("size: " + width + " x " + height);

        ModelAndView mov = new ModelAndView("displayRecording");
        mov.addObject("eventId", eventId);
        mov.addObject("recordingId", recordingId);
        mov.addObject("startTime", startTime);
        mov.addObject("playerWidth", width);
        mov.addObject("playerHeight", height);
        return mov;
    }

    private DisplayRecordingFacade displayRecordingFacade;

}
