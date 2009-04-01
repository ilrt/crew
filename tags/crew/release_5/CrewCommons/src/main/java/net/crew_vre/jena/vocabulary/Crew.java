/*
 * Copyright (c) 2008, University of Manchester
 * Copyright (c) 2008, University of Bristol
 * All rights reserved.
 *
 * See LICENCE in root directory of source code for details of the license.
 */

package net.crew_vre.jena.vocabulary;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * The CREW vocabulary
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Crew {

    private static final Model MODEL = ModelFactory.createDefaultModel();

    /**
     * The CREW namespace
     */
    public static final String NS = "http://www.crew-vre.net/2008/10/crew-ns#";

    /**
     * The type of a bridge registry
     */
    public static final Resource TYPE_BRIDGE_REGISTRY =
        MODEL.createResource(NS + "BridgeRegistry");

    /**
     * The type of a venue server
     */
    public static final Resource TYPE_VENUE_SERVER =
        MODEL.createResource(NS + "VenueServer");

    /**
     * Link to a bridge
     */
    public static final Property HAS_BRIDGE =
        MODEL.createProperty(NS, "has-bridge");

    /**
     * Link to a venue
     */
    public static final Property HAS_VENUE =
        MODEL.createProperty(NS, "has-venue");

    /**
     * The type of a bridge
     */
    public static final Resource TYPE_BRIDGE =
        MODEL.createResource(NS + "Bridge");

    /**
     * The type of a venue
     */
    public static final Resource TYPE_VENUE =
        MODEL.createResource(NS + "Venue");
}
