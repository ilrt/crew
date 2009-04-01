package net.crew_vre.web.facet;

import java.util.List;

/**
 * <p>Represents the state of a facet.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: FacetState.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface FacetState {

    String getName();

    int getCount();

    String getParamName();

    String getParamValue();

    List<FacetState> getRefinements();

    boolean isRoot();

    boolean isLeaf();

    FacetState getParent();

}
