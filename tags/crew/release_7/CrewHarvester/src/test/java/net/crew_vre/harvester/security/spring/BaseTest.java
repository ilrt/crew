package net.crew_vre.harvester.security.spring;

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.providers.TestingAuthenticationToken;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: BaseTest.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public abstract class BaseTest {

    final String GRAPH_ONE = "http://example.org/graph1/";
    final String GRAPH_TWO = "http://example.org/graph2/";

    final String AUTHENTICATED_UID = "fred";
    final String HARVESTER_USER_ONE_UID = "h1";
    final String HARVESTER_USER_TWO_UID = "h1";

    final String AUTHENTICATED_ROLE = "AUTHENTICATED";
    final String HARVESTER_GROUP = "Harvester_Group";
    final String HARVESTER_GROUP_ONE = "Harvester_Group_one";
    final String HARVESTER_GROUP_TWO = "Harvester_Group_Two";

    public final Authentication AUTHENTICATED_USER =
            new TestingAuthenticationToken(AUTHENTICATED_UID, "secret",
                    new GrantedAuthority[]{new GrantedAuthorityImpl(AUTHENTICATED_ROLE)});

    final Authentication HARVESTER_USER_ONE =
            new TestingAuthenticationToken(HARVESTER_USER_ONE_UID, "secret",
                    new GrantedAuthority[]{new GrantedAuthorityImpl(HARVESTER_GROUP),
                            new GrantedAuthorityImpl(HARVESTER_GROUP_ONE)});

    final Authentication HARVESTER_USER_TWO =
            new TestingAuthenticationToken(HARVESTER_USER_TWO_UID, "secret",
                    new GrantedAuthority[]{new GrantedAuthorityImpl(HARVESTER_GROUP),
                            new GrantedAuthorityImpl(HARVESTER_GROUP_TWO)});
}
