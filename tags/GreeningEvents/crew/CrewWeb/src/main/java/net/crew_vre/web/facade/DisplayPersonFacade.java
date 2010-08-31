package net.crew_vre.web.facade;

import net.crew_vre.events.domain.Person;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayPersonFacade.java 538 2007-12-21 14:44:38Z cmmaj $
 */
public interface DisplayPersonFacade {

    Person displayPerson(String personId);

}

