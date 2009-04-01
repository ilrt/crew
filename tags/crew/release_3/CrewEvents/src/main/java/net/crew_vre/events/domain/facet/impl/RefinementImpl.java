package net.crew_vre.events.domain.facet.impl;

import net.crew_vre.events.domain.facet.Refinement;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: RefinementImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class RefinementImpl implements Refinement {

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
