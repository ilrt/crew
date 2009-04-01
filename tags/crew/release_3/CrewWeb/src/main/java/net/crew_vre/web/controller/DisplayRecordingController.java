package net.crew_vre.web.controller;

import net.crew_vre.web.facade.DisplayRecordingFacade;
import net.crew_vre.web.history.BrowseHistory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayRecordingController.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class DisplayRecordingController extends BrowseHistory implements Controller {

    public void setDisplayRecordingFacade(DisplayRecordingFacade displayRecordingFacade) {
        this.displayRecordingFacade = displayRecordingFacade;
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String eventId = null;
        String recordingId = null;

        if (request.getParameter("eventId") != null) {
            eventId = request.getParameter("eventId");
        }

        if (request.getParameter("recordingId") != null) {
            recordingId = request.getParameter("recordingId");
        }

        ModelAndView mov = new ModelAndView("displayRecording");
        mov.addObject("eventId", eventId);
        mov.addObject("recordingId", recordingId);

        return mov;
    }

    private DisplayRecordingFacade displayRecordingFacade;

}
