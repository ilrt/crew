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

package net.crew_vre.annotations;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.crew_vre.annotations.liveannotationtype.LiveAnnotationType;
import net.crew_vre.annotations.liveannotationtype.LiveAnnotationTypeRepository;

import org.apache.commons.lang.StringEscapeUtils;
import org.caboto.domain.Annotation;
import org.caboto.domain.AnnotationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CrewLiveAnnotation implements CrewLiveAnnotationEvent {

    private Map<String, String> body = new HashMap<String, String>();
    private Date date;
    private String edits = "";
    private String type = "";
    private String source;
    private String privacy = "public";
    private String annotator = "";
    private String annotationId = "";
    private Object client = null;
    private boolean isAnnotation = true;

    private LiveAnnotationTypeRepository liveAnnotationTypeRepository = null;

    public Object getClient() {
        return client;
    }

    public void setClient(Object client) {
        this.client = client;
    }

    private String checkType() throws AnnotationException {
        String retType = body.get("liveAnnotationType");
        LiveAnnotationType laType = liveAnnotationTypeRepository
                .findLiveAnnotationType(retType);
        if (laType == null) {
            throw new AnnotationException("unknown liveAnnotationType " + retType);
        }

        if (annotationId.length() == 0) {
            annotationId = date.getTime() + "_" + UUID.randomUUID().toString();
        }

        Iterator<String> fields = laType.getContainedFields().iterator();
        int noMissing = 0;
        while (fields.hasNext()) {
            if (body.get(fields.next()) == null) {
                noMissing++;
            }
        }
        if (noMissing == 0) {
            return retType;
        }
        List<String> betterMatch = laType.getConversions();
        Iterator<String> matches = betterMatch.iterator();
        while (matches.hasNext()) {
            int missing = 0;
            String typeName = matches.next();
            fields = liveAnnotationTypeRepository.findLiveAnnotationType(typeName)
                    .getContainedFields().iterator();
            while (fields.hasNext()) {
                if (body.get(fields.next()) == null) {
                    missing++;
                }
            }
            if (missing < noMissing) {
                noMissing = missing;
                retType = typeName;
            }
            if (noMissing == 0) {
                return retType;
            }
        }
        return retType;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDate(long timestamp) {
        this.date = new Date(timestamp);
    }

    public void setBody(HashMap<String, String> body) {
        this.body = body;
    }

    public void setText(String message) {
        this.body.put("CrewLiveAnnotationComment", message);
    }

    public void setLiveAnnotationType(String type) {
        setType(type);
    }

    public void setType(String type) {
        this.body.put("liveAnnotationType", type);
        this.type = type;
    }

    public void setName(String name) {
        this.body.put("CrewLiveAnnotationTitle", name);
    }

    public void setAnnotationId(String annotationId) {
        this.annotationId = annotationId;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public void setUrl(String url) {
        this.body.put("CrewLiveAnnotationUrl", url);
    }

    public void setAuthor(String source) {
        this.source = source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setAnnotator(String annotator) {
        this.annotator = annotator;
    }

    public void setEmail(String email) {
        this.body.put("CrewLiveAnnotationEmail", email);
    }

    public CrewLiveAnnotation(String messageId) {
        this.annotationId = messageId;
    }

    public void setCrewAnnotation(Attributes attr) {
        String name, qname;
        for (int i = 0; i < attr.getLength(); i++) {
            qname = attr.getQName(i);
            name = "set" + qname.substring(0, 1).toUpperCase()
                    + qname.substring(1);
            try {
                Method meth = getClass().getMethod(name, String.class);
                meth.invoke(this, new Object[] {attr.getValue(i)});
            } catch (Exception e) {
                System.err.println("no method: " + name);
                // e.printStackTrace();
            }
        }
    }

    public void setAnnotation(String annotation) {
        // do nothing
    }

    public void setCrewLiveAnnotation(String annBody) {
        // do nothing
    }

    public void setValueOf(String name, String value) {
        String methodName = "set" + name.substring(0, 1).toUpperCase()
                + name.substring(1);
        try {
            Method method = getClass().getMethod(methodName, String.class);
            method.invoke(this, new Object[] {value});
        } catch (NoSuchMethodException e) {
            body.put(name, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTimestamp(Attributes attr) {
        setDate(Long.valueOf(attr.getValue("value")));
    }

    public long getTimestamp() {
        return date.getTime();
    }

    public String getPrivacy() {
        return privacy;
    }

    public CrewLiveAnnotation(
            LiveAnnotationTypeRepository liveAnnotationTypeRepository,
            String xml) throws AnnotationException {
        this.liveAnnotationTypeRepository = liveAnnotationTypeRepository;
        try {
            CrewLiveAnnotationParser annp = new CrewLiveAnnotationParser(this);
            annp.parse(xml);
        } catch (SAXException e) {
            e.printStackTrace();
        }
        checkType();
    }

    public CrewLiveAnnotation(
            LiveAnnotationTypeRepository liveAnnotationTypeRepository,
            HashMap<String, String> parameters) throws AnnotationException {
        this.liveAnnotationTypeRepository = liveAnnotationTypeRepository;
        this.type = parameters.get("liveAnnotationType");
        this.body.put("liveAnnotationType", type);
        this.privacy = parameters.get("privacy");
        this.edits = parameters.get("edits");
        this.source = parameters.get("source");
        this.date = new Date();
        /*
         * if (annotationId.length()==0){
         * annotationId=date.getTime()+"_"+UUID.randomUUID().toString(); }
         */String timeStamp = parameters.get("timeStamp");
        if (timeStamp != null) {
            this.date = new Date(Long.valueOf(timeStamp));
        }
        Iterator<String> bodyitems = liveAnnotationTypeRepository.findLiveAnnotationType(
                type).getContainedFields().iterator();
        while (bodyitems.hasNext()) {
            String part = bodyitems.next();
            String value = parameters.get(part);
            if (value != null) {
                this.body.put(part, value);
            }
        }
        checkType();
        // System.out.println(formatAnnotation(liveAnnotationTypeRepository.
        // findLiveAnnotationType(type).getFormat("input")));

    }

    public String getThumbnail() {
        return liveAnnotationTypeRepository.findLiveAnnotationType(type).getThumbnail();
    }

    public CrewLiveAnnotation(
            LiveAnnotationTypeRepository liveAnnotationTypeRepository,
            Annotation annotation) throws AnnotationException {
        this.liveAnnotationTypeRepository = liveAnnotationTypeRepository;
        if (!annotation.getType().equals("LiveAnnotation")) {
            throw new AnnotationException("Annotation is not a CrewLiveAnnotation");
        }
        this.date = annotation.getCreated();
        this.annotator = annotation.getAuthor();
        this.body = annotation.getBody();
        this.type = body.get("liveAnnotationType");
        this.annotationId = annotation.getId();
        checkType();
    }

    public String getMessage(String messg) {
        int lastEnd, start, end;
        String newMessage = "";
        Pattern url = Pattern.compile("(^|[ \t\r\n])((ftp|http|https|"
                + "gopher|mailto|news|nntp|telnet|wais|file|prospero|"
                + "aim|webcal):(([A-Za-z0-9$_.+!*(),;/?:@&~=-])|%"
                + "[A-Fa-f0-9]{2}){2,}(#([a-zA-Z0-9][a-zA-Z0-9$_.+!"
                + "*(),;/?:@&~=%-]*))?([A-Za-z0-9$_+!*();/?:~-]))");
        Matcher matcher = url.matcher(messg);
        lastEnd = 0;
        while (matcher.find()) {
            start = matcher.start();
            if (start != 0) {
                start += 1;
            }
            end = matcher.end();
            newMessage += messg.substring(lastEnd, start);
            lastEnd = end;
            newMessage += "<a href='" + messg.substring(start, end) + "' target='_blank'>";
            newMessage += messg.substring(start, end);
            newMessage += "</a>";
        }
        newMessage += messg.substring(lastEnd);
        return newMessage;
    }

    public String getVariableValue(String variable) throws AnnotationException {
        String value = null;
        LiveAnnotationType laType = liveAnnotationTypeRepository.findLiveAnnotationType(type);
        if (laType == null) {
            throw new AnnotationException("unknown liveAnnotationType");
        }
        value = laType.getVariable(variable);
        if (value == null) {
            value = body.get(variable);
        }
        return value;
    }

    public String formatAnnotation(String format) {
        int lastEnd, start, end;
        String newMessage = "";
        Pattern variable = Pattern.compile("\\$\\{.*?\\}");
        Matcher matcher = variable.matcher(format);

        lastEnd = 0;
        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            newMessage += format.substring(lastEnd, start);
            lastEnd = end;
            String variableName = format.substring(start + 2, end - 1);
            try {
                newMessage += getVariableValue(variableName);
            } catch (AnnotationException e) {
                newMessage += "!!!! ${" + variableName + "} !!!!";
            }
        }
        newMessage += format.substring(lastEnd);
        return newMessage;
    }

    /*
     * public String toPlayer(){ String out=""; // out="<img src='" +
     * annotationTypes.get(type) + "' alt='"+type+"'/>&nbsp;"; if
     * (type.equals("link")){ out+="<a href='"+url.replace("\\,",
     * ",")+"' target='_blank'>"; if (name.length()>0){ out+=name.replace("\\,",
     * ",") + " ("+url.replace("\\,", ",")+")"; } else { out+=url.replace("\\,",
     * ","); } out+="</a> "; } if (type.equals("reference")){
     * out+="<em>"+name.replace("\\,", ",")+"</em>"; } if
     * (type.equals("person")){ String i=" (",o=""; out+=name.replace("\\,",
     * ","); if (email.length()>0){ out += i +
     * "<a href='mailto:"+email.replace("\\,",
     * ",")+"'><img src='/img/small/email.png'>&nbsp;"+email.replace("\\,",
     * ",")+"</a>"; i=", "; o=") "; } if (url.length()>0){ out += i +
     * "<a href='"+url.replace("\\,",
     * ",")+"'><img src='/img/small/link.png'>&nbsp;"+url.replace("\\,",
     * ",")+"</a>"; i=", "; o=") "; } out+=o; } out += message; return out; }
     *
     *
     * public String toString(){ String out=""; out="<img src='" +
     * annotationTypes.get(type) + "' alt='"+type+"'/>&nbsp;"; if
     * (type.equals("link")){ out+="<a href='"+url.replace("\\,",
     * ",")+"' target='_blank'>"; if (name.length()>0){ out+=name.replace("\\,",
     * ",") + " ("+url.replace("\\,", ",")+")"; } else { out+=url.replace("\\,",
     * ","); } out+="</a> "; } if (type.equals("reference")){
     * out+="<em>"+name.replace("\\,", ",")+"</em>"; } if
     * (type.equals("person")){ String i=" (",o=""; out+=name.replace("\\,",
     * ","); if (email.length()>0){ out += i +
     * "<a href='mailto:"+email.replace("\\,",
     * ",")+"'><img src='/img/small/email.png'>&nbsp;"+email.replace("\\,",
     * ",")+"</a>"; i=", "; o=") "; } if (url.length()>0){ out += i +
     * "<a href='"+url.replace("\\,",
     * ",")+"'><img src='/img/small/link.png'>&nbsp;"+url.replace("\\,",
     * ",")+"</a>"; i=", "; o=") "; } out+=o; } out += message; return out; }
     */
    public boolean isAnnotation() {
        return isAnnotation;
    }

    public String getAuthor() {
        return escapeRdf(source);
    }

    public String toOldXml() {
        String out = "<annotation>";
        out += "<annotationId>" + StringEscapeUtils.escapeXml(annotationId) + "</annotationId>";
        out += "<author>" + StringEscapeUtils.escapeXml(source) + "</author>";
        out += "<privacy>" + StringEscapeUtils.escapeXml(privacy) + "</privacy>";
        out += "<timestamp value=\"" + date.getTime() + "\">" + date.toString() + "</timestamp>";
        out += "<crewAnnotation type=\"" + type + "\" ";
        if (body.containsKey("CrewLiveAnnotationTitle")) {
            out += "name=\"" + StringEscapeUtils.escapeXml(
                    body.get("CrewLiveAnnotationTitle")) + "\" ";
        }
        if (body.containsKey("CrewLiveAnnotationUrl")) {
            out += "url=\"" + StringEscapeUtils.escapeXml(
                    body.get("CrewLiveAnnotationUrl")) + "\" ";
        }
        if (body.containsKey("CrewLiveAnnotationEmail")) {
            out += "email=\"" + StringEscapeUtils.escapeXml(
                    body.get("CrewLiveAnnotationEmail")) + "\" ";
        }
        out += "/>";
        out += "<text>" + StringEscapeUtils.escapeXml(
                body.get("CrewLiveAnnotationComment")) + "</text>";
        out += "</annotation>";
        return out;
    }

    public String toXml() {
        String out = "<annotation>";
        out += "<annotationId>" + StringEscapeUtils.escapeXml(annotationId)
                + "</annotationId>";
        if (edits != "") {
            out += "<edits>" + edits + "</edits>";
        }
        out += "<author>" + StringEscapeUtils.escapeXml(source) + "</author>";
        out += "<privacy>" + StringEscapeUtils.escapeXml(privacy)
                + "</privacy>";
        out += "<timestamp value=\"" + date.getTime() + "\">" + date.toString()
                + "</timestamp>";
        out += "<crewLiveAnnotation>";
        Iterator<String> bodyKeys = body.keySet().iterator();
        while (bodyKeys.hasNext()) {
            String key = bodyKeys.next();
            out += "<" + key + ">" + StringEscapeUtils.escapeXml(body.get(key))
                    + "</" + key + ">";
        }
        out += "</crewLiveAnnotation>";
        out += "</annotation>";
        return out;
    }

    public Annotation toAnnotation() {
        Annotation annotation = new Annotation();
        annotation.setType("LiveAnnotation");
        annotation.setBody(getAnnotationBody());
        annotation.setCreated(date);
        return annotation;
    }

    public Date getDate() {
        return date;
    }

    private String escapeRdf(String in) {
        if (in == null) {
            return " ";
        }
        return StringEscapeUtils.escapeXml(in);
    }

    /*
     * public void getProperties(Resource annotation, Model model) { String
     * ann_type=null; Property label =
     * model.createProperty(Event.ANNOTATION_LABEL); Property proptype =
     * model.createProperty(DatabaseUtils.RDF_TYPE); Property propurl =
     * model.createProperty(Event.ANNOTATION_URL); Property propname =
     * model.createProperty(Event.ANNOTATION_NAME); Property propemail =
     * model.createProperty(Event.ANNOTATION_EMAIL); Property proplivetype =
     * model.createProperty(Event.ANNOTATION_LIVE_TYPE);
     *
     * if (type.equals("question")){ ann_type=Event.ANNOTATION_TYPE_QUESTION; }
     * else if (type.equals("answer")){ ann_type=Event.ANNOTATION_TYPE_ANSWER; }
     * else if (type.equals("comment")){ ann_type=Event.ANNOTATION_TYPE_COMMENT;
     * } else if (type.equals("link")){
     * ann_type=Event.ANNOTATION_TYPE_LINKED_RESOURCE;
     * annotation.addProperty(propname, escapeRdf(name));
     * annotation.addProperty(propurl, escapeRdf(url)); } else if
     * (type.equals("reference")){ ann_type=Event.ANNOTATION_TYPE_REFERENCE;
     * annotation.addProperty(propurl,escapeRdf(url)); } else if
     * (type.equals("important")){ ann_type=Event.ANNOTATION_TYPE_IMPORTANT; }
     * else if (type.equals("slide")){
     * ann_type=Event.ANNOTATION_TYPE_SLIDE_TITLE; } else if
     * (type.equals("person")){ ann_type=Event.ANNOTATION_TYPE_PERSON; if
     * (name.length()>0) annotation.addProperty(propname, escapeRdf(name)); if
     * (url.length()>0) annotation.addProperty(propurl, escapeRdf(url)); if
     * (email.length()>0) annotation.addProperty(propemail, escapeRdf(email)); }
     * annotation.addProperty(proplivetype, type);
     * annotation.addProperty(proptype, ann_type); annotation.addProperty(label,
     * escapeRdf(message)); }
     */

    public String getListText() throws AnnotationException {
        return type + ": " +
            formatAnnotation(liveAnnotationTypeRepository.findLiveAnnotationType(
                checkType()).getFormat("output"));
    }

    public String getTimeSliderText() throws AnnotationException {
        return formatAnnotation(liveAnnotationTypeRepository.findLiveAnnotationType(
                checkType()).getFormat("player"));
    }

    public String getToolText() throws AnnotationException {
        return formatAnnotation(liveAnnotationTypeRepository.findLiveAnnotationType(
                checkType()).getFormat("input"));
    }

    public Map<String, String> getAnnotationBody() {
        return body;
    }

    public String getIdent() {
        return annotator;
    }

    public String getAnnotationId() {
        return annotationId;
    }

    public boolean equals(Object other) {
        return annotationId.equals(((CrewLiveAnnotation) other).getAnnotationId());
    }

    public int hashCode() {
        return annotationId.hashCode();
    }

}
