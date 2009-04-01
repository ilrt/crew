package net.crew_vre.events.dao;

import net.crew_vre.events.domain.Event;

/**
 * <p>A Data Access Object to find event details.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: EventDao.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface EventDao {

    Event findEventById(final String id);

}
