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
package org.ilrt.dibden.web.controller;

import org.ilrt.dibden.domain.User;
import org.ilrt.dibden.facade.UserManagementFacade;
import org.ilrt.dibden.web.command.UserForm;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 */
public class UpdateUserDetailsController extends SimpleFormController {

    public UpdateUserDetailsController(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }

    @Override
    public ModelAndView onSubmit(Object command, BindException errors) {

        UserForm userForm = (UserForm) command;

        if (userForm.getUsername() != null) {
            // we are updating user details
            if (userForm.getUpdateUser() != null) {
                userManagementFacade.updateUser(userForm.getUsername(), userForm.getName(),
                        userForm.getEmail(), userForm.getPostcode());
            }
        }
        
        return new ModelAndView(new RedirectView("./displayProfile.do"));
    }

    @Override
    public ModelAndView showForm(HttpServletRequest request, HttpServletResponse response,
            BindException errors) {

        if (logger.isDebugEnabled()) {
            logger.debug("Got request for updateUserDetails form");
        }
        ModelAndView mav = new ModelAndView("updateUserDetails");

        if (request.getUserPrincipal() != null) {
            String username = request.getUserPrincipal().getName();
            User user = userManagementFacade.getUser(username);
            if (logger.isDebugEnabled()) {
                logger.debug("Have user details for username: " + username);
            }

            UserForm userForm = new UserForm(user.getUsername(), user.getName(),
                    user.getEmail(), user.getPostcode(), user.getGroups(), null);

            mav.addObject("userForm", userForm);
        }
        
        return mav;
    }

    private UserManagementFacade userManagementFacade;
    private Logger logger = Logger.getLogger("org.ilrt.dibden.web.controller.UpdateUserDetailsController");
}
