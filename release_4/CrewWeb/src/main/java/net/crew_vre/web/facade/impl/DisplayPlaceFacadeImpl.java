package net.crew_vre.web.facade.impl;

import net.crew_vre.events.dao.PlaceDao;
import net.crew_vre.events.domain.Place;
import net.crew_vre.web.facade.DisplayPlaceFacade;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayPlaceFacadeImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class DisplayPlaceFacadeImpl implements DisplayPlaceFacade {

    public DisplayPlaceFacadeImpl(PlaceDao placeDao) {
        this.placeDao = placeDao;
    }

    public Place displayPlace(String placeId) {

        return placeDao.findPlaceById(placeId);
    }

    private PlaceDao placeDao;
}
