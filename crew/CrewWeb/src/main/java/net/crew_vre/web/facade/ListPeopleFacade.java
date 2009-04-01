package net.crew_vre.web.facade;

import net.crew_vre.events.domain.Person;
import net.crew_vre.web.facet.SearchFilter;

import java.util.List;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: ListPeopleFacade.java 702 2008-02-22 14:18:15Z cmmaj $
 */
public interface ListPeopleFacade {

    List<Person> displayPeople();

    List<Person> displayPeople(final int limit, final int offset);

    List<Person> displayPeople(final List<SearchFilter> searchFilters);

    List<Person> displayPeople(final List<SearchFilter> searchFilters, final int limit,
                               final int offset);

    int totalPeopleAvailable();

    int totalPeopleAvailable(final List<SearchFilter> searchFilters);
}
