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

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.reasoner.rulesys.BuiltinRegistry;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import junit.framework.TestCase;

/**
 * @version $Id: ToXSDDateRuleTest.java 1190 2009-03-31 13:22:30Z cmmaj $
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class ToXSDDateRuleTest extends TestCase {


    public void testCreateXsdDate() {

        // dates we are expecting
        String dateTime = "2008-11-25T12:00:00+00:00";
        //String date = "2008-11-25";

        // create a model
        Model model = ModelFactory.createDefaultModel();

        // properties for the times
        Property hasDateTime = model.createProperty("http://test.org/vocab/hasStartDateTime");
        Property hasDate = model.createProperty("http://test.org/vocab/hasStartDate");

        // create some data
        Literal dateTimeLiteral = model.createTypedLiteral(dateTime, XSDDatatype.XSDdateTime);
        model.add(model.createStatement(model.createResource("http://test.org/1/"),
                hasDateTime, dateTimeLiteral));

        // create rules
        String rules = "(?A http://test.org/vocab/hasStartDateTime ?B), toXsdDate(?B, ?C) -> " +
                "(?A http://test.org/vocab/hasStartDate ?C) . ";

        // register the rule and create reasoner
        BuiltinRegistry builtinRegistry = BuiltinRegistry.theRegistry;
        builtinRegistry.register(new ToXSDDateRule());
        GenericRuleReasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));

        // create the inference model
        InfModel inf = ModelFactory.createInfModel(reasoner, model);

        NodeIterator iter = inf.listObjectsOfProperty(hasDate);

        assertEquals("Unexpected size", 1, iter.toList().size());

    }


}
