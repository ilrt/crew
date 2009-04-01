package net.crew_vre.events.domain;

import java.util.Set;
import java.util.HashSet;

/**
 * <p>The Place object represents the location/venue where an event is being
 * held. It holds geo information that might be useful for maps. A place can
 * also have other places - for example, a venue might have a number
 * of rooms.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: Place.java 792 2008-07-07 14:44:01Z cmmaj $
 */
public class Place extends DomainObject {

    /**
     * Unique indentifier (URI) for the place.
     */
    private String id;

    /**
     * The name of the place.
     */
    private String title;

    /**
     * The latitude value (decimal).
     */
    private Float latitude;

    /**
     * The longitude value (decimal).
     */
    private Float longitude;

    /**
     * The altitude value (decimal).
     */
    private Float altitude;

    /**
     * List of other places that this place might hold.
     */
    private Set<Place> places = new HashSet<Place>();

    public Place() { }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(final Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(final Float longitude) {
        this.longitude = longitude;
    }

    public Float getAltitude() {
        return altitude;
    }

    public void setAltitude(final Float altitude) {
        this.altitude = altitude;
    }

    public Set<Place> getLocations() {
        return places;
    }

    public void setLocations(final Set<Place> places) {
        this.places = places;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || !(o instanceof Place)) {
            return false;
        }

        Place other = (Place) o;

        return this.getId().equals(other.getId());

    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

}
