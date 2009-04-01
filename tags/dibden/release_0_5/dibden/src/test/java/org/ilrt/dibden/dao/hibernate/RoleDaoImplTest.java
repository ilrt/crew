/*
 * Copyright (c) 2008, University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the name of the University of Bristol nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.ilrt.dibden.dao.hibernate;

import org.ilrt.dibden.dao.RoleDao;
import org.ilrt.dibden.domain.Role;
import org.junit.Test;

import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: RoleDaoImplTest.java 114 2008-09-12 16:29:22Z cmmaj $
 */
public class RoleDaoImplTest extends AbstractDaoImplTest {

    // auto wiring adds dependency
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Test
    public void testCreateRole() {

        String query = "SELECT COUNT(*) FROM ROLES WHERE ROLEID = 'TEST'";

        assertEquals("The test role already exists in the database", 0,
                jdbcTemplate.queryForInt(query));

        roleDao.createRole("TEST", "Test Role", "A test role");

        flush();

        assertEquals("The test role doesn't exists in the database", 1,
                jdbcTemplate.queryForInt(query));

    }

    @Test
    public void testFindRole() {

        Role role = roleDao.findRole("USER");

        assertNotNull("The role doesn't exist", role);

    }

    @Test
    public void testUpdateRole() {

        assertEquals("The role is not in the database", 1, jdbcTemplate.queryForInt(
                "SELECT COUNT(*) FROM ROLES WHERE ROLEID = 'ACADEMIC'"));

        Role role = roleDao.findRole("ACADEMIC");
        role.setName("Changed Name");
        roleDao.updateRole(role);

        flush();

        assertEquals("The group name has not changed", "Changed Name",
                jdbcTemplate.queryForObject("SELECT NAME FROM ROLES WHERE " +
                        "ROLEID = 'ACADEMIC'", java.lang.String.class));
    }

    @Test
    public void testFindAll() {
        List<Role> results = roleDao.findAll();
        assertEquals("There are not 3 roles", 3, results.size());
    }

    @Test
    public void testFindAllWithOffset() {
        List<Role> results = roleDao.findAll(0, 2);
        assertEquals("There are not 2 roles", 2, results.size());
    }

    @Test
    public void deleteRole() {

        assertEquals("The role is not in the database", 1, jdbcTemplate.queryForInt(
                "SELECT COUNT(*) FROM ROLES WHERE ROLEID = 'ACADEMIC'"));

        roleDao.deleteRole("ACADEMIC");

        flush();

        assertEquals("The role is in the database", 0, jdbcTemplate.queryForInt(
                "SELECT COUNT(*) FROM ROLES WHERE ROLEID = 'ACADEMIC'"));

    }

    private RoleDao roleDao;
}
