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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Base64;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;

/**
 * A filter that performs HTTP Basic authentication
 * @author Andrew G D Rowley
 * @version 1.0
 */
public abstract class BasicAuthenticationFilter extends AuthenticationFilter {

    private boolean lastFailed = false;

    private String lastUsername = null;

    private String lastPassword = null;

    private boolean forceAuthorization = false;

    /**
     * Sets whether authorization should be assumed to be required
     * @param forceAuthorization true to force authorization before checking
     */
    public void setForceAuthorization(boolean forceAuthorization) {
        this.forceAuthorization = forceAuthorization;
    }

    /**
     *
     * @see com.sun.jersey.api.client.filter.ClientFilter#handle(
     *     com.sun.jersey.api.client.ClientRequest)
     */
    public ClientResponse handle(ClientRequest clientRequest) {
        int count = 0;
        ClientResponse response = null;
        while ((lastFailed || (count == 0)) && (count < getMaxTries())) {
            String username = lastUsername;
            String password = lastPassword;
            if (lastFailed || ((count == 0) && forceAuthorization)) {
                username = getUsername();
                if (wasCancelled()) {
                    throw new ClientHandlerException("User cancelled");
                }
                password = getPassword();
                if (wasCancelled()) {
                    throw new ClientHandlerException("User cancelled");
                }
            }
            count += 1;
            lastFailed = false;
            if (username != null || password != null) {

                // encode the password
                byte[] encoded = Base64.encodeBase64(
                        (username + ":" + password).getBytes());

                // add the header
                List<Object> headerValue = new ArrayList<Object>();
                headerValue.add("Basic " + new String(encoded));
                clientRequest.getMetadata().put("Authorization", headerValue);
            }

            response = getNext().handle(clientRequest);
            if (response.getResponseStatus().equals(Status.UNAUTHORIZED)) {
                lastFailed = true;
            } else {
                return response;
            }
        }
        return response;
    }

    /**
     * Gets the username
     * @return The username to use or null if none
     */
    public abstract String getUsername();

    /**
     * Gets the password
     * @return The password to use or null if none
     */
    public abstract String getPassword();

    /**
     * Determines if the user cancelled when asked for a username or password
     * @return True if the user cancelled,
     *         false otherwise (or if not applicable)
     */
    public abstract boolean wasCancelled();

    /**
     * Gets the number of retry attempts before failure
     * @return The number of retry attempts
     */
    public abstract int getMaxTries();
}
