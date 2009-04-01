/*
 * Copyright (c) 2008, University of Bristol
 * Copyright (c) 2008, University of Manchester
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

package net.crew_vre.annotations.liveannotationtype.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.crew_vre.annotations.liveannotationtype.LiveAnnotationProperties;
import net.crew_vre.annotations.liveannotationtype.LiveAnnotationType;
import net.crew_vre.annotations.liveannotationtype.LiveAnnotationTypeRepository;

/**
 * An implementation of the LayoutRepository that uses an xml file
 *
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class LiveAnnotationTypeRepositoryXmlImpl implements
        LiveAnnotationTypeRepository {

    private HashMap<String, LiveAnnotationType> liveAnnotationTypes =
        new HashMap<String, LiveAnnotationType>();
    private List<String> liveAnnotationTypeNames = new Vector<String>();
    private LiveAnnotationProperties liveAnnotationProperties;

    /**
     * Creates a new RtpTypeRepository
     *
     * @param file
     *            The file containing the known types
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public LiveAnnotationTypeRepositoryXmlImpl(String file)
            throws SAXException, IOException, ParserConfigurationException {
        NodeList list = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(getClass().getResourceAsStream(file));
        liveAnnotationProperties = new LiveAnnotationProperties();
        if (document.getElementsByTagName("properties") != null) {
            list = document.getElementsByTagName("properties").item(0).getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    NamedNodeMap attributes = node.getAttributes();
                    if ((attributes.getNamedItem("width") != null)
                            && (attributes.getNamedItem("height") != null)) {
                        liveAnnotationProperties.setSize(node.getLocalName(),
                            Integer.valueOf(attributes.getNamedItem("width").getNodeValue()),
                            Integer.valueOf(attributes.getNamedItem("height").getNodeValue()));
                    } else if (node.getLocalName().equals("textColours")) {
                        NodeList colours = node.getChildNodes();
                        for (int j = 0; j < colours.getLength(); j++) {
                            Node colourNode = colours.item(j);
                            if ((colourNode.getNodeType() == Node.ELEMENT_NODE)
                                    && colourNode.getLocalName().equals("colour")) {
                                liveAnnotationProperties.addTextColour(
                                        colourNode.getAttributes().getNamedItem(
                                                "value").getNodeValue());
                            }
                        }
                    }
                }
            }
        } else {
            throw new RuntimeException("invalid LiveAnnotationTypeRepository");
        }
        list = document.getElementsByTagName("liveAnnotationType");
        for (int i = 0; i < list.getLength(); i++) {
            LiveAnnotationType liveAnnotationType = new LiveAnnotationType();
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap attributes = node.getAttributes();
                String name = null;
                if (attributes.getNamedItem("name") != null) {
                    name = attributes.getNamedItem("name").getNodeValue();
                }
                String type = name;
                if (attributes.getNamedItem("type") != null) {
                    type = attributes.getNamedItem("type").getNodeValue();
                    if (name == null) {
                        name = type;
                    }
                }
                if (attributes.getNamedItem("index") != null) {
                    String indexStr = attributes.getNamedItem("index").getNodeValue();
                    if (indexStr != null) {
                        long index = Long.parseLong(indexStr);
                        liveAnnotationType.setIndex(index);
                    }
                }
                if (type == null) {
                    throw new RuntimeException(
                            "invalid LiveAnnotationTypeRepository");
                }
                liveAnnotationType.setName(name);
                liveAnnotationType.setType(type);
                NodeList liveAnnNodes = node.getChildNodes();
                for (int j = 0; j < liveAnnNodes.getLength(); j++) {
                    Node latnode = liveAnnNodes.item(j);
                    if (latnode.getNodeType() == Node.ELEMENT_NODE) {
                        NamedNodeMap latAttributes = latnode.getAttributes();
                        if (latnode.getLocalName().equals("button")) {
                            String vis = "true";
                            if (latAttributes.getNamedItem("visible") != null) {
                                vis = latAttributes.getNamedItem("visible").getNodeValue();
                            }
                            if (latAttributes.getNamedItem("image") != null) {
                                liveAnnotationType.setButton(latAttributes.getNamedItem(
                                    "image").getNodeValue(), vis);
                            }
                        }
                        if (latnode.getLocalName().equals("thumbnail")) {
                            if (latAttributes.getNamedItem("name") != null) {
                                liveAnnotationType.setThumbnail(latAttributes.getNamedItem(
                                    "name").getNodeValue());
                            }
                        }
                        if (latnode.getLocalName().equals("colour")) {
                            if (latAttributes.getNamedItem("value") != null) {
                                liveAnnotationType.setColour(latAttributes.getNamedItem(
                                    "value").getNodeValue());
                            }
                        }
                        if (latnode.getLocalName().equals("contains")) {
                            String nameatt = null;
                            if (latAttributes.getNamedItem("name") != null) {
                                nameatt = latAttributes.getNamedItem("name")
                                        .getNodeValue();
                            } else {
                                throw new RuntimeException(
                                        "invalid LiveAnnotationTypeRepository");
                            }
                            for (int k = 0; k < latAttributes.getLength(); k++) {
                                if (latAttributes.item(k).getLocalName().equals("type")) {
                                    liveAnnotationType.setContains(nameatt,
                                            latAttributes.item(k).getNodeValue());
                                } else {
                                    if (!latAttributes.item(k).getLocalName()
                                            .equals("name")) {
                                        liveAnnotationType.setVariable(nameatt + "."
                                                + latAttributes.item(k).getLocalName(),
                                                latAttributes.item(k).getNodeValue());
                                    }
                                }
                            }
                        }
                        if (latnode.getLocalName().equals("format")) {
                            NodeList formatNodes = latnode.getChildNodes();
                            for (int k = 0; k < formatNodes.getLength(); k++) {
                                Node formNode = formatNodes.item(k);
                                if (formNode.getNodeType() == Node.ELEMENT_NODE) {
                                    liveAnnotationType.setFormat(formNode.getLocalName(),
                                        formNode.getTextContent());
                                }
                            }
                        }
                        if (latnode.getLocalName().equals("convertsTo")) {
                            NodeList formatNodes = latnode.getChildNodes();
                            for (int k = 0; k < formatNodes.getLength(); k++) {
                                Node formNode = formatNodes.item(k);
                                if (formNode.getNodeType() == Node.ELEMENT_NODE) {
                                    if (formNode.getLocalName().equals("type")) {
                                        if (formNode.getAttributes().getNamedItem("name") != null) {
                                            liveAnnotationType.addConversion(
                                                formNode.getAttributes().getNamedItem(
                                                    "name").getNodeValue());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            liveAnnotationTypes.put(liveAnnotationType.getName(), liveAnnotationType);
            liveAnnotationTypeNames.add(liveAnnotationType.getName());
        }
    }

    public LiveAnnotationType findLiveAnnotationType(String liveAnnotationTypeName) {
        return liveAnnotationTypes.get(liveAnnotationTypeName);
    }

    public LiveAnnotationProperties getProperties() {
        return liveAnnotationProperties;
    }

    public List<String> getLiveAnnotationTypes() {
        return liveAnnotationTypeNames;
    }

}
