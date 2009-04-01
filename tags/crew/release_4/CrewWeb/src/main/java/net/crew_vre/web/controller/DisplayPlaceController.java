package net.crew_vre.web.controller;

import net.crew_vre.events.domain.Place;
import net.crew_vre.web.facade.DisplayPlaceFacade;
import net.crew_vre.web.history.BrowseHistory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayPlaceController.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class DisplayPlaceController implements Controller {

    public DisplayPlaceController(DisplayPlaceFacade displayPlaceFacade, BrowseHistory
            browseHistory, String googleMapKey) {
        this.displayPlaceFacade = displayPlaceFacade;
        this.browseHistory = browseHistory;
        this.googleMapKey = googleMapKey;
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Place place = null;

        if (request.getParameter("placeId") != null) {
            place = displayPlaceFacade.displayPlace(request.getParameter("placeId"));
        }

        if (place != null) {
            browseHistory.addHistory(request, place.getTitle());
        }

        ModelAndView mov = new ModelAndView("displayPlace");
        mov.addObject("place", place);
        mov.addObject("googleMapKey", googleMapKey);

        return mov;
    }

    private DisplayPlaceFacade displayPlaceFacade;

    private String googleMapKey;

    private BrowseHistory browseHistory;

}
