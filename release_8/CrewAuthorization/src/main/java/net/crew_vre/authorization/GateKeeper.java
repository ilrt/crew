package net.crew_vre.authorization;

import net.crew_vre.authorization.Permission;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: GateKeeper.java 1092 2009-03-11 19:01:38Z cmmaj $
 */
public interface GateKeeper {

    boolean userHasPermissionFor(Object user, Permission permission, String graph);

}
