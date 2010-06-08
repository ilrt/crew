/**
 * Copyright (c) 2010, University of Bristol
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

package org.ilrt.green_repository.dao.hibernate;

import org.ilrt.green_repository.web.RepositoryEventForm;
import org.ilrt.green_repository.domain.RepositoryEvent;
import org.ilrt.green_repository.dao.RepositoryDao;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;
import org.ilrt.green_repository.domain.RepositoryEventKml;

/**
 *
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 */
public class RepositoryDaoImpl extends HibernateDaoSupport implements RepositoryDao {

    public RepositoryDaoImpl(HibernateTemplate hibernateRepositoryTemplate) {
        setHibernateTemplate(hibernateRepositoryTemplate);
    }

    public void createRepositoryEvent(RepositoryEventForm repositoryEventForm) {

        RepositoryEvent event = new RepositoryEvent(repositoryEventForm);
        this.getHibernateTemplate().save(event);

    }

    public void updateRepositoryEvent(RepositoryEventForm repositoryEventForm) {

        RepositoryEvent event = new RepositoryEvent(repositoryEventForm);
        this.getHibernateTemplate().update(event);

    }

    public RepositoryEvent findRepositoryEvent(String eventId) {

        List results =
                this.getHibernateTemplate().find("from RepositoryEvent re where re.eventId = ?", eventId);

        RepositoryEvent event = null;
        if (results.size() >= 1) {
            event = (RepositoryEvent) results.get(0);
        }

        return event;
    }

    public List<RepositoryEvent> findAllRepositoryEvents() {

        List results =
                this.getHibernateTemplate().find("from RepositoryEvent order by startDate");

        return results;
    }

    public void deleteRepositoryEvent(String eventId) {

        RepositoryEvent event = findRepositoryEvent(eventId);
        this.getHibernateTemplate().delete(event);

    }

    public RepositoryEventKml findKmlObject(String id){

        RepositoryEventKml kml = null;
        List results =
                this.getHibernateTemplate().find("from RepositoryEventKml k where k.kmlId = ?", id);
        if (results.size() >= 1) {
            kml = (RepositoryEventKml) results.get(0);
        }
        return kml;
    }

}
