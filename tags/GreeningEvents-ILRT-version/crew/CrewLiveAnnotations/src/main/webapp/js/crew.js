
function crew_imageButton(id, title, imgUrl, text, action, width, height, left, top, right, bottom ) {
    //set default values;
    width = width || 0;
    height = height || 0;
    left = left || 0;
    top = top || 0;
    rigth = right || -1;
    bottom = bottom || -1;

    // The number of borders that will be changed
    var NO_BORDERS = 2;

    // The width of the border in pixels
    var BORDER_WIDTH = 2;

    var button= '<div onclick="' + action +'"';
    if (id != null) {
        button += ' id="' + id + '"';
    }
    if (title != null) {
        button += ' title="' + title + '"';
    }
    button += ' onmousedown="this.style.borderColor =' + " '#828177 #f9f8f3 #f9f8f3 #828177';" + '"';
    button += ' onmouseup="this.style.borderColor =' + " '#f9f8f3 #828177 #828177 #f9f8f3';" + '"';
    button += ' onmouseout="this.style.borderColor ='+ " '#f9f8f3 #828177 #828177 #f9f8f3';" + '"';
    button += ' style="width: ' + (width - (BORDER_WIDTH * NO_BORDERS)) + 'px;';
    button += ' height: ' + (height - (BORDER_WIDTH * NO_BORDERS))+ 'px;';
//	button += ' position: absolute;'
    if (bottom != -1 ) {
        button += ' bottom: ' + bottom + 'px;';
    } else {
        button += ' top: ' + top + 'px;';
    }
    if (right != -1) {
        button += ' right: ' + right + 'px;';
    } else {
        button += ' left: ' + left + 'px;';
    }
    button += ' border-width: ' + BORDER_WIDTH + 'px;';
    button += ' border-style: solid;';
    button += ' border-color: #f9f8f3 #828177 #828177 #f9f8f3;';
    button += ' background-color: #ece9d8;';
    button += ' text-align: center; overflow: hidden;';
    button += ' ">';
    button += '<table style="border-width: 0px; width: 100%; height: 100%">';
    button += '<tr><td style="text-align: center; width: 100%; vertical-align: middle;">';
    if (imgUrl != null) {
        button += '<img src="'+imgUrl+'" id="' + id + '_image">';
    } else if (text != null) {
        button += '<span style="cursor: default">' + text + '</span>';
    }
    button += '</td></tr></table></div>';
    return button;
}

function crew_buttonbox(contentid){
 	var CELL_SEP = "</td><td style=\"white-space: nowrap;\">&nbsp</td><td style=\"white-space: nowrap;\">";
 	var content = '<table><tr><td>';
    
	for (var i in liveAnnotations){
		if  (liveAnnotations[i].buttonVisible=="true") {
		   	content += crew_imageButton(liveAnnotations[i].type, "Annotate " + liveAnnotations[i].type, liveAnnotations[i].button,
		               "Create " + liveAnnotations[i].type +" Annotation","crew_add_annotation('"+contentid+"','"+liveAnnotations[i].type+"')",100,100,0,-2);
		   	content += CELL_SEP;
		}
	}
	content+='</td></tr></table>';
	document.getElementById(contentid).innerHTML = content;
}

function crew_add_annotation(contentid,type,refersto,modifies){
 	var time=getTimeStamp();
 	var annotation;
 	if ((modifies!=null) && (modifies!="")) {
 		var modtime=modifies.split("_");
 		time=modtime[0];
 		annotation=localAnnotations[modifies].variables;
 	};
 	var CELL_SEP = "</td><td style=\"white-space: nowrap;\">&nbsp</td><td style=\"white-space: nowrap;\">";
  	var ROW_SEP = "</td></tr><tr><td>";
	var content="<h2>Annotate " + type + "</h2>";
	var textboxes="<table><tr><td>";
	var textareas="<table><tr><td>";
	var useareas=false;
	var boxsep="";
	var areasep="";
	var useboxes=false;
	var areaids=new Array();
	for (var i in liveAnnotations[type].content){
		if (liveAnnotations[type].content[i]=="text"){
			useboxes=true;
			textboxes+=boxsep;
			textboxes+=liveAnnotations[type].variables[i+".displayname"]+':</td><td><input type="text" id="'+i+'" ';
			if (annotation!=null) {
				value=annotation[i];
				if (value!=null)
					textboxes += 'value="'+value+'" ';
			}
			textboxes +='/>';
			boxsep=ROW_SEP;
		}
		if (liveAnnotations[type].content[i]=="textarea"){
			useareas=true;
			textareas+=areasep;
			textareas += liveAnnotations[type].variables[i+".displayname"]+ROW_SEP;
			textareas += '<textarea id="'+i+'">'
			if (annotation!=null) {
				value=annotation[i];
				if (value!=null)
					textareas += value;
			}
			textareas += '</textarea>';
			areaids.push(i); 
			areasep=ROW_SEP;
		}
		if (liveAnnotations[type].content[i]=="messageId"){
			content+='<input type="hidden" name="'+i+'" id="'+i+'" value="'+refersto+'" />';
		}
	}
	textboxes +='</td></tr></table>';
	textareas +='</td></tr></table>';
	content +='<table><tr><td>';
	content +='<img src="'+ liveAnnotations[type].button +'" />'
	content +='</td><td style="white-space: nowrap;">&nbsp</td><td style="white-space: nowrap;">';
	if (useboxes){
		content += textboxes + CELL_SEP;
	}	
	if (useareas){
		content += textareas;
	}
	content +='</td></tr></table><table><tr><td>';
    content += crew_imageButton("submit", "Submit" , null,
            "Create","crew_submit_annotation('"+contentid+"','"+type+"','"+time+"')",100,30,0,-2);
 	content += CELL_SEP;
    content += crew_imageButton("cancel", "Cancel" , null,
            "Cancel","crew_buttonbox('"+contentid+"')",100,30,0,-2);
	content += CELL_SEP;
	content +='<input type="radio" id="privacybox" name="privacy" value="public" checked>public</input>&nbsp;';
	content += CELL_SEP;
	content +='<input type="radio" id="privacybox" name="privacy" value="private">private</input>';
	content +='</td></tr></table>';
	content +='<input type="hidden" id="modifyId" name="modifyid" value="'+modifies+'"/>';
	document.getElementById(contentid).innerHTML = content;
	if (useareas){
		var item=document.getElementById('messages');
		var inboxw=parseInt(item.style.width)-90;
		if (useboxes){
			inboxw=parseInt(item.style.width)-350;
		}
		for (var j in areaids){
			setSize(areaids[j],inboxw+'px','70px');
		}
	}	
	dofocus();
}

