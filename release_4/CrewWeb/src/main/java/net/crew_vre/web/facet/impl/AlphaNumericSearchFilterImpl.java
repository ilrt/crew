package net.crew_vre.web.facet.impl;

import net.crew_vre.web.facet.SearchFilter;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: AlphaNumericSearchFilterImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class AlphaNumericSearchFilterImpl implements SearchFilter {

    public AlphaNumericSearchFilterImpl(final String paramName, final String linkProperty,
                                        final String constraint) {
        this.paramName = paramName;
        this.linkProperty = linkProperty;
        this.constraint = constraint;
    }

    public String getSparqlFragment() {

        String temp = constraint.substring(0, 1);
        String subject = "?" + paramName;

        return new StringBuilder().append("?id ").append("<")
                .append(linkProperty).append(">").append(" ")
                .append(subject).append(" .\n")
                .append("FILTER(regex(str(").append(subject).append("), \"^")
                .append(temp).append("\", \"i\")) .\n").toString();
    }

    private String linkProperty;
    private String constraint;
    private String paramName;
}
