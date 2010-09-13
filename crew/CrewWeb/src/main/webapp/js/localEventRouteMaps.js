var map;
var infowindow;
var marker;
var listener;
var controlSPText;
var controlWP1Text;
var controlWP2Text;
var controlOffText;

function initializeMap(id,startpoint) {
    var latlng = new google.maps.LatLng(53.80065082633023, -4.06494140625);
    var mapOptions = {
        zoom:5,
        center: latlng,
        draggableCursor: 'pointer',
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById(id), mapOptions);

  // Create the DIV to hold the 'Add location coords' control and call the switchOnLatLngControl() constructor
  // passing in this DIV.
  var addCoordsControlDiv = document.createElement('DIV');
  var control = new addCoordsControl(addCoordsControlDiv, map, startpoint);

  map.controls[google.maps.ControlPosition.TOP_RIGHT].push(addCoordsControlDiv);

  // Create the DIV to hold the lat/lng off control and call the switchOffLatLngControl() constructor
  // passing in this DIV.
  var latLngOffControlDiv = document.createElement('DIV');
  var latLngOffControl = new switchOffLatLngControl(latLngOffControlDiv, map);

  map.controls[google.maps.ControlPosition.TOP_RIGHT].push(latLngOffControlDiv);

}

function toggleMap(id,startpoint) {
    var element = document.getElementById(id);
    if (element.style.display != 'none') {
        element.style.display = 'none';
    } else {
        element.style.display = '';
        initializeMap(id,startpoint);
    }
}

function addCoordsControl(controlDiv, map, startpoint) {

    // Set CSS styles for the DIV containing the control
    // Setting padding to 5 px will offset the control
    // from the edge of the map
    controlDiv.style.padding = '5px';

    // Create div for start point listener
    var addSPUI = document.createElement('DIV');
    addSPUI.style.backgroundColor = 'white';
    addSPUI.style.borderStyle = 'solid';
    addSPUI.style.borderWidth = '2px';
    addSPUI.style.cursor = 'pointer';
    addSPUI.style.textAlign = 'center';
    addSPUI.title = 'Click to add coords of Start Point';
    controlDiv.appendChild(addSPUI);

    // Set CSS for the control interior
    controlSPText = document.createElement('DIV');
    controlSPText.style.fontFamily = 'Arial,sans-serif';
    controlSPText.style.fontSize = '12px';
    controlSPText.style.paddingLeft = '4px';
    controlSPText.style.paddingRight = '4px';
    controlSPText.innerHTML = 'Add Start Point coords';
    addSPUI.appendChild(controlSPText);

    // Setup the event listener that will allow switching on of the click for lat/lng function
    google.maps.event.addDomListener(addSPUI, 'click', function() {
        setStartPointListener(startpoint);
    });

    // Create div for waypoint1 listener
    var addWP1UI = document.createElement('DIV');
    addWP1UI.style.backgroundColor = 'white';
    addWP1UI.style.borderStyle = 'solid';
    addWP1UI.style.borderWidth = '2px';
    addWP1UI.style.cursor = 'pointer';
    addWP1UI.style.textAlign = 'center';
    addWP1UI.title = 'Click to add coords of waypoint 1';
    controlDiv.appendChild(addWP1UI);

    // Set CSS for the control interior
    controlWP1Text = document.createElement('DIV');
    controlWP1Text.style.fontFamily = 'Arial,sans-serif';
    controlWP1Text.style.fontSize = '12px';
    controlWP1Text.style.paddingLeft = '4px';
    controlWP1Text.style.paddingRight = '4px';
    controlWP1Text.innerHTML = 'Add Waypoint 1 coords';
    addWP1UI.appendChild(controlWP1Text);

    // Setup the event listener that will allow switching on of the click for lat/lng function
    google.maps.event.addDomListener(addWP1UI, 'click', function() {
        setWaypoint1Listener(startpoint);
    });

    // Create div for waypoint2 listener
    var addWP2UI = document.createElement('DIV');
    addWP2UI.style.backgroundColor = 'white';
    addWP2UI.style.borderStyle = 'solid';
    addWP2UI.style.borderWidth = '2px';
    addWP2UI.style.cursor = 'pointer';
    addWP2UI.style.textAlign = 'center';
    addWP2UI.title = 'Click to add coords of waypoint 2';
    controlDiv.appendChild(addWP2UI);

    // Set CSS for the control interior
    controlWP2Text = document.createElement('DIV');
    controlWP2Text.style.fontFamily = 'Arial,sans-serif';
    controlWP2Text.style.fontSize = '12px';
    controlWP2Text.style.paddingLeft = '4px';
    controlWP2Text.style.paddingRight = '4px';
    controlWP2Text.innerHTML = 'Add Waypoint 2 coords';
    addWP2UI.appendChild(controlWP2Text);

    // Setup the event listener that will allow switching on of the click for lat/lng function
    google.maps.event.addDomListener(addWP2UI, 'click', function() {
        setWaypoint2Listener(startpoint);
    });
}

