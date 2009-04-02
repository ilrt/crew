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
  
import flash.external.ExternalInterface;

import LuminicBox.Log.ConsolePublisher;
import LuminicBox.Log.Logger;

import org.crew.Controls;
import org.crew.TimeSlider;
import org.crew.ThumbnailSlider;
import org.crew.VideoStream;

/**
 * The main CREW player
 */
class org.crew.CrewPlayer extends MovieClip {

    // The log subsystem
    public var logger:Logger = null;

    // The NetConnection object
    private var netConnection:NetConnection = null;

    // The overall duration as reported by the metadata
    private var duration:Number = 0;
    
    // The vector of replay-layouts to be used
    private var layouts:Array = null;
    
    // The vector of annotation types used by the recording
    private var annTypes:Array = null;
    
    // The annotations of the recording 
    private var annotations:Array = null;

    // The url of the session
    private var url:String = "";

	// the video-stream ids this vector contains all streams the 
	// function of each stream is defined by the replay-layout 
	private var videoStreams:Array=null;

    // The audio stream ids
    private var audioStreams:Array = null;

    // The Sound object
    private var sound:Sound = null;

    // The background of the player
    private var backgroundImage:MovieClip = null;

    // The image when buffering
    private var bufferingImage:MovieClip = null;

    // The buffering text field
    private var bufferText:TextField = null;

    // The controls of the player
    private var controls:Controls = null;

    // The slider of the time line
    private var timeSlider:TimeSlider = null;

    // The thumbnail slider
    private var thumbnailSlider:ThumbnailSlider = null;

    // True if the movie is playing
    private var playing:Boolean = false;

    // True if the movie is paused
    private var paused:Boolean = false;

    // True if the flash has loaded
    private var loaded:Boolean = false;

    // The click-to-play clip displayed over the video
    private var clickToPlay:MovieClip;

    // True if the video or screen is buffering
    private var buffering:Boolean = false;

    // The time at which playback was started
    private var startTime:Number = 0;

    public function CrewPlayer() {
        this.layouts = new Array();
        logger = new Logger();
        
        logger.addPublisher(new ConsolePublisher());

        Stage.scaleMode = "noScale";
        logger.debug(Stage.width + ", " + Stage.height);

        netConnection = new NetConnection();
        netConnection.connect(null);

        logger.debug("Attempting to read " + _root.uri);
        var initNetStream:NetStream = new NetStream(netConnection);
        initNetStream.onMetaData = this.onInitMetaDataCallback;
        initNetStream.onStatus =
            function(status:Object):Void {
                var logger:Logger = new Logger();
                logger.addPublisher(new ConsolePublisher());
                logger.debug(status["code"]);
            };
        this.onInitMetaDataCallback.player = this;
        logger.debug("About to play");
        initNetStream.play(_root.uri);
    }

    /**
     * Called when the clip is loaded
     */
    public function onEnterFrame():Void {
        if (loaded || (Stage.width <= 0) || (Stage.height <= 0)) {
            return;
        }
        loaded = true;
        delete this.onEnterFrame;
    }

    public function play():Void {
		seek(0);
    }

    public function seek(time:Number):Void {
        clickToPlay._visible = false;
        logger.debug("Seek " + time );
        if (timeSlider !=null){
        	timeSlider.start();
        }
        if (thumbnailSlider !=null){
        	thumbnailSlider.start();
        }
        buffering = true;
        playing = true;
        paused = false;
        startTime = time;
        if (controls !=null) {
        	controls.drawPause();
        }
		for (var i=0; i<videoStreams.length; i++){
			videoStreams[i].seek(time);
		}
    }

    public function pause():Void {
        if (playing && !paused) {
            paused = true;
        }
    }

    public function resume():Void {
        if (playing && paused) {
            paused = false;
        } else if (!playing) {
            play();
        }
    }

    public function isPlaying():Boolean {
        return playing && !paused;
    }

    public function getTime():Number {
    	var time=0;
		for (var i=0; i<videoStreams.length; i++){
			time=Math.max(time,videoStreams[i].getTime());
		}
        return time;
    }


    public function getDuration():Number {
        return this.duration;
    }

    public function setVolume(volume:Number):Void {
        sound.setVolume(volume);
    }

