package net.crew_vre.authorization.acls.impl.spring;

import net.crew_vre.authorization.Permission;
import net.crew_vre.authorization.AccessDeniedException;
import net.crew_vre.authorization.acls.BaseTest;
import net.crew_vre.events.domain.Event;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: DomainObjectExitGuardIntegrationTest.java 1092 2009-03-11 19:01:38Z cmmaj $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-context.xml"})
public class DomainObjectExitGuardIntegrationTest extends BaseTest {

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setDomianObjectExitGuard(DomainObjectExitGuard domainObjectExitGuard) {
        this.domainObjectExitGuard = domainObjectExitGuard;
    }

    @Before
    public void setUp() {

        // anomymous role can read graph 1
        jdbcTemplate.execute("INSERT INTO GRAPH_ACL (ID, GRAPH, AUTHORITY) VALUES(100, '"
                + GRAPH_ONE + "', '" + ANONYMOUS_ROLE + "')");
        jdbcTemplate.execute("INSERT INTO GRAPH_ACL_ENTRY (ID, GRAPH_ACL_ID, PERMISSION)"
                + " VALUES (100, 100, " + Permission.READ.intValue() + ")");

        // authenticated role can read graphs 1 and 2
        jdbcTemplate.execute("INSERT INTO GRAPH_ACL (ID, GRAPH, AUTHORITY) VALUES(101, '"
                + GRAPH_ONE + "', '" + AUTHENTICATED_ROLE + "')");
        jdbcTemplate.execute("INSERT INTO GRAPH_ACL_ENTRY (ID, GRAPH_ACL_ID, PERMISSION)"
                + " VALUES (101, 101, " + Permission.READ.intValue() + ")");
        jdbcTemplate.execute("INSERT INTO GRAPH_ACL (ID, GRAPH, AUTHORITY) VALUES(102, '"
                + GRAPH_TWO + "', '" + AUTHENTICATED_ROLE + "')");
        jdbcTemplate.execute("INSERT INTO GRAPH_ACL_ENTRY (ID, GRAPH_ACL_ID, PERMISSION)"
                + " VALUES (102, 102, " + Permission.READ.intValue() + ")");

        // admin role can read graph 3
        jdbcTemplate.execute("INSERT INTO GRAPH_ACL (ID, GRAPH, AUTHORITY) VALUES(103, '"
                + GRAPH_THREE + "', '" + ADMIN_ROLE + "')");
        jdbcTemplate.execute("INSERT INTO GRAPH_ACL_ENTRY (ID, GRAPH_ACL_ID, PERMISSION)"
                + " VALUES (103, 103, " + Permission.READ.intValue() + ")");
    }

    @After
    public void cleanup() {
        jdbcTemplate.execute("DELETE FROM GRAPH_ACL_ENTRY");
        jdbcTemplate.execute("DELETE FROM GRAPH_ACL");
    }

    @Test
    public void anonymousReadList() throws Throwable {

        setUpList();

        assertEquals("Unexpected list length", 3, results.size());

        // check we have the ACLs
        assertEquals("The database should have ACLS", 1,
                jdbcTemplate.queryForInt("SELECT COUNT(*) FROM GRAPH_ACL WHERE AUTHORITY = '"
                        + ANONYMOUS_ROLE + "'"));

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(ANONYMOUS_USER);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", ANONYMOUS_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());


        // pass the list of objects
        domainObjectExitGuard.afterReturning(results, null, null, null);

        assertEquals("Unexpected list length", 1, results.size());
    }

    @Test
    public void authenticatedReadList() throws Throwable {

        setUpList();

        assertEquals("Unexpected list length", 3, results.size());

        // check we have the ACLs
        assertEquals("The database should have ACLS", 2,
                jdbcTemplate.queryForInt("SELECT COUNT(*) FROM GRAPH_ACL WHERE AUTHORITY = '"
                        + AUTHENTICATED_ROLE + "'"));

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(AUTHENTICATED_USER);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", AUTHENTICATED_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());

        // pass the list of objects
        domainObjectExitGuard.afterReturning(results, null, null, null);

        assertEquals("Unexpected list length", 2, results.size());

    }


    @Test
    public void adminReadList() throws Throwable {

        setUpList();

        assertEquals("Unexpected list length", 3, results.size());

        // check we have the ACLs
        assertEquals("The database should have ACLS", 1,
                jdbcTemplate.queryForInt("SELECT COUNT(*) FROM GRAPH_ACL WHERE AUTHORITY = '"
                        + ADMIN_ROLE + "'"));

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(ADMIN_USER);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", ADMIN_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());

        // pass the list of objects
        domainObjectExitGuard.afterReturning(results, null, null, null);

        assertEquals("Unexpected list length", 3, results.size());

    }

    @Test
    public void anoymousCanRead() throws Throwable {

        Event event = createEvent(EVENT_ONE, GRAPH_ONE);

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(ANONYMOUS_USER);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", ANONYMOUS_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            domainObjectExitGuard.afterReturning(event, null, null, null);
        } catch (AccessDeniedException ex) {
            fail("The anonymous user should be able to see " + event.getGraph());
        }

    }

    @Test
    public void anoymousCannotRead() throws Throwable {

        Event event = createEvent(EVENT_TWO, GRAPH_TWO);

        // setup the securty context
        SecurityContextHolder.getContext().setAuthentication(ANONYMOUS_USER);

        // double check the correct user is in the context
        assertEquals("Unexpected UID", ANONYMOUS_UID,
                SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            domainObjectExitGuard.afterReturning(event, null, null, null);
            fail("The anonymous user should be able to see " + event.getGraph());
        } catch (AccessDeniedException ex) {
            // worked
        }

    }

    private void setUpList() {
        results.add(createEvent(EVENT_ONE, GRAPH_ONE));
        results.add(createEvent(EVENT_TWO, GRAPH_TWO));
        results.add(createEvent(EVENT_THREE, GRAPH_THREE));
    }

    private List<Event> results = new ArrayList<Event>();

    private DomainObjectExitGuard domainObjectExitGuard;

    private JdbcTemplate jdbcTemplate;


}
