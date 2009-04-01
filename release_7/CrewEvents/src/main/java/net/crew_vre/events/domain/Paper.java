package net.crew_vre.events.domain;

import java.util.List;
import java.util.ArrayList;

/**
 * <p>Represents a paper - an academic paper.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: Paper.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class Paper extends DomainObject {

    /**
     * Unique indentifier (URI) for the paper.
     */
    private String id;

    /**
     * The title of the paper.
     */
    private String title;

    /**
     * The description / abstract of the paper
     */
    private String description;

    /**
     * Can it be downloaded?
     */
    private boolean retrievable = false;

    /**
     * List of people who are authors.
     */
    private List<Person> authors = new ArrayList<Person>();

    public Paper() { }

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

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRetrievable() {
        return retrievable;
    }

    public void setRetrievable(boolean retrievable) {
        this.retrievable = retrievable;
    }

    public List<Person> getAuthors() {
        return authors;
    }

    public void setAuthors(final List<Person> authors) {
        this.authors = authors;
    }
}
