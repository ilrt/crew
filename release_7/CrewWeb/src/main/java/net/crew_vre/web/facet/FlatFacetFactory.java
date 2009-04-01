package net.crew_vre.web.facet;

import org.caboto.jena.db.Data;

import java.util.Map;
import java.util.List;

/**
 *
 * <p>A factory for creating flat facets.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: FlatFacetFactory.java 1132 2009-03-20 19:05:47Z cmmaj $
 *
 **/
public interface FlatFacetFactory {

    /**
     * <p>Creates a facet with no constraints.</p>
     *
     * @param config    a Map holding the configuration details for the facet.
     * @return a facet implementation with no constraints.
     */
    Facet create(Map<String, String> config, Data data);

    /**
     * <p>Creates a facet with no constrants but is constrained by other facets.</p>
     *
     * @param config            a Map holding the configuration details for the facet.
     * @param searchFilters     a List of filters that provide constraints.
     * @return a facet implentation with constraints.
     */
    Facet create(Map<String, String> config, List<SearchFilter> searchFilters, final Data data);

    /**
     * <p>Creates a fully constrained facet.</p>
     *
     * @param config            a Map holding the configuration details for the facet.
     * @param selectedUri       uri that reflects the facet state.
     * @return a representation of a facet that is selected and has no
     *                          further refinements.
     */
    Facet create(Map<String, String> config, String selectedUri, final Data data);
}
