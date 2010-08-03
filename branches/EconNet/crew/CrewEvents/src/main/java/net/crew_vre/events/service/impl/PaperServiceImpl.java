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
package net.crew_vre.events.service.impl;

import net.crew_vre.events.dao.PaperDao;
import net.crew_vre.events.dao.PersonDao;
import net.crew_vre.events.domain.Paper;
import net.crew_vre.events.service.PaperService;

import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class PaperServiceImpl implements PaperService {

    public PaperServiceImpl(PaperDao paperDao, PersonDao personDao) {
        this.paperDao = paperDao;
        this.personDao = personDao;
    }

    public Paper getPaperById(String id) {
        Paper paper = paperDao.findPaperById(id);

        if (paper != null) {
            paper.setAuthors(personDao.findAuthors(id));
        }
        return paper;
    }

    public List<Paper> findPapersRelatedToEvent(String eventId) {

        List<Paper> results = paperDao.findPapersRelatedToEvent(eventId);

        for (Paper p: results) {
            p.setAuthors(personDao.findAuthors(p.getId()));
        }
        return results;
    }

    // used to get the paper object
    private PaperDao paperDao;

    // used to get the authors of a paper
    private PersonDao personDao;

}
