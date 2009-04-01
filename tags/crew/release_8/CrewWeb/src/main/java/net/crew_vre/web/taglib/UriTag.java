package net.crew_vre.web.taglib;

import org.w3c.URLUTF8Encoder;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: UriTag.java 1009 2009-01-17 15:57:48Z cmmaj $
 */
public class UriTag extends BodyTagSupport {

    public UriTag() {
        super();
        init();
    }

    public int doStartTag() throws JspTagException {

        try {
            pageContext.getOut().print(URLUTF8Encoder.encode(uri));
        } catch (IOException e) {
            throw new JspTagException(e.getMessage());
        }

        return SKIP_BODY;
    }

    public int doEndTag() throws JspTagException {
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
        init();
    }

    private void init() {
        uri = null;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    private String uri;

}
