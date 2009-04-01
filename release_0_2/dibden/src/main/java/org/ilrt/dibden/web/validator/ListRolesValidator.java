
package org.ilrt.dibden.web.validator;

import org.ilrt.dibden.facade.UserManagementFacade;
import org.ilrt.dibden.web.command.ListRoleForm;
import org.ilrt.dibden.Role;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 *
 **/
public class ListRolesValidator implements Validator {

    public ListRolesValidator(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }

    public boolean supports(Class aClass) {
        return aClass.equals(ListRoleForm.class);
    }

    public void validate(Object o, Errors errors) {

        ListRoleForm listRoleForm = (ListRoleForm) o;

        if (listRoleForm.getDeleteRole() != null) {

            Role role = userManagementFacade.getRole(listRoleForm.getRoleId());

            if (role.getRoleMembers().size() > 0) {
                errors.reject("role.delete.error");
            }
        }
    }

    private UserManagementFacade userManagementFacade;
}
