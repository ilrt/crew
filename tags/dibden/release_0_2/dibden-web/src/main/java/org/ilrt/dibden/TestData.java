package org.ilrt.dibden;

import org.ilrt.dibden.facade.UserManagementFacade;

public class TestData {

    public TestData(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }

    public void loadData() {

        userManagementFacade.createRole("GOOD_GUYS", "Good guys", "The good guys");
        userManagementFacade.createRole("BAD_GUYS", "Bad guys", "The bad guys");
        userManagementFacade.createRole("UGLY_GUYS", "Ugly guys", "The ugly guys");

        userManagementFacade.registerUser("admin", "admin", "The default admin user",
                "admin@example.org");
        userManagementFacade.registerUser("fsmith", "secret123", "Fred Smith",
                "fred.smith@example.org");
        userManagementFacade.registerUser("jbloggs", "secret123", "Joe Bloggs",
                "joe.bloggs@example.org");
        userManagementFacade.registerUser("jdoe", "secret123", "John Doe",
                "john.doe@example.org");
        userManagementFacade.registerUser("dwho", "secret123", "Dr Who",
                "dr.who@example.org");
        userManagementFacade.registerUser("mjones", "secret123", "Martha Jones",
                "martha.jones@example.org");
        userManagementFacade.registerUser("rtyler", "secret123", "Rose Tyler",
                "rose.tyler@example.org");
        userManagementFacade.registerUser("dnoble", "secret123", "Donna Noble",
                "donna.noble@example.org");
        userManagementFacade.registerUser("jtyler", "secret123", "Jackie Tyler",
                "jackie.tyler@example.org");
        userManagementFacade.registerUser("msmith", "secret123", "Mikey Smith",
                "mikey.smith@example.org");
        userManagementFacade.registerUser("davros", "secret123", "Davros",
                "davros@example.org");
        userManagementFacade.registerUser("jharkness", "secret123", "Jack Harkness",
                "jack.harkness@example.org");
        userManagementFacade.registerUser("sjsmith", "secret123", "Sarah Jane Smith",
                "sarah.jane.smith@example.org");
        userManagementFacade.registerUser("themaster", "secret123", "The Master",
                "the.master@example.org");
        userManagementFacade.registerUser("gcooper", "secret123", "Gwen Cooper",
                "gwen.cooper@example.org");

        userManagementFacade.addRoleToUser("gcooper", "GOOD_GUYS");
        userManagementFacade.addRoleToUser("admin", "ADMIN");


    }

    private UserManagementFacade userManagementFacade;
}
