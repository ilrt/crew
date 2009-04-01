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


package net.crew_vre.media;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import javax.media.Codec;
import javax.media.Format;
import javax.media.PlugInManager;
import javax.media.ResourceUnavailableException;

import net.crew_vre.nativeloader.NativeClassLoader;

import org.xml.sax.SAXException;

/**
 * Miscellaneous utility functions
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Misc {

    private static final Vector<String> KNOWN_CODECS = new Vector<String>();

    private static final HashMap<String, Class< ? >> codecClasses =
        new HashMap<String, Class< ? >>();

    private static boolean codecsConfigured = false;

    private Misc() {
        // Does Nothing
    }

    /**
     * Determines if anyone has called configureCodecs yet
     * @return True if the codecs have been configured, false otherwise
     */
    public static boolean isCodecsConfigured() {
        return codecsConfigured;
    }

    /**
     * Configures the codecs from a configuration file
     * @param codecConfigFile The file listing the codecs and jars,
     *      one per line jar separated from codec by colon e.g. jar:codecClass
     * @throws IOException
     * @throws SAXException
     * @throws ResourceUnavailableException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public static void configureCodecs(String codecConfigFile)
            throws IOException, SAXException, ResourceUnavailableException,
            ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        codecsConfigured = true;
        KNOWN_CODECS.clear();
        PlugInManager.setPlugInList(KNOWN_CODECS, PlugInManager.CODEC);
        KnownCodecsParser parser = new KnownCodecsParser(codecConfigFile);
        for (String codec : parser.getCodecs()) {
            addCodec(codec);
        }
    }

    /**
     * Adds a codec
     * @param codecClassName The class of the codec
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void addCodec(String codecClassName)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        Codec codec = loadCodec(codecClassName);
        PlugInManager.addPlugIn(codec.getClass().getCanonicalName(),
                codec.getSupportedInputFormats(),
                codec.getSupportedOutputFormats(null),
                PlugInManager.CODEC);
    }

    /**
     * Loads a codec
     * @param className The name of the codec class
     * @return The codec or null if there is an error
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Codec loadCodec(String className)
           throws ClassNotFoundException, InstantiationException,
           IllegalAccessException {
        Class< ? > codecClass = null;
        if (codecClasses.containsKey(className)) {
            codecClass = codecClasses.get(className);
        } else {
            NativeClassLoader loader = new NativeClassLoader(
                    className.substring(0, className.lastIndexOf('.')));
            codecClass = Class.forName(className, true, loader);
            codecClasses.put(className, codecClass);
        }
        Codec codec = (Codec) codecClass.newInstance();
        return codec;
    }

    /**
     * Tests a set of codecs
     * @param inputFormat The input format
     * @param codecs The codecs to test
     * @param outputFormat The output format
     * @return True if the codecs work, false otherwise
     */
    public static boolean joinCodecs(Format inputFormat,
            LinkedList<String> codecs, Format outputFormat) {
        if (codecs.isEmpty()) {
            if (inputFormat.matches(outputFormat)) {
                return true;
            }
            return false;
        }

        try {
            String codecClass = codecs.removeFirst();
            Codec codec = Misc.loadCodec(codecClass);
            Format input = codec.setInputFormat(inputFormat);
            if (input == null) {
                throw new Exception("Cannot set codec " + codecClass
                        + " input to " + inputFormat);
            }
            Format[] outputs = codec.getSupportedOutputFormats(input);
            for (int j = 0; j < outputs.length; j++) {
                Format output = codec.setOutputFormat(outputs[j]);
                boolean outputFound = joinCodecs(output, codecs, outputFormat);
                if (outputFound) {
                    codec.open();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
