package org.ilrt.dibden.web.command;

import org.ilrt.dibden.Role;

import java.util.List;
import java.util.Set;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 *
 **/
public class UserForm {

    public UserForm() {
    }

    public UserForm(String username, String name, String email,
                    Set<Role> userRoles, List<Role> roles) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.userRoles = userRoles;
        this.roles = roles;
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

    public Set<Role> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<Role> userRoles) {
        this.userRoles = userRoles;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(String userRoleId) {
        this.userRoleId = userRoleId;
    }

    public String getAddRoleId() {
        return addRoleId;
    }

    public void setAddRoleId(String addRoleId) {
        this.addRoleId = addRoleId;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
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

    private String username;
    private String name;
    private String email;
    private Set<Role> userRoles;
    private List<Role> roles;
    private String userRoleId;
    private String addRoleId;
    private String updateUser;
    private String removeRole;
    private String addRole;
}
