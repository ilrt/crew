package net.crew_vre.web.facet.impl;

import net.crew_vre.web.Utility;
import net.crew_vre.web.facet.AlphaNumericFacetFactory;
import net.crew_vre.web.facet.DateTimeFacetFactory;
import net.crew_vre.web.facet.Facet;
import net.crew_vre.web.facet.FacetService;
import net.crew_vre.web.facet.FlatFacetFactory;
import net.crew_vre.web.facet.HierarchicalFacetFactory;
import net.crew_vre.web.facet.SearchFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p> A service implementation that creates facets with states and filters that represent those
 * states in SPARQL.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: FacetServiceImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class FacetServiceImpl implements FacetService {

    /**
     * <p>Constructor.</p>
     *
     * @param utility   a utilty to deal with URIs in facets.
     * @param anFacetFactory an alpha numeric facet.
     * @param hFacetFactory  an hierarchical facet.
     * @param dtFacetFactory a datetime facet.
     * @param fFacetFactory  a flat facet.
     */
    public FacetServiceImpl(
            final Utility utility,
            final AlphaNumericFacetFactory anFacetFactory,
            final HierarchicalFacetFactory hFacetFactory,
            final DateTimeFacetFactory dtFacetFactory,
            FlatFacetFactory fFacetFactory) {
        this.utility = utility;
        this.anFacetFactory = anFacetFactory;
        this.hFacetFactory = hFacetFactory;
        this.dtFacetFactory = dtFacetFactory;
        this.fFacetFactory = fFacetFactory;
    }

    /**
     * <p>We need to check the request parameters to see what is the current state of the
     * facets. To do this, we iterate over each of the facet configurations and find out
     * what value is used in the request parameter names. It checks if that value exists
     * in the request - if yes, checks the facet type and creates the appropriate
     * search filter.</p>
     *
     * @param facetConfigs the facet configuration
     * @param request the HTTP request
     * @return a list a search filter objects
     */
    public List<SearchFilter> generateSearchFilters(List<Map<String, String>> facetConfigs,
                                                    HttpServletRequest request) {

        // holds the search filters
        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();

        // iterate over the list of facet configurations...
        for (Map<String, String> config : facetConfigs) {

            // get the parameter name and the facet type
            String paramName = config.get(Facet.PARAM_NAME);
            String facetType = config.get(Facet.FACET_TYPE);

            // if the parameter name used by this facet is found in the request ...
            if (request.getParameter(paramName) != null) {

                String parameter = request.getParameter(paramName);
                SearchFilter filter = null;

                // check the type and create the correct filter type
                if (facetType.equals(Facet.ALPHA_NUMERIC_FACET_TYPE)) {

                    filter = new AlphaNumericSearchFilterImpl(config.get(Facet.PARAM_NAME),
                            config.get(Facet.LINK_PROPERTY), parameter);
                    searchFilters.add(filter);

                } else if (facetType.equals(Facet.HIERARCHICAL_FACET_TYPE)) {

                    String uri = utility.parameterValueToUri(parameter);
                    filter = new HierarchicalSearchFilterImpl(config.get(Facet.LINK_PROPERTY), uri);
                } else if (facetType.equals(Facet.DATE_TIME_FACET_TYPE)) {

                    filter = new DateTimeSearchFilterImpl(config.get(Facet.PARAM_NAME),
                            config.get(Facet.LINK_PROPERTY), parameter);

                } else if (facetType.equals(Facet.FLAT_FACET_TYPE)) {

                    String uri = utility.parameterValueToUri(request.getParameter(paramName));

                    filter = new FlatSearchFilterImpl(config.get(Facet.LINK_PROPERTY), uri);
                }

                if (filter != null) {
                    searchFilters.add(filter);
                }
            }
        }

        return searchFilters;
    }

    /**
     * <p>Generate a list of facets with states.</p>
     *
     * @param facetConfigs the facet configuration
     * @return a list of facets.
     */
    public List<Facet> generateStates(List<Map<String, String>> facetConfigs) {
        return generateInitialStates(facetConfigs);
    }

    /**
     * <p>Generate a list of facets with states.</p>
     *
     * @param facetConfigs the facet configuration
     * @param request       the http request.
     * @param searchFilters filters to constrain the facet states.
     * @return a list of facts.
     */
    public List<Facet> generateStates(List<Map<String, String>> facetConfigs,
                                      HttpServletRequest request,
                                      List<SearchFilter> searchFilters) {

        List<Facet> facets = new ArrayList<Facet>();

        for (Map<String, String> config : facetConfigs) {

            Facet facet = null;

            if (config.get(Facet.FACET_TYPE).equals(Facet.ALPHA_NUMERIC_FACET_TYPE)) {

                if (request.getParameter(config.get(Facet.PARAM_NAME)) != null) {

                    facet = anFacetFactory.create(config,
                            request.getParameter(config.get(Facet.PARAM_NAME)));
                } else {

                    facet = anFacetFactory.create(config, searchFilters);
                }

            } else if (config.get(Facet.FACET_TYPE).equals(Facet.HIERARCHICAL_FACET_TYPE)) {

                if (request.getParameter(config.get(Facet.PARAM_NAME)) != null) {

                    facet = hFacetFactory.create(config, searchFilters,
                            request.getParameter(config.get(Facet.PARAM_NAME)));
                } else {

                    facet = hFacetFactory.create(config, searchFilters);
                }

            } else if (config.get(Facet.FACET_TYPE).equals(Facet.DATE_TIME_FACET_TYPE)) {

                if (request.getParameter(config.get(Facet.PARAM_NAME)) != null) {
                    facet = dtFacetFactory.create(config, searchFilters,
                            request.getParameter(config.get(Facet.PARAM_NAME)));
                } else {
                    facet = dtFacetFactory.create(config, searchFilters);
                }

            } else if (config.get(Facet.FACET_TYPE).equals(Facet.FLAT_FACET_TYPE)) {

                if (request.getParameter(config.get(Facet.PARAM_NAME)) != null) {
                    facet = fFacetFactory.create(config,
                            request.getParameter(config.get(Facet.PARAM_NAME)));
                } else {
                    facet = fFacetFactory.create(config, searchFilters);
                }
            }
            facets.add(facet);
        }

        return facets;
    }


    /**
     * @param facetConfigs the facet configuration
     * @return a list of facets in their initial state.
     */
    private List<Facet> generateInitialStates(List<Map<String, String>> facetConfigs) {

        List<Facet> facets = new ArrayList<Facet>();

        for (Map<String, String> config : facetConfigs) {

            Facet facet = null;

            if (config.get(Facet.FACET_TYPE).equals(Facet.ALPHA_NUMERIC_FACET_TYPE)) {
                facet = anFacetFactory.create(config);
            } else if (config.get(Facet.FACET_TYPE).equals(Facet.HIERARCHICAL_FACET_TYPE)) {
                facet = hFacetFactory.create(config);
            } else if (config.get(Facet.FACET_TYPE).equals(Facet.DATE_TIME_FACET_TYPE)) {
                facet = dtFacetFactory.create(config);
            } else if (config.get(Facet.FACET_TYPE).equals(Facet.FLAT_FACET_TYPE)) {
                facet = fFacetFactory.create(config);
            }
            facets.add(facet);
        }

        return facets;
    }

    private Utility utility;
    private AlphaNumericFacetFactory anFacetFactory;
    private HierarchicalFacetFactory hFacetFactory;
    private DateTimeFacetFactory dtFacetFactory;
    private FlatFacetFactory fFacetFactory;
}
