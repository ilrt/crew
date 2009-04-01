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
package org.ilrt.dibden.facade.impl;

import org.apache.commons.lang.RandomStringUtils;
import org.ilrt.dibden.HashUtility;
import org.ilrt.dibden.dao.GroupDao;
import org.ilrt.dibden.dao.RoleDao;
import org.ilrt.dibden.dao.UserDao;
import org.ilrt.dibden.domain.Group;
import org.ilrt.dibden.domain.Role;
import org.ilrt.dibden.domain.User;
import org.ilrt.dibden.facade.UserManagementFacade;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: UserManagementFacadeImpl.java 123 2008-11-22 14:42:33Z cmmaj $
 */
public class UserManagementFacadeImpl implements UserManagementFacade {

    public UserManagementFacadeImpl(UserDao userDao, RoleDao roleDao, GroupDao groupDao,
                                    String digestAlgorthm, MailSender mailSender,
                                    SimpleMailMessage resetMessage) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.groupDao = groupDao;
        this.digestAlgorithm = digestAlgorthm;
        this.mailSender = mailSender;
        this.resetMessage = resetMessage;
    }

    // ---------- move below to a UserRegistrationFacade ???


    public boolean isUsernameRegistered(String username) {
        return userDao.findUser(username) != null;
    }

    public boolean isEmailRegistered(String email) {
        return userDao.findUserByEmail(email) != null;
    }

    public User registerUser(String username, String password, String name, String email) {

        String passwd = HashUtility.generateHash(password, digestAlgorithm);

        Group group = groupDao.findGroup("USER_GROUP");
        return userDao.createUser(username, passwd, name, email, true, group);
    }

    public boolean validatePassword(String username, String password) {

        String passwd = HashUtility.generateHash(password, digestAlgorithm);
        User user = userDao.findUser(username);
        return user != null && user.getPassword().equals(passwd);
    }

    // ---------- keep in this facade ???

    public void updatePassword(String username, String password) {

        String passwd = HashUtility.generateHash(password, digestAlgorithm);
        User user = userDao.findUser(username);
        user.setPassword(passwd);
        userDao.updateUser(user);
    }

    public User getUser(String username) {
        return userDao.findUser(username);
    }

    public User getUserByEmail(String email) {
        return userDao.findUserByEmail(email);
    }

    public void generateNewPassword(String email) {

        User user = userDao.findUserByEmail(email);

        if (user != null) {

            // generate password
            String password = RandomStringUtils.randomAlphanumeric(8);

            // update in the database
            updatePassword(user.getUsername(), password);

            // mail the password
            SimpleMailMessage msg = new SimpleMailMessage(resetMessage);
            msg.setTo(user.getEmail());

            StringBuffer buffer = new StringBuffer("... well, never mind.");
            buffer.append("We have reset the password and your new one is below...\n\n\n")
                    .append("Password: ").append(password).append("\n\n\n")
                    .append("Please login and change your password");

            msg.setText(buffer.toString());
            mailSender.send(msg);
        }

    }

    public List<User> getUsers() {
        return userDao.findAll();
    }

    public List<User> getUsers(int start, int max) {
        return userDao.findAll(start, max);
    }

    public int totalUsers() {
        return userDao.findAll().size();
    }

    public void removeUser(String username) {
        userDao.deleteUser(username);
    }

    public void updateUser(String username, String name, String email) {

        User user = userDao.findUser(username);

        if (user != null) {
            user.setName(name);
            user.setEmail(email);
            userDao.updateUser(user);
        }
    }

    public Role createRole(String roleId, String name, String description) {
        return roleDao.createRole(roleId, name, description);
    }

    public List<Role> getRoles() {
        return roleDao.findAll();
    }

    public Role getRole(String roleId) {
        return roleDao.findRole(roleId);
    }


    public List<Role> getRoles(int start, int max) {
        return roleDao.findAll(start, max);
    }

    public int totalRoles() {
        return roleDao.findAll().size();
    }

    public void removeRole(String roleId) {
        roleDao.deleteRole(roleId);
    }

    public void addRoleToGroup(String groupId, String roleId) {

        Group group = groupDao.findGroup(groupId);

        Role role = roleDao.findRole(roleId);

        group.getRoles().add(role);
        groupDao.updateGroup(group);
    }

    public void updateRole(String roleId, String name, String description) {

        Role role = roleDao.findRole(roleId);

        if (role != null) {
            role.setName(name);
            role.setDescription(description);
            roleDao.updateRole(role);
        }
    }

    public void removeRoleFromGroup(String roleId, String groupId) {

        Role role = roleDao.findRole(roleId);
        Group group = groupDao.findGroup(groupId);
        group.getRoles().remove(role);
        groupDao.updateGroup(group);
    }

    public Group createGroup(String groupId, String name, String description) {

        return groupDao.createGroup(groupId, name, description);
    }

    public void updateGroup(String groupId, String name, String description) {

        Group group = groupDao.findGroup(groupId);

        if (group != null) {
            group.setName(name);
            group.setDescription(description);
            groupDao.updateGroup(group);
        }

    }

    public void removeGroup(String groupId) {
        groupDao.deleteGroup(groupId);
    }

    public Group getGroup(String groupId) {
        return groupDao.findGroup(groupId);
    }

    public List<Group> getGroups() {
        return groupDao.findAll();
    }

    public List<Group> getGroups(int start, int max) {
        return groupDao.findAll(start, max);
    }

    public int totalGroups() {
        return groupDao.findAll().size();
    }

    public void addUserToGroup(String username, String groupId) {

        User user = userDao.findUser(username);
        Group group = groupDao.findGroup(groupId);
        user.getGroups().add(group);
        userDao.updateUser(user);
    }

    public void removeUserFromGroup(String username, String groupId) {

        User user = userDao.findUser(username);
        Group group = groupDao.findGroup(groupId);
        user.getGroups().remove(group);
        userDao.updateUser(user);

    }

    private UserDao userDao;
    private RoleDao roleDao;
    private GroupDao groupDao;
    private String digestAlgorithm;
    private MailSender mailSender;
    private SimpleMailMessage resetMessage;
}
