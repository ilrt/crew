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

import org.ilrt.dibden.Role;
import org.ilrt.dibden.dao.RoleDao;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.hibernate.Query;

import java.util.List;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: RoleDaoImpl.java 89 2008-09-09 14:48:05Z cmmaj $
 *
 **/
public class RoleDaoImpl extends HibernateDaoSupport implements RoleDao {

    public RoleDaoImpl(HibernateTemplate hibernateTemplate) {
        setHibernateTemplate(hibernateTemplate);
    }

    public Role createRole(String roleId, String name, String description) {

        Role group = new Role(roleId, name, description);

        this.getHibernateTemplate().save(group);

        return group;
    }

    public Role findRole(String roleId) {
        List results =
                this.getHibernateTemplate().find("from Role g where g.roleId = ?", roleId);

        Role group = null;
        if (results.size() == 1) {
            group = (Role) results.get(0);
        }

        return group;
    }

    public void updateRole(Role role) {

        this.getHibernateTemplate().update(role);
    }

    public void deleteRole(String roleId) {

        Role role = findRole(roleId);

        if (role != null) {
            this.getHibernateTemplate().delete(role);
        }


    }

    @SuppressWarnings("unchecked")
    public List<Role> findAll() {
        return this.getHibernateTemplate().find("from Role");
    }

    public List<Role> findAll(int first, int max) {

        Query query = getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createQuery("FROM Role ORDER BY roleId");
        query.setFirstResult(first);
        query.setMaxResults(max);

        return query.list();
    }

}
