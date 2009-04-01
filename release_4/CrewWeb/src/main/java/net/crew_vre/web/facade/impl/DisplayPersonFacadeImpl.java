package net.crew_vre.web.facade.impl;

import net.crew_vre.events.domain.Person;
import net.crew_vre.events.service.PersonService;
import net.crew_vre.web.facade.DisplayPersonFacade;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayPersonFacadeImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class DisplayPersonFacadeImpl implements DisplayPersonFacade {

    public DisplayPersonFacadeImpl(PersonService personService) {
        this.personService = personService;
    }

    public Person displayPerson(String personId) {

        return personService.getPersonById(personId);
    }

    private PersonService personService;

}
