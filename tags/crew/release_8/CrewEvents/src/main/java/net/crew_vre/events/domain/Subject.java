package net.crew_vre.events.domain;

/**
 * <p>Represents a skos subject.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: Subject.java 792 2008-07-07 14:44:01Z cmmaj $
 */
public class Subject extends DomainObject {

    /**
     * Unique indentifier (URI) for the event.
     */
    private String id;

    /**
     * The name of the role.
     */
    private String name;

    public Subject() { }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

}
