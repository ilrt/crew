/*
 * @(#)ImageExtractorServlet.java
 * Created: 14 Nov 2007
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

package net.crew_vre.web.servlet.image;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.media.format.UnsupportedFormatException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import net.crew_vre.media.rtptype.RtpTypeRepository;
import net.crew_vre.media.rtptype.impl.RtpTypeRepositoryXmlImpl;

/**
 * A servlet for extracting images from a stream
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ImageServlet extends HttpServlet {

    private static final Pattern pattern = Pattern.compile(
            "/(.*)/([^\\.]*)(\\..*)?");

    private File recordingDirectory = null;

    private RtpTypeRepository rtpTypeRepository = null;

    /**
     *
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException {
        String path = getInitParameter("recordingDirectory");
        String rtpTypeRepositoryFile = getInitParameter("rtpTypeRepository");
        if (path == null) {
            throw new ServletException("recordingDirectory must be specified");
        }
        if (rtpTypeRepositoryFile == null){
            throw new ServletException("rtpTypeRepositoryFile must be specified");
        } else {
        	try {
				rtpTypeRepository = new RtpTypeRepositoryXmlImpl(rtpTypeRepositoryFile);
			} catch (Exception e) {
				throw new ServletException(e);
			}
        }
        recordingDirectory = new File(path);
    }

    /**
    *
    * @see javax.servlet.http.HttpServlet#doPost(
    *     javax.servlet.http.HttpServletRequest,
    *     javax.servlet.http.HttpServletResponse)
    */
   public void doPost(HttpServletRequest request,
           HttpServletResponse response) throws IOException {
       doGet(request, response);
   }

   /**
    *
    * @throws IOException
 * @throws IOException
    * @see javax.servlet.http.HttpServlet#doGet(
    *     javax.servlet.http.HttpServletRequest,
    *     javax.servlet.http.HttpServletResponse)
    */
   public void doGet(HttpServletRequest request,
           HttpServletResponse response) throws IOException {
       String requestName = request.getPathInfo();
       Matcher matcher = pattern.matcher(requestName);
       String sessionId = null;
       String streamId = null;
       String extension = null;
       if (matcher.matches()) {
           sessionId = matcher.group(1);
           streamId = matcher.group(2);
           extension = matcher.group(3);
       } else {
           throw new IOException("Path format incorrect");
       }

       String comp = request.getParameter("compression");
       if (comp == null) {
           comp = "0.5";
       }
       float compression = Float.valueOf(comp);
       String h = request.getParameter("height");
       String w = request.getParameter("width");
       if (h == null) {
           h = "-1";
       }
       if (w == null) {
           w = "-1";
       }
       int width = Integer.parseInt(w);
       int height = Integer.parseInt(h);
       String off = request.getParameter("offset");
       if (off == null) {
           off = "0";
       }
       long offset = Long.parseLong(off);

       try {
           File sessionDir = new File(recordingDirectory, sessionId);
           File file = new File(sessionDir, streamId + "_" + offset + ".jpg");
           File streamFile = new File(sessionDir, streamId);
           BufferedImage image = null;
           if (file.exists()) {
               image = ImageIO.read(file);
           } else {
               ImageExtractor extractor = new ImageExtractor(
                       streamFile.getAbsolutePath(),rtpTypeRepository);
               image = (BufferedImage) extractor.getImage(offset);
           }
           OutputStream output = response.getOutputStream();
           ImageOutputStream ios = ImageIO.createImageOutputStream(output);
           ImageWriter writer = null;
           JPEGImageWriteParam param =
               new JPEGImageWriteParam(Locale.getDefault());
           Iterator<ImageWriter> iter = null;
           param.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
           param.setCompressionQuality(compression);
           response.setHeader("Cache-Control", "max-age=86400");
           response.setHeader("Content-Disposition", "inline; filename="
                   + streamId + extension + ";");
           response.flushBuffer();
           if ((height != -1) || (width != -1)) {
               double scaleX = 0;
               double scaleY = 0;
               AffineTransform xform = null;
               BufferedImage oldImage = null;
               Graphics2D g = null;
               scaleX = width / (double) image.getWidth();
               scaleY = height / (double) image.getHeight();
               if (height == -1) {
                   scaleY = scaleX;
                   height = (int) (image.getHeight() * scaleY);
               } else if (width == -1) {
                   scaleX = scaleY;
                   width = (int) (image.getWidth() * scaleX);
               }
               xform = AffineTransform.getScaleInstance(scaleX, scaleY);
               oldImage = image;
               image = new BufferedImage(width, height, image.getType());
               g = image.createGraphics();
               g.drawRenderedImage(oldImage, xform);
               g.dispose();
           }
           iter = ImageIO.getImageWritersByFormatName("jpg");
           if (iter.hasNext()) {
               writer = iter.next();
           }
           writer.setOutput(ios);
           writer.write(null, new IIOImage(image, null, null), param);
           ios.flush();
           writer.dispose();
           ios.close();
       } catch (UnsupportedFormatException e) {
           e.printStackTrace();
           throw new IOException(e.getMessage());
       }
   }
}
