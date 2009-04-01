package net.crew_vre.harvester;

import java.util.List;
import java.util.Date;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HarvesterSourceManagementFacade.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface HarvesterSourceManagementFacade {

    HarvestSource getSource(String id);

    List<HarvestSource> getAllSources();

    List<HarvestSource> getAllPermittedSources();

    void addSource(String location, String title, String description, boolean isBlocked);

    void updateSource(String location, String name, String description, Date lastVisited,
                             String lastStatus, boolean blocked);

    void removeSource(String location);

    String harvestSource(String location);
}
