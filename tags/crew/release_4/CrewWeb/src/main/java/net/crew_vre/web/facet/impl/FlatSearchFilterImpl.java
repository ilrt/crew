package net.crew_vre.web.facet.impl;

import net.crew_vre.web.facet.SearchFilter;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: FlatSearchFilterImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 *
 **/
public class FlatSearchFilterImpl implements SearchFilter {

    public FlatSearchFilterImpl(final String linkProperty, final String objectUri) {
        this.objectUri = objectUri;
        this.linkProperty = linkProperty;
    }

    public String getSparqlFragment() {

        return new StringBuilder().append("?id ").append("<").append(linkProperty).append(">")
                .append(" <").append(objectUri).append("> .\n").toString();
    }

    private String linkProperty;
    private String objectUri;

}
