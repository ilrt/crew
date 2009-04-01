package org.ilrt.dibden.dao.hibernate;

import org.ilrt.dibden.dao.GroupDao;
import org.ilrt.dibden.domain.Group;
import org.junit.Test;

import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class GroupDaoImplTest extends AbstractDaoImplTest {

    public void setGroupDao(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    @Test
    public void testCreateGroup() {

        String query = "SELECT COUNT(*) FROM GROUPS WHERE GROUPID = 'TEST'";

        assertEquals("The test group already exists in the database", 0,
                jdbcTemplate.queryForInt(query));

        groupDao.createGroup("TEST", "Test Group", "A test group");

        flush();

        assertEquals("The test group doesn't exists in the database", 1,
                jdbcTemplate.queryForInt(query));
    }

    @Test
    public void testFindGroup() {

        Group group = groupDao.findGroup("USER_GROUP");

        assertNotNull("The group doesn't exist", group);
    }

    @Test
    public void testUpdateGroup() {

        assertEquals("The group is not in the database", 1, jdbcTemplate.queryForInt(
                "SELECT COUNT(*) FROM GROUPS WHERE GROUPID = 'ILRT_GROUP'"));

        Group group = groupDao.findGroup("ILRT_GROUP");
        group.setName("Changed Name");
        groupDao.updateGroup(group);

        flush();

        assertEquals("The group name has not changed", "Changed Name",
                jdbcTemplate.queryForObject("SELECT NAME FROM GROUPS WHERE " +
                        "GROUPID= 'ILRT_GROUP'", java.lang.String.class));
    }

    @Test
    public void testDeleteRole() {

        assertEquals("The group is not in the database", 1, jdbcTemplate.queryForInt(
                "SELECT COUNT(*) FROM GROUPS WHERE GROUPID = 'ILRT_GROUP'"));

        groupDao.deleteGroup("ILRT_GROUP");

        flush();

        assertEquals("The group is in the database", 0, jdbcTemplate.queryForInt(
                "SELECT COUNT(*) FROM GROUPS WHERE GROUPID = 'ILRT_GROUP'"));

    }

    @Test
    public void testFindAll() {
        List<Group> results = groupDao.findAll();
        assertEquals("There are not 3 groups", 3, results.size());
    }

    @Test
    public void testFindAllWithOffset() {
        List<Group> results = groupDao.findAll(0, 2);
        assertEquals("There are not 2 groups", 2, results.size());
    }

    private GroupDao groupDao;
}
