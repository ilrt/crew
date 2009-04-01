package net.crew_vre.web.facade.impl;

import net.crew_vre.events.domain.Event;
import net.crew_vre.events.service.MainEventService;
import net.crew_vre.web.facade.ListEventsFacade;
import net.crew_vre.web.facet.SearchFilter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: ListEventsFacadeImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class ListEventsFacadeImpl implements ListEventsFacade {

    public ListEventsFacadeImpl(MainEventService mainEventService) {
        this.mainEventService = mainEventService;
    }

    public List<Event> displayEvents() {

        return mainEventService.getAllEvents();
    }

    public List<Event> displayEvents(final List<SearchFilter> filters) {

        String filterString = filterToString(filters);

        return mainEventService.getEventWithConstraint(filterString);
    }

    public List<Event> displayEvents(final List<SearchFilter> filters, final int limit,
                                     final int offset) {

        String filterString = filterToString(filters);

        return mainEventService.getEventWithConstraint(filterString, limit, offset);
    }

    public List<Event> displayEvents(final int limit, final int offset) {

        return mainEventService.getAllEvents(limit, offset);
    }

    public List<Event> displayUpcomingEvents() {

        LocalDate today = new LocalDate();
        LocalDate future = today.plusMonths(1);


        return mainEventService.getEventsByDate(today, future);
    }

    public List<Event> displayUpcomingEvents(int limit, int offset) {

        LocalDate today = new LocalDate();
        LocalDate future = today.plusMonths(1);

        return mainEventService.getEventsByDate(today, future, limit, offset);
    }

    public List<Event> displayRecentlyAddedEvents(int days) {

        DateTime end = new DateTime();
        DateTime start = end.minusDays(days);

        return mainEventService.getEventsByCreationDate(start, end);
    }

    public List<Event> displayRecentlyAddedEvents(int days, int limit, int offset) {

        DateTime end = new DateTime();
        DateTime start = end.minusDays(days);

        return mainEventService.getEventsByCreationDate(start, end, limit, offset);
    }

    public int totalEventsAvailable() {

        return mainEventService.getTotalEvents();
    }

    public int totalEventsAvailable(List<SearchFilter> filters) {

        String filterString = filterToString(filters);

        return mainEventService.getTotalEventWithConstraint(filterString);
    }

    private String filterToString(final List<SearchFilter> filters) {

        StringBuilder constraints = new StringBuilder();

        if (filters != null) {
            for (SearchFilter filter : filters) {
                constraints.append(filter.getSparqlFragment());
            }
        }

        return constraints.toString();
    }

    private MainEventService mainEventService;
}
