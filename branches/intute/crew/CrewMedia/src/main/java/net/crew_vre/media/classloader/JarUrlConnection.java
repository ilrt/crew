/**
 * Copyright (c) 2008, University of Bristol
 * Copyright (c) 2008, University of Manchester
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


package net.crew_vre.media.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * A URL connection for reading files from jars within the classpath
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class JarUrlConnection extends URLConnection {

    private String jar = null;

    private String resource = null;

    /**
     * Creates a new JarUrlConnection
     * @param u The url to connect to
     */
    public JarUrlConnection(URL u) {
        super(u);
        jar = u.getPath();
        resource = u.getQuery();
    }

    /**
     *
     * @see java.net.URLConnection#connect()
     */
    public void connect() {
        // Does Nothing
    }

    /**
     *
     * @see java.net.URLConnection#getContentType()
     */
    public String getContentType() {
        FileNameMap fileNameMap = java.net.URLConnection.getFileNameMap();
        String contentType = fileNameMap.getContentTypeFor(resource);
        if (contentType == null) {
            contentType = "text/plain";
        }
        return contentType;
    }

    /**
     *
     * @see java.net.URLConnection#getInputStream()
     */
    public InputStream getInputStream() throws IOException {
        InputStream jarInput = getClass().getResourceAsStream(jar);
        if (jarInput == null) {
            return null;
        }
        JarInputStream input = new JarInputStream(jarInput);
        JarEntry entry = CodecClassLoader.findResource(input, resource);
        if (entry == null) {
            return null;
        }
        return input;
    }

}
