package net.crew_vre.events.dao;

import net.crew_vre.events.domain.Location;

import java.util.List;

/**
 * <p>A Data Access Object to find SKOS locations.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: LocationDao.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface LocationDao {

    List<Location> findLocationByEvent(final String id);

}
