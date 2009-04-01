package net.crew_vre.harvester.security.spring;

import net.crew_vre.authorization.AccessDeniedException;
import net.crew_vre.authorization.GateKeeper;
import net.crew_vre.authorization.Permission;
import net.crew_vre.harvester.HarvestSource;
import net.crew_vre.harvester.HarvesterDao;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.context.SecurityContextHolder;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HarvestSourceDeleteGuardTest.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
@RunWith(JMock.class)
public class HarvestSourceDeleteGuardTest extends BaseTest {

    Mockery context = new JUnit4Mockery();
    final HarvesterDao harvestDao = context.mock(HarvesterDao.class);
    final HarvestSource harvestSource = context.mock(HarvestSource.class);
    final GateKeeper gateKeeper = context.mock(GateKeeper.class);

    @Test
    public void deleteSourceWithAuthority() throws Throwable {

        context.checking(new Expectations() {{
            oneOf(gateKeeper).userHasPermissionFor(HARVESTER_USER_ONE, Permission.DELETE,
                    GRAPH_ONE);
            will(returnValue(true));
        }});

        Object[] args = {GRAPH_ONE};

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(HARVESTER_USER_ONE);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", HARVESTER_USER_ONE_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());

        HarvestSourceDeleteGuard harvestSourceDeleteGuard =
                new HarvestSourceDeleteGuard(gateKeeper);

        try {
            harvestSourceDeleteGuard.before(null, args, null);
        } catch (AccessDeniedException ex) {
            fail("The user should be authorized to delete a harvest source");
        }

    }

    @Test
    public void deleteSourceWithoutAuthority() throws Throwable {

        context.checking(new Expectations() {{
            oneOf(gateKeeper).userHasPermissionFor(AUTHENTICATED_USER, Permission.DELETE,
                    GRAPH_ONE);
            will(returnValue(false));
        }});

        Object[] args = {GRAPH_ONE};

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(AUTHENTICATED_USER);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", AUTHENTICATED_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());

        HarvestSourceDeleteGuard harvestSourceDeleteGuard =
                new HarvestSourceDeleteGuard(gateKeeper);

        try {
            harvestSourceDeleteGuard.before(null, args, null);
            fail("The user should not be authorized to delete a harvest source");
        } catch (AccessDeniedException ex) {
        }

    }

}