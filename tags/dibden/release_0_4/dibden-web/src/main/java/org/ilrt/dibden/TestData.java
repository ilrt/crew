package org.ilrt.dibden;

import org.ilrt.dibden.facade.UserManagementFacade;

public class TestData {

    public TestData(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }

    public void loadData() {

        userManagementFacade.registerUser("admin", "admin", "The default admin user",
                "admin@example.org");
        userManagementFacade.addUserToGroup("admin", "ADMIN_GROUP");
        userManagementFacade.addUserToGroup("admin", "USER_GROUP");

        userManagementFacade.registerUser("testuser", "testuser", "A test user",
                "testuser@example.org");
        userManagementFacade.addUserToGroup("testuser", "USER_GROUP");

    }

    private UserManagementFacade userManagementFacade;
}
