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

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.ilrt.dibden.facade.UserManagementFacade;
import org.ilrt.dibden.web.command.RegistrationCommand;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: RegistrationController.java 128 2009-03-31 14:09:42Z cmmaj $
 */
public class RegistrationController extends SimpleFormController {

    public RegistrationController(UserManagementFacade userManagementFacade, ReCaptcha reCaptcha,
                                  String publicKey) {
        this.userManagementFacade = userManagementFacade;
        this.reCaptcha = reCaptcha;
        this.publicKey = publicKey;
    }

    public void doSubmitAction(Object command) {

        RegistrationCommand regCommand = (RegistrationCommand) command;

        userManagementFacade.registerUser(regCommand.getUserName(), regCommand.getPasswordOne(),
                regCommand.getName(), regCommand.getEmailOne(), regCommand.getPostcode());
    }


    public void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) {

        RegistrationCommand regCommand = (RegistrationCommand) command;

        String remoteAddress = request.getRemoteAddr();

        ReCaptchaResponse response = reCaptcha.checkAnswer(remoteAddress,
                regCommand.getRecaptcha_challenge_field(),
                regCommand.getRecaptcha_response_field());

        String msg = response.getErrorMessage();

        System.out.println("Message: " + msg);

        if (msg != null) {
            errors.rejectValue("recaptcha_challenge_field", "captcha.error", msg);
        }

    }


    public ModelAndView showForm(HttpServletRequest request, HttpServletResponse response,
                                 BindException errors) {

        ModelAndView mav = new ModelAndView("registration");
        mav.addObject("publicKey", publicKey);
        mav.addObject("registrationCommand", new RegistrationCommand());
        return mav;
    }

    private UserManagementFacade userManagementFacade;
    private ReCaptcha reCaptcha;
    private String publicKey;
}
