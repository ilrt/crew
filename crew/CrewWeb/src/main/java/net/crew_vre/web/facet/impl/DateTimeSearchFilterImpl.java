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
package net.crew_vre.web.facet.impl;

import net.crew_vre.web.facet.SearchFilter;

/**
 * Represents a filter for date facets.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: DateTimeSearchFilterImpl.java 1191 2009-03-31 13:38:51Z cmmaj $
 */
public class DateTimeSearchFilterImpl implements SearchFilter {

    // ---------- CONSTRUCTORS

    /**
     * A constructor to create a filter with a constraint.
     *
     * @param paramName    the parameter name of the facet constraint.
     * @param linkProperty the rdf link property.
     * @param constraint   the constraint used in the filter.
     */
    public DateTimeSearchFilterImpl(final String paramName, final String linkProperty,
                                    final String constraint) {
        this.linkProperty = linkProperty;
        this.constraint = constraint;
        this.paramName = paramName;
    }

    /**
     * A constructor without a constraint. This is used in creating the filter for the
     * inititial unconstrained facet states.
     *
     * @param paramName    the parameter name of the facet constraint.
     * @param linkProperty the rdf link property.
     */
    public DateTimeSearchFilterImpl(final String paramName, final String linkProperty) {
        this(paramName, linkProperty, null);
    }

    // ---------- PUBLIC METHODS

    public String getSparqlFragment() {

        String dateVar = "?" + paramName;

        String sparql = new StringBuilder().append("?id ").append("<")
                .append(linkProperty).append(">").append(" ")
                .append(dateVar).append(" .\n").toString();

        if (constraint != null) {
            sparql = new StringBuilder(sparql).append("FILTER(regex(str(").append(dateVar)
                    .append("), \"^") .append(constraint).append("\", \"i\")) .\n").toString();
        }

        return sparql;
    }


    private final String paramName;
    private final String linkProperty;
    private final String constraint;
}
