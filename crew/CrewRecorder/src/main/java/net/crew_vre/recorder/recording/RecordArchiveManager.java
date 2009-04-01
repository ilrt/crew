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

package net.crew_vre.recorder.recording;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import net.crew_vre.constants.CrewConstants;
import net.crew_vre.media.rtp.RTCPHeader;
import net.crew_vre.media.rtp.RTCPPacketSink;
import net.crew_vre.media.rtp.RTPHeader;
import net.crew_vre.media.rtp.RTPPacketSink;
import net.crew_vre.media.rtptype.RtpTypeRepository;
import net.crew_vre.recordings.domain.Recording;
import net.crew_vre.recordings.domain.Stream;

/**
 * Manages the saving of stream errors and streams
 *
 * @author Andrew G D Rowley
 * @version 2-0-alpha
 */
public class RecordArchiveManager extends Thread implements RTPPacketSink,
        RTCPPacketSink {

    /**
     * The prefix of the recording graph for local recordings
     */
    public static final String RECORDING_GRAPH_URI_PREFIX =
        "http://localhost/recordings/";

    private static final String RECORDING_URI_PREFIX =
        "http://www.crew-vre.net/recordings/";

    // The maximum RTP type in the RTCP - RTP conflict range
    private static final int MAX_RTCP_CONFLICT = 76;

    // The minimum RTP type in the RTCP - RTP conflict range
    private static final int MIN_RTCP_CONFLICT = 72;

    // A map of streams to StreamArchives
    private HashMap<Long, StreamArchive> streamMap =
        new HashMap<Long, StreamArchive>();

    // The directory containing the files
    private String directory = "";

    // True if we are currently recording
    private boolean recordFlag = false;

    // The time at which the recording started in milliseconds since the epoch
    private long recordStart = -1;

    // The time at which the recording stopped
    private long recordStop = -1;

    // The streams that have been stopped
    private Vector<Long> stoppedStreams = new Vector<Long>();

    // A queue of packets to be handled
    private LinkedList<DatagramPacket> queue = new LinkedList<DatagramPacket>();

    // The time that a packet was queued
    private LinkedList<Long> queueTime = new LinkedList<Long>();

    // Queues if a packet is RTP or not
    private LinkedList<Boolean> queueIsRTP = new LinkedList<Boolean>();

    // The time that the current packet was recieved
    private long packetRecievedTime = 0;

    // True if the current packet is RTP
    private boolean packetIsRTP = false;

    // True when the thread is to be stopped
    private boolean done = false;

    // A listing of streams added since the last request for new streams
    private Vector<String> newStreams = new Vector<String>();

    // The recording being made
    private Recording recording = new Recording();

    // The RtpTypeRepository
    private RtpTypeRepository typeRepository = null;

    // Synchronises the processing of the queue
    private Integer processSync = new Integer(0);

    /**
     * Creates a new RecordArchiveManager
     * @param typeRepository The RTPTypes to use
     */
    public RecordArchiveManager(RtpTypeRepository typeRepository) {
        this.typeRepository = typeRepository;
        String recordingId = String.valueOf(System.currentTimeMillis())
            + (int) (Math.random() * CrewConstants.ID_NORMALIZATION);
        recording.setId(recordingId);
        recording.setUri(RECORDING_URI_PREFIX + recordingId);
        recording.setGraph(RECORDING_GRAPH_URI_PREFIX + recordingId);
        start();
    }

    /**
     * Sets the directory used to record the session
     * @param directory The directory to record to
     */
    public void setDirectory(String directory) {
        File dir = new File(directory, recording.getId());
        this.directory = dir.getAbsolutePath();
        recording.setDirectory(this.directory);
    }



    // Adds a packet to the queue
    private void addPacket(DatagramPacket packet, boolean isRTP, long time) {
        synchronized (queue) {
            if (!done) {
                queue.addLast(packet);
                queueTime.addLast(new Long(time));
                queueIsRTP.addLast(new Boolean(isRTP));
                queue.notifyAll();
            }
        }
    }

    // Retrieves a packet from the queue
    private DatagramPacket nextPacket() {
        DatagramPacket returnPacket = null;
        synchronized (queue) {
            while (!done && queue.isEmpty()) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {

                    // Do Nothing
                }
            }

            if (!queue.isEmpty()) {
                packetRecievedTime = queueTime.removeFirst();
                packetIsRTP = queueIsRTP.removeFirst();
                returnPacket = queue.removeFirst();
            }
        }
        return returnPacket;
    }

    private void processNextPacket() {

        // Get the next packet
        DatagramPacket packet = nextPacket();

        // Process the packet, depending on its type
        if (packet != null) {
            if (packetIsRTP) {
                processRTPPacket(packet, packetRecievedTime);
            } else {
                processRTCPPacket(packet, packetRecievedTime);
            }
        }
    }

    /**
     * Processes incoming packets
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        done = false;

        // Only stop when signalled
        synchronized (processSync) {
            while (!done) {
                processNextPacket();
            }
        }
    }

    /**
     * Starts recording
     *
     * @param streamId The id of the stream to re-enable, or null to start all
     */
    public void record(String streamId) {
        int index = -1;
        if (streamId != null) {
            index = stoppedStreams.indexOf(Long.valueOf(streamId));
        }
        if (index == -1) {
            enableRecording();
        } else {
            stoppedStreams.remove(index);
        }
    }

    /**
     * Handles an RTP packet
     *
     * @param packet The packet to handle
     */
    public void handleRTPPacket(DatagramPacket packet) {
        addPacket(packet, true, System.currentTimeMillis());
    }

    /**
     * Processes an RTP packet
     *
     * @param packet The packet to handle
     * @param packetRecievedTime The time at which the packet was recieved
     */
    public void processRTPPacket(DatagramPacket packet,
            long packetRecievedTime) {

        try {
            RTPHeader packetHeader = new RTPHeader(packet);

            // If we are not recording, do nothing
            if (!recordFlag) {
                return;
            }
            // If this is RTP version 2 and the type is valid
            if ((packetHeader.getVersion() == RTPHeader.VERSION)
                    && (packetHeader.getPacketType() <= RTPHeader.MAX_PAYLOAD)
                            && ((packetHeader.getPacketType()
                                    < MIN_RTCP_CONFLICT)
                                    || (packetHeader.getPacketType()
                                            > MAX_RTCP_CONFLICT))) {

                // Get the stream archive for this ssrc
                StreamArchive streamArchive = null;
                boolean isStopped = false;
                synchronized (streamMap) {
                    streamArchive = streamMap.get(packetHeader.getSsrc());
                    isStopped = stoppedStreams.contains(packetHeader.getSsrc());
                }

                // If it doesn't exist and has not been stopped, create it
                if ((streamArchive == null) && !isStopped) {
                    File dir = new File(directory);
                    dir.mkdirs();
                    streamArchive = new StreamArchive(this, directory,
                            packetHeader.getSsrc(), typeRepository);
                    synchronized (streamMap) {
                        streamMap.put(packetHeader.getSsrc(), streamArchive);
                    }
                } else if (streamArchive != null) {

                    // Let the stream archive handle the packet
                    streamArchive.handleRTPPacket(packet, packetRecievedTime);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles an RTCP packet
     *
     * @param packet The packet to handle
     */
    public void handleRTCPPacket(DatagramPacket packet) {
        addPacket(packet, false, System.currentTimeMillis());
    }

    /**
     * Processes an RTCP packet
     *
     * @param packet The packet to process
     * @param packetRecievedTime The time at which the packet was recieved
     */
    public void processRTCPPacket(DatagramPacket packet,
            long packetRecievedTime) {

        try {
            RTCPHeader packetHeader = new RTCPHeader(packet);

            // If we are not recording, do nothing
            if (!recordFlag) {
                return;
            }

            // If the packet is the correct version and is a sender report
            if ((packetHeader.getVersion() == RTCPHeader.VERSION)
                    && ((packetHeader.getPacketType() == RTCPHeader.PT_SR)
                           || (packetHeader.getPacketType()
                                   == RTCPHeader.PT_RR))) {

                // Get the archive
                StreamArchive streamArchive = null;
                synchronized (streamMap) {
                    streamArchive = streamMap
                            .get(new Long(packetHeader.getSsrc()));
                }

                if (streamArchive != null) {

                    // Handle the packet
                    streamArchive.handleRTCPPacket(packet, packetRecievedTime);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the recording of packets
     */
    public void enableRecording() {
        recordFlag = true;
    }

    /**
     * Stops the recording of packets
     */
    public void disableRecording() {
        recordFlag = false;
    }

    /**
     * Stops recording of a specific stream
     *
     * @param streamId the id of the stream to stop
     * @throws IOException
     */
    public void teardown(String streamId) throws IOException {
        synchronized (streamMap) {
            StreamArchive archive = streamMap.get(Long.valueOf(streamId));
            if (archive != null) {
                archive.terminate();
                stoppedStreams.add(Long.valueOf(streamId));
                streamMap.remove(Long.valueOf(streamId));
            }
        }
    }

    /**
     * Finish with this recording
     * @throws IOException
     */
    public void terminate() throws IOException {
        disableRecording();

        // Finish processing of queued packets
        done = true;
        synchronized (queue) {
            queue.notifyAll();
        }

        synchronized (processSync) {
            while (!queue.isEmpty()) {
                processNextPacket();
            }
        }

        // Stop each of the archivers
        recordStop = 0;
        recordStart = -1;
        Vector<Stream> streams = new Vector<Stream>();
        synchronized (streamMap) {
            Iterator<Long> iter = streamMap.keySet().iterator();
            while (iter.hasNext()) {
                long ssrc = iter.next();
                StreamArchive archive = streamMap.get(ssrc);
                archive.terminate();
                boolean archiveOK = true;
                if ((archive.getStartTime() >= 0)
                        && ((archive.getStartTime() < recordStart)
                        || (recordStart == -1))) {
                    if (archive.getStartTime() != 0) {
                        recordStart = archive.getStartTime();
                    } else {
                        archiveOK = false;
                    }
                }
                if ((archive.getStartTime() + archive.getDuration())
                        > recordStop) {
                    if (archive.getStartTime() != 0) {
                        recordStop =
                            archive.getStartTime() + archive.getDuration();
                    } else {
                        archiveOK = false;
                    }
                }
                if (archiveOK) {
                    Stream stream = archive.getStream();
                    stream.setUri(recording.getUri() + "/stream/"
                            + stream.getSsrc());
                    stream.setGraph(recording.getGraph());
                    stream.setRecording(recording);
                    streams.add(stream);
                }
            }
        }

        synchronized (newStreams) {
            newStreams.notifyAll();
        }

        // Calculate the duration of the session and store it
        if (recordStart == 0) {
            recordStop = 0;
        }

        recording.setStreams(streams);
        recording.setStartTime(recordStart);
        recording.setEndTime(recordStop);
    }

    /**
     * Returns true if the session is being recorded
     * @return True if we are recording
     */
    public boolean isRecording() {
        return recordFlag;
    }

    /**
     * Gets the recording
     * @return the recording
     */
    public Recording getRecording() {
        return recording;
    }
}
