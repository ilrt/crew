package net.crew_vre.web.facade.impl;

import net.crew_vre.events.domain.Person;
import net.crew_vre.events.service.PersonService;
import net.crew_vre.web.facade.ListPeopleFacade;
import net.crew_vre.web.facet.SearchFilter;

import java.util.List;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: ListPeopleFacadeImpl.java 702 2008-02-22 14:18:15Z cmmaj $
 */
public class ListPeopleFacadeImpl implements ListPeopleFacade {

    public ListPeopleFacadeImpl(PersonService personService) {
        this.personService = personService;
    }

    public List<Person> displayPeople() {

        return personService.getAllPeople();
    }

    public List<Person> displayPeople(final List<SearchFilter> searchFilters) {

        String filterString = filterToString(searchFilters);

        return personService.getPeopleByConstraint(filterString);
    }

    public List<Person> displayPeople(final List<SearchFilter> searchFilters, final int limit,
                                      final int offset) {

        String filterString = filterToString(searchFilters);

        return personService.getPeopleByConstraint(filterString, limit, offset);
    }

    public List<Person> displayPeople(final int limit, final int offset) {

        return personService.getAllPeople(limit, offset);
    }

    public int totalPeopleAvailable() {

        return personService.getTotalPeople();
    }

    public int totalPeopleAvailable(List<SearchFilter> searchFilters) {

        String filterString = filterToString(searchFilters);

        return personService.getTotalPeopleByConstraint(filterString);
    }

    private String filterToString(final List<SearchFilter> filters) {

        StringBuilder constraints = new StringBuilder();

        if (filters != null) {
            for (SearchFilter filter : filters) {
                constraints.append(filter.getSparqlFragment());
            }
        }

        return constraints.toString();
    }

    private PersonService personService;

}
