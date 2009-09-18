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

package net.crew_vre.annotations.liveannotationtype;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.crew_vre.domain.DomainObject;

public class LiveAnnotationType extends DomainObject {
    private String button = null;
    private String buttonVisible = "false";
    private String thumbnail = null;
    private String colour = null;
    private HashMap<String, String> textContent = new HashMap<String, String>();
    private List<String> containedFields = new Vector<String>();
    private HashMap<String, String> variables = new HashMap<String, String>();

    private HashMap<String, String> formats = new HashMap<String, String>();
    private List<String> conversions = new Vector<String>();
    private String name;
    private String type = null;
    private long index = -1;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public long getIndex() {
        return index;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setButton(String button, String buttonVisible) {
        this.button = button;
        this.buttonVisible = buttonVisible;
    }

    public String getButton() {
        return button;
    }

    public String getButtonVisible() {
        return buttonVisible;
    }

    public String getButton(String show) {
        if (buttonVisible.equals(show)) {
            return button;
        }
        return null;
    }

    public void setContains(String name, String type) {
        textContent.put(name, type);
        containedFields.add(name);
    }

    public HashMap<String, String> getContent() {
        return textContent;
    }

    public String getContentJS() {
        String out = "{";
        String delim = "";
        Iterator<String> keys = textContent.keySet().iterator();
        while (keys.hasNext()) {
            out += delim;
            String key = keys.next();
            out += "'" + key + "':'" + textContent.get(key) + "'";
            delim = ",";
        }
        out += "}";
        return out;
    }

    public List<String> getContainedFields() {
        return containedFields;
    }

    public String getContainedFieldsJS() {
        String out = "new Array(";
        String delim = "";
        for (int i = 0; i < containedFields.size(); i++) {
            out += delim;
            out += "'" + containedFields.get(i) + "'";
            delim = ",";
        }
        out += ")";
        return out;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setVariable(String name, String value) {
        variables.put(name, value);
    }

    public String getVariable(String name) {
        return variables.get(name);
    }

    public HashMap<String, String> getVariables() {
        return variables;
    }

    public String getVariablesJS() {
        String out = "{";
        String delim = "";
        Iterator<String> keys = variables.keySet().iterator();
        while (keys.hasNext()) {
            out += delim;
            String key = keys.next();
            out += "'" + key + "':'" + variables.get(key) + "'";
            delim = ",";
        }
        out += "}";
        return out;
    }

    public void setFormat(String name, String value) {
        formats.put(name, value);
    }

    public HashMap<String, String> getFormats() {
        return formats;
    }

    public String getFormat(String name) {
        return formats.get(name);
    }

    public void setThumbnail(String image) {
        this.thumbnail = image;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getColour() {
        return colour;
    }

    public void addConversion(String type) {
        conversions.add(type);
    }

    public List<String> getConversions() {
        return conversions;
    }
}
