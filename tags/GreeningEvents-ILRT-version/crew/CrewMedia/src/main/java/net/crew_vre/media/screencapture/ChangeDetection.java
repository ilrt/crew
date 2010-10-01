package net.crew_vre.media.screencapture;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.ComponentColorModel;
import java.awt.image.WritableRaster;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.lang.reflect.Field;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.Renderer;
import javax.media.format.RGBFormat;

import sun.misc.Unsafe;

/**
 * @author anja
 *
 */
public class ChangeDetection implements Renderer {

    // The CCITT RGB to YUV Y Blue multiplier
    private static final double CCITT_Y_BLUE_MULTIPLIER = 0.114;

    // The CCITT RGB to YUV Y Green multiplier
    private static final double CCITT_Y_GREEN_MULTIPLIER = 0.587;

    // The CCITT RGB to YUV Y Red multiplier
    private static final double CCITT_Y_RED_MULTIPLIER = 0.299;

    // The CCITT RGB to YUV Y Constant
    private static final int CCITT_Y_CONSTANT = 16;

    // The mask to convert a byte to an int
    private static final int BYTE_TO_INT_MASK = 0xFF;

    // The name of the effect
    private static final String NAME = "Change Detection Effect";

    // The percentage change between two frames that indicates a scene change
    private static final int SCENE_CHANGE_PERCENT_THRESHOLD = 10;

    // The input/output format
    private RGBFormat format = null;

    // The allowed input formats
    private Format[] inputFormats;

    // The last buffer stored
    private Buffer lastBuffer = null;

    // The pixel stride of the input
    private int pixelStride = 0;

    // The line stride of the input
    private int lineStride = 0;

    // The red mask of the input
    private int redMask = 0;

    // The blue mask of the input
    private int blueMask = 0;

    // The green mask of the input
    private int greenMask = 0;

    // The lock status
    private boolean locked = false;

    private int[] crvec_ = null;

    private long refbuf_ = 0;

    private long devbuf_ = 0;

    private int scan_ = 0;

    private int blkw_ = 0;

    private int blkh_ = 0;

    private int nblk_ = 0;

    private int width_ = 0;

    private int threshold_ = 400;

    private Unsafe unsafe = null;

    private long inObjectOffset = 0;

    private long byteArrayOffset = 0;

    private Object inObject = null;

    private long lastUpdateTime = -1;

    // The listeners to screen change events
    private Vector < CaptureChangeListener > screenListeners =
        new Vector<CaptureChangeListener>();

