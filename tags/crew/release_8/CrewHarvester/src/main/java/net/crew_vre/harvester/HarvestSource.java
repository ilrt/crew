package net.crew_vre.harvester;

import java.util.Date;

/**
 * @author Damian Steer (d.steer@bristol.ac.uk)
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HarvestSource.java 929 2008-11-18 15:28:41Z cmmaj $
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
