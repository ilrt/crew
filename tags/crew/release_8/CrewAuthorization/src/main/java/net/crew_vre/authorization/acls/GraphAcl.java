package net.crew_vre.authorization.acls;

import net.crew_vre.authorization.acls.impl.GraphAclEntryImpl;

import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: GraphAcl.java 1092 2009-03-11 19:01:38Z cmmaj $
 */
public interface GraphAcl {

    Long getId();

    String getGraph();

    String getAuthority();

    List<GraphAclEntry> getEntries();

}
