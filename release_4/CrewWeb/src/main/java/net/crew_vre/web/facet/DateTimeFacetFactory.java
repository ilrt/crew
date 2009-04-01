package net.crew_vre.web.facet;

import org.caboto.jena.db.Data;

import java.util.List;
import java.util.Map;

/**
 * <p>A factory for creating DateTime facets with a state and possible refinements.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DateTimeFacetFactory.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface DateTimeFacetFactory {

    /**
     * <p>Create a facet with no constraints and is not constrained by other facets.</p>
     *
     * @param config a Map holding the configuration details for the facet.
     * @return a facet implementation with no constraints.
     */
    Facet create(final Map<String, String> config, final Data data);

    /**
     * <p>Creates a facet with no constrants but it constrained by other facets.</p>
     *
     * @param config        a Map holding the configuration details for the facet.
     * @param searchFilters a List of filters that provide constraints.
     * @return a facet implentation with constraints.
     */
    Facet create(final Map<String, String> config, final List<SearchFilter> searchFilters,
                 final Data data);

    /**
     * <p>Creates a constrained facet.</p>
     *
     * @param config        a Map holding the configuration details for the facet.
     * @param searchFilters a List of filters that provide constraints.
     * @param dateString    a string representing a date constraint.
     * @return a representation of a facet that is selected and has no
     *         further refinements.
     */
    Facet create(final Map<String, String> config, final List<SearchFilter> searchFilters,
                 final String dateString, final Data data);
}
