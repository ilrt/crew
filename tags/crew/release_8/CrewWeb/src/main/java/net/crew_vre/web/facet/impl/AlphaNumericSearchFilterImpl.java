package net.crew_vre.web.facet.impl;

import net.crew_vre.web.facet.SearchFilter;

/**
 * Represents an filter for Alpha-Numeric facets.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: AlphaNumericSearchFilterImpl.java 994 2009-01-09 15:42:35Z cmmaj $
 */
public class AlphaNumericSearchFilterImpl implements SearchFilter {

    // ---------- CONSTRUCTORS

    /**
     * A constructor to create a filter with a constraint.
     *
     * @param paramName    the parameter name of the facet constraint.
     * @param linkProperty the rdf link property.
     * @param constraint   the constraint used in the filter.
     */
    public AlphaNumericSearchFilterImpl(final String paramName, final String linkProperty,
                                        final String constraint) {
        this.paramName = paramName;
        this.linkProperty = linkProperty;
        this.constraint = constraint;
    }

    /**
     * A constructor without a constraint. This is used in creating the filter for the
     * inititial unconstrained facet states.
     *
     * @param paramName     the parameter name of the facet constraint.
     * @param linkProperty  the rdf link property.
     */
    public AlphaNumericSearchFilterImpl(final String paramName, final String linkProperty) {
        this(paramName, linkProperty, null);
    }


    // ---------- PUBLIC METHODS

    public String getSparqlFragment() {

        String subject = "?" + paramName;

        String sparql = new StringBuilder().append("?id ").append("<")
                .append(linkProperty).append(">").append(" ")
                .append(subject).append(" .\n").toString();

        // add a FILTER if we have a constraint 
        if (constraint != null) {

            String temp = constraint.substring(0, 1);
            sparql = new StringBuilder(sparql).append("FILTER(regex(str(")
                    .append(subject).append("), \"^")
                    .append(temp).append("\", \"i\")) .\n").toString();
        }

        return sparql;
    }

    private final String linkProperty;
    private final String paramName;
    private final String constraint;
}
