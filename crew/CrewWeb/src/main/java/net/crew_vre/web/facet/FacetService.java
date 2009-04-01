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
package net.crew_vre.web.facet;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p> A service that creates facets with states and filters that represent those
 * states in SPARQL.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: FacetService.java 1191 2009-03-31 13:38:51Z cmmaj $
 */
public interface FacetService {
    /**
     * <p>We need to check the request parameters to see what is the current state of the
     * facets. To do this, we iterate over each of the facet configurations and find out
     * what value is used in the request parameter names. It checks if that value exists
     * in the request - if yes, checks the facet type and creates the appropriate
     * search filter.</p>
     *
     * @param facetConfigs the list of facet configurations
     * @param request      the HTTP request.
     * @return a list a search filter objects
     */
    List<SearchFilter> generateSearchFilters(List<Map<String, String>> facetConfigs,
                                             HttpServletRequest request);

    /**
     * <p>Generate a list of facets with states.</p>
     *
     * @param facetConfigs the list of facet configurations
     * @return a list of facets.
     */
    List<Facet> generateStates(List<Map<String, String>> facetConfigs);

    /**
     * <p>Generate a list of facets with states.</p>
     *
     * @param facetConfigs  the list of facet configurations
     * @param request       the http request.
     * @param searchFilters filters to constrain the facet states.
     * @return a list of facts.
     */
    List<Facet> generateStates(List<Map<String, String>> facetConfigs,
                               HttpServletRequest request,
                               List<SearchFilter> searchFilters);
}
