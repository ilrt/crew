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
package org.ilrt.dibden.web.controller;

import org.ilrt.dibden.Role;
import org.ilrt.dibden.User;
import org.ilrt.dibden.Utility;
import org.ilrt.dibden.facade.UserManagementFacade;
import org.ilrt.dibden.web.command.ListUserForm;
import org.ilrt.dibden.web.command.UserForm;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: ListUsersController.java 89 2008-09-09 14:48:05Z cmmaj $
 */
public class ListUsersController extends SimpleFormController {

    public ListUsersController(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }


    public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response,
                                 Object command, BindException errors) {

        ListUserForm listUserForm = (ListUserForm) command;

        // we want to edit an existing user - redirect to correct view
        if (listUserForm.getEditUser() != null) {

            if (listUserForm.getUsername() != null) {

                User user = userManagementFacade.getUser(listUserForm.getUsername());
                List<Role> roles = userManagementFacade.getRoles();

                UserForm userForm = new UserForm(user.getUsername(), user.getName(),
                        user.getEmail(), user.getRoles(), roles);
                ModelAndView mav = new ModelAndView("editUser");

                mav.addObject("userForm", userForm);
                return mav;
            }

        }

        // delete a user if one has been selected
        if (listUserForm.getDeleteUser() != null) {
            if (listUserForm.getUsername() != null) {
                userManagementFacade.removeUser(listUserForm.getUsername());
            }
        }

        return listUsersView(request);
    }


    public ModelAndView showForm(HttpServletRequest request, HttpServletResponse response,
                                 BindException errors) {

        return listUsersView(request);
    }

    private ModelAndView listUsersView(HttpServletRequest request) {

        int page = 1;

        String pageValue = request.getParameter("page");

        if (pageValue != null) {
            page = Integer.parseInt(pageValue);

        }

        int total = userManagementFacade.totalUsers();
        List<User> results = userManagementFacade.getUsers(Utility.calculateRecord(page),
                MAX_RECORDS);

        ModelAndView mav = new ModelAndView("listUsers");
        mav.addObject("total", total);
        mav.addObject("results", results);
        mav.addObject("listUserForm", new ListUserForm());
        mav.addObject("pages", Utility.calculatePages(total, MAX_RECORDS));
        mav.addObject("page", page);

        return mav;
    }



    UserManagementFacade userManagementFacade;
    private int MAX_RECORDS = 10;

}
