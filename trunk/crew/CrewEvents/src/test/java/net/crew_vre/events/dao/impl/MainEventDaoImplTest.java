/**
 * Copyright (c) 2008-2009, University of Bristol
 * Copyright (c) 2008-2009, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package net.crew_vre.events.dao.impl;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.Utility;
import net.crew_vre.events.dao.LocationDao;
import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventPart;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.impl.FileDatabase;
import org.joda.time.LocalDate;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: MainEventDaoImplTest.java 1189 2009-03-31 13:14:53Z cmmaj $
 */
public class MainEventDaoImplTest extends TestCase {

    private MainEventDaoImpl mainEventDao;

    //private final String testUri = "http://crew.ilrt.bris.ac.uk/event/1";

    @Override
    public void setUp() {
        try {
            Database database = new FileDatabase(Constants.DEFAULT_MODEL,
                    Constants.NAMED_GRAPHS);
            LocationDao locationDao = new LocationDaoImpl(database);
            mainEventDao = new MainEventDaoImpl(database, locationDao);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testFindAllEvents() {

        List<EventPart> results = mainEventDao.findAllEvents();
        assertNotNull("The results should not be null even when empty", results);
        assertEquals("There should be one event", EVENTS_TOTAL, results.size());

        for (EventPart e : results) {
            assertNotNull("The graph should not be null", e.getGraph());
        }

    }

    public void testFindEventById() {

        EventPart event = mainEventDao.findEventById(Constants.EVENT_ONE_ID);
        assertNotNull("The Event should not be null", event);
        assertNotNull("The graph should not be null", event.getGraph());
        assertEquals("The URI should be " + Constants.EVENT_ONE_ID, Constants.EVENT_ONE_ID,
                event.getId());
        assertEquals("There should be 5 locations", event.getLocations().size(), LOCATIONS_TOTAL);
    }

    public void testFindEventByIdUnkownUri() {

        EventPart event = mainEventDao.findEventById("http://example.org/#1");
        assertNull("Uri doesn't exist so the event should be null", event);
    }

    public void testFindEventsWithOffset() {

        List results = mainEventDao.findAllEvents(LIMIT, 0);
        assertNotNull("The results should not be null even when empty", results);
        assertTrue("There should be results", results.size() > 0);
    }

    public void testFindEventsByDates() {

        LocalDate startDate;
        LocalDate endDate;

        try {

            startDate = Utility.parseStringToLocalDate("2007-08-01");
            endDate = Utility.parseStringToLocalDate("2008-08-31");
            List<EventPart> results = mainEventDao.findEventsByDate(startDate, endDate);
            assertNotNull("The results should not be null even when empty", results);
            assertTrue("There should be results", results.size() > 0);

            for (EventPart e : results) {
                assertNotNull("The graph should not be null", e.getGraph());
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }


    public void testFindEventsByDatesWithOffset() {

        LocalDate startDate;
        LocalDate endDate;

        try {

            startDate = Utility.parseStringToLocalDate("2007-08-01");
            endDate = Utility.parseStringToLocalDate("2008-08-30");
            List<EventPart> results = mainEventDao.findEventsByDate(startDate, endDate, LIMIT, 0);
            assertNotNull("The results should not be null even when empty", results);
            assertTrue("There should be results", results.size() > 0);

            for (EventPart e : results) {
                assertNotNull("The graph should not be null", e.getGraph());
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    public void testFindEventsByCreationDate() {

        DateTime startDate;
        DateTime endDate;

        try {
            endDate = Utility.parseStringToDateTime("2007-08-29T17:00+00:00");
            startDate = endDate.minusMonths(1);
            List<EventPart> results = mainEventDao.findEventsByCreationDate(startDate, endDate);

            assertNotNull("The results should not be null", results);
            assertEquals("There should 1 events", 1, results.size());

            for (EventPart e : results) {
                assertNotNull("The graph should not be null", e.getGraph());
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
        }


    }

    /**
     * Needed to add support for xsd:date
     */
    public void testXSDDate() {

        // The first attempt has an xsd:date

        EventPart eventOne = mainEventDao.findEventById("http://www.crew_vre.net/testdata#event6");
        assertNotNull("The Event should not be null", eventOne);
        assertNotNull("The graph should not be null", eventOne.getGraph());
        assertEquals("Incorrect year", 2009, eventOne.getStartDate().getYear());
        assertEquals("Incorrect month", 3, eventOne.getStartDate().getMonthOfYear());
        assertEquals("Incorrect day", 5, eventOne.getStartDate().getDayOfMonth());

        // The second attempt has an xsd:dateTime - the local date is calculated
        EventPart eventTwo = mainEventDao.findEventById("http://www.crew_vre.net/testdata#event5");
        assertNotNull("The Event should not be null", eventTwo);
        assertNotNull("The graph should not be null", eventTwo.getGraph());
        assertEquals("Incorrect year", 2008, eventTwo.getStartDate().getYear());
        assertEquals("Incorrect month", 5, eventTwo.getStartDate().getMonthOfYear());
        assertEquals("Incorrect day", 10, eventTwo.getStartDate().getDayOfMonth());

    }


    private static final int EVENTS_TOTAL = 6;
    private static final int LOCATIONS_TOTAL = 5;
    private static final int LIMIT = 10;
}
