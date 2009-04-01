package net.crew_vre.web.facade.impl;

import net.crew_vre.events.domain.Person;
import net.crew_vre.events.service.PersonService;
import net.crew_vre.web.facade.DisplayPersonFacade;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayPersonFacadeImpl.java 534 2007-12-20 17:10:53Z cmmaj $
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
