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

import java.util.List;
import java.util.Map;

import net.crew_vre.web.facet.Facet;
import net.crew_vre.web.facet.FacetState;
import net.crew_vre.web.facet.SearchFilter;

import org.caboto.jena.db.Data;

import edu.emory.mathcs.backport.java.util.Collections;

public class TextFacetFactory {

	public Facet create(Map<String, String> config, String parameter, Data data) {
		FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
		facet.setState(getState(parameter, config.get(Facet.PARAM_NAME)));
		return facet;
	}

	public Facet create(Map<String, String> config,
			List<SearchFilter> searchFilters, Data data) {
		FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
		facet.setState(getState(null, config.get(Facet.PARAM_NAME)));
		return facet;
	}

	public Facet create(Map<String, String> config, Data data) {
		FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
		facet.setState(getState(null, config.get(Facet.PARAM_NAME)));
		return facet;
	}

	private FacetState getState(String name, String paramName) {
		return new TextFacetState(name, paramName);
	}
	
	public static class TextFacetState implements FacetState {

		private final String searchTerm;
		private final String paramName;
		private final FacetState parent;
		
		public TextFacetState(final String searchTerm, final String paramName) {
			this.searchTerm = searchTerm;
			this.paramName = paramName;
			this.parent = (searchTerm == null) ?
					null :
					new TextFacetState(null, paramName);
		}
		
		public int getCount() { return 0; }

		public String getName() { return searchTerm; }

		public String getParamName() { return paramName; }

		public String getParamValue() { return searchTerm; }

		public FacetState getParent() { return parent; }

		public List<FacetState> getRefinements() { return Collections.emptyList(); }

		public boolean isLeaf() { return true; }

		public boolean isRoot() { return searchTerm == null; }
		
	}
}
