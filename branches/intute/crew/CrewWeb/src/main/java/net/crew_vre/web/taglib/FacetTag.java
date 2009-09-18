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
package net.crew_vre.web.taglib;

import net.crew_vre.web.facet.Facet;
import net.crew_vre.web.facet.FacetState;
import net.crew_vre.web.facet.impl.TextFacetFactory;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>A tag component to display a facet in a JSP page. A facet is displayed as a nested
 * list of parents and refinements.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: FacetTag.java 1191 2009-03-31 13:38:51Z cmmaj $
 */
public class FacetTag extends BodyTagSupport {

    public FacetTag() {
        super();
        init();
    }

    public int doStartTag() throws JspTagException {

        try {

            // get the JSP writer
            JspWriter out = pageContext.getOut();

            // display the name of the facet
            if (facet != null && facet.getState() != null) {

                out.println("<div class=\"crew-facet\">");

                out.println(new StringBuilder().append("<h5>")
                        .append(facet.getName()).append("</h5>").toString());

                // get the state of the facet
                FacetState facetState = facet.getState();
                
                // if root just display any refinements
                if (facetState.isRoot()) {
                	if (facetState instanceof TextFacetFactory.TextFacetState)
						displayTextSearch(out, facetState);
					else displayRefinements(facetState.getRefinements());
                    out.println("</div>");
                    return SKIP_BODY;
                }

                // find the parents of the facet
                List list = findParents(facetState);

                if (list.size() > 1) {

                    // add the parents to the nested list
                    Iterator i = list.iterator();
                    while (i.hasNext()) {
                        FacetState state = (FacetState) i.next();

                        if (state.isRoot()) {
                            out.println("<ul class=\"crew-facet-list\">");
                        }

                        if (!state.isRoot()) {
                            out.println(new StringBuilder()
                                    .append("<li class=\"crew-facet-list-item\">")
                                    .append(state.getName()).append("&nbsp;[<a href=\"")
                                    .append(createUrl(state.getParent().getParamName(),
                                            state.getParent().getParamValue()))
                                    .append("\">x</a>]"));
                        }

                        if (!state.isRoot() && i.hasNext()) {
                            out.println("<ul>");
                        }
                    }
                }

                // add the current facet details
                out.println("<ul class=\"crew-facet-list\">");
                out.println(new StringBuilder()
                        .append("<li class=\"crew-facet-list-item\">")
                        .append("<span class=\"current-facet\">")
                        .append(facetState.getName()).append("</span>")
                        .append("&nbsp;[<a href=\"")
                        .append(createUrl(facetState.getParent().getParamName(),
                                facetState.getParent().getParamValue()))
                        .append("\">x</a>]").toString());

                // display any refinements
                if (facetState.getRefinements().size() > 0) {
                    displayRefinements(facetState.getRefinements());
                }
                out.println("</li>");

                // display the correct number of closing <ul> tags
                if (list.size() > 1) {
                    for (int j = 1; j < list.size(); j++) {
                        out.println("</ul>");
                        out.println("</li>");
                    }
                }

                out.println("</ul>");
                out.println("</div>");
            }
        } catch (IOException e) {
            throw new JspTagException(e.getMessage());
        }

        return SKIP_BODY;
    }

	/**
     * <p>We display the parents in a nested list - we need to go through each facet and find
     * its parent and place it in the correct order on the List - e.g. great grand parent,
     * grand parent and then parent.</p>
     *
     * @param state the current facet state.
     * @return a List of parent facet states in the correct order
     */
    private List<FacetState> findParents(FacetState state) {

        List<FacetState> pList = new ArrayList<FacetState>();

        while (state.getParent() != null) {
            state = state.getParent();
            pList.add(0, state);
        }
        return pList;
    }

