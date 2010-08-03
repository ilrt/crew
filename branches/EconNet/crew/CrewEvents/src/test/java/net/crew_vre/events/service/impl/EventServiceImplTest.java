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
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: EventServiceImplTest.java 1189 2009-03-31 13:14:53Z cmmaj $
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
