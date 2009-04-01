package net.crew_vre.authorization.acls;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: GraphAclEntry.java 1092 2009-03-11 19:01:38Z cmmaj $
 */
public interface GraphAclEntry {

    Long getId();

    int getPermission();

    GraphAcl getGraphAcl();

}
