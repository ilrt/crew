package net.crew_vre.harvester.web;

import net.crew_vre.harvester.HarvesterSourceManagementFacade;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.ServletException;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: AddHarvestSourceController.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class AddHarvestSourceController extends SimpleFormController {

    public AddHarvestSourceController(HarvesterSourceManagementFacade facade) {
        this.facade = facade;
    }

    @Override
    protected ModelAndView onSubmit(Object command) throws ServletException {

        // get the form object
        HarvestSourceForm source = (HarvestSourceForm) command;

        // only add if the add button is pressed ...
        if (source.getAddButton() != null) {

            // save the harvester source
            facade.addSource(source.getLocation(), source.getName(),
                    source.getDescription(), source.isBlocked());
        }

        return null; // default to "successView" in the config file
    }

    private final HarvesterSourceManagementFacade facade;
}
