package net.crew_vre.web.controller;

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
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: ListEventsController.java 1132 2009-03-20 19:05:47Z cmmaj $
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
        String url = request.getRequestURL().toString();

        if (request.getParameter("maxResults") != null) {
            maxResults = Integer.parseInt(request.getParameter("maxResults"));
        }

        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        // generate the search filters
        searchFilters = facetService.generateSearchFilters(facetConfigs, request);

        if (searchFilters.size() == 0) {
            facets = facetService.generateStates(facetConfigs);
        } else {
            facets = facetService.generateStates(facetConfigs, request, searchFilters);
        }

        // max number of results available?
        int total = listEventsFacade.totalEventsAvailable(searchFilters);

        // create nav helper object
        NavHelper navHelper = new NavHelper(total, maxResults, page);

        // it might be that a new request could have been caused by a facet change,
        // we need to make sure that the current page in the nav is still valid.
        // If not, default to 1.
        if (page > navHelper.getTotalPages()) {
            page = 1;
            navHelper.setCurrentPage(1);
        }

        // calculate limit and offsets
        int offset = (page - 1) * maxResults;
        int limit = maxResults;

        ModelAndView mov = new ModelAndView("listEvents");
        mov.addObject("facets", facets);
        mov.addObject("url", url);
        mov.addObject("nav", navHelper);
        mov.addObject("parameters", request.getParameterMap());
        mov.addObject("listEvents", listEventsFacade.displayEvents(searchFilters, limit, offset));
        mov.addObject("total", total);
        mov.addObject("feedList", feedList);
        mov.addObject("upcomingEvents", listEventsFacade.displayUpcomingEvents());
        mov.addObject("recentlyAddedEvents", listEventsFacade.displayRecentlyAddedEvents(31, 5, 0));
        return mov;
    }

    private FacetService facetService;
    private ListEventsFacade listEventsFacade;
    private BrowseHistory browseHistory;
    private List<Map<String, String>> facetConfigs;
    private List<Map<String, String>> feedList;

}
