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
 
import org.crew.CrewPlayer;
import org.crew.Utils;

/**
 * A Slider that moves as the time update
 */
class org.crew.TimeSlider {

    // The id of the interval used to update this time line
    private var intervalId:Number = -1;

    // The time line bars
    private var bars:Object;

    // The number of bars
    private var noBars:Number;

    // The actual slider object
    private var slider:MovieClip;

    // The field that contains the current time
    private var timeText:TextField;

    // The parent player containing the control functions
    private var parent:CrewPlayer;

    // The depth of the slider in the flash order
    private var depth:Number;

    // The x position of the slider
    private var x:Number;

    // The y position of the slider
    private var y:Number;

    // The width of the bar
    private var width:Number;

    // The height of the section
    private var height:Number;

    // The height of each bar
    private var barheight:Number;

    // The width of the slider
    private var sliderWidth:Number;

    // The background colour of the bar
    private var backgroundColour:Number;

    // The border colour of the bar
    private var borderColour:Number;

    // The colour of the slider tab
    private var sliderColour:Number;

    // The colour of the text on the time panel
    private var textColour:Number;

    // True if the slider is sliding
    private var isSliding:Boolean = false;

    // The annotations to be displayed
    private var annotations:Array;

    // The types of the annotations available
    private var annotationTypes:Array;

    public function TimeSlider(parent:CrewPlayer,
            depth:Number, x:Number, y:Number, width:Number, height:Number,
            sliderWidth:Number, backgroundColour:Number,
            borderColour:Number, sliderColour:Number, textColour:Number,
            annotations:Array, annotationTypes:Array) {

        MovieClip.prototype.toolTip = function(thumb:Array) {
            var timer;
            this.onRollOver = function() {
                var showTip = function() {
                    clearInterval(timer);
                    _root.createTextField("toolTip", 65530, _root._xmouse,
                         _root._ymouse + 20, 10, 10);
                    _root.toolTip.border = true;
                    _root.toolTip.multiline = true;
                    _root.toolTip.background = true;
                    _root.toolTip.backgroundColor = 0xFFFFC9;
                    var mouse_x:Number = _root._xmouse+Stage.width/2;
                    var mouse_y:Number = _root._ymouse+Stage.height/2;
                    var myformat:TextFormat = new TextFormat();
                    var textFormat:TextFormat = new TextFormat();
                    textFormat.font = "_serif";
                    textFormat.size = 12;
                    textFormat.color = 0x000000;
                    myformat.color=0x000000;
                    myformat.font = "_serif";
                    myformat.size = 12;
                    var tpos:Number=1;
                    var layer:Number=65531;
                    var ttheight:Number=2;
                    var ttwidth:Number=2;

//	                    parent.logger.debug("t: "+ thumb["text"]);
                        if (thumb["text"]){
//	                    	parent.logger.debug("tt:"+ thumb["text"]);
                            _root.createTextField("tooltiptext",layer,(_root._xmouse+2),(_root._ymouse+20+tpos),10,10);
                            textFormat.bold=false;
                            textFormat.underline=false;
                            textFormat.font = "_serif";
                            textFormat.size = 12;
                            textFormat.color = 0x000000;
                            _root.tooltiptext.setNewTextFormat(textFormat);
                            _root.tooltiptext.autoSize = true;
                            _root.tooltiptext.html = true;
                            _root.tooltiptext.multiline = true;
                            _root.tooltiptext.htmlText=thumb["text"];
                            _root.tooltiptext._height = _root.tooltiptext.textHeight;
                            ttheight=tpos+_root.tooltiptext._height;
                            tpos+=_root.tooltiptext._height + 2;
                            _root.toolTip._width=Math.max(_root.toolTip._width,(_root.tooltiptext._width+4));
                            layer++;
                        }
                        _root.toolTip._height=ttheight;
                        ttwidth=_root.toolTip._width;
                        parent.logger.debug("rtw: "+ ttwidth + " xpos: "+mouse_x+" sw: "+Stage.width+" texth: " +_root.toolTip._height );
                        var tyoffset:Number=0;
                        var oth:Number=0;
                        var xoffset:Number=0;
                        var theight:Number=0;
                        var xused:Number=0;
                           var yoffset:Number=0;
                        if ((ttwidth+mouse_x)>Stage.width)
                        {
                            Utils.change_TooltipWidth (_root,(Stage.width-mouse_x-5));
                            xoffset=0;
                            theight=0;
                            xused=0;
                               yoffset=0;
                            while ((_root.toolTip._height+mouse_y)>(Stage.height-5)){
                                xoffset+=10;
                                Utils.change_TooltipWidth (_root,(Stage.width-mouse_x-5+xoffset));
                                if (_root.toolTip._width >  (Stage.width-10))
                                    break;
                                if (theight!=_root.toolTip._height){
                                    theight=_root.toolTip._height;
                                    xused=xoffset;
                                }
                            }
                            Utils.change_TooltipWidth (_root,(Stage.width-mouse_x-5+xused));
                            Utils.change_TooltipPos (_root,(_root.toolTip._x-xused),_root.toolTip._y);
                        }

                        parent.logger.debug("mouse_y: " + mouse_y + " h: " +_root.toolTip._height + " sh: " + Stage.height);
                        var tpos_y:Number =_root.toolTip._y + Stage.height/2;
                          if ((mouse_y +_root.toolTip._height)>(Stage.height-25)){
                            yoffset = Stage.height - tpos_y - _root.toolTip._height - 25 ;
                            parent.logger.debug("mouse_y: " + mouse_y + " rty: " +_root.toolTip._y + " yoff: " + yoffset);
                            Utils.change_TooltipPos (_root,_root.toolTip._x,(_root.toolTip._y -_root.toolTip._height -30));
                            parent.logger.debug("xpos: "+_root.toolTip._x+" ypos: "+_root.toolTip._y );
                       }
                        }
                timer = setInterval(showTip, 500);
            };
            this.onRollOut = function() {
                _root.toolTip.removeTextField();
                _root.tooltiptext.removeTextField();
                clearInterval(timer);
            };
        };

        MovieClip.prototype.annotation = function(start:Number) {

            this.onRelease = function() {
                parent.seek(start);
            }
        }

        this.noBars = annotationTypes.length;
        if (this.noBars == 0) {
            this.noBars = 1;
        }
        this.parent = parent;
        this.depth = depth;
        this.barheight = (height / this.noBars) - 5;
        this.x = x + barheight + 5;
        this.y = y;
        this.width = width - 100 - barheight - 5;
        this.height = ((barheight + 5) * noBars) - 5;
        this.sliderWidth = sliderWidth;
        this.backgroundColour = backgroundColour;
        this.borderColour = borderColour;
        this.sliderColour = sliderColour;
        this.textColour = textColour;
        this.annotations = annotations;
        this.annotationTypes = annotationTypes;

        this.onStartSliderDrag.timeSlider = this;
        this.onStopSliderDrag.timeSlider = this;
        this.onSliderMove.timeSlider = this;
        this.onBarClick.timeSlider = this;

        this.bars = new Object();
        for (var i:Number = 0; i < noBars; i++) {
            var type:String = "";
            var atype = new Object();
            if (i < annotationTypes.length) {
                atype = annotationTypes[i];
                type=atype["type"]
            }
            parent.logger.debug("bar type = " + type);
            bars[type] = parent.createEmptyMovieClip("bar" + type,
                depth + i + 1);
            bars[type].onRelease = this.onBarClick;
        }

        slider = parent.createEmptyMovieClip("slider", depth + 65533);
        slider.onPress = this.onStartSliderDrag;
        slider.onRelease = this.onStopSliderDrag;
        slider.onReleaseOutside = this.onStopSliderDrag;
        slider.onMouseMove = this.onSliderMove;
    }

