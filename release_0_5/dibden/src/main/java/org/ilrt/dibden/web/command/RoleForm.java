package org.ilrt.dibden.web.command;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: RoleForm.java 89 2008-09-09 14:48:05Z cmmaj $
 *
 **/
public class RoleForm {

    public RoleForm() {
    }

    public RoleForm(String roleId, String name, String description) {
        this.roleId = roleId;
        this.name = name;
        this.description = description;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
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

    public String getUpdateRole() {
        return updateRole;
    }

    public void setUpdateRole(String updateRole) {
        this.updateRole = updateRole;
    }

    public String getAddRole() {
        return addRole;
    }

    public void setAddRole(String addRole) {
        this.addRole = addRole;
    }

    private String roleId;
    private String name;
    private String description;
    private String updateRole;
    private String addRole;
}
