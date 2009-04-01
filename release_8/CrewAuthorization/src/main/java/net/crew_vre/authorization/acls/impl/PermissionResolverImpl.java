package net.crew_vre.authorization.acls.impl;

import net.crew_vre.authorization.acls.AclLookupManager;
import net.crew_vre.authorization.acls.GraphAcl;
import net.crew_vre.authorization.acls.GraphAclEntry;
import net.crew_vre.authorization.Permission;
import net.crew_vre.authorization.acls.PermissionResolver;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: PermissionResolverImpl.java 1095 2009-03-12 17:41:11Z cmmaj $
 */
public class PermissionResolverImpl implements PermissionResolver {

    public PermissionResolverImpl(AclLookupManager aclLookupManager) {
        this.aclLookupManager = aclLookupManager;
    }

    public boolean authorityHasPermissionForGraph(String graph, String authority,
                                                  Permission permission) {

        // ROLE_ might be prefixed for the spring role voter - remove it for checking the
        // with the database
        if (authority.startsWith(ROLE_PREFIX)) {
            authority = authority.substring(ROLE_PREFIX.length(), authority.length());
        }
        
        GraphAcl graphAcl = aclLookupManager.lookupAcl(graph, authority);

        if (graphAcl != null) {
            for (GraphAclEntry entry : graphAcl.getEntries()) {
                if (permission.intValue() == entry.getPermission()) {
                    return true;
                }
            }
        }

        return false;
    }

    private final AclLookupManager aclLookupManager;
    private final String ROLE_PREFIX = "ROLE_";
}
