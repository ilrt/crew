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

package net.crew_vre.liveAnnotations.tags;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;

import net.crew_vre.annotations.liveannotationtype.LiveAnnotationType;
import net.crew_vre.annotations.liveannotationtype.LiveAnnotationTypeRepository;

public class LiveAnnotationClientLayoutTag extends CrewTag {

    // The name of the variable holding the type of the liveAnnotation
    private String liveAnnotationTypeVar = null;

    // The name of the variable holding the button image
    private String buttonVar = null;

    // The name of the variable holding the visibility of the button
    private String buttonVisibleVar = null;

    // The name of the variable holding the thumbnail image
    private String thumbnailVar = null;

    // The name of the variable holding the colour value
    private String colourVar = null;

    // The name of the variable holding the content Values
    private String contentVar = null;

    // The name of the variable holding the list of content fields
    private String containedFieldsVar = null;

    // The name of the variable holding the content variables
    private String variablesVar = null;

    public void setButtonVar(String buttonVar) {
        this.buttonVar = buttonVar;
    }

    public void setButtonVisibleVar(String buttonVisibleVar) {
        this.buttonVisibleVar = buttonVisibleVar;
    }

    public void setLiveAnnotationTypeVar(String liveAnnotationTypeVar) {
        this.liveAnnotationTypeVar = liveAnnotationTypeVar;
    }

    public void doTag() throws JspException, IOException {
        LiveAnnotationTypeRepository liveAnnotationTypeRepository = getServer()
                .getLiveAnnotationTypeRepository();
        List<String> liveAnnotationTypeNames =
            liveAnnotationTypeRepository.getLiveAnnotationTypes();
        Iterator<String> iter = liveAnnotationTypeNames.iterator();
        while (iter.hasNext()) {
            LiveAnnotationType liveAnnotationType =
                liveAnnotationTypeRepository.findLiveAnnotationType(iter.next());
            setValue(liveAnnotationTypeVar, liveAnnotationType.getType());
            setValue(buttonVar, liveAnnotationType.getButton());
            setValue(buttonVisibleVar, liveAnnotationType.getButtonVisible());
            setValue(thumbnailVar, liveAnnotationType.getThumbnail());
            setValue(colourVar, liveAnnotationType.getColour());
            setValue(contentVar, liveAnnotationType.getContentJS());
            setValue(variablesVar, liveAnnotationType.getVariablesJS());
            setValue(containedFieldsVar, liveAnnotationType.getContainedFieldsJS());
            generate();
        }
    }

    public void setThumbnailVar(String thumbnailVar) {
        this.thumbnailVar = thumbnailVar;
    }

    public void setColourVar(String colourVar) {
        this.colourVar = colourVar;
    }

    public void setContentVar(String contentVar) {
        this.contentVar = contentVar;
    }

    public void setContainedFieldsVar(String containedFieldsVar) {
        this.containedFieldsVar = containedFieldsVar;
    }

    public void setVariablesVar(String variablesVar) {
        this.variablesVar = variablesVar;
    }

}
