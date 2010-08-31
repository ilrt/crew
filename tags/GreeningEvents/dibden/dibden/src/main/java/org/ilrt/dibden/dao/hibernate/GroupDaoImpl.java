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
import org.ilrt.dibden.dao.GroupDao;
import org.ilrt.dibden.domain.Group;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class GroupDaoImpl extends HibernateDaoSupport implements GroupDao {

    public GroupDaoImpl(HibernateTemplate hibernateTemplate) {
        setHibernateTemplate(hibernateTemplate);
    }

    public Group createGroup(String groupId, String name, String description) {

        Group group = new Group(groupId, name, description);
        this.getHibernateTemplate().save(group);
        return group;
    }

    public Group findGroup(String groupId) {

        List results =
                this.getHibernateTemplate().find("from Group g where g.groupId = ?", groupId);

        Group group = null;
        if (results.size() == 1) {
            group = (Group) results.get(0);
        }

        return group;
    }

    public void updateGroup(Group group) {
        getHibernateTemplate().saveOrUpdate(group);
    }

    public void deleteGroup(String groupId) {

        Group group = findGroup(groupId);

        if (group != null) {
            this.getHibernateTemplate().delete(group);
        }
    }

    public List<Group> findAll() {
        return this.getHibernateTemplate().find("from Group");
    }

    public List<Group> findAll(int first, int max) {

        Query query = getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createQuery("FROM Group ORDER BY groupId");
        query.setFirstResult(first);
        query.setMaxResults(max);

        return query.list();
    }
}
