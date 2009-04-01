package net.crew_vre.web.filters;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

public class ICalendarResponseWrapper extends HttpServletResponseWrapper {

    private CharArrayWriter output;

    public ICalendarResponseWrapper(HttpServletResponse response) {
        super(response);
        output = new CharArrayWriter();
    }

    public PrintWriter getWriter() {
        return new PrintWriter(output);
    }

    public String toString() {
        return output.toString();
    }
}
