package net.crew_vre.web.facade;

import net.crew_vre.events.domain.EventPart;
import net.crew_vre.web.facet.SearchFilter;

import java.util.List;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: ListEventsFacade.java 1171 2009-03-26 17:36:37Z arowley $
 */
public interface ListEventsFacade {

    List<EventPart> displayEvents();

    List<EventPart> displayEvents(List<SearchFilter> filters);

    List<EventPart> displayEvents(List<SearchFilter> filters, int limit, int offset);

    List<EventPart> displayEvents(int limit, int offset);

    List<EventPart> displayUpcomingEvents();

    List<EventPart> displayUpcomingEvents(int limit, int offset);

    List<EventPart> displayRecentlyAddedEvents(int days);

    List<EventPart> displayRecentlyAddedEvents(int days, int limit, int offset);

    int totalEventsAvailable();

    int totalEventsAvailable(List<SearchFilter> filters);

}
