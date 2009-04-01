package net.crew_vre.events.service;

import net.crew_vre.events.domain.Person;

import java.util.List;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PersonService.java 702 2008-02-22 14:18:15Z cmmaj $
 */
public interface PersonService {

    Person getPersonById(final String personId);

    List<Person> getAllPeople();

    List<Person> getAllPeople(final int offset, final int limit);

    int getTotalPeople();

    List<Person> getPeopleByConstraint(String constraint);

    List<Person> getPeopleByConstraint(String constraint, int limit, int offset);

    int getTotalPeopleByConstraint(String constraint);
}
