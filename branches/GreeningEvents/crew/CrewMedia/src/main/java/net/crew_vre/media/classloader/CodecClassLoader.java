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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import net.crew_vre.constants.CrewConstants;

/**
 * A class loader for loading codecs (and native libraries)
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class CodecClassLoader extends ClassLoader {

    private static final String LINUX32_PREFIX = "linux32/";

    private static final String LINUX64_PREFIX = "linux64/";

    private static final String MACOSX_PREFIX = "macosx/";

    private static final String WINDOWS32_PREFIX = "windows32/";

    private static final String WINDOWS64_PREFIX = "windows64/";

    private static final String CLASS_FILENAME_EXTENSION = ".class";

    private static final int BUFFER_SIZE = 8096;

    private static final String JAVA_PROTOCOL_HANDLER =
        "java.protocol.handler.pkgs";

    static {
        String handlerPackage = System.getProperty(JAVA_PROTOCOL_HANDLER);
        if (handlerPackage == null) {
            handlerPackage = "";
        }
        if (handlerPackage.length() > 0) {
            handlerPackage = "|" + handlerPackage;
        }
        handlerPackage = "net.crew_vre.codec" + handlerPackage;
        System.setProperty(JAVA_PROTOCOL_HANDLER, handlerPackage);
    }

    private class Result {

        private JarInputStream inputStream;

        private String codecJar;

        private Result(JarInputStream inputStream, String codecJar) {
            this.inputStream = inputStream;
            this.codecJar = codecJar;
        }
    }

    private class ExitDeleter extends Thread {

        private HashSet<File> filesCreated = new HashSet<File>();

        private HashSet<File> directoriesCreated = new HashSet<File>();

        private void addDirectory(File directory) {
            if (directory.isDirectory()) {
                directoriesCreated.add(directory);
            }
        }

        private void addFile(File file) {
            filesCreated.add(file);
        }

        public void run() {
            for (File file : filesCreated) {
                file.deleteOnExit();
            }
            for (File dir : directoriesCreated) {
                dir.deleteOnExit();
            }
        }
    }

    private final String uniqueId = "" + System.currentTimeMillis()
        + (int) (Math.random() * CrewConstants.ID_NORMALIZATION);

    // The location of the codec jars within the classpath
    private Vector<String> codecJars = new Vector<String>();

    // The known libraries extracted
    private HashMap<String, String> knownLibraryPaths =
        new HashMap<String, String>();

    // The known resources extracted
    private HashMap<String, URL> knownResourceURLs =
        new HashMap<String, URL>();

    // The previously loaded classes
    private HashMap<String, byte[]> knownClasses =
        new HashMap<String, byte[]>();
    // The previously loaded classes
    private HashMap<String, Class< ? >> loadedClasses =
        new HashMap<String, Class< ? >>();

    // Known contents of jar files
    private HashMap<String, String> knownResources =
        new HashMap<String, String>();

    private ExitDeleter deleter = new ExitDeleter();

    /**
     * Creates a new CodecClassLoader
     * @param parent The parent class loader
     * @param codecJars The location of the codec jar files within the classpath
     */
    public CodecClassLoader(ClassLoader parent, String... codecJars) {
        super(parent);
        for (String jar : codecJars) {
            addJar(jar);
        }
        Runtime.getRuntime().addShutdownHook(deleter);
    }

    /**
     * Creates a new CodecClassLoader
     * @param codecJars The location of the codec jar files within the classpath
     */
    public CodecClassLoader(String... codecJars) {
        super();
        for (String jar : codecJars) {
            addJar(jar);
        }
    }

    /**
     * Adds a jar to search
     * @param jar The jar to add
     */
    public void addJar(String jar) {
        codecJars.add(jar);
        InputStream file = getClass().getResourceAsStream(jar);
        if (file == null) {
            System.err.println("Warning: " + jar + " not found in classpath");
        } else {
            try {
                JarInputStream input = new JarInputStream(file);
                JarEntry entry = input.getNextJarEntry();
                while (entry != null) {
                    knownResources.put(entry.getName(), jar);
                    if (entry.getName().endsWith(CLASS_FILENAME_EXTENSION)) {
                        byte[] classData = readData(input);
                        String name = entry.getName();
                        name = name.substring(0, name.length() - CLASS_FILENAME_EXTENSION.length());
                        name = name.replace('/', '.');
                        System.out.println("define " + name);
                        knownClasses.put(name, classData);
                    }
                    entry = input.getNextJarEntry();
                }
            } catch (IOException e) {
                System.err.println("Warning: error reading " + jar
                        + ":" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Finds a resource in a jar
     * @param input The jar input stream
     * @param fileName The name of the resource to find
     * @return The entry of the resource, or null if not found
     * @throws IOException
     */
    public static JarEntry findResource(JarInputStream input, String fileName)
            throws IOException {
        JarEntry entry = input.getNextJarEntry();
        while (entry != null) {
            if (entry.getName().equals(fileName)) {
                return entry;
            }
            entry = input.getNextJarEntry();
        }
        return null;
    }

    private static String getName(String resource) {
        return resource.replaceAll("\\.", "/") + ".class";
    }

    private Result find(String fileName) {
        if (knownResources.containsKey(fileName)) {
            String codecJar = knownResources.get(fileName);
            InputStream jar = getClass().getResourceAsStream(codecJar);
            if (jar != null) {
                try {
                    JarInputStream input = new JarInputStream(jar);
                    JarEntry entry = findResource(input, fileName);
                    if (entry != null) {
                        return new Result(input, codecJar);
                    }
                } catch (IOException e) {
                    System.err.println("Warning: error reading " + codecJar
                            + ":" + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.err.println("Warning: " + codecJar
                        + " not found in classpath!");
            }
        }
        return null;
    }

    private byte[] readData(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        passData(input, output);
        return output.toByteArray();
    }

    private void passData(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = input.read(buffer, 0, buffer.length);
        while (bytesRead != -1) {
            output.write(buffer, 0, bytesRead);
            bytesRead = input.read(buffer, 0, buffer.length);
        }
    }

    /**
     *
     * @see java.lang.ClassLoader#loadClass(java.lang.String)
     */
    public Class< ? > loadClass(String name) throws ClassNotFoundException {
        if (knownClasses.containsKey(name)) {
            System.out.println("loaded " + name);
            if (loadedClasses.containsKey(name)) {
                return loadedClasses.get(name);
            }
            byte[] classData = knownClasses.get(name);
            Class< ? > loadedClass = defineClass(name,
                    classData, 0, classData.length);
            loadedClasses.put(name, loadedClass);
            return loadedClass;
        }
        return super.loadClass(name);
    }

    protected URL findResource(String name) {
        if (knownResourceURLs.containsKey(name)) {
            return knownResourceURLs.get(name);
        }
        String fileName = getName(name);
        Result result = find(fileName);
        if (result != null) {
            try {
                return new URL(Handler.PROTOCOL + ":" + result.codecJar + "?"
                        + fileName);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected String findLibrary(String name) {
        if (knownLibraryPaths.containsKey(name)) {
            return knownLibraryPaths.get(name);
        }
        String fileName = System.mapLibraryName(name);
        Result result = find("native/" + fileName);
        if (result == null) {
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
                result = find("native/" + prefix + fileName);
            }
        }

        if (result != null) {
            try {
                File codecJarFile = new File(result.codecJar);
                File tempDir = new File(System.getProperty("java.io.tmpdir"),
                        codecJarFile.getName() + uniqueId);
                tempDir.mkdirs();
                deleter.addDirectory(tempDir);
                File tempFile = new File(tempDir, fileName);
                FileOutputStream output = new FileOutputStream(tempFile);
                passData(result.inputStream, output);
                output.close();
                deleter.addFile(tempFile);
                result.inputStream.close();
                knownLibraryPaths.put(name, tempFile.getAbsolutePath());
                return tempFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
