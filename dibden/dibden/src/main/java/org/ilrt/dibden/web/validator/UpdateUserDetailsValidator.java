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

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ilrt.dibden.web.command.UserForm;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: RegistrationValidator.java 128 2009-03-31 14:09:42Z cmmaj $
 *
 **/
public class UpdateUserDetailsValidator implements Validator {

    public UpdateUserDetailsValidator() { }


    public boolean supports(Class aClass) {
        return aClass.equals(UserForm.class);
    }

    public void validate(Object o, Errors errors) {

        UserForm userForm = (UserForm) o;

        if (userForm == null) {

            // ????? DO SOMETHING

        } else {

            // check that the fields aren't empty
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "register.name.empty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "register.email.empty");

            // check the structure of the email address
            Matcher emailMatcher = emailPattern.matcher(userForm.getEmail());

            if (!emailMatcher.matches()) {
                errors.rejectValue("email", "register.email.valid");
            }

            // check the structure of the optional post code
            if (userForm.getPostcode() != null && !userForm.getPostcode().isEmpty()) {
                Matcher postcodeMatcher = postcodePattern.matcher(userForm.getPostcode());

                if (!postcodeMatcher.matches()) {
                    errors.rejectValue("postcode", "register.postcode.valid");
                }
            }

        }

    }

    Pattern emailPattern = Pattern.compile("^[\\.\\+_a-zA-Z0-9-]+@[a-zA-Z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,6})$");
    Pattern postcodePattern = Pattern.compile("^\\w{2}\\d\\d?\\s\\d\\w{2}$");

}
