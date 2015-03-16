# Introduction #

![http://crew.googlecode.com/svn/wiki/images/CrewPlayer/positions.png](http://crew.googlecode.com/svn/wiki/images/CrewPlayer/positions.png)

The ReplayLayout defines the look of the CrewPlayer. A Crew user can Define different layouts to be used for the Player. These ReplayLayouts define replay positions, i.e. positions where a video or metadata stream is displayed. A ReplayLayout is defined in the layouts.xml file as described below.

```
<?xml version="1.0" encoding="UTF-8"?>
<layouts>
    <layout name="CrewDefault">
        <element name="Video" x="30" y="30" width="240" height="196"/>
        <element name="Screen" x="285" y="30" width="720" height="540" has-changes="true" />
        <element name="Annotation" x="30" y="245" width="240" height="325" assignable="false"/>
        <element name="Slider" x="120" y="580" width="885" height="175" assignable="false"/>
        <element name="Controls" x="30" y="580" width="100" height="20" assignable="false"/>
    </layout>
</layouts>
```

Each replay position has a position `x="`**`<number>`**`" y="`**`<number>`**`"` and its dimensions `width="`**`<number>`**`" height="`**`<number>`**`"` assigned.

The xml file defines different kinds of replay positions:
  * **assingable**
> an assignable replay position displays a video stream.
  * **predefined**
> a predefined replay position `assignable="false"` is used for elements used for standard tasks the current crew-player

## Assingable Replay Positions ##
Each video stream has a name assigned that matches the stream name of a recording. The assingment of these names is done in the [event recorder](Recording.md).

An assignable replay position can have the `has-changes="true"` parameter. This indicates to the [event recorder](Recording.md) that the CrewPlayer expects "slide changes" in this replay-position. The event recorder calculates time positions where the video stream assigned to that position changes "significantly" assuming a slide change happened. These slide changes will be stored on the server creating thumbnail images that are placed in the annotation slider. The slide changes are also used to generate the Slide annotations in the time-slider.

## Predefined Replay Positions ##

  * **Controls**
> ![http://crew.googlecode.com/svn/wiki/images/CrewPlayer/controls.png](http://crew.googlecode.com/svn/wiki/images/CrewPlayer/controls.png)

> The controls position contains the play/pause button and the audio volume control.

  * **Annotation**
> ![http://crew.googlecode.com/svn/wiki/images/CrewPlayer/annotations.png](http://crew.googlecode.com/svn/wiki/images/CrewPlayer/annotations.png)

> The Annotation position accommodates the screen-shots of the new slides as well as the live annotations created by the event audience.

  * **Slider**
> ![http://crew.googlecode.com/svn/wiki/images/CrewPlayer/slider.png](http://crew.googlecode.com/svn/wiki/images/CrewPlayer/slider.png)

> The Slider position is a representation of a time-slider. It consists of a list of slide-bars one for each of the used annotation types. This slider bar should be located on the bottom of the player, as the player currently calculates its height dependent on the number of annotation types. The time-slider represents contains annotation indicators for each individual live annotation in relation to the recording. The gray bar can be moved along the time slider to seek to a new replay position. When hovering over an annotation indicator a tooltip appears to show the content of the annotation.