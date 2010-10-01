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

import java.lang.reflect.Method;

/**
 * A utility class for loading native libraries from the classpath
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class NativeLoader {

     private static final NativeClassLoader CLASS_LOADER =
         new NativeClassLoader(Loader.class.getCanonicalName());

     /**
      * Loads libraries from the class path.
      *
      * The libraries must be stored in either the /native directory within
      * the class path, or, dependent on the current os or architecture:
      *     /native/linux32 - 32-bit linux libraries
      *     /native/linux64 - 64-bit linux libraries
      *     /native/windows32 - 32-bit windows libraries
      *     /native/windows64 - 64-bit windows libraries
      *     /native/macosx - Mac OS X libraries
      *
      * Once loaded with this funtion, libraries will be associated with the
      * JVM.  System.loadLibrary must then be used to load the library within
      * the class that it is to be used from.
      *
      * @param libraries The libraries to load
     * @throws UnsatisfiedLinkError
      */
     public static void loadLibraries(String... libraries)
             throws UnsatisfiedLinkError {
         try {
             Class< ? > loaderClass = Class.forName(
                     Loader.class.getCanonicalName(), true, CLASS_LOADER);
             Method loaderMethod = loaderClass.getMethod("loadLibraries",
                     new Class[]{String[].class});
             loaderMethod.invoke(null, new Object[]{libraries});
         } catch (Exception e) {
             e.printStackTrace();
             throw new UnsatisfiedLinkError(e.getMessage());
         }
     }
}
