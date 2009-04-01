package net.crew_vre.events.dao.impl;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.dao.RoleDao;
import net.crew_vre.events.domain.Role;

import java.io.IOException;
import java.util.List;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.impl.FileDatabase;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: RoleDaoImplTest.java 929 2008-11-18 15:28:41Z cmmaj $
 */
public class RoleDaoImplTest extends TestCase {

    private RoleDao roleDao;

    @Override
    public void setUp() {
        try {
            Database database = new FileDatabase(Constants.DEFAULT_MODEL,
                    Constants.NAMED_GRAPHS);
            roleDao = new RoleDaoImpl(database);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testFindRolesByPersonId() {

        List<Role> roles = roleDao.findRolesByPerson(Constants.PERSON_ONE_ID);
        assertNotNull("The results should not be empty", roles);

        for (Role r : roles) {
            assertNotNull("The role should not be null", r.getGraph());
        }

        assertTrue("The results should not be empty", roles.size() > 0);
    }


    public void testFindRolesByEventId() {

        List<Role> roles = roleDao.findRolesByEvent(Constants.EVENT_ONE_ID);
        assertNotNull("The results should not be empty", roles);
        assertTrue("The results should not be empty", roles.size() > 0);
    }


}
