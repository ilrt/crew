package net.crew_vre.events.domain;

import java.util.Set;
import java.util.HashSet;

/**
 * <p>A role represents what someone or something has done - author,
 * presenter, barista etc.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: Role.java 792 2008-07-07 14:44:01Z cmmaj $
 */
public class Role extends DomainObject {

    /**
     * Unique indentifier (URI) for the role.
     */
    private String id;

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
        return id.hashCode();
    }
}
