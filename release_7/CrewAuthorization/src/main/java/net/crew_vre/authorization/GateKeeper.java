package net.crew_vre.authorization;

import net.crew_vre.authorization.Permission;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: GateKeeper.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface GateKeeper {

    boolean userHasPermissionFor(Object user, Permission permission, String graph);

}
