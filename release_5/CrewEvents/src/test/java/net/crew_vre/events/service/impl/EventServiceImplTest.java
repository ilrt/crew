package net.crew_vre.events.service.impl;

import java.io.IOException;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.impl.FileDatabase;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.dao.*;
import net.crew_vre.events.dao.impl.*;
import net.crew_vre.events.domain.Event;
import net.crew_vre.events.service.EventService;
import net.crew_vre.events.service.PaperService;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public class EventServiceImplTest extends TestCase {

    private EventService eventService;

    @Override
    public void setUp() {
        try {
            Database database = new FileDatabase(Constants.DEFAULT_MODEL,
                    Constants.NAMED_GRAPHS);
            EventDao eventDao = new EventDaoImpl(database);
            LocationDao locationDao = new LocationDaoImpl(database);
            RoleDao roleDao = new RoleDaoImpl(database);
            PaperDao paperDao = new PaperDaoImpl(database);
            PersonDao personDao = new PersonDaoImpl(database, eventDao);
            PaperService paperService = new PaperServiceImpl(paperDao, personDao);
            eventService = new EventServiceImpl(eventDao, locationDao, roleDao, paperService);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void testGetEventById() {
        Event event = eventService.getEventById(Constants.EVENT_ONE_ID);
        assertNotNull("The event should not be null", event);
        assertTrue("There should be more than one location", event.getLocations().size() > 0);
        assertTrue("There should be more than one role", event.getRoles().size() > 0);
    }

    public void testEventHasPaper() {
        Event event = eventService.getEventById(Constants.EVENT_TWO_ID);
        assertNotNull("The event should not be null", event);
        assertEquals("The event has two paper", 2, event.getPapers().size());
    }

}
