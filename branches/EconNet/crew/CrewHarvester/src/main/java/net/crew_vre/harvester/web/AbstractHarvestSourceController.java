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
package net.crew_vre.harvester.web;

import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public abstract class AbstractHarvestSourceController extends SimpleFormController {

    protected List<HarvestSourceAuthority> findAuthorities(String graph, HttpServletRequest request) {

        // hold a list of relevant keys
        List<String> keys = new ArrayList<String>();

        List<HarvestSourceAuthority> authorities = new ArrayList<HarvestSourceAuthority>();

        // find the authority keys
        Enumeration e = request.getParameterNames();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            if (key.startsWith(AUTHORITY_PREFIX)) {
                keys.add(key);
            }
        }

        // create HarvestSourceAuthority objects
        for (String key : keys) {

            // calculate the authority from the key
            String authority = key.substring(AUTHORITY_PREFIX.length(), key.length());

            HarvestSourceAuthority harvestSourceAuthority = new HarvestSourceAuthority(graph,
                    authority, request.getParameterValues(key));
            authorities.add(harvestSourceAuthority);
        }

        return authorities;

    }

    private final String AUTHORITY_PREFIX = "AUTHORITY_";
}
