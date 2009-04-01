package net.crew_vre.harvester;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author Mike Jones (m.a.jones@bristol.ac.uk)
 * @version $Id: RulesManager.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface RulesManager {

    Model fireRules(Model model);

}
