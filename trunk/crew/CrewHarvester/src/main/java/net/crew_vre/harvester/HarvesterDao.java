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
package net.crew_vre.harvester;

import java.util.Date;
import java.util.List;

/**
 * <p>A DAO for handling harvester sources.</p>
 *
 * @version $Id: HarvesterDao.java 1190 2009-03-31 13:22:30Z cmmaj $
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public interface HarvesterDao {

    /**
     * Create a new harvester source.
     *
     * @param location    the location of the source - this will act as the URI.
     * @param name        the name of the source.
     * @param description a description of the source.
     * @param isBlocked   is the source blocked? i.e. harvestable?
     * @return a POJO encapsulating the harvester source.
     */
    HarvestSource createHarvestSource(String location, String name, String description,
                                      boolean isBlocked);

    /**
     * Update an existing harvester source.
     *
     * @param location    the location of the source - this will act as the URI.
     * @param name        the name of the source.
     * @param description a description of the source.
     * @param lastVisited the date that the source was last harvested.
     * @param lastStatus  the status of the last harvest.
     * @param blocked     is the source blocked? i.e. harvestable?
     */
    void updateHarvestSource(String location, String name, String description, Date lastVisited,
                             String lastStatus, boolean blocked);

    /**
     * Find the harvester source with the specified URI.
     *
     * @param location the location (URI) of the source.
     * @return the source specified by the URI.
     */
    HarvestSource findSource(String location);

    /**
     * Find all harvester sources.
     *
     * @return a list of all harvester sources.
     */
    List<HarvestSource> findAllSources();

    /**
     * Find all harvester sources that are not blocked.
     *
     * @return a list of all harvester sources that are not blocked.
     */
    List<HarvestSource> findAllPermittedSources();

    /**
     * Delete the harvester source identified by the URI
     *
     * @param location the URI of the harvester source.
     */
    void deleteSource(String location);

    /**
     * Delete all data related to a source.
     * @param location the location of the source.
     */
    void deleteData(String location);
}
