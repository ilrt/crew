package net.crew_vre.web.facade.impl;

import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventPart;
import net.crew_vre.events.service.MainEventService;
import net.crew_vre.web.facade.ListEventsFacade;
import net.crew_vre.web.facet.SearchFilter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.ListIterator;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: ListEventsFacadeImpl.java 1171 2009-03-26 17:36:37Z arowley $
 */
public class ListEventsFacadeImpl implements ListEventsFacade {

    public ListEventsFacadeImpl(MainEventService mainEventService) {
        this.mainEventService = mainEventService;
    }

    public List<EventPart> displayEvents() {

        return mainEventService.getAllEvents();
    }

    public List<EventPart> displayEvents(final List<SearchFilter> filters) {

        String filterString = filterToString(filters);

        return mainEventService.getEventWithConstraint(filterString);
    }

    public List<EventPart> displayEvents(final List<SearchFilter> filters, final int limit,
                                     final int offset) {

        String filterString = filterToString(filters);

        return mainEventService.getEventWithConstraint(filterString, limit, offset);
    }

    public List<EventPart> displayEvents(final int limit, final int offset) {

        return mainEventService.getAllEvents(limit, offset);
    }

    public List<EventPart> displayUpcomingEvents() {

        LocalDate today = new LocalDate();
        LocalDate future = today.plusMonths(1);

        List<EventPart> results =
            mainEventService.getEventsByDateAsc(today, future);
        filterEmptyEvents(results);
        return results;
    }

    public List<EventPart> displayUpcomingEvents(int limit, int offset) {

        LocalDate today = new LocalDate();
        LocalDate future = today.plusMonths(1);

        List<EventPart> results = 
            mainEventService.getEventsByDateAsc(today, future, limit, offset);
        filterEmptyEvents(results);
        return results;
    }

    public List<EventPart> displayRecentlyAddedEvents(int days) {

        DateTime end = new DateTime();
        DateTime start = end.minusDays(days);

        List<EventPart> results = 
            mainEventService.getEventsByCreationDate(start, end);
        filterEmptyEvents(results);
        return results;
    }

    public List<EventPart> displayRecentlyAddedEvents(int days, int limit, int offset) {

        DateTime end = new DateTime();
        DateTime start = end.minusDays(days);

        List<EventPart> results = 
            mainEventService.getEventsByCreationDate(start, end, limit, offset);
        filterEmptyEvents(results);
        return results;
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

    private void filterEmptyEvents(List<EventPart> events) {
        for (ListIterator iter = events.listIterator(); iter.hasNext();) {
            EventPart event = (EventPart) iter.next();
            if (event.getId() == null || event.getId().equals("")) {
                iter.remove();
            }
        }
    }

    private MainEventService mainEventService;
}
