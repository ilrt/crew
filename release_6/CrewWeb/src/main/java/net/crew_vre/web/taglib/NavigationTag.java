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
 * @version $Id: NavigationTag.java 1132 2009-03-20 19:05:47Z cmmaj $
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
                navAid(navHelper.getPrevious(), "&lt; Prev");
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
                navAid(navHelper.getNext(), "Next &gt;");
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
