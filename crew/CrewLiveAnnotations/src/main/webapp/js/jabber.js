var messagePercentWidth = 80;
var participants = [];

String.prototype.trim = function() {
    return this.replace(/^\s+|\s+$/g, "");
};

function addParticipant(name) {
    participants.push(name);
    displayParticipants();
}

function removeParticipant(name) {
    var index = -1;
    for (var i = 0; i < participants.length; i++) {
        if (participants[i] == name) {
            index = i;
        }
    }
    if (index!=-1) {
    	participants.splice(index, 1);
    	displayParticipants();
    }
}

function displayParticipants() {
    var contents = "";
    for (var i = 0; i < participants.length; i++) {
        contents += "<span style='white-space: nowrap'>- "
            + participants[i] + "</span><br/>\n";
    }
    document.getElementById('participants').innerHTML = contents;
}

function calculateWidthPercent(messagedivid) {
    var width = getWidth();
    var messageWidth = parseInt(
        document.getElementById(messagedivid).style.width, 10);
    messagePercentWidth = Math.round((messageWidth * 100) / width);
}

function beforeclose() {
    request = newRequest();
    request.open("POST", "close.jsp", false);
    request.send("");
}


function doresize(messagedivid, participantdivid, sendmessagedivid,
        vertsepdivid, horizsepdivid) {
    var remainheight = getHeight() - 260;
    setSize(messagedivid, messagePercentWidth - 2.8, remainheight - 11 + "px");
    setPosition(messagedivid, 0, 0);
    setSize(participantdivid, 100 - messagePercentWidth, remainheight + "px");
    setPosition(participantdivid, messagePercentWidth, 0);
    setSize(vertsepdivid, "3px", remainheight + "px");
    setPosition(vertsepdivid, messagePercentWidth, 0);
    var messagediv = document.getElementById(messagedivid);
    messagediv.scrollTop = messagediv.scrollHeight;
    dofocus();
}

function dofocus() {
    var sendbox = document.getElementById('sendbox');
    if (sendbox != null) {
        sendbox.focus();
    }
}

function getWidth() {
    var myWidth = 0;
    if (typeof(window.innerWidth) == 'number') {
        myWidth = window.innerWidth;
    } else if (document.documentElement &&
             document.documentElement.clientWidth) {
        myWidth = document.documentElement.clientWidth;
    } else if (document.body && document.body.clientWidth) {
        myWidth = document.body.clientWidth;
    }
    return myWidth;
}

function getHeight() {
    var myHeight = 0;
    if (typeof(window.innerHeight) == 'number') {
        myHeight = window.innerHeight;
    } else if (document.documentElement &&
             document.documentElement.clientHeight) {
        myHeight = document.documentElement.clientHeight;
    } else if (document.body && document.body.clientHeight) {
        myHeight = document.body.clientHeight;
    }
    return myHeight;
}

function setSize(itemid, widthpercent, heightpercent) {
    var width = getWidth();
    var height = getHeight();
    var item = document.getElementById(itemid);
    if (widthpercent == 0) {
        item.style.width = 0;
    } else if (typeof(widthpercent) == 'string') {
        item.style.width = widthpercent;
    } else {
        item.style.width = Math.round(((width * widthpercent) / 100)) + "px";
    }
    if (heightpercent == 0) {
        item.style.height = 0;
    } else if (typeof(heightpercent) == 'string') {
        item.style.height = heightpercent;
    } else {
        item.style.height = Math.round(((height * heightpercent) / 100)) + "px";
    }
}

function setPosition(itemid, widthpercent, heightpercent) {
    var width = getWidth();
    var height = getHeight();
    var item = document.getElementById(itemid);
    if (widthpercent == 0) {
        item.style.left = 0;
    } else {
        item.style.left = Math.round(((width * widthpercent) / 100)) + "px";
    }
    if (heightpercent == 0) {
        item.style.top = 50;
    } else {
        item.style.top = Math.round(((height * heightpercent) / 100)) + "px";
    }
}

