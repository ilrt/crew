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

package net.crew_vre.nativeloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * A class loader for finding native libraries in the classpath
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class NativeClassLoader extends ClassLoader {

    private static final String LINUX32_PREFIX = "linux32/";

    private static final String LINUX64_PREFIX = "linux64/";

    private static final String MACOSX_PREFIX = "macosx/";

    private static final String WINDOWS32_PREFIX = "windows32/";

    private static final String WINDOWS64_PREFIX = "windows64/";

    private static final int BUFFER_SIZE = 8096;

    private String packageToLoad = null;

    private HashMap<String, String> loadedLibraries =
        new HashMap<String, String>();

    /**
     * Creates a new NativeClassLoader
     * @param packageToLoad The name of the package to load - all others will be
     *                    loaded by the classloader of this class
     */
    public NativeClassLoader(String packageToLoad) {
        this.packageToLoad = packageToLoad;
    }

    private void passData(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = input.read(buffer);
        while (bytesRead != -1) {
            output.write(buffer, 0, bytesRead);
            bytesRead = input.read(buffer);
        }
    }

    /**
     *
     * @see java.lang.ClassLoader#loadClass(java.lang.String)
     */
    public Class< ? > loadClass(String name) throws ClassNotFoundException {
        if (name.startsWith(packageToLoad)) {
            try {
                InputStream input = getClass().getResourceAsStream(
                        "/" + name.replace('.', '/') + ".class");
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                passData(input, output);
                input.close();
                output.close();
                byte[] data = output.toByteArray();
                return defineClass(name, data, 0, data.length);
            } catch (IOException e) {
                throw new ClassNotFoundException("Error loading " + name, e);
            }
        }
        return getClass().getClassLoader().loadClass(name);
    }

    /**
     *
     * @see java.lang.ClassLoader#findLibrary(java.lang.String)
     */
    public synchronized String findLibrary(String name) {
        if (loadedLibraries.containsKey(name)) {
            return loadedLibraries.get(name);
        }
        String fileName = System.mapLibraryName(name);
        InputStream input = getClass().getResourceAsStream(
                "/native/" + fileName);
        if (input == null) {
            String prefix = null;
            String os = System.getProperty("os.name").toLowerCase();
            String arch = System.getProperty("os.arch");
            if (os.startsWith("windows")) {
                if (arch.equals("x86")) {
                    prefix = WINDOWS32_PREFIX;
                } else {
                    prefix = WINDOWS64_PREFIX;
                }
            } else if (os.startsWith("linux")) {
                if (arch.equals("x86") || arch.equals("i386")
                        || arch.equals("i686")) {
                    prefix = LINUX32_PREFIX;
                } else {
                    prefix = LINUX64_PREFIX;
                }
            } else if (os.startsWith("mac os x")) {
                prefix = MACOSX_PREFIX;
            }
            if (prefix != null) {
                input = getClass().getResourceAsStream(
                        "/native/" + prefix + fileName);
            }
        }

        if (input != null) {
            try {
                File tempDir = new File(System.getProperty("java.io.tmpdir"),
                        "NativeClassLoaderFiles");
                tempDir.mkdirs();
                File tempFile = new File(tempDir, fileName);
                FileOutputStream output = new FileOutputStream(tempFile);
                passData(input, output);
                output.close();
                input.close();
                loadedLibraries.put(name, tempFile.getAbsolutePath());
                return tempFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
