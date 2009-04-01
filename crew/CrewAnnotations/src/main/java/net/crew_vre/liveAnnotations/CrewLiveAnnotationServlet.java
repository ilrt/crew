/*
 * Copyright (c) 2008, University of Bristol
 * Copyright (c) 2008, University of Manchester
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

package net.crew_vre.liveAnnotations;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.caboto.domain.AnnotationException;

import net.crew_vre.annotations.CrewLiveAnnotation;
import net.crew_vre.annotations.liveannotationtype.LiveAnnotationTypeRepository;
import net.crew_vre.annotations.liveannotationtype.impl.LiveAnnotationTypeRepositoryXmlImpl;

public class CrewLiveAnnotationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private LiveAnnotationTypeRepository liveAnnotationTypeRepository = null;
    private Server server = null;

    /**
     *
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException {
        try {
            liveAnnotationTypeRepository = new LiveAnnotationTypeRepositoryXmlImpl(
                    getInitParameter("liveAnnotationRepository"));
            server = new Server(liveAnnotationTypeRepository);
            server.setAnnotationFile("logs/default.xml");
            server.start();
            getServletContext().setAttribute("server", server);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doGet(req, resp);
    }

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        String uri = request.getRequestURI();
        if (uri.equals(request.getContextPath()) || uri.equals("/")) {
            uri += "index.jsp";
        }
        if (uri.endsWith("SendMessage.jsp")) {
            HttpSession session = request.getSession();
            HashMap<String, String> params = new HashMap<String, String>();
            Client client = (Client) session.getAttribute("client");
            Enumeration< ? > parameters = request.getParameterNames();
            while (parameters.hasMoreElements()) {
                String parameterName = (String) parameters.nextElement();
                params.put(parameterName, request.getParameter(parameterName));
            }
            try {
                client.setMessage(new CrewLiveAnnotation(
                        liveAnnotationTypeRepository, params));
            } catch (AnnotationException e) {
                e.printStackTrace();
            }
        } else if (uri.endsWith("getTime.jsp")) {
            Date date = new Date();
            response.getWriter().println(date.getTime());
        } else if (uri.endsWith("getmessage.jsp")) {
            Client client = (Client) request.getSession().getAttribute("client");
            response.setContentType("text/xml");
            PrintWriter writer = response.getWriter();
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            if (client != null) {
                String message = client.getMessage();
                writer.println("<messages>" + message + "</messages>");
            } else {
                writer.println("<messages>Done</messages>");
            }
        } else if (uri.endsWith("close.jsp")) {
            Client client = (Client) request.getSession().getAttribute("client");
            if (client != null) {
                server.close(client);
                request.getSession().removeAttribute("client");
            }
        } else if (uri.endsWith("index.jsp")) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/index.jsp");
            request.setAttribute("server", server);
            dispatcher.forward(request, response);
        }
    }

    public Server getServer() {
        return server;
    }
}
