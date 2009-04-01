package net.crew_vre.harvester.impl;

import net.crew_vre.harvester.HarvestSource;
import net.crew_vre.harvester.Harvester;
import net.crew_vre.harvester.HarvesterDao;
import net.crew_vre.harvester.HarvesterSourceManagementFacade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HarversterSourceManagementFacadeImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class HarversterSourceManagementFacadeImpl implements HarvesterSourceManagementFacade {

    public HarversterSourceManagementFacadeImpl(HarvesterDao harvesterDao,
                                                Harvester harvester) {
        this.harvesterDao = harvesterDao;
        this.harvester = harvester;
    }

    public HarvestSource getSource(String id) {
        return harvesterDao.findSource(id);
    }

    public List<HarvestSource> getAllSources() {
        return harvesterDao.findAllSources();
    }

    public List<HarvestSource> getAllPermittedSources() {
        return harvesterDao.findAllPermittedSources();
    }

    public void addSource(String location, String name, String description, boolean isBlocked) {
        // only add if the URI doesn't already exist
        if (getSource(location) == null) {
            harvesterDao.createHarvestSource(location, name, description, isBlocked);
        }
    }

    public void updateSource(String location, String name, String description, Date lastVisited,
                             String lastStatus, boolean blocked) {
        harvesterDao.updateHarvestSource(location, name, description, lastVisited, lastStatus,
                blocked);
    }

    public void removeSource(String location) {

        if (getSource(location) != null) {
            harvesterDao.deleteSource(location); // delete source
            harvesterDao.deleteData(location);   // delete data derived from source
        }
    }

    public String harvestSource(String location) {

        List<HarvestSource> harvestSources = new ArrayList<HarvestSource>();
        harvestSources.add(harvesterDao.findSource(location));
        return harvester.harvest(harvestSources);
    }

    private final HarvesterDao harvesterDao;
    private final Harvester harvester;
}
