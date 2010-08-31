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

package net.crew_vre.codec.linear;

import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Format;
import javax.media.format.AudioFormat;

/**
 * Converts the rate between two values
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class RateConverter implements Codec {

    private double ratio = 0;

    private int bytesPerSample = 0;

    private AudioFormat inputFormat = null;

    private AudioFormat outputFormat = null;

    /**
     *
     * @see javax.media.Codec#getSupportedInputFormats()
     */
    public Format[] getSupportedInputFormats() {
        return new Format[]{
            new AudioFormat(AudioFormat.LINEAR)
        };
    }

    /**
     *
     * @see javax.media.Codec#getSupportedOutputFormats(javax.media.Format)
     */
    public Format[] getSupportedOutputFormats(Format format) {
        if (format == null) {
            return new Format[]{
                new AudioFormat(AudioFormat.LINEAR)
            };
        }

        if (format instanceof AudioFormat) {
            AudioFormat af = (AudioFormat) format;
            if (af.getEncoding().equals(AudioFormat.LINEAR)) {
                int sampleSize = af.getSampleSizeInBits();
                int channels = af.getChannels();
                int signed = af.getSigned();
                int endian = af.getEndian();

                return new Format[] {
                    new AudioFormat(AudioFormat.LINEAR, 8000,
                            sampleSize, channels, endian, signed),
                    new AudioFormat(AudioFormat.LINEAR, 16000,
                            sampleSize, channels, endian, signed),
                    new AudioFormat(AudioFormat.LINEAR, 32000,
                            sampleSize, channels, endian, signed),
                    new AudioFormat(AudioFormat.LINEAR, 44100,
                            sampleSize, channels, endian, signed),
                    new AudioFormat(AudioFormat.LINEAR, 48000,
                            sampleSize, channels, endian, signed)
                };
            }
        }

        return null;
    }

    /**
     *
     * @see javax.media.Codec#process(javax.media.Buffer, javax.media.Buffer)
     */
    public int process(Buffer input, Buffer output) {
        int inLength = input.getLength();
        int inOffset = input.getOffset();
        int inEnd = inLength + inOffset;
        byte[] inData = (byte[]) input.getData();
        int outLength = (int) Math.round(
                ((inLength * outputFormat.getSampleRate())
                / inputFormat.getSampleRate()) + 0.5);
        byte[] outData = (byte[]) output.getData();
        if ((outData == null)
                || (outData.length != outLength)) {
            outData = new byte[outLength];
        }
        int inPos = inOffset;
        int outPos = 0;
        double sum = 0.0;
        while (inPos < inEnd) {
            for (int c = 0; c < outputFormat.getChannels(); c++) {
                int val = 0;
                for (int j = 0; j < bytesPerSample; j++) {
                    int shift = (3 - (bytesPerSample - j)) * 8;
                    if (inputFormat.getEndian() == AudioFormat.LITTLE_ENDIAN) {
                        shift = j * 8;
                    }
                    int inArrayPos = inPos + (c * bytesPerSample);
                    val |= (inData[inArrayPos + j] & 0xFF) << shift;
                }

                while (sum < 1.0) {
                    if (outPos < outLength) {
                        int v = val;
                        for (int j = 0; (j < bytesPerSample)
                                && (outPos < outLength); j++) {
                            outData[outPos++] =
                                (byte) (v & 0xFF);
                            v >>= 8;
                        }
                    }
                    sum += ratio;
                }
            }
            sum -= 1.0;
            inPos += bytesPerSample * outputFormat.getChannels();
        }
        output.setData(outData);
        output.setLength(outData.length);
        output.setOffset(0);
        output.setFormat(outputFormat);

        return BUFFER_PROCESSED_OK;
    }

    /**
     *
     * @see javax.media.Codec#setInputFormat(javax.media.Format)
     */
    public Format setInputFormat(Format format) {
        if (format instanceof AudioFormat) {
            AudioFormat af = (AudioFormat) format;
            if (af.getEncoding().equals(AudioFormat.LINEAR)) {
                inputFormat = af;
                if (outputFormat != null) {
                    ratio = af.getSampleRate() / outputFormat.getSampleRate();
                }
                return format;
            }
        }

        return null;
    }

    /**
     *
     * @see javax.media.Codec#setOutputFormat(javax.media.Format)
     */
    public Format setOutputFormat(Format format) {
        if (format instanceof AudioFormat) {
            AudioFormat af = (AudioFormat) format;
            if (af.getEncoding().equals(AudioFormat.LINEAR)) {
                outputFormat = af;
                if (inputFormat != null) {
                    ratio = inputFormat.getSampleRate() / af.getSampleRate();
                    bytesPerSample = outputFormat.getSampleSizeInBits() / 8;
                }
                return format;
            }
        }

        return null;
    }

    /**
     *
     * @see javax.media.PlugIn#close()
     */
    public void close() {
        // Does Nothing
    }

    /**
     *
     * @see javax.media.PlugIn#getName()
     */
    public String getName() {
        return "RateConverter";
    }

    /**
     *
     * @see javax.media.PlugIn#open()
     */
    public void open() {
        // Does Nothing
    }

    /**
     *
     * @see javax.media.PlugIn#reset()
     */
    public void reset() {
        // Does Nothing
    }

    /**
     *
     * @see javax.media.Controls#getControl(java.lang.String)
     */
    public Object getControl(String className) {
        return null;
    }

    /**
     *
     * @see javax.media.Controls#getControls()
     */
    public Object[] getControls() {
        return new Object[0];
    }

}
