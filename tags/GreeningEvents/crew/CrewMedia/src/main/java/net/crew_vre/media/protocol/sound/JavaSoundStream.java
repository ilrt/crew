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

package net.crew_vre.media.protocol.sound;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.MediaLocator;
import javax.media.SystemTimeBase;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.TargetDataLine;

/**
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class JavaSoundStream implements PushBufferStream {

    private static final int BUFFER_DURATION = 125;

    private static final int AUDIO_BITRATE = 44100;

    private static final int AUDIO_DEPTH = 16;

    private static HashMap<String, Mixer> listenMixers = null;

    private static HashMap<String, Mixer> portMixers = null;

    private TargetDataLine targetDataLine = null;

    private javax.media.format.AudioFormat jmfFormat = null;

    private javax.sound.sampled.AudioFormat jsoundFormat = null;

    private BufferTransferHandler transferHandler = null;

    private int bufferSize = 0;

    private Buffer buffer = new Buffer();

    private boolean done = false;

    private long sequence = 0;

    private boolean canRead = false;

    private boolean canWrite = true;

    private Integer readSync = new Integer(0);

    private boolean started = false;

    /**
     * Initialises the stream
     * @param locator The locator of the stream
     * @throws LineUnavailableException
     */
    public void init(MediaLocator locator) throws LineUnavailableException {
        float rate = AUDIO_BITRATE;
        int channels = 1;
        int bits = AUDIO_DEPTH;
        boolean bigEndian = false;
        boolean signed = true;
        String mixer = null;

        String config = locator.getRemainder();
        while (config.charAt(0) == '/') {
            config = config.substring(1);
        }
        String[] options = config.split("&");
        for (int i = 0; i < options.length; i++) {
            String[] option = options[i].split("=", 2);
            if (option[0].equals("rate")) {
                rate = Float.parseFloat(option[1]);
            } else if (option[0].equals("channels")) {
                channels = Integer.parseInt(option[1]);
            } else if (option[0].equals("bits")) {
                bits = Integer.parseInt(option[1]);
            } else if (option[0].equals("endian")) {
                bigEndian = option[1].equals("big");
            } else if (option[0].equals("signed")) {
                signed = option[1].equals("signed");
            } else if (option[0].equals("mixer")) {
                mixer = option[1];
            }
        }

        jsoundFormat = new javax.sound.sampled.AudioFormat(
                rate, bits, channels, signed, bigEndian);
        if (mixer != null) {
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class,
                    jsoundFormat);
            Mixer listenMixer = listenMixers.get(mixer);
            targetDataLine = (TargetDataLine) listenMixer.getLine(dataLineInfo);
        } else {
            targetDataLine = AudioSystem.getTargetDataLine(jsoundFormat);
        }
        targetDataLine.open();
        jsoundFormat = targetDataLine.getFormat();
        jmfFormat = new javax.media.format.AudioFormat(
                javax.media.format.AudioFormat.LINEAR,
                jsoundFormat.getSampleRate(),
                jsoundFormat.getSampleSizeInBits(),
                jsoundFormat.getChannels(),
                jsoundFormat.isBigEndian()?
                    javax.media.format.AudioFormat.BIG_ENDIAN :
                    javax.media.format.AudioFormat.LITTLE_ENDIAN,
                signed? javax.media.format.AudioFormat.SIGNED :
                    javax.media.format.AudioFormat.UNSIGNED);

        bufferSize = (int) ((rate * bits * channels * BUFFER_DURATION) / 8000);
        while ((bufferSize % bits != 0)) {
            bufferSize += 1;
        }
    }

    /**
     * Un-initialises the stream
     */
    public void uninit() {
        targetDataLine.close();
    }

    /**
     * Searches for mixers
     */
    public static final void findMixers() {
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        Line.Info portInfo = new Line.Info(Port.class);
        Line.Info targetInfo = new Line.Info(TargetDataLine.class);
        listenMixers = new HashMap<String, Mixer>();
        portMixers = new HashMap<String, Mixer>();

        for (int i = 0; i < mixerInfos.length; i++) {
            Mixer mixer = AudioSystem.getMixer(mixerInfos[i]);
            if (mixer.isLineSupported(portInfo)) {
                if (mixer.getSourceLineInfo().length > 0) {
                    portMixers.put(mixerInfos[i].getName(), mixer);
                }
            } else if (mixer.isLineSupported(targetInfo)) {
                if (mixer.getTargetLineInfo().length > 0) {
                    listenMixers.put(mixerInfos[i].getName(), mixer);
                }
            }
        }
        Vector<String> mixersToRemove = new Vector<String>();
        Iterator<String> iter = listenMixers.keySet().iterator();
        while (iter.hasNext()) {
            String name = iter.next();
            Mixer portMixer = portMixers.get("Port " + name);
            if (portMixer == null) {
                mixersToRemove.add(name);
            }
        }
        for (int i = 0; i < mixersToRemove.size(); i++) {
            String name = mixersToRemove.get(i);
            listenMixers.remove(name);
        }
    }

    /**
     * Gets the mixers compatible with this data source
     * @return A list of mixer names that can be passed to the locator as mixer=
     */
    public static final Vector<String> getCompatibleMixers() {
        if (listenMixers == null) {
            findMixers();
        }
        return new Vector<String>(listenMixers.keySet());
    }

    /**
     * Gets a mixer with the given name
     * @param name The name of the mixer as returned by getCompatibleMixers
     * @return The mixer, or null if none
     */
    public static final Mixer getMixer(String name) {
        if (listenMixers == null) {
            findMixers();
        }
        return listenMixers.get(name);
    }

    /**
     * Gets the port mixer for a mixer
     * @param name The name of the mixer as returned by getCompatibleMixers
     * @return The mixer or null of none
     */
    public static final Mixer getPortMixer(String name) {
        if (portMixers == null) {
            findMixers();
        }
        return portMixers.get("Port " + name);
    }

    /**
     * @see javax.media.protocol.PushBufferStream#getFormat()
     */
    public Format getFormat() {
        return jmfFormat;
    }

    /**
     * @see javax.media.protocol.PushBufferStream#read(javax.media.Buffer)
     */
    public void read(Buffer readBuffer) {
        synchronized (readSync) {
            while (!canRead && !done) {
                try {
                    readSync.wait();
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
            canRead = false;
        }
        byte[] data = (byte []) readBuffer.getData();
        readBuffer.copy(buffer);
        buffer.setData(data);

        synchronized (readSync) {
            canWrite = true;
            readSync.notifyAll();
        }
    }

    /**
     * @see javax.media.protocol.PushBufferStream#setTransferHandler(
     *    javax.media.protocol.BufferTransferHandler)
     */
    public void setTransferHandler(BufferTransferHandler transferHandler) {
        this.transferHandler = transferHandler;
    }

    /**
     * @see javax.media.protocol.SourceStream#endOfStream()
     */
    public boolean endOfStream() {
        return false;
    }

    /**
     * @see javax.media.protocol.SourceStream#getContentDescriptor()
     */
    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(ContentDescriptor.RAW);
    }

    /**
     * @see javax.media.protocol.SourceStream#getContentLength()
     */
    public long getContentLength() {
        return LENGTH_UNKNOWN;
    }

    /**
     * @see javax.media.Controls#getControl(java.lang.String)
     */
    public Object getControl(String className) {
        return null;
    }

    /**
     * @see javax.media.Controls#getControls()
     */
    public Object[] getControls() {
        return new Object[0];
    }

    /**
     * Starts the stream
     */
    public void start() {
        if (!started) {
            started = true;
            done = false;
            targetDataLine.start();
            Reader reader = new Reader(this);
            reader.start();
        }
    }

    /**
     * Stops the stream
     */
    public void stop() {
        if (started) {
            started = false;
            done = true;
            targetDataLine.stop();
            synchronized (readSync) {
                readSync.notifyAll();
            }
        }
    }

    private class Reader extends Thread {

        private SystemTimeBase timebase = new SystemTimeBase();

        private JavaSoundStream stream = null;

        private Reader(JavaSoundStream stream) {
            this.stream = stream;
        }

        /**
         *
         * @see java.lang.Thread#run()
         */
        public void run() {
            while (!done) {
                synchronized (readSync) {
                    while (!canWrite && !done) {
                        try {
                            readSync.wait();
                        } catch (InterruptedException e) {
                            // Do Nothing
                        }
                    }
                    canWrite = false;
                }
                byte[] data = (byte []) buffer.getData();
                if (data == null) {
                    data = new byte[bufferSize];
                    buffer.setData(data);
                }
                int bytesRead = targetDataLine.read(data, 0, data.length);

                buffer.setOffset(0);
                buffer.setLength(bytesRead);
                buffer.setFormat(jmfFormat);
                buffer.setFlags(
                        Buffer.FLAG_SYSTEM_TIME | Buffer.FLAG_LIVE_DATA);
                buffer.setTimeStamp(timebase.getNanoseconds());
                buffer.setSequenceNumber(sequence++);

                synchronized (readSync) {
                    canRead = true;
                    readSync.notifyAll();
                }

                if (transferHandler != null) {
                    transferHandler.transferData(stream);
                }
            }
        }
    }

}
