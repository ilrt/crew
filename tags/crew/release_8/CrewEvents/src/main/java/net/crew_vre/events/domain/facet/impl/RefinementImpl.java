package net.crew_vre.events.domain.facet.impl;

import net.crew_vre.events.domain.facet.Refinement;

import java.io.Serializable;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: RefinementImpl.java 948 2008-11-28 14:25:26Z cmmaj $
 */
public class RefinementImpl implements Refinement, Serializable {

    public RefinementImpl() {

    }

    public RefinementImpl(String id, String title, String label) {
        this.id = id;
        this.title = title;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        if (title != null) {
            return title;
        } else if (label != null) {
            return label;
        } else {
            return id;
        }
    }

    private String id;
    private String title;
    private String label;
}
