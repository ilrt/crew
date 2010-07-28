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
import java.util.HashMap;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.crew_vre.web.command.JourneySharersForm;
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

    private static String JOURNEY_SHARERS = "findJourneySharers";
    private static String noUserEmailMsg = "Your own email is not set in your profile";
    private static String noUsersMsg = "You need to select at least one other delegate";
    private static String selectRangeMsg = "Select a maximum range in km and submit";

    private static String emailMessageText = " wishes to ask if you would like to share the journey to XXX on XXX.  Please see their message " +
            "below. If you wish to respond to this request, you can email to: ";
    
    public ReqJourneySharersController(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
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
        ArrayList<String> notFoundUsers = new ArrayList<String>();
        HashMap<String,String> emailAddressMap = new HashMap<String,String>();
        HashMap<String,String> nameMap = new HashMap<String,String>();

        String debugOutText = null;  ////  DEBUGGING

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
            String[] remoteUsers = journeySharersForm.getUsernames();

            if ( remoteUsers == null || remoteUsers.length < 1 ) {
                // No users selected - return form with error message
                mav = new ModelAndView(getSuccessView());
                mav.addObject("message", noUsersMsg);
                return mav;
            }

            userMessage = journeySharersForm.getMessage();

            // Get user email addresses and names
            for (String username : remoteUsers) {
                User user = userManagementFacade.getUser(username);
                if (user == null) {
                    notFoundUsers.add(username);
                    continue;
                }
                emailAddressMap.put(username, user.getEmail());
                nameMap.put(username, user.getName());
            }

///////////// DEBUGGING /////////////
            StringBuffer debugOutput = new StringBuffer("Will email the following users: <br/>");
            for (String username : nameMap.keySet()) {
                debugOutput.append( (String)nameMap.get(username) );
                debugOutput.append(" : ");
                debugOutput.append( (String)emailAddressMap.get(username) );
                debugOutput.append(" <br/>");
            }
            debugOutput.append("with message<br/>" + userMessage);
            debugOutText = debugOutput.toString();

///////////////////////////////////////////////////

            mav = new ModelAndView(getSuccessView());
            mav.addObject("message",debugOutText);

        } // end if getUserPrinciple not null

        return mav;
    }
    
}
