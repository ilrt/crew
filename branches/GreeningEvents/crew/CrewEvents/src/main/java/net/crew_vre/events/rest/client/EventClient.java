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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventParent;
import net.crew_vre.events.rest.Utils;
import net.crew_vre.events.rest.resources.EventResource;
import net.crew_vre.rest.AuthenticationFilter;
import net.crew_vre.rest.ClientUtils;

import org.caboto.RdfMediaType;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.sun.jersey.api.client.ClientResponse;

/**
 * A client of the Rest resources
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class EventClient {

    /**
     * The path for the client
     */
    public static final String PATH = "rest/event/";

    /**
     * The extra path for the client
     */
    public static final String EXTRA_PATH = "local/";

    private String uri = null;

    private AuthenticationFilter authentication = null;

    /**
     * Creates an EventClient
     * @param authentication The authentication to use or null if none
     * @param uri The uri of the CREW server
     */
    public EventClient(AuthenticationFilter authentication, String uri) {
        this.authentication = authentication;
        if (!uri.endsWith("/")) {
            uri += "/";
        }
        uri += PATH;
        this.uri = uri;
    }


    /**
     * Gets an event
     * @param id The id of the event to get
     * @return The event or null if not found
     */
    public Event getEvent(String id) {
        String eventUri = null;
        try {
            eventUri = uri + URLEncoder.encode(id, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ClientResponse response = ClientUtils.get(authentication, eventUri,
                RdfMediaType.APPLICATION_RDF_XML);
        if (response.getResponseStatus().equals(Response.Status.OK)) {
            Model model = response.getEntity(Model.class);
            Event event = new Event();
            Resource resource = model.getResource(id);
            Utils.fillInEvent(event, resource);
            event.setGraph(uri);
            return event;
        }
        return null;
    }

    /**
     * Uploads an event
     * @param event The event to upload
     * @return The error or null if upload was successful
     * @throws UnsupportedEncodingException
     */
    public String uploadEvent(Event event) throws UnsupportedEncodingException {
        String query = "";
        if ((event.getId() != null)
                && event.getId().startsWith(getLocalGraphUriPrefix())) {
            query += "&uri=" + URLEncoder.encode(event.getId(), "UTF-8");
        }
        if (event.getTitle() != null) {
            query += "&title=" + URLEncoder.encode(event.getTitle(), "UTF-8");
        }
        if (event.getDescription() != null) {
            query += "&description=" + URLEncoder.encode(event.getDescription(),
                    "UTF-8");
        }
        if (event.getStartDateTime() != null) {
            query += "&startDateTime=" + URLEncoder.encode(
                   event.getStartDateTime().toString(
                           EventResource.DATETIME_PATTERN), "UTF-8");
        }
        if (event.getEndDateTime() != null) {
            query += "&endDateTime=" + URLEncoder.encode(
                   event.getEndDateTime().toString(
                           EventResource.DATETIME_PATTERN), "UTF-8");
        }
        if ((event.getPartOf() != null) && (event.getPartOf().size() > 0)) {
            EventParent parent = event.getPartOf().get(
                    event.getPartOf().size() - 1);
            query += "&isPartOf=" + URLEncoder.encode(parent.getId(), "UTF-8");
        }
        query = query.substring(1);

        System.err.println("Uploading to " + event.getGraph());
        ClientResponse response = ClientUtils.post(authentication,
                event.getGraph(), MediaType.APPLICATION_FORM_URLENCODED,
                query);
        if (!response.getResponseStatus().equals(Response.Status.CREATED)) {
            System.err.println("Response code = " + response.getResponseStatus().getStatusCode());
            return response.getResponseStatus().toString();
        }
        event.setId(response.getLocation().toString());
        return null;
    }

    /**
     * Gets the prefix of the graph uris local to this server
     * @return The prefix
     */
    public String getLocalGraphUriPrefix() {
        return uri + EXTRA_PATH;
    }
}
