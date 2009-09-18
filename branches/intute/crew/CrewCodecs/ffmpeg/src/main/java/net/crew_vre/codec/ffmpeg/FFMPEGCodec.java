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

package net.crew_vre.codec.ffmpeg;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.control.BitRateControl;
import javax.media.format.JPEGFormat;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.format.YUVFormat;

/**
 * A Codec that uses FFMPEG
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class FFMPEGCodec implements Codec {

    private static final Integer SYNC = new Integer(0);

    protected static class FFMPEGFormat {

        private VideoFormat encodedFormat = null;

        private VideoFormat decodedFormat = null;

        private int pixFormat = Utils.PIX_FMT_NONE;

        private int codecId = Utils.CODEC_ID_NONE;

        private boolean encoder = false;

        private boolean decoder = false;

        private Dimension[] decodedSizes = null;

        private FFMPEGFormat(VideoFormat encodedFormat, int pixFormat,
                int codecId, boolean encoder, boolean decoder,
                Dimension[] decodedSizes) {
            this.encodedFormat = encodedFormat;
            this.pixFormat = pixFormat;
            this.codecId = codecId;
            this.encoder = encoder;
            this.decoder = decoder;
            this.decodedSizes = decodedSizes;
            this.decodedFormat = Utils.getVideoFormat(this.pixFormat,
                    null, Format.NOT_SPECIFIED);
        }
    }

    private static final HashMap<String, FFMPEGFormat> CODECS =
        new HashMap<String, FFMPEGFormat>();

    private static final Vector<VideoFormat> INPUT_FORMATS
        = new Vector<VideoFormat>();

    private static final Vector<Format> OUTPUT_FORMATS = new Vector<Format>();

    static {
        INPUT_FORMATS.add(new YUVFormat());
        INPUT_FORMATS.add(new RGBFormat());
        OUTPUT_FORMATS.add(new YUVFormat());
        OUTPUT_FORMATS.add(new RGBFormat());
        addCodec("h261", Utils.CODEC_ID_H261, true, true,
                new VideoFormat[]{new VideoFormat(VideoFormat.H261)},
                new int[]{Utils.PIX_FMT_YUV420P},
                new Dimension[]{new Dimension(352, 288),
                                new Dimension(176, 144)});
        addCodec("flv1", Utils.CODEC_ID_FLV1, true, true,
                new VideoFormat[]{new VideoFormat("flv1")},
                new int[]{Utils.PIX_FMT_YUV420P},
                null);
        addCodec("mjpeg", Utils.CODEC_ID_MJPEG, true, true,
                new VideoFormat[]{new JPEGFormat()},
                new int[]{Utils.PIX_FMT_YUVJ420P},
                null);
    }

    private FFMPEGFormat currentCodec = null;

    private VideoFormat inputFormat = null;

    private VideoFormat outputFormat = null;

    private int pixFormat = -1;

    private boolean isEncoding = false;

    private boolean inited = false;

    private byte[] bytedata = null;

    private int[] intdata = null;

    private short[] shortdata = null;

    // The address of the native FFMpegJ instance
    private long ffmpegj = 0;

    // The address of the native codec context instance
    private long codecContext = 0;

    // The bit rate to encode at
    private BitRateControl bitRateControl = new BitRate();

    private boolean closed = false;

    /**
     * Adds a new codec
     *
     * Note that the codec must support *all combinations* of the specified
     * encodedFormats, pixelFormats and decodedSizes.  If this is not the case,
     * you can call this function more than once for the codec with the
     * appropriate combinations.
     *
     * Similarly, if the codec supports both encoding and decoding, it must
     * support these combinations in both directions.  Again, if this is not the
     * case, you can call this function more than once for the codec with the
     * appropriate combinations.
     *
     * @param name The name of the codec
     * @param ffmpegCodecId The FFMPEG id of the codec
     * @param encoder True if the codec can encode
     * @param decoder True if the codec can decode
     * @param encodedFormats The encoded JMF formats
     * @param pixFormats The decoded FFMPEG formats
     * @param decodedSizes The decoded size that the codec supports, or null
     *                         if all sizes supported
     */
    public static void addCodec(String name, int ffmpegCodecId, boolean encoder,
            boolean decoder, VideoFormat[] encodedFormats, int[] pixFormats,
            Dimension[] decodedSizes) {
        for (int i = 0; i < encodedFormats.length; i++) {
            if (encoder) {
                if (!OUTPUT_FORMATS.contains(encodedFormats[i])) {
                    OUTPUT_FORMATS.add(encodedFormats[i]);
                }
            }
            if (decoder) {
                if (!INPUT_FORMATS.contains(encodedFormats[i])) {
                    INPUT_FORMATS.add(encodedFormats[i]);
                }
            }
            for (int j = 0; j < pixFormats.length; j++) {
                CODECS.put(name, new FFMPEGFormat(encodedFormats[i],
                        pixFormats[j], ffmpegCodecId, encoder, decoder,
                        decodedSizes));
            }
        }
    }

    /**
     *
     * @see javax.media.Codec#getSupportedInputFormats()
     */
    public Format[] getSupportedInputFormats() {
        return INPUT_FORMATS.toArray(new Format[0]);
    }

    /**
     * Utility function to get the input formats for an output format
     * @param format The format to get input formats for
     * @return The set of input formats
     */
    public Format[] getSupportedInputFormats(Format format) {
        if (format == null) {
            return INPUT_FORMATS.toArray(new Format[0]);
        }

        if (!(format instanceof VideoFormat)) {
            return null;
        }

        VideoFormat vf = (VideoFormat) format;
        Vector<Format> supportedInputFormats = new Vector<Format>();

        if ((format instanceof YUVFormat) || (format instanceof RGBFormat)) {
            Iterator<FFMPEGFormat> codecs =
                FFMPEGCodec.CODECS.values().iterator();
            while (codecs.hasNext()) {
                FFMPEGFormat codec = codecs.next();
                if (codec.decoder) {
                    if ((codec.decodedFormat != null)
                            && codec.decodedFormat.matches(format)) {
                        if (!supportedInputFormats.contains(
                                codec.encodedFormat)) {
                            supportedInputFormats.add(codec.encodedFormat);
                        }
                    } else {
                        if (Utils.canBeConverted(vf)) {
                            if (!supportedInputFormats.contains(
                                    codec.encodedFormat)) {
                                supportedInputFormats.add(codec.encodedFormat);
                            }
                        }
                    }
                }
            }
        } else {
            Iterator<FFMPEGFormat> codecs =
                FFMPEGCodec.CODECS.values().iterator();
            while (codecs.hasNext()) {
                FFMPEGFormat codec = codecs.next();
                if (codec.encoder) {
                    if (codec.encodedFormat.matches(format)) {
                        Format[] inputFormats = Utils.getVideoFormats(null,
                                Format.NOT_SPECIFIED);
                        for (int i = 0; i < inputFormats.length; i++) {
                            if (!supportedInputFormats.contains(
                                    inputFormats[i])) {
                                supportedInputFormats.add(inputFormats[i]);
                            }
                        }
                    }
                }
            }
        }

        return supportedInputFormats.toArray(new Format[0]);
    }

    /**
     *
     * @see javax.media.Codec#getSupportedOutputFormats(javax.media.Format)
     */
    public Format[] getSupportedOutputFormats(Format format) {
        System.err.println("Checking for output formats for input format "
                + format);
        if (format == null) {
            return OUTPUT_FORMATS.toArray(new Format[0]);
        }

        if (!(format instanceof VideoFormat)) {
            return null;
        }

        VideoFormat vf = (VideoFormat) format;

        Vector<Format> supportedOutputFormats = new Vector<Format>();
        if ((format instanceof YUVFormat) || (format instanceof RGBFormat)) {
            Iterator<FFMPEGFormat> codecs =
                FFMPEGCodec.CODECS.values().iterator();
            while (codecs.hasNext()) {
                FFMPEGFormat codec = codecs.next();
                if (codec.encoder) {
                    if ((codec.decodedFormat != null)
                            && codec.decodedFormat.matches(format)) {
                        if (!supportedOutputFormats.contains(
                                codec.encodedFormat)) {
                            supportedOutputFormats.add(codec.encodedFormat);
                        }
                    } else {
                        if (Utils.canBeConverted(vf)) {
                            if (!supportedOutputFormats.contains(
                                    codec.encodedFormat)) {
                                supportedOutputFormats.add(codec.encodedFormat);
                            }
                        }
                    }
                }
            }
        } else {
            Iterator<FFMPEGFormat> codecs =
                FFMPEGCodec.CODECS.values().iterator();
            while (codecs.hasNext()) {
                FFMPEGFormat codec = codecs.next();
                if (codec.decoder) {
                    if (codec.encodedFormat.matches(format)) {
                        Format[] outputFormats = Utils.getVideoFormats(null,
                                Format.NOT_SPECIFIED);
                        for (int i = 0; i < outputFormats.length; i++) {
                            if (!supportedOutputFormats.contains(
                                    outputFormats[i])) {
                                supportedOutputFormats.add(outputFormats[i]);
                            }
                        }
                    }
                }
            }
        }
        return supportedOutputFormats.toArray(new Format[0]);
    }

    /**
     *
     * @see javax.media.Codec#process(javax.media.Buffer, javax.media.Buffer)
     */
    public int process(Buffer input, Buffer output) {
        synchronized (SYNC) {
            if (closed) {
                return BUFFER_PROCESSED_FAILED;
            }
            if (!inited) {
                inputFormat = (VideoFormat) input.getFormat();
                init();
            }
            if (outputFormat.getDataType() == Format.byteArray) {
                output.setData(bytedata);
                output.setLength(bytedata.length);
            } else if (outputFormat.getDataType() == Format.intArray) {
                output.setData(intdata);
                output.setLength(intdata.length * 4);
            } else if (outputFormat.getDataType() == Format.shortArray) {
                output.setData(shortdata);
                output.setLength(shortdata.length * 2);
            }

            output.setOffset(0);
            output.setFormat(outputFormat);
            int retval = 0;
            if (isEncoding) {
                retval = encodeNative(input, output);
            } else {
                //System.err.println("Decoding native");
                retval = decodeNative(input, output);
                //System.err.println("Done decoding native");
            }
            if (retval != BUFFER_PROCESSED_FAILED) {
                if (outputFormat.getDataType() == Format.intArray) {
                    output.setLength(output.getLength() / 4);
                    output.setOffset(output.getOffset() / 4);
                } else if (outputFormat.getDataType() == Format.shortArray) {
                    output.setLength(output.getLength() / 2);
                    output.setOffset(output.getOffset() / 2);
                }
                output.setTimeStamp(input.getTimeStamp());
            }
            return retval;
        }
    }

    private Format getCompatibleFormat(VideoFormat vf, boolean settingInput) {
        System.err.println("VideoFormat = " + vf
                + ", input = " + settingInput + ", currentCodec = " + currentCodec);
        if (currentCodec != null) {
            System.err.println("isEncoding = " + isEncoding);
            if (isEncoding == settingInput) {
                Format decodedFormat = Utils.getVideoFormat(
                        currentCodec.pixFormat, vf.getSize(),
                        Format.NOT_SPECIFIED);
                if (((decodedFormat != null) && decodedFormat.matches(vf))
                        || Utils.canBeConverted(vf)) {
                    int pixelFormat = Utils.getPixFormat(vf);
                    if (pixelFormat != currentCodec.pixFormat) {
                        this.pixFormat = pixelFormat;
                    }
                    if (settingInput) {
                        inputFormat = vf;
                    } else {
                        outputFormat = vf;
                    }
                    return vf;
                }
                currentCodec = null;
                pixFormat = -1;
            } else {
                if (vf.matches(currentCodec.encodedFormat)) {
                    if (settingInput) {
                        inputFormat = vf;
                    } else {
                        outputFormat = vf;
                    }
                    return vf;
                }
                currentCodec = null;
                pixFormat = -1;
            }
        }
        if (currentCodec == null) {
            VideoFormat compareFormat = null;
            if (settingInput) {
                compareFormat = outputFormat;
            } else {
                compareFormat = inputFormat;
            }
            if ((vf instanceof YUVFormat) || (vf instanceof RGBFormat)) {
                Iterator<FFMPEGFormat> codecs =
                    FFMPEGCodec.CODECS.values().iterator();
                while (codecs.hasNext()) {
                    FFMPEGFormat codec = codecs.next();
                    if ((codec.encoder && settingInput)
                            || (codec.decoder && !settingInput)) {
                        if ((compareFormat == null)
                                || (compareFormat.matches(
                                        codec.encodedFormat))) {
                            if (((codec.decodedFormat != null)
                                    && (codec.decodedFormat.matches(vf)))
                                        || Utils.canBeConverted(vf)) {
                                int pixelFormat = Utils.getPixFormat(vf);
                                System.err.println("PixFormat = " + pixelFormat
                                        + " codec format = " + codec.pixFormat);
                                if (pixelFormat != codec.pixFormat) {
                                    pixFormat = pixelFormat;
                                }
                                this.isEncoding = settingInput;
                                this.currentCodec = codec;
                                if (settingInput) {
                                    inputFormat = vf;
                                } else {
                                    outputFormat = vf;
                                }
                                return vf;
                            }
                        }
                    }
                }
            } else {
                Iterator<FFMPEGFormat> codecs =
                    FFMPEGCodec.CODECS.values().iterator();
                while (codecs.hasNext()) {
                    FFMPEGFormat codec = codecs.next();
                    if ((codec.decoder && settingInput)
                            || (codec.encoder && !settingInput)) {
                        if ((compareFormat == null)
                                || ((codec.decodedFormat != null)
                                        && compareFormat.matches(
                                                codec.decodedFormat))
                                || Utils.canBeConverted(compareFormat)) {
                            if (codec.encodedFormat.matches(vf)) {
                                this.isEncoding = !settingInput;
                                this.currentCodec = codec;
                                if (settingInput) {
                                    inputFormat = vf;
                                } else {
                                    outputFormat = vf;
                                }
                                return vf;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     *
     * @see javax.media.Codec#setInputFormat(javax.media.Format)
     */
    public Format setInputFormat(Format format) {
        System.err.println("Setting input format to " + format);
        if (!(format instanceof VideoFormat)) {
            return null;
        }
        VideoFormat vf = (VideoFormat) format;
        return getCompatibleFormat(vf, true);
    }

    /**
     *
     * @see javax.media.Codec#setOutputFormat(javax.media.Format)
     */
    public Format setOutputFormat(Format format) {
        if (!(format instanceof VideoFormat)) {
            return null;
        }
        VideoFormat vf = (VideoFormat) format;
        return getCompatibleFormat(vf, false);
    }

    /**
     *
     * @see javax.media.PlugIn#close()
     */
    public void close() {
        System.err.println("Closing...");
        synchronized (SYNC) {
            if (!closed) {
                closeCodec();
                bytedata = null;
                intdata = null;
                shortdata = null;
            }
            closed = true;
        }
        System.err.println("Finished closing");
    }

    /**
     *
     * @see javax.media.PlugIn#getName()
     */
    public String getName() {
        return "FFMPEGCodec";
    }

    private void init() {
        System.err.println("Initializing Codec with input = " + inputFormat
                + " and output = " + outputFormat + ", pixFormat = "
                + pixFormat + ", codecFormat = " + currentCodec.pixFormat);
        int outputSize = 0;
        Dimension size = inputFormat.getSize();
        Dimension outSize = outputFormat.getSize();
        boolean flipped = false;
        if (isEncoding && (inputFormat instanceof RGBFormat)) {
            flipped = ((RGBFormat) inputFormat).getFlipped() == Format.TRUE;
        } else if (!isEncoding && (outputFormat instanceof RGBFormat)) {
            flipped = ((RGBFormat) outputFormat).getFlipped() == Format.TRUE;
        }
        if (outSize == null) {
            if (!isEncoding) {
                outSize = size;
            } else {
                if ((currentCodec.decodedSizes == null)
                        || (currentCodec.decodedSizes.length == 0)) {
                    outSize = size;
                } else {

                    // Find the closest match larger than the size if possible
                    int outSizeError = 0;
                    for (int i = 0; i < currentCodec.decodedSizes.length; i++) {
                        int currentError = (currentCodec.decodedSizes[i].width
                                - size.width);
                        currentError += (currentCodec.decodedSizes[i].height
                                - size.height);
                        if (outSize == null) {
                            outSize = currentCodec.decodedSizes[i];
                            outSizeError = currentError;
                        } else {
                            if ((outSizeError < 0)
                                    && (currentError > outSizeError)) {
                                outSize = currentCodec.decodedSizes[i];
                                outSizeError = currentError;
                            } else if ((outSizeError > 0) && (currentError > 0)
                                    && (currentError < outSizeError)) {
                                outSize = currentCodec.decodedSizes[i];
                                outSizeError = currentError;
                            }
                        }
                    }
                }
            }
        }
        if (pixFormat != -1) {
            synchronized (SYNC) {
                System.err.println("Initializing Codec: pixFormat = " + pixFormat
                        + " width = " + size.width + " height = " + size.height
                        + " intermediatePixFormat = " + currentCodec.pixFormat
                        + " interwidth = " + outSize.width + " interheight = " + outSize.height);
                outputSize = init(pixFormat, size.width, size.height,
                        currentCodec.pixFormat, outSize.width, outSize.height,
                        flipped);
            }
            if (!isEncoding) {
                outputFormat = Utils.getVideoFormat(pixFormat, size,
                        outputFormat.getFrameRate());
            }
        } else {
            synchronized (SYNC) {
                outputSize = init(currentCodec.pixFormat, size.width,
                        size.height, currentCodec.pixFormat, outSize.width,
                        outSize.height, flipped);
            }
            if (!isEncoding) {
                outputFormat = Utils.getVideoFormat(currentCodec.pixFormat,
                        outSize, outputFormat.getFrameRate());
            }
        }
        if (isEncoding) {
            if (outputFormat.getEncoding().equals(VideoFormat.JPEG)) {
                outputFormat = new FFMpegJPEGVideoFormat(new JPEGFormat(),
                        codecContext);
            } else {
                outputFormat = new FFMpegVideoFormat(outputFormat,
                        codecContext);
            }
            if (outputFormat.getSize() == null) {
                System.err.println("Setting size on output to " + outSize);
                ((FFMpegBasicVideoFormat) outputFormat).setSize(outSize);
            }
        }
        if (outputFormat.getDataType() == Format.byteArray) {
            bytedata = new byte[outputSize];
        } else if (outputFormat.getDataType() == Format.intArray) {
            intdata = new int[outputSize / 4];
        } else if (outputFormat.getDataType() == Format.shortArray) {
            shortdata = new short[outputSize / 2];
        }
        inited = true;
    }

    /**
     * Gets the ffmpegj object
     * @return The ffmpegj object to the native method
     */
    protected long getFFMpegJ() {
        return ffmpegj;
    }

    /**
     * Sets the ffmpegj object from a native mathod
     * @param ffmpegj The ffmpegj object to set
     */
    protected void setFFMpegJ(long ffmpegj) {
        this.ffmpegj = ffmpegj;
    }

    private native long openCodec(boolean encoding, int codecId);

    private native int init(int pixFmt, int width, int height,
            int intermediatePixFmt, int intermediateWidth,
            int intermediateHeight, boolean flipped);

    private native int decodeNative(Buffer input, Buffer output);

    private native int encodeNative(Buffer input, Buffer output);

    private native boolean closeCodec();

    /**
     *
     * @see javax.media.PlugIn#open()
     */
    public void open() throws ResourceUnavailableException {
        System.err.println("Opening...");
        if (inputFormat == null) {
            throw new ResourceUnavailableException(
                   "Input format not set or not compatible with output format");
        }
        if (outputFormat == null) {
            throw new ResourceUnavailableException(
                   "Output format not set or not compatible with input format");
        }
        System.err.println("Codec is " + currentCodec.codecId + " encoding = "
                + isEncoding);

        try {
            System.loadLibrary("ffmpegj");
            codecContext = openCodec(isEncoding, currentCodec.codecId);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new ResourceUnavailableException("Error initializing FFMPEG");
        }
        System.err.println("Finished opening");
    }

    /**
     *
     * @see javax.media.PlugIn#reset()
     */
    public void reset() {
        /*close();
        try {
            open();
        } catch (ResourceUnavailableException e) {
            e.printStackTrace();
        } */
    }

    /**
     *
     * @see javax.media.Controls#getControl(java.lang.String)
     */
    public Object getControl(String s) {
        if (s.equals("javax.media.control.BitRateControl")) {
            return bitRateControl;
        }
        return null;
    }

    /**
     *
     * @see javax.media.Controls#getControls()
     */
    public Object[] getControls() {
        return new Object[]{bitRateControl};
    }


}
