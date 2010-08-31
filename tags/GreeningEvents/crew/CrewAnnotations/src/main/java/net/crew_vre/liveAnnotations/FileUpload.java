/*
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

package net.crew_vre.liveAnnotations;

import java.io.File;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

public class FileUpload {

    public static void uploadFiles(String recordingUri, File[] files,
            long sessionOffset, String url) {

        HttpClient client = new HttpClient();

        PostMethod httppost = new PostMethod(url);

        httppost.addParameter("recordingUri", recordingUri);
        httppost.addParameter("sessionOffset", String.valueOf(sessionOffset));

        httppost.setRequestEntity(new CrewZipStreamRequestEntity(files));
        httppost.setContentChunked(true);

        try {
            client.executeMethod(httppost);
            if (httppost.getStatusCode() == HttpStatus.SC_OK) {
                System.out.println(httppost.getResponseBodyAsString());
            } else {
                System.out.println("Unexpected failure: "
                        + httppost.getStatusLine().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httppost.releaseConnection();
        }
    }

    public static void uploadFile(String recordingUri, String filename,
            long sessionOffset, String url) {

        File[] files = new File[] {new File(filename)};
        uploadFiles(recordingUri, files, sessionOffset, url);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        String filename = "";
        String url = "";
        String uri = "";
        long offsett = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-f")) {
                filename = (args[i + 1]);
                i++;
            } else if (args[i].equals("-h")) {
                url = (args[i + 1]);
                i++;
            } else if (args[i].equals("-u")) {
                uri = (args[i + 1]);
                i++;
            } else if (args[i].equals("-o")) {
                offsett = Long.parseLong(args[i + 1]);
                i++;
            }
        }
        uploadFile(uri, filename, offsett, url);
    }

}
