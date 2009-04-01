package net.crew_vre.web.facade.impl;

import net.crew_vre.events.domain.Event;
import net.crew_vre.events.service.EventService;
import net.crew_vre.web.facade.DisplayEventFacade;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayEventFacadeImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class DisplayEventFacadeImpl implements DisplayEventFacade {

    public DisplayEventFacadeImpl(EventService eventService) {
        this.eventService = eventService;
    }

    public Event displayEvent(String eventId) {

        return eventService.getEventById(eventId);
    }

    private EventService eventService;
}
