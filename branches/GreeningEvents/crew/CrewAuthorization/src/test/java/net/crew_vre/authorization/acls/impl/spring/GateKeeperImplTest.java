/**
 * Copyright (c) 2008-2009 University of Bristol
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
package net.crew_vre.authorization.acls.impl.spring;

import net.crew_vre.authorization.GateKeeper;
import net.crew_vre.authorization.Permission;
import net.crew_vre.authorization.acls.BaseTest;
import net.crew_vre.authorization.acls.PermissionResolver;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: GateKeeperImplTest.java 1186 2009-03-31 12:37:17Z cmmaj $
 */
@RunWith(JMock.class)
public class GateKeeperImplTest extends BaseTest {

    Mockery context = new JUnit4Mockery();
    final PermissionResolver permissionResolver = context.mock(PermissionResolver.class);


    @Test
    public void anonymousCanRead() {

        context.checking(new Expectations() {{
            oneOf(permissionResolver).authorityHasPermissionForGraph(GRAPH_ONE,
                    ANONYMOUS_ROLE, Permission.READ);
            will(returnValue(true));
        }});

        GateKeeper gateKeeper = new GateKeeperImpl(permissionResolver);

        boolean isAllowed = gateKeeper.userHasPermissionFor(ANONYMOUS_USER,
                Permission.READ, GRAPH_ONE);

        assertTrue("The anonymous reader can view the graph", isAllowed);
    }

    @Test
    public void anonymousCannotRead() {

        context.checking(new Expectations() {{
            oneOf(permissionResolver).authorityHasPermissionForGraph(GRAPH_TWO,
                    ANONYMOUS_ROLE, Permission.READ);
            will(returnValue(false));
        }});

        GateKeeper gateKeeper = new GateKeeperImpl(permissionResolver);

        boolean isAllowed = gateKeeper.userHasPermissionFor(ANONYMOUS_USER,
                Permission.READ, GRAPH_TWO);

        assertFalse("The anonymous reader cannot view the graph", isAllowed);

    }

    @Test
    public void adminCanRead() {

        context.checking(new Expectations() {{
            oneOf(permissionResolver).authorityHasPermissionForGraph(GRAPH_TWO,
                    AUTHENTICATED_ROLE, Permission.READ);
            will(returnValue(false));
        }});

        context.checking(new Expectations() {{
            oneOf(permissionResolver).authorityHasPermissionForGraph(GRAPH_TWO,
                    ADMIN_ROLE, Permission.READ);
            will(returnValue(true));
        }});

        GateKeeper gateKeeper = new GateKeeperImpl(permissionResolver);

        boolean isAllowed = gateKeeper.userHasPermissionFor(ADMIN_USER,
                Permission.READ, GRAPH_TWO);

        assertTrue("The admin reader can view the graph", isAllowed);
    }

}
