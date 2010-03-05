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

import net.crew_vre.authorization.Permission;
import net.crew_vre.authorization.AccessDeniedException;
import net.crew_vre.events.acls.BaseTest;
import net.crew_vre.events.acls.impl.spring.DomainObjectExitGuard;
import net.crew_vre.events.domain.Event;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
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
 * @version $Id: DomainObjectExitGuardIntegrationTest.java 1189 2009-03-31 13:14:53Z cmmaj $
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

        assertEquals("Unexpected list length", 3, results.size());

                boolean emptyEventexists = false;

        for (Event event : results) {
            if (event.getId() == null || event.getId().equals("")) {
                emptyEventexists = true;
            }
        }

        assertTrue("One of the events had been replaced with an empty event", emptyEventexists);
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

        assertEquals("Unexpected list length", 3, results.size());

        boolean emptyEventexists = false;

        for (Event event : results) {
            if (event.getId() == null || event.getId().equals("")) {
                emptyEventexists = true;
            }
        }

        assertTrue("One of the events had been replaced with an empty event", emptyEventexists);

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
