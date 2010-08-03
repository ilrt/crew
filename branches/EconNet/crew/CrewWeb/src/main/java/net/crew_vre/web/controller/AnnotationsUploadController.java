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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.crew_vre.annotations.CrewLiveAnnotation;
import net.crew_vre.annotations.liveannotationtype.LiveAnnotationTypeRepository;
import net.crew_vre.constants.CrewConstants;
import net.crew_vre.events.dao.EventDao;
import net.crew_vre.events.domain.Event;
import net.crew_vre.recordings.dao.RecordingDao;
import net.crew_vre.recordings.domain.Recording;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.caboto.dao.AnnotationDao;
import org.caboto.domain.Annotation;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class AnnotationsUploadController implements Controller {

    private AnnotationDao annotationStoreDao = null;
    private RecordingDao recordingDao = null;
    private EventDao eventDao = null;
    private String mailServer = "mailrouter.mcc.ac.uk";
    private String mailFrom = "CREW-DEV@listserv.manchester.ac.uk";
    private String url = "http://localhost/";
    private LiveAnnotationTypeRepository liveAnnotationTypeRepository = null;

    public AnnotationsUploadController(
            LiveAnnotationTypeRepository liveAnnotationTypeRepository,
            AnnotationDao annotationStoreDao, RecordingDao recordingDao,
            EventDao eventDao, String mailServer, String mailFrom) {
        this.liveAnnotationTypeRepository = liveAnnotationTypeRepository;
        this.annotationStoreDao = annotationStoreDao;
        this.recordingDao = recordingDao;
        this.eventDao = eventDao;
        this.mailServer = mailServer;
        this.mailFrom = mailFrom;
    }

    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String server = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort() + request.getContextPath();
        String sendEmail = null;
        Recording recording = null;
        long sessionOffset = 0;
        BufferedReader in = null;
        HashMap<String, String> keymap = new HashMap<String, String>();
        if (ServletFileUpload.isMultipartContent(request)) {
            ServletFileUpload servletFileUpload = new ServletFileUpload(
                    new DiskFileItemFactory());
            try {
                List< ? > fileItemsList = servletFileUpload.parseRequest(request);
                Iterator< ? > it = fileItemsList.iterator();
                while (it.hasNext()) {
                    FileItem fileItem = (FileItem) it.next();
                    if (fileItem.isFormField()) {
                        if (fileItem.getFieldName().equals("recordingUri")) {
                            String recordingUri = fileItem.getString();
                            recording = recordingDao.findRecordingByUri(recordingUri);
                        }
                        if (fileItem.getFieldName().equals("recordingId")) {
                            String recordingId = fileItem.getString();
                            recording = recordingDao.findRecordingById(recordingId);
                        }
                        if (fileItem.getFieldName().equals("sessionOffset")) {
                            sessionOffset = Long.parseLong(fileItem.getString());
                        }
                        if (fileItem.getFieldName().equals("sendEmailAddress")) {
                            sendEmail = fileItem.getString();
                        }
                    } else {
                        in = new BufferedReader(
                                new InputStreamReader(fileItem.getInputStream()));
                    }
                }
            } catch (FileUploadException e) {
                e.printStackTrace();
            }
        }
        Event event = eventDao.findEventById(recording.getEventUri());
        if (in != null) {
            String line = in.readLine();
            while (line != null) {
                String ann = "";
                while (!ann.endsWith("</annotation>") && (line != null)) {
                    ann += line;
                    line = in.readLine();
                }
                if (!ann.endsWith("</annotation>")) {
                    break;
                }
                CrewLiveAnnotation liveAnnotation = new CrewLiveAnnotation(
                    liveAnnotationTypeRepository, ann);
                long anntime = liveAnnotation.getTimestamp() + sessionOffset;
                if ((anntime >= recording.getStartTime().getTime())
                        && (anntime < recording.getEndTime().getTime())) {
                    Annotation annotation = liveAnnotation.toAnnotation();
                    annotation.setAnnotates(recording.getUri());
                    String key = keymap.get(liveAnnotation.getAuthor());
                    if (key == null) {
                        key = String.valueOf(
                                (long) (Math.random() * CrewConstants.ID_NORMALIZATION));
                        keymap.put(liveAnnotation.getAuthor(), key);
                    }
                    String author = url + key;
                    annotation.setAuthor(author);
                    annotation.setGraphId(author);
                    annotationStoreDao.addAnnotation(annotation);
                }
            }
            Iterator<Entry<String, String>> iter = keymap.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, String> entry = iter.next();
                SimpleEmail mail = new SimpleEmail();
                mail.setHostName(mailServer);
                if ((sendEmail != null) && !sendEmail.equals("")) {
                    System.err.println("Sending e-mail to " + sendEmail);
                    mail.addTo(sendEmail);
                } else {
                    mail.addTo(entry.getKey());
                }
                mail.setSubject("Confirmation of Live Annotations for " + event.getTitle());
                mail.setFrom(mailFrom);
                String msgurl = server
                    + "/secured/confirmAnnotations.do?annotatorId="
                    + entry.getValue();
                String msgstr = "Please confirm your annotations by clicking on :\n\n" + msgurl;
                if (sendEmail != null) {
                    msgstr += "\n\n(This e-mail would have been sent to " + entry.getKey() + ")\n";
                }
                mail.setMsg(msgstr);
                try {
                    mail.send();
                } catch (EmailException e) {
                    System.err.println("Error sending e-mail: "
                            + e.getMessage());
                }
                System.err.println("Annotations Uploaded - URL for approval = " + msgurl);
            }
            in.close();
        }
        return null;
    }

}
