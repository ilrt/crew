package net.crew_vre.web.facade;

import java.util.List;

import net.crew_vre.events.domain.Event;
import net.crew_vre.recordings.domain.Recording;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayEventFacade.java 1171 2009-03-26 17:36:37Z arowley $
 */
public interface DisplayEventFacade {

    Event displayEvent(String eventId);

    List<Recording> displayRecordings(Event event);

}
