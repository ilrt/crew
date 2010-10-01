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
