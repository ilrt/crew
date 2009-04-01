package net.crew_vre.web.facet;

/**
 * Facet states create filters that are used to constrain the facets and the results. A
 * number of SearchFilter objects might be used to generate the SPARQL that both
 * generates the available facet items and the search results.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public interface SearchFilter {

    String getSparqlFragment();

}
