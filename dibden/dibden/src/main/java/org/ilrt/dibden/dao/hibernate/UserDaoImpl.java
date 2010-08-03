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
package org.ilrt.dibden.dao.hibernate;

import org.hibernate.Query;
import org.ilrt.dibden.dao.UserDao;
import org.ilrt.dibden.domain.Group;
import org.ilrt.dibden.domain.User;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.Date;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: UserDaoImpl.java 128 2009-03-31 14:09:42Z cmmaj $
 */
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

    public User createUser(String username, String password, String name, String postcode,
            String email, boolean active) {

        User user = new User(username, password, email, name, postcode, new Date(), active);
        this.getHibernateTemplate().save(user);
        return user;
    }

    public User createUser(String username, String password, String name, String postcode, 
            String email, boolean active, Group group) {

        User user = new User(username, password, email, name, postcode, new Date(), active);
        user.getGroups().add(group);
        this.getHibernateTemplate().save(user);
        return user;
    }

    public void updateUser(User user) {
        this.getHibernateTemplate().saveOrUpdate(user);
    }

    public void deleteUser(String username) {
        User user = findUser(username);
        this.getHibernateTemplate().delete(user);
    }

    @SuppressWarnings("unchecked")
    public List<User> findAll() {
        return this.getHibernateTemplate().find("from User");
    }

    @SuppressWarnings("unchecked")
    public List<User> findAll(int first, int max) {

        Query query = getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createQuery("FROM User ORDER BY username");
        query.setFirstResult(first);
        query.setMaxResults(max);

        return query.list();
    }


}
