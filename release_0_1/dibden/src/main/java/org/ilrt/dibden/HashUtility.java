/*
 * Copyright (c) 2008, University of Bristol
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
 * 3) Neither the name of the University of Bristol nor the names of its
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
package org.ilrt.dibden;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: HashUtility.java 11 2008-06-04 10:41:37Z cmmaj $
 *
 **/
public final class HashUtility {

    private HashUtility() {
    }

    public static String generateHash(final String msg, final String hashAlgorithm) {

        if (!hashAlgorithm.equals("plain")) {
            StringBuffer hexString = new StringBuffer();

            try {

                MessageDigest messageDigest = MessageDigest.getInstance(hashAlgorithm);
                messageDigest.update(msg.getBytes());
                byte[] digest = messageDigest.digest();

                for (byte aDigest : digest) {

                    String val = Integer.toHexString(0xFF & aDigest);

                    if (val.length() < 2) {
                        val = "0" + val;
                    }

                    hexString.append(val);
                }

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            return hexString.toString();
        } else {
            return msg;
        }
    }

}
