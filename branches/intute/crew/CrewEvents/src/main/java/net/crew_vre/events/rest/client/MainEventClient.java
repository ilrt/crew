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
package net.crew_vre.events.rest.client;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.caboto.RdfMediaType;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.sun.jersey.api.client.ClientResponse;

import net.crew_vre.events.domain.EventPart;
import net.crew_vre.events.rest.Utils;
import net.crew_vre.jena.vocabulary.IUGO;
import net.crew_vre.rest.AuthenticationFilter;
import net.crew_vre.rest.ClientUtils;

/**
 * A client of the Rest resources
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class MainEventClient {

    private String uri = null;

    private AuthenticationFilter authentication = null;

    /**
     * Creates a MainEventClient
     * @param authentication The authentication to use or null if none
     * @param uri The uri of the CREW server
     */
    public MainEventClient(AuthenticationFilter authentication, String uri) {
        this.authentication = authentication;
        if (!uri.endsWith("/")) {
            uri += "/";
        }
        uri += "rest/events/";
        this.uri = uri;
    }

    /**
     * Gets all the main events
     * @return The list of main events
     */
    public List<EventPart> getMainEvents() {
        ClientResponse response = ClientUtils.get(authentication, uri,
                RdfMediaType.APPLICATION_RDF_XML);
        List<EventPart> events = new ArrayList<EventPart>();
        if (response.getResponseStatus().equals(Response.Status.OK)) {
            Model model = response.getEntity(Model.class);
            ResIterator resources = model.listResourcesWithProperty(RDF.type,
                    IUGO.MainEvent);
            while (resources.hasNext()) {
                Resource resource = resources.nextResource();
                EventPart event = new EventPart();
                Utils.fillInPart(event, resource);
                event.setGraph(uri);
                events.add(event);
            }
        }
        return events;
    }
}
