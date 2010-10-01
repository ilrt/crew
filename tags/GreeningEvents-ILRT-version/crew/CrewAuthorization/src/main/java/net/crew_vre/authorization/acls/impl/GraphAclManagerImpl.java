/**
 * Copyright (c) 2008-2009 University of Bristol
 * Copyright (c) 2008-2009 University of Manchester
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
package net.crew_vre.authorization.acls.impl;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

import net.crew_vre.authorization.acls.impl.GraphAclImpl;
import net.crew_vre.authorization.acls.GraphAclManager;
import net.crew_vre.authorization.acls.GraphAcl;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: GraphAclManagerImpl.java 1186 2009-03-31 12:37:17Z cmmaj $
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
