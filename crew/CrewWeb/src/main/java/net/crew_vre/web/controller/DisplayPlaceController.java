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

import java.util.List;
import net.crew_vre.events.domain.Place;
import net.crew_vre.web.facade.DisplayPlaceFacade;
import net.crew_vre.web.history.BrowseHistory;
import net.crew_vre.events.domain.StartPoint;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: DisplayPlaceController.java 1191 2009-03-31 13:38:51Z cmmaj $
 */
public class DisplayPlaceController implements Controller {

    public DisplayPlaceController(DisplayPlaceFacade displayPlaceFacade, BrowseHistory
            browseHistory) {
        this.displayPlaceFacade = displayPlaceFacade;
        this.browseHistory = browseHistory;
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Place place = null;
        List<StartPoint> startPoints = null;

        if (request.getParameter("placeId") != null) {
            place = displayPlaceFacade.displayPlace(request.getParameter("placeId"));
        }

        if (request.getParameter("eventId") != null) {
            startPoints = displayPlaceFacade.getStartPoints(request.getParameter("eventId"));
        }

        if (place != null) {
            browseHistory.addHistory(request, place.getTitle());
        }

        ModelAndView mov = new ModelAndView("displayPlace");
        // Pass event details back for use in nav link
        mov.addObject("eventId", request.getParameter("eventId"));
        mov.addObject("eventTitle", request.getParameter("eventTitle"));
        mov.addObject("place", place);
        if (startPoints != null)
            mov.addObject("startPointList", startPoints);

        return mov;
    }

    private DisplayPlaceFacade displayPlaceFacade;

    private BrowseHistory browseHistory;

}
