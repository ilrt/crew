<%-- dtd and xml declaration --%>
<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ page session="true" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><spring:message code="proj.title"/></title>
    <style type="text/css" media="screen">@import "./style.css";</style>
    <script type="text/javascript" src="js/swfobject.js"></script>
</head>
<body>

<div id="container">

    <%-- banner navigation--%>
    <%@ include file="includes/topNav.jsp" %>


    <%-- the logo banner --%>
    <%@ include file="includes/logo.jsp" %>

    <%-- The main content --%>
    <div id="mainBody">

        <div id="player-container">

            <p><a href="./displayEvent.do?eventId=<crew:uri uri='${eventId}'/>"><spring:message
                    code="event.return"/></a></p>

            <!-- div that displays the player -->
            <div id="player">&nbsp;</div>

            <!-- JS to setup and control the player -->
            <script type="text/javascript">

                function loadVideo(divid, playerid) {
                    document.getElementById(divid).innerHTML =
                    "Flash is required to replay this video."
                            + "  Please click "
                            + "<a href=\"http://www.macromedia.com/go/getflashplayer\">here</a>"
                            + " to get Flash.";
                    var mplayer = new SWFObject("CrewPlayer.swf", playerid,
                            "1005", "760", "8");
                    mplayer.addParam("AllowScriptAccess", "always");
                    mplayer.addParam("allowfullscreen", "true");
                    mplayer.addVariable("uri", "http://memetic.ag.manchester.ac.uk:5400/play/${recordingId}.flv?username=admin%26password=admin1234");
                    mplayer.write(divid);
                }

                function goToTime(time) {
                    document.getElementById('mplayer').seek(time);
                }

                function pause() {
                    document.getElementById('mplayer').pause();
                }

                function resume() {
                    document.getElementById('mplayer').resume();
                }
            </script>
            <script language="JavaScript">
                loadVideo("player", "mplayer");
            </script>

        </div>

        <script language="JavaScript">
            loadVideo('player', 'mplayer');
        </script>

    </div>
</div>


<%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>