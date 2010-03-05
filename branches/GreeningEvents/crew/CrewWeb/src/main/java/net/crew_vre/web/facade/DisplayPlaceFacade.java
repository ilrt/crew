package net.crew_vre.web.facade;

import net.crew_vre.events.domain.Place;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayPlaceFacade.java 538 2007-12-21 14:44:38Z cmmaj $
 */
public interface DisplayPlaceFacade {

    Place displayPlace(String placeId);

}
