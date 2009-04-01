package net.crew_vre.harvester;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author Damian Steer (d.steer@bristol.ac.uk)
 * @version $Id: Resolver.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface Resolver {
    public Model get(HarvestSource source);
}