function crew_submit_annotationOnReturn(e, contentid, type, time) {
    if (e.keyCode == 13) {getNamedItem
        crew_submit_annotation(contentid,type,time);
        return false;
    }
    return true;
}

function crew_submit_annotation(contentid,type,time){
	var privacy = document.getElementById('privacybox').value;
    var modifyid = document.getElementById('modifyId').value;
    var inputvalue="";
 
    request = newRequest();
    request.open("POST", "SendMessage.jsp", false);
    request.setRequestHeader('Content-Type',
                             'application/x-www-form-urlencoded');
    var post_text = "timeStamp=" + encodeURIComponent(time);
    post_text += "&liveAnnotationType=" + encodeURIComponent(type);
    if ((privacy!=null)&&(privacy!=""))
    {
    	post_text += "&privacy="+encodeURIComponent(privacy);
    }else{
    	post_text += "&privacy="+encodeURIComponent("public");
    }
    if ((modifyid!=null)&&(modifyid!="")&&(modifyid!="undefined"))
    {
    	post_text += "&edits="+encodeURIComponent(modifyid);
    }
	for (var i in liveAnnotations[type].content){
		inputvalue = document.getElementById(i);
		if (inputvalue && (inputvalue.value!="")){
			post_text +="&"+encodeURIComponent(i)+"="+encodeURIComponent(inputvalue.value);
		}
	}   
    request.send(post_text);
    if (request.status != 200) {
        alert("Error sending message.");
    } 
    crew_buttonbox(contentid);
}

function format_annotation(contentid,colour,date,author,annotation,text,modifies,allowedit){
	var data="";
	var name="";
	var url="";
	var email="";
	var icons="";
	var type=annotation.getElementsByTagName("liveAnnotationType")[0].firstChild.data;
	if (allowedit){
		localAnnotations[modifies]=new Object();
		localAnnotations[modifies].variables=new Array();
		for (var i in liveAnnotations[type].content){
			value=annotation.getElementsByTagName(i);
			if (value){
				var val="";
				if (value.length>0){
					val=value[0].firstChild.data;
				}
				localAnnotations[modifies].variables[i]=val;
			}
		}
		icons = crew_imageButton("edit", "Edit" , "../img/small/edit.png",
	            "Edit","crew_add_annotation('"+contentid+"','"+type+"','"+modifies+"','"+modifies+"')",30,30,0,-2);
		icons+="</td><td>";
	}
	icons += crew_imageButton("Response", "Respond" , "../img/small/response.png",
            "Edit","crew_add_annotation('"+contentid+"','Response','"+modifies+"')",30,30,0,-2);
	return "<table><tr><td>"+date+"</td><td>"+icons+"</td><td>"+author+":&nbsp;<span style='color: " + colour + ";'><img src='"+liveAnnotations[type].thumbnail+"' alt='"+type+"' />&nbsp;"+ text +"</span></td></tr></table>";
}

function getTimeStamp() {
	var timeStamp=0;
    request = newRequest();
    request.open("POST", "getTime.jsp", false);
    request.setRequestHeader('Content-Type',
                             'application/x-www-form-urlencoded');
    request.send("time=now");
    if (request.status != 200) {
        alert("Error sending message.");
    } else {
    	timeStamp=request.responseText.trim();
    }
	return timeStamp;
}
