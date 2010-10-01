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
 * @version $Id: HarvestSourceCreateGuardTest.java 1190 2009-03-31 13:22:30Z cmmaj $
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
