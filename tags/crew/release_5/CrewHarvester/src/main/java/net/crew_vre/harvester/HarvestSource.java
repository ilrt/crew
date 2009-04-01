package net.crew_vre.harvester;

import java.util.Date;

/**
 * @author Damian Steer (d.steer@bristol.ac.uk)
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HarvestSource.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface HarvestSource {

    /**
     * The URL of the harvestable resource. This also acts as the URI of an RDF resource
     * and so *no* setter method for Location is provided.
     *
     * @return the URL of the harvestable resource.
     */
    public String getLocation();

    public Date getLastVisited();

    public String getLastStatus();

    public boolean isBlocked();

    public String getName();

    public String getDescription();

}
