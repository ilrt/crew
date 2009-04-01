package net.crew_vre.harvester.security.spring;

import net.crew_vre.authorization.AccessDeniedException;
import net.crew_vre.authorization.GateKeeper;
import net.crew_vre.authorization.Permission;
import net.crew_vre.harvester.HarvestSource;
import net.crew_vre.harvester.HarvesterDao;
import net.crew_vre.harvester.impl.HarvestSourceImpl;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HarvestSourceReadGuardTest.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
@RunWith(JMock.class)
public class HarvestSourceReadGuardTest extends BaseTest {

    Mockery context = new JUnit4Mockery();
    final HarvesterDao harvestDao = context.mock(HarvesterDao.class);
    final HarvestSource harvestSource = context.mock(HarvestSource.class);
    final GateKeeper gateKeeper = context.mock(GateKeeper.class);
    final List<HarvestSource> results = new ArrayList<HarvestSource>();

    @Test
    public void readListAuthorized() throws Throwable {

        createList();

        // check the initial list size
        assertEquals("There should be 2 objects", 2, results.size());

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(HARVESTER_USER_ONE);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", HARVESTER_USER_ONE_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());

        context.checking(new Expectations() {{
            one(gateKeeper).userHasPermissionFor(HARVESTER_USER_ONE, Permission.READ, GRAPH_ONE);
            will(returnValue(true));
        }});

        context.checking(new Expectations() {{
            one(gateKeeper).userHasPermissionFor(HARVESTER_USER_ONE, Permission.READ, GRAPH_TWO);
            will(returnValue(true));
        }});

        HarvestSourceReadGuard harvestSourceReadGuard =
                new HarvestSourceReadGuard(gateKeeper);

        harvestSourceReadGuard.afterReturning(results, null, null, null);

        // none of the items should have been removed
        assertEquals("There should be 2 objects", 2, results.size());
    }

    @Test
    public void readListPartialAuthorized() throws Throwable {

        createList();

        // check the initial list size
        assertEquals("There should be 2 objects", 2, results.size());

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(HARVESTER_USER_TWO);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", HARVESTER_USER_TWO_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());

        context.checking(new Expectations() {{
            one(gateKeeper).userHasPermissionFor(HARVESTER_USER_TWO, Permission.READ, GRAPH_ONE);
            will(returnValue(true));
        }});

        context.checking(new Expectations() {{
            one(gateKeeper).userHasPermissionFor(HARVESTER_USER_TWO, Permission.READ, GRAPH_TWO);
            will(returnValue(false));
        }});

        HarvestSourceReadGuard harvestSourceReadGuard =
                new HarvestSourceReadGuard(gateKeeper);

        harvestSourceReadGuard.afterReturning(results, null, null, null);

        // one of the items should have been removed
        assertEquals("There should be 1 objects", 1, results.size());
    }


    @Test
    public void readListUnAuthorized() throws Throwable {

        createList();

        // check the initial list size
        assertEquals("There should be 2 objects", 2, results.size());

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(AUTHENTICATED_USER);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", AUTHENTICATED_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());

        context.checking(new Expectations() {{
            one(gateKeeper).userHasPermissionFor(AUTHENTICATED_USER, Permission.READ, GRAPH_ONE);
            will(returnValue(false));
        }});

        context.checking(new Expectations() {{
            one(gateKeeper).userHasPermissionFor(AUTHENTICATED_USER, Permission.READ, GRAPH_TWO);
            will(returnValue(false));
        }});

        HarvestSourceReadGuard harvestSourceReadGuard =
                new HarvestSourceReadGuard(gateKeeper);

        harvestSourceReadGuard.afterReturning(results, null, null, null);

        // all of the items should have been removed
        assertEquals("There should be 0 objects", 0, results.size());
    }

    @Test
    public void readSourceAuthorized() throws Throwable {

        HarvestSource harvestSource = new HarvestSourceImpl(GRAPH_ONE, null, null, false);

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(HARVESTER_USER_ONE);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", HARVESTER_USER_ONE_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());

        context.checking(new Expectations() {{
            one(gateKeeper).userHasPermissionFor(HARVESTER_USER_ONE, Permission.READ, GRAPH_ONE);
            will(returnValue(true));
        }});

        HarvestSourceReadGuard harvestSourceReadGuard =
                new HarvestSourceReadGuard(gateKeeper);

        try {

            harvestSourceReadGuard.afterReturning(harvestSource, null, null, null);

        } catch (AccessDeniedException ex) {
            fail("The user has access to this item");
        }
    }

    @Test
    public void readSourceUnAuthorized() throws Throwable {

        HarvestSource harvestSource = new HarvestSourceImpl(GRAPH_ONE, null, null, false);

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(AUTHENTICATED_USER);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", AUTHENTICATED_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());

        context.checking(new Expectations() {{
            one(gateKeeper).userHasPermissionFor(AUTHENTICATED_USER, Permission.READ, GRAPH_ONE);
            will(returnValue(false));
        }});

        HarvestSourceReadGuard harvestSourceReadGuard =
                new HarvestSourceReadGuard(gateKeeper);

        try {

            harvestSourceReadGuard.afterReturning(harvestSource, null, null, null);
            fail("The user does not have access to this item");
        } catch (AccessDeniedException ex) {
        }
    }

    private void createList() {
        results.add(new HarvestSourceImpl(GRAPH_ONE, null, null, false));
        results.add(new HarvestSourceImpl(GRAPH_TWO, null, null, false));
    }


}
