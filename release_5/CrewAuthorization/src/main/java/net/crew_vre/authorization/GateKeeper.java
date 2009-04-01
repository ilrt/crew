package net.crew_vre.authorization;

/**
 * <p>The GateKeeper determines if a user has the rights to perform an operation on
 * a graph.</p>
 *
 * @author Damian Steer (d.steer@bristol.ac.uk)
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 *
 * @version $Id: GateKeeper.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
interface GateKeeper {

    boolean userHasPermissionForGraph(User user, Permission permission, Graph graph);

}
