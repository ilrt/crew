package net.crew_vre.events.domain;

/**
 * <p>The location object represents a geographical location for an event,
 * e.g. Bristol. The data will represent a location that is held in a SKOS
 * taxonomy. The details of the place (venue) where the event occured is
 * held in a <code>Place</code> object.</p>
 *
 * @see Place
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: Location.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class Location extends DomainObject {

    /**
     * Unique indentifier (URI) for the location
     */
    private String id;

    /**
     * The name of the location
     */
    private String name;

    public Location() { }

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
