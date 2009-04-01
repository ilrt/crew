package net.crew_vre.authorization.acls;

import net.crew_vre.authorization.acls.impl.PermissionResolverImpl;
import net.crew_vre.authorization.Permission;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: PermissionResolverTest.java 1092 2009-03-11 19:01:38Z cmmaj $
 */
@RunWith(JMock.class)
public class PermissionResolverTest {

    // ---------- TESTS

    @Test
    public void needsReadHasRead() {

        setReadPermission();

        boolean hasPermission = permissionResolver.authorityHasPermissionForGraph(graph,
                role, Permission.READ);

        assertTrue("The permission should be true", hasPermission);
    }

    @Test
    public void needsWriteHasRead() {

        setReadPermission();

        boolean hasPermission = permissionResolver.authorityHasPermissionForGraph(graph,
                role, Permission.WRITE);

        assertFalse("The permission should be false", hasPermission);
    }

    @Test
    public void needsWriteHasReadWriteAndDelete() {

        setAllPermission();

        boolean hasPermission = permissionResolver.authorityHasPermissionForGraph(graph,
                role, Permission.WRITE);

        assertTrue("The permission should be true", hasPermission);
    }

    @Test
    public void needsReadHasNone() {

        commonMockSetUp(graphAcls);

        boolean hasPermission = permissionResolver.authorityHasPermissionForGraph(graph,
                role, Permission.READ);

        assertFalse("The permission should be false", hasPermission);
    }

    // ---------- PRIVATE SETUP METHODS

    private void setReadPermission() {

        // we create one context entry - the getPermission value should be called
        context.checking(new Expectations() {{
            oneOf(graphAclEntry).getPermission();
            will(returnValue(Permission.READ.intValue()));
        }});

        graphAcls.add(graphAclEntry);

        commonMockSetUp(graphAcls);
    }

    private void setAllPermission() {

        final GraphAclEntry delete = context.mock(GraphAclEntry.class, "delete");

        context.checking(new Expectations() {{
            oneOf(delete).getPermission();
            will(returnValue(Permission.DELETE.intValue()));
        }});

        graphAcls.add(delete);


        final GraphAclEntry read = context.mock(GraphAclEntry.class, "read");

        context.checking(new Expectations() {{
            oneOf(read).getPermission();
            will(returnValue(Permission.READ.intValue()));
        }});

        graphAcls.add(read);

        final GraphAclEntry write = context.mock(GraphAclEntry.class, "write");

        context.checking(new Expectations() {{
            oneOf(write).getPermission();
            will(returnValue(Permission.WRITE.intValue()));
        }});

        graphAcls.add(write);

        commonMockSetUp(graphAcls);
    }

    private void commonMockSetUp(final List<GraphAclEntry> graphAcls) {


        // the GraphAcl should be queried for entries that it holds
        context.checking(new Expectations() {{
            oneOf(graphAcl).getEntries();
            will(returnValue(graphAcls));
        }});

        // the lookup should return the the ACL
        context.checking(new Expectations() {{
            oneOf(aclLookupManager).lookupAcl(graph, role);
            will(returnValue(graphAcl));
        }});

        permissionResolver = new PermissionResolverImpl(aclLookupManager);
    }

    final String role = "TEST";
    final String graph = "http://example.org/graph1";
    Mockery context = new JUnit4Mockery();

    final AclLookupManager aclLookupManager = context.mock(AclLookupManager.class);
    final GraphAcl graphAcl = context.mock(GraphAcl.class);
    final GraphAclEntry graphAclEntry = context.mock(GraphAclEntry.class);

    PermissionResolver permissionResolver;
    List<GraphAclEntry> graphAcls = new ArrayList<GraphAclEntry>();

}
