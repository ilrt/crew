package net.crew_vre.web.startup;

import org.ilrt.dibden.facade.UserManagementFacade;
import org.ilrt.dibden.domain.Group;
import org.ilrt.dibden.domain.Role;
import org.ilrt.dibden.domain.User;

public class UserManagement {

    public UserManagement(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }


    public void init() {


        // user group
        Group userGroup = userManagementFacade.getGroup("USER_GROUP");

        if (userGroup == null) {

            userManagementFacade.createGroup("USER_GROUP", "Users group",
                    "Default Group for users");

            Role role = userManagementFacade.getRole("USER");

            if (role == null) {
                userManagementFacade.createRole("USER", "Users", "Users role");
            }

            userManagementFacade.addRoleToGroup("USER_GROUP", "USER");
        }

        // admin group
        Group adminGroup = userManagementFacade.getGroup("ADMIN_GROUP");

        if (adminGroup == null) {

            userManagementFacade.createGroup("ADMIN_GROUP", "Admin group",
                    "The admin user group");

            Role role = userManagementFacade.getRole("ADMIN");

            if (role == null) {
                userManagementFacade.createRole("ADMIN", "Admin", "Admin role");
            }

            userManagementFacade.addRoleToGroup("ADMIN_GROUP", "ADMIN");
            userManagementFacade.addRoleToGroup("ADMIN_GROUP", "USER");
        }


        // harvester group
        Group harvesterGroup = userManagementFacade.getGroup("HARVESTER_GROUP");

        if (harvesterGroup == null) {

            userManagementFacade.createGroup("HARVESTER_GROUP", "Harvester group",
                    "The harvester admin user group");

            Role role = userManagementFacade.getRole("HARVESTER_ADMIN");

            if (role == null) {
                userManagementFacade.createRole("HARVESTER_ADMIN", "Harvester Admin",
                        "Harvester Admin role");
            }

            userManagementFacade.addRoleToGroup("HARVESTER_GROUP", "HARVESTER_ADMIN");
            userManagementFacade.addRoleToGroup("HARVESTER_GROUP", "USER");
        }

        User user = userManagementFacade.getUser("admin");

        if (user == null) {
            userManagementFacade.registerUser("admin", "admin", "Admin user", "admin@example.org");
            userManagementFacade.addUserToGroup("admin", "ADMIN_GROUP");
            userManagementFacade.addUserToGroup("admin", "HARVESTER_GROUP");
        }

        // create the anonymous user

        Role role = userManagementFacade.getRole("ANONYMOUS");

        if (role == null) {
            userManagementFacade.createRole("ANONYMOUS", "Anonymous User", "The great unwashed");
        }

    }


    private UserManagementFacade userManagementFacade;
}
