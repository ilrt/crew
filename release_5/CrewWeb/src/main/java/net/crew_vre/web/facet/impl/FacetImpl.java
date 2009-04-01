package net.crew_vre.web.facet.impl;

import net.crew_vre.web.facet.FacetState;
import net.crew_vre.web.facet.Facet;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: FacetImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class FacetImpl implements Facet {

    public FacetImpl() {
    }

    public FacetImpl(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public FacetState getState() {
        return state;
    }

    public void setState(final FacetState state) {
        this.state = state;
    }

    private String name;

    private FacetState state;
}
