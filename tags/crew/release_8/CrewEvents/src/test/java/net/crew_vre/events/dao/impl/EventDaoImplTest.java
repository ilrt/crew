package net.crew_vre.events.dao.impl;

import java.io.IOException;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.impl.FileDatabase;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.Place;
import net.crew_vre.events.domain.Subject;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: EventDaoImplTest.java 929 2008-11-18 15:28:41Z cmmaj $
 */
public class EventDaoImplTest extends TestCase {

    private EventDaoImpl eventDao;

    @Override
    public void setUp() {

        try {
            Database database = new FileDatabase(Constants.DEFAULT_MODEL,
                            Constants.NAMED_GRAPHS);
            eventDao = new EventDaoImpl(database);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testFindEventById() {

        Event event = eventDao.findEventById(Constants.EVENT_ONE_ID);
        assertNotNull("The Event should not be null", event);
        assertEquals("The URI should be " + Constants.EVENT_ONE_ID, Constants.EVENT_ONE_ID,
                event.getId());

        assertNotNull("The title should not be null", event.getTitle());
        assertNotNull("The location should not be null", event.getPlaces());

        for (Place p : event.getPlaces()) {
            assertNotNull("The graph should not be null", p.getGraph());
        }

        assertEquals("There should be 1 part", 1, event.getParts().size());

        for (Event e : event.getParts()) {
            assertNotNull("The graph should not be null", e.getGraph());
        }


        assertEquals("There should be 3 subjects", 3, event.getSubjects().size());

        for (Subject subject : event.getSubjects()) {
            assertNotNull("The graph should not be null", subject.getGraph());
        }

        assertEquals("There should be 3 tags", 3, event.getTags().size());
//        assertEquals("There should be 4 locations", 5, event.getLocations().size());
    }


    public void testFindEventById_partOf() {

        Event event = eventDao.findEventById(Constants.EVENT_TWO_ID);

        assertNotNull("The Event should not be null", event);
        assertNotNull("The graph should not be null", event.getGraph());
        assertTrue("The event is the part of another event", event.getPartOf().size() == 1);

        for (Event e : event.getPartOf()) {
            assertNotNull("The graph of the parent event should not be null", e.getGraph());
        }



    }

}
