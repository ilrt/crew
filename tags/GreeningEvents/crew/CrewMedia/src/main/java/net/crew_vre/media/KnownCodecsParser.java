package net.crew_vre.media;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Parser for a knownCodecs.xml file
 * Format:
 *     <codecs>
 *         <codec>
 *             <class>name-of-class</class>
 *             <nativeLib>name-of-required-native-library</nativeLib>
 *             ...
 *
 *         </codec>
 *         ...
 *
 *     </codecs>
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class KnownCodecsParser extends DefaultHandler {

    // The xml parser to use
    private static final String XML_PARSER_IMPLEMENTATION =
        "org.apache.xerces.parsers.SAXParser";

    // The reader of the XML
    private XMLReader parser = null;

    private boolean inCodecs = false;

    private boolean inCodec = false;

    private boolean inClass = false;

    private boolean inNativeLib = false;

    private String chars = "";

    private Vector<String> knownCodecs = new Vector<String>();

    private Vector<String> requiredLibraries = new Vector<String>();

    /**
     * Creates a new KnownCodecsParser
     * @param file The file to parse
     * @throws SAXException
     * @throws IOException
     */
    public KnownCodecsParser(String file) throws SAXException, IOException {
        InputStream inputStream = getClass().getResourceAsStream(file);
        InputSource source = new InputSource(inputStream);
        parser = XMLReaderFactory.createXMLReader(XML_PARSER_IMPLEMENTATION);
        parser.setContentHandler(this);
        parser.parse(source);
    }

    /**
     *
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     *     java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        chars = "";
        if (!inCodecs) {
            if (localName.equals("codecs")) {
                inCodecs = true;
            } else {
                throw new SAXException(localName + " not allowed here");
            }
        } else if (!inCodec) {
            if (localName.equals("codec")) {
                inCodec = true;
            } else {
                throw new SAXException(localName + " not allowed here");
            }
        } else if (!inClass && !inNativeLib) {
            if (localName.equals("class")) {
                inClass = true;
            } else if (localName.equals("nativeLib")) {
                inNativeLib = true;
            } else {
                throw new SAXException(localName + " not allowed here");
            }
        } else {
            throw new SAXException(localName + " not allowed here");
        }
    }

    /**
     *
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length) {
        chars += new String(ch, start, length);
    }

    /**
     *
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     *     java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        if (inClass && localName.equals("class")) {
            if (!knownCodecs.contains(chars)) {
                knownCodecs.add(chars);
            }
            inClass = false;
        } else if (inNativeLib && localName.equals("nativeLib")) {
            if (!requiredLibraries.contains(chars)) {
                requiredLibraries.add(chars);
            }
            inNativeLib = false;
        } else if (inCodec && localName.equals("codec")) {
            inCodec = false;
        } else if (inCodecs && localName.equals("codecs")) {
            inCodecs = false;
        } else {
            throw new SAXException(localName + " not allowed here");
        }
        chars = "";
    }

    /**
     * Gets the known codecs
     * @return The known codecs
     */
    public Vector<String> getCodecs() {
        return knownCodecs;
    }

    /**
     * Gets the required libraries
     * @return The required libraries
     */
    public Vector<String> getLibraries() {
        return requiredLibraries;
    }
}
