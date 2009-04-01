package net.crew_vre.authorization.acls.impl;

import net.crew_vre.authorization.acls.AclLookupManager;
import net.crew_vre.authorization.acls.GraphAclManager;
import net.crew_vre.authorization.acls.GraphAcl;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: AclLookupManagerImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class AclLookupManagerImpl implements AclLookupManager {

    public AclLookupManagerImpl(GraphAclManager graphAclManager) {
        this.graphAclManager = graphAclManager;
    }

    public GraphAcl lookupAcl(String graph, String authority) {
        return graphAclManager.findAcl(graph, authority);
    }

    private GraphAclManager graphAclManager;
}
