var map;
var markers = [];
var infowindow;

$(document).ready(function() {

	$("#tabs").tabs({
		activate : function(event, ui) {
			if (ui.newPanel.index() == 13) { // ATLASrift map
				console.log("loading tab 13.");
				$("#loading").show();
				showATLASriftMap();
				$("#loading").hide();
			} else {

			}
		}
	});
	
	showATLASriftMap();
	
});

function showATLASriftMap() {

	$("#loading").show();
	var mapOptions = {
		zoom : 2,
		center : new google.maps.LatLng(20.0, 0.0)
	};
	map1 = new google.maps.Map(document.getElementById('map-atlasrift'),
			mapOptions);

	$.post("ATLASriftMonitor", {}, function(msg) {
		armarkers = [];
		for (var i = 0; i < msg.length; i++) {
			var rec = msg[i];
			console.log(rec);
			var marker = new google.maps.Marker({
				position : new google.maps.LatLng(parseFloat(rec.lat),
						parseFloat(rec.long)),
				map : map1,
				title : rec.city
			});

			// google.maps.event.addListener(marker, 'click',
			// function(){drawConnections(marker);});

			armarkers.push(marker);
		}
	});
	$("#loading").hide();
}

window.onload = function() {
	// alert("welcome");

}
