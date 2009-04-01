package org.ilrt.dibden.web.validator;

import org.ilrt.dibden.domain.Group;
import org.ilrt.dibden.facade.UserManagementFacade;
import org.ilrt.dibden.web.command.ListGroupForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class ListGroupsValidator implements Validator {

    public ListGroupsValidator(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }

    public boolean supports(Class aClass) {
        return aClass.equals(ListGroupForm.class);
    }

    public void validate(Object o, Errors errors) {

        ListGroupForm listGroupForm = (ListGroupForm) o;

        if (listGroupForm.getDeleteGroup() != null) {

            if (listGroupForm.getGroupId() != null) {
                Group group = userManagementFacade.getGroup(listGroupForm.getGroupId());

                if (group.getGroupMembers().size() > 0) {
                    errors.reject("group.delete.error");
                }
            }
        }
    }

    private UserManagementFacade userManagementFacade;
}