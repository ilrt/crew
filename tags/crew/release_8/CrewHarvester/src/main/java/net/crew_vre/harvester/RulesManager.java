package net.crew_vre.harvester;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author Mike Jones (m.a.jones@bristol.ac.uk)
 * @version $Id: RulesManager.java 929 2008-11-18 15:28:41Z cmmaj $
 */
public interface RulesManager {

    Model fireRules(Model model);

}
