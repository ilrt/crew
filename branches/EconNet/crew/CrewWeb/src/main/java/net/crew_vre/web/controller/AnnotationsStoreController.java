/*
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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.crew_vre.annotations.CrewLiveAnnotation;
import net.crew_vre.annotations.liveannotationtype.LiveAnnotationTypeRepository;
import net.crew_vre.constants.CrewConstants;
import net.crew_vre.events.dao.EventDao;
import net.crew_vre.events.domain.Event;
import net.crew_vre.recordings.dao.RecordingDao;
import net.crew_vre.recordings.domain.Recording;

import org.caboto.dao.AnnotationDao;
import org.caboto.domain.Annotation;
import org.caboto.domain.AnnotationException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class AnnotationsStoreController implements Controller {

    private AnnotationDao annotationStoreDao = null;
    private AnnotationDao cabotoDao = null;
    private RecordingDao recordingDao = null;
    private EventDao eventDao = null;
    private String cabotoContext = null;
    private LiveAnnotationTypeRepository liveAnnotationTypeRepository = null;
    private String url = "http://localhost/";

    public AnnotationsStoreController(AnnotationDao annotationStoreDao,
            AnnotationDao cabotoDao, RecordingDao recordingDao,
            EventDao eventDao,
            LiveAnnotationTypeRepository liveAnnotationTypeRepository,
            String cabotoContext) {
        this.annotationStoreDao = annotationStoreDao;
        this.cabotoDao = cabotoDao;
        this.recordingDao = recordingDao;
        this.eventDao = eventDao;
        this.cabotoContext = cabotoContext;
        this.liveAnnotationTypeRepository = liveAnnotationTypeRepository;
        if (!cabotoContext.endsWith("/")) {
            cabotoContext = cabotoContext + "/";
        }
    }

    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mov = null;

        Event crewEvent = null;
        String annotatorId = request.getParameter("annotatorId");

        String userId = request.getUserPrincipal().getName();
        String serverPort = "";
        int port = request.getServerPort();
        if ((port != CrewConstants.HTTP_PORT) && (port != CrewConstants.HTTPS_PORT)) {
            serverPort = ":" + String.valueOf(port);
        }
        String author = request.getScheme() + "://" + request.getServerName()
                + serverPort + cabotoContext + "person/" + userId;
        String[] annotationIDs = request.getParameterValues("annotationId");
        // request.getParameter("personId");

        if (annotatorId != null) {
            Vector<Annotation> annVector = (Vector<Annotation>) annotationStoreDao
                    .getAnnotationsByAuthor(url + annotatorId);
            Iterator<Annotation> annIter = annVector.iterator();
            Vector<CrewLiveAnnotation> annotations = new Vector<CrewLiveAnnotation>();
            while (annIter.hasNext()) {
                try {
                    Annotation ann = annIter.next();
                    if (crewEvent == null) {
                        Recording recording = recordingDao.findRecordingByUri(ann.getAnnotates());
                        crewEvent = eventDao.findEventById(recording.getEventUri());
                    }
                    ann.setAuthor(author);
                    if (annotationIDs != null) {
                        for (int i = 0; i < annotationIDs.length; i++) {
                            if (ann.getId().equals(annotationIDs[i])) {
                                annotationStoreDao.deleteAnnotation(
                                    annotationStoreDao.findAnnotation(annotationIDs[i]));
                            }
                            cabotoDao.addAnnotation(ann);
                        }
                    } else {
                        annotations.add(new CrewLiveAnnotation(
                                liveAnnotationTypeRepository, ann));
                    }
                } catch (AnnotationException e) {
                    e.printStackTrace();
                }
            }
            if (annotationIDs == null) {
                mov = new ModelAndView("listAnnotations");
                mov.addObject("annotations", annotations);
                mov.addObject("event", crewEvent);
                mov.addObject("annotatorId", annotatorId);
            } else {
                mov = new ModelAndView("listAnnotations");
                mov.addObject("event", crewEvent);
                mov.addObject("success", true);
                mov.addObject("annotations", annotations);
                mov.addObject("event", crewEvent);
                mov.addObject("annotatorId", annotatorId);
            }
        }
        return mov;
    }

}
