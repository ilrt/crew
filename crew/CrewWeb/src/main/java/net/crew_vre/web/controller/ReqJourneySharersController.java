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
package net.crew_vre.web.controller;

import java.util.ArrayList;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.crew_vre.web.command.JourneySharersForm;
import net.crew_vre.web.facade.SendEmailToUsersFacade;
import org.ilrt.dibden.facade.UserManagementFacade;
import org.ilrt.dibden.domain.User;

/**
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 */
public class ReqJourneySharersController extends SimpleFormController {

    /*
     * The logic in this controller should really be split between two separate controllers!
     */

//    private Logger logger = Logger.getLogger("net.crew_vre.web.controller.JourneySharersController");

    private UserManagementFacade userManagementFacade;
    private SendEmailToUsersFacade sendEmailToUsersFacade;

    private static String JOURNEY_SHARERS = "findJourneySharers";
    private static String noUserEmailMsg = "Your own email is not set in your profile";
    private static String noUsersMsg = "You need to select at least one other delegate";
    private static String selectRangeMsg = "Select a maximum range in km and submit";
    private static String errorSendingEmailMsg = "There was an error sending the email(s). Please contact the help desk";
    private static String successfulEmailMsg = "Your email(s) were successfully sent";
    
    public ReqJourneySharersController(UserManagementFacade userManagementFacade,
            SendEmailToUsersFacade sendEmailToUsersFacade) {
        this.userManagementFacade = userManagementFacade;
        this.sendEmailToUsersFacade = sendEmailToUsersFacade;
    }

    @Override
    protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response,
            BindException errors)  throws Exception {

        // This is a GET request not a submission from the findJourneySharers page so
        // ignore and send back findJourneySharers form instead
        ModelAndView mav = new ModelAndView(JOURNEY_SHARERS);
        mav.addObject("message", selectRangeMsg);
        return mav;
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors) {

        // get the command object - RequestJourneySharersForm
        JourneySharersForm journeySharersForm = (JourneySharersForm) command;
            	    
        User localUser = null;
        String localUsername;
        String localUserEmailAddress = null;
        String userMessage;

        ModelAndView mav = null;

        if ( journeySharersForm.getCancelButton() != null ) {
            // Send back to original search form with message
            mav = new ModelAndView(JOURNEY_SHARERS);
            mav.addObject("message", selectRangeMsg);
            return mav;
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug("Have submit to journey sharers form");
        }

        // Do we have the user's username? If not return null
        if (request.getUserPrincipal() != null) {

            if (logger.isDebugEnabled()) {
                logger.debug("Have user's username");
            }

            localUsername = request.getUserPrincipal().getName();
            localUser = userManagementFacade.getUser(localUsername);
            localUserEmailAddress = localUser.getEmail();
            if (localUserEmailAddress == null) {
                // Send back to original search form with message
                mav = new ModelAndView(JOURNEY_SHARERS);
                mav.addObject("message", noUserEmailMsg);
                return mav;
            }
            String[] remoteUsernames = journeySharersForm.getUsernames();

            if ( remoteUsernames == null || remoteUsernames.length < 1 ) {
                // No users selected - return form with error message
                mav = new ModelAndView(getSuccessView());
                mav.addObject("message", noUsersMsg);
                return mav;
            }

            ArrayList<User> remoteUsers = new ArrayList<User>();
            for (String username : remoteUsernames) {
                remoteUsers.add(userManagementFacade.getUser(username));
            }


            userMessage = journeySharersForm.getMessage();

            // Send off emails
            sendEmailToUsersFacade.setLocalUser(localUser);
            sendEmailToUsersFacade.setUserMessage(userMessage);
            sendEmailToUsersFacade.setUsers(remoteUsers);

            mav = new ModelAndView(getSuccessView());

            if (sendEmailToUsersFacade.sendMessages()) {
                mav.addObject("message",successfulEmailMsg);
            } else {
                mav.addObject("message", errorSendingEmailMsg);
            }

        } // end if getUserPrinciple not null

        return mav;
    }
    
}
