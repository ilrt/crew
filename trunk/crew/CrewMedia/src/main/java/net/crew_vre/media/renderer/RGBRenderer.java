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

package net.crew_vre.media.renderer;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.Effect;
import javax.media.Format;
import javax.media.control.BitRateControl;
import javax.media.control.FrameRateControl;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.format.YUVFormat;
import javax.media.protocol.DataSource;
import javax.media.renderer.VideoRenderer;

import net.crew_vre.media.processor.SimpleProcessor;

/**
 * A Renderer of RGB Data
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class RGBRenderer implements VideoRenderer, BitRateControl,
        FrameRateControl {

    private static final int UPDATE_TIME = 1000;

    private Format[] inputFormats = null;

    private SimpleProcessor processor = null;

    private SimpleProcessor[] effectProcessors = new SimpleProcessor[0];

    private VideoRenderer renderer = null;

    private VideoRenderer preview = null;

    private Effect[] renderEffects = new Effect[0];

    private long lastUpdateTime = 0;

    private RenderingThread thread = null;

    private int bitsRead = 0;

    private int framesRead = 0;

    private long lastBitRate = 0;

    private float lastFrameRate = 0.0f;

    private long lastBitRateTime = 0;

    private long lastFrameRateTime = 0;

    /**
     * Creates a new Renderer of RGB Data
     *
     * @param renderEffects Effects to apply before rendering
     *
     */
    public RGBRenderer(Effect[] renderEffects) {
        inputFormats = new Format[]{new VideoFormat(null)};
        this.renderEffects = renderEffects;
        effectProcessors = new SimpleProcessor[renderEffects.length];
    }

    /**
     * Sets the data source to use
     * @param dataSource The data source
     * @param track The track to render
     */
    public void setDataSource(DataSource dataSource, int track) {
        thread = new RenderingThread(dataSource, track, this);
    }

    /**
     *
     * @see javax.media.Codec#setInputFormat(javax.media.Format)
     */
    public Format setInputFormat(Format inputFormat) {

        // Find a processor from the input format to the effects
        for (int i = 0; i < renderEffects.length; i++) {
            System.err.println("Finding input for effect " + renderEffects[i]);
            Format format = renderEffects[i].setInputFormat(inputFormat);
            if (format == null) {
                Format[] inputs = renderEffects[i].getSupportedInputFormats();
                SimpleProcessor proc = null;
                for (int j = 0; (j < inputs.length) && (proc == null); j++) {
                    try {
                        proc = new SimpleProcessor(inputFormat, inputs[j]);
                    } catch (Exception e) {
                        proc = null;
                    }
                }
                if (proc != null) {
                    effectProcessors[i] = proc;
                    inputFormat = proc.getOutputFormat();
                }
            }
        }
        Vector<String> renderers = new Vector<String>();
        renderers.add("net.sf.fmj.media.renderer.video.Java2dRenderer");
        System.err.println("Renderers = " + renderers);
        for (int i = 0; (i < renderers.size()) && (renderer == null); i++) {
            String rendererClassName = renderers.get(i);

            try {
                System.err.println("Trying renderer " + rendererClassName);
                Class< ? > rendererClass = Class.forName(rendererClassName);
                VideoRenderer r = (VideoRenderer) rendererClass.newInstance();
                Format input = r.setInputFormat(inputFormat);
                if (input == null) {
                    processor = new SimpleProcessor(inputFormat, r);
                } else {
                    r.open();
                }
                System.err.println("renderer " + rendererClassName + " " + input + " " + processor);
                if ((input != null) || (processor != null)) {
                    renderer = r;
                    renderer.start();
                    preview = (VideoRenderer) rendererClass.newInstance();
                    if (processor != null) {
                        preview.setInputFormat(processor.getOutputFormat());
                    } else if (input != null) {
                        preview.setInputFormat(input);
                    }
                    preview.open();
                    preview.start();
                }
                System.err.println("Renderer class = " + rendererClass);
            } catch (Throwable e) {
                e.printStackTrace();
                renderer = null;
            }
        }
        if (renderer != null) {
            return inputFormat;
        }
        return null;
    }

    /**
     *
     * @see javax.media.PlugIn#getName()
     */
    public String getName() {
        return "RGBRenderer";
    }

    /**
     *
     * @see javax.media.renderer.VideoRenderer#getComponent()
     */
    public Component getComponent() {
        return renderer.getComponent();
    }

    /**
     *
     * @see javax.media.renderer.VideoRenderer#setComponent(java.awt.Component)
     */
    public boolean setComponent(Component comp) {
        return false;
    }

    /**
     *
     * @see javax.media.renderer.VideoRenderer#setBounds(java.awt.Rectangle)
     */
    public void setBounds(Rectangle rect) {
        renderer.setBounds(rect);
    }

    /**
     *
     * @see javax.media.renderer.VideoRenderer#getBounds()
     */
    public Rectangle getBounds() {
        return renderer.getBounds();
    }

    /**
     *
     * @see javax.media.Renderer#start()
     */
    public void start() {
        if (thread != null) {
            thread.start();
        }
    }

    /**
     *
     * @see javax.media.Renderer#stop()
     */
    public void stop() {
        if (thread != null) {
            thread.close();
        }
    }

    private boolean updatePreview() {
        if ((System.currentTimeMillis() - lastUpdateTime) > UPDATE_TIME) {
            lastUpdateTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    /**
     *
     * @see javax.media.Renderer#process(javax.media.Buffer)
     */
    public int process(Buffer input) {
        int retval = 0;
        boolean visible = getComponent().isVisible();
        boolean updatePreview = updatePreview();
        retval = BUFFER_PROCESSED_OK;

        int bits = input.getLength();
        if (input.getFormat().getDataType().equals(Format.intArray)) {
            bits *= 4;
        } else if (input.getFormat().getDataType().equals(Format.shortArray)) {
            bits *= 2;
        }
        bitsRead += bits;

        if (((input.getFormat() instanceof RGBFormat) || (input.getFormat() instanceof YUVFormat))
                && !visible && !updatePreview) {
            framesRead += 1;
            return BUFFER_PROCESSED_OK;
        }

        // Run through the effects
        for (int i = 0; (i < renderEffects.length)
                && ((retval == BUFFER_PROCESSED_OK)
                    || (retval == INPUT_BUFFER_NOT_CONSUMED)); i++) {
            if (effectProcessors[i] != null) {
                retval = effectProcessors[i].process(input, false);
                input = effectProcessors[i].getOutputBuffer();
            }
            if (visible && ((retval == BUFFER_PROCESSED_OK)
                    || (retval == INPUT_BUFFER_NOT_CONSUMED))) {
                Buffer b = new Buffer();
                b.setOffset(0);
                b.setLength(0);
                b.setFlags(0);
                b.setSequenceNumber(0);
                b.setTimeStamp(0);

                retval = renderEffects[i].process(input, b);
                input = b;
            }
        }


        // Run the final processor
        if ((processor != null) && ((retval == BUFFER_PROCESSED_OK)
                || (retval == INPUT_BUFFER_NOT_CONSUMED))) {
            retval = processor.process(input, false);
        }



        if ((retval == BUFFER_PROCESSED_OK)
                || (retval == INPUT_BUFFER_NOT_CONSUMED)) {
            framesRead += 1;
            if (visible) {
                if (processor != null) {

                    retval = renderer.process(processor.getOutputBuffer());
                } else {
                    retval = renderer.process(input);
                }
            }
            if (updatePreview) {
                if (processor != null) {
                    retval = preview.process(processor.getOutputBuffer());
                } else {
                    retval = preview.process(input);
                }
            }
        }
        return retval;
    }

    /**
     * Gets the video renderer for the preview
     * @return the preview video renderer
     */
    public VideoRenderer getPreviewRenderer() {
        return preview;
    }

    /**
     *
     * @see com.sun.media.BasicPlugIn#getControl(java.lang.String)
     */
    public Object getControl(String className) {
        if (className.equals(BitRateControl.class.getCanonicalName())) {
            return this;
        }
        if (className.equals(FrameRateControl.class.getCanonicalName())) {
            return this;
        }

        for (int i = 0; i < effectProcessors.length; i++) {
            if (effectProcessors[i] != null) {
                Object control = effectProcessors[i].getControl(className);
                if (control != null) {
                    return control;
                }
            }
        }
        if (processor != null) {
            Object control = processor.getControl(className);
            if (control != null) {
                return control;
            }
        }
        return renderer.getControl(className);
    }

    /**
     *
     * @see javax.media.control.BitRateControl#getBitRate()
     */
    public int getBitRate() {
        long now = System.currentTimeMillis();
        long rate = (long)(bitsRead * 8.0 /
                (now - lastBitRateTime) * 1000.0);
        long avg = (lastBitRate + rate) / 2;
        lastBitRate = rate;
        lastBitRateTime = now;
        bitsRead = 0;
        return (int) avg;
    }

    /**
     *
     * @see javax.media.control.BitRateControl#getMaxSupportedBitRate()
     */
    public int getMaxSupportedBitRate() {
        return 0;
    }

    /**
     *
     * @see javax.media.control.BitRateControl#getMinSupportedBitRate()
     */
    public int getMinSupportedBitRate() {
        return 0;
    }

    /**
     *
     * @see javax.media.control.BitRateControl#setBitRate(int)
     */
    public int setBitRate(int arg0) {
        return 0;
    }

    /**
     *
     * @see javax.media.Control#getControlComponent()
     */
    public Component getControlComponent() {
        return null;
    }

    /**
     *
     * @see javax.media.control.FrameRateControl#getFrameRate()
     */
    public float getFrameRate() {
        long now = System.currentTimeMillis();
        float rate = ((float) framesRead / (now - lastFrameRateTime) * 1000.0f);
        float avg = ((int)(((lastFrameRate + rate)/2) * 10)) / 10.f;
        lastFrameRate = rate;
        lastFrameRateTime = now;
        framesRead = 0;
        return avg;
    }

    /**
     *
     * @see javax.media.control.FrameRateControl#getMaxSupportedFrameRate()
     */
    public float getMaxSupportedFrameRate() {
        return 0;
    }

    /**
     *
     * @see javax.media.control.FrameRateControl#getPreferredFrameRate()
     */
    public float getPreferredFrameRate() {
        return 0;
    }

    /**
     *
     * @see javax.media.control.FrameRateControl#setFrameRate(float)
     */
    public float setFrameRate(float arg0) {
        return 0;
    }

    /**
     *
     * @see javax.media.Renderer#getSupportedInputFormats()
     */
    public Format[] getSupportedInputFormats() {
        return inputFormats;
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
     * @see javax.media.Controls#getControls()
     */
    public Object[] getControls() {
        return new Object[]{this};
    }
}
