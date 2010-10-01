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
package net.crew_vre.rest.providers;

import java.io.File;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.caboto.RdfMediaType;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
@Provider
@Produces({ RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3 })
public class RDFFileProvider implements MessageBodyWriter<File[]> {

    /**
     *
     * @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class,
     *     java.lang.reflect.Type, java.lang.annotation.Annotation[],
     *     javax.ws.rs.core.MediaType)
     */
    public boolean isWriteable(Class< ? > aClass, Type type,
            Annotation[] annotations, MediaType mediaType) {
        return File[].class.isAssignableFrom(aClass);
    }

    /**
     *
     * @see javax.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object,
     *     java.lang.Class, java.lang.reflect.Type,
     *     java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
     */
    public long getSize(File[] files, Class< ? > aClass, Type type,
            Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    /**
     *
     * @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object,
     *     java.lang.Class, java.lang.reflect.Type,
     *     java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType,
     *     javax.ws.rs.core.MultivaluedMap, java.io.OutputStream)
     */
    public void writeTo(final File[] files, final Class< ? > aClass,
            final Type type, final Annotation[] annotations,
            final MediaType mediaType,
            final MultivaluedMap<String, Object> stringObjectMultivaluedMap,
            final OutputStream outputStream) {

        Model model = ModelFactory.createDefaultModel();
        for (File file : files) {
            System.err.println("Reading " + file);
            model.read(file.toURI().toString());
        }
        if (mediaType.getType().equals("text")
                && mediaType.getSubtype().equals("rdf+n3")) {
            model.write(outputStream, "N3");
        } else {
            model.write(outputStream, "RDF/XML-ABBREV");
        }
    }

}
