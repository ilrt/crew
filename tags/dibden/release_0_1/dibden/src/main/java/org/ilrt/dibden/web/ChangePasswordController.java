/*
 * Copyright (c) 2008, University of Bristol
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
 * 3) Neither the name of the University of Bristol nor the names of its
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
package org.ilrt.dibden.web;

import org.ilrt.dibden.facade.UserManagementFacade;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: ChangePasswordController.java 11 2008-06-04 10:41:37Z cmmaj $
 *
 **/
public class ChangePasswordController extends SimpleFormController {

    public ChangePasswordController(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }

    public void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) {

        ChangePasswordCommand passwordCommand = (ChangePasswordCommand) command;

        String username = request.getUserPrincipal().getName();

        if (username == null) {
            errors.reject("username.missing");
        }

        if (!userManagementFacade.validatePassword(username, passwordCommand.getOldPassword())) {
            errors.rejectValue("oldPassword", "register.oldPassword.wrong");
        }

        if (!errors.hasErrors()) {
            userManagementFacade.updatePassword(username, passwordCommand.getPasswordOne());
        }

    }


    private UserManagementFacade userManagementFacade;
}
