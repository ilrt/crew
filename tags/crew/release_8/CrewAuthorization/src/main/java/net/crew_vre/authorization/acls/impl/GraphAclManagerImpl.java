package net.crew_vre.authorization.acls.impl;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

import net.crew_vre.authorization.acls.impl.GraphAclImpl;
import net.crew_vre.authorization.acls.GraphAclManager;
import net.crew_vre.authorization.acls.GraphAcl;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: GraphAclManagerImpl.java 1092 2009-03-11 19:01:38Z cmmaj $
 */
public class GraphAclManagerImpl extends HibernateDaoSupport implements GraphAclManager {

    public GraphAclManagerImpl(HibernateTemplate hibernateTemplate) {
        super.setHibernateTemplate(hibernateTemplate);
    }

    public GraphAcl createGraphAcl(String graph, String authority) {

        GraphAcl acl = new GraphAclImpl(graph, authority);
        getHibernateTemplate().save(acl);
        getHibernateTemplate().flush();
        return acl;
    }

    public GraphAcl findAcl(String graph, String authority) {

        Object[] params = {graph, authority };

        GraphAcl graphAcl = null;

        List results =
                getHibernateTemplate().find("from GraphAclImpl where graph = ? and authority = ?",
                        params);

        if (results.size() == 1) {
            graphAcl = (GraphAcl) results.get(0);
        }

        return  graphAcl;
    }

    @SuppressWarnings("unchecked")    
    public List<GraphAcl> findAcls(String graph) {
        return getHibernateTemplate().find("from GraphAclImpl where graph = ?", graph);
    }


    public void updateAcl(GraphAcl acl) {
        getHibernateTemplate().saveOrUpdate(acl);
        getHibernateTemplate().flush();
    }


    public void deleteAcl(GraphAcl acl) {
        getHibernateTemplate().delete(acl);
        getHibernateTemplate().flush();
    }

}
