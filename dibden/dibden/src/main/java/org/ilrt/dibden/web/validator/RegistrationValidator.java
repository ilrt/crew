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
package org.ilrt.dibden.web.validator;

import org.ilrt.dibden.facade.UserManagementFacade;
import org.ilrt.dibden.web.command.RegistrationCommand;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: RegistrationValidator.java 128 2009-03-31 14:09:42Z cmmaj $
 *
 **/
public class RegistrationValidator implements Validator {

    public RegistrationValidator(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }


    public boolean supports(Class aClass) {
        return aClass.equals(RegistrationCommand.class);
    }

    public void validate(Object o, Errors errors) {

        RegistrationCommand regCommand = (RegistrationCommand) o;

        if (regCommand == null) {

            // ????? DO SOMETHING

        } else {

            // check that the fields aren't empty
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName",
                    "register.username.empty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "register.name.empty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailOne", "register.email.empty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailTwo",
                    "register.email.confirm.empty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordOne",
                    "register.password.empty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordTwo",
                    "register.password.confirm.empty");

            // check the username is alpha numeric characters
            Matcher userNameMatcher = userNamePattern.matcher(regCommand.getUserName());

            if (!userNameMatcher.matches()) {
                errors.rejectValue("userName", "register.username.alpha");
            }

            // check that the username isn't registered
            if (userManagementFacade.isUsernameRegistered(regCommand.getUserName())) {
                errors.rejectValue("userName", "register.username.exists");
            }

            // check that passwords match
            if (!regCommand.getPasswordOne().equals(regCommand.getPasswordTwo())) {
                errors.reject("passwordOne", "register.password.match");
            }

            // check the structure of the emal address
            Matcher emailMatcher = emailPattern.matcher(regCommand.getEmailOne());

            if (!emailMatcher.matches()) {
                errors.rejectValue("emailOne", "register.email.valid");
            }

            // check that emails match
            if (!regCommand.getEmailOne().equals(regCommand.getEmailTwo())) {
                errors.reject("emailOne", "register.email.match");
            }

            // check that the email isn't registered
            if (userManagementFacade.isEmailRegistered(regCommand.getEmailOne())) {
                errors.rejectValue("emailOne", "register.email.exists");
            }

        }

    }

    private UserManagementFacade userManagementFacade;

    Pattern userNamePattern = Pattern.compile("^[a-zA-Z0-9]+$");
    Pattern emailPattern = Pattern.compile("^[\\.\\+_a-zA-Z0-9-]+@[a-zA-Z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,6})$");

}
