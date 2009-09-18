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


package net.crew_vre.media.processor;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Format;
import javax.media.Multiplexer;
import javax.media.PlugIn;
import javax.media.PlugInManager;
import javax.media.Renderer;
import javax.media.ResourceUnavailableException;
import javax.media.format.AudioFormat;
import javax.media.format.RGBFormat;
import javax.media.format.UnsupportedFormatException;
import javax.media.format.YUVFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferDataSource;

import net.crew_vre.media.Misc;

/**
 * Performs simple processing operations
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class SimpleProcessor {

    private Codec[] codecs = null;

    private Format[] inputFormats = null;

    private Format[] outputFormats = null;

    private Buffer[] outputBuffers = null;

    private Buffer inputBuffer = new Buffer();

    private Renderer renderer = null;

    private Multiplexer multiplexer = null;

    private int track = 0;

    private ProcessingThread thread = null;

    /**
     * Creates a new Processor for a multiplexer
     *
     * @param inputFormat The input format to start with
     * @param multiplexer The multiplexer to finish with
     * @param track the track to set in the multiplexer
     * @throws UnsupportedFormatException
     * @throws ResourceUnavailableException
     */
    public SimpleProcessor(Format inputFormat, Multiplexer multiplexer,
            int track) throws UnsupportedFormatException,
            ResourceUnavailableException {
        init(inputFormat, multiplexer, track);
    }

    /**
     * Creates a new Processor for a renderer
     *
     * @param inputFormat The input format to start with
     * @param renderer The renderer to finish with
     * @throws UnsupportedFormatException
     * @throws ResourceUnavailableException
     */
    public SimpleProcessor(Format inputFormat, Renderer renderer)
            throws UnsupportedFormatException, ResourceUnavailableException {
        init(inputFormat, renderer);
    }

    /**
     * Creates a new SimpleProcessor
     * @param inputFormat The input format
     * @param outputFormat The desired output format
     * @throws UnsupportedFormatException
     */
    public SimpleProcessor(Format inputFormat, Format outputFormat)
            throws UnsupportedFormatException {
        init(inputFormat, outputFormat);
    }

    private void init(Format inputFormat, Multiplexer multiplexer, int track)
            throws UnsupportedFormatException, ResourceUnavailableException {
        this.multiplexer = multiplexer;
        this.track = track;

        Format[] formats = multiplexer.getSupportedInputFormats();
        boolean inited = false;
        for (int i = 0; (i < formats.length) && !inited; i++) {
            System.err.println("Trying format " + formats[i]);
            if (formats[i].getClass().isInstance(inputFormat)) {
                try {
                    init(inputFormat, formats[i]);
                    multiplexer.setInputFormat(outputFormats[codecs.length - 1],
                            track);
                    multiplexer.open();
                    inited = true;
                } catch (UnsupportedFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!inited) {
            throw new UnsupportedFormatException(inputFormat);
        }
    }

    private void init(Format inputFormat, Renderer renderer)
            throws UnsupportedFormatException, ResourceUnavailableException {
        this.renderer = renderer;
        Format[] formats = renderer.getSupportedInputFormats();
        boolean inited = false;
        for (int i = 0; (formats != null) && (i < formats.length)
                && !inited; i++) {
            System.err.println("Checking format " + formats[i] + " for input = " + inputFormat);
            if (formats[i].matches(inputFormat)
                    && inputFormat.matches(formats[i])) {
                try {
                    System.err.println("Trying direct format " + formats[i]);
                    init(inputFormat, formats[i]);
                    Format f = renderer.setInputFormat(
                            outputFormats[codecs.length - 1]);
                    if (f != null) {
                        renderer.open();
                        inited = true;
                    }
                } catch (UnsupportedFormatException e) {
                    // Do Nothing
                }
            }
        }
        for (int i = 0; (formats != null) && (i < formats.length)
                && !inited; i++) {
            try {
                init(inputFormat, formats[i]);
                renderer.setInputFormat(outputFormats[codecs.length - 1]);
                renderer.open();
                inited = true;
            } catch (UnsupportedFormatException e) {
                // Do Nothing
            }
        }
        if (!inited) {
            throw new UnsupportedFormatException(inputFormat);
        }
    }

    private void init(Format inputFormat, Format outputFormat)
            throws UnsupportedFormatException {
        HashMap<String, Boolean> searched = new HashMap<String, Boolean>();
        Codecs codecList = null;
        boolean forward = false;
        if ((outputFormat instanceof RGBFormat)
                || (outputFormat instanceof YUVFormat)
                || (outputFormat instanceof AudioFormat)
                || (!(inputFormat instanceof RGBFormat))
                || (!(inputFormat instanceof YUVFormat))) {
            codecList = search(inputFormat, outputFormat, searched, true);
            forward = true;
        } else {
            codecList = search(inputFormat, outputFormat, searched, false);
            forward = false;
        }
        if (codecList == null) {
            throw new UnsupportedFormatException("Cannot translate from "
                    + inputFormat + " to " + outputFormat, inputFormat);
        }
        codecs = new Codec[codecList.codecList.size()];
        inputFormats = new Format[codecList.inputFormatList.size()];
        outputFormats = new Format[codecList.outputFormatList.size()];
        outputBuffers = new Buffer[inputFormats.length];
        Iterator<Codec> codec = codecList.codecList.iterator();
        Iterator<Format> iFormat = codecList.inputFormatList.iterator();
        Iterator<Format> oFormat = codecList.outputFormatList.iterator();
        for (int i = 0; i < outputBuffers.length; i++) {
            int pos = i;
            if (!forward) {
                pos = outputBuffers.length - i - 1;
            }
            codecs[pos] = codec.next();
            inputFormats[pos] = iFormat.next();
            outputFormats[pos] = oFormat.next();
            System.err.println(
                    inputFormats[pos] + " -> " +  codecs[pos] + " -> " + outputFormats[pos]);
            outputBuffers[pos] = new Buffer();
            outputBuffers[pos].setFormat(outputFormats[pos]);
            outputBuffers[pos].setOffset(0);
            outputBuffers[pos].setLength(0);
            outputBuffers[pos].setFlags(0);
            outputBuffers[pos].setSequenceNumber(0);
            outputBuffers[pos].setTimeStamp(0);
        }
    }

    /**
     * Gets the output buffer
     * @return The output buffer
     */
    public Buffer getOutputBuffer() {
        return outputBuffers[outputBuffers.length - 1];
    }

    /**
     * Gets the output format
     * @return The output format
     */
    public Format getOutputFormat() {
        return outputFormats[outputFormats.length - 1];
    }

    /**
     * Processes an input buffer rendering it if necessary
     * @param inputBuffer The buffer to process
     * @return The status of the processing
     */
    public int process(Buffer inputBuffer) {
        return process(inputBuffer, true);
    }

    /**
     * Processes an input buffer
     * @param inputBuffer The buffer to process
     * @param render True if the rendering should be done
     * @return The status of the processing
     */
    public int process(Buffer inputBuffer, boolean render) {
        this.inputBuffer = inputBuffer;
        /*if (this.inputBuffer.getFormat() == null) {
            this.inputBuffer.setFormat(inputFormats[0]);
        } */

        try {
            int status = process(0, render);
            return status;
        } catch (Throwable t) {
            t.printStackTrace();
            return PlugIn.BUFFER_PROCESSED_FAILED;
        }
    }

    private int render() {
        int status = PlugIn.BUFFER_PROCESSED_OK;
        do {
            if (!outputBuffers[codecs.length - 1].getFormat().equals(
                    outputFormats[codecs.length - 1])) {
                Format format = renderer.setInputFormat(
                        outputBuffers[codecs.length - 1].getFormat());
                outputFormats[codecs.length - 1] = format;
            }
            status = renderer.process(outputBuffers[codecs.length - 1]);
            if (status == PlugIn.BUFFER_PROCESSED_FAILED) {
                return status;
            }
        } while (status == PlugIn.INPUT_BUFFER_NOT_CONSUMED);
        return status;
    }

    private int multiplex() {
        int status = PlugIn.BUFFER_PROCESSED_OK;
        do {
            status = multiplexer.process(outputBuffers[codecs.length - 1],
                    track);
            if (status == PlugIn.BUFFER_PROCESSED_FAILED) {
                return status;
            }
        } while (status == PlugIn.INPUT_BUFFER_NOT_CONSUMED);
        return status;
    }

    // Processes with a specific codec
    private int process(int codec, boolean render) {
        int status = PlugIn.BUFFER_PROCESSED_OK;
        do {
            Buffer localInputBuffer = this.inputBuffer;
            if (codec > 0) {
                localInputBuffer = outputBuffers[codec - 1];
            }

            if (!localInputBuffer.getFormat().equals(inputFormats[codec])) {
                Format format = codecs[codec].setInputFormat(
                        localInputBuffer.getFormat());
                inputFormats[codec] = format;
            }

            long lastSequence = outputBuffers[codec].getSequenceNumber();
            outputBuffers[codec].setTimeStamp(0);
            outputBuffers[codec].setOffset(0);
            if (outputBuffers[codec].getData() != null) {
                outputBuffers[codec].setLength(Array.getLength(
                        outputBuffers[codec].getData()));
            } else {
                outputBuffers[codec].setLength(0);
            }
            outputBuffers[codec].setFlags(0);
            status = codecs[codec].process(localInputBuffer, outputBuffers[codec]);
            if (status == PlugIn.BUFFER_PROCESSED_FAILED) {
                return status;
            }

            if (status != PlugIn.OUTPUT_BUFFER_NOT_FILLED) {
                if (lastSequence == outputBuffers[codec].getSequenceNumber()) {
                    lastSequence += 1;
                    if (lastSequence < 0) {
                        lastSequence = 0;
                    }
                    outputBuffers[codec].setSequenceNumber(lastSequence);
                }
                if (outputBuffers[codec].getTimeStamp() == 0) {
                    outputBuffers[codec].setTimeStamp(
                            localInputBuffer.getTimeStamp());
                }
                if (!outputBuffers[codec].isDiscard()
                        && !outputBuffers[codec].isEOM()) {
                    if ((codec + 1) < codecs.length) {
                        if (process(codec + 1, render)
                                == PlugIn.BUFFER_PROCESSED_FAILED) {
                            return PlugIn.BUFFER_PROCESSED_FAILED;
                        }
                    } else if (render && (renderer != null)
                            && ((status == PlugIn.BUFFER_PROCESSED_OK)
                                || (status == PlugIn.INPUT_BUFFER_NOT_CONSUMED))) {
                        if (render() == PlugIn.BUFFER_PROCESSED_FAILED) {
                            return PlugIn.BUFFER_PROCESSED_FAILED;
                        }
                    } else if (render && (multiplexer != null)
                            && ((status == PlugIn.BUFFER_PROCESSED_OK)
                                || (status == PlugIn.INPUT_BUFFER_NOT_CONSUMED))) {
                        return multiplex();
                    } else if (render && (thread != null)
                            && ((status == PlugIn.BUFFER_PROCESSED_OK)
                                || (status == PlugIn.INPUT_BUFFER_NOT_CONSUMED))) {
                        thread.finishedProcessing();
                    }
                }

            }

        } while (status == PlugIn.INPUT_BUFFER_NOT_CONSUMED);
        return status;
    }

    private class Codecs {

        private LinkedList<Codec> codecList = new LinkedList<Codec>();

        private LinkedList<Format> inputFormatList = new LinkedList<Format>();

        private LinkedList<Format> outputFormatList = new LinkedList<Format>();
    }

    private Codecs search(Format input, Format output,
            HashMap<String, Boolean> searched, boolean forward) {
        System.err.println(
                "Finding codec from " + input + " to " + output + " forward = " + forward);
        if (input.matches(output)) {
            Codecs searchCodecs = new Codecs();
            searchCodecs.codecList.addFirst(new CopyCodec());
            searchCodecs.inputFormatList.addFirst(input);
            searchCodecs.outputFormatList.addFirst(input);
            return searchCodecs;
        }
        Vector< ? > codecsFromHere = PlugInManager.getPlugInList(
                input, output, PlugInManager.CODEC);
        if (!codecsFromHere.isEmpty()) {
            System.err.println("Final codec found...");
            Codecs searchCodecs = new Codecs();
            for (int i = 0; i < codecsFromHere.size(); i++) {
                String codecClassName = (String) codecsFromHere.get(i);
                System.err.println("Trying to initialize " + codecClassName);
                try {
                    Codec codec = Misc.loadCodec(codecClassName);
                    int matched = -1;
                    Format in = null;
                    Format out = null;
                    if (forward) {
                        in = codec.setInputFormat(input);
                        Format[] outs = codec.getSupportedOutputFormats(in);
                        for (int j = 0; (j < outs.length) && (matched == -1); j++) {
                            if (output.matches(outs[j])) {
                                out = codec.setOutputFormat(output.intersects(
                                        outs[j]));
                                matched = j;
                            }
                        }
                    } else {
                        out = codec.setOutputFormat(output);
                        Format[] ins = codec.getSupportedInputFormats();
                        for (int j = 0; (j < ins.length) && (matched == -1); j++) {
                            if (input.matches(ins[j])) {
                                Format inF = codec.setInputFormat(input);
                                if (inF != null) {
                                    Format[] outs = codec.getSupportedOutputFormats(inF);
                                    boolean ok = false;
                                    for (int k = 0; (k < outs.length) && !ok; k++) {
                                        if (out.matches(outs[k])) {
                                            ok = true;
                                        }
                                    }
                                    if (ok) {
                                        codec.setInputFormat(ins[j]);
                                        in = ins[j];
                                        matched = j;
                                    }
                                }
                            }
                        }
                    }
                    if (matched != -1) {
                        codec.open();
                        searchCodecs.codecList.addFirst(codec);
                        searchCodecs.inputFormatList.addFirst(in);
                        searchCodecs.outputFormatList.addFirst(out);
                        System.err.println(
                                "Final codec initialized!  In = " + in + " Out = " + out);
                        return searchCodecs;
                    }
                    System.err.println(
                        "Could not find matching output format to " + output);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (forward) {
            codecsFromHere = PlugInManager.getPlugInList(input, null, PlugInManager.CODEC);
        } else {
            codecsFromHere = PlugInManager.getPlugInList(null, output, PlugInManager.CODEC);
        }
        System.err.println("Trying codecs " + codecsFromHere);
        for (int i = 0; i < codecsFromHere.size(); i++) {
            String codecClassName = (String) codecsFromHere.get(i);
            if (!searched.containsKey(codecClassName)) {
                if (forward) {
                    System.err.println("Trying codec " + codecClassName + " with input " + input);
                } else {
                    System.err.println("Trying codec " + codecClassName + " with output " + output);
                }
                searched.put(codecClassName, true);
                try {
                    Codec codec = Misc.loadCodec(codecClassName);
                    Format[] formats = null;
                    if (forward) {
                        codec.setInputFormat(input);
                        formats = codec.getSupportedOutputFormats(input);
                    } else {
                        codec.setOutputFormat(output);
                        Vector<Format> fmts = new Vector<Format>();
                        formats = codec.getSupportedInputFormats();
                        for (int j = 0; j < formats.length; j++) {
                            Format[] outs = codec.getSupportedOutputFormats(formats[j]);
                            System.err.println("Trying input format "
                                    + formats[j] + " -> " + Arrays.toString(outs));
                            boolean ok = false;
                            for (int k = 0; (k < outs.length) && !ok; k++) {
                                if (output.matches(outs[k])) {
                                    fmts.add(formats[j]);
                                    ok = true;
                                }
                            }
                        }
                        formats = fmts.toArray(new Format[0]);
                        System.err.println("    formats = " + fmts);
                    }
                    for (int j = 0; j < formats.length; j++) {
                        Format fmt = null;
                        if (forward) {
                            fmt = codec.setOutputFormat(formats[j]);
                        } else {
                            fmt = codec.setInputFormat(formats[j]);
                        }
                        System.err.println("Trying next level with " + fmt);
                        Codecs searchCodecs = null;
                            if (forward) {
                                searchCodecs = search(fmt, output, searched, forward);
                            } else {
                                searchCodecs = search(input, fmt, searched, forward);
                            }
                        if (searchCodecs != null) {
                            codec.open();
                            if (forward) {
                                searchCodecs.codecList.addFirst(codec);
                                searchCodecs.inputFormatList.addFirst(input);
                                searchCodecs.outputFormatList.addFirst(fmt);
                            } else {
                                searchCodecs.codecList.addFirst(codec);
                                searchCodecs.inputFormatList.addFirst(fmt);
                                searchCodecs.outputFormatList.addFirst(output);
                            }
                            return searchCodecs;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        System.err.println("Backtracking from " + input);
        return null;
    }

    /**
     * Closes the codecs
     *
     */
    public void close() {
        for (int i = 0; i < codecs.length; i++) {
            codecs[i].close();
            codecs[i] = null;
            inputFormats[i] = null;
            outputFormats[i] = null;
            outputBuffers[i].setData(null);
            outputBuffers[i] = null;
        }
        inputFormats = null;
        outputFormats = null;
        outputBuffers = null;
        codecs = null;
        inputBuffer.setData(null);
        inputBuffer = null;
        System.gc();
    }

    /**
     * Starts the processing of a track of a datasource
     * @param ds The datasource
     * @param track The track to process
     */
    public void start(DataSource ds, int track) {
        if (thread == null) {
            thread = new ProcessingThread(ds, track, this);
        }
        thread.start();
    }

    /**
     * Stops the processing of a datasource
     *
     */
    public void stop() {
        if (thread != null) {
            thread.close();
            thread = null;
        }
    }

    /**
     * Gets a datasouce output of the processor
     * @param ds The datasource input
     * @param track The track of the input to process
     * @return The output data source
     */
    public PushBufferDataSource getDataOutput(DataSource ds, int track) {
        if (thread == null) {
            thread = new ProcessingThread(ds, track, this);
        }
        return new ProcessorSource(thread);
    }

    /**
     * Gets a control
     * @param className The class of the control
     * @return The control or null if none
     */
    public Object getControl(String className) {
        for (int i = 0; i < codecs.length; i++) {
            Object control = codecs[i].getControl(className);
            if (control != null) {
                return control;
            }
        }
        return null;
    }

    /**
     * Gets the most recent buffer in a particular format
     * @param format The format to get the buffer in
     * @return The buffer or null if none found
     */
    public Buffer getBuffer(Format format) {
        for (int i = 0; i < outputFormats.length; i++) {
            if (format.matches(outputFormats[i])) {
                return outputBuffers[i];
            }
        }
        return null;
    }
}
