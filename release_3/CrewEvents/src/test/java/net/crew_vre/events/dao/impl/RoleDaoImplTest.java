package net.crew_vre.events.dao.impl;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.dao.RoleDao;
import net.crew_vre.events.domain.Role;
import net.crew_vre.jena.exception.DatasetFactoryException;
import net.crew_vre.jena.query.DatasetFactory;
import net.crew_vre.jena.query.impl.DatasetFactoryClasspathFileImpl;

import java.util.List;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: RoleDaoImplTest.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class RoleDaoImplTest extends TestCase {

    private RoleDao roleDao;

    @Override
    public void setUp() {
        try {
            DatasetFactory datasetFactory =
                    new DatasetFactoryClasspathFileImpl(Constants.DEFAULT_MODEL,
                            Constants.NAMED_GRAPHS);
            roleDao = new RoleDaoImpl(datasetFactory, new JenaQueryUtilityImpl());
        } catch (DatasetFactoryException e) {
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
