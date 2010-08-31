/*
 * @(#)ImageExtractor.java
 * Created: 2 Nov 2007
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

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.Format;
import javax.media.PlugIn;
import javax.media.format.RGBFormat;
import javax.media.format.UnsupportedFormatException;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;

import net.crew_vre.media.MemeticFileReader;
import net.crew_vre.media.processor.SimpleProcessor;
import net.crew_vre.media.rtptype.RtpTypeRepository;

/**
 * Extracts a screen from a memetic stream
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ImageExtractor extends MemeticFileReader {

    private SimpleProcessor processor = null;

    /**
     * Creates a new ScreenExtractor
     *
     * @param filename The file containing the stream
     * @throws UnsupportedFormatException
     * @throws IOException
     */
    public ImageExtractor(String filename, RtpTypeRepository typeRepository)
            throws UnsupportedFormatException, IOException {
        super(filename,typeRepository);
        processor = new SimpleProcessor(getFormat(), new RGBFormat(null,
                   Format.NOT_SPECIFIED, // maxDataLength
                   Format.intArray,       // type
                   Format.NOT_SPECIFIED,
                   32,  // bpp
                   Format.NOT_SPECIFIED, Format.NOT_SPECIFIED,
                   Format.NOT_SPECIFIED,  // masks
                   Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, // strides
                   RGBFormat.FALSE, // flipped
                   Format.NOT_SPECIFIED)); // endian);
    }

    /**
     * Gets an image for the stream
     * @param offset The offset at which the image should be extracted
     * @return The image at the offset
     * @throws IOException
     */
    public Image getImage(long offset) throws IOException {
        String image = getFilename() + "_" + offset + ".jpg";
        System.err.println("File = " + image);

        File file = new File(image);
        if (file.exists()) {
            return ImageIO.read(file);
        }

        // Find the nearest frame to the offset
        streamSeek(offset - getOffsetShift());

        // Pass frames into the codec one at a time until buffer contains the
        // frame
        boolean isData = readNextPacket();
        int status = PlugIn.OUTPUT_BUFFER_NOT_FILLED;
        while ((status == PlugIn.OUTPUT_BUFFER_NOT_FILLED) && isData
                && (!processor.getOutputBuffer().isDiscard())) {

            // Read the frame into the buffer
            Buffer inputBuffer = getBuffer();
            status = processor.process(inputBuffer);

            if (status == PlugIn.BUFFER_PROCESSED_OK
                    && (getOffset() < offset)) {
                status = PlugIn.OUTPUT_BUFFER_NOT_FILLED;
            }
            if (status == PlugIn.OUTPUT_BUFFER_NOT_FILLED) {
                isData = readNextPacket();
            }
        }

        if (status != PlugIn.BUFFER_PROCESSED_OK) {
            throw new IOException("Error processing frame");
        }

        // Convert the buffer to an image and return it
        BufferToImage convertor = new BufferToImage((VideoFormat)
                processor.getOutputBuffer().getFormat());
        return convertor.createImage(processor.getOutputBuffer());
    }
}
