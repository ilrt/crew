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
 * @version $Id: ToXSDDateRuleTest.java 948 2008-11-28 14:25:26Z cmmaj $
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
