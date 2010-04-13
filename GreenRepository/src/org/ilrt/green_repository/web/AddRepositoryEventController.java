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
import net.crew_vre.harvester.HarvesterSourceManagementFacade;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 *
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 */

public class AddRepositoryEventController extends SimpleFormController {


    private final RepositoryEventManagementFacade repositoryFacade;
    private HarvesterSourceManagementFacade harvesterFacade;
    private final String VIEW_NAME = "addRepositoryEvent";
    private final String DEFAULT_REPOSITORY_LOCATION = "repository/localEvents.rdf";
    private Map<String, String> config;

    public AddRepositoryEventController(RepositoryEventManagementFacade repositoryFacade,
            HarvesterSourceManagementFacade harvesterFacade, final Map<String, String> config) {
        this.repositoryFacade = repositoryFacade;
        this.harvesterFacade = harvesterFacade;
        this.config = config;
    }

    @Override
    public ModelAndView showForm(HttpServletRequest request, HttpServletResponse response,
                                 BindException errors) {

        ModelAndView mav = new ModelAndView(VIEW_NAME);
        RepositoryEventForm repositoryEventForm = new RepositoryEventForm();
        mav.addObject("repositoryEventForm", repositoryEventForm);
        return mav;
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors) {

        // get the command object - RepositoryEventForm
        RepositoryEventForm repositoryEventForm = (RepositoryEventForm) command;

        // only add if the add button is pressed ...
        if (repositoryEventForm.getAddButton() != null) {
            // save the repository event
            repositoryFacade.addRepositoryEvent(repositoryEventForm);

            // Re-harvest from the local repository
            String repositoryLocation = config.get("location");
            if (repositoryLocation == null)
                repositoryLocation = DEFAULT_REPOSITORY_LOCATION;

            String msg = harvesterFacade.harvestSource(
                    request.getScheme() + "://" +
                    request.getServerName() + ":" +
                    request.getServerPort() +
                    request.getContextPath() + "/" + repositoryLocation);
        }

        return new ModelAndView("redirect:./listRepositoryEvents.do");
    }
}
