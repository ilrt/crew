package net.crew_vre.harvester.web;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HarvestSourceForm.java 1092 2009-03-11 19:01:38Z cmmaj $
 */
public class HarvestSourceForm {

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public Date getLastVisited() {
        return lastVisited;
    }

    public void setLastVisited(Date lastVisited) {
        this.lastVisited = lastVisited;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus;
    }

    public String getAddButton() {
        return addButton;
    }

    public void setAddButton(String addButton) {
        this.addButton = addButton;
    }

    public String getUpdateButton() {
        return updateButton;
    }

    public void setUpdateButton(String updateButton) {
        this.updateButton = updateButton;
    }

    public String getCancelButton() {
        return cancelButton;
    }

    public void setCancelButton(String cancelButton) {
        this.cancelButton = cancelButton;
    }

    public List<HarvestSourceAuthority> getAuthorityList() {
        return authorityList;
    }

    public void setAuthorityList(List<HarvestSourceAuthority> authorityList) {
        this.authorityList = authorityList;
    }

    private String location;
    private String name;
    private String description;
    private Date lastVisited;
    private String lastStatus;
    private boolean blocked;
    private String addButton;
    private String updateButton;
    private String cancelButton;
    private List<HarvestSourceAuthority> authorityList = new ArrayList<HarvestSourceAuthority>();
}
