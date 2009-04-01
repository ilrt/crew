package net.crew_vre.events.dao;

import net.crew_vre.events.domain.Person;

import java.util.List;

/**
 * <p>A Data Access Object that provides access to people.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PersonDao.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface PersonDao {

    Person findPersonById(final String id);

    List<Person> findAllPeople();

    List<Person> findAllPeople(final int limit, final int offset);

    List<Person> findPeopleByEvent(final String id);

    List<Person> findPeopleByEvent(final String id, final int limit, final int offset);

    List<Person> findPeopleByRole(final String id);

    List<Person> findAuthors(final String id);

    List<Person> findPeopleByConstraint(final String constraint);

    List<Person> findPeopleByConstraint(final String constraint, final int limit, final int offset);

}
