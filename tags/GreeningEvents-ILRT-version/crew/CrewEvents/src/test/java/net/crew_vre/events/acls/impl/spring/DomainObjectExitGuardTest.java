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
package net.crew_vre.events.acls.impl.spring;

import net.crew_vre.authorization.GateKeeper;
import net.crew_vre.authorization.Permission;
import net.crew_vre.authorization.AccessDeniedException;
import net.crew_vre.events.acls.BaseTest;
import net.crew_vre.events.acls.impl.spring.DomainObjectExitGuard;
import net.crew_vre.events.domain.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.security.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: DomainObjectExitGuardTest.java 1189 2009-03-31 13:14:53Z cmmaj $
 */
@RunWith(JMock.class)
public class DomainObjectExitGuardTest extends BaseTest {

    Mockery context = new JUnit4Mockery();
    final GateKeeper gateKeeper = context.mock(GateKeeper.class);
    List<Event> results = new ArrayList<Event>();

    @Test
    public void testAnonymousCanSeeOneGraph() throws Throwable {

        setUpList();

        // check that the list size is correct at the start
        assertEquals("The list should have two events", 2, results.size());

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(ANONYMOUS_USER);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", ANONYMOUS_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());


        DomainObjectExitGuard domainObjectExitGuard = new DomainObjectExitGuard(gateKeeper);

        context.checking(new Expectations() {{
            oneOf(gateKeeper).userHasPermissionFor(ANONYMOUS_USER, Permission.READ, GRAPH_ONE);
            will(returnValue(true));
        }});

        context.checking(new Expectations() {{
            oneOf(gateKeeper).userHasPermissionFor(ANONYMOUS_USER, Permission.READ, GRAPH_TWO);
            will(returnValue(false));
        }});

        domainObjectExitGuard.afterReturning(results, null, null, null);

        assertEquals("The list should have two events", 2, results.size());

        // one should be empty

        boolean emptyEventexists = false;

        for (Event event : results) {
            if (event.getId() == null || event.getId().equals("")) {
                emptyEventexists = true;
            }
        }

        assertTrue("One of the events had been replaced with an empty event", emptyEventexists);

    }

    @Test
    public void testAdminCanSeeTwoGraphs() throws Throwable {

        setUpList();

        // check that the list size is correct at the start
        assertEquals("The list should have two events", 2, results.size());

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(ADMIN_USER);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", ADMIN_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());


        DomainObjectExitGuard domainObjectExitGuard = new DomainObjectExitGuard(gateKeeper);

        context.checking(new Expectations() {{
            oneOf(gateKeeper).userHasPermissionFor(ADMIN_USER, Permission.READ, GRAPH_ONE);
            will(returnValue(true));
        }});

        context.checking(new Expectations() {{
            oneOf(gateKeeper).userHasPermissionFor(ADMIN_USER, Permission.READ, GRAPH_TWO);
            will(returnValue(true));
        }});

        domainObjectExitGuard.afterReturning(results, null, null, null);

        assertEquals("The list should have two events", 2, results.size());

    }

    @Test
    public void testAnonymousCanSeeEvent() throws Throwable {

        Event event = createEvent(EVENT_ONE, GRAPH_ONE);

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(ANONYMOUS_USER);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", ANONYMOUS_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());


        DomainObjectExitGuard domainObjectExitGuard = new DomainObjectExitGuard(gateKeeper);

        context.checking(new Expectations() {{
            oneOf(gateKeeper).userHasPermissionFor(ANONYMOUS_USER, Permission.READ, GRAPH_ONE);
            will(returnValue(true));
        }});


        domainObjectExitGuard.afterReturning(event, null, null, null);

        // exit guard should not have touched the object
        assertNotNull("The event should not be null", event);
    }

    @Test
    public void testAnonymousCannotSeeEvent() throws Throwable {

        Event event = createEvent(EVENT_TWO, GRAPH_TWO);

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(ANONYMOUS_USER);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", ANONYMOUS_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());


        DomainObjectExitGuard domainObjectExitGuard = new DomainObjectExitGuard(gateKeeper);

        context.checking(new Expectations() {{
            oneOf(gateKeeper).userHasPermissionFor(ANONYMOUS_USER, Permission.READ, GRAPH_TWO);
            will(returnValue(false));
        }});


        try {
            domainObjectExitGuard.afterReturning(event, null, null, null);
            fail("Should have caught an AccessDeniedException exception");
        } catch (AccessDeniedException ex) {
            // yay
        }

    }

    private void setUpList() {
        results.add(createEvent(EVENT_ONE, GRAPH_ONE));
        results.add(createEvent(EVENT_TWO, GRAPH_TWO));
    }

}
