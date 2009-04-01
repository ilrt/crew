/**
 * Copyright (c) 2008-2009, University of Bristol
 * Copyright (c) 2008-2009, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package net.crew_vre.events.domain;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.ArrayList;

/**
 * <p>Represents an Event. This might be the "main" event such as a conference
 * or workshop, but can also be a lecture, tutorial, tea break etc.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: Event.java 1188 2009-03-31 13:09:20Z cmmaj $
 */
public class Event extends EventPart {

    /**
     * String representing the URL for the event's programme
     */
    private String programme = null;

    /**
     * String representing the URL for the event's proceedings
     */
    private String proceedings = null;

    /**
     * Location(s) used by the event - it might be possible for an event to use
     * multiple locations such as a main room and an overflow room.
     */
    private List<PlacePart> places = new ArrayList<PlacePart>();

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
    private List<EventPart> parts = new ArrayList<EventPart>();

    /**
     * Events that this Event has a "partOf" relationship
     */
    private List<EventParent> partOf = new ArrayList<EventParent>();
    

    /**
     * Roles associated with an event
     */
    private List<Role> roles = null;

    /**
     * Papers associated with the event
     */
    private List<Paper> papers = new ArrayList<Paper>();

    public Event() { }

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

    public List<PlacePart> getPlaces() {
        return places;
    }

    public void setPlaces(List<PlacePart> places) {
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

    public List<EventPart> getParts() {
        return parts;
    }

    public void setParts(final List<EventPart> parts) {
        this.parts = parts;
    }

    public List<EventParent> getPartOf() {
        return partOf;
    }

    public void setPartOf(final List<EventParent> partOf) {
        this.partOf = partOf;
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

}
