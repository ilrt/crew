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

import java.io.IOException;
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
import org.xml.sax.SAXException;

/**
 *
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 *
 **/
public class AnnotationsFeedController implements Controller {


    private Map<String, String> config;
    private static final String COMMENTS = "feeds/comments.do";

    public AnnotationsFeedController(final Map<String, String> config) {
        this.config = config;
    }

    public ModelAndView handleRequest(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        // get the request url
        String requestUrl = request.getRequestURL().toString();
        if (logger.isDebugEnabled())
                logger.debug("requestUrl: " + requestUrl);

        // get event id
        String eventId = request.getParameter("eventId");

        // create the base URI
        String baseUrl = requestUrl.substring(0, requestUrl.length() - COMMENTS.length());
        if (logger.isDebugEnabled())
                logger.debug("baseUrl: " + baseUrl);

        // Get a List of annotations using eventId; parse response and insert
        // into a List of Annotation objects

        // Generate a request to Coboto
        String annotationsRequest = baseUrl + "annotation/about?id=" + eventId;
        if (logger.isDebugEnabled())
                logger.debug("Requesting annotations from: " + annotationsRequest);

        List<HashMap<String,String>> annotations = new ArrayList<HashMap<String,String>>();
        annotations = parseAnnotations(annotations,annotationsRequest);

        if (logger.isDebugEnabled() && annotations != null)
                logger.debug("Found: " + annotations.size() + " annotations");

        // write the feed
        response.setContentType(config.get("contentType"));

        AnnotationFeedWriter annotationFeedWriter = new AnnotationFeedWriter();
        try {
            annotationFeedWriter.write(response.getWriter(), eventId, annotations,
                    baseUrl, requestUrl, config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<HashMap<String,String>> parseAnnotations (List<HashMap<String,String>> annotations, String URI)
        throws Exception {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            if (logger.isDebugEnabled())
                    logger.debug("Fetching feed from: " + URI);
            Document doc = db.parse(URI);

            Element root = doc.getDocumentElement();

            if (logger.isDebugEnabled())
                    logger.debug("Have root element: " + root.getTagName());

            // Get comments
            NodeList comments = doc.getElementsByTagName("caboto:SimpleComment");

            if (logger.isDebugEnabled())
                    logger.debug("Found: " + comments.getLength() + " SimpleComments");

            for (int i = 0; i < comments.getLength(); i++) {
                HashMap<String,String> annotation = new HashMap<String,String>();
                Node commentNode = comments.item(i);
                if (commentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element commentElement = (Element) commentNode;

                    // Get annotation URI
                    String annotationUrl = commentElement.getAttribute("rdf:about");
                    annotation.put("annotationUrl", annotationUrl);
                    if (logger.isDebugEnabled()) {
                            logger.debug("annotation url: " + annotationUrl);
                    }

                    // Get created dateTime
                    NodeList createdNodeList = commentElement.getElementsByTagName("annotea:created");
                    Element createdElement = (Element) createdNodeList.item(0);
                    NodeList createdDateList = createdElement.getChildNodes();
                    String createdDateTime = ((Node)createdDateList.item(0)).getNodeValue();
                    annotation.put("createdDateTime", createdDateTime);
                    if (logger.isDebugEnabled()) {
                            logger.debug("createdDateTime: " + createdDateTime);
                    }

                    // Get author URI
                    NodeList authorNodeList = commentElement.getElementsByTagName("annotea:author");
                    Element authorElement = (Element) authorNodeList.item(0);
                    String authorUri = authorElement.getAttribute("rdf:resource");
                    annotation.put("authorUri", authorUri);
                    if (logger.isDebugEnabled()) {
                            logger.debug("authorUri: " + authorUri);
                    }

                    // Get title
                    NodeList titleNodeList = commentElement.getElementsByTagName("dc:title");
                    Element titleElement = (Element)titleNodeList.item(0);
                    NodeList titleList = titleElement.getChildNodes();
                    String commentTitle = ((Node)titleList.item(0)).getNodeValue();
                    annotation.put("commentTitle", commentTitle);
                    if (logger.isDebugEnabled()) {
                            logger.debug("commentTitle: " + commentTitle);
                    }

                    // Get comment description
                    NodeList descriptionNodeList = commentElement.getElementsByTagName("dc:description");
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

            // If any exceptions, just return current annotations list 
        } catch (IOException ioe) {
            // throw new Exception(ioe.getClass().toString() + ": " + ioe.getMessage());
            return annotations;
        } catch (SAXException se) {
            // throw new Exception(se.getClass().toString() + ": " + se.getMessage());
            return annotations;
        } catch (Exception e) {
            // e.printStackTrace();
            // throw new Exception(e.getClass().toString() + ": " + e.getMessage());
            return annotations;
        }
        return annotations;
    }
    private Logger logger = Logger.getLogger("net.crew_vre.web.controller.feeds.AnnotationsFeedController");
}
