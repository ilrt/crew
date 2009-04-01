package net.crew_vre.events.domain;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.List;
import java.util.ArrayList;

/**
 * <p>Represents an Event. This might be the "main" event such as a conference
 * or workshop, but can also be a lecture, tutorial, tea break etc.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: Event.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class Event extends DomainObject {

    /**
     * Unique indentifier (URI) for the event
     */
    private String id;

    /**
     * The title of the event
     */
    private String title;

    /**
     * The description of the event
     */
    private String description;

    /**
     * Start date and time of the event
     */
    private DateTime startDate;

    /**
     * End date and time of the event
     */
    private DateTime endDate;

    /**
     * String representing the URL for the event's programme
     */
    private String programme;

    /**
     * String representing the URL for the event's proceedings
     */
    private String proceedings;

    /**
     * Location(s) used by the event - it might be possible for an event to use
     * multiple locations such as a main room and an overflow room.
     */
    private List<Place> places = new ArrayList<Place>();

    /**
     * Location - from a location taxonomy
     */
    private List<Location> locations = new ArrayList<Location>();

    /**
     * Tags assigned to the event
     */
    private List<String> tags = new ArrayList<String>();

    /**
     * Subject areas assigned to the event
     */
    private List<Subject> subjects = new ArrayList<Subject>();

    /**
     * Events that have an "hasPart" relationship with this event
     */
    private List<Event> parts = new ArrayList<Event>();

    /**
     * Events that this Event has a "partOf" relationship
     */
    private List<Event> partOf = new ArrayList<Event>();

    /**
     * Hold the uri for a recording
     */
    private String recording;

    /**
     * Roles associated with an event
     */
    private List<Role> roles;

    /**
     * Papers associated with the event
     */
    private List<Paper> papers = new ArrayList<Paper>();
    
    public Event() { }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(final DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(final DateTime endDate) {
        this.endDate = endDate;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(final String programme) {
        this.programme = programme;
    }

    public String getProceedings() {
        return proceedings;
    }

    public void setProceedings(final String proceedings) {
        this.proceedings = proceedings;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(final List<Subject> subjects) {
        this.subjects = subjects;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(final List<Location> locations) {
        this.locations = locations;
    }

    public List<Event> getParts() {
        return parts;
    }

    public void setParts(final List<Event> parts) {
        this.parts = parts;
    }

    public List<Event> getPartOf() {
        return partOf;
    }

    public void setPartOf(final List<Event> partOf) {
        this.partOf = partOf;
    }

    public String getRecording() {
        return recording;
    }

    public void setRecording(final String recording) {
        this.recording = recording;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(final List<Role> roles) {
        this.roles = roles;
    }

    public List<Paper> getPapers() {
        return papers;
    }

    public void setPapers(List<Paper> papers) {
        this.papers = papers;
    }

    /**
     * <p>Equality of two events is based on the equality of the URI.</p>
     *
     * @param o the object to be compared against
     * @return true or false that o is equal to this Event
     */
    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || !(o instanceof Event)) {
            return false;
        }

        Event other = (Event) o;

        return this.getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }


    /**
     * @return true or false whether or not the event occurs on a single day
     */
    public boolean isSingleDay() {

        Period period = new Period(startDate, endDate);

        /**
         * I need to relook at this - zero also indicates "not supported"
         * in the Joda Time API for Period.
         */
        return period.getDays() == 0;

    }

}
