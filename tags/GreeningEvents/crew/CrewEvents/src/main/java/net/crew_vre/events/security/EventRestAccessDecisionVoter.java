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
package net.crew_vre.events.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.crew_vre.authorization.GateKeeper;

import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.intercept.web.FilterInvocation;
import org.springframework.security.vote.AccessDecisionVoter;

/**
 *
 */
public class EventRestAccessDecisionVoter implements AccessDecisionVoter {

    private GateKeeper gateKeeper = null;

    private Pattern contextPattern = null;

    /**
     * Creates a new EventRestAccessDecisionVoter
     *
     * @param gateKeeper The gateKeeper
     * @param context The context to be protected
     */
    public EventRestAccessDecisionVoter(GateKeeper gateKeeper, String context) {
        this.gateKeeper = gateKeeper;
        this.contextPattern = Pattern.compile("^.*/" + context + "/(.*)$");
    }

    /**
     *
     * @see org.springframework.security.vote.AccessDecisionVoter#supports(
     * org.springframework.security.ConfigAttribute)
     */
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    /**
     *
     * @see org.springframework.security.vote.AccessDecisionVoter#supports(
     *     java.lang.Class)
     */
    public boolean supports(Class aClass) {
        return (FilterInvocation.class.isAssignableFrom(aClass));
    }

    /**
     *
     * @see org.springframework.security.vote.AccessDecisionVoter#vote(
     *     org.springframework.security.Authentication, java.lang.Object,
     *     org.springframework.security.ConfigAttributeDefinition)
     */
    public int vote(Authentication authentication, Object object,
            ConfigAttributeDefinition config) {
        HttpServletRequest request =
            ((FilterInvocation) object).getHttpRequest();

        String method = request.getMethod();

        String path = request.getRequestURI();

        if (path == null) {
            return ACCESS_ABSTAIN;
        };

        Matcher matcher = contextPattern.matcher(path);
        if (matcher.find()) {

            // TODO: Only allow post access to events if they have the right role
            return ACCESS_GRANTED;
        }
        return ACCESS_ABSTAIN;
    }

}
