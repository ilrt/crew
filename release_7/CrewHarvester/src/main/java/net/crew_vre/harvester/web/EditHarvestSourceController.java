package net.crew_vre.harvester.web;

import net.crew_vre.harvester.HarvestSource;
import net.crew_vre.harvester.HarvesterSourceManagementFacade;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: EditHarvestSourceController.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class EditHarvestSourceController extends AbstractHarvestSourceController {

    public EditHarvestSourceController(HarvesterSourceManagementFacade facade) {
        this.facade = facade;
    }

    @Override
    public ModelAndView showForm(HttpServletRequest request, HttpServletResponse response,
                                 BindException errors) {

        ModelAndView mav = new ModelAndView("editHarvestSource");

        HarvestSourceForm harvestSourceForm = new HarvestSourceForm();

        String id = request.getParameter("id");

        if (id != null) {
            HarvestSource source = facade.getSource(id);
            if (source != null) {
                harvestSourceForm.setLocation(source.getLocation());
                harvestSourceForm.setDescription(source.getDescription());
                harvestSourceForm.setBlocked(source.isBlocked());
                harvestSourceForm.setAuthorityList(facade.lookupPermissions(id));
                mav.addObject("source", harvestSourceForm);
                mav.addObject("authorities", facade.getAuthoritiesList(source.getLocation()));
            }
        }

        return mav;
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 Object command, BindException errors) {

        HarvestSourceForm source = (HarvestSourceForm) command;

        // only do something if the update button was pressed
        if (source.getUpdateButton() != null) {

            // update the harvester source
            facade.updateSource(source.getLocation(), source.getName(), source.getDescription(),
                    source.getLastVisited(), source.getLastStatus(), source.isBlocked());

            // update the access controls
            facade.updatePermissions(source.getLocation(),
                    findAuthorities(source.getLocation(), request));
        }

        return new ModelAndView("redirect:./listHarvestSources.do");
    }

    private final HarvesterSourceManagementFacade facade;
}
