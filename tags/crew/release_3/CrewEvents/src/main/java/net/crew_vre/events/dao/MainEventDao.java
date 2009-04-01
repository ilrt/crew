package net.crew_vre.events.dao;

import net.crew_vre.events.domain.Event;
import org.joda.time.DateTime;

import java.util.List;

/**
 * <p>A Data Access Object that provides access to "Main Events".</p>
 *
 * <p>In the ESWC2006 schema there are many different types of events and each
 * event might have zero to many parts. A conference might have tracks and
 * tracks might have sessions etc. To easily identify the main event, e.g. the
 * conference that has the sessions, the IUGO ontology had a type called
 * MainEvent.</p>
 *
 * <p>These methods provide basic details that will be used in displaying search
 * results. The EventDao implementation will be used to provide more detailed
 * information about an event.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: MainEventDao.java 1132 2009-03-20 19:05:47Z cmmaj $
 * @see EventDao
 */
public interface MainEventDao {

    Event findEventById(final String id);

    List<Event> findAllEvents();

    List<Event> findAllEvents(final int limit, final int offset);

    List<Event> findEventsByDate(final DateTime startDate, final DateTime endDate);

    List<Event> findEventsByDate(final DateTime startDate, final DateTime endDate, final int limit,
                                 final int offset);

    List<Event> findEventsWithConstraint(final String constraint);

    List<Event> findEventsWithConstraint(final String constraint, final int limit, int offset);

    List<Event> findEventsByCreationDate(final DateTime startDate, final DateTime endDate);

    List<Event> findEventsByCreationDate(final DateTime startDate, final DateTime endDate,
                                         final int limit, final int offset);

}
