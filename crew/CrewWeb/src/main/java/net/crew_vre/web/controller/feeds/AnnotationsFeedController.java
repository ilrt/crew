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
package net.crew_vre.web.controller.feeds;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.crew_vre.web.feed.AnnotationFeedWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 *
 **/
public class AnnotationsFeedController implements Controller {


    private Map<String, String> config;
    private static final String COMMENTS = "feeds/comments.xml";

    public AnnotationsFeedController(final Map<String, String> config) {
        this.config = config;
    }

    public ModelAndView handleRequest(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        // get the request url
        String requestUrl = request.getRequestURI();

        // get event id
        String eventId = request.getParameter("eventId");

        // create the base URI
        String baseUrl = requestUrl.substring(0, requestUrl.length() - COMMENTS.length());

        // Get a List of annotations using eventId; parse response and insert
        // into a List of Annotation objects

        // Generate a request to Coboto
        String annotationsRequest = baseUrl + "annotation/about?id=" + eventId;
        String annotationsResponse = fetchUrl(annotationsRequest);

        if (logger.isDebugEnabled()) {
                logger.debug("Get annotations response: \\n\\n" + annotationsResponse);
        }

        List<HashMap<String,String>> annotations = new ArrayList<HashMap<String,String>>();
        if (annotationsResponse != null && !annotationsResponse.equals("")) {
            // Parse response
            annotations = parseAnnotations(annotations,annotationsResponse);
        }

        // write the feed
        response.setContentType(config.get("contentType"));

        AnnotationFeedWriter annotationFeedWriter = new AnnotationFeedWriter();
        try {
            annotationFeedWriter.write(response.getWriter(), annotations,
                    baseUrl, requestUrl, config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String fetchUrl (String urlRequest) {
        BufferedReader in = null;
        StringBuffer content = new StringBuffer();
        try {
            URL url = new URL(urlRequest);
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line=null;
            while ((line=in.readLine()) != null)
              content.append(line);
          }
          catch (MalformedURLException ex) {
            return "";
          }
          catch (FileNotFoundException ex) {
            return "";
          }
          catch (IOException ex) {
            return "";
          }
          if (in != null)
            try {in.close();} catch (IOException ex) {return "";}

          return content.toString();
    }

    private List<HashMap<String,String>> parseAnnotations (List<HashMap<String,String>> annotations, String rdf) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(rdf);
            doc.getDocumentElement().normalize();

            // Get comments
            NodeList comments = doc.getElementsByTagNameNS("*","SimpleComment");

            for (int i = 0; i < comments.getLength(); i++) {
                HashMap<String,String> annotation = new HashMap<String,String>();
                Node commentNode = comments.item(i);
                if (commentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element commentElement = (Element) commentNode;

                    // Get annotation URI
                    String annotationUrl = commentElement.getAttribute("about");
                    annotation.put("annotationUrl", annotationUrl);
                    if (logger.isDebugEnabled()) {
                            logger.debug("annotation url: " + annotationUrl);
                    }

                    // Get created dateTime
                    NodeList createdNodeList = commentElement.getElementsByTagNameNS("*","created");
                    Element createdElement = (Element) createdNodeList.item(0);
                    NodeList createdDateList = createdElement.getChildNodes();
                    String createdDateTime = ((Node)createdDateList.item(0)).getNodeValue();
                    annotation.put("createdDateTime", createdDateTime);
                    if (logger.isDebugEnabled()) {
                            logger.debug("createdDateTime: " + createdDateTime);
                    }

                    // Get author URI
                    NodeList authorNodeList = commentElement.getElementsByTagNameNS("*","author");
                    Element authorElement = (Element) authorNodeList.item(0);
                    NodeList authorList = authorElement.getChildNodes();
                    String authorUri = ((Node)authorList.item(0)).getNodeValue();
                    annotation.put("authorUri", authorUri);
                    if (logger.isDebugEnabled()) {
                            logger.debug("authorUri: " + authorUri);
                    }

                    // Get title
                    NodeList titleNodeList = commentElement.getElementsByTagNameNS("*", "title");
                    Element titleElement = (Element)titleNodeList.item(0);
                    NodeList titleList = titleElement.getChildNodes();
                    String commentTitle = ((Node)titleList.item(0)).getNodeValue();
                    annotation.put("commentTitle", commentTitle);
                    if (logger.isDebugEnabled()) {
                            logger.debug("commentTitle: " + commentTitle);
                    }

                    // Get comment description
                    NodeList descriptionNodeList = commentElement.getElementsByTagNameNS("*", "description");
                    Element descriptionElement = (Element)descriptionNodeList.item(0);
                    NodeList descList = descriptionElement.getChildNodes();
                    String commentDescription = ((Node)descList.item(0)).getNodeValue();
                    annotation.put("commentDescription", commentDescription);
                    if (logger.isDebugEnabled()) {
                            logger.debug("commentDescription: " + commentDescription);
                    }
                }
                annotations.add(annotation);
            }
        } catch (Exception e) {
            return null;
        }
        return annotations;
    }
    private Logger logger = Logger.getLogger("net.crew_vre.web.controller.feeds.AnnotationsFeedController");
}
