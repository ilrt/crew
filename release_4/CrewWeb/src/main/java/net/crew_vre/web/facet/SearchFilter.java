package net.crew_vre.web.facet;

/**
 * <p>Facet states create filters that are used to constrain the facets and the results.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public interface SearchFilter {

    String getSparqlFragment();

}
