package org.ilrt.dibden.web.controller;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import org.ilrt.dibden.Role;
import org.ilrt.dibden.User;
import org.ilrt.dibden.facade.UserManagementFacade;
import org.ilrt.dibden.web.command.UserForm;

import java.util.List;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 *
 **/
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

            // we are removing a role to a user
            if (userForm.getRemoveRole() != null) {
                userManagementFacade.removeRoleFromUser(userForm.getUserRoleId(),
                        userForm.getUsername());
                return redisplayUser(userForm.getUsername());
            }

            // we are adding a role to a user
            if (userForm.getAddRole() != null) {
                userManagementFacade.addRoleToUser(userForm.getUsername(), userForm.getAddRoleId());
                return redisplayUser(userForm.getUsername());
            }

        }

        return new ModelAndView(new RedirectView("./listUsers.do"));
    }

    private ModelAndView redisplayUser(String username) {
        User user = userManagementFacade.getUser(username);
        List<Role> roles = userManagementFacade.getRoles();

        UserForm userForm = new UserForm(user.getUsername(), user.getName(),
                user.getEmail(), user.getRoles(), roles);
        ModelAndView mav = new ModelAndView("editUser");

        mav.addObject("userForm", userForm);
        return mav;
    }

    private UserManagementFacade userManagementFacade;
}
