import flash.external.ExternalInterface;

import LuminicBox.Log.ConsolePublisher;
import LuminicBox.Log.Logger;

import org.crew.Controls;
import org.crew.TimeSlider;
import org.crew.ThumbnailSlider;

/**
 * The main CREW player
 */
class org.crew.CrewPlayer extends MovieClip {

    // The log subsystem
    public var logger:Logger = null;

    // The NetConnection object
    private var netConnection:NetConnection = null;

    // The NetStream object
    private var videoNetStream:NetStream = null;

    // The Screen NetStream object
    private var screenNetStream:NetStream = null;

    // The duration of the video
    private var videoDuration:Number = 0;

    // The duration of the screen
    private var screenDuration:Number = 0;

    // The video display object
    private var videoDisplay:MovieClip = null;

    // The screen display object
    private var screenDisplay:MovieClip = null;

    // The Sound object
    private var sound:Sound = null;

    // The Audio movie clip
    private var audio:MovieClip = null;

    // The background of the player
    private var backgroundImage:MovieClip = null;

    // The image when buffering
    private var bufferingImage:MovieClip = null;

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

    // True if the video has finished playing
    private var videoFinished:Boolean = false;

    // True if the screen has finished playing
    private var screenFinished:Boolean = false;

    // True if the video is buffering
    private var videoBufferFilling:Boolean = false;

    // True if the screen is buffering
    private var screenBufferFilling:Boolean = false;

    public function CrewPlayer() {
        logger = new Logger();
        logger.addPublisher(new ConsolePublisher());

        Stage.scaleMode = "noScale";
        logger.debug(Stage.width + ", " + Stage.height);

        timeSlider = new TimeSlider(this, 30,
            Number(_root.sliderX) + 100, Number(_root.sliderY),
            Number(_root.sliderWidth) - 100, Number(_root.sliderHeight), 10,
            0x000000, 0x555555, 0x0000FF, 0xFFFFFF);
        thumbnailSlider = new ThumbnailSlider(this,
            Number(_root.annotationX), Number(_root.annotationY),
            Number(_root.annotationWidth), Number(_root.annotationHeight),
            40, _root.thumbnails.split(","), _root.thumbnailTimes.split(","),
            _root.thumbnailEndTimes.split(","), 0x000000, 0x555555, 0x0000FF,
            0x999999);
        controls = new Controls(this, 50, Number(_root.sliderX),
            Number(_root.sliderY), 100, Number(_root.sliderHeight), 0x000000,
            0x555555, 0xFFFFFF, 0x0000FF);

        ExternalInterface.addCallback("seek", this, this.seek);
        ExternalInterface.addCallback("pause", this, this.pause);
        ExternalInterface.addCallback("resume", this, this.resume);
    }

