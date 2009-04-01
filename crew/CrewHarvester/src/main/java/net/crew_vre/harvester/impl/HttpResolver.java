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
package net.crew_vre.harvester.impl;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import net.crew_vre.harvester.HarvestSource;
import net.crew_vre.harvester.HarvesterDao;
import net.crew_vre.harvester.Resolver;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Date;

/**
 * @author Damian Steer (d.steer@bristol.ac.uk)
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HttpResolver.java 1190 2009-03-31 13:22:30Z cmmaj $
 */
public class HttpResolver implements Resolver {

    public HttpResolver(HarvesterDao harvesterDao) {
        this.harvesterDao = harvesterDao;
    }

    public Model get(HarvestSource source) {

        // using a http client to GET the resource
        HttpClient client = new HttpClient();
        HttpMethod get = new GetMethod(source.getLocation());

        // only get if the file is modified since the last visit
        // TODO bet that time isn't formatted correctly
        if (source.getLastVisited() != null) {
            get.addRequestHeader("if-modified-since", source.getLastVisited().toString());
        }
        get.setFollowRedirects(true);

        // whatever errors ... we attempted a visit at this time
        Date lastVisited = new Date();

        int status;

        // what status was the request?
        try {
            status = client.executeMethod(get);
        } catch (IOException ex) {
            log.error("Error GETting <" + source.getLocation() + ">", ex);

            harvesterDao.updateHarvestSource(source.getLocation(), source.getName(),
                    source.getDescription(), lastVisited, "Error: " + ex.getMessage(),
                    source.isBlocked());

            return null;
        }

        // handle none 200 requests
        if (status != HttpStatus.SC_OK) {
            if (status >= 400) // generally bad
                log.warn("GET failed on: <" + source.getLocation() + "> "
                        + get.getStatusLine());

            harvesterDao.updateHarvestSource(source.getLocation(), source.getName(),
                    source.getDescription(), lastVisited, get.getStatusText(),
                    source.isBlocked());

            return null;
        }

        // Ropey! Doesn't deal with other forms of rdf/xml, either
        String contentType = get.getResponseHeader("Content-Type").getValue();

        if (!contentType.contains("application/rdf+xml")) {

            harvesterDao.updateHarvestSource(source.getLocation(), source.getName(),
                    source.getDescription(), lastVisited, "Didn't find RDF",
                    source.isBlocked());

            return null;
        }

        Model model = ModelFactory.createDefaultModel();

        try {
            model.read(get.getResponseBodyAsStream(), source.getLocation(), "RDF/XML");
        } catch (Throwable ex) {
            log.error("Error reading <" + source.getLocation() + ">", ex);


            harvesterDao.updateHarvestSource(source.getLocation(), source.getName(),
                    source.getDescription(), lastVisited, "Error: " + ex.getMessage(),
                    source.isBlocked());

            return null;
        }

        harvesterDao.updateHarvestSource(source.getLocation(), source.getName(),
                source.getDescription(), lastVisited, String.valueOf(status),
                source.isBlocked());

        return model;

    }

    private HarvesterDao harvesterDao;
    private Log log = LogFactory.getLog(HttpResolver.class);
}
