package net.crew_vre.web.facade.impl;

import net.crew_vre.events.domain.Event;
import net.crew_vre.events.service.EventService;
import net.crew_vre.web.facade.DisplayEventFacade;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayEventFacadeImpl.java 534 2007-12-20 17:10:53Z cmmaj $
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
