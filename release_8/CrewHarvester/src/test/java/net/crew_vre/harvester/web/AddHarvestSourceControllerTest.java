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
 * @version $Id: AddHarvestSourceControllerTest.java 1109 2009-03-17 15:47:00Z cmmaj $
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
