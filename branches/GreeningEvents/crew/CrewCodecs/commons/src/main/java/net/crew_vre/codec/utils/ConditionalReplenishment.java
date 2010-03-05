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


package net.crew_vre.codec.utils;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

/**
 * Decides which blocks should be replenished
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ConditionalReplenishment {

    public static final int CR_MOTION = 0;
    private static final int CR_AGETHRESH = 31;
    private static final int CR_IDLE = 0x40;
    public static final int CR_BG = 0x41;
    private static final int CR_SEND = 0x80;

    private static final int CR_STATE(int s) {
        return ((s) & 0x7f);
    }

    private int[] crvec_ = null;

    private long refbuf_ = 0;

    private int scan_ = 0;

    private int rover_ = 0;

    private int blkw_ = 0;

    private int blkh_ = 0;

    private int nblk_ = 0;

    private int width_ = 0;

    private int threshold_ = 48;

    private Unsafe unsafe = null;

    private long outObjectOffset = 0;

    private long byteArrayOffset = 0;

    private Object outObject = null;

    private int width = 0;

    private int height = 0;

    /**
     * Creates a new ConditionalReplenishment
     *
     * @param width
     *            The width to replenish
     * @param height
     *            The height to replenish
     */
    public ConditionalReplenishment(int width, int height) {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            outObjectOffset = unsafe
                    .objectFieldOffset(ConditionalReplenishment.class
                            .getDeclaredField("outObject"));
            byteArrayOffset = unsafe.arrayBaseOffset(byte[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.width_ = width;
        blkw_ = width >> 4;
        blkh_ = height >> 4;
        nblk_ = blkw_ * blkh_;
        crvec_ = new int[nblk_];
        reset();
        refbuf_ = unsafe.allocateMemory(width * height);
        unsafe.setMemory(refbuf_, width * height, (byte) 0);
        this.width = width;
        this.height = height;
    }

    /**
     * Resets the replenishment so all blocks appear new
     */
    public void reset() {
        for (int i = 0; i < nblk_; i++) {
            crvec_[i] = CR_MOTION | CR_SEND;
        }
        unsafe.setMemory(refbuf_, width * height, (byte) 0);
    }

    /**
     * Updates the replenishment
     *
     * @param devbuf
     *            The buffer to update with
     */
    public void replenish(byte[] devbuf) {

        /*
         * First age the blocks from the previous frame.
         */
        ageBlocks();
        outObject = devbuf;

        int _ds = width_;
        int _rs = width_;
        long devbufAddr = (unsafe.getInt(this, outObjectOffset) & 0xffffffffl)
                + byteArrayOffset;
        long db = devbufAddr + scan_ * _ds;
        long rb = refbuf_ + scan_ * _rs;
        int w = blkw_;
        int crv = 0;

        for (int y = 0; y < blkh_; ++y) {
            long ndb = db;
            long nrb = rb;
            int ncrv = crv;
            for (int x = 0; x < blkw_; x++) {
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
                top += (unsafe.getByte(db + 0 + 1 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 0 + 1 * 4) & 0xFF);
                top += (unsafe.getByte(db + 1 + 1 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 1 + 1 * 4) & 0xFF);
                top += (unsafe.getByte(db + 2 + 1 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 2 + 1 * 4) & 0xFF);
                top += (unsafe.getByte(db + 3 + 1 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 3 + 1 * 4) & 0xFF);
                top += (unsafe.getByte(db + 0 + 2 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 0 + 2 * 4) & 0xFF);
                top += (unsafe.getByte(db + 1 + 2 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 1 + 2 * 4) & 0xFF);
                top += (unsafe.getByte(db + 2 + 2 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 2 + 2 * 4) & 0xFF);
                top += (unsafe.getByte(db + 3 + 2 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 3 + 2 * 4) & 0xFF);
                right += (unsafe.getByte(db + 0 + 3 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 0 + 3 * 4) & 0xFF);
                right += (unsafe.getByte(db + 1 + 3 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 1 + 3 * 4) & 0xFF);
                right += (unsafe.getByte(db + 2 + 3 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 2 + 3 * 4) & 0xFF);
                right += (unsafe.getByte(db + 3 + 3 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 3 + 3 * 4) & 0xFF);
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
                bottom += (unsafe.getByte(db + 0 + 1 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 0 + 1 * 4) & 0xFF);
                bottom += (unsafe.getByte(db + 1 + 1 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 1 + 1 * 4) & 0xFF);
                bottom += (unsafe.getByte(db + 2 + 1 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 2 + 1 * 4) & 0xFF);
                bottom += (unsafe.getByte(db + 3 + 1 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 3 + 1 * 4) & 0xFF);
                bottom += (unsafe.getByte(db + 0 + 2 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 0 + 2 * 4) & 0xFF);
                bottom += (unsafe.getByte(db + 1 + 2 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 1 + 2 * 4) & 0xFF);
                bottom += (unsafe.getByte(db + 2 + 2 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 2 + 2 * 4) & 0xFF);
                bottom += (unsafe.getByte(db + 3 + 2 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 3 + 2 * 4) & 0xFF);
                right += (unsafe.getByte(db + 0 + 3 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 0 + 3 * 4) & 0xFF);
                right += (unsafe.getByte(db + 1 + 3 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 1 + 3 * 4) & 0xFF);
                right += (unsafe.getByte(db + 2 + 3 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 2 + 3 * 4) & 0xFF);
                right += (unsafe.getByte(db + 3 + 3 * 4) & 0xFF)
                        - (unsafe.getByte(rb + 3 + 3 * 4) & 0xFF);
                right = Math.abs(right);
                left = Math.abs(left);
                bottom = Math.abs(bottom);
                db -= _ds << 3;
                rb -= _rs << 3;

                int center = 0;
                if (left >= threshold_ && x > 0) {
                    crvec_[crv - 1] = CR_MOTION | CR_SEND;
                    center = 1;
                }
                if (right >= threshold_ && x < w - 1) {
                    crvec_[crv + 1] = CR_MOTION | CR_SEND;
                    center = 1;
                }
                if (bottom >= threshold_ && y < blkh_ - 1) {
                    crvec_[crv + w] = CR_MOTION | CR_SEND;
                    center = 1;
                }
                if (top >= threshold_ && y > 0) {
                    crvec_[crv - w] = CR_MOTION | CR_SEND;
                    center = 1;
                }
                if (center > 0) {
                    crvec_[crv + 0] = CR_MOTION | CR_SEND;
                }

                db += 16;
                rb += 16;
                ++crv;
            }
            db = ndb + (_ds << 4);
            rb = nrb + (_rs << 4);
            crv = ncrv + w;
        }
        saveblks(devbufAddr);

        /*
         * Bump the CR scan pointer. This variable controls which scan line of a
         * block we use to make the replenishment decision. We skip 3 lines at a
         * time to quickly precess over the block. Since 3 and 8 are coprime, we
         * will sweep out every line.
         */
        scan_ = (scan_ + 3) & 7;
    }

    private void ageBlocks() {
        for (int i = 0; i < nblk_; ++i) {
            int s = CR_STATE(crvec_[i]);
            /*
             * Age this block. Once we hit the age threshold, we set CR_SEND as
             * a hint to send a higher-quality version of the block. After this
             * the block will stop aging, until there is motion. In the
             * meantime, we might send it as background fill using the highest
             * quality.
             */
            if (s <= CR_AGETHRESH) {
                if (s == CR_AGETHRESH)
                    s = CR_IDLE;
                else {
                    if (++s == CR_AGETHRESH)
                        s |= CR_SEND;
                }
                crvec_[i] = s;
            } else if (s == CR_BG)
                /*
                 * reset the block to IDLE if it was sent as a BG block in the
                 * last frame.
                 */
                crvec_[i] = CR_IDLE;
        }
        /*
         * Now go through and look for some idle blocks to send as background
         * fill.
         */
        int blkno = rover_;
        int n = 2;
        while (n > 0) {
            int s = CR_STATE(crvec_[blkno]);
            if (s == CR_IDLE) {
                crvec_[blkno] = CR_SEND | CR_BG;
                --n;
            }
            if (++blkno >= nblk_) {
                blkno = 0;
                /* one way to guarantee loop termination */
                break;
            }
        }
        rover_ = blkno;
    }

    private void save(long lum, int pos, int stride) {
        for (int i = 16; --i >= 0;) {
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
                if ((crvec_[crv++] & CR_SEND) != 0) {
                    save(lum, pos, width_);
                }
                pos += 16;
            }
            pos += stride;
        }
    }

    /**
     * Gets the conditional replenishment for a block
     *
     * @param block
     *            The block
     * @return The state
     */
    public int getCrState(int block) {
        return CR_STATE(crvec_[block]);
    }

    /**
     * Determines if a block should be sent
     *
     * @param block
     *            The block to decide
     * @return True if the block should be sent
     */
    public boolean send(int block) {
        return (crvec_[block] & CR_SEND) > 0;
    }

    /**
     * Frees any data structures in use
     */
    public void close() {
        unsafe.freeMemory(refbuf_);
    }
}
