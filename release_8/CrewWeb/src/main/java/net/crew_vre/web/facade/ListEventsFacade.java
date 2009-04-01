package net.crew_vre.web.facade;

import net.crew_vre.events.domain.Event;
import net.crew_vre.web.facet.SearchFilter;

import java.util.List;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: ListEventsFacade.java 1011 2009-01-27 19:44:47Z cmmaj $
 */
public interface ListEventsFacade {

    List<Event> displayEvents();

    List<Event> displayEvents(List<SearchFilter> filters);

    List<Event> displayEvents(List<SearchFilter> filters, int limit, int offset);

    List<Event> displayEvents(int limit, int offset);

    List<Event> displayUpcomingEvents();

    List<Event> displayUpcomingEvents(int limit, int offset);

    List<Event> displayRecentlyAddedEvents(int days);

    List<Event> displayRecentlyAddedEvents(int days, int limit, int offset);

    int totalEventsAvailable();

    int totalEventsAvailable(List<SearchFilter> filters);

}
