package net.crew_vre.events.domain;

import java.util.List;
import java.util.ArrayList;

/**
 * <p>Represents a person - a human!.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: Person.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class Person extends DomainObject {

    /**
     * Unique indentifier (URI) for the event.
     */
    private String id;

    /**
     * Title - Mr, Ms, Dr etc.
     */
    private String title;

    /**
     * Family name of a person, e.g. Smith.
     */
    private String familyName;

    /**
     * Given name of a person, e.g. Fred.
     */
    private String givenName;

    /**
     * Full name for a person, e.g. "Fred Smith".
     */
    private String name;

    /**
     * URL for the person's homepage.
     */
    private String homepage;

    /**
     * URL for the homepage of the person's workplace.
     */
    private String workplaceHomepage;

    /**
     * URL for a person's Flickr account.
     */
    private String flickrHomepage;

    /**
     * List of roles that a person holds.
     */
    private List<Role> roles = new ArrayList<Role>();

    public Person() { }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(final String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(final String givenName) {
        this.givenName = givenName;
    }

    public String getName() {

        if (name == null) {
            if (familyName != null && givenName != null) {
                return givenName + " " + familyName;
            }
        }

        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(final String homepage) {
        this.homepage = homepage;
    }

    public String getWorkplaceHomepage() {
        return workplaceHomepage;
    }

    public void setWorkplaceHomepage(final String workplaceHomepage) {
        this.workplaceHomepage = workplaceHomepage;
    }

    public String getFlickrHomepage() {
        return flickrHomepage;
    }

    public void setFlickrHomepage(final String flickrHomepage) {
        this.flickrHomepage = flickrHomepage;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(final List<Role> roles) {
        this.roles = roles;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

}
