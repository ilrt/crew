package net.crew_vre.harvester.web;

import net.crew_vre.harvester.HarvesterSourceManagementFacade;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: AddHarvestSourceController.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class AddHarvestSourceController extends AbstractHarvestSourceController {

    public AddHarvestSourceController(HarvesterSourceManagementFacade facade) {
        this.facade = facade;
    }

    @Override
    public ModelAndView showForm(HttpServletRequest request, HttpServletResponse response,
                                 BindException errors) {

        ModelAndView mav = new ModelAndView(VIEW_NAME);
        HarvestSourceForm harvestSourceForm = new HarvestSourceForm();
        harvestSourceForm.setAuthorityList(facade.defaultPermissions());
        mav.addObject("authorities", facade.getAuthoritiesList(null));
        mav.addObject("source", harvestSourceForm);
        return mav;
    }


    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 Object command, BindException errors) {

        // make sure that user entered data is returned
        if (errors.hasErrors()) {

            ModelAndView mav = new ModelAndView(VIEW_NAME);
            HarvestSourceForm harvestSourceForm =
                    (HarvestSourceForm) errors.getModel().get("source");

            List<HarvestSourceAuthority> authoritesSelected =
                    findAuthorities(harvestSourceForm.getLocation(), request);

            harvestSourceForm.setAuthorityList(authoritesSelected);

            // when an error has been caught, a new role with permission might have
            // been added to the form but not persisted. we need to ensure that the
            // role is removed from the drop down list of roles so we don't get
            // duplicates.

            Set<String> selectedAuthorites = new HashSet<String>();

            for (HarvestSourceAuthority harvestSourceAuthority : authoritesSelected) {
                selectedAuthorites.add(harvestSourceAuthority.getAuthority());
            }

            List<String> authorityList = facade.getAuthoritiesList(harvestSourceForm.getLocation());

            authorityList.removeAll(selectedAuthorites);

            errors.getModel().put("source", harvestSourceForm);
            mav.addObject("authorities", authorityList);
            mav.addAllObjects(errors.getModel());
            return mav;
        }


        // get the form object
        HarvestSourceForm source = (HarvestSourceForm) command;

        // only add if the add button is pressed ...
        if (source.getAddButton() != null) {

            // save the harvester source
            facade.addSource(source.getLocation(), source.getName(),
                    source.getDescription(), source.isBlocked());

            // add the permissions
            facade.updatePermissions(source.getLocation(),
                    findAuthorities(source.getLocation(), request));
        }

        return new ModelAndView("redirect:./listHarvestSources.do");
    }


    private final HarvesterSourceManagementFacade facade;

    private final String VIEW_NAME = "addHarvestSource";
}
