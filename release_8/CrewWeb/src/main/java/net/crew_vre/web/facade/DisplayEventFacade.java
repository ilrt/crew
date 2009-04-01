package net.crew_vre.web.facade;

import net.crew_vre.events.domain.Event;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayEventFacade.java 538 2007-12-21 14:44:38Z cmmaj $
 */
public interface DisplayEventFacade {

    Event displayEvent(String eventId);

}
