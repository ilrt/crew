package org.ilrt.dibden.web.command;

import org.ilrt.dibden.domain.Group;

import java.util.List;
import java.util.Set;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class UserForm {

    public UserForm() {
    }

    public UserForm(String username, String name, String email, Set<Group> userGroups,
                    List<Group> groups) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.userGroups = userGroups;
        this.groups = groups;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Group> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(Set<Group> userGroups) {
        this.userGroups = userGroups;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public String getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(String userGroupId) {
        this.userGroupId = userGroupId;
    }

    public String getAddGroupId() {
        return addGroupId;
    }

    public void setAddGroupId(String addGroupId) {
        this.addGroupId = addGroupId;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getRemoveGroup() {
        return removeGroup;
    }

    public void setRemoveGroup(String removeGroup) {
        this.removeGroup = removeGroup;
    }

    public String getAddGroup() {
        return addGroup;
    }

    public void setAddGroup(String addGroup) {
        this.addGroup = addGroup;
    }

    private String username;
    private String name;
    private String email;
    private Set<Group> userGroups;
    private List<Group> groups;
    private String userGroupId;
    private String addGroupId;
    private String updateUser;
    private String removeGroup;
    private String addGroup;
}
