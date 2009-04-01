/*
 * @(#)BlankLineResponseWrapper.java
 * Created: 1 Jun 2007
 * Version: 1.0
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
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
 */

package net.crew_vre.filters;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * A Wrapper for the BlankLineFilter
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class BlankLineResponseWrapper extends HttpServletResponseWrapper {

    private CharArrayWriter writer = new CharArrayWriter();

    private boolean gotOutput=false;

    /**
     * Creates a new BlankLineResponseWrapper
     * @param response The original response object
     */
    public BlankLineResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    /**
     * @see javax.servlet.ServletResponseWrapper#getOutputStream()
     */
    public ServletOutputStream getOutputStream() throws IOException{
        gotOutput=true;
        return super.getOutputStream();
    }

    /**
     * Return the output available flag
     * @return  the output available flag
     */
    public boolean getGotOutput(){
        return gotOutput;
    }

    /**
     * @see javax.servlet.ServletResponseWrapper#getWriter()
     */
    public PrintWriter getWriter() {
        return new PrintWriter(writer);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
//        System.err.println("BlankLineFilterResponseType: " +getContentType().toLowerCase());
 
        if ((getContentType() != null) &&
                getContentType().toLowerCase().startsWith("text/xml")) {
            String result = "";
            char[] chars = writer.toCharArray();
            BufferedReader reader = new BufferedReader(
                    new CharArrayReader(chars));
            try {
                String line = reader.readLine();
                while (line != null) {
                    if (!line.trim().equals("")) {
                        result += line.trim();
                    }
                    line = reader.readLine();
                }
//                System.out.println(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
        return writer.toString();
    }

}
