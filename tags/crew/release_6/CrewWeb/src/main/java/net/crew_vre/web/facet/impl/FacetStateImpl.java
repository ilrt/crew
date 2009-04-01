package net.crew_vre.web.facet.impl;

import net.crew_vre.web.facet.FacetState;

import java.util.List;
import java.util.ArrayList;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: FacetStateImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class FacetStateImpl implements FacetState {

    public FacetStateImpl() {
    }

    public FacetStateImpl(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public FacetStateImpl(String name, int count, String paramName,
                          String paramValue) {
        this.name = name;
        this.count = count;
        this.paramName = paramName;
        this.paramValue = paramValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<FacetState> getRefinements() {
        return refinements;
    }

    public void setRefinements(List<FacetState> refinements) {
        this.refinements = refinements;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public FacetState getParent() {
        return parent;
    }

    public void setParent(FacetState parent) {
        this.parent = parent;
    }

    private String name;
    private int count;
    private List<FacetState> refinements = new ArrayList<FacetState>();
    private boolean isLeaf = false;
    private String paramName;
    private String paramValue;
    private FacetState parent;
}