    private void displayTextSearch(JspWriter out, FacetState facetState)
			throws IOException {out.print("<form class=\"crew-facet-list-item\" method=\"GET\" action=\"");
		out.print(url);
		out.print("\">\n");
		out.print("  <input name=\"");
		out.print(facetState.getParamName());
		out.println("\" type=\"text\" value=\"\" />");
		out.println("  <input type=\"submit\" value=\"search...\" />");
		if (parameters != null) {
			for (Map.Entry<String, String[]> entry: parameters.entrySet()) {
				for (String paramValue: entry.getValue()) {
					out.println(String.format(
							"  <input type=\"hidden\" name=\"%s\" value=\"%s\" />",
							entry.getKey(), paramValue ));
				}
			}
		}
		out.println("</form>");
	}

	public void displayRefinements(List<FacetState> refinements) throws IOException {

        pageContext.getOut().println("<ul class=\"crew-facet-list\">");

        for (FacetState state : refinements) {
            if (state.getCount() > 0 || showEmpty) {
                pageContext.getOut().print(new StringBuilder()
                        .append("<li class=\"crew-facet-list-item\">")
                        .append("<a href=\"")
                        .append(createUrl(state.getParamName(), state.getParamValue()))
                        .append("\">").append(state.getName()).append("</a>"));

                if (showCount) {
                    pageContext.getOut().print(new StringBuilder().append("&nbsp;").append("(")
                            .append(state.getCount()).append(")"));
                }

                pageContext.getOut().print("</li>\n");
            }
        }
        pageContext.getOut().println("</ul>");
    }

    private String createUrl(String facetParam, String facetParamVal) {

        // if there are no original request parameters and there is no new
        // parameter value, then just return the base url
        if (parameters == null && facetParamVal == null && facetParam == null) {
            return url;
        }

        // if the parameters are null instantiate the Map
        if (parameters == null) {
            parameters = new HashMap<String, String[]>();
        }

        // does the parameter already exist on the request? we need to check whether we are
        // replacing the value or removing the parameter and value
        if (parameters.get(facetParam) != null) {

            if (facetParamVal == null || facetParam.equals("")) {
                parameters.remove(facetParam);
            } else {
                parameters.put(facetParam, new String[]{facetParamVal});
            }

        } else {

            // it doesn't exist - if there is a new parameter value then we need to add it
            // to the map
            if (facetParam != null && facetParamVal != null) {
                parameters.put(facetParam, new String[]{facetParamVal});
            }
        }

        // reconstruct the URL from the Map
        Iterator<String> i = parameters.keySet().iterator();

        StringBuilder builder = new StringBuilder(url).append("?");

        while (i.hasNext()) {
            String key = i.next();

            String[] values = parameters.get(key);

            for (int j = 0; j < values.length; j++) {
                if (j != 0) {
                    builder.append("&amp;");
                }
                builder.append(key).append("=").append(values[j]);
            }

            if (i.hasNext()) {
                builder.append("&amp;");
            }
        }

        return builder.toString();
    }

    public int doEndTag() throws JspTagException {
        resetValues();
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
        init();
    }

    /**
     * <p>Values that need to be reset after each request.</p>
     */
    public void resetValues() {
        facet = null;
        parameters = new HashMap<String, String[]>();
        url = null;
    }

    /**
     * <p>Instantiate member variables with default values.</p>
     */
    private void init() {
        resetValues();
        showEmpty = false;
        showCount = true;
    }

    public void setFacet(Facet facet) {
        this.facet = facet;
    }

    public void setShowEmpty(boolean showEmpty) {
        this.showEmpty = showEmpty;
    }

    public void setShowCount(boolean showCount) {
        this.showCount = showCount;
    }

    /**
     * <p>Create a deep copy of the original request parameter values.</p>
     *
     * @param params the original request parameters.
     */
    public void setParameters(Map params) {
        if (params != null) {
            for (Object o : params.keySet()) {
                String key = (String) o;
                String[] val = (String[]) params.get(key);
                parameters.put(key, val);
            }
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private Facet facet;

    private boolean showEmpty;

    private boolean showCount;

    private Map<String, String[]> parameters;

    private String url;
}
