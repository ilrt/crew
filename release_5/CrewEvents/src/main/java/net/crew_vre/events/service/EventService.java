package net.crew_vre.events.service;

import net.crew_vre.events.domain.Event;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: EventService.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface EventService {

    Event getEventById(final String id);

}
