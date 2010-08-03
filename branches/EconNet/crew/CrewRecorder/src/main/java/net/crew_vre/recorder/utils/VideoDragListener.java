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

package net.crew_vre.recorder.utils;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import net.crew_vre.media.renderer.RGBRenderer;
import net.crew_vre.recorder.Recorder;

/**
 * A drag listener for dragging video windows
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class VideoDragListener extends MouseInputAdapter {

    private Recorder recorder = null;

    private JPanel videoPanel = null;

    private JPanel screenPanel = null;

    private RGBRenderer renderer = null;

    private long ssrc = 0;

    private Cursor cursor = null;

    /**
     * Creates a new VideoDragListener
     * @param recorder The recorder
     * @param videoPanel The video panel
     * @param screenPanel The screen panel
     * @param renderer The data source renderer
     * @param ssrc The ssrc of the stream
     */
    public VideoDragListener(Recorder recorder, JPanel videoPanel,
            JPanel screenPanel, RGBRenderer renderer, long ssrc) {
        this.recorder = recorder;
        this.videoPanel = videoPanel;
        this.screenPanel = screenPanel;
        this.renderer = renderer;
        this.ssrc = ssrc;
    }

    /**
     *
     * @see java.awt.event.MouseListener#mouseReleased(
     *     java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
        recorder.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        cursor = null;
        Point videoPanelPoint = videoPanel.getLocationOnScreen();
        Point screenPanelPoint = screenPanel.getLocationOnScreen();
        Point componentPoint = e.getComponent().getLocationOnScreen();
        int xOnScreen = e.getX() + componentPoint.x;
        int yOnScreen = e.getY() + componentPoint.y;
        if (videoPanel.contains(xOnScreen - videoPanelPoint.x,
                yOnScreen - videoPanelPoint.y)) {
            recorder.moveToVideoDisplay(renderer, ssrc);
        } else if (screenPanel.contains(xOnScreen - screenPanelPoint.x,
                yOnScreen - screenPanelPoint.y)) {
            recorder.moveToScreenDisplay(renderer, ssrc);
        }
    }

    /**
     *
     * @see java.awt.event.MouseMotionListener#mouseDragged(
     * java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent e) {
        if (cursor == null) {
            cursor = new Cursor(Cursor.HAND_CURSOR);
            recorder.setCursor(cursor);
        }
    }

}
