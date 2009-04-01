package net.crew_vre.events.service.impl;

import net.crew_vre.events.dao.MainEventDao;
import net.crew_vre.events.domain.Event;
import net.crew_vre.events.service.MainEventService;
import org.joda.time.LocalDate;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: MainEventServiceImpl.java 948 2008-11-28 14:25:26Z cmmaj $
 */
public class MainEventServiceImpl implements MainEventService {

    public MainEventServiceImpl(final MainEventDao mainEventDao) {
        this.mainEventDao = mainEventDao;
    }

    public Event getEventById(final String eventId) {

        return mainEventDao.findEventById(eventId);
    }

    public List<Event> getAllEvents() {
        return mainEventDao.findAllEvents();
    }

    public List<Event> getAllEvents(final int limit, final int offset) {
        return mainEventDao.findAllEvents(limit, offset);
    }

    public List<Event> getEventsByDate(final LocalDate startDate, final LocalDate endDate) {
        return mainEventDao.findEventsByDate(startDate, endDate);
    }

    public List<Event> getEventsByDate(final LocalDate startDate, final LocalDate endDate,
                                       final int limit, final int offset) {
        return mainEventDao.findEventsByDate(startDate, endDate, limit, offset);
    }

    public List<Event> getEventsByCreationDate(final DateTime startDate, final DateTime endDate) {
        return mainEventDao.findEventsByCreationDate(startDate, endDate);
    }

    public List<Event> getEventsByCreationDate(DateTime startDate, DateTime endDate, int limit,
                                               int offset) {
        return mainEventDao.findEventsByCreationDate(startDate, endDate, limit, offset);
    }

    public int getTotalEvents() {
        return mainEventDao.findAllEvents().size();
    }

    public int getTotalEventsByDate(final LocalDate startDate, final LocalDate endDate) {
        return mainEventDao.findEventsByDate(startDate, endDate).size();
    }

    public List<Event> getEventWithConstraint(String constraint) {
        return mainEventDao.findEventsWithConstraint(constraint);
    }

    public List<Event> getEventWithConstraint(String constraint, int limit, int offset) {
        return mainEventDao.findEventsWithConstraint(constraint, limit, offset);
    }

    public int getTotalEventWithConstraint(String constraint) {
        return mainEventDao.findEventsWithConstraint(constraint).size();
    }

    /**
     * DAO for "main event" details
     */
    private MainEventDao mainEventDao;
}
