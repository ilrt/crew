var map;
var infowindow;
var marker;
var listener;
var controlOnText;
var controlOffText;

function initializeMap() {
    var latlng = new google.maps.LatLng(53.80065082633023, -4.06494140625);
    var mapOptions = {
        zoom:5,
        center: latlng,
        draggableCursor: 'pointer',
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);

  // Create the DIV to hold the 'Add location coords' control and call the switchOnLatLngControl() constructor
  // passing in this DIV.
  var latLngOnControlDiv = document.createElement('DIV');
  var latLngOnControl = new switchOnLatLngControl(latLngOnControlDiv, map);

  map.controls[google.maps.ControlPosition.TOP_RIGHT].push(latLngOnControlDiv);

  // Create the DIV to hold the lat/lng off control and call the switchOffLatLngControl() constructor
  // passing in this DIV.
  var latLngOffControlDiv = document.createElement('DIV');
  var latLngOffControl = new switchOffLatLngControl(latLngOffControlDiv, map);

  map.controls[google.maps.ControlPosition.TOP_RIGHT].push(latLngOffControlDiv);

}

function toggleMap(id) {
    var element = document.getElementById(id);
    if (element.style.display != 'none') {
        element.style.display = 'none';
    } else {
        element.style.display = '';
        initializeMap();
    }
}

function switchOnLatLngControl(controlDiv, map) {

    // Set CSS styles for the DIV containing the control
    // Setting padding to 5 px will offset the control
    // from the edge of the map
    controlDiv.style.padding = '5px';

    // Set CSS for the control border
    var controlUI = document.createElement('DIV');
    controlUI.style.backgroundColor = 'white';
    controlUI.style.borderStyle = 'solid';
    controlUI.style.borderWidth = '2px';
    controlUI.style.cursor = 'pointer';
    controlUI.style.textAlign = 'center';
    controlUI.title = 'Click to insert latitude and longitude';
    controlDiv.appendChild(controlUI);

    // Set CSS for the control interior
    controlOnText = document.createElement('DIV');
    controlOnText.style.fontFamily = 'Arial,sans-serif';
    controlOnText.style.fontSize = '12px';
    controlOnText.style.paddingLeft = '4px';
    controlOnText.style.paddingRight = '4px';
    controlOnText.innerHTML = 'Add location coords';
    controlUI.appendChild(controlOnText);

    // Setup the event listener that will allow switching on of the click for lat/lng function
    google.maps.event.addDomListener(controlUI, 'click', function() {
        setLatLngListener();
    });
}

function switchOffLatLngControl(controlDiv, map) {

    // Set CSS styles for the DIV containing the control
    // Setting padding to 5 px will offset the control
    // from the edge of the map
    controlDiv.style.padding = '5px';

    // Set CSS for the control border
    var controlUI = document.createElement('DIV');
    controlUI.style.backgroundColor = 'white';
    controlUI.style.borderStyle = 'solid';
    controlUI.style.borderWidth = '2px';
    controlUI.style.cursor = 'pointer';
    controlUI.style.textAlign = 'center';
    controlUI.title = 'Click to disable display of latitude and longitude';
    controlDiv.appendChild(controlUI);

    // Set CSS for the control interior
    controlOffText = document.createElement('DIV');
    controlOffText.style.fontFamily = 'Arial,sans-serif';
    controlOffText.style.fontSize = '12px';
    controlOffText.style.paddingLeft = '4px';
    controlOffText.style.paddingRight = '4px';
    controlOffText.style.fontWeight = 'bold';
    controlOffText.innerHTML = 'Lat/Long off';
    controlUI.appendChild(controlOffText);

    // Setup the event listener that will allow switching off of the click for lat/lng function
    google.maps.event.addDomListener(controlUI, 'click', function() {
        disableLatLngListener();
    });
}

function setLatLngListener() {
    listener = google.maps.event.addListener(map, 'click', function(event) {
            showLatLng(event.latLng);
    });
    controlOffText.style.fontWeight = 'normal';
    controlOnText.style.fontWeight = 'bold';
}

function disableLatLngListener() {
    if (listener != null) {
        google.maps.event.removeListener(listener);
        controlOffText.style.fontWeight = 'bold';
        controlOnText.style.fontWeight = 'normal';
    }
}


function showLatLng(location) {
    document.getElementById('repositoryEventForm').latitude.value = location.lat();
    document.getElementById('repositoryEventForm').longitude.value = location.lng();
}


