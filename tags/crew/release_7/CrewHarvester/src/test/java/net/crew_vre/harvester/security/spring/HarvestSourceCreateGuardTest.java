package net.crew_vre.harvester.security.spring;

import net.crew_vre.authorization.AccessDeniedException;
import net.crew_vre.authorization.GateKeeper;
import net.crew_vre.harvester.HarvestSource;
import net.crew_vre.harvester.HarvesterDao;
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
 * @version $Id: HarvestSourceCreateGuardTest.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
@RunWith(JMock.class)
public class HarvestSourceCreateGuardTest extends BaseTest {

    Mockery context = new JUnit4Mockery();
    final HarvesterDao harvestDao = context.mock(HarvesterDao.class);
    final HarvestSource harvestSource = context.mock(HarvestSource.class);
    final GateKeeper gateKeeper = context.mock(GateKeeper.class);

    @Test
    public void createSourceWithAuthority() throws Throwable {

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(HARVESTER_USER_ONE);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", HARVESTER_USER_ONE_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());

        HarvestSourceCreateGuard harvestSourceCreateGuard =
                new HarvestSourceCreateGuard(HARVESTER_GROUP);

        try {
            harvestSourceCreateGuard.before(null, null, null);
        } catch (AccessDeniedException ex) {
            fail("The user should be authorized to create a harvest source");
        }

    }

    @Test
    public void createSourceWithoutAuthority() throws Throwable {

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(AUTHENTICATED_USER);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", AUTHENTICATED_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());

        HarvestSourceCreateGuard harvestSourceCreateGuard =
                new HarvestSourceCreateGuard(HARVESTER_GROUP);

        try {
            harvestSourceCreateGuard.before(null, null, null);
            fail("The user should not be authorized to create a harvest source");
        } catch (AccessDeniedException ex) {

        }

    }

}
