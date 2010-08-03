/**
 * This class is used to update the jabber messages
 */

/**
 * Creates a new Update
 *
 * @param messagediv The div containing the messages
 * @param participantdivid The div containing the participants
 * @param senddivid The div containing the send box
 * @param bringtofrontcheckid The checkbox to decide if the window should
 *                                jump to the front on a new message
 * @param url The url to get the message from
 */
function Update(messagedivid, participantdivid, senddivid,
        bringtofrontcheckid, url) {
    this.url = url;
    this.messagedivid = messagedivid;
    this.participantdivid = participantdivid;
    this.senddivid = senddivid;
    this.bringtofrontcheckid = bringtofrontcheckid;

    this.contentid='sendmessagecontent';

    this.request = null;

    this.getUpdate = function() {
        this.request = newRequest();
        this_ = this;
        this.request.onreadystatechange = function() {this_.handleUpdate()};
        this.request.open("POST", this.url, true);
        this.request.setRequestHeader('Content-Type',
                                      'application/x-www-form-urlencoded');
        this.request.send("");
    };
    this.handleUpdate = function() {
        if (this.request.readyState == 4) {
            if (this.request.status == 200) {
                var response = this.request.responseXML.documentElement;
                var type=response.getElementsByTagName('type')[0].firstChild.data;
                if (type == "CrewLiveAnnotation") {
                    var colour=response.getElementsByTagName('colour')[0].firstChild.data;
                    var annotation = response.getElementsByTagName('annotation');
                    var messagediv = document.getElementById(this.messagedivid);
                    var message_text = response.getElementsByTagName('crew_annotation')[0].firstChild.data;
                    var ann_text=null;
                    var allowedit=false;
                    if (response.getElementsByTagName('text')[0] != null)
                        ann_text= response.getElementsByTagName('text')[0].firstChild;
                    var mssgId_text=null;
                    if (response.getElementsByTagName('messageId')[0] != null)
                        mssgId_text= response.getElementsByTagName('messageId')[0].firstChild;
                    if (response.getElementsByTagName('submittingClient')[0] != null)
                        allowedit=true;

                    var timestamp = response.getElementsByTagName('timestamp')[0].firstChild.data;
                    var dateparts =timestamp.split(" ");
                    var date=dateparts[3];
                    var author = response.getElementsByTagName('displayName')[0].firstChild.data;
                    var annotation_text="";
                    var modifies="";

                    var date_str="["+date+"]";
                    if (ann_text!=null)
                        annotation_text=ann_text.data;
                    if (mssgId_text!=null)
                        modifies=mssgId_text.data;

                    var edits=annotation[0].getElementsByTagName("edits");
                    var table_text="";
                    var msg_output=true;
                    if (edits){
                        var editId=edits[0].firstChild.data;
                        table_text=document.getElementById(edits[0].firstChild.data);
                        if (table_text){
                            table_text.innerHTML=format_annotation('sendmessagecontent',colour,date_str,author,annotation[0],message_text,editId,allowedit);
                            msg_output=false;
                        }
                    }
                    if (msg_output){
                        var relates=annotation[0].getElementsByTagName("CrewLiveAnnotationRelatesTo");
                        if (relates.length!=0){
                            table_text=document.getElementById(relates[0].firstChild.data+'_responses');
                            if (table_text){
                                table_text.innerHTML += '<div id="'+modifies+'">'+format_annotation('sendmessagecontent',colour,date_str,author,annotation[0],message_text,modifies,allowedit)+'</div>';
                                table_text.innerHTML += '<div style="position:relative; left:20px;" id="'+modifies+'_responses"></div>';
                                msg_output=false;
                            }
                        }
                        if (msg_output){
                            messagediv.innerHTML += '<div id="'+modifies+'">'+format_annotation('sendmessagecontent',colour,date_str,author,annotation[0],message_text,modifies,allowedit)+'</div>';
                            messagediv.innerHTML += '<div style="position:relative; left:20px;" id="'+modifies+'_responses"></div>';
                        }
                    }
                    messagediv.scrollTop = messagediv.scrollHeight;
                } else if (type == "CrewLiveAnnotationClient") {
                    var name=response.getElementsByTagName('name')[0].firstChild.data;
                    var event=response.getElementsByTagName('event')[0].firstChild.data;
                    if (event=="add"){
                        addParticipant(name);
                    } else if (event=="remove"){
                        removeParticipant(name);
                    }
                }
                if (type != "None"){
 //               	alert("Type: "+ type);
                }
                if (type != "Done") {
                    this.getUpdate();
                } else {
                    document.getElementById('sendmessage').innerHTML =
                        '<a href="javascript: window.close()">'
                        + 'Close this window</a><br/>'
                        + '<a href="javascript: window.location.reload()">'
                        + 'Re-connect</a><br/>';
                }
            } else {
                alert("An error has occured.");
            }
        }
    };
    this.getUpdate();
}

function newRequest() {
    if (window.XMLHttpRequest) {
        return new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        return new ActiveXObject("Microsoft.XMLHTTP");
    }
}
