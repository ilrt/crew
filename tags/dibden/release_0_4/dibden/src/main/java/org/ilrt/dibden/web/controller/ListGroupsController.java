package org.ilrt.dibden.web.controller;

import org.ilrt.dibden.Utility;
import org.ilrt.dibden.domain.Group;
import org.ilrt.dibden.domain.Role;
import org.ilrt.dibden.facade.UserManagementFacade;
import org.ilrt.dibden.web.command.GroupForm;
import org.ilrt.dibden.web.command.ListGroupForm;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class ListGroupsController extends SimpleFormController {

    public ListGroupsController(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }

    public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response,
                                 Object command, BindException errors) {

        ListGroupForm listGroupForm = (ListGroupForm) command;

        // we want to create a new group - redirect to correct view
        if (listGroupForm.getNewGroup() != null) {
            ModelAndView mav = new ModelAndView("addGroup");
            mav.addObject("groupForm", new GroupForm());
            return mav;
        }

        // we want to edit an existing group - redirect to correct view
        if (listGroupForm.getEditGroup() != null) {
            if (listGroupForm.getGroupId() != null) {

                Group group = userManagementFacade.getGroup(listGroupForm.getGroupId());
                List<Role> roles = userManagementFacade.getRoles();

                GroupForm groupForm = new GroupForm(group.getGroupId(), group.getName(),
                        group.getDescription(), group.getRoles(), roles);

                ModelAndView mav = new ModelAndView("editGroup");

                mav.addObject("groupForm", groupForm);
                return mav;
            }
        }

        // delete a group if one has been selected
        if (listGroupForm.getDeleteGroup() != null) {
            if (listGroupForm.getGroupId() != null) {
                userManagementFacade.removeGroup(listGroupForm.getGroupId());
            }
        }

        return listGroupsView(request, errors);
    }


    public ModelAndView showForm(HttpServletRequest request, HttpServletResponse response,
                                 BindException errors) {

        return listGroupsView(request, errors);
    }

    private ModelAndView listGroupsView(HttpServletRequest request, BindException errors) {

        int page = 1;

        String pageValue = request.getParameter("page");

        if (pageValue != null) {
            page = Integer.parseInt(pageValue);

        }

        int total = userManagementFacade.totalGroups();


        ModelAndView mav = new ModelAndView("listGroups");
        mav.addObject("total", total);
        mav.addObject("results", userManagementFacade.getGroups(Utility.calculateRecord(page),
                MAX_RECORDS));
        mav.addObject("listGroupForm", new ListGroupForm());
        mav.addObject("pages", Utility.calculatePages(total, MAX_RECORDS));
        mav.addObject("page", page);
        mav.addObject("errors", errors);
        return mav;
    }


    private int MAX_RECORDS = 10;
    final private UserManagementFacade userManagementFacade;
}
