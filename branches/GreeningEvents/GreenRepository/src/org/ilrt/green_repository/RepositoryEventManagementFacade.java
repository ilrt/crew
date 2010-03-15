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

package org.ilrt.green_repository;

import java.util.List;

/**
 *
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 */
public class RepositoryEventManagementFacade {
    
    private final RepositoryDao repositoryDao;

    public RepositoryEventManagementFacade(RepositoryDao repositoryDao) {        
        this.repositoryDao = repositoryDao;
    }

    public RepositoryEventForm getRepositoryEvent(String id) {
        return repositoryDao.findRepositoryEvent(id);
    }

    public List<RepositoryEventForm> getAllRepositoryEvents() {
        return repositoryDao.findAllRepositoryEvents();
    }

    public void addRepositoryEvent(RepositoryEventForm repositoryEvent) {
        repositoryDao.storeRepositoryEvent(repositoryEvent);
    }

    public void updateRepositoryEvent(RepositoryEventForm repositoryEvent) {
        repositoryDao.updateRepositoryEvent(repositoryEvent);
    }

    public void removeRepositoryEvent(String eventId) {

        if (getRepositoryEvent(eventId) != null) {
            repositoryDao.deleteRepositoryEvent(eventId);

        }
    }

}
