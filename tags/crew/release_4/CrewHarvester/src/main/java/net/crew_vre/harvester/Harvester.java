package net.crew_vre.harvester;

import com.hp.hpl.jena.rdf.model.Model;
import org.apache.log4j.Logger;
import org.caboto.jena.db.Database;

import java.util.List;


public class Harvester {

    public Harvester(Database database, Resolver resolver, RulesManager rulesManager) {
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

            database.deleteAll(source.getLocation());
            database.addModel(source.getLocation(), rulesModel);

            log.info("Stored " + rulesModel.size() + " triples");
            //System.out.println(rulesModel.size());
        }

        status = READY;
        return DONE;
    }

    private String status;

    private final String READY = "Ready";
    private final String BUSY = "Busy";
    private final String DONE = "Done";


    private Resolver resolver;
    //private Log log = LogFactory.getLog(Harvester.class);
    private Database database;
    private RulesManager rulesManager;

    private Logger log = Logger.getLogger(Harvester.class);
}
