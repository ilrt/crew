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
 * @version $Id: HarvestSourceReadGuardTest.java 1190 2009-03-31 13:22:30Z cmmaj $
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
