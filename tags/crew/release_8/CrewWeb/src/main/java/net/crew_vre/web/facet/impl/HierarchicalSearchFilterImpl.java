package net.crew_vre.web.facet.impl;

import net.crew_vre.web.facet.SearchFilter;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: HierarchicalSearchFilterImpl.java 592 2008-02-07 16:49:16Z cmmaj $
 */
public class HierarchicalSearchFilterImpl implements SearchFilter {

    public HierarchicalSearchFilterImpl(String linkProperty, String objectUri) {
        this.linkProperty = linkProperty;
        this.objectUri = objectUri;
    }

    public String getSparqlFragment() {

        return new StringBuilder().append("?id ").append("<").append(linkProperty).append(">")
                .append(" <").append(objectUri).append("> .\n").toString();

    }

    private String linkProperty;
    private String objectUri;
}