function doDragVertical(event, draggingItemsLeft, draggingItemsRight,
        draggingItemVertical, cursorStart, dragItemStart,
        draggingItemsLeftStartWidth, draggingItemsRightStartWidth,
        draggingItemsRightStart, scrollItems) {
    if (event == null) {
        event = window.event;
    }
    var diff = event.clientX - cursorStart;
    draggingItemVertical.style.left = parseInt(dragItemStart, 10) + diff;
    for (i in draggingItemsLeft) {
        draggingItemsLeft[i].style.width =
            parseInt(draggingItemsLeftStartWidth[i], 10) + diff;
    }
    for (i in draggingItemsRight) {
        draggingItemsRight[i].style.width =
            parseInt(draggingItemsRightStartWidth[i], 10) - diff;
        draggingItemsRight[i].style.left =
            parseInt(draggingItemsRightStart[i], 10) + diff;
    }
    for (i in scrollItems) {
        scrollItems[i].scrollTop = scrollItems[i].scrollHeight;
    }
}

function stopDrag() {
    document.onmousemove = null;
    dofocus();
}

function mouseOverVertical(event, leftitems, rightitems, dragitem,
        scrolls) {
    if (event == null) {
        event = window.event;
    }
    var draggingItemsLeft = new Array(leftitems.length);
    var draggingItemsLeftStartWidth = new Array(leftitems.length);
    var draggingItemsRight = new Array(rightitems.length);
    var draggingItemsRightStartWidth = new Array(rightitems.length);
    var draggingItemsRightStart = new Array(rightitems.length);
    var scrollItems = new Array(scrolls.length);
    var draggingItemVertical = document.getElementById(dragitem);
    var dragItemStart = draggingItemVertical.style.left;
    var cursorStart = event.clientX;
    for (i in leftitems) {
        draggingItemsLeft[i] = document.getElementById(leftitems[i]);
        draggingItemsLeftStartWidth[i] = draggingItemsLeft[i].style.width;
    }
    for (i in rightitems) {
        draggingItemsRight[i] = document.getElementById(rightitems[i]);
        draggingItemsRightStartWidth[i] = draggingItemsRight[i].style.width;
        draggingItemsRightStart[i] = draggingItemsRight[i].style.left;
    }
    for (i in scrolls) {
        scrollItems[i] = document.getElementById(scrolls[i]);
    }

    document.onmousemove = function(event) {
        doDragVertical(event, draggingItemsLeft, draggingItemsRight,
            draggingItemVertical, cursorStart, dragItemStart,
            draggingItemsLeftStartWidth, draggingItemsRightStartWidth,
            draggingItemsRightStart, scrollItems);
        return false;
        };
}

function sendOnReturn(e, sendboxid) {
    if (e.keyCode == 13) {
        sendMessage(sendboxid);
        return false;
    }
    return true;
}

function about() {
    window.open('about.jsp', 'about',
        'menubar=no,resizable=yes,toolbar=no,status=no,'
        + 'scrollbars=no,height=100,width=200');
}

function closeButton() {
    request = newRequest();
    request.open("POST", "close.jsp", false);
    request.send("");
    document.getElementById('sendmessage').innerHTML =
        '<a href="javascript: window.close()">Close this window</a><br/>'
        + '<a href="javascript: window.location.reload()">Re-connect</a><br/>';
}

function sendMessage(sendboxid) {
    var sendbox = document.getElementById(sendboxid);
    var text = sendbox.value;
    request = newRequest();
    request.open("POST", "sendmessage.jsp", false);
    request.setRequestHeader('Content-Type',
                             'application/x-www-form-urlencoded');
    request.send("text=" + encodeURIComponent(text));
    if (request.status != 200) {
        alert("Error sending message.");
    } else {
        sendbox.value = "";
    }
}

function changeNickname(oldnick) {
    var newnickname = prompt("Enter your new nickname:", oldnick);
    if ((newnickname != null) && (newnickname != oldnick)
            && (newnickname != "")) {
        request = newRequest();
        request.open("POST", "changenick.jsp", false);
        request.setRequestHeader('Content-Type',
                                 'application/x-www-form-urlencoded');
        request.send("nickname=" + encodeURIComponent(newnickname));
        if (request.status != 200) {
            alert("Error changing nickname.");
        } else {
            if (request.responseText.trim() == "") {
                return newnickname;
            } else {
                alert(request.responseText.trim());
            }
        }
    }
    return oldnick;
}