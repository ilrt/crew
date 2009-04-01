package org.ilrt.dibden.dao;

import org.ilrt.dibden.domain.Group;

import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public interface GroupDao {

    Group createGroup(String groupId, String name, String description);

    Group findGroup(String groupId);

    void updateGroup(Group group);

    void deleteGroup(String groupId);

    List<Group> findAll();

    List<Group> findAll(int first, int max);

}
