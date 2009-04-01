package net.crew_vre.events.dao.impl;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.Utility;
import net.crew_vre.events.dao.LocationDao;
import net.crew_vre.events.domain.Event;
import net.crew_vre.jena.exception.DatasetFactoryException;
import net.crew_vre.jena.query.DatasetFactory;
import net.crew_vre.jena.query.impl.DatasetFactoryClasspathFileImpl;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.util.List;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: MainEventDaoImplTest.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class MainEventDaoImplTest extends TestCase {

    private MainEventDaoImpl mainEventDao;

    //private final String testUri = "http://crew.ilrt.bris.ac.uk/event/1";

    @Override
    public void setUp() {
        try {
            DatasetFactory datasetFactory =
                    new DatasetFactoryClasspathFileImpl(Constants.DEFAULT_MODEL,
                            Constants.NAMED_GRAPHS);
            JenaQueryUtility jqUtility = new JenaQueryUtilityImpl();
            LocationDao locationDao = new LocationDaoImpl(datasetFactory, jqUtility);
            mainEventDao = new MainEventDaoImpl(datasetFactory, jqUtility, locationDao);
        } catch (DatasetFactoryException e) {
            e.printStackTrace();
        }
    }

    public void testFindAllEvents() {

        List<Event> results = mainEventDao.findAllEvents();
        assertNotNull("The results should not be null even when empty", results);
        assertEquals("There should be one event", EVENTS_TOTAL, results.size());

        for (Event e : results) {
            assertNotNull("The graph should not be null", e.getGraph());
        }

    }

    public void testFindEventById() {

        Event event = mainEventDao.findEventById(Constants.EVENT_ONE_ID);
        assertNotNull("The Event should not be null", event);
        assertNotNull("The graph should not be null", event.getGraph());
        assertEquals("The URI should be " + Constants.EVENT_ONE_ID, Constants.EVENT_ONE_ID,
                event.getId());
        assertEquals("There should be 5 locations", event.getLocations().size(), LOCATIONS_TOTAL);
    }

    public void testFindEventByIdUnkownUri() {

        Event event = mainEventDao.findEventById("http://example.org/#1");
        assertNull("Uri doesn't exist so the event should be null", event);
    }

    public void testFindEventsWithOffset() {

        List results = mainEventDao.findAllEvents(LIMIT, 0);
        assertNotNull("The results should not be null even when empty", results);
        assertTrue("There should be results", results.size() > 0);
    }

    public void testFindEventsByDates() {

        DateTime startDate;
        DateTime endDate;

        try {

            startDate = Utility.parseString("2007-08-01T09:00+01:00");
            endDate = Utility.parseString("2008-08-31T0:00+01:00");
            List<Event> results = mainEventDao.findEventsByDate(startDate, endDate);
            assertNotNull("The results should not be null even when empty", results);
            assertTrue("There should be results", results.size() > 0);

            for (Event e : results) {
                assertNotNull("The graph should not be null", e.getGraph());
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }


    public void testFindEventsBySatesWithOffset() {

        DateTime startDate;
        DateTime endDate;

        try {

            startDate = Utility.parseString("2007-08-01T09:00+01:00");
            endDate = Utility.parseString("2008-08-30T00:00+01:00");
            List<Event> results = mainEventDao.findEventsByDate(startDate, endDate, LIMIT, 0);
            assertNotNull("The results should not be null even when empty", results);
            assertTrue("There should be results", results.size() > 0);

            for (Event e : results) {
                assertNotNull("The graph should not be null", e.getGraph());
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    /*
        public void testTotalEvents() {

            int total = mainEventDao.totalEvents();
            assertEquals("There should be 1 events", EVENTS_TOTAL, total);
        }


        public void testTotalEventsByDates() {

            DateTime startDate;
            DateTime endDate;

            try {
                startDate = Utility.parseString("2007-08-01T09:00+01:00");
                endDate = Utility.parseString("2008-08-30T21:00+01:00");
                int total = mainEventDao.totalEventsByDate(startDate, endDate);
                assertEquals("There should 1 event", EVENTS_TOTAL, total);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
    */
    public void testFindEventsByCreationDate() {

        DateTime startDate;
        DateTime endDate;

        try {
            endDate = Utility.parseString("2007-08-29T17:00+00:00");
            startDate = endDate.minusMonths(1);
            List<Event> results = mainEventDao.findEventsByCreationDate(startDate, endDate);

            assertNotNull("The results should not be null", results);
            assertEquals("There should 1 events", 1, results.size());

            for (Event e : results) {
                assertNotNull("The graph should not be null", e.getGraph());
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
        }


    }

    private static final int EVENTS_TOTAL = 5;
    private static final int LOCATIONS_TOTAL = 5;
    private static final int LIMIT = 10;
}
