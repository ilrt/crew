package net.crew_vre.web.facade;

import net.crew_vre.events.domain.Event;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayEventFacade.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface DisplayEventFacade {

    Event displayEvent(String eventId);

}
