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

package net.crew_vre.media.protocol.screen;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.StringTokenizer;
import java.util.Timer;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Format;
import javax.media.MediaLocator;
import javax.media.control.FrameRateControl;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;

/**
 * A stream for sending the screen via RTP
 *
 * @author Andrew G D Rowley
 * @version 1-1-alpha3
 */
public class LiveStream implements PushBufferStream, Runnable,
        FrameRateControl {

    // The conversion factor between ms and a buffer timestamp
    private static final int MS_TO_TIMESTAMP = 1000000;

    // The number of ms in a second
    private static final int SECS_TO_MS = 1000;

    // The locator separator char
    private static final String LOCATOR_SEPARATOR = "/";

    // The mask of the blue pixel value
    private static final int BLUE_MASK = 0xFF;

    // The mask of the green pixel value
    private static final int GREEN_MASK = 0xFF00;

    // The mask of the red pixel value
    private static final int RED_MASK = 0xFF0000;

    // The format bits per pixel
    private static final int BITS_PER_PIXEL = 32;

    // The number of bytes per mouse pixel
    private static final int BYTES_PER_PIXEL = 3;

    // The default frame rate
    private static final float DEFAULT_FRAME_RATE = 5f;

    // The line change value
    private static final int LINECHANGE = 5;

    // The width of the mouse pointer
    private static final int MOUSE_WIDTH = 17;

    // The height of the mouse pointer
    private static final int MOUSE_HEIGHT = 29;

    // The mouse pointer data
    private static final int[] MOUSE = new int[MOUSE_WIDTH * MOUSE_HEIGHT
                                               * BYTES_PER_PIXEL];

    // The mouse pointer x points
    private static final int[] MOUSE_X_POINTS =
        {0, 0, 4, 10, 12, 14, 8, 16, 0};

    // The mouse pointer y points
    private static final int[] MOUSE_Y_POINTS =
        {0, 22, 18, 28, 28, 28, 16, 16, 0};

    // The number of milliseconds after which not to draw a non-moving mouse
    private static final long MOUSE_SAME_TIME = 5000;

    // The amount by which the mouse can move before the mouse is redrawn
    private static final int MOUSE_MOVE_ALLOWED = 5;

    // Draw the mouse
    static {
        BufferedImage mouse = new BufferedImage(MOUSE_WIDTH, MOUSE_HEIGHT,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = mouse.createGraphics();
        g2d.setPaint(Color.GREEN);
        g2d.fillRect(0, 0, MOUSE_WIDTH, MOUSE_HEIGHT);

        // These points represent a mouse pointer
        Polygon p = new Polygon(MOUSE_X_POINTS, MOUSE_Y_POINTS,
                MOUSE_X_POINTS.length);
        g2d.setPaint(Color.black);
        g2d.draw(p);
        g2d.setPaint(Color.white);
        g2d.fill(p);
        g2d.dispose();
        mouse.getRGB(0, 0, MOUSE_WIDTH, MOUSE_HEIGHT, MOUSE, 0, MOUSE_WIDTH);
    }

    // The content descriptor
    private ContentDescriptor cd = new ContentDescriptor(
            ContentDescriptor.RAW);

    // The maximum length of a data packet with this stream
    private int maxDataLength;

    // The data to be sent with the stream
    private int[] data;

    // The size of the video
    private Dimension size;

    // The videoformat to send data in
    private VideoFormat format;

    // True if the stream has been started
    private boolean started;

    // The rate of the frames to send
    private float frameRate = DEFAULT_FRAME_RATE;

    // The handler of transfers
    private BufferTransferHandler transferHandler;

    // The controls of the stream
    private Control[] controls = new Control[]{this};

    // The dimensions of the screen to capture
    private int x;
    private int y;
    private int width;
    private int height;

    // The x-coordinate of the mouse pointer
    private int mousex = 0;

    // The y-coordinate of the mouse pointer
    private int mousey = 0;

    // The time when the mouse pointer was last updated
    private long lastMouseMoveTime = 0;

    // The robot for capturing the screen
    private Robot robot = null;

    // The image captured
    private BufferedImage image = null;

    // The number of frames sent
    private int seqNo = 0;

    // The last line captured
    private int[] lastLine = null;

    // A Timer to time events with
    private Timer timer = null;

    /**
     * Creates a new Stream for sending screens
     *
     * @param locator
     *            The locator to parse
     */
    public LiveStream(MediaLocator locator) {

        // Work out what is to be sent
        try {
            parseLocator(locator);
        } catch (Exception e) {
            System.err.println(e);
        }

        // Get the size to send
        size = new Dimension(width, height);

        // Setup the format
        maxDataLength = size.width * size.height;
        format = new RGBFormat(size, maxDataLength, Format.intArray, frameRate,
                BITS_PER_PIXEL, RED_MASK, GREEN_MASK, BLUE_MASK, 1, size.width,
                VideoFormat.FALSE,
                Format.NOT_SPECIFIED);

        // generate the data
        data = new int[maxDataLength];
        image = new BufferedImage(size.width, size.height,
                BufferedImage.TYPE_INT_RGB);


        // Create the screen capturer
        try {
            robot = new Robot();
        } catch (AWTException awe) {
            throw new RuntimeException(awe.getMessage());
        }

        lastLine = new int[width];
    }

    // Works out what is to be send
    protected void parseLocator(MediaLocator locator) {
        String rem = locator.getRemainder();

        // Strip off starting slashes
        while (rem.startsWith(LOCATOR_SEPARATOR) && (rem.length() > 1)) {
            rem = rem.substring(1);
        }

        // Parse the string
        StringTokenizer st = new StringTokenizer(rem, LOCATOR_SEPARATOR);
        if (st.hasMoreTokens()) {
            String position = st.nextToken();

            // If the request is for fullscreen ...
            if (position.matches("fullscreen:\\d+")) {

                // Find which screen is required
                GraphicsConfiguration gc = null;
                StringTokenizer screens = new StringTokenizer(position, ":");
                screens.nextToken();
                int screen = Integer.parseInt(screens.nextToken());

                // If the default screen is selected, send this
                if (screen == 0) {
                    GraphicsEnvironment ge = GraphicsEnvironment
                            .getLocalGraphicsEnvironment();
                    GraphicsDevice gs = ge.getDefaultScreenDevice();
                    gc = gs.getDefaultConfiguration();
                } else {

                    // Otherwise send the selected screen
                    screen = screen - 1;
                    GraphicsEnvironment ge = GraphicsEnvironment
                            .getLocalGraphicsEnvironment();
                    GraphicsDevice[] gs = ge.getScreenDevices();
                    gc = gs[screen].getDefaultConfiguration();
                }

                // Get the screen dimensions
                Rectangle bounds = gc.getBounds();
                x = bounds.x;
                y = bounds.y;
                width = bounds.width;
                height = bounds.height;
            } else {

                // Otherwise, get the x,y,width,height from the url
                StringTokenizer nums = new StringTokenizer(position, ",");
                String stX = nums.nextToken();
                String stY = nums.nextToken();
                String stW = nums.nextToken();
                String stH = nums.nextToken();
                x = Integer.parseInt(stX);
                y = Integer.parseInt(stY);
                width = Integer.parseInt(stW);
                height = Integer.parseInt(stH);
            }
        }

        // Get the framerate if present
        if (st.hasMoreTokens()) {
            // Parse the frame rate
            String stFPS = st.nextToken();
            frameRate = (Double.valueOf(stFPS)).floatValue();
        }
    }

    /**
     * @see javax.media.protocol.SourceStream#getContentDescriptor()
     */
    public ContentDescriptor getContentDescriptor() {
        return cd;
    }

    /**
     * @see javax.media.protocol.SourceStream#getContentLength()
     */
    public long getContentLength() {
        return LENGTH_UNKNOWN;
    }

    /**
     * @see javax.media.protocol.SourceStream#endOfStream()
     */
    public boolean endOfStream() {
        return false;
    }

    /**
     * @see javax.media.protocol.PushBufferStream#getFormat()
     */
    public Format getFormat() {
        return format;
    }

    private void fillBuffer() {

        // Capture the screen, one line in every 5 interlaced
        int startno = seqNo % LINECHANGE;
        int lines = 1;

        // Go through the lines of the screen
        for (int i = startno; i < (height - y); i += LINECHANGE) {

            // If there are not enough lines, capture what you can
            while (i + lines > height) {
                lines--;
            }

            // Copy the last line for post comparison
            System.arraycopy(data, i * width, lastLine, 0, width);

            // Create the screen capture of the line (avoids jumpyness)
            BufferedImage bi = robot.createScreenCapture(new Rectangle(x, y
                    + i, width, lines));

            // Get the RGB data from the capture
            bi.getRGB(0, 0, size.width, lines, data, i * width, size.width);

            // Store the line in the whole image
            image.setRGB(0, i, size.width, lines, data, i * width, size.width);
        }

        // Get the mouse pointer location
        try {
            Point pnt = MouseInfo.getPointerInfo().getLocation();
            if ((Math.abs(mousex - pnt.x) > MOUSE_MOVE_ALLOWED)
                    || (Math.abs(mousey - pnt.y) > MOUSE_MOVE_ALLOWED)
                    || ((System.currentTimeMillis() - lastMouseMoveTime)
                            < MOUSE_SAME_TIME)) {
                if ((mousex != pnt.x) || (mousey != pnt.y)) {
                    lastMouseMoveTime = System.currentTimeMillis();
                }
                mousex = pnt.x;
                mousey = pnt.y;

                // If the mouse is in the capture area, add the mouse pointer
                if ((mousex > x) && ((mousex + MOUSE_WIDTH) < (x + width))
                        && (mousey > y)
                        && ((mousey + MOUSE_HEIGHT) < (y + height))) {
                    for (int j = 0; j < MOUSE_HEIGHT; j++) {
                        for (int i = 0; i < MOUSE_WIDTH; i++) {
                            int ypos = (mousey - y + j);
                            int xpos = (mousex - x + i);
                            int pos = (ypos * width) + xpos;
                            int mousepos = ((j * MOUSE_WIDTH) + i);
                            if (MOUSE[mousepos] != Color.GREEN.getRGB()) {
                                data[pos] = MOUSE[mousepos];
                            }
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            //no mouse pointer available -- ignore
        }
    }

    /**
     * Reads the screen data
     *
     * @see javax.media.protocol.PushBufferStream#read(javax.media.Buffer)
     */
    public void read(Buffer buffer) {

            Thread t = new Thread() {
                public void run() {
                    fillBuffer();
                }
            };
            t.start();
            //fillBuffer();

            // Setup the buffer
            buffer.setData(data);
            buffer.setFormat(format);

            // Convert the timestamp to be based on the framerate*bg.getHeight()
            buffer.setTimeStamp((long) (seqNo * (SECS_TO_MS / frameRate)
                    * MS_TO_TIMESTAMP));

            // Setup the buffer some more
            buffer.setSequenceNumber(seqNo);
            buffer.setLength(maxDataLength);
            buffer.setFlags(Buffer.FLAG_KEY_FRAME);
            buffer.setHeader(null);
            seqNo++;
    }

    /**
     * @see javax.media.protocol.PushBufferStream#setTransferHandler
     *      (javax.media.protocol.BufferTransferHandler)
     */
    public void setTransferHandler(BufferTransferHandler transferHandler) {
        synchronized (this) {
            this.transferHandler = transferHandler;
            notifyAll();
        }
    }

    /**
     * Starts the capture of data
     *
     * @param started
     *            True if the thread is to be started
     */
    void start(boolean started) {
        this.started = started;
        if (started) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new StreamTimerTask(this), 0,
                    (long) (SECS_TO_MS / frameRate));
        } else {
            timer.cancel();
        }
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        final LiveStream me = this;
        if (started && (transferHandler != null)) {
            Thread t = new Thread() {
                public void run() {
                    transferHandler.transferData(me);
                }
            };
            t.start();
        }
    }

    /**
     * @see javax.media.Controls#getControls()
     */
    public Object[] getControls() {
        return controls;
    }

    /**
     * @see javax.media.Controls#getControl(java.lang.String)
     */
    public Object getControl(String controlType) {
        try {
            Class< ? > cls = Class.forName(controlType);
            Object[] cs = getControls();
            for (int i = 0; i < cs.length; i++) {
                if (cls.isInstance(cs[i])) {
                    return cs[i];
                }
            }
            return null;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @see javax.media.control.FrameRateControl#setFrameRate(float)
     */
    public float setFrameRate(float frameRate) {
        this.frameRate = frameRate;
        format = new RGBFormat(size, maxDataLength, Format.intArray, frameRate,
                BITS_PER_PIXEL, RED_MASK, GREEN_MASK, BLUE_MASK, 1, size.width,
                VideoFormat.FALSE,
                Format.NOT_SPECIFIED);
        return frameRate;
    }

    /**
     *
     * @see javax.media.control.FrameRateControl#getFrameRate()
     */
    public float getFrameRate() {
        return frameRate;
    }

    /**
     *
     * @see javax.media.control.FrameRateControl#getMaxSupportedFrameRate()
     */
    public float getMaxSupportedFrameRate() {
        return -1;
    }

    /**
     *
     * @see javax.media.control.FrameRateControl#getPreferredFrameRate()
     */
    public float getPreferredFrameRate() {
        return DEFAULT_FRAME_RATE;
    }

    /**
     *
     * @see javax.media.Control#getControlComponent()
     */
    public Component getControlComponent() {
        return null;
    }
}
