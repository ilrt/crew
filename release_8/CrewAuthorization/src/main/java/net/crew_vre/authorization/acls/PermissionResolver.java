package net.crew_vre.authorization.acls;

import net.crew_vre.authorization.Permission;

/**
 * <p>The PermissionResolver determines if an authority (role) has the right to perform an
 * operation on a graph.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public interface PermissionResolver {

    boolean authorityHasPermissionForGraph(String graph, String authority,
                                                  Permission permission);
}
