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
import junit.framework.TestCase;
import net.crew_vre.harvester.HarvestSource;
import net.crew_vre.harvester.HarvesterDao;
import net.crew_vre.harvester.Resolver;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;

import java.io.File;
import java.util.Date;

/**
 * @author Damian Steer (d.steer@bristol.ac.uk)
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HttpResolverTest.java 1190 2009-03-31 13:22:30Z cmmaj $
 */
public class HttpResolverTest extends TestCase {

    private Server server;

    @Before
    public void setUp() throws Exception {

        // calculate the directory the file are served from ...
        File file = new File(getClass().getClassLoader().getResource("rdf").toURI());
        String path = file.getAbsolutePath();
        //String path = filePath.substring(0, filePath.length() - eventFile.length());

        server = new Server(port);
        ResourceHandler handler = new ResourceHandler();
        handler.setResourceBase(path);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{handler, new DefaultHandler()});

        server.setHandler(handlers);
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testResolverWith200() throws Exception {


        // create a mock harvest object
        HarvestSource harvestSource = new HarvestSourceImpl(location, name, description,
                isBlocked);

        Mockery context = new Mockery();

        // mock the management facade
        final HarvesterDao harvesterDao =
                context.mock(HarvesterDao.class);

        // mock the action of updating the source after the source has been visited
        context.checking(new Expectations() {{
            oneOf(harvesterDao).updateHarvestSource(with(aNonNull(String.class)),
                    with(aNonNull(String.class)), with(aNonNull(String.class)),
                    with(aNonNull(Date.class)), with(aNonNull(String.class)),
                    with(aNonNull(Boolean.class)));
        }});

        Resolver resolver = new HttpResolver(harvesterDao);

        Model m = resolver.get(harvestSource);

        assertNotNull("The model should not be null", m);
        assertTrue("The model should have triples", m.size() > 0);


    }

    @Test
    public void testResolver404() throws Exception {

        // create a mock harvest object
        HarvestSource harvestSource = new HarvestSourceImpl("http://localhost:9090/missing.rdf",
                name, description, false);

        Mockery context = new Mockery();

        // mock the management facade
        final HarvesterDao harvesterDao =
                context.mock(HarvesterDao.class);

        // mock the action of updating the source after the source has been visited
        context.checking(new Expectations() {{
            oneOf(harvesterDao).updateHarvestSource(with(aNonNull(String.class)),
                    with(aNonNull(String.class)), with(aNonNull(String.class)),
                    with(aNonNull(Date.class)), with(aNonNull(String.class)),
                    with(aNonNull(Boolean.class)));
        }});

        Resolver resolver = new HttpResolver(harvesterDao);

        Model m = resolver.get(harvestSource);

        assertNull("The model should be null", m);

    }

    private final int port = 9090;

    final String location = "http://localhost:9090/event.rdf";
    final String name = "Test";
    final String description = "Test";
    final boolean isBlocked = false;
}
