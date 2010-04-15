/**
 * Copyright (c) 2008-2009, University of Bristol
 * Copyright (c) 2008-2009, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package net.crew_vre.events.dao;

import net.crew_vre.events.domain.EventPart;

import org.joda.time.LocalDate;
import org.joda.time.DateTime;

import com.hp.hpl.jena.rdf.model.Model;

import java.util.List;

/**
 * <p>A Data Access Object that provides access to "Main Events".</p>
 * <p/>
 * <p>In the ESWC2006 schema there are many different types of events and each
 * event might have zero to many parts. A conference might have tracks and
 * tracks might have sessions etc. To easily identify the main event, e.g. the
 * conference that has the sessions, the IUGO ontology had a type called
 * MainEvent.</p>
 * <p/>
 * <p>These methods provide basic details that will be used in displaying search
 * results. The EventDao implementation will be used to provide more detailed
 * information about an event.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: MainEventDao.java 1188 2009-03-31 13:09:20Z cmmaj $
 * @see EventDao
 */
public interface MainEventDao {

    EventPart findEventById(final String id);

    List<EventPart> findAllEvents();

    List<EventPart> findAllEvents(final int limit, final int offset);

    List<EventPart> findEventsByDate(final LocalDate startDate, final LocalDate endDate);

    List<EventPart> findEventsByDate(final LocalDate startDate, final LocalDate endDate,
                                 final int limit, final int offset);
    List<EventPart> findEventsByDateAsc(final LocalDate startDate, final LocalDate endDate);

    List<EventPart> findEventsByDateAsc(final LocalDate startDate, final LocalDate endDate,
                                 final int limit, final int offset);

    List<EventPart> findEventsWithConstraint(final String constraint);

    List<EventPart> findEventsWithConstraint(final String constraint, final int limit, int offset);

    List<EventPart> findEventsByCreationDate(final DateTime startDate, final DateTime endDate);

    List<EventPart> findEventsByCreationDate(final DateTime startDate, final DateTime endDate,
                                         final int limit, final int offset);

}
