package net.crew_vre.jena.query;

import com.hp.hpl.jena.query.Dataset;

/**
 * <p>For querying data we want to work on an immutable dataset. This is provided by a Dataset.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DatasetFactory.java 1132 2009-03-20 19:05:47Z cmmaj $
 *
 */
public interface DatasetFactory {
    Dataset create();
}
