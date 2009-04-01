package net.crew_vre.web.facade;

import net.crew_vre.events.domain.Place;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayPlaceFacade.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface DisplayPlaceFacade {

    Place displayPlace(String placeId);

}
