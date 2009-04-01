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
