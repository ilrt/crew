package net.crew_vre.web.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class ICalendarResponseFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (filterConfig == null) {
            return;
        }

        PrintWriter out = response.getWriter();

        ICalendarResponseWrapper wrapper =
                new ICalendarResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, wrapper);

        BufferedReader reader = new BufferedReader(new StringReader(wrapper.toString()));

        String line;

        StringBuffer buffer = new StringBuffer();

        while ((line = reader.readLine()) != null) {

            if (!line.matches("^[A-Z]{3,}:.*")) {
                line = " " + line;
            }

            // only take the lines that have content ...
            if (line.length() > 0 && (!line.matches("\\s+"))) {

                if (line.startsWith(" ")) {
                    line = " " + line.trim();
                }

                // if we are over 75 characters we need to wrap the line
                while (line.length() > 75) {

                    List<String> results = lineLength(line);

                    buffer.append(results.get(0)).append("\n");

                    line = " " + results.get(1);
                }

                buffer.append(line).append("\n");

            }

        }


        wrapper.setContentType("text/calendar");
        wrapper.setHeader("Content-Disposition", "attachment; filename=event.ics");

        out.write(buffer.toString());
        
    }

    private List<String> lineLength(String line) {

        List<String> results = new ArrayList<String>();

        results.add(line.substring(0, 74));
        results.add(line.substring(74, line.length()));

        return results;
    }


    public void destroy() {
        this.filterConfig = null;
    }


    private FilterConfig filterConfig = null;
}
