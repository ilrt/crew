package net.crew_vre.harvester;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import static com.hp.hpl.jena.rdf.model.ResourceFactory.createResource;
import static com.hp.hpl.jena.rdf.model.ResourceFactory.createProperty;

/**
 * @author Damian Steer (d.steer@bristol.ac.uk)
 * @version $Id: Vocab.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class Vocab {
    public static String NS = "http://crew_vre.net/vocab/harvester#";
    public static Resource Source = createResource(NS + "Source");
    public static Property lastVisited = createProperty(NS + "lastVisited");
    public static Property lastStatus = createProperty(NS + "lastStatus");
    public static Property isBlocked = createProperty(NS + "isBlocked");
}