    public function drawAnnotations() {
        for (var i:Number = 0; i < annotations.length; i++) {
            var ann=annotations[i];
            var startX = getPos(ann["start"]) - this.x;
            var endX = getPos(ann["end"]) - this.x;

            var colour = ann["colour"];
//            parent.logger.debug("Colour: " +ann["colour"]);
            var type = ann["nodeType"];
            var clip:MovieClip = parent.createEmptyMovieClip(
                "annotation" + i, depth + 1000 + i);
            clip._x = startX + bars[type]._x;
            clip._y = 1 + bars[type]._y;
            clip.lineStyle(0, colour, 100);
            clip.beginFill(colour);
            clip.moveTo(0, 0);
            clip.lineTo(endX - startX, 0);
            clip.lineTo(endX - startX, barheight - 2);
            clip.lineTo(0, barheight - 2);
            clip.lineTo(0, 0);
            clip.endFill();
            if (annotations[i]["text"]!=""){
                clip.toolTip(annotations[i]);
            }
            clip.annotation(ann["start"]);
        }
    }

    public function draw() {
        var loader:MovieClipLoader = new MovieClipLoader();
        loader.addListener(this);
        var bary:Number = y;
        for (var i:Number = 0; i < noBars; i++) {
            var type:String = "";
             var atype= new Array();
            if (i < annotationTypes.length) {
                atype = annotationTypes[i]
                type=atype["type"];
            }
            bars[type]._x = x;
            bars[type]._y = bary;
            bars[type].lineStyle(1, borderColour, 100, false, "normal", "round",
                "round", 3);
            bars[type].beginFill(backgroundColour);
            bars[type].moveTo(0, 0);
            bars[type].lineTo(width, 0);
            bars[type].lineTo(width, barheight);
            bars[type].lineTo(0, barheight);
            bars[type].endFill();

            var icon = parent.createEmptyMovieClip("icon" + type, depth +
                noBars + i + 100);
            icon.toolTip(atype["text"]);
            icon._x = x - barheight - 5;
            icon._y = bary;
            loader.loadClip(atype["icon"], icon);

            bary += barheight + 5;
        }

        slider._alpha = 50;
        slider.beginFill(sliderColour);
        slider.lineStyle(1, borderColour, 100, false, "normal", "round",
            "round", 3);
        slider.moveTo(0, 0);
        slider.lineTo(sliderWidth, 0);
         parent.logger.debug("Slider height " + height);
        slider.lineTo(sliderWidth, height + 12);
        slider.lineTo(0, height + 12);
        slider.endFill();
        slider._y = y - 6;
        slider._x = x - (sliderWidth / 2);

        var textFormat:TextFormat = new TextFormat();
        textFormat.font = "_sans";
        textFormat.size = 10;
        textFormat.color = textColour;
        timeText = parent.createTextField("timeText", depth + noBars + 1,
            x + width + 2, y, 100, barheight);
        timeText.setNewTextFormat(textFormat);
        timeText.background = false;
        timeText.text = "Unknown Duration";
    }

