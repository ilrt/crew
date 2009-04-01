package org.ilrt.dibden.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
@Entity
@Table(name = "GROUPS")
public class Group {

    public Group() {
    }

    public Group(String groupId, String name, String description) {
        this.groupId = groupId;
        this.name = name;
        this.description = description;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<User> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(Set<User> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Group group = (Group) o;

        if (groupId != null ? !groupId.equals(group.groupId) : group.groupId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = groupId != null ? groupId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (groupMembers != null ? groupMembers.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        return result;
    }

    @Id
    @Column(name = "GROUPID")
    private String groupId;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @ManyToMany(mappedBy = "groups", fetch = FetchType.EAGER)
    private Set<User> groupMembers = new HashSet<User>();

    @ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER)
    @JoinTable(
            name = "GROUPS_ROLES",
            joinColumns = {@JoinColumn(name = "GROUPID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLEID")}
    )
    private Set<Role> roles = new HashSet<Role>();


}
