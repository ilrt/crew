/**
 * Copyright (c) 2008-2009, University of Bristol
 * Copyright (c) 2008-2009, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package net.crew_vre.events.dao;

import net.crew_vre.events.domain.facet.Refinement;
import net.crew_vre.events.domain.facet.CountItem;

import java.util.List;

import org.caboto.jena.db.Data;

/**
 *
 * @author Mike Jones (mike.a.jones@gmail.com)
 * @version $Id: RefinementDao.java 1188 2009-03-31 13:09:20Z cmmaj $
 *
 **/
public interface RefinementDao {
    /**
     * <p>Find the names of refinements - used by the hierarchical facet.</p>
     *
     * @param widerProperty     the property used to widen searches.
     * @param subjectUri        the resource to start the search.
     * @return a list of refinements.
     */
    List<Refinement> findNames(String widerProperty, String subjectUri, Data data);

    /**
     * <p>Find the name of a resource.</p>
     *
     * @param uri   the URI of the resource.
     * @return a Refinement with a name.
     */
    Refinement getName(String uri, Data data);

    /**
     * <p>Returns a list of refinenments based on a single linking property - it is used by the
     * Flat Facet.</p>
     *
     * @param linkProperty      the linking property.
     * @return a list of refinements.
     */
    List<Refinement> findProperties(String linkProperty, Data data);

    /**
     * <p>Find the parents of a resource - used in the hierachical facets.</p>
     *
     * @param linkProperty      the linking property.
     * @param childUri          the child URI we want to find the parents.
     * @return the parent of the childUri or null.
     */
    Refinement findParents(String linkProperty, String childUri, Data data);

    /**
     * <p>Count the items that are available for a refinement, e,g, how many events start with the
     * letter A. It returns a list of count objects - these objects include graph details which
     * allows us to filter the results if the person browsing isn't allowed to access an event.
     * This allows us to display a number in the UI of matching events that a person is allows to
     * view rather than all matching events.</p>
     *
     * @param sparqlFragment    SPARQL fragment that constraints the count.
     * @param type              the RDF type we are interested in.
     * @return return a list of count objects.
     */
    List<CountItem> countRefinements(String sparqlFragment, String type, String paramName, final Data data);
}
