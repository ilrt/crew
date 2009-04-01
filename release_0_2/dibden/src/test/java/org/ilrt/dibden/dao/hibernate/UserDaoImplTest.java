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
package org.ilrt.dibden.dao.hibernate;

import org.ilrt.dibden.HashUtility;
import org.ilrt.dibden.User;
import org.ilrt.dibden.dao.UserDao;
import org.junit.Test;

import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: UserDaoImplTest.java 92 2008-09-09 14:51:02Z cmmaj $
 */
public class UserDaoImplTest extends AbstractDaoImplTest {

    // auto wiring adds dependency
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Test
    public void testCreateUser() {

        String query = "SELECT COUNT(*) FROM USERS WHERE USERNAME = 'jdoe'";

        // test that user is not already in the database
        assertEquals("The user already exists in the database", 0, jdbcTemplate.queryForInt(query));

        // create the user
        userDao.createUser("jdoe", HashUtility.generateHash("wh0am1", "MD5"),
                "John Doe", "john.doe@example.org", true);

        // flush the session (write to db) so we can test with JdbcTemplate
        flush();

        // test the insert was successful
        assertEquals("The user does not exist in the database", 1,
                jdbcTemplate.queryForInt(query));

    }

    @Test
    public void testUpdateUser() {

        assertEquals("The user is not in the database", 1, jdbcTemplate.queryForInt(
                "SELECT COUNT(*) FROM USERS WHERE USERNAME = 'ghunt'"));

        User user = userDao.findUser("ghunt");
        user.setPassword(HashUtility.generateHash("changed", "MD5"));
        userDao.updateUser(user);

        flush();

        assertEquals("The password has not changed", "8977dfac2f8e04cb96e66882235f5aba",
                jdbcTemplate.queryForObject("SELECT PASSWORD FROM USERS WHERE " +
                        "USERNAME = 'ghunt'", java.lang.String.class));
    }

    @Test
    public void testFindAll() {

        List<User> results = userDao.findAll();
        assertEquals("There should be five users", 5, results.size());
    }

    @Test
    public void testFindUser() {

        User user = userDao.findUser("styler");
        assertNotNull("The user is not found", user);
        assertEquals("The name should be Sam Tyler", "Sam Tyler", user.getName());

    }

    @Test
    public void testFindUserByEmail() {

        User user = userDao.findUserByEmail("s.tyler@example.org");
        assertNotNull("The user is not found", user);
        assertEquals("The name should be Sam Tyler", "Sam Tyler", user.getName());

    }

    @Test
    public void testDeleteUser() {

        String query = "SELECT COUNT(*) FROM USERS WHERE USERNAME = 'styler'";

        // test that user is in the database
        assertEquals("The user does not exists in the database", 1,
                jdbcTemplate.queryForInt(query));

        userDao.deleteUser("styler");

        flush();

        // test that user is no longer in the database
        assertEquals("The user exists in the database", 0,
                jdbcTemplate.queryForInt(query));

    }

    private UserDao userDao;
}
