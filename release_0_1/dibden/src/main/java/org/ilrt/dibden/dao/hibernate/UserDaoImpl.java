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

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import org.ilrt.dibden.Role;
import org.ilrt.dibden.User;
import org.ilrt.dibden.dao.UserDao;

import java.util.Date;
import java.util.List;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: UserDaoImpl.java 11 2008-06-04 10:41:37Z cmmaj $
 *
 **/
public class UserDaoImpl extends HibernateDaoSupport implements UserDao {

    public UserDaoImpl(HibernateTemplate hibernateTemplate) {
        setHibernateTemplate(hibernateTemplate);
    }

    public User findUser(String username) {

        List results =
                this.getHibernateTemplate().find("from User u where u.username = ?", username);

        User user = null;
        if (results.size() == 1) {
            user = (User) results.get(0);
        }

        return user;
    }

    public User findUserByEmail(String email) {

        List results = this.getHibernateTemplate().find("from User u where u.email = ?", email);

        User user = null;
        if (results.size() == 1) {
            user = (User) results.get(0);
        }

        return user;
    }

    public User createUser(String username, String password, String name, String email) {

        User user = new User(username, password, email, name, new Date());

        this.getHibernateTemplate().save(user);

        return user;
    }

    public User createUser(String username, String password, String name, String email, Role role) {

        User user = new User(username, password, email, name, new Date());
        user.getRoles().add(role);

        this.getHibernateTemplate().save(user);

        return user;
    }

    public void updateUser(User user) {
        System.out.println(">>>>? " + user.getPassword());
        this.getHibernateTemplate().saveOrUpdate(user);
    }

    public void deleteUser(User user) {
        this.getHibernateTemplate().delete(user);
    }

    @SuppressWarnings("unchecked")
    public List<User> findAll() {
        return this.getHibernateTemplate().find("from User");
    }


}
