package org.ilrt.dibden.web.controller;

import org.ilrt.dibden.domain.Group;
import org.ilrt.dibden.domain.User;
import org.ilrt.dibden.facade.UserManagementFacade;
import org.ilrt.dibden.web.command.UserForm;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class EditUserController extends SimpleFormController {

    public EditUserController(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }

    public ModelAndView onSubmit(Object command, BindException errors) {

        UserForm userForm = (UserForm) command;

        if (userForm.getUsername() != null) {

            // we are updating user details
            if (userForm.getUpdateUser() != null) {
                userManagementFacade.updateUser(userForm.getUsername(), userForm.getName(),
                        userForm.getEmail());
            }

            // we are removing a group from the user
            if (userForm.getRemoveGroup() != null) {
                if (userForm.getUsername() != null && userForm.getUserGroupId() != null) {
                    userManagementFacade.removeUserFromGroup(userForm.getUsername(),
                            userForm.getUserGroupId());
                }
                return redisplayUser(userForm.getUsername());
            }

            // we are adding a role to a user
            if (userForm.getAddGroup() != null) {

                System.out.println("Need to add user to group");

                userManagementFacade.addUserToGroup(userForm.getUsername(), userForm.getAddGroupId());
                return redisplayUser(userForm.getUsername());
            }

        }

        return new ModelAndView(new RedirectView("./listUsers.do"));
    }

    private ModelAndView redisplayUser(String username) {
        User user = userManagementFacade.getUser(username);
        List<Group> groups = userManagementFacade.getGroups();

        UserForm userForm = new UserForm(user.getUsername(), user.getName(),
                user.getEmail(), user.getGroups(), groups);

        ModelAndView mav = new ModelAndView("editUser");

        mav.addObject("userForm", userForm);
        return mav;
    }

    private UserManagementFacade userManagementFacade;
}
