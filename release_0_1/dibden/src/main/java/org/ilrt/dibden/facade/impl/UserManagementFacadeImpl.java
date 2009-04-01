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
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import org.ilrt.dibden.HashUtility;
import org.ilrt.dibden.Role;
import org.ilrt.dibden.User;
import org.ilrt.dibden.dao.RoleDao;
import org.ilrt.dibden.dao.UserDao;
import org.ilrt.dibden.facade.UserManagementFacade;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: UserManagementFacadeImpl.java 11 2008-06-04 10:41:37Z cmmaj $
 *
 **/
public class UserManagementFacadeImpl implements UserManagementFacade {

    public UserManagementFacadeImpl(UserDao userDao, RoleDao roleDao, String digestAlgorthm,
                                    MailSender mailSender, SimpleMailMessage resetMessage) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.digestAlgorithm = digestAlgorthm;
        this.mailSender = mailSender;
        this.resetMessage = resetMessage;
    }

    public boolean isUsernameRegistered(String username) {
        return userDao.findUser(username) != null;
    }

    public boolean isEmailRegistered(String email) {
        return userDao.findUserByEmail(email) != null;
    }

    public User registerUser(String username, String password, String name, String email) {

        String passwd = HashUtility.generateHash(password, digestAlgorithm);

        Role role = roleDao.findGroup("USERS");
        return userDao.createUser(username, passwd, name, email, role);
    }

    public boolean validatePassword(String username, String password) {

        String passwd = HashUtility.generateHash(password, digestAlgorithm);
        User user = userDao.findUser(username);
        return user != null && user.getPassword().equals(passwd);
    }

    public void updatePassword(String username, String password) {

        String passwd = HashUtility.generateHash(password, digestAlgorithm);
        User user = userDao.findUser(username);
        user.setPassword(passwd);
        userDao.updateUser(user);
    }

    public User getUser(String username) {
        return userDao.findUser(username);
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


    private UserDao userDao;
    private RoleDao roleDao;
    private String digestAlgorithm;
    private MailSender mailSender;
    private SimpleMailMessage resetMessage;
}
