package net.crew_vre.authorization.acls;

import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: GraphAclManager.java 1068 2009-03-03 17:24:37Z cmmaj $
 */
public interface GraphAclManager {

    GraphAcl createGraphAcl(String graph, String authority);

    GraphAcl findAcl(String graph, String authority);

    List<GraphAcl> findAcls(String graph);

    void updateAcl(GraphAcl acl);

    void deleteAcl(GraphAcl acl);
}
