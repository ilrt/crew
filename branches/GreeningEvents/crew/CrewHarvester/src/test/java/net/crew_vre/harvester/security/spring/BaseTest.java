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

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.providers.TestingAuthenticationToken;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: BaseTest.java 1190 2009-03-31 13:22:30Z cmmaj $
 */
public abstract class BaseTest {

    final String GRAPH_ONE = "http://example.org/graph1/";
    final String GRAPH_TWO = "http://example.org/graph2/";

    final String AUTHENTICATED_UID = "fred";
    final String HARVESTER_USER_ONE_UID = "h1";
    final String HARVESTER_USER_TWO_UID = "h1";

    final String AUTHENTICATED_ROLE = "AUTHENTICATED";
    final String HARVESTER_GROUP = "Harvester_Group";
    final String HARVESTER_GROUP_ONE = "Harvester_Group_one";
    final String HARVESTER_GROUP_TWO = "Harvester_Group_Two";

    public final Authentication AUTHENTICATED_USER =
            new TestingAuthenticationToken(AUTHENTICATED_UID, "secret",
                    new GrantedAuthority[]{new GrantedAuthorityImpl(AUTHENTICATED_ROLE)});

    final Authentication HARVESTER_USER_ONE =
            new TestingAuthenticationToken(HARVESTER_USER_ONE_UID, "secret",
                    new GrantedAuthority[]{new GrantedAuthorityImpl(HARVESTER_GROUP),
                            new GrantedAuthorityImpl(HARVESTER_GROUP_ONE)});

    final Authentication HARVESTER_USER_TWO =
            new TestingAuthenticationToken(HARVESTER_USER_TWO_UID, "secret",
                    new GrantedAuthority[]{new GrantedAuthorityImpl(HARVESTER_GROUP),
                            new GrantedAuthorityImpl(HARVESTER_GROUP_TWO)});
}
