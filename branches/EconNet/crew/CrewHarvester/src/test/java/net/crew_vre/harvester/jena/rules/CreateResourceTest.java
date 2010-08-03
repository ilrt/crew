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
package net.crew_vre.harvester.jena.rules;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.reasoner.rulesys.BuiltinRegistry;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DC;
import junit.framework.TestCase;
import net.crew_vre.jena.vocabulary.ESWC2006;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: CreateResourceTest.java 1190 2009-03-31 13:22:30Z cmmaj $
 */
public class CreateResourceTest extends TestCase {


    public void testCreateXsdDate() {

        // create some dummy data
        Model model = ModelFactory.createDefaultModel();

        Resource documentUri = model.createResource("http://example/doc/1");
        Resource personUri = model.createResource("http://example/fred/1");
        Resource eventUri = model.createResource("http://example/event/1");

        model.add(documentUri, DC.title, model.createLiteral("Example Document"));
        model.add(documentUri, DC.creator, personUri);
        model.add(personUri, FOAF.name, model.createLiteral("Fred Smith"));
        model.add(eventUri, ESWC2006.hasRelatedArtefact, documentUri);

        // fire the rule
        String rules = "(?A http://purl.org/dc/elements/1.1/creator ?B), createResource(?C), " +
                "(?D http://www.eswc2006.org/technologies/ontology#hasRelatedArtefact ?A) -> " +
                "(?D http://www.eswc2006.org/technologies/ontology#hasRole ?C), " +
                "(?C http://www.eswc2006.org/technologies/ontology#heldBy ?B), " +
                "(?B http://www.eswc2006.org/technologies/ontology#holdsRole ?C), " +
                "(?C http://www.eswc2006.org/technologies/ontology#isRoleAt ?D), " +
                "(?C http://purl.org/dc/elements/1.1/title \"Paper Author\") .";

        // register the rule and create reasoner
        BuiltinRegistry builtinRegistry = BuiltinRegistry.theRegistry;
        builtinRegistry.register(new CreateResource());
        GenericRuleReasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));

        // create the inference model
        InfModel inf = ModelFactory.createInfModel(reasoner, model);

        Resource r = inf.getResource(eventUri.getURI());
        Statement s = r.getProperty(ESWC2006.hasRole);

        // check the resource was created
        assertNotNull("The resource should not be null", s.getResource());

    }


}