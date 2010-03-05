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

package net.crew_vre.recorder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Parses the config file
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Config extends DefaultHandler {

    // The start of an end tag
    private static final String TAG_END_START = "</";

    // The end of a tag
    private static final String TAG_END = ">";

    // The start of a start tag
    private static final String TAG_START = "<";

    // The parameter name attribute
    private static final String NAME_ATTRIBUTE = "name";

    // The parameter tag
    private static final String PARAM_TAG = "param";

    // The not allowed error
    private static final String NOT_ALLOWED_ERROR =
        " not allowed at this point";

    // The service tag
    private static final String SERVICE_TAG = "service";

    // The xml parser to use
    private static final String XML_PARSER_IMPLEMENTATION =
        "org.apache.xerces.parsers.SAXParser";

    // The reader of the XML
    private XMLReader parser = null;

    // True if we are in the service tag in the document
    private boolean inService = false;

    // True if we are in the param tag in the document
    private boolean inParam = false;

    // The name of the current parameter
    private String currentParamName = null;

    // The value of the current parameter
    private String currentParamValue = null;

    // The parameters of the current service
    private HashMap<String, Vector<String>> currentParameters = null;

    /**
     * Creates a new empty configuration
     */
    public Config() {
        currentParameters = new HashMap<String, Vector<String>>();
    }

    /**
     * Parses a config file
     * @param file The config file
     * @throws SAXException
     * @throws IOException
     */
    public Config(String file) throws SAXException,
            IOException {
        FileReader reader = new FileReader(file);
        InputSource source = new InputSource(reader);
        parser = XMLReaderFactory.createXMLReader(XML_PARSER_IMPLEMENTATION);
        parser.setContentHandler(this);
        parser.parse(source);
    }

    /**
     * Parses a config input stream
     * @param inputStream The input stream
     * @throws SAXException
     * @throws IOException
     */
    public Config(InputStream inputStream) throws SAXException, IOException {
        InputSource source = new InputSource(inputStream);
        parser = XMLReaderFactory.createXMLReader(XML_PARSER_IMPLEMENTATION);
        parser.setContentHandler(this);
        parser.parse(source);
    }

    /**
     *
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *     java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String namespaceURI, String localName,
                String qualifiedName, Attributes atts) throws SAXException {
        if (!inService) {
            if (localName.equals(SERVICE_TAG)) {
                inService = true;
                currentParameters = new HashMap<String, Vector<String>>();
            } else {
                throw new SAXException(localName
                        + NOT_ALLOWED_ERROR);
            }
        } else if (inService) {
            if (localName.equals(PARAM_TAG)) {
                currentParamName = atts.getValue("", NAME_ATTRIBUTE);
                if (currentParamName == null) {
                    throw new SAXException("Required Attribute "
                            + NAME_ATTRIBUTE
                            + " missing from parameter");
                }
                inParam = true;
                currentParamValue = "";
            } else {
                throw new SAXException(localName + NOT_ALLOWED_ERROR);
            }
        } else {
            throw new SAXException(localName + NOT_ALLOWED_ERROR);
        }
    }

    /**
     *
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     *     java.lang.String, java.lang.String)
     */
    public void endElement(String namespaceURI, String localName,
            String qualifiedName) throws SAXException {
        if (inService && localName.equals(SERVICE_TAG)) {
            inService = false;
        } else if (inParam && localName.equals(PARAM_TAG)) {
            if (currentParameters.containsKey(currentParamName)) {
                Vector<String> values = currentParameters.get(
                        currentParamName);
                values.add(currentParamValue);
                currentParameters.put(currentParamName, values);
            } else {
                Vector<String> values = new Vector<String>();
                values.add(currentParamValue);
                currentParameters.put(currentParamName, values);
            }
            inParam = false;
        } else {
            throw new SAXException(localName
                    + " ended but did not start");
        }
    }

    /**
     *
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int offset, int length) {
        if (inParam) {
            currentParamValue += new String(ch, offset, length);
        }
    }

    /**
     * Gets a loaded parameter
     * @param name The name of the parameter
     * @param def The default value
     * @return The parameter, or the default value if not specified
     */
    public String getParameter(String name, String def) {
        Vector<String> values = currentParameters.get(name);
        if (values == null) {
            return def;
        }
        return values.get(0);
    }

    /**
     * Gets all the parameters with the given name
     * @param name The name of the parameter
     * @return An array of strings (zero length if parameter doesn't exist)
     */
    public String[] getParameters(String name) {
        Vector<String> values = currentParameters.get(name);
        if (values == null) {
            return new String[0];
        }
        return values.toArray(new String[0]);
    }

    /**
     * Returns an integer parameter
     * @param name The name of the parameter
     * @param def The default value
     * @return The parameter value as an integer
     */
    public int getIntegerParameter(String name, int def) {
        return Integer.parseInt(getParameter(name, String.valueOf(def)));
    }

    /**
     * Sets a parameter
     * @param name The name of the parameter
     * @param value The value of a parameter
     */
    public void setParameter(String name, String value) {
        Vector<String> values = new Vector<String>();
        values.add(value);
        currentParameters.put(name, values);
    }

    /**
     * Sets a set of parameters
     * @param name The name of the parameters
     * @param values The values to assign
     */
    public void setParameters(String name, Vector<String> values) {
        currentParameters.put(name, values);
    }

    /**
     * Gets a map of parameters in the configuration (only one per key)
     * @return The map of parameters
     */
    public Map<String, String> getConfigMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        Iterator<String> iter = currentParameters.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            Vector<String> values = currentParameters.get(key);
            map.put(key, values.get(0));
        }
        return map;
    }

    /**
     * Saves the parameters to a file
     * @param filename The name of the file
     * @throws IOException
     */
    public void saveParameters(String filename) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(filename));
        Iterator<String> iterator = currentParameters.keySet().iterator();
        writer.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        writer.println(TAG_START + SERVICE_TAG + TAG_END);
        while (iterator.hasNext()) {
            String name = iterator.next();
            Vector<String> values = currentParameters.get(name);
            for (int i = 0; i < values.size(); i++) {
                String value = values.get(i);
                writer.print(TAG_START + PARAM_TAG + " " + NAME_ATTRIBUTE
                        + "=\"" + name + "\"" + TAG_END);
                writer.print(value);
                writer.println(TAG_END_START + PARAM_TAG + TAG_END);
            }
        }
        writer.println(TAG_END_START + SERVICE_TAG + TAG_END);
        writer.close();
    }
}
