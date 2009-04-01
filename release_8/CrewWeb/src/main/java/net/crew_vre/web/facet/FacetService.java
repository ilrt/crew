package net.crew_vre.web.facet;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p> A service that creates facets with states and filters that represent those
 * states in SPARQL.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: FacetService.java 657 2008-02-13 14:19:12Z cmmaj $
 */
public interface FacetService {
    /**
     * <p>We need to check the request parameters to see what is the current state of the
     * facets. To do this, we iterate over each of the facet configurations and find out
     * what value is used in the request parameter names. It checks if that value exists
     * in the request - if yes, checks the facet type and creates the appropriate
     * search filter.</p>
     *
     * @param facetConfigs the list of facet configurations
     * @param request      the HTTP request.
     * @return a list a search filter objects
     */
    List<SearchFilter> generateSearchFilters(List<Map<String, String>> facetConfigs,
                                             HttpServletRequest request);

    /**
     * <p>Generate a list of facets with states.</p>
     *
     * @param facetConfigs the list of facet configurations
     * @return a list of facets.
     */
    List<Facet> generateStates(List<Map<String, String>> facetConfigs);

    /**
     * <p>Generate a list of facets with states.</p>
     *
     * @param facetConfigs  the list of facet configurations
     * @param request       the http request.
     * @param searchFilters filters to constrain the facet states.
     * @return a list of facts.
     */
    List<Facet> generateStates(List<Map<String, String>> facetConfigs,
                               HttpServletRequest request,
                               List<SearchFilter> searchFilters);
}
