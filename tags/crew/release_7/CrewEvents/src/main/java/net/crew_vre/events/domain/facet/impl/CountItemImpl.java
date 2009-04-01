package net.crew_vre.events.domain.facet.impl;

import net.crew_vre.events.domain.facet.CountItem;
import net.crew_vre.events.domain.DomainObject;

import java.io.Serializable;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: CountItemImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class CountItemImpl extends DomainObject implements CountItem, Serializable  {

    public CountItemImpl() {}

    public CountItemImpl(String graph, String id) {
        this.graph = graph;
        this.id = id;
    }

    public CountItemImpl(String graph, String id, String value) {
        this.graph = graph;
        this.id = id;
        this.value = value;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * <p>The graph in which the refinement item is located.</p>
     */
    private String graph;

    /**
     * <p>The ID (URI) of the refinement item. The item might be an event, person, paper.</p>
     */
    private String id;


    private String value = null;


}
