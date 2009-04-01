package net.crew_vre.events.dao;

import net.crew_vre.events.domain.Place;

/**
 * <p>A Data Access Object that provides access to places.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PlaceDao.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface PlaceDao {

    Place findPlaceById(final String id);

}
