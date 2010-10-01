<%@page isELIgnored="false"%>
<%@page import="net.crew_vre.liveAnnotations.Client"%>
<%@page import="net.crew_vre.liveAnnotations.Server"%>
<%@page import="net.crew_vre.annotations.liveannotationtype.LiveAnnotationTypeRepository"%>
<%@page import="net.crew_vre.annotations.liveannotationtype.impl.LiveAnnotationTypeRepositoryXmlImpl"%>
<%@page import="java.util.Enumeration"%><html>
<%@taglib prefix="crew" uri="/WEB-INF/tld/CREW.tld" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <head>
        <title>
            CREW live Annotation tool
            <c:if test="${not empty param.annotates}">
                - ${param.annotates}
            </c:if>
        </title>
        <link rel="stylesheet" type="text/css" href="../css/jabber.css"/>
        <script language="JavaScript" src="../js/update.js"></script>
        <script language="JavaScript" src="../js/crew.js"></script>
        <script language="JavaScript" src="../js/jabber.js"></script>
        <script language="JavaScript">
            var currentNickname = '${param.nickname}';

            function changeNick() {
                if (currentNickname == null) {
                    currentNickname = '${param.nickname}';
                }
                currentNickname = changeNickname(currentNickname);
            }
        </script>
    </head>
    <c:choose>
        <c:when test="${not empty sessionScope.client}">
            <body>
                <p>Client already open in other Browser Window!</p>
            </body>
        </c:when>
        <c:when test="${empty param.email}">
            <body>
                <h1>Select a Session to Annotate:</h1>
                <form method="post" action="index.jsp">
                    <p>Please Enter your Name: <input type="text" name="nickname"/></p>
                    <p>Please Enter your email: <input type="text" name="email"/></p>
                    <p><input type="submit" name="submit" value="Annotate Selected Session"/></p>
                </form>
            </body>
        </c:when>
        <c:otherwise>
            <%
                boolean done = false;
                String nickname = request.getParameter("nickname");
                String email = request.getParameter("email");
                Client client = new Client((Server)
                        request.getAttribute("server"),
                        nickname, email);
                session.setAttribute("client", client);
                session.setMaxInactiveInterval(365 * 24 * 60 * 60);
            %>
            <body onfocus="dofocus();"
                    onresize="doresize('messages', 'participants',
                                     'sendmessage', 'vertsep', 'horizsep');"
                      onbeforeunload="return beforeclose();">
                <div class="header" id="header">
                    <h1>Current Annotations:</h1>
                </div>
                <div class="message" id="messages"></div>
                <div class="participants" id="participants"></div>
                <div class="sendmessage" id="sendmessage">
                    <div class="sendmessagecontent" id="sendmessagecontent"></div>
                </div>
                <div class="verticalseparator" id="vertsep"
                    onmousedown="mouseOverVertical(event,
                        new Array('messages'), new Array('participants'),
                        'vertsep', new Array('messages'));"
                    onmouseup="stopDrag();calculateWidthPercent('messages');">
                </div>
            </body>
            <script language="JavaScript">
                doresize('messages', 'participants', 'sendmessage',
                         'vertsep', 'horizsep');

                var liveAnnotations=new Array();
                var localAnnotations=new Array();
                <crew:liveAnnotationClientLayout liveAnnotationTypeVar="liveAnnotationType"
                    buttonVar="button" buttonVisibleVar="buttonVisible" colourVar="colour"
                    containedFieldsVar="containedFields" contentVar="content"
                    thumbnailVar="thumbnail" variablesVar="variables">
                    if ('${buttonVisible}'!='false'){
                        liveAnnotations['${liveAnnotationType}']=new Object();
                        liveAnnotations['${liveAnnotationType}'].type='${liveAnnotationType}';
                        liveAnnotations['${liveAnnotationType}'].button='${button}';
                        liveAnnotations['${liveAnnotationType}'].buttonVisible='${buttonVisible}';
                        liveAnnotations['${liveAnnotationType}'].colour='${colour}';
                        liveAnnotations['${liveAnnotationType}'].containedFields=${containedFields};
                        liveAnnotations['${liveAnnotationType}'].content=${content};
                        liveAnnotations['${liveAnnotationType}'].thumbnail='${thumbnail}';
                        liveAnnotations['${liveAnnotationType}'].variables=${variables};
                    }
                </crew:liveAnnotationClientLayout>
                crew_buttonbox('sendmessagecontent');
                new Update('messages', 'participants', 'sendmessage',
                    'focusbutton', 'getmessage.jsp');
                window.onbeforeunload = function (event) {
                    return beforeclose();
                };
            </script>
        </c:otherwise>
    </c:choose>
</html>
