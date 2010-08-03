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

import java.util.Set;
import java.util.HashSet;

import net.crew_vre.domain.DomainObject;

/**
 * <p>A role represents what someone or something has done - author,
 * presenter, barista etc.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: Role.java 1188 2009-03-31 13:09:20Z cmmaj $
 */
public class Role extends DomainObject {

    /**
     * The name of the role, e.g. Author.
     */
    private String name;

    /**
     * A list of events this role is associated with.
     */
    private Set<Event> roleAt = new HashSet<Event>();

    /**
     * The person who holds the role.
     */
    private Person person;

    public Role() { }

    public String getId() {
        return getUri();
    }

    public void setId(final String id) {
        setUri(id);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Set<Event> getRoleAt() {
        return roleAt;
    }

    public void setRoleAt(final Set<Event> roleAt) {
        this.roleAt = roleAt;
    }

    public Person getHeldBy() {
        return person;
    }

    public void setHeldBy(final Person person) {
        this.person = person;
    }

    /**
     * <p>Equality of two roles is based on the equality of the URI.</p>
     *
     * @param o the object to be compared against
     * @return true or false that o is equal to this Event
     */
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || !(o instanceof Role)) {
            return false;
        }

        Role other = (Role) o;

        return this.getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getUri().hashCode();
    }
}
