package net.crew_vre.web.facade;

import net.crew_vre.events.domain.Person;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayPersonFacade.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface DisplayPersonFacade {

    Person displayPerson(String personId);

}