    /**
     * Creates a new ChangeDetectionEffect
     *
     */
    public ChangeDetection() {
        inputFormats = new Format[] {
            new RGBFormat(
                    null,
                    Format.NOT_SPECIFIED, Format.byteArray,
                    Format.NOT_SPECIFIED, 24, Format.NOT_SPECIFIED,
                    Format.NOT_SPECIFIED, Format.NOT_SPECIFIED,
                    3, Format.NOT_SPECIFIED, Format.FALSE, Format.NOT_SPECIFIED)
        };
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            inObjectOffset = unsafe.objectFieldOffset(
                ChangeDetection.class.getDeclaredField("inObject"));
            byteArrayOffset = unsafe.arrayBaseOffset(byte[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @see javax.media.Codec#getSupportedInputFormats()
     */
    public Format[] getSupportedInputFormats() {
        return inputFormats;
    }


    private void save(long lum, int pos, int stride) {
        for (int i = 16; --i >= 0; ) {
            unsafe.copyMemory(lum + pos, refbuf_ + pos, 16);
            pos += stride;
        }
    }

    /*
     * Default save routine -- stuff new luma blocks into cache.
     */
    private void saveblks(long lum) {
        int crv = 0;
        int pos = 0;
        int stride = width_;
        stride = (stride << 4) - stride;
        for (int y = 0; y < blkh_; y++) {
            for (int x = 0; x < blkw_; x++) {
                if (crvec_[crv++] == 1) {
                    convertBlock(lum, devbuf_, x, y);
                    save(devbuf_, pos, width_);
                }
                pos += 16;
            }
            pos += stride;
        }
    }

    private void convertBlockLine(long inBuf, long devBuf, int line,
            int blkx, int blky) {
        int x = blkx * 16;
        int y = (blky * 16) + line;
        long posOut = devBuf + (y * width_) + x;
        long posIn = inBuf + (y * lineStride) + (x * pixelStride);

        for (int i = 0; i < 16; i++) {
            int r = unsafe.getByte(posIn + redMask - 1) & BYTE_TO_INT_MASK;
            int g = unsafe.getByte(posIn + greenMask - 1) & BYTE_TO_INT_MASK;
            int b = unsafe.getByte(posIn + blueMask - 1) & BYTE_TO_INT_MASK;

            int yVal = (int) (CCITT_Y_RED_MULTIPLIER * r)
                  + (int) (CCITT_Y_GREEN_MULTIPLIER * g)
                  + (int) (CCITT_Y_BLUE_MULTIPLIER * b)
                  + CCITT_Y_CONSTANT;
            unsafe.putByte(posOut, (byte) (yVal & BYTE_TO_INT_MASK));

            posOut += 1;
            posIn += pixelStride;
        }
    }

    private void convertBlock(long inBuf, long devBuf, int blkx, int blky) {
        for (int i = 0; i < 16; i++) {
            convertBlockLine(inBuf, devBuf, i, blkx, blky);
        }
    }

    /**
     *
     * @see javax.media.Renderer#process(javax.media.Buffer)
     */
    public int process(Buffer bufIn) {
        if (nblk_ <= 0) {
            setInputFormat(bufIn.getFormat());
            Dimension size = format.getSize();
            this.width_ = size.width;
            blkw_ = size.width >> 4;
            blkh_ = size.height >> 4;
            nblk_ = blkw_ * blkh_;
            refbuf_ = unsafe.allocateMemory(size.width * size.height);
            devbuf_ = unsafe.allocateMemory(size.width * size.height);
            unsafe.setMemory(refbuf_, size.width * size.height, (byte) 0);
            unsafe.setMemory(devbuf_, size.width * size.height, (byte) 0);
            crvec_ = new int[nblk_];
            lastBuffer = null;
        }
        lock();

        for (int i = 0; i < nblk_; ++i) {
            crvec_[i] = 0;
        }
        scan_ = (scan_ + 3) & 7;
        inObject = bufIn.getData();

        int _ds = width_;
        int _rs = width_;
        long inbufAddr = unsafe.getLong(this, inObjectOffset)
            + byteArrayOffset;
        long db = devbuf_ + scan_ * _ds;
        long rb = refbuf_ + scan_ * _rs;
        int w = blkw_;
        int crv = 0;
        for (int y = 0; y < blkh_; ++y) {
            long ndb = db;
            long nrb = rb;
            int ncrv = crv;
            for (int x = 0; x < blkw_; x++) {
                convertBlockLine(inbufAddr, devbuf_, scan_, x, y);
                int left = 0;
                int right = 0;
                int top = 0;
                int bottom = 0;
                left += (unsafe.getByte(db + 0) & 0xFF)
                      - (unsafe.getByte(rb + 0) & 0xFF);
                left += (unsafe.getByte(db + 1) & 0xFF)
                      - (unsafe.getByte(rb + 1) & 0xFF);
                left += (unsafe.getByte(db + 2) & 0xFF)
                      - (unsafe.getByte(rb + 2) & 0xFF);
                left += (unsafe.getByte(db + 3) & 0xFF)
                      - (unsafe.getByte(rb + 3) & 0xFF);
                top += (unsafe.getByte(db + 0 + 1*4) & 0xFF)
                     - (unsafe.getByte(rb + 0 + 1*4) & 0xFF);
                top += (unsafe.getByte(db + 1 + 1*4) & 0xFF)
                     - (unsafe.getByte(rb + 1 + 1*4) & 0xFF);
                top += (unsafe.getByte(db + 2 + 1*4) & 0xFF)
                     - (unsafe.getByte(rb + 2 + 1*4) & 0xFF);
                top += (unsafe.getByte(db + 3 + 1*4) & 0xFF)
                     - (unsafe.getByte(rb + 3 + 1*4) & 0xFF);
                top += (unsafe.getByte(db + 0 + 2*4) & 0xFF)
                     - (unsafe.getByte(rb + 0 + 2*4) & 0xFF);
                top += (unsafe.getByte(db + 1 + 2*4) & 0xFF)
                     - (unsafe.getByte(rb + 1 + 2*4) & 0xFF);
                top += (unsafe.getByte(db + 2 + 2*4) & 0xFF)
                     - (unsafe.getByte(rb + 2 + 2*4) & 0xFF);
                top += (unsafe.getByte(db + 3 + 2*4) & 0xFF)
                     - (unsafe.getByte(rb + 3 + 2*4) & 0xFF);
                right += (unsafe.getByte(db + 0 + 3*4) & 0xFF)
                       - (unsafe.getByte(rb + 0 + 3*4) & 0xFF);
                right += (unsafe.getByte(db + 1 + 3*4) & 0xFF)
                       - (unsafe.getByte(rb + 1 + 3*4) & 0xFF);
                right += (unsafe.getByte(db + 2 + 3*4) & 0xFF)
                       - (unsafe.getByte(rb + 2 + 3*4) & 0xFF);
                right += (unsafe.getByte(db + 3 + 3*4) & 0xFF)
                       - (unsafe.getByte(rb + 3 + 3*4) & 0xFF);
                right = Math.abs(right);
                left = Math.abs(left);
                top = Math.abs(top);
                db += _ds << 3;
                rb += _rs << 3;
                left += (unsafe.getByte(db + 0) & 0xFF)
                      - (unsafe.getByte(rb + 0) & 0xFF);
                left += (unsafe.getByte(db + 1) & 0xFF)
                      - (unsafe.getByte(rb + 1) & 0xFF);
                left += (unsafe.getByte(db + 2) & 0xFF)
                      - (unsafe.getByte(rb + 2) & 0xFF);
                left += (unsafe.getByte(db + 3) & 0xFF)
                      - (unsafe.getByte(rb + 3) & 0xFF);
                bottom += (unsafe.getByte(db + 0 + 1*4) & 0xFF)
                        - (unsafe.getByte(rb + 0 + 1*4) & 0xFF);
                bottom += (unsafe.getByte(db + 1 + 1*4) & 0xFF)
                        - (unsafe.getByte(rb + 1 + 1*4) & 0xFF);
                bottom += (unsafe.getByte(db + 2 + 1*4) & 0xFF)
                        - (unsafe.getByte(rb + 2 + 1*4) & 0xFF);
                bottom += (unsafe.getByte(db + 3 + 1*4) & 0xFF)
                        - (unsafe.getByte(rb + 3 + 1*4) & 0xFF);
                bottom += (unsafe.getByte(db + 0 + 2*4) & 0xFF)
                        - (unsafe.getByte(rb + 0 + 2*4) & 0xFF);
                bottom += (unsafe.getByte(db + 1 + 2*4) & 0xFF)
                        - (unsafe.getByte(rb + 1 + 2*4) & 0xFF);
                bottom += (unsafe.getByte(db + 2 + 2*4) & 0xFF)
                        - (unsafe.getByte(rb + 2 + 2*4) & 0xFF);
                bottom += (unsafe.getByte(db + 3 + 2*4) & 0xFF)
                        - (unsafe.getByte(rb + 3 + 2*4) & 0xFF);
                right += (unsafe.getByte(db + 0 + 3*4) & 0xFF)
                       - (unsafe.getByte(rb + 0 + 3*4) & 0xFF);
                right += (unsafe.getByte(db + 1 + 3*4) & 0xFF)
                       - (unsafe.getByte(rb + 1 + 3*4) & 0xFF);
                right += (unsafe.getByte(db + 2 + 3*4) & 0xFF)
                       - (unsafe.getByte(rb + 2 + 3*4) & 0xFF);
                right += (unsafe.getByte(db + 3 + 3*4) & 0xFF)
                       - (unsafe.getByte(rb + 3 + 3*4) & 0xFF);
                right = Math.abs(right);
                left = Math.abs(left);
                bottom = Math.abs(bottom);
                db -= _ds << 3;
                rb -= _rs << 3;

                int center = 0;
                if (left >= threshold_ && x > 0) {
                    crvec_[crv - 1] = 1;
                    center = 1;
                }
                if (right >= threshold_ && x < w - 1) {
                    crvec_[crv + 1] = 1;
                    center = 1;
                }
                if (bottom >= threshold_ && y < blkh_ - 1) {
                    crvec_[crv + w] = 1;
                    center = 1;
                }
                if (top >= threshold_ && y > 0) {
                    crvec_[crv - w] = 1;
                    center = 1;
                }
                if (center > 0) {
                    crvec_[crv + 0] = 1;
                }

                db += 16;
                rb += 16;
                ++crv;
            }
            db = ndb + (_ds << 4);
            rb = nrb + (_rs << 4);
            crv = ncrv + w;
        }
        saveblks(inbufAddr);
        int diffCount = 0;
        for (int i = 0; i < nblk_; i++) {
            diffCount += crvec_[i];
        }
        if (((diffCount * 100) / nblk_) > SCENE_CHANGE_PERCENT_THRESHOLD) {
            lastUpdateTime = bufIn.getTimeStamp();
        } else if ((diffCount == 0) && (lastUpdateTime != -1)) {
            lastBuffer = bufIn;
            for (int i = 0; i < screenListeners.size(); i++) {
                final CaptureChangeListener listener = screenListeners.get(i);
                listener.captureDone(lastUpdateTime);
            }
            lastUpdateTime = -1;
        }

        release();
        return BUFFER_PROCESSED_OK;
    }
    /**
     * @see javax.media.Codec#setInputFormat(javax.media.Format)
     */
    public Format setInputFormat(Format input) {
        format = (RGBFormat) input;
        pixelStride = format.getPixelStride();
        lineStride = format.getLineStride();
        redMask = format.getRedMask();
        blueMask = format.getBlueMask();
        greenMask = format.getGreenMask();
        return input;
    }

    /**
     * @see javax.media.PlugIn#close()
     */
    public void close() {
        nblk_ = -1;
        unsafe.freeMemory(refbuf_);
        unsafe.freeMemory(devbuf_);
    }

    /**
     * @see javax.media.PlugIn#getName()
     */
    public String getName() {
        return NAME;
    }

    /**
     * @see javax.media.PlugIn#open()
     */
    public void open() {
        // no action required
    }

    /**
     * @see javax.media.PlugIn#reset()
     */
    public void reset() {

        // no action required
    }

    /**
     * @see javax.media.Controls#getControl(java.lang.String)
     */
    public Object getControl(String arg0) {
        return null;
    }

    /**
     * @see javax.media.Controls#getControls()
     */
    public Object[] getControls() {
        return null;
    }

    /**
     * Gets the latest captured image
     * @return the latest captured image
     */
    public BufferedImage getImage() {
        BufferedImage image = null;
        if (format.getDataType() == Format.intArray) {
            image = new BufferedImage(
                    format.getSize().width,
                    format.getSize().height,
                    BufferedImage.TYPE_INT_RGB);
            image.setRGB(0, 0, image.getWidth(), image.getHeight(),
                    (int[]) lastBuffer.getData(), 0, image.getWidth());
        } else if (format.getDataType() == Format.byteArray) {
            int w = format.getSize().width;
            int h = format.getSize().height;
            int pixStride = format.getPixelStride();
            int scanlineStride = format.getLineStride();
            int[] bandOffsets = {format.getRedMask() - 1,
                    format.getGreenMask() - 1, format.getBlueMask() - 1};
            byte[] data = new byte[lastBuffer.getLength()];
            System.arraycopy(lastBuffer.getData(), lastBuffer.getOffset(),
                    data, 0, lastBuffer.getLength());
            DataBuffer buffer = new DataBufferByte(
                    data, w * h);
            WritableRaster raster = Raster.createInterleavedRaster(
                    buffer, w, h, scanlineStride, pixStride, bandOffsets,
                    null);

            ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            boolean hasAlpha = false;
            boolean isAlphaPremultiplied = false;
            int transparency = ComponentColorModel.OPAQUE;
            int transferType = DataBuffer.TYPE_BYTE;
            ColorModel colorModel = new ComponentColorModel(colorSpace,
                    hasAlpha, isAlphaPremultiplied, transparency, transferType);

            image = new BufferedImage(colorModel, raster, isAlphaPremultiplied,
                    null);
            if (format.getFlipped() == RGBFormat.TRUE) {
                AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
                tx.translate(0, -image.getHeight(null));
                AffineTransformOp op = new AffineTransformOp(tx,
                        AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                image = op.filter(image, null);
            }
        }
        return image;
    }

    /**
     * Locks the thread against updates
     */
    public synchronized void lock() {
        while (locked) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        locked = true;
    }
    /**
     * Allows updates to proceed
     */
    public synchronized void release() {
        locked = false;
        notifyAll();
    }

    /**
     * Adds a new listener to screen capture events
     * @param listener The listener to add
     */
    public void addScreenListener(CaptureChangeListener listener) {
        if (!screenListeners.contains(listener)) {
            screenListeners.add(listener);
        }
    }

    /**
     * Removes a listener of screen capture events
     * @param listener The listener to remove
     */
    public void removeScreenListener(CaptureChangeListener listener) {
        screenListeners.remove(listener);
    }

    /**
     *
     * @see javax.media.Renderer#start()
     */
    public void start() {
        // Does Nothing
    }

    /**
     *
     * @see javax.media.Renderer#stop()
     */
    public void stop() {
        // Does Nothing
    }
}
