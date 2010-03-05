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

package net.crew_vre.media.rtptype.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.crew_vre.media.rtptype.RTPType;
import net.crew_vre.media.rtptype.RtpTypeRepository;

/**
 * An implementation of the RtpTypeRepository that uses an xml file
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class RtpTypeRepositoryXmlImpl implements RtpTypeRepository {

    private HashMap<Integer, RTPType> types = new HashMap<Integer, RTPType>();

    /**
     * Creates a new RtpTypeRepository
     * @param file The file containing the known types
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public RtpTypeRepositoryXmlImpl(String file) throws SAXException,
            IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(getClass().getResourceAsStream(file));
        NodeList list = document.getElementsByTagName("rtptype");
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            NamedNodeMap attributes = node.getAttributes();
            RTPType type = new RTPType();
            type.setId(Integer.parseInt(
                    attributes.getNamedItem("id").getNodeValue()));
            String mediaType = attributes.getNamedItem(
                    "mediaType").getNodeValue();
            type.setMediaType(mediaType);
            if (mediaType.equals("audio")) {
                Node encoding = attributes.getNamedItem("encoding");
                Node sampleRate = attributes.getNamedItem("sampleRate");
                Node sampleSize = attributes.getNamedItem("sampleSize");
                Node frameRate = attributes.getNamedItem("frameRate");
                Node frameSize = attributes.getNamedItem("frameSize");
                Node endian = attributes.getNamedItem("bigendian");
                Node signed = attributes.getNamedItem("signed");
                Node channels = attributes.getNamedItem("channels");

                if (encoding != null) {
                    float rate = -1;
                    if (sampleRate != null) {
                        rate = Float.parseFloat(sampleRate.getNodeValue());
                    }
                    int sampsize = -1;
                    if (sampleSize != null) {
                        sampsize = Integer.parseInt(sampleSize.getNodeValue());
                    }
                    float frate = -1;
                    if (frameRate != null) {
                        frate = Float.parseFloat(frameRate.getNodeValue());
                    }
                    int frsize = -1;
                    if (frameSize != null) {
                        frsize = Integer.parseInt(frameSize.getNodeValue());
                    }
                    int end = -1;
                    if (endian != null) {
                        end = Integer.parseInt(endian.getNodeValue());
                    }
                    int sig = -1;
                    if (signed != null) {
                        sig = Integer.parseInt(signed.getNodeValue());
                    }
                    int chs = -1;
                    if (channels != null) {
                        chs = Integer.parseInt(channels.getNodeValue());
                    }
                    type.setFormat(new AudioFormat(encoding.getNodeValue(),
                            rate, sampsize, chs, end, sig, frsize, frate,
                            Format.byteArray));
                }
            } else if (mediaType.equals("video")) {
                Node encoding = attributes.getNamedItem("encoding");
                if (encoding != null) {
                    type.setFormat(new VideoFormat(encoding.getNodeValue()));
                }
            }

            types.put(type.getId(), type);
        }
    }

    /**
     *
     * @see net.crew_vre.media.rtptype.RtpTypeRepository#findRtpType(int)
     */
    public RTPType findRtpType(int rtptype) {
        return types.get(rtptype);
    }

    /**
     * @see net.crew_vre.media.rtptype.RtpTypeRepository#findRtpTypes()
     */
    public List<RTPType> findRtpTypes() {
        return new Vector<RTPType>(types.values());
    }

}
