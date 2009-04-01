package net.crew_vre.harvester.jena.rules;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.reasoner.rulesys.BindingEnvironment;
import com.hp.hpl.jena.reasoner.rulesys.BuiltinException;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.Builtin;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * <p>A simple rule that will convert an xsd:dateTime to an xsd:date - it basically removes
 * the time and timezone.<p>
 * <p>Example of a rule:-</p>
 * <p><pre>
 * (?A http://test.org/vocab/hasStartDateTime ?B), toXsdDate(?B, ?C) -> " +
 *               "(?A http://test.org/vocab/hasStartDate ?C) .
 * </pre>
 * </p>
 *
 * @version $Id: ToXSDDateRule.java 948 2008-11-28 14:25:26Z cmmaj $
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class ToXSDDateRule extends BaseBuiltin implements Builtin {

    public String getName() {
        return "toXsdDate";
    }

    @Override
    public int getArgLength() {
        return 2;
    }

    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {

        checkArgs(length, context);
        BindingEnvironment env = context.getEnv();
        Node val = getArg(0, args, context);
        Node bind = getArg(1, args, context);

        if (!(XSD.dateTime.getURI().equals(val.getLiteralDatatypeURI()))) {
            throw new BuiltinException(this, context, "Unexpected type, got "
                    + val.getLiteralDatatype() + " but received " + val.getLiteralDatatype());
        }

        String dateTime = val.getLiteralLexicalForm();
        Node date = Node.createLiteral(dateTime.substring(0, 10), null, XSDDatatype.XSDdate);
        return env.bind(bind, date);

    }
}
