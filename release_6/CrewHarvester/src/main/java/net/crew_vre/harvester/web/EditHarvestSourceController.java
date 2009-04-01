package net.crew_vre.harvester.web;

import net.crew_vre.harvester.HarvestSource;
import net.crew_vre.harvester.HarvesterSourceManagementFacade;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: EditHarvestSourceController.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class EditHarvestSourceController extends SimpleFormController {

    public EditHarvestSourceController(HarvesterSourceManagementFacade facade) {
        this.facade = facade;
    }

    @Override
    public ModelAndView showForm(HttpServletRequest request, HttpServletResponse response,
                                 BindException errors) {

        ModelAndView mav = new ModelAndView("editHarvestSource");

        String id = request.getParameter("id");

        if (id != null) {
            HarvestSource source = facade.getSource(id);
            mav.addObject("source", source);
        }

        return mav;
    }

    protected Object formBackingObject(HttpServletRequest request)
            throws Exception {

        return new HarvestSourceForm();
    }

    @Override
    public ModelAndView onSubmit(Object command) throws ServletException {

        // get the form object
        HarvestSourceForm source = (HarvestSourceForm) command;

        // only do something if the update button was pressed
        if (source.getUpdateButton() != null) {

            // update the harvester source
            facade.updateSource(source.getLocation(), source.getName(), source.getDescription(),
                    source.getLastVisited(), source.getLastStatus(), source.isBlocked());
        }

        return null; // success view handled by the spring configuration
    }

    private final HarvesterSourceManagementFacade facade;
}
