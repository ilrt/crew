package net.crew_vre.web.controller;

import net.crew_vre.events.domain.Event;
import net.crew_vre.web.facade.DisplayEventFacade;
import net.crew_vre.web.history.BrowseHistory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayEventController.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class DisplayEventController implements Controller {

    public DisplayEventController(DisplayEventFacade displayEventFacade,
                                  BrowseHistory browseHistory) {
        this.displayEventFacade = displayEventFacade;
        this.browseHistory = browseHistory;
    }

    public ModelAndView handleRequest(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        Event event = null;

        if (request.getParameter("eventId") != null) {
            event = displayEventFacade.displayEvent(request.getParameter("eventId"));
        }

        if (event != null) {
            browseHistory.addHistory(request, event.getTitle());
        }

        ModelAndView mov = new ModelAndView("displayEvent");
        mov.addObject("event", event);

        return mov;
    }

    private DisplayEventFacade displayEventFacade;

    private BrowseHistory browseHistory;
}
