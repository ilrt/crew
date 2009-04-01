package net.crew_vre.events.dao;

import net.crew_vre.events.domain.Event;

/**
 * <p>A Data Access Object to find event details.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: EventDao.java 408 2007-11-15 16:13:09Z cmmaj $
 */
public interface EventDao {

    Event findEventById(final String id);

}
