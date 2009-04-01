package net.crew_vre.web.facet;

/**
 * <p>A facet is a way of browsing the data.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: Facet.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface Facet {

    String getName();

    FacetState getState();

    // possible parameter values in the facet configuration
    String FACET_TYPE = "facetType";
    String FACET_TITLE = "facetTitle";
    String LINK_PROPERTY = "linkProperty";
    String WIDER_PROPERTY = "widerProperty";
    String FACET_BASE = "facetBase";
    String CONSTRAINT_TYPE = "constraintType";
    String PARAM_NAME = "paramName";
    String PREFIX = "prefix";
    String START_YEAR = "startYear";
    String END_YEAR = "endYear";

    // facet types
    String ALPHA_NUMERIC_FACET_TYPE = "AlphaNumeric";
    String HIERARCHICAL_FACET_TYPE = "Hierarchical";
    String DATE_TIME_FACET_TYPE = "DateTime";
    String FLAT_FACET_TYPE = "Flat";
}
