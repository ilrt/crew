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
package org.ilrt.dibden.facade;

import org.ilrt.dibden.HashUtility;
import org.ilrt.dibden.dao.GroupDao;
import org.ilrt.dibden.dao.RoleDao;
import org.ilrt.dibden.dao.UserDao;
import org.ilrt.dibden.dao.hibernate.AbstractDaoImplTest;
import org.ilrt.dibden.domain.Group;
import org.ilrt.dibden.domain.Role;
import org.ilrt.dibden.domain.User;
import org.junit.Test;

import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: UserManagementFacadeTest.java 128 2009-03-31 14:09:42Z cmmaj $
 */
public class UserManagementFacadeTest extends AbstractDaoImplTest {

    public void setUserManagementFacade(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public void setGroupDao(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    @Test
    public void testIsUsernameRegistered() {

        assertFalse(userManagementFacade.isUsernameRegistered("flipper"));
    }

    @Test
    public void testIsUsernameRegisteredExists() {

        assertTrue(userManagementFacade.isUsernameRegistered("ghunt"));
    }

    @Test
    public void testIsEmailRegistered() {

        assertFalse(userManagementFacade.isEmailRegistered("flipper@example.org"));
    }

    @Test
    public void testIsEmailRegistered_Exists() {

        assertFalse(userManagementFacade.isEmailRegistered("ghunt@example.org"));
    }

    @Test
    public void testRegisterUser() {

        String password = "secret";

        User user = userManagementFacade.registerUser("mjones", password, "M Jones",
                "m.jones@example.org", "TQ14 8PT");

        assertNotNull("The user is null", user);
        assertEquals("Incorrect password hash", HashUtility.generateHash(password, "md5"),
                user.getPassword());
        assertTrue("There should be 1 group", user.getGroups().size() == 1);
    }

    @Test
    public void testValidatePassword() {

        // create the user
        String password = "secret";

        User user = userManagementFacade.registerUser("mjones", password, "M Jones",
                "m.jones@example.org", "TQ14 8PT");

        // make sure the user is in the database
        assertTrue("The user doesn't exist", userManagementFacade.isUsernameRegistered(user.getUsername()));

        assertTrue("The password doesn't validate", userManagementFacade.validatePassword(user.getUsername(),
                password));

    }

    @Test
    public void testUpdatePassword() {

        // get a user and old password
        User user = userDao.findUser("ghunt");
        String oldPassword = user.getPassword();

        String newPassword = "newpassword";

        // update password
        userManagementFacade.updatePassword("ghunt", newPassword);

        // get the user again
        user = userDao.findUser("ghunt");

        assertNotSame("The passwords are the same", oldPassword, user.getPassword());
        assertEquals("Incorrect password hash", HashUtility.generateHash(newPassword, "md5"),
                user.getPassword());

    }

    @Test
    public void testGetUser() {

        User user = userManagementFacade.getUser("ghunt");

        assertNotNull("The user is null", user);
        assertEquals("Incorrect username", "ghunt", user.getUsername());

    }

    @Test
    public void testGenerateNewPassword() {

        // get the user
        User user = userDao.findUser("ghunt");
        String oldPassword = user.getPassword();

        // generate a new password
        userManagementFacade.generateNewPassword("g.hunt@example.org");

        // get the user again
        user = userDao.findUser("ghunt");

        assertNotSame("The passwords are the same", oldPassword, user.getPassword());

    }

    @Test
    public void testFindUsers() {

        List<User> results = userManagementFacade.getUsers();

        assertEquals("There should be 5 users", 5, results.size());
    }

    @Test
    public void testFindUsersWithOffset() {

        List<User> results = userManagementFacade.getUsers(0, 2);

        assertEquals("There should be 2 users", 2, results.size());
    }

    @Test
    public void testTotalUsers() {

        assertEquals("There should be 5 users", 5, userManagementFacade.totalUsers());
    }

    @Test
    public void testRemoveUser() {

        User user = userDao.findUser("ghunt");

        assertNotNull("The user is null", user);

        userManagementFacade.removeUser("ghunt");

        user = userDao.findUser("ghunt");

        assertNull("The user is not null", user);
    }

    @Test
    public void testUpdateUser() {

        userManagementFacade.updateUser("ghunt", "changed", "ghunt@test.org", "BS8 1HH");

        User user = userDao.findUser("ghunt");

        assertEquals("The name hasn't changed", "changed", user.getName());
        assertEquals("The email hasn't changed", "ghunt@test.org", user.getEmail());
        assertEquals("The post code hasn't changed", "BS8 1HH", user.getPostcode());
    }

    @Test
    public void testCreateRole() {

        Role role = userManagementFacade.createRole("TESTROLE", "Test", "Test");

        assertNotNull("The role is null", role);
    }

    @Test
    public void testGetRole() {

        Role role = userManagementFacade.getRole("ACADEMIC");
        assertNotNull("The role is null", role);
    }

    @Test
    public void testGetRoles() {

        List<Role> results = userManagementFacade.getRoles();

        assertEquals("There should be 3 roles", 3, results.size());

    }

    @Test
    public void testGetRolesWithOffset() {

        List<Role> results = userManagementFacade.getRoles(0, 2);

        assertEquals("There should be 2 roles", 2, results.size());
    }

    @Test
    public void testTotalRoles() {
        assertEquals("There should be 3 roles", 3, userManagementFacade.totalRoles());
    }

    @Test
    public void testRemoveRole() {

        Role role = roleDao.findRole("ACADEMIC");

        assertNotNull("The role is null", role);

        userManagementFacade.removeRole("ACADEMIC");

        role = roleDao.findRole("ACADEMIC");

        assertNull("The role is not null", role);
    }

    @Test
    public void testAddRoleToGroup() {

        userManagementFacade.addRoleToGroup("ILRT_GROUP", "ACADEMIC");

        Group group = groupDao.findGroup("ILRT_GROUP");

        assertEquals("There should be 1 role", 1, group.getRoles().size());

    }

    @Test
    public void testUpdateRole() {

        userManagementFacade.updateRole("ACADEMIC", "changed", "changed");

        Role role = roleDao.findRole("ACADEMIC");

        assertEquals("The name hasn't changed", "changed", role.getName());
        assertEquals("The description hasn't changed", "changed", role.getDescription());
    }

    @Test
    public void testRemoveRoleFromGroup() {

        assertEquals("There should be 1 role", 1, groupDao.findGroup("USER_GROUP").getRoles().size());

        userManagementFacade.removeRoleFromGroup("USER", "USER_GROUP");

        assertEquals("There should be 0 roles", 0, groupDao.findGroup("USER_GROUP").getRoles().size());
    }

    @Test
    public void testCreateGroup() {

        Group group = groupDao.findGroup("NEW_GROUP");

        assertNull("The group already exists", group);

        userManagementFacade.createGroup("NEW_GROUP", "new group", "new group");

        group = groupDao.findGroup("NEW_GROUP");

        assertNotNull("The group wasn't created", group);
    }


    @Test
    public void testUpdateGroup() {

        userManagementFacade.updateGroup("ILRT_GROUP", "changed", "changed");

        Group group = groupDao.findGroup("ILRT_GROUP");

        assertEquals("The name hasn't changed", "changed", group.getName());
        assertEquals("The description hasn't changed", "changed", group.getDescription());

    }

    @Test
    public void testRemoveGroup() {

        Group group = groupDao.findGroup("ILRT_GROUP");

        assertNotNull("The group doesn't exist", group);

        userManagementFacade.removeGroup("ILRT_GROUP");

        group = groupDao.findGroup("ILRT_GROUP");

        assertNull("The group wasn't deleted", group);

    }

    @Test
    public void testGetGroups() {

        List<Group> results = userManagementFacade.getGroups();

        assertEquals("There should be 3 groups", 3, results.size());
    }

    @Test
    public void testGetGroupsOffset() {

        List<Group> results = userManagementFacade.getGroups(0, 2);

        assertEquals("There should be 2 groups", 2, results.size());
    }

    @Test
    public void testTotalGroups() {

        assertEquals("There should be 3 groups", 3, userManagementFacade.totalGroups());
    }

    /**
    @Test
    public void testAddUserToGroup() {

        userManagementFacade.addUserToGroup("ghunt", "ILRT_GROUP");

        Group group = groupDao.findGroup("ILRT_GROUP");

        assertEquals("There should be 1 member", 1, group.getGroupMembers().size());

    }

    @Test
    public void testRemoveUserFromGroup() {

        assertTrue(true);

        userManagementFacade.removeUserFromGroup("ghunt", "ADMIN_GROUP");

    }
**/
    private UserManagementFacade userManagementFacade;
    private UserDao userDao;
    private RoleDao roleDao;
    private GroupDao groupDao;
}
