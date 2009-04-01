package net.crew_vre.web.facet.impl;

import net.crew_vre.web.facet.SearchFilter;

public class TextSearchFilter implements SearchFilter {
	
	private String searchTerm;

	public TextSearchFilter(final String searchTerm) {
		this.searchTerm = searchTerm;
	}
	
	public String getSparqlFragment() {
		return String.format("?id <http://jena.hpl.hp.com/ARQ/property#textMatch> \"%s\" .\n", searchTerm);
	}

}
