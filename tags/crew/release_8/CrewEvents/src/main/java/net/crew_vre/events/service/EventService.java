package net.crew_vre.events.service;

import net.crew_vre.events.domain.Event;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: EventService.java 417 2007-11-19 11:48:44Z cmmaj $
 */
public interface EventService {

    Event getEventById(final String id);

}
