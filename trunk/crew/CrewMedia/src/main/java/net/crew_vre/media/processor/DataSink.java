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

import java.io.IOException;

import javax.media.Buffer;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.DataSource;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.media.protocol.PushDataSource;
import javax.media.protocol.PushSourceStream;
import javax.media.protocol.SourceTransferHandler;

/**
 * @author Andrew G D Rowley
 * @version 1.0
 */
public abstract class DataSink extends Thread implements SourceTransferHandler,
        BufferTransferHandler {

    private static final int DATA_LENGTH = 10000;

    private DataSource dataSource = null;

    private int track = 0;

    private boolean done = false;

    private Buffer inputBuffer = new Buffer();

    private byte[] data = new byte[DATA_LENGTH];

    /**
     * A DataSink for a track
     *
     * @param dataSource The data source
     * @param track The track to process
     */
    public DataSink(DataSource dataSource, int track) {
        this.dataSource = dataSource;
        this.track = track;
    }

    /**
     *
     * @see java.lang.Thread#run()
     */
    public void run() {
        try {
            dataSource.connect();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (dataSource instanceof PushBufferDataSource) {
            PushBufferStream[] streams = ((PushBufferDataSource)
                    dataSource).getStreams();
            streams[track].setTransferHandler(this);
            try {
                dataSource.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (dataSource instanceof PushDataSource) {
            PushSourceStream[] streams = ((PushDataSource)
                    dataSource).getStreams();
            streams[track].setTransferHandler(this);
            try {
                dataSource.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (dataSource instanceof PullBufferDataSource) {
            try {
                dataSource.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PullBufferStream[] streams = ((PullBufferDataSource)
                    dataSource).getStreams();
            PullBufferStream stream = streams[track];
            while (!stream.endOfStream() && !done) {
                try {
                    inputBuffer.setData(null);
                    inputBuffer.setLength(0);
                    inputBuffer.setOffset(0);
                    stream.read(inputBuffer);
                    handleBuffer(inputBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                    synchronized (this) {
                        done = true;
                        notifyAll();
                    }
                }
            }
        } else if (dataSource instanceof PullDataSource) {
            try {
                dataSource.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PullSourceStream[] streams = ((PullDataSource)
                    dataSource).getStreams();
            PullSourceStream stream = streams[track];
            int bytesRead = 0;
            while (!stream.endOfStream() && !done && (bytesRead != -1)) {
                try {
                    bytesRead = stream.read(data, 0, data.length);
                    if (bytesRead != -1) {
                        inputBuffer.setData(data);
                        inputBuffer.setOffset(0);
                        inputBuffer.setLength(bytesRead);
                        handleBuffer(inputBuffer);
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    close();
                }
            }
        }
        synchronized (this) {
            done = true;
            notifyAll();
        }
        System.err.println("End of thread");
    }

    /**
     * Stops the processing
     *
     */
    public void close() {
        synchronized (this) {
            done = true;
            notifyAll();
        }
        if (dataSource instanceof PushBufferDataSource) {
            PushBufferStream[] streams = ((PushBufferDataSource)
                    dataSource).getStreams();
            streams[track].setTransferHandler(null);
        } else if (dataSource instanceof PushDataSource) {
            PushSourceStream[] streams = ((PushDataSource)
                    dataSource).getStreams();
            streams[track].setTransferHandler(null);
        }

        try {
            dataSource.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataSource.disconnect();


        data = null;
        inputBuffer.setData(null);
        System.err.println("Closed");
    }

    /**
     *
     * @see javax.media.protocol.SourceTransferHandler#transferData(
     *     javax.media.protocol.PushSourceStream)
     */
    public void transferData(PushSourceStream stream) {
        try {
            int bytesRead = stream.read(data, 0, data.length);
            if (bytesRead != -1) {
                inputBuffer.setData(data);
                inputBuffer.setOffset(0);
                inputBuffer.setLength(bytesRead);
                handleBuffer(inputBuffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @see javax.media.protocol.BufferTransferHandler#transferData(
     *     javax.media.protocol.PushBufferStream)
     */
    public void transferData(PushBufferStream stream) {
        try {
            inputBuffer.setData(null);
            inputBuffer.setLength(0);
            inputBuffer.setOffset(0);
            stream.read(inputBuffer);
            handleBuffer(inputBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles
     * @param buffer The buffer to handle
     * @throws IOException
     */
    public abstract void handleBuffer(Buffer buffer) throws IOException;

    /**
     * Returns true if the data sink has finished writing
     * @return True if the data sink has finished writing
     */
    public boolean isDone() {
        return done;
    }
}
