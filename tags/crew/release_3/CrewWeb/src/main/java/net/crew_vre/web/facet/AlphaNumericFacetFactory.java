package net.crew_vre.web.facet;

import java.util.Map;
import java.util.List;

/**
 * <p>A factory for creating alpha-numeric facets with a state and possible refinements.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: AlphaNumericFacetFactory.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface AlphaNumericFacetFactory {

    /**
     * <p>Create a facet with no constraints and is not constrained by other facets.</p>
     *
     * @param config a Map holding the configuration details for the facet.
     * @return a facet implementation with no constraints.
     */
    Facet create(final Map<String, String> config);

    /**
     * <p>Creates a facet with no constrants but it constrained by other facets.</p>
     *
     * @param config        a Map holding the configuration details for the facet.
     * @param searchFilters a List of filters that provide constraints.
     * @return a facet implentation with constraints.
     */
    Facet create(final Map<String, String> config, final List<SearchFilter> searchFilters);

    /**
     * <p>Creates a fully constrained facet, e.g. "A" has been selected.</p>
     *
     * @param config         a Map holding the configuration details for the facet.
     * @param facetStateName a facet state name
     * @return a representation of a facet that is selected and has no
     *         further refinements.
     */
    Facet create(final Map<String, String> config, final String facetStateName);
}
