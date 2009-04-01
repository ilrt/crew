package net.crew_vre.harvester;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author Damian Steer (d.steer@bristol.ac.uk)
 * @version $Id: Resolver.java 929 2008-11-18 15:28:41Z cmmaj $
 */
public interface Resolver {
    public Model get(HarvestSource source);
}
