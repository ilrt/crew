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

import net.crew_vre.events.domain.EventPart;
import net.crew_vre.web.facade.ListEventsFacade;
import net.crew_vre.web.facet.Facet;
import net.crew_vre.web.facet.FacetService;
import net.crew_vre.web.facet.SearchFilter;
import net.crew_vre.web.history.BrowseHistory;
import net.crew_vre.web.navigation.NavHelper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: ListEventsController.java 1191 2009-03-31 13:38:51Z cmmaj $
 */
public class ListEventsController implements Controller {

    public ListEventsController(FacetService facetService, ListEventsFacade listEventsFacade,
                                BrowseHistory browseHistory,
                                List<Map<String, String>> facetConfigs,
                                List<Map<String, String>> feedList) {
        this.facetService = facetService;
        this.listEventsFacade = listEventsFacade;
        this.browseHistory = browseHistory;
        this.facetConfigs = facetConfigs;
        this.feedList = feedList;
    }

    public ModelAndView handleRequest(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        //long start = System.currentTimeMillis();

        // keep a record of the search request
        browseHistory.addSearchHistory(request);

        // hold the currently selected page - default to 1
        int page = 1;

        // hold the number of results per page
        int maxResults = 10;

        // list to hold the different facets
        List<Facet> facets;
        List<SearchFilter> searchFilters;

        // get the request URL
        String url = request.getRequestURI();

        if (request.getParameter("maxResults") != null) {
            maxResults = Integer.parseInt(request.getParameter("maxResults"));
        }

        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        // generate the search filters
        //long startFilter = System.currentTimeMillis();
        searchFilters = facetService.generateSearchFilters(facetConfigs, request);
        //long endFilter = System.currentTimeMillis();

        long startFacet = System.currentTimeMillis();
        if (searchFilters.size() == 0) {
            facets = facetService.generateStates(facetConfigs);
        } else {
            facets = facetService.generateStates(facetConfigs, request, searchFilters);
        }
        long endFacet = System.currentTimeMillis();

        // max number of results available?
        //long startTotal = System.currentTimeMillis();
        //int total = listEventsFacade.totalEventsAvailable(searchFilters);
        // We will just grab everything, and reuse it. offset isn't much faster (pldms)
        List<EventPart> matchingEvents = listEventsFacade.displayEvents(searchFilters);
        int total = matchingEvents.size();
        //long endTotal = System.currentTimeMillis();

        // create nav helper object
        NavHelper navHelper = new NavHelper(total, maxResults, page);

        // it might be that a new request could have been caused by a facet change,
        // we need to make sure that the current page in the nav is still valid.
        // If not, default to 1.
        if (page > navHelper.getTotalPages()) {
            page = 1;
            navHelper = new NavHelper(total, maxResults, page);
        }



        // calculate limit and offsets
        int offset = (page - 1) * maxResults;
        int limit = maxResults;

        ModelAndView mov = new ModelAndView("listEvents");
        mov.addObject("facets", facets);
        mov.addObject("url", url);
        mov.addObject("nav", navHelper);
        mov.addObject("parameters", request.getParameterMap());
        //long startEventList = System.currentTimeMillis();
        //mov.addObject("listEvents", listEventsFacade.displayEvents(searchFilters, limit, offset));
        int lastItem = (offset + limit > total) ? total : offset + limit;
        mov.addObject("listEvents", matchingEvents.subList(offset, lastItem));
        //long endEventList = System.currentTimeMillis();
        mov.addObject("total", total);
        mov.addObject("feedList", feedList);
        //long startUpcoming = System.currentTimeMillis();
        mov.addObject("upcomingEvents", listEventsFacade.displayUpcomingEvents(10, 0));
        //long endUpcoming = System.currentTimeMillis();
        //long startRecent = System.currentTimeMillis();
        mov.addObject("recentlyAddedEvents", listEventsFacade.displayRecentlyAddedEvents(31, 5, 0));
        //long endRecent = System.currentTimeMillis();
        //long end = System.currentTimeMillis();

        //System.out.println("****************************************************");

        //System.out.println("Search Filter: " + (endFilter - startFilter));
        System.out.println("Facet: " + (endFacet - startFacet));
        //System.out.println("Event Total: " + (endTotal - startTotal));
        //System.out.println("Event List: " + (endEventList - startEventList));
        //System.out.println("Upcoming: " + (endUpcoming - startUpcoming));
        //System.out.println("Recent: " + (endRecent - startRecent));
        //System.out.println("Total: " + (end - start));

        return mov;
    }

    private FacetService facetService;
    private ListEventsFacade listEventsFacade;
    private BrowseHistory browseHistory;
    private List<Map<String, String>> facetConfigs;
    private List<Map<String, String>> feedList;

}
