package net.crew_vre.web;

import java.util.Map;

/**
 * <p>A utility class with common useful methods for dealing with facets.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: Utility.java 1132 2009-03-20 19:05:47Z cmmaj $
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
