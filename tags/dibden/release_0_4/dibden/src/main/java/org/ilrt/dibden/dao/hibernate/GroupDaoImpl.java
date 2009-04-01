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
