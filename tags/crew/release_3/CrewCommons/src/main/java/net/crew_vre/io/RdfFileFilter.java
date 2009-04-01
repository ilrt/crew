package net.crew_vre.io;

import java.io.FileFilter;
import java.io.File;

/**
 * <p>File filter for RDF files.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: RdfFileFilter.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class RdfFileFilter implements FileFilter {
    public boolean accept(File file) {
        return file.getName().toLowerCase().endsWith(".rdf");
    }
}
