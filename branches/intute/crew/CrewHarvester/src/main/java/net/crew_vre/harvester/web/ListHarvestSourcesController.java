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
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: ListHarvestSourcesController.java 1190 2009-03-31 13:22:30Z cmmaj $
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
