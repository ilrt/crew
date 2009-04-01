package net.crew_vre.web.facet.impl;

import net.crew_vre.web.facet.SearchFilter;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DateTimeSearchFilterImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 *
 **/
public class DateTimeSearchFilterImpl implements SearchFilter {

    public DateTimeSearchFilterImpl(final String paramName, final String linkProperty,
                                    final String constraint) {
        this.linkProperty = linkProperty;
        this.constraint = constraint;
        this.paramName = paramName;
    }

    public String getSparqlFragment() {

        String dateVar = "?" + paramName;

        return new StringBuilder().append("?id ").append("<")
                .append(linkProperty).append(">").append(" ")
                .append(dateVar).append(" .\n")
                .append("FILTER(regex(str(")
                .append(dateVar).append("), \"^")
                .append(constraint).append("\", \"i\")) .\n")
                .toString();
    }

    private String paramName;
    private String linkProperty;
    private String constraint;
}
