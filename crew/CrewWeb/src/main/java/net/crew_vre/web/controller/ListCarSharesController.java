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

import net.crew_vre.web.history.BrowseHistory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ilrt.dibden.facade.UserManagementFacade;
import org.ilrt.dibden.domain.User;

/**
 * @author Pihl Cross (phil.cross@bristol.ac.uk)
 */
public class ListCarSharesController implements Controller {

    private static String SUCCESSFUL_REQ = "requestcarshares.do";
    private static String FAILED_REQ = "listcarshares.do";
    private static int DEFAULT_DISTANCE = 5;
    private static int MAX_ALLOWED_DISTANCE = 10;
    
    public ListCarSharesController(UserManagementFacade usermanagementFacade, BrowseHistory
            browseHistory) {
        this.usermanagementFacade = usermanagementFacade;
        this.browseHistory = browseHistory;
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
            	    
        String localUserName = null;
        User localUser = null;
        String localPostcode = null;
        int maxDistance;
        
        ModelAndView mov = null;

        if (request.getUserPrincipal() != null) {
            localUserName = request.getUserPrincipal().getName();
            localUser = userManagementFacade.getUser(localUserName);
            
            localPostcode = localUser.getPostcode();
            
            // Check that maxDistance is within allowed range
	    if ( request.getParameter("maxDistance") ) {
		    try {
				maxDistance = Integer.parseInt(request.getParameter("maxDistance"));
				if (maxDistance < 1) { maxDistance = DEFAULT_DISTANCE; }
				if (maxDistance > MAX_ALLOWED_DISTANCE) { maxDistance = MAX_ALLOWED_DISTANCE; }				
		    } catch (NumberFormatException) {
		    	maxDistance = DEFAULT_DISTANCE;
		    }
	    } else {
	    	maxDistance = DEFAULT_DISTANCE;
	    }
            
		
		if (localPostcode != null && !localPostcode.equals("")) {
			// We have a post code for the user
			mov = new ModelAndView(SUCCESSFUL_REQ); // OR SHOULD THIS BE A REDIRECT????
			
			// Now lookup all other available post codes for all users (not including this one!)
			
			// Get all users
			List<User> allUsers = userManagementFacade.getUsers();
			List<User> carsharers = new List<User>();
			for (User remoteUser: allUsers) {
				String remotePostcode = remoteUser.getPostcode  ();
				if (remotePostcode != null && !remotePostcode.equals("")) {
					// Check if post code within specified distance
					if ( isWithinRange(localPostcode, remotePostcode) ) {
						carsharers.add(remoteUser);	
					}
				}
			}
			mov.addObject("carsharers",carsharers);
		} else {
			// No postcode for this user so return error message    
			mov = new ModelAndView(FAILED_REQ);
			mov.addObject("error", "postcode.error.nopostcode");
		}
            
            
            
        }
        
        ////////////////////////////////////////

        if (request.getParameter("startPointId") != null) {
            startPoint = displayRouteFacade.displayStartPoint(request.getParameter("startPointId"));
            if (startPoint != null) {
                browseHistory.addHistory(request, startPoint.getTitle());
            }
        }

        return mov;
    }
    
    private boolean isWithinRange ( String postcode1, String postcode2 ) {
    	
    	// Tidy up postcodes
    	
    	
    }

    private UsermanagementFacade usermanagementFacade;

    private BrowseHistory browseHistory;

}
