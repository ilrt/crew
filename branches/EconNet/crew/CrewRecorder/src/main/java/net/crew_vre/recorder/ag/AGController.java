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

package net.crew_vre.recorder.ag;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.media.rtp.LocalParticipant;
import javax.media.rtp.RTPControl;
import javax.media.rtp.RTPManager;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.RemoteListener;
import javax.media.rtp.event.ByeEvent;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.ReceiverReportEvent;
import javax.media.rtp.event.RemoteEvent;
import javax.media.rtp.event.SenderReportEvent;
import javax.media.rtp.rtcp.Report;
import javax.media.rtp.rtcp.SourceDescription;

import org.xml.sax.SAXException;

import net.crew_vre.media.rtp.BridgedRTPConnector;
import net.crew_vre.media.rtp.UnsupportedEncryptionException;
import net.crew_vre.recorder.recording.RecordArchiveManager;
import ag3.ClientUpdateThread;
import ag3.interfaces.Venue;
import ag3.interfaces.types.BridgeDescription;
import ag3.interfaces.types.Capability;
import ag3.interfaces.types.ClientProfile;
import ag3.interfaces.types.ConnectionDescription;
import ag3.interfaces.types.NetworkLocation;
import ag3.interfaces.types.StreamDescription;

/**
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class AGController {

    private ClientProfile profile = null;

    private Capability[] capabilities = null;

    private BridgeDescription bridge = null;

    private String encryptionKey = null;

    private Venue currentVenue = null;

    private ClientUpdateThread currentVenueUpdater = null;

    private String currentConnectionId = null;

    private Vector<RTPManager> receiveManagers = new Vector<RTPManager>();

    private Vector<BridgedRTPConnector> agConnectors =
        new Vector<BridgedRTPConnector>();

    private Vector<UpdateHandler> updateHandlers = new Vector<UpdateHandler>();

    private HashSet<Long> existingSsrcs = new HashSet<Long>();

    private HashMap<Integer, Format> mappedFormats =
        new HashMap<Integer, Format>();

    private StreamListener listener = null;

    /**
     * @param bridge The bridge to connect to
     * @param capabilities The capabilities to connect with
     * @param encryptionKey The encryption key to use
     * @param profile The client profile
     */
    public AGController(BridgeDescription bridge, Capability[] capabilities,
            String encryptionKey, ClientProfile profile) {
        this.bridge = bridge;
        this.capabilities = capabilities;
        this.encryptionKey = encryptionKey;
        this.profile = profile;
    }

    /**
     * Maps a format to an RTP Type identifier
     * @param rtpType The RTP Type
     * @param format The format to map to
     */
    public void mapFormat(int rtpType, Format format) {
        mappedFormats.put(rtpType, format);
        if (receiveManagers != null) {
            for (int i = 0; i < receiveManagers.size(); i++) {
                receiveManagers.get(i).addFormat(format, rtpType);
            }
        }
    }

    /**
     * Sets the stream listener
     * @param listener The listener to set
     */
    public void setListener(StreamListener listener) {
        this.listener = listener;
        for (int i = 0; i < updateHandlers.size(); i++) {
            updateHandlers.get(i).setStreamListener(listener);
        }
    }

    private void addAllFormats(RTPManager manager) {
        for (int i : mappedFormats.keySet()) {
            manager.addFormat(mappedFormats.get(i), i);
        }
    }

    private long getNewSsrc() {
        long ssrc = (long) (Math.random() * Integer.MAX_VALUE);
        while (existingSsrcs.contains(ssrc)) {
            ssrc = (long) (Math.random() * Integer.MAX_VALUE);
        }
        return ssrc;
    }

    /**
     * Joins a venue
     * @param venueDescription The venue description
     * @throws SAXException
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws UnsupportedEncryptionException
     */
    public void joinVenue(final ConnectionDescription venueDescription)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, IOException, SAXException,
            ClassNotFoundException, InstantiationException,
            UnsupportedEncryptionException {
        leaveCurrentVenue();
        currentVenue = new Venue(
                venueDescription.getUri());
        currentConnectionId = currentVenue.enter(profile);
        currentVenueUpdater = new ClientUpdateThread(
                currentVenue, currentConnectionId);
        StreamDescription[] streams =
            currentVenue.negotiateCapabilities(
                currentConnectionId, capabilities);

        for (int i = 0; i < streams.length; i++) {
            Vector<Capability> streamCaps = streams[i].getCapability();
            boolean matches = false;
            for (int j = 0; (j < streamCaps.size()) && !matches; j++) {
                Capability cap = streamCaps.get(j);
                for (int k = 0; (k < capabilities.length) && !matches; k++) {
                    if (cap.matches(capabilities[k])) {
                        matches = true;
                    }
                }
            }
            if (matches) {
                NetworkLocation location = streams[i].getLocation();
                BridgedRTPConnector connector = new BridgedRTPConnector(bridge,
                        new NetworkLocation[]{location});
                connector.setEncryption(encryptionKey);
                UpdateHandler handler = new UpdateHandler();
                RTPManager manager = RTPManager.newInstance();
                addAllFormats(manager);
                manager.addReceiveStreamListener(handler);
                manager.addRemoteListener(handler);
                manager.initialize(connector);
                LocalParticipant localParticipant =
                    manager.getLocalParticipant();

                Report report = (Report) localParticipant.getReports().get(0);
                long localssrc = report.getSSRC();
                if (localssrc < 0) {
                    localssrc += Math.pow(2, 32);
                }
                connector.addStream(localssrc, location);

                agConnectors.add(connector);
                updateHandlers.add(handler);
                receiveManagers.add(manager);
            }
        }
    }

    /**
     * Leaves the current venue
     */
    public void leaveCurrentVenue() {
        if (currentVenueUpdater != null) {
            currentVenueUpdater.close();
            currentVenueUpdater = null;
        }
        if (currentConnectionId != null) {
            try {
                currentVenue.exit(currentConnectionId);
            } catch (Exception e) {
                // Do Nothing
            }
            currentConnectionId = null;
            currentVenue = null;
        }

        if (receiveManagers != null) {
            for (int i = 0; i < receiveManagers.size(); i++) {
                RTPManager manager = receiveManagers.get(i);
                if (manager != null) {
                    manager.removeTargets("Leaving");
                    manager.dispose();
                }
            }
        }
    }

    /**
     * Sets the archive manager
     * @param manager The manager to set
     */
    public void setArchiveManager(RecordArchiveManager manager) {
        for (int i = 0; i < agConnectors.size(); i++) {
            BridgedRTPConnector connector = agConnectors.get(i);
            connector.setRtpSink(manager);
            connector.setRtcpSink(manager);
        }
    }

    private class UpdateHandler implements ReceiveStreamListener,
            RemoteListener {

        private HashMap<Long, Stream> streams = new HashMap<Long, Stream>();

        private HashMap<Long, Long> ssrcMap = new HashMap<Long, Long>();

        private HashMap<Long, String> agSdesNames = new HashMap<Long, String>();

        private HashMap<Long, String> agSdesDescriptions =
            new HashMap<Long, String>();

        /**
         * @see javax.media.rtp.ReceiveStreamListener#update(
         *     javax.media.rtp.event.ReceiveStreamEvent)
         */
        public void update(ReceiveStreamEvent event) {
            ReceiveStream stream = event.getReceiveStream();
            if (event instanceof NewReceiveStreamEvent) {
                DataSource ds = stream.getDataSource();
                RTPControl ctl = (RTPControl) ds.getControl(
                        "javax.media.rtp.RTPControl");
                if (ctl != null) {
                    Format format = ctl.getFormat();
                    long realSsrc = event.getReceiveStream().getSSRC();
                    if (!ssrcMap.containsKey(realSsrc)) {
                        long ssrc = getNewSsrc();
                        ssrcMap.put(realSsrc, ssrc);
                        String name = "Waiting for name for stream " + ssrc;
                        Stream dataStream = new Stream(ssrc, ds, format,
                                name);
                        streams.put(realSsrc, dataStream);
                        if (listener != null) {
                            if (format instanceof VideoFormat) {
                                listener.addVideoStream(ssrc, ds,
                                        (VideoFormat) format);
                                listener.setVideoStreamName(ssrc, name);
                            } else if (format instanceof AudioFormat) {
                                listener.addAudioStream(ssrc, ds,
                                        (AudioFormat) format);
                                listener.setAudioStreamName(ssrc, name);
                            }
                        }
                    }
                }
            } else if (event instanceof ByeEvent) {
                if (stream != null) {
                    DataSource ds = stream.getDataSource();
                    RTPControl ctl = (RTPControl) ds.getControl(
                            "javax.media.rtp.RTPControl");
                    if (ctl != null) {
                        Format format = ctl.getFormat();
                        long realSsrc = event.getReceiveStream().getSSRC();
                        long ssrc = ssrcMap.get(realSsrc);
                        streams.remove(realSsrc);
                        if (format instanceof VideoFormat) {
                            if (listener != null) {
                                listener.removeVideoStream(ssrc);
                            }
                        } else if (format instanceof AudioFormat) {
                            if (listener != null) {
                                listener.removeAudioStream(ssrc);
                            }
                        }
                    }
                }
            }
        }

        /**
         * @see javax.media.rtp.RemoteListener#update(
         *     javax.media.rtp.event.RemoteEvent)
         */
        public void update(RemoteEvent event) {
            if ((event instanceof SenderReportEvent)
                    || (event instanceof ReceiverReportEvent)) {

                // Get the report
                Report report = null;
                if (event instanceof SenderReportEvent) {
                    report = ((SenderReportEvent) event).getReport();
                } else {
                    report = ((ReceiverReportEvent) event).getReport();
                }
                if ((report != null) && (report.getParticipant() != null)) {
                    long realSsrc = report.getSSRC();
                    if (ssrcMap.containsKey(realSsrc)) {
                        long ssrc = ssrcMap.get(realSsrc);
                        Vector< ? > sdes =
                            report.getSourceDescription();
                        Stream stream = streams.get(realSsrc);
                        if ((sdes != null) && (stream != null)) {
                            String name = agSdesNames.get(ssrc);
                            String description = agSdesDescriptions.get(ssrc);
                            String cname = null;
                            for (int i = 0; i < sdes.size(); i++) {
                                SourceDescription d = (SourceDescription) sdes.get(i);
                                if (d.getType()
                                        == SourceDescription.SOURCE_DESC_NAME) {
                                    name = d.getDescription();
                                } else if (d.getType()
                                        == SourceDescription.SOURCE_DESC_NOTE) {
                                    description = d.getDescription();
                                } else if (d.getType()
                                       == SourceDescription.SOURCE_DESC_CNAME) {
                                    cname = d.getDescription();
                                }
                            }
                            String text = "";
                            String cnameText = " (from " + cname + ")";
                            if (name != null) {
                                text = name;
                                agSdesNames.put(ssrc, name);
                                if (description != null) {
                                    text += " - " + description;
                                    agSdesDescriptions.put(ssrc, description);
                                }
                                text += cnameText;
                            } else {
                                text = stream.name;
                                if (!text.endsWith(cnameText)) {
                                    text += cnameText;
                                }
                            }
                            stream.name = text;
                            if (listener != null) {
                                if (stream.format instanceof VideoFormat) {
                                    listener.setVideoStreamName(ssrc, text);
                                } else if (stream.format instanceof AudioFormat) {
                                    listener.setAudioStreamName(ssrc, text);
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Sets the stream listener
         * @param listener The listener
         */
        public void setStreamListener(StreamListener listener) {
            if (listener != null) {
                for (Stream stream : streams.values()) {
                    if (stream.format instanceof VideoFormat) {
                        listener.addVideoStream(stream.ssrc, stream.dataSource,
                                (VideoFormat) stream.format);
                        listener.setVideoStreamName(stream.ssrc, stream.name);
                    } else if (stream.format instanceof AudioFormat) {
                        listener.addAudioStream(stream.ssrc, stream.dataSource,
                                (AudioFormat) stream.format);
                        listener.setAudioStreamName(stream.ssrc, stream.name);
                    }
                }
            }
        }
    }

    private class Stream {

        private long ssrc = 0;

        private DataSource dataSource = null;

        private Format format = null;

        private String name = null;

        public Stream(long ssrc, DataSource dataSource, Format format,
                String name) {
            this.ssrc = ssrc;
            this.dataSource = dataSource;
            this.format = format;
            this.name = name;
        }
    }
}