    public function onInitMetaData(data:Object):Void {
    	logger.debug(data);
    	this.url = data["url"];
    	this.startTime = data["startTime"];
    	logger.debug("url:"+this.url);
        this.duration = data["duration"];
        this.layouts = data["layouts"]; 
        this.annTypes = data["annotationTypes"];
        this.annotations = data["annotations"];
        this.audioStreams = new Array();
        this.videoStreams = new Array();
        var ctpdims:Object={area:0,x:0,y:0,width:0,height:0};
        
        backgroundImage = createEmptyMovieClip("background", 0);
        backgroundImage.beginFill(0xcecccc);
        backgroundImage.moveTo(0, 0);
        backgroundImage.lineTo(Stage.width + 15, 0);
        backgroundImage.lineTo(Stage.width + 15, Stage.height + 15);
        backgroundImage.lineTo(0, Stage.height + 15);
        backgroundImage.endFill();
        
        netConnection = new NetConnection();
        netConnection.connect(null);
        
        
        // get first layout (deal with changes of layouts later)
        var layout=layouts[0];
        var videoDepth=100;
        var layoutpos=layout["layoutPositions"];
        logger.debug("layoutpos:"+layoutpos);
        for (var l=0;l<layoutpos.length;l++){
        	var lp=layoutpos[l];
        	logger.debug("lp["+l+"]:"+lp["name"]);
        	
        	if (lp["type"] == "audio")
        	{
        		this.audioStreams.push(lp["stream"]);
        	}
        	if (lp["type"] == "Annotation"){
        		thumbnailSlider = new ThumbnailSlider(this,
		            Number(lp["x"]), Number(lp["y"]),Number(lp["width"]), Number(lp["height"]),
		            1000, data["thumbnails"], data["annotations"],
		            0x9a9393, 0x555555, 0x4b4a4a, 0x563E3E, 0xFFFFFF);        
		        thumbnailSlider.draw();
        	}
        	if (lp["type"] == "Slider"){ 
        		var height= annTypes.length;
        		if (height==0) {
        			height=1;
        		}
        		logger.debug("Slider height: "+ height*25);
	            timeSlider = new TimeSlider(this, 2000,
		            Number(lp["x"]), Number(lp["y"]),Number(lp["width"]), Number(height*25), 10,
		            0x9a9393, 0x555555, 0x4b4a4a, 0x000000, annotations, annTypes); 
		        timeSlider.draw();
		        timeSlider.setSliderTime(startTime);
        		timeSlider.drawAnnotations();
        	}
        	if (lp["type"] == "Controls"){
        		controls = new Controls(this, 30,
        		    Number(lp["x"]), Number(lp["y"]),Number(lp["width"]), Number(lp["height"]),
		            0x9a9393, 0x555555, 0xFFFFFF, 0x4b4a4a);
		        controls.draw();
        	}
        	if (lp["type"] == "video"){
        		var video=new VideoStream(this,lp["name"],
        		Number(lp["x"]), Number(lp["y"]),Number(lp["width"]), Number(lp["height"]),videoDepth,
        		lp["stream"],this.backgroundImage,netConnection);
        		videoDepth++;
        		var dims=video.getDims();
        		if (dims.area>ctpdims.area) {
        			ctpdims=dims;
        		}
        		this.videoStreams.push(video);
        	}
        }
        for (var i=0; i<videoStreams.length;i++){
        	videoStreams[i].setUrl(this.url, this.audioStreams, this.videoStreams);
        	sound=videoStreams[i].getSound();
        }
        
        ExternalInterface.addCallback("seek", this, this.seek);
        ExternalInterface.addCallback("pause", this, this.pause);
        ExternalInterface.addCallback("resume", this, this.resume);
        ExternalInterface.addCallback("stop", this, this.stop);

        _x = -(Stage.width / 2);
        _y = -(Stage.height / 2);

        clickToPlay = createEmptyMovieClip("ClickToPlay", 500);

        bufferingImage = createEmptyMovieClip("bufferingImage", 65535);
        bufferingImage._visible = false;
        bufferingImage._alpha = 10;
        bufferingImage.beginFill(0x000080);
        bufferingImage.moveTo(0, 0);
        bufferingImage.lineTo(Stage.width + 15, 0);
        bufferingImage.lineTo(Stage.width + 15, Stage.height + 15);
        bufferingImage.lineTo(0, Stage.height + 15);
        bufferingImage.endFill();

        var bufferTextFormat:TextFormat = new TextFormat();
        bufferTextFormat.align = "center";
        bufferTextFormat.color = 0xFFFFFF;
        bufferTextFormat.size = 20;
        bufferTextFormat.font = "_serif"; 

        bufferText = bufferingImage.createTextField("text", 0,
            ((Stage.width + 15) / 2) - 50, ((Stage.height + 15) / 2) - 15,
            300, 30);
        bufferText.setNewTextFormat(bufferTextFormat);
        bufferText.text = "Buffering 0%...";

        this.onPlayPress.player = this;
        sound.setVolume(50);

        clickToPlay.beginFill(0x000000);
        clickToPlay.moveTo(0, 0);
        clickToPlay.lineTo(ctpdims.width, 0);
        clickToPlay.lineTo(ctpdims.width,
                           ctpdims.height);
        clickToPlay.lineTo(0, ctpdims.height);
        clickToPlay.endFill();
        clickToPlay._x = Number(ctpdims.x);
        clickToPlay._y = Number(ctpdims.y);

        clickToPlay.beginFill(0xFFFFFF);
        var centerX = (clickToPlay._width / 2);
        var centerY = (clickToPlay._height / 2);
        clickToPlay.moveTo(centerX - 25, centerY - 25);
        clickToPlay.lineTo(centerX - 25, centerY + 25);
        clickToPlay.lineTo(centerX + 25, centerY);
        clickToPlay.lineTo(centerX - 25, centerY - 25);
        clickToPlay.endFill();
        clickToPlay.onPress = this.onPlayPress;

        setInterval(this, "update", 250);
    }

