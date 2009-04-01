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

import java.io.IOException;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.impl.FileDatabase;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventParent;
import net.crew_vre.events.domain.EventPart;
import net.crew_vre.events.domain.Place;
import net.crew_vre.events.domain.PlacePart;
import net.crew_vre.events.domain.Subject;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: EventDaoImplTest.java 1189 2009-03-31 13:14:53Z cmmaj $
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

        for (PlacePart p : event.getPlaces()) {
            assertNotNull("The graph should not be null", p.getGraph());
        }

        assertEquals("There should be 1 part", 1, event.getParts().size());

        for (EventPart e : event.getParts()) {
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

        for (EventParent e : event.getPartOf()) {
            assertNotNull("The graph of the parent event should not be null", e.getGraph());
        }



    }

}
