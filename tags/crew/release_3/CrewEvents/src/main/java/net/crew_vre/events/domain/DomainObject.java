package net.crew_vre.events.domain;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DomainObject.java 1132 2009-03-20 19:05:47Z cmmaj $
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
