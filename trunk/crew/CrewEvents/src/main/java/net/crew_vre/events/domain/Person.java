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

import java.util.List;
import java.util.ArrayList;

import net.crew_vre.domain.DomainObject;

/**
 * <p>Represents a person - a human!.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: Person.java 1188 2009-03-31 13:09:20Z cmmaj $
 */
public class Person extends DomainObject {

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
        return getUri();
    }

    public void setId(final String id) {
        setUri(id);
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
