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
package net.crew_vre.web;

import java.util.Map;

/**
 * <p>A utility class with common useful methods for dealing with facets.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: Utility.java 1191 2009-03-31 13:38:51Z cmmaj $
 *
 **/
public class Utility {

    public Utility(Map<String, String> nsPrefixes) {
        this.nsPrefixes = nsPrefixes;
    }

    public String uriToParmeterValue(String uri) {
        for (String key : nsPrefixes.keySet()) {
            if (uri.startsWith(nsPrefixes.get(key))) {
                return uriToParmeterValue(key, nsPrefixes.get(key), uri);
            }
        }
        return uri;
    }


    /**
     * <p>URIs are used as parameter values to indicate the state of a facet. URIs make the request
     * URL too long. The prefixes are used to replace part of the URI to make the parameter
     * value shorter.</p>
     *
     * @param prefix    the prefix used by this facet.
     * @param base      the URI represented by the prefix.
     * @param uri       the URI representing a facet state.
     * @return a value that represents the URI of the facet state.
     */
    public String uriToParmeterValue(final String prefix, final String base, final String uri) {
        return prefix + ":" + uri.substring(base.length());
    }

    /**
     * <p>Converts the value that represents a URI back to the original URI value.</p>
     *
     * @param paramValue    the parameter value used to represent a URI.
     * @return the URI that reprsents a facet state.
     */
    public String parameterValueToUri(final String paramValue) {

        for (String key : nsPrefixes.keySet()) {
            if (paramValue.startsWith(key + ":")) {
                String[] splitVal = paramValue.split(":");
                return nsPrefixes.get(splitVal[0]) + splitVal[1];
            }
        }
        return paramValue;
    }

    private Map<String, String> nsPrefixes;
}
