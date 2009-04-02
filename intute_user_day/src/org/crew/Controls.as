import org.crew.CrewPlayer;

/**
 * The controls of the player
 */
class org.crew.Controls {

    // The player
    private var parent:CrewPlayer;

    // The depth of the clips
    private var depth:Number;

    // The x coordinate
    private var x:Number;

    // The y coordinate
    private var y:Number;

    // The width
    private var width:Number;

    // The height
    private var height:Number;

    // The background colour
    private var backgroundColour:Number;

    // The border colour
    private var borderColour:Number;

    // The text colour
    private var textColour:Number;

    // The colour of the volume slider
    private var sliderColour:Number;

    // A play / pause button
    private var playButton:MovieClip;

    // A fast-forward button
    private var ffButton:MovieClip;

    // A rewind button
    private var rwButton:MovieClip;

    // The image begind the volume slider
    private var volumeImage:MovieClip;

    // A volume control slider
    private var volumeSlider:MovieClip;

    // True if the volume is changing
    private var volumeSliding:Boolean = false;

    public function Controls(parent:CrewPlayer, depth:Number, x:Number,
            y:Number, width:Number, height:Number, backgroundColour:Number,
            borderColour:Number, textColour:Number, sliderColour:Number) {
        this.parent = parent;
        this.depth = depth;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundColour = backgroundColour;
        this.borderColour = borderColour;
        this.textColour = textColour;
        this.sliderColour = sliderColour;

        playButton = parent.createEmptyMovieClip("playButton", depth);
        volumeImage = parent.createEmptyMovieClip("volumeImage", depth + 1);
        volumeSlider = parent.createEmptyMovieClip("volumeSlider", depth + 2);

        this.onSliderPress.controls = this;
        this.onSliderRelease.controls = this;
        this.onSliderMove.controls = this;
        this.onPlayPress.controls = this;
        volumeSlider.onPress = this.onSliderPress;
        volumeSlider.onRelease = this.onSliderRelease;
        volumeSlider.onReleaseOutside = this.onSliderRelease;
        volumeSlider.onMouseMove = this.onSliderMove;
        playButton.onPress = this.onPlayPress;
    }

    public function draw():Void {
        volumeImage._x = x;
        volumeImage._y = y;
        volumeImage.lineStyle(1, borderColour, 100, false, "normal", "round",
            "round", 3);
        volumeImage.beginFill(backgroundColour);
        volumeImage.moveTo(0, 0);
        volumeImage.lineTo(0, height);
        volumeImage.lineTo(width / 2, height);
        volumeImage.lineTo(width / 2, 0);
        volumeImage.lineTo(0, 0);
        volumeImage.endFill();
        volumeImage.lineStyle(4, textColour, 100, false, "normal", "none",
                "square", 3);
        for (var i:Number = 0; i < 7; i++) {
            var pos:Number = i * ((width - 4) / 12) + 2;
            volumeImage.moveTo(pos, height);
            volumeImage.lineTo(pos, height - (pos * (height / (width / 2))));
        }

        volumeSlider._x = x + (width / 4) - 3;
        volumeSlider._y = y;
        volumeImage.lineStyle(1, borderColour, 100, false, "normal", "round",
            "round", 3);
        volumeSlider.beginFill(sliderColour);
        volumeSlider.moveTo(0, 0);
        volumeSlider.lineTo(0, height);
        volumeSlider.lineTo(6, height);
        volumeSlider.lineTo(6, 0);
        volumeSlider.lineTo(0, 0);
        volumeSlider.endFill();

        playButton._x = x + (width / 2) + 10;
        playButton._y = y;
        if (parent.isPlaying()) {
            drawPause();
        } else {
            drawPlay();
        }
    }

    function drawPlay() {
        playButton.lineStyle(1, borderColour, 100, false, "normal", "round",
            "round", 3);
        playButton.beginFill(backgroundColour);
        playButton.moveTo(0, 0);
        playButton.lineTo(0, height);
        playButton.lineTo(height, height);
        playButton.lineTo(height, 0);
        playButton.lineTo(0, 0);
        playButton.endFill();
        playButton.beginFill(textColour);
        playButton.moveTo(6, 6);
        playButton.lineTo(6, height - 5);
        playButton.lineTo(height - 5, height / 2);
        playButton.lineTo(6, 6);
        playButton.endFill();
    }

    function drawPause() {
        playButton.lineStyle(1, borderColour, 100, false, "normal", "round",
            "round", 3);
        playButton.beginFill(backgroundColour);
        playButton.moveTo(0, 0);
        playButton.lineTo(0, height);
        playButton.lineTo(height, height);
        playButton.lineTo(height, 0);
        playButton.lineTo(0, 0);
        playButton.endFill();
        playButton.beginFill(textColour);
        playButton.moveTo(5, 5);
        playButton.lineTo(5, height - 4);
        playButton.lineTo((height / 2) - 1, height - 4);
        playButton.lineTo((height / 2) - 1, 5);
        playButton.endFill();
        playButton.beginFill(textColour);
        playButton.moveTo((height / 2) + 1, 5);
        playButton.lineTo((height / 2) + 1, height - 4);
        playButton.lineTo(height - 5, height - 4);
        playButton.lineTo(height - 5, 5);
        playButton.endFill();
    }

    function onSliderPress():Void {
        var controls:Controls = arguments.callee.controls;
        controls.volumeSliding = true;
    }

    function onSliderRelease():Void {
        var controls:Controls = arguments.callee.controls;
        controls.volumeSliding = false;
    }

    function onSliderMove():Void {
        var controls:Controls = arguments.callee.controls;
        if (controls.volumeSliding) {
            var mouseX = controls.volumeSlider._parent._xmouse;
            var min = controls.volumeImage._x
                - (controls.volumeSlider._width / 2);
            var max = controls.volumeImage._x + controls.volumeImage._width
                - (controls.volumeSlider._width / 2);
            if (mouseX < min) {
                mouseX = min;
            } else if (mouseX > max) {
                mouseX = max;
            }

            controls.volumeSlider._x = mouseX
            var x:Number = controls.volumeSlider._x - min;
            var unitWidth:Number = 100 / controls.volumeImage._width;
            var volume:Number = unitWidth * x;
            controls.parent.setVolume(volume);
        }
    }

    function onPlayPress():Void {
        var controls:Controls = arguments.callee.controls;
        if (controls.parent.isPlaying()) {
            controls.parent.pause();
            controls.drawPlay();
        } else {
            controls.parent.resume();
            controls.drawPause();
        }
    }
}
