package net.crew_vre.authorization.acls.impl.spring;

import net.crew_vre.authorization.GateKeeper;
import net.crew_vre.authorization.Permission;
import net.crew_vre.authorization.AccessDeniedException;
import net.crew_vre.authorization.acls.BaseTest;
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
 * @version $Id: DomainObjectExitGuardTest.java 1092 2009-03-11 19:01:38Z cmmaj $
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

        assertEquals("The list should have one events", 1, results.size());

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