function switchOffLatLngControl(controlDiv, map) {

    // Set CSS styles for the DIV containing the control
    // Setting padding to 5 px will offset the control
    // from the edge of the map
    controlDiv.style.padding = '5px';

    // Set CSS for the control border
    var addSPUI = document.createElement('DIV');
    addSPUI.style.backgroundColor = 'white';
    addSPUI.style.borderStyle = 'solid';
    addSPUI.style.borderWidth = '2px';
    addSPUI.style.cursor = 'pointer';
    addSPUI.style.textAlign = 'center';
    addSPUI.title = 'Click to disable display of latitude and longitude';
    controlDiv.appendChild(addSPUI);

    // Set CSS for the control interior
    controlOffText = document.createElement('DIV');
    controlOffText.style.fontFamily = 'Arial,sans-serif';
    controlOffText.style.fontSize = '12px';
    controlOffText.style.paddingLeft = '4px';
    controlOffText.style.paddingRight = '4px';
    controlOffText.style.fontWeight = 'bold';
    controlOffText.innerHTML = 'Lat/Long off';
    addSPUI.appendChild(controlOffText);

    // Setup the event listener that will allow switching off of the click for lat/lng function
    google.maps.event.addDomListener(addSPUI, 'click', function() {
        switchOffListeners();
    });
}

function setStartPointListener(startpoint) {
    disableAllListeners();
    listener = google.maps.event.addListener(map, 'click', function(event) {
            addStartPointCoords(event.latLng,startpoint);
    });
    controlOffText.style.fontWeight = 'normal';
    controlWP1Text.style.fontWeight = 'normal';
    controlWP2Text.style.fontWeight = 'normal';
    controlSPText.style.fontWeight = 'bold';
}

function setWaypoint1Listener(startpoint) {
    disableAllListeners();
    listener = google.maps.event.addListener(map, 'click', function(event) {
            addWaypoint1Coords(event.latLng,startpoint);
    });
    controlOffText.style.fontWeight = 'normal';
    controlSPText.style.fontWeight = 'normal';
    controlWP2Text.style.fontWeight = 'normal';
    controlWP1Text.style.fontWeight = 'bold';
}

function setWaypoint2Listener(startpoint) {
    disableAllListeners();
    listener = google.maps.event.addListener(map, 'click', function(event) {
            addWaypoint2Coords(event.latLng,startpoint);
    });
    controlOffText.style.fontWeight = 'normal';
    controlSPText.style.fontWeight = 'normal';
    controlWP1Text.style.fontWeight = 'normal';
    controlWP2Text.style.fontWeight = 'bold';
}

function switchOffListeners() {
    disableAllListeners();
    controlOffText.style.fontWeight = 'bold';
    controlSPText.style.fontWeight = 'normal';
    controlWP1Text.style.fontWeight = 'normal';
    controlWP2Text.style.fontWeight = 'normal';
}

function disableAllListeners() {
    if (listener != null) {
        google.maps.event.removeListener(listener);
    }
}

function addStartPointCoords(location,startpoint) {
    var latElement = "startPointLat" + startpoint;
    var lngElement = "startPointLong" + startpoint;
    document.getElementById('repositoryEventForm')[latElement].value = location.lat();
    document.getElementById('repositoryEventForm')[lngElement].value = location.lng();
}

function addWaypoint1Coords(location,startpoint) {
    var latElement = "waypointLat" + startpoint + "_1";
    var lngElement = "waypointLong" + startpoint + "_1";
    document.getElementById('repositoryEventForm')[latElement].value = location.lat();
    document.getElementById('repositoryEventForm')[lngElement].value = location.lng();
}

function addWaypoint2Coords(location,startpoint) {
    var latElement = "waypointLat" + startpoint + "_2";
    var lngElement = "waypointLong" + startpoint + "_2";
    document.getElementById('repositoryEventForm')[latElement].value = location.lat();
    document.getElementById('repositoryEventForm')[lngElement].value = location.lng();
}