    public function onInitMetaDataCallback(data:Object):Void {
        var player:CrewPlayer = arguments.callee.player;
        player.onInitMetaData(data);
    }

    public function onPlayPress():Void {
        var player:CrewPlayer = arguments.callee.player;
        player.seek(player.startTime);
    }

    public function update():Void {
        if (paused) {
			for (var i=0; i<videoStreams.length; i++){
				videoStreams[i].pause(true);
			}
            buffering = true;
        } else if (playing) {
        	var isFinished:Boolean = true;
			for (var i=0; i<videoStreams.length; i++){
				if (!videoStreams[i].isFinished()) {
					isFinished = false;
					break; 
				}
			}
            if (isFinished) {
                clickToPlay._visible = true;
            } else if (buffering) {
	         	var isBufferFull:Boolean = true;
				for (var i=0; i<videoStreams.length; i++){
					if (!videoStreams[i].isBufferFull()) {
						logger.debug("stream["+i+"] Buffer not full");
						isBufferFull = false; 
						break;
					}
				}
                if (isBufferFull) {
		 			for (var i=0; i<videoStreams.length; i++){
						videoStreams[i].pause(false);
						logger.debug("stream["+i+"] started");
					}
                    buffering = false;
                    bufferingImage._visible = false;
                } else {
                	var timeSum:Number=0;
                	var lengthSum:Number=0;
 		 			for (var i=0; i<videoStreams.length; i++){
						if (!videoStreams[i].getBufferLength()>=0.5) {
							logger.debug("stream["+i+"] paused");
							videoStreams[i].pause(true);
						}
						timeSum+=videoStreams[i].getBufferTime();
						lengthSum+=videoStreams[i].getBufferLength();
					}
                    var bufferingPercent:Number = lengthSum/timeSum;
                    bufferingPercent *= 100;
                    bufferText.text = "Buffering "
                        + Math.round(bufferingPercent) + "%...";
                    bufferingImage._visible = true;
                }
            } else {
	         	var isBufferEmpty:Boolean = false;
				for (var i=0; i<videoStreams.length; i++){
					if (videoStreams[i].isBufferEmpty()) {
						logger.debug("stream["+i+"] Buffer empty");
						isBufferEmpty = true; 
						break;
					}
				}
                if (isBufferEmpty) {
                	logger.debug("start buffering");
                    buffering = true;
                }
            }
        }
    }

    public function stop() {
		for (var i=0; i<videoStreams.length; i++){
			videoStreams[i].stop();
		}
        netConnection.close();
    }
}
