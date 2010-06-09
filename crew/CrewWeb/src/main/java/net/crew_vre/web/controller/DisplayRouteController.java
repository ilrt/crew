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

import net.crew_vre.events.domain.Place;
import net.crew_vre.web.facade.DisplayRouteFacade;
import net.crew_vre.web.history.BrowseHistory;
import net.crew_vre.events.domain.StartPoint;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.crew_vre.events.domain.KmlObject;

/**
 * @author Pihl Cross (phil.cross@bristol.ac.uk)
 */
public class DisplayRouteController implements Controller {

    private static String DISPLAY_ROUTE = "displayroute.do";

    public DisplayRouteController(DisplayRouteFacade displayRouteFacade, BrowseHistory
            browseHistory) {
        this.displayRouteFacade = displayRouteFacade;
        this.browseHistory = browseHistory;
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Place place = null;
        StartPoint startPoint = null;
        KmlObject kmlObject = null;

        if (request.getParameter("placeId") != null) {
            place = displayRouteFacade.displayPlace(request.getParameter("placeId"));
        }

        if (request.getParameter("startPointId") != null) {
            startPoint = displayRouteFacade.displayStartPoint(request.getParameter("startPointId"));
            if (startPoint != null) {
                browseHistory.addHistory(request, startPoint.getTitle());
            }
        }

        if (request.getParameter("kml") != null) {
            kmlObject = displayRouteFacade.displayKmlObject(request.getParameter("kml"));
            if (kmlObject != null) {
                browseHistory.addHistory(request, kmlObject.getTitle());
            }
        }

        ModelAndView mov = new ModelAndView("displayRoute");
        mov.addObject("place", place);
        if (startPoint != null)
            mov.addObject("startPoint", startPoint);
        if (kmlObject != null) {
            mov.addObject("kml", kmlObject);
            
            /* See if there is a url available from the object
             * - this would be where an existing online kml file was
             * used as opposed to one generated within the local repository 
             * from a kml file. Not used at present.
            */
            String kmlUrl;
            if (kmlObject.getUrl() != null && !kmlObject.getUrl().equals("")) {
                kmlUrl = kmlObject.getUrl();
            } else {
                String requestURL = request.getRequestURL().toString();
                String baseURL = requestURL.substring(0,requestURL.length() - DISPLAY_ROUTE.length());
                // De-URI the kml id
                String kmlId = kmlObject.getId();
                String id = "";
                if (kmlId != null ) {
                    id = kmlId.substring(kmlId.indexOf("KML"), kmlId.length());
                }
                kmlUrl = baseURL + "repository/route_" + id + ".kml";
            }
            mov.addObject("kmlUrl", kmlUrl);
        }

        return mov;
    }

    private DisplayRouteFacade displayRouteFacade;

    private BrowseHistory browseHistory;

}
