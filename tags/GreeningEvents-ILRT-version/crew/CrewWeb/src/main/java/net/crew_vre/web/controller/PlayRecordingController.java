/*
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

package net.crew_vre.web.controller;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.crew_vre.annotations.CrewLiveAnnotation;
import net.crew_vre.annotations.liveannotationtype.LiveAnnotationType;
import net.crew_vre.annotations.liveannotationtype.LiveAnnotationTypeRepository;
import net.crew_vre.constants.CrewConstants;
import net.crew_vre.recordings.dao.RecordingDao;
import net.crew_vre.recordings.domain.Recording;
import net.crew_vre.recordings.domain.ReplayLayout;
import net.crew_vre.recordings.domain.ReplayLayoutPosition;
import net.crew_vre.recordings.domain.Stream;
import net.crew_vre.recordings.layout.Layout;
import net.crew_vre.recordings.layout.LayoutPosition;
import net.crew_vre.recordings.layout.LayoutRepository;

import org.caboto.dao.AnnotationDao;
import org.caboto.domain.Annotation;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class PlayRecordingController implements Controller {

    private static final byte[] FLV_TYPE = new byte[] {0x46, 0x4C, 0x56};

    private static final int FLV_DATA_TAG = 0x12;

    private static final int FLV_VERSION = 0x1;

    private static final int DATA_OFFSET = 0x9;

    private static final int BIT_SHIFT_0 = 0;

    private static final int BIT_SHIFT_8 = 8;

    private static final int BIT_SHIFT_16 = 16;

    private static final int BYTE_MASK = 0xFF;

    private class Thumbnail {

        private double start = 0;

        private double end = 0;

        private String filename = null;

        private String yuvfile = null;

        private Thumbnail(double start, double end, String filename,
                String yuvfile) {
            this.start = start;
            this.end = end;
            this.filename = filename;
            this.yuvfile = yuvfile;
        }

        /**
         * Returns the end
         *
         * @return the end
         */
        public double getEnd() {
            return end;
        }

        /**
         * Returns the filename
         *
         * @return the filename
         */
        public String getFilename() {
            return filename;
        }

        public String setFilename(String filename) {
            if (this.filename == null) {
                this.filename = filename;
            }
            return this.filename;
        }

        public String getYuvfile() {
            return yuvfile;
        }

        public String setYUVFile(String yuvfile) {
            if (this.yuvfile == null) {
                this.yuvfile = yuvfile;
            }
            return this.yuvfile;
        }

        /**
         * Returns the start
         *
         * @return the start
         */
        public double getStart() {
            return start;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Thumbnail)) {
                return false;
            }

            Thumbnail t = (Thumbnail) o;
            if (!t.filename.equals(filename)) {
                return false;
            }
            if (t.start != start) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            String hash = filename + start;
            return hash.hashCode();
        }

    }

    private class TextThumbnail extends Thumbnail {

        private String text = null;
        private String type = null;
        private String name = null;
        private String url = null;
        private String email = null;
        private String icon = null;

        private TextThumbnail(double start, double end, String filename,
                String text, String type) {
            super(start, end, filename, null);
            this.text = text;
            this.type = type;
        }

        private TextThumbnail(double start, double end, String icon,
                String text, String type, String name, String url, String email) {
            super(start, end, null, null);
            this.text = text;
            this.icon = icon;
            this.type = type;
            this.name = name;
            this.url = url;
            this.email = email;
        }

        /**
         * Gets the text
         *
         * @return The text
         */
        public String getText() {
            return text;
        }

        /**
         * Gets the text
         *
         * @return The type
         */
        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getIcon() {
            return icon;
        }

        public String getUrl() {
            return url;
        }

        public String getEmail() {
            return email;
        }

        public String setFilename(String filename) {
            return super.setFilename(filename);
        }

        public boolean equals(Object o) {
            if (!(o instanceof TextThumbnail)) {
                return false;
            }
            TextThumbnail t = (TextThumbnail) o;
            if (!t.type.equals(type)) {
                return false;
            }
            if (!t.text.equals(text)) {
                return false;
            }
            return super.equals((Thumbnail) o);
        }

        public int hashCode() {
            String hash = type + text;
            return hash.hashCode();
        }
    }

    private class ThumbSorter implements Comparator<Thumbnail> {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Thumbnail arg0, Thumbnail arg1) {
            if ((arg0.start != 0) && (arg1.start != 0)) {
                return (int) (arg0.start - arg1.start);
            }
            return 0;
        }

    }

    private class MetadataStream {
        private long start = 0;
        private long end = 0;
        private String type;
        private String id;
        private String ssrc;
        private double duration;

        private MetadataStream(Stream stream) {
            start = stream.getStartTime().getTime();
            end = stream.getEndTime().getTime();
            duration = ((double) (end - start)) / CrewConstants.THOUSAND;
            type = stream.getRtpType().getMediaType();
            id = stream.getUri();
            ssrc = stream.getSsrc();
        }

        public long getStart() {
            return start;
        }

        public long getEnd() {
            return end;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public String getSsrc() {
            return ssrc;
        }

        public double getDuration() {
            return duration;
        }

    }

    private class MetadataLayoutPosition {
        private MetadataStream stream = null;
        private String name = null;
        private String type = null;
        private int width = 0;
        private int height = 0;
        private int x = 0;
        private int y = 0;
        private boolean changes = false;

        private MetadataLayoutPosition(String layoutName,
                ReplayLayoutPosition replayLayoutPosition) {
            name = replayLayoutPosition.getName();
            Stream replayStream = replayLayoutPosition.getStream();
            System.out.println("MetadataLayoutPosition[" + name + "]-stream:"
                    + replayStream);
            if (replayStream != null) {
                stream = new MetadataStream(replayStream);
                type = stream.type;
            }
            LayoutPosition layoutPosition = layoutRepository.findLayout(
                    layoutName).findStreamPosition(name);
            if (layoutPosition != null) {
                x = layoutPosition.getX();
                y = layoutPosition.getY();
                width = layoutPosition.getWidth();
                height = layoutPosition.getHeight();
                changes = layoutPosition.hasChanges();
            }
        }

        private MetadataLayoutPosition(LayoutPosition layoutPosition) {
            name = layoutPosition.getName();
            type = layoutPosition.getName();
            x = layoutPosition.getX();
            y = layoutPosition.getY();
            width = layoutPosition.getWidth();
            height = layoutPosition.getHeight();
            changes = layoutPosition.hasChanges();
        }

        private MetadataLayoutPosition(Stream audioStream) {
            name = "audio";
            stream = new MetadataStream(audioStream);
            type = stream.type;
        }

        public MetadataStream getStream() {
            return stream;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean hasChanges() {
            return changes;
        }
    }

    private class MetadataLayout {
        private double timeStamp = 0;
        private String layoutName = null;
        private Vector<MetadataLayoutPosition> layoutPositions =
            new Vector<MetadataLayoutPosition>();

        private MetadataLayout(Recording recording, ReplayLayout replayLayout) {
            this.timeStamp = (double) (replayLayout.getTime().getTime()
                    - recording.getStartTime().getTime()) / CrewConstants.THOUSAND;
            this.layoutName = replayLayout.getName();
            System.out.println("time:" + timeStamp + " layout:" + layoutName);
            Layout layout = layoutRepository.findLayout(replayLayout.getName());
            for (LayoutPosition layoutPosition : layout.getStreamPositions()) {
                if (!layoutPosition.isAssignable()) {
                    layoutPositions.add(
                        new MetadataLayoutPosition(layoutPosition));
                }
            }
            for (Stream stream : recording.getStreams()) {
                if (stream.getRtpType().getMediaType().equals("audio")) {
                    layoutPositions.add(new MetadataLayoutPosition(stream));
                }
            }
            for (ReplayLayoutPosition replayLayoutPosition : replayLayout.getLayoutPositions()) {
                layoutPositions.add(
                    new MetadataLayoutPosition(layoutName, replayLayoutPosition));
            }
        }

        public double getTimeStamp() {
            return timeStamp;
        }

        public String getLayoutName() {
            return layoutName;
        }

        public Vector<MetadataLayoutPosition> getLayoutPositions() {
            return layoutPositions;
        }
    }

    private class MetadataAnnotation {

        private double start = 0;

        private double end = 0;

        private String nodeType = null;

        private String text = null;

        private String colour = null;
        private String name = null;
        private String url = null;
        private String email = null;

        private MetadataAnnotation(double start, double end, String nodeType,
                String text, String colour) {
            this.colour = "0x000000";
            this.start = start;
            this.end = end;
            this.nodeType = nodeType;
            this.text = text;
            if (colour != null) {
                this.colour = colour.replace("#", "0x");
            }
        }

        private MetadataAnnotation(double start, double end, String nodeType,
                String text, String colour, String name, String url,
                String email) {
            this.colour = "0x000000";
            this.start = start;
            this.end = end;
            this.nodeType = nodeType;
            this.text = text;
            if (colour != null) {
                this.colour = colour.replace("#", "0x");
            }
            this.name = name;
            this.url = url;
            this.email = email;
        }

        /**
         * Returns the end
         *
         * @return the end
         */
        public double getEnd() {
            return end;
        }

        /**
         * Returns the text
         *
         * @return the text
         */
        public String getText() {
            return text;
        }

        /**
         * Returns the nodeType
         *
         * @return the nodeType
         */
        public String getNodeType() {
            return nodeType;
        }

        /**
         * Returns the start
         *
         * @return the start
         */
        public double getStart() {
            return start;
        }

        /**
         * Returns the colour
         *
         * @return the colour
         */
        public String getColour() {
            return colour;
        }

        /**
         * Gets the text
         *
         * @return The type
         */
        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public String getEmail() {
            return email;
        }

        public boolean equals(Object o) {
            if (!(o instanceof MetadataAnnotation)) {
                return false;
            }
            MetadataAnnotation a = (MetadataAnnotation) o;
            if (!a.nodeType.equals(nodeType)) {
                return false;
            }
            if (!a.text.equals(text)) {
                return false;
            }
            if (a.start != start) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            String hash = nodeType + text + start;
            return hash.hashCode();
        }

        public boolean update(double startTime, String text) {
            if ((start <= startTime) && (end >= startTime)) {
                this.text = text;
                return true;
            }
            return false;
        }
    }

    private class MetadataAnnotationType implements Comparable<MetadataAnnotationType> {

        private String type = null;

        private String icon = null;

        private String text = null;

        private long index = -1;

        private MetadataAnnotationType(String type, String icon, String text,
                long index) {
            this.type = type;
            this.icon = icon;
            this.text = text;
            this.index = index;
        }

        /**
         * Returns the type
         *
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * Returns the icon
         *
         * @return the icon
         */
        public String getIcon() {
            return icon;
        }

        /**
         * Returns the text
         *
         * @return the text
         */
        public String getText() {
            return text;
        }

        /**
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object o) {
            if (!(o instanceof MetadataAnnotationType)) {
                return false;
            }
            MetadataAnnotationType a = (MetadataAnnotationType) o;
            return a.type.equals(type);
        }

        /**
         *
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return type.hashCode();
        }

        public int compareTo(MetadataAnnotationType o) {
            if (index == -1) {
                return 1;
            }
            if (((MetadataAnnotationType) o).index == -1) {
                return -1;
            }
            long diff = index - ((MetadataAnnotationType) o).index;
            return ((int) diff);
        }
    }

    private Vector<Thumbnail> getSlides(String server, Recording recording, final String ssrc) {
        Vector<Thumbnail> thumb = new Vector<Thumbnail>();

        File dir = new File(recording.getDirectory());

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.startsWith(ssrc) && name.endsWith(".jpg"));
            }
        };

        String[] children = dir.list(filter);
        if (children != null) {
            for (String fname : children) {
                String[] fparts = fname.split("[_.]");
                if (fparts.length < 2) {
                    continue;
                }
                double start = Double.parseDouble(fparts[1]) / CrewConstants.THOUSAND;
                String imgname = "/image/" + recording.getId() + "/" + ssrc
                        + ".jpg?offset=" + fparts[1];
                thumb.add(new Thumbnail(start, start, server + imgname + "&width=200",
                        "." + imgname.replace(".jpg", ".yuv.zip")));
            }
        }
        return thumb;
    }

    // Converts an int into a 3-byte array
    private byte[] intTo24Bits(int value) {
        return new byte[] {(byte) ((value >> BIT_SHIFT_16) & BYTE_MASK),
                (byte) ((value >> BIT_SHIFT_8) & BYTE_MASK),
                (byte) ((value >> BIT_SHIFT_0) & BYTE_MASK)};
    }

    private void writeDataItem(DataOutputStream out, Object value)
            throws IOException {
        if (value == null) {
            out.write(2);
            out.writeShort(0);
        } else {
            Class < ? > cls = value.getClass();
            if (value instanceof String) {
                out.write(2);
                out.writeUTF((String) value);
            } else if (value instanceof Boolean) {
                out.write(1);
                if ((Boolean) value) {
                    out.write(1);
                } else {
                    out.write(0);
                }
            } else if (value instanceof Double) {
                out.write(0);
                out.writeDouble((Double) value);
            } else if (value instanceof Integer) {
                out.write(0);
                out.writeDouble((Integer) value);
            } else if (value instanceof Float) {
                out.write(0);
                out.writeDouble((Float) value);
            } else if (value instanceof Long) {
                out.write(0);
                out.writeDouble((Long) value);
            } else if (cls.isArray()) {
                int length = Array.getLength(value);
                out.write(8);
                out.writeInt(length);
                for (int i = 0; i < length; i++) {
                    out.writeUTF(String.valueOf(i));
                    writeDataItem(out, Array.get(value, i));
                }
                out.writeShort(0);
                out.write(9);
            } else if (value instanceof Collection) {
                Collection< ? > c = (Collection < ? >) value;
                out.write(8);
                out.writeInt(c.size());
                Iterator < ? > iter = c.iterator();
                int i = 0;
                while (iter.hasNext()) {
                    out.writeUTF(String.valueOf(i));
                    writeDataItem(out, iter.next());
                    i++;
                }
                out.writeShort(0);
                out.write(9);
            } else if (value instanceof Map) {
                Map< ? , ? > m = (Map< ? , ? >) value;
                out.write(8);
                out.writeInt(m.size());
                Iterator< ? > iter = m.keySet().iterator();
                while (iter.hasNext()) {
                    String name = (String) iter.next();
                    out.writeUTF(name);
                    writeDataItem(out, m.get(name));
                }
            } else {
                out.write(3);
                while (!cls.equals(Object.class) && (cls != null)) {
                    Method[] methods = cls.getDeclaredMethods();
                    for (int i = 0; i < methods.length; i++) {
                        String name = methods[i].getName();
                        int mod = methods[i].getModifiers();
                        if (Modifier.isPublic(mod) && !Modifier.isStatic(mod)
                                && (methods[i].getParameterTypes().length == 0)) {
                            String var = null;
                            if (name.startsWith("get")) {
                                var = name.substring(3);
                            } else if (name.startsWith("is")) {
                                var = name.substring(2);
                            }
                            if (var != null) {
                                try {
                                    Object result = methods[i].invoke(value,
                                            new Object[0]);
                                    var = var.substring(0, 1).toLowerCase()
                                            + var.substring(1);
                                    out.writeUTF(var);
                                    writeDataItem(out, result);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    cls = cls.getSuperclass();
                }
                out.writeShort(0);
                out.write(9);
            }
        }
    }

    // The size of the buffer

    private RecordingDao recordingDao = null;
    private AnnotationDao annotationDao = null;
    private LayoutRepository layoutRepository = null;
    private LiveAnnotationTypeRepository liveAnnotationTypeRepository = null;

    public PlayRecordingController(RecordingDao recordingDao,
            AnnotationDao annotationDao, LayoutRepository layoutRepository,
            LiveAnnotationTypeRepository liveAnnotationTypeRepository) {
        this.recordingDao = recordingDao;
        this.annotationDao = annotationDao;
        this.layoutRepository = layoutRepository;
        this.liveAnnotationTypeRepository = liveAnnotationTypeRepository;
    }

    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        /**
         * parameters: recordingId
         */
        Vector<MetadataAnnotation> metadataAnnotations = new Vector<MetadataAnnotation>();
        Vector<MetadataAnnotation> slideAnnotations = new Vector<MetadataAnnotation>();

        Vector<Thumbnail> thumbs = new Vector<Thumbnail>();
        Vector<Thumbnail> slideThumbs = new Vector<Thumbnail>();
        Vector<MetadataAnnotationType> metadataAnnotationTypes =
            new Vector<MetadataAnnotationType>();
        Vector<MetadataLayout> metadataLayouts = new Vector<MetadataLayout>();
        String server = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort() + request.getContextPath();
        long duration = CrewConstants.THOUSAND;
        long startTime = 0;
        long annLength = duration / CrewConstants.HUNDRED;
        String recordingId = request.getParameter("recordingId");
        long rStTime;
        String stTime = request.getParameter("startTime");
        if (stTime != null) {
            try {
                startTime = Long.parseLong(request.getParameter("startTime"));
            } catch (NumberFormatException e) {
                startTime = 0;
            }
        }
        System.out.println("recordingId:" + recordingId);
        if (recordingId != null) {
            Recording recording = recordingDao.findRecordingById(recordingId);
            rStTime = recording.getStartTime().getTime();
            duration = recording.getEndTime().getTime() - rStTime;
            annLength = duration / CrewConstants.HUNDRED;
            List<ReplayLayout> replayLayouts = recording.getReplayLayouts();
            System.out.println("layouts: " + replayLayouts);
            for (ReplayLayout replayLayout : replayLayouts) {
                MetadataLayout metadataLayout = new MetadataLayout(recording, replayLayout);
                metadataLayouts.add(metadataLayout);
            }
            for (MetadataLayout lay : metadataLayouts) {
                for (MetadataLayoutPosition layPos : lay.layoutPositions) {
                    if (layPos.hasChanges()) {
                        slideThumbs = getSlides(server, recording, layPos.stream.getSsrc());
                    }
                }
            }
            Collections.sort(slideThumbs, new ThumbSorter());
            double thstart = 0;
            double thend;
            int colindex = 0;
            String colour = CrewConstants.SLIDE_COLOURS[0];
            if (slideThumbs.size() != 0) {
                LiveAnnotationType latype =
                    liveAnnotationTypeRepository.findLiveAnnotationType(CrewConstants.SLIDE_TYPE);
                MetadataAnnotationType matype = new MetadataAnnotationType(
                        CrewConstants.SLIDE_TYPE, server + latype.getThumbnail(),
                        latype.getName(), latype.getIndex());
                if (!metadataAnnotationTypes.contains(matype)) {
                    metadataAnnotationTypes.add(matype);
                }
                for (Thumbnail thumb : slideThumbs) {
                    thend = thumb.getStart();
                    slideAnnotations.add(
                        new MetadataAnnotation(
                                thstart, thend, CrewConstants.SLIDE_TYPE, "", colour));
                    thstart = thend;
                    colindex++;
                    if (colindex == CrewConstants.SLIDE_COLOURS.length) {
                        colindex = 0;
                    }
                    colour = CrewConstants.SLIDE_COLOURS[colindex];
                }
                thend = duration / CrewConstants.THOUSAND;
                slideAnnotations.add(
                    new MetadataAnnotation(thstart, thend, CrewConstants.SLIDE_TYPE, "", colour));
            } else {
                LiveAnnotationType latype =
                    liveAnnotationTypeRepository.findLiveAnnotationType(CrewConstants.SLIDE_TYPE);
                MetadataAnnotationType matype =
                    new MetadataAnnotationType(CrewConstants.SLIDE_TYPE, "", "", latype.getIndex());
                if (!metadataAnnotationTypes.contains(matype)) {
                    metadataAnnotationTypes.add(matype);
                }
            }
            for (Annotation annotation : annotationDao.getAnnotations(recording.getUri())) {
                if (annotation.getType().equals("LiveAnnotation")) {
                    CrewLiveAnnotation liveAnnotation =
                        new CrewLiveAnnotation(liveAnnotationTypeRepository, annotation);
                    double start = (liveAnnotation.getTimestamp() - rStTime)
                            / CrewConstants.THOUSAND;
                    double end = start + annLength / CrewConstants.THOUSAND;
                    boolean addAnn = true;
                    String aType = liveAnnotation.getAnnotationBody().get("liveAnnotationType");
                    LiveAnnotationType latype =
                        liveAnnotationTypeRepository.findLiveAnnotationType(aType);
                    if (aType.equals(CrewConstants.SLIDE_TYPE)) {
                        for (MetadataAnnotation ann : slideAnnotations) {
                            if (ann.update(start, liveAnnotation.getTimeSliderText())) {
                                addAnn = false;
                            }
                        }
                    }
                    MetadataAnnotation metaAnn = new MetadataAnnotation(start, end, aType,
                            liveAnnotation.getTimeSliderText(), latype.getColour());
                    if (addAnn && (!metadataAnnotations.contains(metaAnn))) {
                        metadataAnnotations.add(metaAnn);
                    }
                    TextThumbnail textThumb = new TextThumbnail(start, end,
                            server + latype.getThumbnail(), liveAnnotation.getTimeSliderText(), aType);
                    if (!thumbs.contains(textThumb)) {
                        thumbs.add(textThumb);
                    }
                    MetadataAnnotationType matype = new MetadataAnnotationType(
                            aType, server + latype.getThumbnail(), latype.getName(), latype.getIndex());
                    if (!metadataAnnotationTypes.contains(matype)) {
                        metadataAnnotationTypes.add(matype);
                    }
                }
            }
        }
        metadataAnnotations.addAll(slideAnnotations);
        thumbs.addAll(slideThumbs);
        Collections.sort(thumbs, new ThumbSorter());

        // for-loop sets the filename of an annotation which has no value at the
        // time to the filename of the last annotation that has one.
        if (thumbs.size() > 0) {
            String filename = thumbs.get(0).getFilename();
            String yuvfile = thumbs.get(0).getYuvfile();
            for (Thumbnail thumb : thumbs) {
                filename = thumb.setFilename(filename);
                yuvfile = thumb.setYUVFile(yuvfile);
            }
        }
        HashMap<String, Object> values = new HashMap<String, Object>();
        Collections.sort(metadataAnnotationTypes);
        values.put("startTime", startTime);
        values.put("duration", ((double) (duration) / CrewConstants.THOUSAND));
        values.put("annotationTypes", metadataAnnotationTypes);
        values.put("layouts", metadataLayouts);
        values.put("annotations", metadataAnnotations);
        values.put("thumbnails", thumbs);
        values.put("url", server + "/flv/" + recordingId + ".flv");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);
        // OnMetaData
        data.write(2);
        data.writeUTF("onMetaData");
        // Write the data out
        writeDataItem(data, values);
        data.close();
        bytes.close();
        response.setContentType("video/x-flv");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setDateHeader("Expires", System.currentTimeMillis());
        DataOutputStream out = new DataOutputStream(response.getOutputStream());
        // Write a header
        out.write(FLV_TYPE);
        out.write(FLV_VERSION);
        out.write(0);
        out.writeInt(DATA_OFFSET);
        // Header
        byte[] metadata = bytes.toByteArray();
        out.writeInt(0);
        out.write(FLV_DATA_TAG);
        out.write(intTo24Bits(metadata.length));
        out.writeInt(0);
        out.write(intTo24Bits(0));
        // Write the data
        out.write(metadata);
        out.writeInt(metadata.length + 11);
        out.close();
        return null;
    }

}
