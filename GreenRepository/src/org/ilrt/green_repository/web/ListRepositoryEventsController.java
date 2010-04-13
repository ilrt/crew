/**
 * Copyright (c) 2010, University of Bristol
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

package org.ilrt.green_repository.web;

import org.ilrt.green_repository.RepositoryEventManagementFacade;
import org.ilrt.green_repository.domain.RepositoryEvent;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import net.crew_vre.harvester.HarvesterSourceManagementFacade;

/**
 *
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 */
public class ListRepositoryEventsController extends SimpleFormController {

    private RepositoryEventManagementFacade repositoryFacade;
    private HarvesterSourceManagementFacade harvesterFacade;
    private final String VIEW_NAME = "listRepositoryEvents";
    private Map<String, String> config;
    private final String DEFAULT_REPOSITORY_LOCATION = "repository/localEvents.rdf";

    public ListRepositoryEventsController(RepositoryEventManagementFacade repositoryFacade,
            HarvesterSourceManagementFacade harvesterFacade, final Map<String, String> config) {
        this.repositoryFacade = repositoryFacade;
        this.harvesterFacade = harvesterFacade;
        this.config = config;
    }


    @Override
    public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response,
                                 Object command, BindException errors) throws ServletException {

        ListRepositoryEventsForm listForm = (ListRepositoryEventsForm) command;

        // add a new event
        if (listForm.getAddButton() != null) {
            return new ModelAndView("redirect:./addRepositoryEvent.do");
        } else {

            if (listForm.getEventId() != null) { // anything else needs an id

                if (listForm.getDeleteButton() != null) { // delete
                    repositoryFacade.removeRepositoryEvent(listForm.getEventId());
                    // Re-harvest from the local repository
                    String repositoryLocation = config.get("location");
                    if (repositoryLocation == null)
                        repositoryLocation = DEFAULT_REPOSITORY_LOCATION;

                    String msg = harvesterFacade.harvestSource(
                            request.getScheme() + "://" +
                            request.getServerName() + ":" +
                            request.getServerPort() +
                            request.getContextPath() + "/" + repositoryLocation);
                    
                } else if (listForm.getEditButton() != null) { // we want to edit an existing role - redirect to correct view
                    if (listForm.getEventId() != null) {
                        RepositoryEvent event = repositoryFacade.getRepositoryEvent(listForm.getEventId());
                        RepositoryEventForm form = new RepositoryEventForm(event);
                        ModelAndView mav = new ModelAndView("editRepositoryEvent");
                        mav.addObject("repositoryEventForm", form);
                        return mav;
                    }
                }
            }
        }

        // defaults to just showing the list of events
        return listRepositoryEvents();
    }


    @Override
    public ModelAndView showForm(HttpServletRequest request, HttpServletResponse response,
                                 BindException errors) {

        return listRepositoryEvents();
    }


    private ModelAndView listRepositoryEvents() {

        // display a list of harvester sources
        ModelAndView mav = new ModelAndView(VIEW_NAME);
        mav.addObject("events", repositoryFacade.getAllRepositoryEvents());
        mav.addObject("listRepositoryEventsForm", new ListRepositoryEventsForm());
        return mav;
    }

}
