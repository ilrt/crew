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
package net.crew_vre.rest;

import net.crew_vre.rest.providers.RDFFileProvider;

import org.caboto.rest.providers.JenaModelRdfProvider;
import org.caboto.rest.providers.JenaResourceRdfProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * Rest Client utilities
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ClientUtils {

    private ClientUtils() {
        // Does Nothing
    }

    /**
     * Sends a REST POST request
     * @param authentication The authentication or null if none
     * @param uri The uri to connect to
     * @param type The type of the request
     * @param postData The data to post
     * @return The response to the request
     */
    public static ClientResponse post(AuthenticationFilter authentication,
            String uri, String type, Object postData) {

        Client c = Client.create(createClientConfig());

        if (authentication != null) {
            c.addFilter(authentication);
        }

        return c.resource(uri).type(type).post(ClientResponse.class, postData);
    }

    /**
     * Sends a REST GET request
     * @param authentication The authentication or null if none
     * @param uri The uri to get
     * @param type The type of the response
     * @return The response to the request
     */
    public static ClientResponse get(AuthenticationFilter authentication,
            String uri, String type) {

        Client c = Client.create(createClientConfig());

        if (authentication != null) {
            c.addFilter(authentication);
        }

        return c.resource(uri).accept(type).get(ClientResponse.class);
    }

    private static ClientConfig createClientConfig() {
        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JenaResourceRdfProvider.class);
        config.getClasses().add(JenaModelRdfProvider.class);
        config.getClasses().add(RDFFileProvider.class);
        return config;
    }
}
