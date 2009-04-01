package org.ilrt.dibden.web.command;

public class ListGroupForm {

    public ListGroupForm() {
    }

    public ListGroupForm(String groupId, String deleteGroup, String newGroup, String editGroup) {
        this.groupId = groupId;
        this.deleteGroup = deleteGroup;
        this.newGroup = newGroup;
        this.editGroup = editGroup;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getDeleteGroup() {
        return deleteGroup;
    }

    public void setDeleteGroup(String deleteGroup) {
        this.deleteGroup = deleteGroup;
    }

    public String getNewGroup() {
        return newGroup;
    }

    public void setNewGroup(String newGroup) {
        this.newGroup = newGroup;
    }

    public String getEditGroup() {
        return editGroup;
    }

    public void setEditGroup(String editGroup) {
        this.editGroup = editGroup;
    }

    private String groupId;
    private String deleteGroup;
    private String newGroup;
    private String editGroup;
}
