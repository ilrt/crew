package net.crew_vre.events.service;

import net.crew_vre.events.domain.Event;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: MainEventService.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface MainEventService {

    Event getEventById(String eventId);

    List<Event> getAllEvents();

    List<Event> getAllEvents(final int limit, final int offset);

    List<Event> getEventsByDate(final LocalDate startDate, final LocalDate endDate);

    List<Event> getEventsByDate(final LocalDate startDate, final LocalDate endDate, final int limit,
                                final int offset);

    List<Event> getEventsByCreationDate(final DateTime startDate, final DateTime endDate);

    List<Event> getEventsByCreationDate(final DateTime startDate, final DateTime endDate,
                                        final int limit, final int offset);

    int getTotalEvents();

    int getTotalEventsByDate(final LocalDate startDate, final LocalDate endDate);

    List<Event> getEventWithConstraint(final String constraint);

    List<Event> getEventWithConstraint(final String constraint, final int limit, final int offset);

    int getTotalEventWithConstraint(final String constraint);

}
