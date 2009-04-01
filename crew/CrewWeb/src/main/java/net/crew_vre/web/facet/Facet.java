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

/**
 * <p>A facet is a way of browsing the data.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: Facet.java 1191 2009-03-31 13:38:51Z cmmaj $
 */
public interface Facet {

    String getName();

    FacetState getState();

    // possible parameter values in the facet configuration
    String FACET_TYPE = "facetType";
    String FACET_TITLE = "facetTitle";
    String LINK_PROPERTY = "linkProperty";
    String WIDER_PROPERTY = "widerProperty";
    String FACET_BASE = "facetBase";
    String CONSTRAINT_TYPE = "constraintType";
    String PARAM_NAME = "paramName";
    String PREFIX = "prefix";
    String START_YEAR = "startYear";
    String END_YEAR = "endYear";

    // facet types
    String ALPHA_NUMERIC_FACET_TYPE = "AlphaNumeric";
    String HIERARCHICAL_FACET_TYPE = "Hierarchical";
    String DATE_TIME_FACET_TYPE = "DateTime";
    String FLAT_FACET_TYPE = "Flat";
    String TEXT_SEARCH_FACET = "TextSearch" ;
}
