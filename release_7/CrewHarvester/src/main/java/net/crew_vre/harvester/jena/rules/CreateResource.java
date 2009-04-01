package net.crew_vre.harvester.jena.rules;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.reasoner.rulesys.BindingEnvironment;
import com.hp.hpl.jena.reasoner.rulesys.Builtin;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;

import java.util.UUID;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class CreateResource extends BaseBuiltin implements Builtin {

    public String getName() {
        return "createResource";
    }

    @Override
    public int getArgLength() {
        return 1;
    }

    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {

        checkArgs(length, context);
        BindingEnvironment env = context.getEnv();
        Node bind = getArg(0, args, context);
        Node node = Node.createURI("http://www.crew_vre.net/events#" + UUID.randomUUID());
        return env.bind(bind, node);
    }

}
