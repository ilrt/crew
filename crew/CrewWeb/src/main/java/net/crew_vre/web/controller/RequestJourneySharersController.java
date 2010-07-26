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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ilrt.dibden.facade.UserManagementFacade;
import org.ilrt.dibden.domain.User;

import org.apache.log4j.Logger;

/**
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 */
public class RequestJourneySharersController implements Controller {



    private Logger logger = Logger.getLogger("net.crew_vre.web.controller.RequestJourneySharersController");

    private UserManagementFacade userManagementFacade;

    private static String RETURN_TO_PAGE = "requestJourneySharers";
    private static String SUCCESS   = "requestSent";

    private static String messageText = " wishes to ask if you would like to share the journey to XXX on XXX.  Please see their message " +
            "below. If you wish to respond to this request, you can email to: ";
    
    public RequestJourneySharersController(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String localUserName;
        User localUser;
        String[] remoteUsers;
        String userMessage;
        ArrayList<String> notFoundUsers = new ArrayList<String>();
        HashMap<String,String> emailAddressMap = new HashMap<String,String>();
        HashMap<String,String> nameMap = new HashMap<String,String>();

        String debugOutText = null;  ////  DEBUGGING

        ModelAndView mov = null;
        
        if (request.getUserPrincipal() != null) {
            localUserName = request.getUserPrincipal().getName();
            localUser = userManagementFacade.getUser(localUserName);
            remoteUsers = request.getParameterValues("username");
            if ( remoteUsers == null || remoteUsers.length < 1 ) {
                // No users requested
                mov = new ModelAndView(RETURN_TO_PAGE);
                mov.addObject("message", "<fmt:message key=\"journeysharer.message.nousersselected\"/>");
            }
            userMessage = request.getParameter("message");

            // Get user email addresses and names
            for (String username: remoteUsers) {
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
            for (String username: nameMap.keySet()) {
                debugOutput.append( (String)nameMap.get(username) );
                debugOutput.append(" : ");
                debugOutput.append( (String)emailAddressMap.get(username) );
                debugOutput.append(" <br/>\\n");
            }
            debugOutText = debugOutput.toString();
        }

        mov = new ModelAndView(SUCCESS);
        mov.addObject("response",debugOutText);
        return mov;

    }
    

}