    /**
     * Called when the clip is loaded     */
    public function onEnterFrame():Void {
        if (loaded || (Stage.width <= 0) || (Stage.height <= 0)) {
            return;
        }
        loaded = true;

        timeSlider.draw();
        thumbnailSlider.draw();
        controls.draw();

        _x = -(Stage.width / 2);
        _y = -(Stage.height / 2);

        videoDisplay = attachMovie("VideoDisplay", "videoDisplay", 10);
        screenDisplay = attachMovie("ScreenDisplay", "screenDisplay", 20);
        audio = createEmptyMovieClip("audio", getNextHighestDepth());
        backgroundImage = createEmptyMovieClip("background", 0);
        backgroundImage.beginFill(_root.backgroundColour);
        backgroundImage.moveTo(0, 0);
        backgroundImage.lineTo(Stage.width + 15, 0);
        backgroundImage.lineTo(Stage.width + 15, Stage.height + 15);
        backgroundImage.lineTo(0, Stage.height + 15);
        backgroundImage.endFill();
        backgroundImage.beginFill(0x000000);
        backgroundImage.moveTo(Number(_root.videoX), Number(_root.videoY));
        backgroundImage.lineTo(Number(_root.videoX),
            Number(_root.videoY) + Number(_root.videoHeight));
        backgroundImage.lineTo(Number(_root.videoX) + Number(_root.videoWidth),
            Number(_root.videoY) + Number(_root.videoHeight));
        backgroundImage.lineTo(Number(_root.videoX) + Number(_root.videoWidth),
            Number(_root.videoY));
        backgroundImage.lineTo(Number(_root.videoX), Number(_root.videoY));
        backgroundImage.endFill();
        backgroundImage.beginFill(0x000000);
        backgroundImage.moveTo(Number(_root.screenX), Number(_root.screenY));
        backgroundImage.lineTo(Number(_root.screenX),
            Number(_root.screenY) + Number(_root.screenHeight));
        backgroundImage.lineTo(
            Number(_root.screenX) + Number(_root.screenWidth),
            Number(_root.screenY) + Number(_root.screenHeight));
        backgroundImage.lineTo(
            Number(_root.screenX) + Number(_root.screenWidth),
            Number(_root.screenY));
        backgroundImage.lineTo(Number(_root.screenX), Number(_root.screenY));
        backgroundImage.endFill();

        bufferingImage = createEmptyMovieClip("bufferingImage", 500);
        bufferingImage._visible = false;
        bufferingImage._alpha = 50;
        bufferingImage.beginFill(_root.backgroundColour);
        bufferingImage.moveTo(0, 0);
        bufferingImage.lineTo(Stage.width + 15, 0);
        bufferingImage.lineTo(Stage.width + 15, Stage.height + 15);
        bufferingImage.lineTo(0, Stage.height + 15);
        bufferingImage.endFill();
        var bufferText:TextField = bufferingImage.createTextField("text", 0,
            ((Stage.width + 15) / 2) - 50, ((Stage.height + 15) / 2) - 15,
            100, 30);
        var bufferTextFormat:TextFormat = new TextFormat();
        bufferTextFormat.align = "center";
        bufferTextFormat.color = 0xFFFFFF;
        bufferTextFormat.size = 20;
        bufferText.setNewTextFormat(bufferTextFormat);
        bufferText.text = "Buffering...";

        netConnection = new NetConnection();
        netConnection.connect(null);
        videoNetStream = new NetStream(netConnection);
        screenNetStream = new NetStream(netConnection);
        videoNetStream.onMetaData = this.onVideoMetaDataCallback;
        screenNetStream.onMetaData = this.onScreenMetaDataCallback;
        this.onVideoMetaDataCallback.player = this;
        this.onVideoMetaDataCallback.stream = videoNetStream;
        this.onScreenMetaDataCallback.player = this;
        this.onScreenMetaDataCallback.stream = screenNetStream;
        this.onVideoStatus.player = this;
        this.onVideoStatus.stream = videoNetStream;
        this.onScreenStatus.player = this;
        this.onScreenStatus.stream = screenNetStream;
        videoDisplay.video.attachVideo(videoNetStream);
        audio.attachAudio(videoNetStream);
        screenDisplay.screen.attachVideo(screenNetStream);
        sound = new Sound(audio);
        sound.setVolume(50);

        videoDisplay.video._width = Number(_root.videoWidth);
        videoDisplay.video._height = Number(_root.videoHeight);
        videoDisplay._x = Number(_root.videoX);
        videoDisplay._y = Number(_root.videoY);

        screenDisplay.screen._width = Number(_root.screenWidth);
        screenDisplay.screen._height = Number(_root.screenHeight);
        screenDisplay._x = Number(_root.screenX);
        screenDisplay._y = Number(_root.screenY);

        videoNetStream.setBufferTime(0);
        screenNetStream.setBufferTime(0);
        videoNetStream.play(_root.videoFile + "?firstframe=true");
        screenNetStream.play(_root.screenFile + "?firstframe=true");

        delete this.onEnterFrame;
    }

    public function play():Void {
        logger.debug("Play");
        timeSlider.start();
        thumbnailSlider.start();
        videoNetStream.onStatus = this.onVideoStatus;
        screenNetStream.onStatus = this.onScreenStatus;
        bufferingImage._visible = true;
        videoBufferFilling = true;
        screenBufferFilling = true;
        videoFinished = false;
        screenFinished = false;
        playing = true;
        paused = false;
        controls.drawPause();
        videoNetStream.setBufferTime(10);
        screenNetStream.setBufferTime(10);
        videoNetStream.play(_root.videoFile);
        screenNetStream.play(_root.screenFile);
    }

    public function seek(time:Number):Void {
        logger.debug("Seek " + time);
        timeSlider.start();
        thumbnailSlider.start();
        videoNetStream.onStatus = this.onVideoStatus;
        screenNetStream.onStatus = this.onScreenStatus;
        bufferingImage._visible = true;
        if (time < videoDuration) {
            videoBufferFilling = true;
            videoFinished = false;
        } else {
            videoBufferFilling = false;
            videoFinished = true;
        }
        if (time < screenDuration) {
            screenBufferFilling = true;
            screenFinished = false;
        } else {
            screenBufferFilling = false;
            screenFinished = true;
        }
        playing = true;
        paused = false;
        controls.drawPause();
        if (time < videoDuration) {
            if ((videoDuration - time) < 10) {
                videoNetStream.setBufferTime((videoDuration - time) / 2);
            } else {
                videoNetStream.setBufferTime(10);
            }
            videoNetStream.play(_root.videoFile + "?start=" + time);
        } else {
            videoNetStream.setBufferTime(0.5);
            videoNetStream.play(_root.videoFile + "?start="
                + (videoDuration - 1));
        }
        if (time < screenDuration) {
            if ((screenDuration - time) < 10) {
                screenNetStream.setBufferTime((screenDuration - time) / 2);
            } else {
                screenNetStream.setBufferTime(10);
            }
            screenNetStream.play(_root.screenFile + "?start=" + time);
        } else {
            screenNetStream.setBufferTime(0.5);
            screenNetStream.play(_root.screenFile + "?start="
                + (screenDuration - 1));
        }
    }

