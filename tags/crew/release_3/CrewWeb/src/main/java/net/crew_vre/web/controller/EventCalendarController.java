package net.crew_vre.web.controller;

import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.Place;
import net.crew_vre.web.facade.DisplayEventFacade;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: EventCalendarController.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class EventCalendarController implements Controller {

    public EventCalendarController(DisplayEventFacade displayEventFacade) {
        this.displayEventFacade = displayEventFacade;
    }

    public ModelAndView handleRequest(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("iCalendar");

        Event event = null;

        if (request.getParameter("eventId") != null) {
            event = displayEventFacade.displayEvent(request.getParameter("eventId"));
        }

        mav.addObject("event", event);

        return mav;
    }


    private DisplayEventFacade displayEventFacade;

    private String productID = ":-//University of Bristol//NONSGML CREW//EN";
}
