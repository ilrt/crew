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

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import org.ilrt.green_repository.RepositoryEventManagementFacade;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ilrt.green_repository.domain.RepositoryEventKml;

/**
 *
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 */

public class LocalEventsKmlExportController implements Controller {

    private Logger logger = Logger.getLogger("org.ilrt.green_repository.web.LocalEventsKmlExportController");

    private RepositoryEventManagementFacade repositoryFacade;

    public LocalEventsKmlExportController(RepositoryEventManagementFacade repositoryFacade) {
        this.repositoryFacade = repositoryFacade;
    }


    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

       // String kmlId = (String)request.getParameter("id");
        
        // Request now takes the form /repository/route_<kmlID>.kml
        String servletName = request.getServletPath();
        String kmlId = servletName.substring(servletName.indexOf("KML_"), servletName.indexOf("."));


       if (logger.isDebugEnabled()) {
           logger.debug("Got request for KML with id: " + kmlId);
       }

       response.setContentType("application/vnd.google-earth.kml+xml");
       
       PrintWriter writer = response.getWriter();
       if (kmlId != null && !kmlId.equals("")) {
           // kmlId may be in the form of a URI, from which the id will need extracting
           if (kmlId.startsWith("http")) {
               kmlId = kmlId.substring(kmlId.indexOf("KML_"));
           }
           RepositoryEventKml kml = repositoryFacade.getRepositoryEventKmlObject(kmlId);
           if (kml != null) {
               writer.print(kml.getXml());
           } else {
               // Return empty KML object
               writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
               writer.println("<kml xmlns=\"http://earth.google.com/kml/2.0\">");
               writer.println("<Document/>");
               writer.println("</kml>");
           }
       } else {
           // Return empty KML object
           writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
           writer.println("<kml xmlns=\"http://earth.google.com/kml/2.0\">");
           writer.println("<Document/>");
           writer.println("</kml>");
       }

       return null;
    }


}
