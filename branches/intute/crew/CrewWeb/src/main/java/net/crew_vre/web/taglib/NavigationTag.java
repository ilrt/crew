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

import net.crew_vre.web.navigation.NavHelper;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * <p>A tag library for providing pagination.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: NavigationTag.java 1191 2009-03-31 13:38:51Z cmmaj $
 */
public class NavigationTag extends TagSupport {

    // ---------- Constructor and initialization ----------

    public NavigationTag() {
        super();
    }

    public void init() {
        params = null;
        navHelper = null;
        className = null;
    }

    // ---------- Tag logic ----------

    public int doStartTag() throws JspTagException {

        try {

            pageContext.getOut().print("<ul");
            checkCssClassProvided();
            pageContext.getOut().print(">\n");


            // do we have a prev link
            if (navHelper.hasPrev()) {
                navAid(navHelper.getPrevious(), "Prev");
            }

            // go through page numberts
            for (int pageNo = navHelper.getStartPage(); pageNo <= navHelper.getEndPage(); pageNo++) {

                pageContext.getOut().print("<li");
                checkCssClassProvided();
                pageContext.getOut().print(">");

                if (pageNo == navHelper.getCurrentPage()) {
                    pageContext.getOut().print("<strong>" + pageNo + "</strong>");
                } else {
                    pageContext.getOut().print("<a href=\"?page=");
                    pageContext.getOut().print(pageNo);

                    // get the keys for the parameter values
                    Set<String> keys = params.keySet();

                    // go through each key
                    for (String key : keys) {
                        // ignore the page parameter
                        if (!key.equals("page")) {
                            // parameter values are string arrays so check each element
                            for (String value : params.get(key)) {
                                pageContext.getOut().print("&amp;" + key + "=" + value);
                            }
                        }
                    }

                    pageContext.getOut().print("\">");
                    pageContext.getOut().print(pageNo);
                    pageContext.getOut().print("</a>");
                }

                pageContext.getOut().print("</li>\n");
            }


            // do we have a prev link
            if (navHelper.hasNext()) {
                navAid(navHelper.getNext(), "Next");
            }

            pageContext.getOut().println("</ul>");


        } catch (IOException e) {
            throw new JspTagException(e.getMessage());
        }

        return SKIP_BODY;
    }

    public int doEndTag() throws JspTagException {
        init();
        return EVAL_PAGE;
    }

    private void checkCssClassProvided() throws IOException, JspTagException {
        if (className != null) {
            pageContext.getOut().print(" class=\"");
            pageContext.getOut().print(className);
            pageContext.getOut().print("\"");
        }
    }

    private void navAid(int pageNumber, String linkText)
            throws IOException, JspTagException {

        // start list item
        pageContext.getOut().print("<li");
        checkCssClassProvided();
        pageContext.getOut().print(">");

        // item content
        pageContext.getOut().print("<a href=\"?page=");
        pageContext.getOut().print(pageNumber);
        pageContext.getOut().print("\">");
        pageContext.getOut().print(linkText);
        pageContext.getOut().print("</a>");

        // close
        pageContext.getOut().print("</li>\n");
    }


    // ---------- Setter Methods ----------

    public void setParams(Map<String, String[]> params) {
        this.params = params;
    }

    public void setNavHelper(NavHelper navHelper) {
        this.navHelper = navHelper;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    private Map<String, String[]> params;
    private NavHelper navHelper;
    private String className;
}
