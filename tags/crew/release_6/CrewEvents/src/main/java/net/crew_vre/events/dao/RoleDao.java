package net.crew_vre.events.dao;

import net.crew_vre.events.domain.Role;

import java.util.List;

/**
 * <p>A Data Access Object that provides access to Roles.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: RoleDao.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface RoleDao {

    List<Role> findRolesByPerson(final String id);

    List<Role> findRolesByEvent(final String id);

}
