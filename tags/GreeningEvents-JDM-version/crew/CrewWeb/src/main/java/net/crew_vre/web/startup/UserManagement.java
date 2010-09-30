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
            userManagementFacade.registerUser("admin", "admin", "Admin user", "admin@example.org", "");
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
