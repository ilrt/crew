package net.crew_vre.web.facet.impl;

import net.crew_vre.web.facet.SearchFilter;

/**
 * Represents a filter for date facets.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: DateTimeSearchFilterImpl.java 994 2009-01-09 15:42:35Z cmmaj $
 */
public class DateTimeSearchFilterImpl implements SearchFilter {

    // ---------- CONSTRUCTORS

    /**
     * A constructor to create a filter with a constraint.
     *
     * @param paramName    the parameter name of the facet constraint.
     * @param linkProperty the rdf link property.
     * @param constraint   the constraint used in the filter.
     */
    public DateTimeSearchFilterImpl(final String paramName, final String linkProperty,
                                    final String constraint) {
        this.linkProperty = linkProperty;
        this.constraint = constraint;
        this.paramName = paramName;
    }

    /**
     * A constructor without a constraint. This is used in creating the filter for the
     * inititial unconstrained facet states.
     *
     * @param paramName    the parameter name of the facet constraint.
     * @param linkProperty the rdf link property.
     */
    public DateTimeSearchFilterImpl(final String paramName, final String linkProperty) {
        this(paramName, linkProperty, null);
    }

    // ---------- PUBLIC METHODS

    public String getSparqlFragment() {

        String dateVar = "?" + paramName;

        String sparql = new StringBuilder().append("?id ").append("<")
                .append(linkProperty).append(">").append(" ")
                .append(dateVar).append(" .\n").toString();

        if (constraint != null) {
            sparql = new StringBuilder(sparql).append("FILTER(regex(str(").append(dateVar)
                    .append("), \"^") .append(constraint).append("\", \"i\")) .\n").toString();
        }

        return sparql;
    }


    private final String paramName;
    private final String linkProperty;
    private final String constraint;
}
