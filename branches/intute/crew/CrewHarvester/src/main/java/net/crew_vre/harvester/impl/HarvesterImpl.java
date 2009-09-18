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
package net.crew_vre.harvester.impl;

import com.hp.hpl.jena.rdf.model.Model;
import org.apache.log4j.Logger;
import org.caboto.jena.db.Database;

import java.util.List;

import net.crew_vre.harvester.Harvester;
import net.crew_vre.harvester.Resolver;
import net.crew_vre.harvester.RulesManager;
import net.crew_vre.harvester.HarvestSource;


public class HarvesterImpl implements Harvester {

    public HarvesterImpl(Database database, Resolver resolver, RulesManager rulesManager) {
        this.database = database;
        this.resolver = resolver;
        this.rulesManager = rulesManager;
        this.status = READY;
    }

    public String harvest(List<HarvestSource> harvestSources) {

        // if we are already harvesting return that we are busy
        if (status.equals(BUSY)) {
            log.info("The harvester is busy");
            return BUSY;
        }

        // set that we are now busy
        status = BUSY;

        // harvest
        for (HarvestSource source : harvestSources) {

            log.info("Harvesting: " + source.getLocation());

            Model grabbedModel = resolver.get(source);

            if (grabbedModel == null) continue;

            log.info("Retrieved model with " + grabbedModel.size() + " triples.");
            //System.out.println(grabbedModel.size());

            Model rulesModel = rulesManager.fireRules(grabbedModel);

            harvestModel(source.getLocation(), rulesModel);

            //database.deleteAll(source.getLocation());
            //database.addModel(source.getLocation(), rulesModel);

            log.info("Stored " + rulesModel.size() + " triples");
            System.out.println("Triple size: " + rulesModel.size());
        }

        status = READY;
        return DONE;
    }

    public void harvestModel(String uri, Model model) {
        database.deleteAll(uri);
        database.addModel(uri, model);
    }

    private String status;

    private final String READY = "Ready";
    private final String BUSY = "Busy";
    private final String DONE = "Done";


    private Resolver resolver;
    //private Log log = LogFactory.getLog(Harvester.class);
    private Database database;
    private RulesManager rulesManager;

    private Logger log = Logger.getLogger(HarvesterImpl.class);
}
