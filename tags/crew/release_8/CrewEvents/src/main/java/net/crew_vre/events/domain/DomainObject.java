package net.crew_vre.events.domain;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DomainObject.java 793 2008-07-07 14:45:19Z cmmaj $
 */
public abstract class DomainObject {

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    private String graph;
}
