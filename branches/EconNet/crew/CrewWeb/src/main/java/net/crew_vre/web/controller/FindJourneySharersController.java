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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.crew_vre.web.command.JourneySharersForm;
import org.apache.log4j.Logger;
import org.ilrt.dibden.facade.UserManagementFacade;
import org.ilrt.dibden.domain.User;

/**
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 */
public class FindJourneySharersController implements Controller {

    /*
     * The logic in this controller should really be split between two separate controllers!
     */

    private Logger logger = Logger.getLogger("net.crew_vre.web.controller.JourneySharersController");

    private UserManagementFacade userManagementFacade;
    private String postcodeMapDirName;
    String postcodesPath;

    private static String FIND_JOURNEY_SHARERS = "findJourneySharers";
    private static String REQ_JOURNEY_SHARERS = "reqJourneySharers";
    private static int DEFAULT_DISTANCE = 5;
    private static int MAX_ALLOWED_DISTANCE = 10;
    private static String noJourneySharersMsg = "There are no other journey sharers within the distance specified";
    private static String noPostcodeMsg = "You have not set a valid post code in your profile. " +
            "Go to the My Profile page to update your details.";
    private static String systemErrorMsg = "There has been an error with the system. Please notify the web site help";
    private static String selectRangeMsg = "Select a maximum range in km and submit";

    private int maxDistance;
    
    public FindJourneySharersController(UserManagementFacade userManagementFacade, String postcodeMapDirName) {
        this.userManagementFacade = userManagementFacade;
        this.postcodeMapDirName = postcodeMapDirName;
    }

    public ModelAndView handleRequest(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
            	    
        String localUserName = null;
        User localUser = null;
        String localPostcode = null;

        ModelAndView mav = null;

        if ( request.getParameter("cancelButton") != null
                || request.getParameter("maxDistance") == null
                || request.getParameter("maxDistance").equals("")) {

            // Just a request for the form
            mav = new ModelAndView(FIND_JOURNEY_SHARERS);
            mav.addObject("message", selectRangeMsg);
            return mav;
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug("Have submit to find journey sharers form");
        }
        
        try {
            // Do we have the user's username? If not return null ModelAndView
            if (request.getUserPrincipal() != null) {

                if (logger.isDebugEnabled()) {
                    logger.debug("Have user's username");
                }

                // get the full path to the post codes directory
                postcodesPath = new File(this.getClass().getClassLoader()
                        .getResource(postcodeMapDirName).toURI()).getAbsolutePath();

                localUserName = request.getUserPrincipal().getName();
                localUser = userManagementFacade.getUser(localUserName);

                localPostcode = localUser.getPostcode();

                if (logger.isDebugEnabled()) {
                    logger.debug("Checking for journeysharers for user: " + localUser.getName() + " with post code: "
                            + localPostcode);
                }

                // Check that maxDistance is within allowed range
                if ( request.getParameter("maxDistance") != null ) {
                    try {
                        maxDistance = Integer.parseInt(request.getParameter("maxDistance"));
                        if (maxDistance < 1) { maxDistance = DEFAULT_DISTANCE; }
                        if (maxDistance > MAX_ALLOWED_DISTANCE) { maxDistance = MAX_ALLOWED_DISTANCE; }
                    } catch (NumberFormatException nfe) {
                        maxDistance = DEFAULT_DISTANCE;
                    }
                } else {
                    maxDistance = DEFAULT_DISTANCE;
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("... using maximum distance of: " + maxDistance + "km");
                }

                if (localPostcode != null && !localPostcode.equals("")) {
                    // We have a post code for the user

                    // Now lookup all other available post codes for all users (not including this one!)

                    // Get all users
                    ArrayList<User> allUsers = (ArrayList) userManagementFacade.getUsers();
                    ArrayList<User> journeysharers = new ArrayList<User>();
                    HashMap<String,Integer> distances = new HashMap<String,Integer>();

                    if (logger.isDebugEnabled()) {
                        logger.debug("Found " + allUsers.size() + " users in database");
                    }

                    for (User remoteUser: allUsers) {
                        if (remoteUser.getUsername().equals(localUserName)) {
                            // Don't compare user with him/herself!
                            continue;
                        }
                        String remotePostcode = remoteUser.getPostcode();
                        if (logger.isDebugEnabled()) {
                            logger.debug("User: " + remoteUser.getName() + "; post code: " + remotePostcode);
                        }

                        if (remotePostcode != null && !remotePostcode.equals("")) {
                            // Check if post code within specified distance
                            if (logger.isDebugEnabled()) {
                                logger.debug("Checking distance for: " + remoteUser.getName());
                            }
                            int distance = calcDistance(localPostcode, remotePostcode);
                            // Returns -1 if error calculating distance
                            if ( distance >= 0 && distance <= maxDistance ) {
                                    journeysharers.add(remoteUser);
                                    distances.put(remoteUser.getUsername(),distance);
                            }
                        }
                    }
                    if (journeysharers.isEmpty()){
                        mav = new ModelAndView(FIND_JOURNEY_SHARERS);
                        mav.addObject("message", noJourneySharersMsg);
                    } else {
                        // Successful search - show results
                        mav = new ModelAndView(REQ_JOURNEY_SHARERS);
                        mav.addObject("total",journeysharers.size());
                        mav.addObject("journeysharers",journeysharers);
                        mav.addObject("distances",distances);
                        
                        // Add empty command object
                        mav.addObject("journeySharersForm", new JourneySharersForm());
                    }
                } else {
                    // No postcode for this user so return error message
                    mav = new ModelAndView(FIND_JOURNEY_SHARERS);
                    mav.addObject("message", noPostcodeMsg);
                }

            } // end if getUserPrinciple not null

            return mav;

        } catch (URISyntaxException ue) {
            mav = new ModelAndView(FIND_JOURNEY_SHARERS);
            mav.addObject("message", systemErrorMsg);
            return mav;
        }
    }
    
