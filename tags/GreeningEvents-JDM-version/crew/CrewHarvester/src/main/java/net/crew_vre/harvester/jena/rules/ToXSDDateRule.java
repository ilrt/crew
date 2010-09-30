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
 * @version $Id: ToXSDDateRule.java 1190 2009-03-31 13:22:30Z cmmaj $
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
