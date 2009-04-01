package net.crew_vre.authorization.acls;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: GraphAclEntry.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface GraphAclEntry {

    Long getId();

    int getPermission();

    GraphAcl getGraphAcl();

}
