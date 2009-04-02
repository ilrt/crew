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

class org.crew.VideoStream {
	
	private var parent:CrewPlayer = null;
	private var videoName:String="";
	private var videoNetStream:NetStream = null;
    private var videoDuration:Number = -1;
    private var videoUrl:String = "";
    private var audioStreams:Array = null;
    private var videoDisplay:MovieClip = null;
    private var videoStream:Number = 0;
	private var x:Number = 0;
	private var y:Number = 0;
	private var width:Number = 0;
	private var height:Number = 0;
	private var depth:Number = 0;
	private var area:Number=0;
	private var syncedStreams:Array=null;
	private var backgroundImage:MovieClip = null;  
	private var netConnection:NetConnection;
    private var audio:MovieClip = null;
	private var sound:Sound=null;

	public function VideoStream(parent:CrewPlayer, name:String,
			    x:Number, y:Number, width:Number, height:Number, depth:Number,
	            stream:Array, bgImage:MovieClip, netConnection:NetConnection) {
		this.parent=parent;	
		this.videoName=name;
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.area=width*height;
		this.depth=depth;
		this.videoDuration=stream["duration"];		
		this.videoStream=stream["ssrc"];
	    this.videoDisplay=parent.attachMovie("VideoDisplay", "dummyDisplay", depth);   
        this.videoDisplay.video._width = this.width;
        this.videoDisplay.video._height = this.height;
        this.videoDisplay._x = this.x;
        this.videoDisplay._y = this.y;
	    this.backgroundImage = bgImage;	     
	    this.backgroundImage.beginFill(0x9a9393);
        this.backgroundImage.moveTo(this.x,this.y);
        this.backgroundImage.lineTo(this.x,this.y+this.height);
        this.backgroundImage.lineTo(this.x+this.width,this.y+this.height);
        this.backgroundImage.lineTo(this.x+this.width,this.y);
        this.backgroundImage.lineTo(this.x,this.y);
        this.backgroundImage.endFill();
        this.netConnection = netConnection;        
        this.videoNetStream = new NetStream(netConnection);
        this.videoNetStream.onStatus = this.onStatus;
        this.onStatus.stream = this;
        this.videoDisplay.video.attachVideo(videoNetStream);        
        this.audio = this.parent.createEmptyMovieClip("audio", this.parent.getNextHighestDepth());
        this.audio.attachAudio(videoNetStream);
        this.sound = new Sound(audio);
        this.sound.setVolume(0);
	}

	public function setUrl(url:String, audioStreams:Array, syncStreams:Array){
		this.videoUrl=url + "?duration=" + this.videoDuration
            + "&video=" + this.videoStream;
        this.videoUrl += "&width=" + this.width;
        this.videoUrl += "&height=" + this.height;
        for (var i = 0; i < syncStreams.length; i++) {
            var ss=syncStreams[i].getVideoStream();
            if (ss!=this.videoStream){
            	this.videoUrl += "&sync=" + ss;
				this.syncedStreams.push(syncStreams[i]);
            }
        }
        for (var i = 0; i < audioStreams.length; i++) {
            var as=audioStreams[i];
            this.videoUrl += "&audio=" + as["ssrc"];
        }
	}
	
	public function getSound():Sound {
		return sound;
	}
	
	public function getDims():Object{
		var dims:Object={area:area,x:x,y:y,width:width,height:height};
		return dims;
	}
	
    public function updateStatus(status:Object) {
	    if (status["code"] == "NetStream.Play.StreamNotFound") {
			var error:TextField = videoDisplay.createTextField("error",
				10, 0, (videoDisplay._height / 2) - 10,
				videoDisplay._width, 20);
			var format:TextFormat = new TextFormat();
			format.align = "center";
			format.size = 20;
			error.setTextFormat(format);
			error.text = "Error loading stream";
			this.videoDuration = 0;
			this.parent.logger.debug("Error loading video");
		}
    }
        
    public function onStatus(info:Object):Void {
        var stream:VideoStream = arguments.callee.stream;
        stream.updateStatus(info);
    }
    
	public function pause(state:Boolean){
		this.videoNetStream.pause(state);
	}

    public function getTime():Number {
        return videoNetStream.time;
    }
    
    public function isFinished():Boolean {
        return (videoNetStream.time >= (videoDuration-1));
    }
    
    public function isBufferFull():Boolean {
        return (isFinished() || (videoNetStream.bufferLength > videoNetStream.bufferTime));
    }
    
    public function isBufferEmpty():Boolean {
        return (!isFinished() && (videoNetStream.bufferLength <= 1));
    }
    
    public function seek(time:Number):Void {
    	parent.logger.debug("seek url:" + this.videoUrl + "&start=" + time);
    	this.videoNetStream.close();
    	this.videoNetStream.setBufferTime(0);       
    	if (time < this.videoDuration) {
            if ((this.videoDuration - time) < 10) {
                this.videoNetStream.setBufferTime((this.videoDuration - time) / 2);
            } else {
                this.videoNetStream.setBufferTime(10);
            }
            this.videoNetStream.play(this.videoUrl + "&start=" + time);
        } else {
            this.videoNetStream.setBufferTime(0.5);
            this.videoNetStream.play(this.videoUrl + "&start="
                + (this.videoDuration - 1));
        }
    }
    
    public function stop() {
    	this.videoNetStream.close();
    }

    public function getBufferLength():Number {
        return videoNetStream.bufferLength;
    }
    
    public function getBufferTime():Number {
        return videoNetStream.bufferTime;
    }

	public function getVideoStream():Number{
		return videoStream;
	}
}
