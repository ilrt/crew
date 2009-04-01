/*
 * Copyright (c) 2008, University of Manchester All rights reserved.
 * See LICENCE in root directory of source code for details of the license.
 */
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
package net.crew_vre.events.rest.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import net.crew_vre.events.Utility;
import net.crew_vre.events.dao.EventDao;
import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventParent;
import net.crew_vre.harvester.HarvesterSourceManagementFacade;
import net.crew_vre.harvester.web.HarvestSourceAuthority;
import net.crew_vre.jena.vocabulary.Crew;

import org.caboto.RdfMediaType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * An Event resource represented via rest
 * @author Andrew G D Rowley
 * @version 1.0
 */
@Scope("singleton")
@Path("/event/")
@Component
public final class EventResource {

    /**
     * The pattern to use for a date time object
     */
    public static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZZ";

    @Autowired
    @Qualifier("eventDao")
    private EventDao eventDao = null;

    @Autowired
    @Qualifier("eventsUploadDirectory")
    private String eventUploadStoreDirectory = null;

    @Autowired
    @Qualifier("harvesterSourceManagementFacade")
    private HarvesterSourceManagementFacade harvesterSourceManagementFacade;

    @Context
    private UriInfo uriInfo = null;

    /**
     * Gets an event
     * @param eventId The id of the event
     * @return The response
     */
    @Path("{eid}")
    @GET
    @Produces({ RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3 })
    public Response getEvent(@PathParam("eid") String eventId) {
        Event event = eventDao.findEventById(eventId);
        if (event == null) {
            System.err.println("Event " + eventId + " not found");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Model model = ModelFactory.createDefaultModel();
        model.add(Utility.createModelFromEvent(event));
        com.hp.hpl.jena.rdf.model.Resource eventResource =
            model.createResource(event.getUri());
        eventResource.addProperty(Crew.HAS_GRAPH,
                model.createResource(event.getGraph()));
        return Response.status(Response.Status.OK).entity(model).build();
    }

    /**
     * Gets an event
     * @return The response
     */
    @Path("/local/{gid}/{eid}")
    @GET
    @Produces({ RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3 })
    public Response getLocalEvent() {
        String eventId = uriInfo.getRequestUri().toString();
        return getEvent(eventId);
    }

    /**
     * Gets the locally stored events
     * @param graphId The id of the graph to read
     * @return The response
     */
    @Path("local/{gid}")
    @GET
    @Produces({ RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3 })
    public Response getLocalEvents(@PathParam("gid") String graphId) {
        File eventUploadDirectory = new File(eventUploadStoreDirectory,
               graphId);
        eventUploadDirectory.mkdirs();
        if (!eventUploadDirectory.exists()) {
            System.err.println("Directory " + eventUploadDirectory
                    + " does not exist!");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        FilenameFilter filter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                if (name.endsWith("rdf")) {
                    return true;
                }
                return false;
            }
        };
        File[] files = eventUploadDirectory.listFiles(filter);
        return Response.status(Response.Status.OK).entity(files).build();
    }

    /**
     * Uploads a new event
     * @param params The parameters of the event
     * @param graphId The id of the graph to add the event to
     * @return The response
     * @throws ParseException
     * @throws URISyntaxException
     */
    @Path("local/{gid}")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response uploadEvent(final MultivaluedMap<String, String> params,
            @PathParam("gid") String graphId) throws ParseException,
            URISyntaxException {
        Event event = new Event();

        String graphUri = uriInfo.getRequestUri().toString();
        if (!graphUri.endsWith("/")) {
            graphUri += "/";
        }
        event.setGraph(graphUri);

        String uri = null;
        if (params.get("uri") == null) {
            uri = graphUri + System.currentTimeMillis()
                + (int) (Math.random() * 100000);
        } else {
            uri = params.remove("uri").get(0);
            if (!uri.startsWith(graphUri)) {
                return Response.status(Response.Status.CONFLICT).build();
            }
        }
        event.setId(uri);

        if (params.get("title") != null) {
            event.setTitle(params.remove("title").get(0));
        }
        if (params.get("description") != null) {
            event.setDescription(params.remove("description").get(0));
        }

        DateTimeFormatter fmt = DateTimeFormat.forPattern(DATETIME_PATTERN);
        if (params.get("startDateTime") != null) {
            event.setStartDateTime(fmt.parseDateTime(
                    params.remove("startDateTime").get(0)));
        }
        if (params.get("endDateTime") != null) {
            event.setEndDateTime(fmt.parseDateTime(
                    params.remove("endDateTime").get(0)));
        }

        if (params.get("isPartOf") != null) {
            EventParent parent = new EventParent();
            parent.setId(params.get("isPartOf").get(0));
            Vector<EventParent> parents = new Vector<EventParent>();
            parents.add(parent);
            event.setPartOf(parents);
        }

        File eventUploadDirectory = new File(eventUploadStoreDirectory,
                graphId);
        eventUploadDirectory.mkdirs();
        Model eventModel = Utility.createModelFromEvent(event);
        File file = new File(eventUploadDirectory,
                event.getId().substring(graphUri.length()) + ".rdf");
        try {
            eventModel.write(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Response.status(Response.Status.CONFLICT).build();
        }

        try {
            List<HarvestSourceAuthority> authorities =
                harvesterSourceManagementFacade.defaultPermissions();
            for (HarvestSourceAuthority auth : authorities) {
                auth.setGraph(graphUri);
            }

            harvesterSourceManagementFacade.addSource(graphUri, "Local Events",
                    "Events Uploaded by the Recorder", false);
            harvesterSourceManagementFacade.updatePermissions(graphUri,
                    authorities);
            harvesterSourceManagementFacade.harvestSource(graphUri);
        } catch (Throwable t) {
            t.printStackTrace();
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.created(new URI(event.getId())).build();
    }
}