    /*
     * Returns distance in interger kilometers or -1 if error condition
    */
    private int calcDistance ( String postcode1, String postcode2 ) throws URISyntaxException {
    	
    	// Tidy up postcodes
    	postcode1 = postcode1.trim();
        postcode2 = postcode2.trim();
        postcode1 = postcode1.toLowerCase();      
        postcode2 = postcode2.toLowerCase();
        // May have e.g. 'B1  4HZ'
        postcode1 = postcode1.replaceAll("\\s+", " ");
        postcode2 = postcode2.replaceAll("\\s+", " ");

        // Fetch relevant post code file(s) (may have bs12 8pt or b1 8pt - need first one or two letters)
        String[] matches1 = postcode1.split("\\d", 2);
        String postcode1FileName = matches1[0] + ".csv";
        String[] matches2 = postcode1.split("\\d", 2);
        String postcode2FileName = matches2[0] + ".csv";

        String northing1 = null;
        String northing2 = null;
        String easting1 = null;
        String easting2 = null;

        if (logger.isDebugEnabled()) {
            logger.debug("Fetching 1st post code file: " + postcodesPath + "/" + postcode1FileName);
        }

        try {
            FileReader fr = new FileReader(postcodesPath + "/" + postcode1FileName);
            BufferedReader br = new BufferedReader(fr);
            if (logger.isDebugEnabled()) {
                logger.debug("Looking for post code: " + postcode1);
            }
            String line;
            while ( (line=br.readLine()) != null ) {
                String[] fields = line.split(",");
                String postcode = fields[0];
                postcode = postcode.replaceAll("\\s+", " ");
            //    if (logger.isDebugEnabled()) {
            //        logger.debug("Considering post code: " + postcode);
            //    }
                if (postcode1.equalsIgnoreCase(postcode)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found post code");
                    }
                    // Got our 1st post code - get coords
                    easting1 = fields[1];
                    northing1 = fields[2];
                    break;
                }
            }
            br.close();
            fr.close();

            if (logger.isDebugEnabled()) {
                logger.debug("easting1: " + easting1 + "; northing1: " + northing1);
                logger.debug("Fetching 2nd post code file: " + postcodesPath + "/" + postcode2FileName);
            }

            fr = new FileReader(postcodesPath + "/" + postcode2FileName);
            br = new BufferedReader(fr);

            if (logger.isDebugEnabled()) {
                logger.debug("Looking for post code: " + postcode2);
            }
            while ( (line=br.readLine()) != null ) {
                String[] fields = line.split(",");
                String postcode = fields[0];
                postcode = postcode.replaceAll("\\s+", " ");
                if (postcode2.equalsIgnoreCase(postcode)) {
                    // Got our 2nd post code - get coords
                    easting2 = fields[1];
                    northing2 = fields[2];
                    break;
                }
            }
            br.close();
            fr.close();

            if (logger.isDebugEnabled()) {
                logger.debug("easting2: " + easting2 + "; northing2: " + northing2);
            }

        } catch (FileNotFoundException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error fetching file: " + ex.getMessage());
            }
            return -1;
        } catch (IOException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error fetching file: " + ex.getMessage());
            }
            return -1;
        }

        if (northing1 == null || northing2 == null || easting1 == null || easting2 == null ) {
            return -1;
        }

        // Calculate distances from coords
        try {
            int intNorthing1 = Integer.parseInt(northing1);
            int intNorthing2 = Integer.parseInt(northing2);
            int intEasting1 = Integer.parseInt(easting1);
            int intEasting2 = Integer.parseInt(easting2);

            int northingDiff = intNorthing1 - intNorthing2;
            int eastingDiff = intEasting1 - intEasting2;

            int hypotsquare = (northingDiff * northingDiff) + (eastingDiff * eastingDiff);
            double distance = Math.sqrt( (double)hypotsquare );

            if (logger.isDebugEnabled()) {
                logger.debug("Calculated distance: " + distance + " metres");
            }
            
            int intDistance = (int) distance/1000;
            
            return intDistance;

        } catch (NumberFormatException nfe) {
            return -1;
        }
    	
    }
}
