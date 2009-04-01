package org.ilrt.dibden.web.command;

import org.ilrt.dibden.domain.Group;
import org.ilrt.dibden.domain.Role;

import java.util.Set;
import java.util.List;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: GroupForm.java 117 2008-09-15 16:36:31Z cmmaj $
 *
 **/
public class GroupForm {

    public GroupForm() {
    }

    public GroupForm(String groupId, String name, String description) {
        this.groupId = groupId;
        this.name = name;
        this.description = description;
    }

    public GroupForm(String groupId, String name, String description, Set<Role> groupRoles,
                    List<Role> roles) {
        this.groupId = groupId;
        this.name = name;
        this.description = description;
        this.groupRoles = groupRoles;
        this.roles = roles;
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

    public String getUpdateGroup() {
        return updateGroup;
    }

    public void setUpdateGroup(String updateGroup) {
        this.updateGroup = updateGroup;
    }

    public String getAddGroup() {
        return addGroup;
    }

    public void setAddGroup(String addGroup) {
        this.addGroup = addGroup;
    }

    public Set<Role> getGroupRoles() {
        return groupRoles;
    }

    public void setGroupRoles(Set<Role> groupRoles) {
        this.groupRoles = groupRoles;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getGroupRoleId() {
        return groupRoleId;
    }

    public void setGroupRoleId(String groupRoleId) {
        this.groupRoleId = groupRoleId;
    }

    public String getAddRoleId() {
        return addRoleId;
    }

    public void setAddRoleId(String addRoleId) {
        this.addRoleId = addRoleId;
    }

    public String getRemoveRole() {
        return removeRole;
    }

    public void setRemoveRole(String removeRole) {
        this.removeRole = removeRole;
    }

    public String getAddRole() {
        return addRole;
    }

    public void setAddRole(String addRole) {
        this.addRole = addRole;
    }

    private String groupId;
    private String name;
    private String description;
    private String updateGroup;
    private String addGroup;
    private Set<Role> groupRoles;
    private List<Role> roles;
    private String groupRoleId;
    private String addRoleId;
    private String removeRole;
    private String addRole;
}