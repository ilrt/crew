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

import org.ilrt.dibden.domain.Role;
import org.ilrt.dibden.dao.RoleDao;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.hibernate.Query;

import java.util.List;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: RoleDaoImpl.java 128 2009-03-31 14:09:42Z cmmaj $
 *
 **/
public class RoleDaoImpl extends HibernateDaoSupport implements RoleDao {

    public RoleDaoImpl(HibernateTemplate hibernateTemplate) {
        setHibernateTemplate(hibernateTemplate);
    }

    public Role createRole(String roleId, String name, String description) {

        Role role = new Role(roleId, name, description);

        this.getHibernateTemplate().save(role);

        return role;
    }

    public Role findRole(String roleId) {
        List results =
                this.getHibernateTemplate().find("from Role r where r.roleId = ?", roleId);

        Role role = null;
        if (results.size() == 1) {
            role = (Role) results.get(0);
        }

        return role;
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
