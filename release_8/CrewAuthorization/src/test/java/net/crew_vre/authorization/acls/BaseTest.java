package net.crew_vre.authorization.acls;

import net.crew_vre.events.domain.Event;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.providers.TestingAuthenticationToken;


public abstract class BaseTest {

    // graph URIs
    public final String GRAPH_ONE = "http://example.org/graph1/";
    public final String GRAPH_TWO = "http://example.org/graph2/";
    public final String GRAPH_THREE = "http://example.org/graph3/";

    // event URIs
    public final String EVENT_ONE = "http://example.org/event/1/";
    public final String EVENT_TWO = "http://example.org/event/2/";
    public final String EVENT_THREE = "http://example.org/event/3/";

    // roles
    public final String ANONYMOUS_ROLE = "ANONYMOUS";
    public final String AUTHENTICATED_ROLE = "AUTHENTICATED";
    public final String ADMIN_ROLE = "ADMIN";

    // uids
    public final String ANONYMOUS_UID = "anonymous";
    public final String AUTHENTICATED_UID = "fred";
    public final String ADMIN_UID = "admin";

    // users
    public final Authentication ANONYMOUS_USER =
            new TestingAuthenticationToken(ANONYMOUS_UID, "anonymous",
                    new GrantedAuthority[]{new GrantedAuthorityImpl(ANONYMOUS_ROLE)});

    public final Authentication AUTHENTICATED_USER =
            new TestingAuthenticationToken(AUTHENTICATED_UID, "secret",
                    new GrantedAuthority[]{new GrantedAuthorityImpl(AUTHENTICATED_ROLE)});

    public final Authentication ADMIN_USER =
            new TestingAuthenticationToken(ADMIN_UID, "secret",
                    new GrantedAuthority[]{new GrantedAuthorityImpl(AUTHENTICATED_ROLE),
                            new GrantedAuthorityImpl(ADMIN_ROLE)});

    protected Event createEvent(String eventId, String graphId) {
        Event event = new Event();
        event.setId(eventId);
        event.setGraph(graphId);
        return event;
    }
    
}
