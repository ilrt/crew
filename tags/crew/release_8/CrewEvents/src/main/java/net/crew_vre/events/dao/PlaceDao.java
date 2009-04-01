package net.crew_vre.events.dao;

import net.crew_vre.events.domain.Place;

/**
 * <p>A Data Access Object that provides access to places.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PlaceDao.java 408 2007-11-15 16:13:09Z cmmaj $
 */
public interface PlaceDao {

    Place findPlaceById(final String id);

}
