package net.crew_vre.events.service.impl;

import net.crew_vre.events.dao.PersonDao;
import net.crew_vre.events.domain.Person;
import net.crew_vre.events.service.PersonService;

import java.util.List;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PersonServiceImpl.java 702 2008-02-22 14:18:15Z cmmaj $
 */
public class PersonServiceImpl implements PersonService {

    public PersonServiceImpl(final PersonDao personDao) {
        this.personDao = personDao;
    }

    public Person getPersonById(final String personId) {
        return personDao.findPersonById(personId);
    }

    public List<Person> getAllPeople() {
        return personDao.findAllPeople();
    }

    public List<Person> getAllPeople(final int offset, final int limit) {
        return personDao.findAllPeople(offset, limit);
    }

    public int getTotalPeople() {
        return personDao.findAllPeople().size();
    }

    public List<Person> getPeopleByConstraint(final String constraint) {
        return personDao.findPeopleByConstraint(constraint);
    }

    public List<Person> getPeopleByConstraint(final String constraint, final int limit,
                                              final int offset) {
        return personDao.findPeopleByConstraint(constraint, limit, offset);
    }

    public int getTotalPeopleByConstraint(final String constraint) {
        return personDao.findPeopleByConstraint(constraint).size();
    }

    /**
     * DAO to find people.
     */
    private PersonDao personDao;
}
