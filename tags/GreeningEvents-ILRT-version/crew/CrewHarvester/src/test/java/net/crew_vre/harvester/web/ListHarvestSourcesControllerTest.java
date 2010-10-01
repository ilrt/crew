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
package net.crew_vre.harvester.web;

import net.crew_vre.harvester.HarvesterSourceManagementFacade;
import org.jmock.Expectations;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: ListHarvestSourcesControllerTest.java 1190 2009-03-31 13:22:30Z cmmaj $
 */
public class ListHarvestSourcesControllerTest extends AbstractHarvesterTests {

    @Test
    @Override
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testListHarvestSources() throws Exception {

        // mock the facade
        final HarvesterSourceManagementFacade facade
                = context.mock(HarvesterSourceManagementFacade.class);

        context.checking(new Expectations() {{
            oneOf(facade).getAllSources();
            will(returnValue(createTestSources()));
        }});

        // initiate the controller with the mocked facade
        ListHarvestSourcesController controller = new ListHarvestSourcesController(facade);
        controller.setCommandClass(net.crew_vre.harvester.web.ListHarvestSourcesForm.class);
        request.setMethod("GET");

        // run the controller
        ModelAndView mav = controller.handleRequest(request, response);

        assertEquals("Unexpected view name", "listHarvestSources", mav.getViewName());

        List sources = (List) mav.getModel().get("sources");

        assertEquals("Unexpected number of sources", 2, sources.size());

        // checks that the facade was called - call made in the the controller life cycle
        context.assertIsSatisfied();
    }

    @Test
    public void testDeleteSelected() throws Exception {

        final String location = "http://example.org/test1";

        // mock the facade
        final HarvesterSourceManagementFacade facade
                = context.mock(HarvesterSourceManagementFacade.class);

        // called to delete the facade
        context.checking(new Expectations() {{
            oneOf(facade).removeSource(location);
        }});

        // called to list the sources
        context.checking(new Expectations() {{
            oneOf(facade).getAllSources();
            will(returnValue(createTestSources()));
        }});

        // initiate the controller with the mocked facade
        ListHarvestSourcesController controller = new ListHarvestSourcesController(facade);
        controller.setCommandClass(net.crew_vre.harvester.web.ListHarvestSourcesForm.class);
        request.setMethod("POST");
        request.setParameter("deleteButton", "Delete");
        request.setParameter("id", location);

        // run the controller
        ModelAndView mav = controller.handleRequest(request, response);

        assertEquals("Unexpected view name", "listHarvestSources", mav.getViewName());

        // checks that the facade was called - call made in the the controller life cycle
        context.assertIsSatisfied();

    }

    @Test
    public void testAddselected() throws Exception {

        // mock the facade
        final HarvesterSourceManagementFacade facade
                = context.mock(HarvesterSourceManagementFacade.class);

        // initiate the controller with the mocked facade
        ListHarvestSourcesController controller = new ListHarvestSourcesController(facade);
        controller.setCommandClass(net.crew_vre.harvester.web.ListHarvestSourcesForm.class);
        request.setMethod("POST");
        request.setParameter("addButton", "Add Source");

        ModelAndView mav = controller.handleRequest(request, response);

        assertEquals("Unexpected view", "redirect:./addHarvestSource.do", mav.getViewName());
    }

    @Test
    public void testEditSelected() throws Exception {

        final String id = "http://example.org";

        // mock the facade
        final HarvesterSourceManagementFacade facade
                = context.mock(HarvesterSourceManagementFacade.class);

        // initiate the controller with the mocked facade
        ListHarvestSourcesController controller = new ListHarvestSourcesController(facade);
        controller.setCommandClass(net.crew_vre.harvester.web.ListHarvestSourcesForm.class);
        request.setMethod("POST");
        request.setParameter("editButton", "Edit");
        request.setParameter("id", id);
        ModelAndView mav = controller.handleRequest(request, response);

        assertEquals("Unexpected view", "redirect:./editHarvestSource.do?id=" + id,
                mav.getViewName());
    }

    @Test
    public void testHarvestSelected() throws Exception {

        final String location = "http://example.org";

        // mock the facade
        final HarvesterSourceManagementFacade facade
                = context.mock(HarvesterSourceManagementFacade.class);


        // called to harvest a source
        context.checking(new Expectations() {{
            oneOf(facade).harvestSource(location);
            returnValue("Done");
        }});

        // called when searching for a source
        context.checking(new Expectations() {{
            exactly(2).of(facade).getAllSources();
            returnValue(aNonNull(List.class));
        }});

        // initiate the controller with the mocked facade
        ListHarvestSourcesController controller = new ListHarvestSourcesController(facade);
        controller.setCommandClass(net.crew_vre.harvester.web.ListHarvestSourcesForm.class);
        request.setMethod("POST");
        request.setParameter("harvestButton", "Harvest");
        request.setParameter("id", location);
        ModelAndView mav = controller.handleRequest(request, response);

        assertEquals("Unexpected view", "listHarvestSources", mav.getViewName());

    }
}
