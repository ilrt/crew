package net.crew_vre.web.facade.impl;

import net.crew_vre.events.dao.PlaceDao;
import net.crew_vre.events.domain.Place;
import net.crew_vre.web.facade.DisplayPlaceFacade;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayPlaceFacadeImpl.java 534 2007-12-20 17:10:53Z cmmaj $
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
