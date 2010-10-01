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
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: AddHarvestSourceControllerTest.java 1190 2009-03-31 13:22:30Z cmmaj $
 */
public class AddHarvestSourceControllerTest extends AbstractHarvesterTests {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testDisplayForm() throws Exception {

        // mock the facade
        final HarvesterSourceManagementFacade facade =
                context.mock(HarvesterSourceManagementFacade.class);

        context.checking(new Expectations() {{
            oneOf(facade).defaultPermissions();
        }});

        context.checking(new Expectations() {{
            oneOf(facade).getAuthoritiesList(null);
            //will(returnValue(aNonNull(List.class)));
        }});

        AddHarvestSourceController controller = new AddHarvestSourceController(facade);
        controller.setCommandClass(net.crew_vre.harvester.web.HarvestSourceForm.class);
        controller.setFormView("addHarvestSource");

        request.setMethod("GET");

        ModelAndView mav = controller.handleRequest(request, response);

        assertEquals("Unexpected display name", "addHarvestSource", mav.getViewName());

    }


    @Test
    public void testSubmitForm() throws Exception {

        // test source values
        final String location = "http://example";
        final String name = "test";
        final String description = "test";
        final boolean blocked = false;
        final String button = "addButton";

        // mock the facade
        final HarvesterSourceManagementFacade facade =
                context.mock(HarvesterSourceManagementFacade.class);

        //context.checking(new Expectations() {{
        //    oneOf(facade).defaultPermissions();
        //}});

        context.checking(new Expectations() {{
            oneOf(facade).updatePermissions(location, new ArrayList());
        }});

        // controller will call this to add the source
        context.checking(new Expectations() {{
            oneOf(facade).addSource(location, name, description, blocked);
        }});

        // create controller and hook in the facade
        AddHarvestSourceController controller = new AddHarvestSourceController(facade);
        controller.setCommandClass(net.crew_vre.harvester.web.HarvestSourceForm.class);
        controller.setSuccessView("redirect:./listHarvestSources.do");

        // create the post requests
        request.setMethod("POST");
        request.setParameter("location", location);
        request.setParameter("name", name);
        request.setParameter("description", description);
        request.setParameter("blocked", String.valueOf(blocked));
        request.setParameter(button, button);

        // get and test the view
        ModelAndView mav = controller.handleRequest(request, response);
        assertEquals("Unexpected display name", "redirect:./listHarvestSources.do",
                mav.getViewName());

        // check the controller ran expecyed facade methods
        context.assertIsSatisfied();
    }


}
