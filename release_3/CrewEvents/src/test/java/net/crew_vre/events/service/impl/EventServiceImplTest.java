package net.crew_vre.events.service.impl;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.dao.*;
import net.crew_vre.events.dao.impl.*;
import net.crew_vre.events.domain.Event;
import net.crew_vre.events.service.EventService;
import net.crew_vre.events.service.PaperService;
import net.crew_vre.jena.exception.DatasetFactoryException;
import net.crew_vre.jena.query.DatasetFactory;
import net.crew_vre.jena.query.impl.DatasetFactoryClasspathFileImpl;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public class EventServiceImplTest extends TestCase {

    private EventService eventService;

    @Override
    public void setUp() {
        try {
            DatasetFactory datasetFactory =
                    new DatasetFactoryClasspathFileImpl(Constants.DEFAULT_MODEL,
                            Constants.NAMED_GRAPHS);
            JenaQueryUtility jqUtility = new JenaQueryUtilityImpl();
            EventDao eventDao = new EventDaoImpl(datasetFactory, jqUtility);
            LocationDao locationDao = new LocationDaoImpl(datasetFactory, jqUtility);
            RoleDao roleDao = new RoleDaoImpl(datasetFactory, jqUtility);
            PaperDao paperDao = new PaperDaoImpl(datasetFactory, jqUtility);
            PersonDao personDao = new PersonDaoImpl(datasetFactory, jqUtility, eventDao);
            PaperService paperService = new PaperServiceImpl(paperDao, personDao);
            eventService = new EventServiceImpl(eventDao, locationDao, roleDao, paperService);
        } catch (DatasetFactoryException e) {
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
