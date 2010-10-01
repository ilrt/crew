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

import org.ilrt.dibden.domain.Role;
import org.ilrt.dibden.Utility;
import org.ilrt.dibden.facade.UserManagementFacade;
import org.ilrt.dibden.web.command.ListRoleForm;
import org.ilrt.dibden.web.command.RoleForm;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: ListRolesController.java 128 2009-03-31 14:09:42Z cmmaj $
 */
public class ListRolesController extends SimpleFormController {

    public ListRolesController(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }


    public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response,
                                 Object command, BindException errors) {

        ListRoleForm listRoleForm = (ListRoleForm) command;

        // we want to create a new role - redirect to correct view
        if (listRoleForm.getNewRole() != null) {
            return new ModelAndView(new RedirectView("./addRole.do"));
        }

        // we want to edit an existing role - redirect to correct view
        if (listRoleForm.getEditRole() != null) {
            if (listRoleForm.getRoleId() != null) {
                Role role = userManagementFacade.getRole(listRoleForm.getRoleId());
                RoleForm roleForm = new RoleForm(role.getRoleId(), role.getName(),
                        role.getDescription());
                ModelAndView mav = new ModelAndView("editRole");
                mav.addObject("roleForm", roleForm);
                return mav;
            }
        }

        // delete a role if one has been selected
        if (listRoleForm.getDeleteRole() != null) {
            if (listRoleForm.getRoleId() != null) {
                userManagementFacade.removeRole(listRoleForm.getRoleId());
            }
        }

        return listRolesView(request, errors);
    }


    public ModelAndView showForm(HttpServletRequest request, HttpServletResponse response,
                                 BindException errors) {

        return listRolesView(request, errors);
    }

    private ModelAndView listRolesView(HttpServletRequest request, BindException errors) {

        int page = 1;

        String pageValue = request.getParameter("page");

        if (pageValue != null) {
            page = Integer.parseInt(pageValue);

        }

        int total = userManagementFacade.totalRoles();


        ModelAndView mav = new ModelAndView("listRoles");
        mav.addObject("total", total);
        mav.addObject("results", userManagementFacade.getRoles(Utility.calculateRecord(page),
                MAX_RECORDS));
        mav.addObject("listRoleForm", new ListRoleForm());
        mav.addObject("pages", Utility.calculatePages(total, MAX_RECORDS));
        mav.addObject("page", page);
        mav.addObject("errors", errors);
        return mav;
    }

    public ModelAndView newRoleView() {

        return new ModelAndView("addRole");
    }

    private UserManagementFacade userManagementFacade;
    private int MAX_RECORDS = 10;
}
