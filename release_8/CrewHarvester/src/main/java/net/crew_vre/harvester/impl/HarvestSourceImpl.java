package net.crew_vre.harvester.impl;

import net.crew_vre.harvester.HarvestSource;

import java.util.Date;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HarvestSourceImpl.java 929 2008-11-18 15:28:41Z cmmaj $
 */
public class HarvestSourceImpl implements HarvestSource {

    public HarvestSourceImpl(String location, String name, String description, boolean blocked) {
        this(location, name, description, null, null, blocked);
    }

    public HarvestSourceImpl(String location, String name, String description,
                             Date lastVisited, String lastStatus, boolean blocked) {
        this.location = location;
        this.name = name;
        this.description = description;
        this.lastVisited = lastVisited;
        this.lastStatus = lastStatus;
        this.blocked = blocked;
    }

    public String getLocation() {
        return location;
    }

    public Date getLastVisited() {
        return lastVisited;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setLastStatus(String statusText) {
        this.lastStatus = statusText;
    }

    public void setLastVisited(Date date) {
        this.lastVisited = date;
    }

    public void setIsBlocked(boolean blocked) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public void setName(String name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public void setDescripton(String description) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("HarvestSourceImpl");
        sb.append("{location='").append(location).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", lastVisited=").append(lastVisited);
        sb.append(", lastStatus='").append(lastStatus).append('\'');
        sb.append(", blocked=").append(blocked);
        sb.append('}');
        return sb.toString();
    }

    private String location;
    private String name;
    private String description;
    private Date lastVisited;
    private String lastStatus;
    private boolean blocked;


    
}
