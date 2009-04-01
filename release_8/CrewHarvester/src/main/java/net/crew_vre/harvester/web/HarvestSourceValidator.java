package net.crew_vre.harvester.web;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import net.crew_vre.harvester.HarvesterSourceManagementFacade;

/**
 * @author Damian Steer (d.steer@bristol.ac.uk)
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HarvestSourceValidator.java 929 2008-11-18 15:28:41Z cmmaj $
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
