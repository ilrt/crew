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
import org.springframework.web.servlet.mvc.AbstractWizardFormController;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import org.ilrt.green_repository.domain.RepositoryEvent;
import org.springframework.validation.Errors;

/**
 *
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 */

public class EditRepositoryEventWizardController extends AbstractWizardFormController {

    private final String DEFAULT_REPOSITORY_LOCATION = "repository/localEvents.rdf";
    private Map<String, String> config;
    private final RepositoryEventManagementFacade repositoryFacade;
    private HarvesterSourceManagementFacade harvesterFacade;
    private Logger logger = Logger.getLogger("org.ilrt.green_repository.web.EditRepositoryEventController");

    public EditRepositoryEventWizardController(RepositoryEventManagementFacade repositoryFacade,
            HarvesterSourceManagementFacade harvesterFacade, final Map<String, String> config) {
        this.repositoryFacade = repositoryFacade;
        this.harvesterFacade = harvesterFacade;
        this.config = config;
    }

    // Get existing data on the event and insert into the command object passed to the first page
    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        RepositoryEventForm form = null;
        if (request.getParameter("eventId") != null) {
            RepositoryEvent event = repositoryFacade.getRepositoryEvent((String)request.getParameter("eventId"));
            form = new RepositoryEventForm(event);
        }
        return form;
    }

    @Override
    protected ModelAndView processFinish(HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors) {

        // get the command object - RepositoryEventForm
        RepositoryEventForm repositoryEventForm = (RepositoryEventForm) command;

        // update the repository event
        repositoryFacade.updateRepositoryEvent(repositoryEventForm);

        // Re-harvest from the local repository
        String repositoryLocation = config.get("location");
        if (repositoryLocation == null)
            repositoryLocation = DEFAULT_REPOSITORY_LOCATION;

        StringBuffer repositoryUrl = new StringBuffer();
        repositoryUrl.append(request.getScheme());
        repositoryUrl.append("://");
        repositoryUrl.append(request.getServerName());

        if ( request.getServerPort() != 80 )  {
            // A direct string comparison is made with the list of harvestable urls, so :80 will cause an error
            repositoryUrl.append( ":");
            repositoryUrl.append(request.getServerPort());
        }

        repositoryUrl.append(request.getContextPath());
        repositoryUrl.append("/");
        repositoryUrl.append(repositoryLocation);

        logger.debug("Requesting reharvest at: " + repositoryUrl.toString());

        String msg = harvesterFacade.harvestSource(repositoryUrl.toString());

        return new ModelAndView(getSuccessView());
    }

    @Override
    protected void validatePage(Object command, Errors errors, int page) {
        RepositoryEventForm repositoryEventForm = (RepositoryEventForm) command;
        RepositoryEventValidator validator = (RepositoryEventValidator)getValidator();

        if (page == 0) {
            // Details page
            validator.validateTitle(repositoryEventForm, errors);
            validator.validateDates(repositoryEventForm, errors);
            validator.validateUrl(repositoryEventForm, errors);
        }

        if (page == 1) {
            // Location details page
            validator.validateLocation(repositoryEventForm, errors);
        }
    }

    @Override
    protected ModelAndView processCancel(HttpServletRequest request, HttpServletResponse response,
            Object command, BindException bindException) throws Exception {
        return new ModelAndView(getSuccessView());
    }

    private String getSuccessView() {
        // Return last entry in pages list for controller
        return getPages()[getPages().length-1];
    }
}
