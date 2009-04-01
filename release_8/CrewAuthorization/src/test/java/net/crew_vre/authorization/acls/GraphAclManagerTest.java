package net.crew_vre.authorization.acls;

import net.crew_vre.authorization.Permission;
import net.crew_vre.authorization.acls.impl.GraphAclEntryImpl;
import org.hibernate.SessionFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: GraphAclManagerTest.java 1092 2009-03-11 19:01:38Z cmmaj $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-context.xml"})
@Transactional
public class GraphAclManagerTest extends BaseTest {

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Autowired
    public void setGraphAclManager(GraphAclManager graphAclManager) {
        this.graphAclManager = graphAclManager;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    public void createAcl() {

        String query = "SELECT COUNT(*) FROM GRAPH_ACL";

        // test that the entry is not already in the database
        assertEquals("The datbase is not empty", 0, jdbcTemplate.queryForInt(query));

        GraphAcl acl = graphAclManager.createGraphAcl(GRAPH_ONE, ADMIN_ROLE);

        flush();

        assertEquals("The datbase is empty", 1, jdbcTemplate.queryForInt(query));
        assertNotNull("The acl has not got an identifier", acl.getId());
        assertEquals("Unexpected value", GRAPH_ONE, acl.getGraph());
        assertEquals("Unexpected value", ADMIN_ROLE, acl.getAuthority());
    }


    @Test
    public void findAcl() {

        jdbcTemplate.execute(insertAcl);

        assertEquals("The database is empty", 1, jdbcTemplate.queryForInt(countAclQuery));

        GraphAcl acl = graphAclManager.findAcl(GRAPH_ONE, ADMIN_ROLE);

        assertNotNull("The acl should not be null", acl);
        assertEquals("Unexpected key", KEY, acl.getId());
        assertEquals("Unexpected value", GRAPH_ONE, acl.getGraph());
        assertEquals("Unexpected value", ADMIN_ROLE, acl.getAuthority());
    }

    @Test
    public void updateAcl() {

        jdbcTemplate.execute(insertAcl);

        assertEquals("The database is empty", 1, jdbcTemplate.queryForInt(countAclQuery));

        GraphAcl acl = graphAclManager.findAcl(GRAPH_ONE, ADMIN_ROLE);

        assertEquals("There are entries", 0, acl.getEntries().size());

        acl.getEntries().add(new GraphAclEntryImpl(Permission.READ.intValue()));

        graphAclManager.updateAcl(acl);

        flush();

        String testInsert = "SELECT COUNT(*) FROM GRAPH_ACL_ENTRY";

        assertEquals("There should be 1 entry", 1, jdbcTemplate.queryForInt(testInsert));

    }


    @Test
    public void deleteAcl() {

        jdbcTemplate.execute(insertAcl);
        jdbcTemplate.execute("INSERT INTO GRAPH_ACL_ENTRY (ID, GRAPH_ACL_ID, PERMISSION)"
                + " VALUES (100, 100, 1)");
        jdbcTemplate.execute("INSERT INTO GRAPH_ACL_ENTRY (ID, GRAPH_ACL_ID, PERMISSION)"
                + " VALUES (101, 100, 2)");
        jdbcTemplate.execute("INSERT INTO GRAPH_ACL_ENTRY (ID, GRAPH_ACL_ID, PERMISSION)"
                + " VALUES (102, 100, 4)");

        assertEquals("ACL not foud", 1, jdbcTemplate.queryForInt(countAclQuery));
        assertEquals("ACL ENTRY not found", 3, jdbcTemplate.queryForInt("SELECT COUNT(*)"
                + " FROM GRAPH_ACL_ENTRY WHERE GRAPH_ACL_ID = " + KEY));
    }

    @Test
    public void findAcls() {

        // check we have an empty databse
        assertEquals("The database is empty", 0, jdbcTemplate.queryForInt(countAclQuery));

        jdbcTemplate.execute("INSERT INTO GRAPH_ACL (ID, GRAPH, AUTHORITY) VALUES (100, " +
                "'http://example.org/graph/', 'TEST_ONE')");
        jdbcTemplate.execute("INSERT INTO GRAPH_ACL (ID, GRAPH, AUTHORITY) VALUES (101, " +
                "'http://example.org/graph/', 'TEST_TWO')");

        assertEquals("The database should have 2 rows", 2, jdbcTemplate.queryForInt(countAclQuery));

        List<GraphAcl> results = graphAclManager.findAcls("http://example.org/graph/");

        assertEquals("There should be 2 acls", 2, results.size());
    }


    final private String countAclQuery = "SELECT COUNT(*) FROM GRAPH_ACL";
    final private String insertAcl = "INSERT INTO GRAPH_ACL (ID, GRAPH, AUTHORITY) VALUES (100, '"
            + GRAPH_ONE + "', '" + ADMIN_ROLE + "')";
    final private Long KEY = 100L;

    private GraphAclManager graphAclManager;

    private SessionFactory sessionFactory;

    private JdbcTemplate jdbcTemplate;


    protected void flush() {
        SessionFactoryUtils.getSession(sessionFactory, false).flush();
    }
}