    public function onLoadInit(clip:MovieClip) {
        parent.logger.debug("Loaded image " + clip._url);
        clip._width = barheight;
        clip._height = barheight;
        for (var i:Number = 0; i < annotationTypes.length; i++) {
            if (annotationTypes[i]["icon"] == clip._url) {
                clip.toolTip(annotationTypes[i]["text"]);
            }
        }
    }

    public function onLoadError(clip:MovieClip, error:String) {
        parent.logger.debug("Error loading " + clip._url + ": " + error);
    }

    public function getPos(time:Number) {
        var x:Number = this.x;
        if (parent.getDuration() > 0) {
            var unitWidth:Number = width / parent.getDuration();
            x = this.x + (unitWidth * time);
        }
        return x;
    }

    public function getTime(x:Number) {
        var time:Number = 0;
        if (parent.getDuration() > 0) {
            var unitWidth:Number = width / parent.getDuration();
            time = (x - this.x) / unitWidth;
        }
        return time;
    }

    public function setSliderTime(time:Number):Void {
        slider._x = getPos(time) - (sliderWidth / 2);
        setTimeText(time);
    }

    public function getSliderTime():Number {
        var x:Number = slider._x + (sliderWidth / 2);
        return getTime(x);
    }

    private function formatTimeDigit(digit:Number):String {
        var str:String = "";
        if (digit < 10) {
            str += "0";
        }
        str += String(digit);
        return str;
    }

    public function formatTime(time:Number):String {
        var hours:Number = Math.floor(time / 3600);
        time = time - (hours * 3600);
        var minutes:Number = Math.floor(time / 60);
        time = time - (minutes * 60);
        var seconds:Number = Math.floor(time);

        var text:String = "";
        text += formatTimeDigit(hours) + ":";
        text += formatTimeDigit(minutes) + ":";
        text += formatTimeDigit(seconds);
        return text;
    }

    public function setTimeText(time:Number):Void {
        var dur = parent.getDuration();
        timeText.text = formatTime(time) + " / " + formatTime(dur);
    }

    public function start():Void {
        if (intervalId == -1) {
            intervalId = setInterval(this, "update", 250);
        }
    }

    public function stop():Void {
        if (intervalId != -1) {
            clearInterval(intervalId);
            intervalId = -1;
        }
    }

    public function update():Void {
        if (parent.getDuration() > 0) {
            setSliderTime(parent.getTime());
        }
    }

    public function onStartSliderDrag():Void {
        var timeSlider:TimeSlider = arguments.callee.timeSlider;
        timeSlider.stop();
        timeSlider.parent.pause();
        timeSlider.isSliding = true;
    }

    public function onStopSliderDrag():Void {
        var timeSlider:TimeSlider = arguments.callee.timeSlider;
        timeSlider.isSliding = false;
        timeSlider.parent.seek(timeSlider.getSliderTime());
        timeSlider.start(timeSlider.parent.getDuration());
    }

    public function onSliderMove():Void {
        var timeSlider:TimeSlider = arguments.callee.timeSlider;
        if (timeSlider.isSliding && timeSlider.parent.getDuration() > 0) {
            var mouseX:Number = timeSlider.slider._parent._xmouse;
            var min:Number = timeSlider.x - (timeSlider.sliderWidth / 2);
            var max:Number = timeSlider.x + timeSlider.width
                - (timeSlider.sliderWidth / 2);
            if (mouseX < min) {
                mouseX = min;
            } else if (mouseX > max) {
                mouseX = max;
            }
            timeSlider.slider._x = mouseX;
            timeSlider.setTimeText(timeSlider.getSliderTime());
        }
    }

    public function onBarClick():Void {
        var timeSlider:TimeSlider = arguments.callee.timeSlider;
        if (!timeSlider.isSliding) {
            timeSlider.parent.seek(timeSlider.getTime(
                timeSlider.parent._xmouse));
        }
    }
}
