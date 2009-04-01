package net.crew_vre.events.service.impl;

import net.crew_vre.events.dao.EventDao;
import net.crew_vre.events.dao.LocationDao;
import net.crew_vre.events.dao.RoleDao;
import net.crew_vre.events.domain.Event;
import net.crew_vre.events.service.EventService;
import net.crew_vre.events.service.PaperService;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: EventServiceImpl.java 517 2007-12-19 20:02:47Z cmmaj $
 */
public class EventServiceImpl implements EventService {

    public EventServiceImpl(final EventDao eventDao, final LocationDao locationDao,
                            final RoleDao roleDao, final PaperService paperService) {
        this.eventDao = eventDao;
        this.roleDao = roleDao;
        this.locationDao = locationDao;
        this.paperService = paperService;
    }

    public Event getEventById(final String id) {

        Event event = eventDao.findEventById(id);
        event.setLocations(locationDao.findLocationByEvent(id));
        event.setRoles(roleDao.findRolesByEvent(id));
        event.setPapers(paperService.findPapersRelatedToEvent(id));
        return event;
    }

    /**
     * DAO to access the events.
     */
    private EventDao eventDao;

    /**
     * DAO to access an event locations
     */
    private LocationDao locationDao;

    /**
     * DAO to access roles
     */
    private RoleDao roleDao;

    /**
     * Details about papers
     */
    private PaperService paperService;
}