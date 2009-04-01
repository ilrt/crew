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
 * @version $Id$
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

            if (className != null) {
                pageContext.getOut().print(" class=\"");
                pageContext.getOut().print(className);
                pageContext.getOut().print("\"");
            }

            pageContext.getOut().print(">\n");

            // go through page numberts
            for (int pageNo = 1; pageNo <= navHelper.getTotalPages(); pageNo++) {

                pageContext.getOut().print("<li");

                if (className != null) {
                    pageContext.getOut().print(" class=\"");
                    pageContext.getOut().print(className);
                    pageContext.getOut().print("\"");
                }

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
