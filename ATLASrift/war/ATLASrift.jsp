<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>ATLASrift - VR ATLAS event viewer</title>

<link rel="stylesheet" href="static/ilija.css" type="text/css">
<link rel="stylesheet" type="text/css"
	href="//code.jquery.com/ui/1.10.3/themes/redmond/jquery-ui.css">

<link rel="stylesheet" type="text/css"
	href="//cdn.datatables.net/plug-ins/725b2a2115b/integration/jqueryui/dataTables.jqueryui.css">

<script type="text/javascript" language="javascript"
	src="//code.jquery.com/jquery-1.11.2.min.js"></script>
<script src="http://code.jquery.com/ui/1.11.3/jquery-ui.js"></script>


<script type="text/javascript" language="javascript"
	src="//cdn.datatables.net/1.10.5/js/jquery.dataTables.min.js"></script>

<script src="http://code.highcharts.com/highcharts.js"></script>
<script src="http://code.highcharts.com/modules/heatmap.js"></script>
<script src="http://code.highcharts.com/modules/exporting.js"></script>

<script
	src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false"></script>

<script src="static/dynamic.js"></script>
</head>
<body>


		<div class="mainheading">
			<a href="https://atlasrift.web.cern.ch/"> <img
				border="0" src="static/Ar_logo.png" width="17%" alt="ATLASrift main page">
			</a>
			<div id="maintitle">ATLASrift</div>
		</div>


		<div id="tabs" style="height: 100%; width: 100%;">
			<ul>
				<li><a href="#tabs-13">Visitors</a></li>
				<li><a href="#tabs-12">Configurations</a></li>
			</ul>
			<div id="tabs-13" style="height: 100%; width: 100%;">
				<div class="gmap" id="map-atlasrift"></div>
			</div>
			<div id="tabs-12" style="height: 100%; width: 100%;">
				<div class="gmap" id="configurations"></div>
			</div>
		</div>



	<div id="loading" style="display: none">
		<br> <br>Loading data. Please wait...<br> <br> <img
			src="static/wait_animated.gif" alt="loading" />
	</div>


</body>
</html>