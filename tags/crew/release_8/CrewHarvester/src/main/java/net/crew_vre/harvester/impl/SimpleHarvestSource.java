package net.crew_vre.harvester.impl;

import net.crew_vre.harvester.HarvestSource;

import java.util.Date;

/**
 * @author Damian Steer (d.steer@bristol.ac.uk)
 * @author Mioke Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: SimpleHarvestSource.java 929 2008-11-18 15:28:41Z cmmaj $
 */
public class SimpleHarvestSource implements HarvestSource {


    public SimpleHarvestSource(String location, boolean isBlocked, String name,
                               String description) {
        this.location = location;
        this.isBlocked = isBlocked;
        this.lastVisited = new Date(0);
        this.lastStatus = null;
        this.name = name;
        this.description = description;
    }

    public SimpleHarvestSource(String location, String name, String description) {
        this(location, false, name, description);
    }

    public SimpleHarvestSource(String location) {
        this(location, false, null, null);
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
        return isBlocked;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    private String location;
    private boolean isBlocked;
    private Date lastVisited;
    private String lastStatus;
    private String name;
    private String description;
}
