package net.crew_vre.web.controller;

import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author: Mike Jones (mike.a.jones@gmail.com)
 * @version: $Id$
 *
 **/
public class LogoutController extends AbstractController {
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
                                                 HttpServletResponse response)
            throws Exception {

        request.getSession().invalidate();
        return new ModelAndView("redirect:./");
    }
}
