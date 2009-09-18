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
package net.crew_vre.domain;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Utility Class to fill in objects from search results
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class JenaFiller {

    private JenaFiller() {
        // Does Nothing
    }



    /**
     * Sets the value of a variable using an RDF Node
     * @param variable The variable to set the value of
     * @param node The value to set to
     * @throws UnknownVariableException if the variable cannot be set
     */
    private static void setVariable(Object object, String variable,
            RDFNode node)
            throws UnknownVariableException {
        Class cls = object.getClass();
        String setMethod = "set" + variable.substring(0, 1).toUpperCase()
            + variable.substring(1);
        Class paramType = null;
        Object value = null;
        if (node == null) {
            return;
        }
        if (node.isLiteral()) {
            Literal literal = (Literal) node;
            RDFDatatype datatype = literal.getDatatype();
            if (datatype != null) {
                paramType = datatype.getJavaClass();
            }
            value = literal.getValue();
        } else {
            paramType = String.class;
            value = ((Resource) node).getURI();
        }
        if ((paramType != null) && (value != null)) {
            try {
                Method method = cls.getMethod(setMethod,
                        new Class[]{value.getClass()});
                method.invoke(object, value);
            } catch (Exception e) {
                throw new UnknownVariableException(variable, value,
                        object.getClass());
            }
        } else {
            Method[] methods = cls.getMethods();
            boolean done = false;
            for (int i = 0; (i < methods.length) && !done; i++) {
                if (methods[i].getName().equals(setMethod)
                        && (methods[i].getParameterTypes().length == 1)) {
                    try {
                        methods[i].invoke(object, value);
                        done = true;
                    } catch (Exception e) {
                        // Do Nothing
                    }
                }
            }
            if (!done) {
                throw new UnknownVariableException(variable, value,
                        object.getClass());
            }
        }
    }

    /**
     * Fills in the details of an object from a solution
     *
     * @param object The object to fill in
     * @param solution The solution to the query
     * @param vars The variables to set
     */
    public static void fillIn(Object object, QuerySolution solution,
            List<String> vars) {
        Iterator<String> iterator = vars.listIterator();
        while (iterator.hasNext()) {
            String var = iterator.next();
            RDFNode value = solution.get(var);
            try {
                setVariable(object, var, value);
            } catch (UnknownVariableException e) {
                System.err.println("Warning: " + e.getMessage());
            }
        }
    }

}
