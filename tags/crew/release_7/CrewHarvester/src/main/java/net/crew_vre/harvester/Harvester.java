package net.crew_vre.harvester;

import com.hp.hpl.jena.rdf.model.Model;

import java.util.List;


public interface Harvester {
    
    String harvest(List<HarvestSource> harvestSources);

    void harvestModel(String uri, Model model);
}
