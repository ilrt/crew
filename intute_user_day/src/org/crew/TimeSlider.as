import org.crew.CrewPlayer;

/**
 * A Slider that moves as the time updates
 */
class org.crew.TimeSlider {

    // The id of the interval used to update this time line
    private var intervalId:Number = -1;

    // The time line bar
    private var bar:MovieClip;

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

    // The height of the bar
    private var height:Number;

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

    public function TimeSlider(parent:CrewPlayer,
            depth:Number, x:Number, y:Number, width:Number, height:Number,
            sliderWidth:Number, backgroundColour:Number, borderColour:Number,
            sliderColour:Number, textColour:Number) {
        this.parent = parent;
        this.depth = depth;
        this.x = x;
        this.y = y;
        this.width = width - 100;
        this.height = height;
        this.sliderWidth = sliderWidth;
        this.backgroundColour = backgroundColour;
        this.borderColour = borderColour;
        this.sliderColour = sliderColour;
        this.textColour = textColour;
        this.onStartSliderDrag.timeSlider = this;
        this.onStopSliderDrag.timeSlider = this;
        this.onSliderMove.timeSlider = this;
        bar = parent.createEmptyMovieClip("bar", depth + 1);
        slider = parent.createEmptyMovieClip("slider", depth + 2);
        slider.onPress = this.onStartSliderDrag;
        slider.onRelease = this.onStopSliderDrag;
        slider.onReleaseOutside = this.onStopSliderDrag;
        slider.onMouseMove = this.onSliderMove;
    }

    public function draw() {
        bar._x = x;
        bar._y = y;
        bar.beginFill(backgroundColour);
        bar.moveTo(0, 0);
        bar.lineTo(width, 0);
        bar.lineTo(width, height);
        bar.lineTo(0, height);
        bar.endFill();
        bar.lineStyle(1, borderColour, 100, false, "normal", "round",
            "round", 3);
        bar.moveTo(0, 0);
        bar.lineTo(width, 0);
        bar.lineTo(width, height);
        bar.lineTo(0, height);
        bar.lineTo(0, 0);

        slider.beginFill(sliderColour);
        slider.lineStyle(1, borderColour, 100, false, "normal", "round",
            "round", 3);
        slider.moveTo(0, 0);
        slider.lineTo(sliderWidth, 0);
        slider.lineTo(sliderWidth, height - 2);
        slider.lineTo(0, height - 2);
        slider.endFill();
        slider._y = y + 1;
        slider._x = x - (sliderWidth / 2);

        var textFormat:TextFormat = new TextFormat();
        textFormat.color = textColour;
        timeText = parent.createTextField("timeText", depth + 3,
            x + bar._width + 2, y, 100, height);
        timeText.setNewTextFormat(textFormat);
        timeText.background = false;
        timeText.text = "Unknown Duration";
    }

    public function setSliderTime(time:Number):Void {
        var x:Number = this.x;
        if (parent.getDuration() > 0) {
            var unitWidth:Number = width / parent.getDuration();
            x = this.x + (unitWidth * time);
            setTimeText(time);
        }
        slider._x = x - (sliderWidth / 2);
    }

    public function getSliderTime():Number {
        var time:Number = 0;
        if (parent.getDuration() > 0) {
            var x:Number = slider._x + (sliderWidth / 2);
            var unitWidth:Number = width / parent.getDuration();
            time = (x - this.x) / unitWidth;
        }
        return time;
    }

    private function formatTimeDigit(digit:Number):String {
        var str:String = "";
        if (digit < 10) {
            str += "0";
        }
        str += String(digit);
        return str;
    }

    public function setTimeText(time:Number):Void {
        var dur = parent.getDuration();
        var durationHours:Number = Math.floor(dur / 3600);
        dur = dur - (durationHours * 3600);
        var durationMinutes:Number = Math.floor(dur / 60);
        dur = dur - (durationMinutes * 60);
        var durationSeconds:Number = Math.floor(dur);

        var hours:Number = Math.floor(time / 3600);
        time = time - (hours * 3600);
        var minutes:Number = Math.floor(time / 60);
        time = time - (minutes * 60);
        var seconds:Number = Math.floor(time);

        var text:String = "";
        text += formatTimeDigit(hours) + ":";
        text += formatTimeDigit(minutes) + ":";
        text += formatTimeDigit(seconds) + " / ";
        text += formatTimeDigit(durationHours) + ":";
        text += formatTimeDigit(durationMinutes) + ":";
        text += formatTimeDigit(durationSeconds);

        timeText.text = text;
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
}
