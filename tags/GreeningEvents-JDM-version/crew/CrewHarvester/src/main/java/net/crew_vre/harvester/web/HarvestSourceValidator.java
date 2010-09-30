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
package net.crew_vre.harvester.web;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import net.crew_vre.harvester.HarvesterSourceManagementFacade;

/**
 * @author Damian Steer (d.steer@bristol.ac.uk)
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HarvestSourceValidator.java 1190 2009-03-31 13:22:30Z cmmaj $
 */
public class HarvestSourceValidator implements Validator {

    public HarvestSourceValidator(HarvesterSourceManagementFacade facade) {
        this.facade = facade;
    }

    public boolean supports(Class clazz) {
        return HarvestSourceForm.class.equals(clazz);
    }

    public void validate(Object target, Errors errors) {

        HarvestSourceForm obj = (HarvestSourceForm) target;

        // only validate if the cancel button is *NOT* selected

        if (obj.getCancelButton() == null) {

            // location shouldn't be empty
            ValidationUtils.rejectIfEmpty(errors, "location", "harvester.location.missing");

            // check the protocol
            if (!(obj.getLocation().startsWith("http://")
                    || obj.getLocation().startsWith("http://")
                    || obj.getLocation().startsWith("file://"))) {
                errors.rejectValue("location", "harvester.location.protocol");
            }

            // check that the source doesn't already exist
            if (facade.getSource(obj.getLocation()) != null) {
                errors.rejectValue("location", "harvester.location.exists");
            }

        }
    }

    private HarvesterSourceManagementFacade facade;
}
