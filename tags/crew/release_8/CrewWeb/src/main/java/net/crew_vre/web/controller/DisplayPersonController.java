package net.crew_vre.web.controller;

import net.crew_vre.events.domain.Person;
import net.crew_vre.web.facade.DisplayPersonFacade;
import net.crew_vre.web.history.BrowseHistory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayPersonController.java 538 2007-12-21 14:44:38Z cmmaj $
 */
public class DisplayPersonController implements Controller {

    public DisplayPersonController(DisplayPersonFacade displayPersonFacade,
                                   BrowseHistory browseHistory) {
        this.displayPersonFacade = displayPersonFacade;
        this.browseHistory = browseHistory;
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Person person = null;

        if (request.getParameter("personId") != null) {
            person = displayPersonFacade.displayPerson(request.getParameter("personId"));
        }

        if (person != null) {
            browseHistory.addHistory(request, person.getName());
        }

        ModelAndView mov = new ModelAndView("displayPerson");
        mov.addObject("person", person);

        return mov;
    }

    private DisplayPersonFacade displayPersonFacade;

    private BrowseHistory browseHistory;

}
