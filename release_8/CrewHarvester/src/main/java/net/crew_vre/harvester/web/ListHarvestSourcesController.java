package net.crew_vre.harvester.web;

import net.crew_vre.harvester.HarvesterSourceManagementFacade;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: ListHarvestSourcesController.java 929 2008-11-18 15:28:41Z cmmaj $
 */
public class ListHarvestSourcesController extends SimpleFormController {

    public ListHarvestSourcesController(HarvesterSourceManagementFacade facade) {
        this.facade = facade;
    }


    @Override
    public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response,
                                 Object command, BindException errors) throws ServletException {

        ListHarvestSourcesForm listForm = (ListHarvestSourcesForm) command;

        // add a new source
        if (listForm.getAddButton() != null) {
            return new ModelAndView("redirect:./addHarvestSource.do");
        } else {

            if (listForm.getId() != null) { // anything else needs an id

                if (listForm.getDeleteButton() != null) { // delete
                    facade.removeSource(listForm.getId());
                } else if (listForm.getEditButton() != null) { // edit
                    return new ModelAndView("redirect:./editHarvestSource.do?id="
                            + listForm.getId());
                } else if (listForm.getHarvestButton() != null) { // harvest

                    String msg = facade.harvestSource(listForm.getId());
                    ModelAndView mav = listSources();
                    mav.addObject("harvestMessage", msg);
                }
            }
        }

        // defaults to just showing the list of events
        return listSources();
    }


    @Override
    public ModelAndView showForm(HttpServletRequest request, HttpServletResponse response,
                                 BindException errors) {

        return listSources();
    }


    private ModelAndView listSources() {

        // display a list of harvester sources
        ModelAndView mav = new ModelAndView("listHarvestSources");
        mav.addObject("sources", facade.getAllSources());
        mav.addObject("listSourcesForm", new ListHarvestSourcesForm());
        return mav;
    }


    private HarvesterSourceManagementFacade facade;
}
