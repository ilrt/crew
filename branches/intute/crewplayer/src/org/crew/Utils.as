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


class org.crew.Utils {

    public static function getHighlight(colour:Number,
            highlight:Number):Number {
        if (((colour & 0xFF) + (highlight & 0xFF)) > 0xFF) {
            highlight &= 0xFFFF00;
        }
        if (((colour & 0xFF00) + (highlight & 0xFF00)) > 0xFF00) {
            highlight &= 0xFF00FF;
        }
        if (((colour & 0xFF0000) + (highlight & 0xFF0000)) > 0xFF0000) {
            highlight &= 0x00FFFF;
        }
        return highlight;
    }

    public static function getLowlight(colour:Number, lowlight:Number):Number {
        if (((colour & 0xFF) - (lowlight & 0xFF)) < 0) {
            lowlight &= 0xFFFF00;
        }
        if (((colour & 0xFF00) - (lowlight & 0xFF00)) < 0) {
            lowlight &= 0xFF00FF;
        }
        if (((colour & 0xFF0000) - (lowlight & 0xFF0000)) < 0) {
            lowlight &= 0x00FFFF;
        }
        return lowlight;
    }
    
    public static function change_TooltipWidth (parent:Object, ttwidth:Number) : Number{
    	var oth:Number;
    	var tyoffset:Number=0;
    	parent.toolTip._width = ttwidth;
    	if (parent.tooltipname){
	    	oth=parent.tooltipname._height;
			parent.tooltipname.wordWrap = true;
		    parent.tooltipname._width = ttwidth-4;
		    tyoffset+=parent.tooltipname._height-oth;
    	}
    	if (parent.tooltipurl){
			parent.tooltipurl._y+=tyoffset;
		    oth=parent.tooltipurl._height;
		    parent.tooltipurl.wordWrap = true;
		    parent.tooltipurl._width = ttwidth-4;
		    tyoffset+=parent.tooltipurl._height-oth;
    	}
    	if (parent.tooltipemail){
			parent.tooltipemail._y+=tyoffset;
		    oth=parent.tooltipemail._height;
		    parent.tooltipemail.wordWrap = true;
		    parent.tooltipemail._width = ttwidth-4;
		    tyoffset+=parent.tooltipemail._height-oth;
    	}
    	if (parent.tooltiptext){
		    parent.tooltiptext._y+=tyoffset;
		    oth=parent.tooltiptext._height;
		    parent.tooltiptext.wordWrap = true;
		    parent.tooltiptext._width = ttwidth-4;
		    tyoffset+=parent.tooltiptext._height-oth;
    	}
    	parent.toolTip._height+=tyoffset;
    	return tyoffset;
    }

    public static function change_TooltipPos (parent:Object, xpos:Number, ypos:Number){
    	var xoff:Number;
    	var yoff:Number;
    	xoff = xpos - parent.toolTip._x;
    	yoff = ypos - parent.toolTip._y;
    	parent.toolTip._x += xoff;
    	parent.toolTip._y += yoff;
    	if (parent.tooltipname){
			parent.tooltipname._x += xoff;
		    parent.tooltipname._y += yoff;
    	}
		if (parent.tooltipurl){
		    parent.tooltipurl._x += xoff;
		    parent.tooltipurl._y += yoff;
    	}
	    if (parent.tooltipemail){
		    parent.tooltipemail._x +=xoff;
		    parent.tooltipemail._y +=yoff;
    	}
    	if (parent.tooltiptext){
		    parent.tooltiptext._x += xoff;
		    parent.tooltiptext._y += yoff;
    	}
    }
    
    public static function drawButton(button:MovieClip, buttonColour:Number,
            x:Number, y:Number, width:Number, height:Number, raised:Boolean,
            background:Boolean) {
        var yscale = button._yscale / 100;
        var highlight:Number = 0x555555;
        var lowlight:Number = 0x555555;
        if (!raised) {
            highlight = -highlight;
            highlight = getLowlight(buttonColour, highlight);
            lowlight = -lowlight;
            lowlight = getHighlight(buttonColour, lowlight);
        } else {
            highlight = getHighlight(buttonColour, highlight);
            lowlight = getLowlight(buttonColour, lowlight);
        }

        button._x = x;
        button._y = y;
        if (background) {
            button.beginFill(buttonColour);
        }
        button.moveTo(0, 0);
        button.lineStyle(1, buttonColour + highlight, 100, false, "normal",
            "round", "round", 3);
        button.lineTo(width, 0);
        button.lineStyle(1, buttonColour - lowlight, 100, false, "normal",
            "round", "round", 3);
        button.lineTo(width, height / yscale);
        button.lineStyle(1, buttonColour - lowlight, 100, false, "normal",
            "round", "round", 3);
        button.lineTo(0, height / yscale);
        button.lineStyle(1, buttonColour + highlight, 100, false, "normal",
            "round", "round", 3);
        button.lineTo(0, 0);
        if (background) {
            button.endFill();
        }
    }
}
