package net.crew_vre.web.facet;

import org.caboto.jena.db.Data;

import java.util.Map;
import java.util.List;

/**
 *
 * <p>A factory for creating hierachical facets with a state and possible refinements.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: HierarchicalFacetFactory.java 948 2008-11-28 14:25:26Z cmmaj $
 *
 **/
public interface HierarchicalFacetFactory {

    /**
     *  <p>Create a facet with no constraints and is not constrained by other facets.</p>
     *
     * @param config    a Map holding the configuration details for the facet.
     * @return a facet implementation with no constraints.
     */
    Facet create(Map<String, String> config, final Data data);

    /**
     * <p>Creates a facet with no constrants but it constrained by other facets.</p>
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
     * @param searchFilters     a List of filters that provide constraints.
     * @param selectedUri       a URI reprsenting the facet state.
     * @return a facet state reprsented by the URI.
     */
    Facet create(Map<String, String> config, List<SearchFilter> searchFilters,
                        String selectedUri, final Data data);
}
