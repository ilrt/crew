/**
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

package net.crew_vre.recordings.layout.impl;

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

import net.crew_vre.recordings.layout.Layout;
import net.crew_vre.recordings.layout.LayoutPosition;
import net.crew_vre.recordings.layout.LayoutRepository;

/**
 * An implementation of the LayoutRepository that uses an xml file
 *
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class LayoutRepositoryXmlImpl implements LayoutRepository {

    private HashMap<String, Layout> layouts = new HashMap<String, Layout>();

    /**
     * Creates a new RtpTypeRepository
     *
     * @param file
     *            The file containing the known types
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public LayoutRepositoryXmlImpl(String file) throws SAXException,
            IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(getClass().getResourceAsStream(file));
        NodeList list = document.getElementsByTagName("layout");
        for (int i = 0; i < list.getLength(); i++) {
            Layout layout = new Layout();
            Node node = list.item(i);
            NamedNodeMap attributes = node.getAttributes();
            layout.setName(attributes.getNamedItem("name").getNodeValue());
            NodeList streams = node.getChildNodes();
            List<LayoutPosition> layoutPostions = new Vector<LayoutPosition>();
            for (int j = 0; j < streams.getLength(); j++) {
                Node streamnode = streams.item(j);
                if ((streamnode.getNodeType() == Node.ELEMENT_NODE)
                        && streamnode.getLocalName().equals("element")) {
                    NamedNodeMap streamAttributes = streamnode.getAttributes();
                    LayoutPosition pos = new LayoutPosition();
                    pos.setName(streamAttributes.getNamedItem("name").getNodeValue());
                    pos.setX(Integer.parseInt(
                            streamAttributes.getNamedItem("x").getNodeValue()));
                    pos.setY(Integer.parseInt(
                            streamAttributes.getNamedItem("y").getNodeValue()));
                    pos.setWidth(Integer.parseInt(
                            streamAttributes.getNamedItem("width").getNodeValue()));
                    pos.setHeight(Integer.parseInt(
                            streamAttributes.getNamedItem("height").getNodeValue()));
                    if (streamAttributes.getNamedItem("assignable") != null) {
                        pos.setAssignable(Boolean.parseBoolean(
                                streamAttributes.getNamedItem("assignable").getNodeValue()));
                    }
                    if (streamAttributes.getNamedItem("has-changes") != null) {
                        pos.setChanges(Boolean.parseBoolean(
                                streamAttributes.getNamedItem("has-changes").getNodeValue()));
                    }
                    layoutPostions.add(pos);
                }
            }
            layout.setStreamPostions(layoutPostions);
            layouts.put(layout.getName(), layout);
        }
    }

    public Layout findLayout(String layoutName) {
        return layouts.get(layoutName);
    }

}
