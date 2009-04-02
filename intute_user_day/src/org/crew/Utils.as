/**
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