    public function pause():Void {
        if (playing && !paused) {
            paused = true;
            videoNetStream.pause(true);
            screenNetStream.pause(true);
        }
    }

    public function resume():Void {
        if (playing && paused) {
            paused = false;
            videoNetStream.pause(false);
            screenNetStream.pause(false);
        } else if (!playing) {
            play();
        }
    }

    public function isPlaying():Boolean {
        return playing && !paused;
    }

    public function getTime():Number {
        return Math.max(videoNetStream.time, screenNetStream.time);
    }

    public function getDuration():Number {
        return Math.max(videoDuration, screenDuration);
    }

    public function setVolume(volume:Number):Void {
        sound.setVolume(volume);
    }

    public function onMetaData(netStream:NetStream, data:Object):Void {
        if (data["duration"] != undefined) {
            if (netStream == videoNetStream) {
                videoDuration = data["duration"];
            } else if (netStream == screenNetStream) {
                screenDuration = data["duration"];
            }
            timeSlider.setSliderTime(0);
        }
    }

    public function onVideoMetaDataCallback(data:Object):Void {
        var player:CrewPlayer = arguments.callee.player;
        var netStream:NetStream = arguments.callee.stream;
        player.onMetaData(netStream, data);
    }

    public function onScreenMetaDataCallback(data:Object):Void {
        var player:CrewPlayer = arguments.callee.player;
        var netStream:NetStream = arguments.callee.stream;
        player.onMetaData(netStream, data);
    }

    public function updateStatus(netStream:NetStream, status:Object) {
        if (netStream == videoNetStream) {
            logger.debug("Video: " + status["code"]);
            if (status["code"] == "NetStream.Buffer.Empty") {
                logger.debug("Empty Video Buffer");
                if (videoNetStream.time < (videoDuration - 1)) {
                    bufferingImage._visible = true;
                    videoBufferFilling = true;
                    if (!screenBufferFilling) {
                        screenNetStream.pause(true);
                    }
                } else {
                    videoFinished = true;
                    if (screenFinished) {
                        playing = false;
                        controls.drawPlay();
                    }
                }
            } else if ((status["code"] == "NetStream.Buffer.Full")
                    || (status["code"] == "NetStream.Play.StreamNotFound")) {
                logger.debug("Full Video Buffer");
                videoBufferFilling = false;
                if (screenBufferFilling) {
                    videoNetStream.pause(true);
                } else {
                    bufferingImage._visible = false;
                    screenNetStream.pause(false);
                }
                if (status["code"] == "NetStream.Play.StreamNotFound") {
                     var error:TextField = videoDisplay.createTextField("error",
                        10, 0, (videoDisplay._height / 2) - 10,
                        videoDisplay._width, 20);
                     var format:TextFormat = new TextFormat();
                     format.align = "center";
                     format.size = 20;
                     error.setTextFormat(format);
                     error.text = "Error loading stream";
                }
            }
        } else if (netStream == screenNetStream) {
            logger.debug("Screen: " + status["code"]);
            if (status["code"] == "NetStream.Buffer.Empty") {
                logger.debug("Empty Screen Buffer");
                if (screenNetStream.time < (screenDuration - 1)) {
                    bufferingImage._visible = true;
                    screenBufferFilling = true;
                    if (!videoBufferFilling) {
                        videoNetStream.pause(true);
                    }
                } else {
                    screenFinished = true;
                    if (videoFinished) {
                        playing = false;
                        controls.drawPlay();
                    }
                }
            } else if ((status["code"] == "NetStream.Buffer.Full")
                    || (status["code"] == "NetStream.Play.StreamNotFound")) {
                logger.debug("Full Screen Buffer");
                screenBufferFilling = false;
                if (videoBufferFilling) {
                    screenNetStream.pause(true);
                } else {
                    bufferingImage._visible = false;
                    videoNetStream.pause(false);
                }
                if (status["code"] == "NetStream.Play.StreamNotFound") {
                     var error:TextField = screenDisplay.createTextField(
                        "error", 10, 0, (videoDisplay._height / 2) - 10,
                        videoDisplay._width, 20);
                     var format:TextFormat = new TextFormat();
                     format.align = "center";
                     format.size = 20;
                     error.setTextFormat(format);
                     error.text = "Error loading stream";
                }
            }
        } else {
            logger.debug("Unknown stream! " + status["code"]);
        }
    }

    public function onVideoStatus(info:Object):Void {
        var player:CrewPlayer = arguments.callee.player;
        var netStream:NetStream = arguments.callee.stream;
        player.updateStatus(netStream, info);
    }

    public function onScreenStatus(info:Object) {
        var player:CrewPlayer = arguments.callee.player;
        var netStream:NetStream = arguments.callee.stream;
        player.updateStatus(netStream, info);
    }
}
