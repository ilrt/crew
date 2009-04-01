package net.crew_vre.harvester;

import java.util.Date;
import java.util.List;

/**
 * <p>A DAO for handling harvester sources.</p>
 *
 * @version $Id: HarvesterDao.java 1132 2009-03-20 19:05:47Z cmmaj $
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public interface HarvesterDao {

    /**
     * Create a new harvester source.
     *
     * @param location    the location of the source - this will act as the URI.
     * @param name        the name of the source.
     * @param description a description of the source.
     * @param isBlocked   is the source blocked? i.e. harvestable?
     * @return a POJO encapsulating the harvester source.
     */
    HarvestSource createHarvestSource(String location, String name, String description,
                                      boolean isBlocked);

    /**
     * Update an existing harvester source.
     *
     * @param location    the location of the source - this will act as the URI.
     * @param name        the name of the source.
     * @param description a description of the source.
     * @param lastVisited the date that the source was last harvested.
     * @param lastStatus  the status of the last harvest.
     * @param blocked     is the source blocked? i.e. harvestable?
     */
    void updateHarvestSource(String location, String name, String description, Date lastVisited,
                             String lastStatus, boolean blocked);

    /**
     * Find the harvester source with the specified URI.
     *
     * @param location the location (URI) of the source.
     * @return the source specified by the URI.
     */
    HarvestSource findSource(String location);

    /**
     * Find all harvester sources.
     *
     * @return a list of all harvester sources.
     */
    List<HarvestSource> findAllSources();

    /**
     * Find all harvester sources that are not blocked.
     *
     * @return a list of all harvester sources that are not blocked.
     */
    List<HarvestSource> findAllPermittedSources();

    /**
     * Delete the harvester source identified by the URI
     *
     * @param location the URI of the harvester source.
     */
    void deleteSource(String location);

    /**
     * Delete all data related to a source.
     * @param location the location of the source.
     */
    void deleteData(String location);
}
