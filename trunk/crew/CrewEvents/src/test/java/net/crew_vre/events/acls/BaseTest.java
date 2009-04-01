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
package net.crew_vre.events.acls;

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.providers.TestingAuthenticationToken;

import net.crew_vre.events.domain.Event;


public abstract class BaseTest {

    // graph URIs
    public final String GRAPH_ONE = "http://example.org/graph1/";
    public final String GRAPH_TWO = "http://example.org/graph2/";
    public final String GRAPH_THREE = "http://example.org/graph3/";

    // event URIs
    public final String EVENT_ONE = "http://example.org/event/1/";
    public final String EVENT_TWO = "http://example.org/event/2/";
    public final String EVENT_THREE = "http://example.org/event/3/";

    // roles
    public final String ANONYMOUS_ROLE = "ANONYMOUS";
    public final String AUTHENTICATED_ROLE = "AUTHENTICATED";
    public final String ADMIN_ROLE = "ADMIN";

    // uids
    public final String ANONYMOUS_UID = "anonymous";
    public final String AUTHENTICATED_UID = "fred";
    public final String ADMIN_UID = "admin";

    // users
    public final Authentication ANONYMOUS_USER =
            new TestingAuthenticationToken(ANONYMOUS_UID, "anonymous",
                    new GrantedAuthority[]{new GrantedAuthorityImpl(ANONYMOUS_ROLE)});

    public final Authentication AUTHENTICATED_USER =
            new TestingAuthenticationToken(AUTHENTICATED_UID, "secret",
                    new GrantedAuthority[]{new GrantedAuthorityImpl(AUTHENTICATED_ROLE)});

    public final Authentication ADMIN_USER =
            new TestingAuthenticationToken(ADMIN_UID, "secret",
                    new GrantedAuthority[]{new GrantedAuthorityImpl(AUTHENTICATED_ROLE),
                            new GrantedAuthorityImpl(ADMIN_ROLE)});


    protected Event createEvent(String eventId, String graphId) {
        Event event = new Event();
        event.setId(eventId);
        event.setGraph(graphId);
        return event;
    }

}
