package net.crew_vre.authorization.acls.impl.spring;

import net.crew_vre.authorization.GateKeeper;
import net.crew_vre.authorization.Permission;
import net.crew_vre.authorization.acls.BaseTest;
import net.crew_vre.authorization.acls.PermissionResolver;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: GateKeeperImplTest.java 1092 2009-03-11 19:01:38Z cmmaj $
 */
@RunWith(JMock.class)
public class GateKeeperImplTest extends BaseTest {

    Mockery context = new JUnit4Mockery();
    final PermissionResolver permissionResolver = context.mock(PermissionResolver.class);


    @Test
    public void anonymousCanRead() {

        context.checking(new Expectations() {{
            oneOf(permissionResolver).authorityHasPermissionForGraph(GRAPH_ONE,
                    ANONYMOUS_ROLE, Permission.READ);
            will(returnValue(true));
        }});

        GateKeeper gateKeeper = new GateKeeperImpl(permissionResolver);

        boolean isAllowed = gateKeeper.userHasPermissionFor(ANONYMOUS_USER,
                Permission.READ, GRAPH_ONE);

        assertTrue("The anonymous reader can view the graph", isAllowed);
    }

    @Test
    public void anonymousCannotRead() {

        context.checking(new Expectations() {{
            oneOf(permissionResolver).authorityHasPermissionForGraph(GRAPH_TWO,
                    ANONYMOUS_ROLE, Permission.READ);
            will(returnValue(false));
        }});

        GateKeeper gateKeeper = new GateKeeperImpl(permissionResolver);

        boolean isAllowed = gateKeeper.userHasPermissionFor(ANONYMOUS_USER,
                Permission.READ, GRAPH_TWO);

        assertFalse("The anonymous reader cannot view the graph", isAllowed);

    }

    @Test
    public void adminCanRead() {

        context.checking(new Expectations() {{
            oneOf(permissionResolver).authorityHasPermissionForGraph(GRAPH_TWO,
                    AUTHENTICATED_ROLE, Permission.READ);
            will(returnValue(false));
        }});

        context.checking(new Expectations() {{
            oneOf(permissionResolver).authorityHasPermissionForGraph(GRAPH_TWO,
                    ADMIN_ROLE, Permission.READ);
            will(returnValue(true));
        }});

        GateKeeper gateKeeper = new GateKeeperImpl(permissionResolver);

        boolean isAllowed = gateKeeper.userHasPermissionFor(ADMIN_USER,
                Permission.READ, GRAPH_TWO);

        assertTrue("The admin reader can view the graph", isAllowed);
    }

}
