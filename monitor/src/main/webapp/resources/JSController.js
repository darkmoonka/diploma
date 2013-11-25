window.setInterval(refresh, 1000);

var chartData = [];
var isLoaded = false;

function refresh()
{
	jq
			.ajax(
			{
				url : "list",
				type : "GET",
				success : function(data)
				{

					table = jq.parseJSON(data);

					var reportTable = jq(".reports");
					reportTable.text("");

					// [táblaindex][sorindex][attribútumindex]
					// [i][j][k] az i. tábla j. sorának k. attribútumának az
					// értéke

					for ( var i in table)
					{
						reportTable.append("<table><tr>");
						for ( var key in table[i][0])
						{
							reportTable.append("<th>" + key + "</th>");
						}

						reportTable.append("</tr>");

						for ( var j = 0; j < table[i].length; j++)
						{
							reportTable.append("<tr>");
							for ( var key in table[i][j])
							{
								reportTable.append("<td>" + table[i][j][key]
										+ "</td>");
							}
							reportTable.append("</tr>");
						}
						reportTable.append("</tr></td></table>");
						reportTable.append("</br>");

						// chart
						if (!isLoaded)
						{
							for ( var j = 0; j < table[1].length; j++)
							{
								var attributes = [];
								var values = [];
								var temp = {};
								for ( var key in table[1][j])
								{
									// reportTable.append("<td>" +
									// table[1][j][key]
									// + "</td>");

									attributes.push(key);
									values.push(table[1][j][key]);

									temp[key] = table[1][j][key];

								}
								chartData.push(temp);
							}
							console.log(chartData);
							isLoaded = true;
							drawChart();
						}
					}
				}
			});

}

var chart;

function drawChart()
{
	// generate some random data first
	// generateChartData();

	// SERIAL CHART
	if (chartData.length > 0)
	{
		chart = new AmCharts.AmSerialChart();
		chart.pathToImages = "http://www.amcharts.com/lib/3/images/";
		chart.dataProvider = chartData;
		chart.categoryField = "timestamp"; // timestamp

		// listen for "dataUpdated" event (fired when chart is inited) and call
		// zoomChart method when it happens
		chart.addListener("dataUpdated", zoomChart);

		// AXES
		// category
		var categoryAxis = chart.categoryAxis;
		// categoryAxis.parseDates = true; // as our data is date-based, we set
		// parseDates to true
		// categoryAxis.minPeriod = "ss"; // our data is daily, so we set
		// minPeriod to DD
		// categoryAxis.minorGridEnabled = true;
		categoryAxis.axisColor = "#DADADA";

		// first value axis (on the left)
		var valueAxis1 = new AmCharts.ValueAxis();
		valueAxis1.axisColor = "#FF6600";
		valueAxis1.axisThickness = 2;
		valueAxis1.gridAlpha = 0;
		chart.addValueAxis(valueAxis1);

		// second value axis (on the right)
		var valueAxis2 = new AmCharts.ValueAxis();
		valueAxis2.position = "right"; // this line makes the axis to appear on
		// the right
		valueAxis2.axisColor = "#FCD202";
		valueAxis2.gridAlpha = 0;
		valueAxis2.axisThickness = 2;
		chart.addValueAxis(valueAxis2);

		// third value axis (on the left, detached)
		valueAxis3 = new AmCharts.ValueAxis();
		valueAxis3.offset = 50; // this line makes the axis to appear detached
		// from plot area
		valueAxis3.gridAlpha = 0;
		valueAxis3.axisColor = "#B0DE09";
		valueAxis3.axisThickness = 2;
		chart.addValueAxis(valueAxis3);

		// GRAPHS
		// first graph
		var graph1 = new AmCharts.AmGraph();
		graph1.valueAxis = valueAxis1; // we have to indicate which value axis
		// should be used
		graph1.title = "red line";
		graph1.valueField = "processCount";
		graph1.bullet = "round";
		graph1.hideBulletsCount = 30;
		graph1.bulletBorderThickness = 1;
		chart.addGraph(graph1);

		// second graph
		// var graph2 = new AmCharts.AmGraph();
		// graph2.valueAxis = valueAxis2; // we have to indicate which value
		// axis
		// // should be used
		// graph2.title = "yellow line";
		// graph2.valueField = "table[0][0][0]";
		// graph2.bullet = "square";
		// graph2.hideBulletsCount = 30;
		// graph2.bulletBorderThickness = 1;
		// chart.addGraph(graph2);
		//
		// // third graph
		// var graph3 = new AmCharts.AmGraph();
		// graph3.valueAxis = valueAxis3; // we have to indicate which value
		// axis
		// // should be used
		// graph3.valueField = "table[0][0][0]";
		// graph3.title = "green line";
		// graph3.bullet = "triangleUp";
		// graph3.hideBulletsCount = 30;
		// graph3.bulletBorderThickness = 1;
		// chart.addGraph(graph3);

		// CURSOR
		var chartCursor = new AmCharts.ChartCursor();
		chartCursor.cursorPosition = "mouse";
		chart.addChartCursor(chartCursor);

		// SCROLLBAR
		var chartScrollbar = new AmCharts.ChartScrollbar();
		chart.addChartScrollbar(chartScrollbar);

		// LEGEND
		var legend = new AmCharts.AmLegend();
		legend.marginLeft = 110;
		legend.useGraphSettings = true;
		chart.addLegend(legend);

		// WRITE
		chart.write("chartdiv");
	}
};

// generate some random data, quite different range
function generateChartData()
{
	var firstDate = new Date();
	firstDate.setDate(firstDate.getDate() - 50);

	for ( var i = 0; i < 50; i++)
	{
		// we create date objects here. In your data, you can have date
		// strings
		// and then set format of your dates using chart.dataDateFormat
		// property,
		// however when possible, use date objects, as this will speed up
		// chart rendering.
		var newDate = new Date(firstDate);
		newDate.setDate(newDate.getDate() + i);

		var visits = Math.round(Math.random() * 40) + 100;
		var hits = Math.round(Math.random() * 80) + 500;
		var views = Math.round(Math.random() * 6000);

		chartData.push(
		{
			date : newDate,
			visits : visits,
			hits : hits,
			views : views
		});
	}
}

// this method is called when chart is first inited as we listen for
// "dataUpdated" event
function zoomChart()
{
	// different zoom methods can be used - zoomToIndexes, zoomToDates,
	// zoomToCategoryValues
	chart.zoomToIndexes(10, 20);
}

// var poiList = new Array();
// var map;
// var mapProp;
// var markers = new Array();
// var infoWindows = new Array();
//   
// function searchJSON() {
// var params= {
// name: jq("#name").val(),
// address: jq("#address").val(),
// type: jq("#type").val()
// };
//	
// jq(function() {
// jq.ajax({
// url: "/poi/search",
// type: "POST",
// data: JSON.stringify(params),
// contentType: "application/json; charset=utf-8",
// dataType: "json",
// beforeSend: function(x) {
// if (x && x.overrideMimeType) {
// x.overrideMimeType("application/j-son;charset=UTF-8");
// }
// jq("#result").replaceWith('<div id="result">Working...<br/>' +
// '<img src="resource/loading.gif" width="30px"/></div>');
// },
// success: function (data) {
// jq.extend(true, poiList, data); // Deep copy for usage outside the function
//				
// jq("#searchFieldSet").fadeOut("slow", function() { // Fade out the search
// field
// jq("#result").empty();
// jq("#result").append('<div id="list"> </div>' +
// '<div id="multimedia"> </div>');
//					
// jq("#list").hide(); // Needed fo the fade in effect
// jq("#multimedia").hide();
//					
// jq.each(poiList, function(index, listItem) { // Process result list items
// jq("#list").append(
// '<div class="listItem"><table>' +
// '<tr>' +
// '<td>' + listItem.name + '</td>' +
// '<td>' + listItem.address + '</td>' +
// '</tr><tr>' +
// '<td>' + listItem.type + '</td>' +
// '<td>' + listItem.rating + '</td>' +
// '</tr><tr>' +
// '<td><input type="button" value="Image"' +
// 'class="imageBtn" id="imageBtn' + index + '"/></td>' +
// '<td><input type="button" value="Video"' +
// 'class="videoBtn" id="videoBtn' + index + '"/></td>' +
// '</tr><tr>' +
// '<td><input type="button" value="Show on map"' +
// 'class="showOnMapBtn" id="showOnMapBtn' + index + '"/></td>' +
// '</tr>' +
// '</table></div>'
// );
// jq("#imageBtn" + index).bind("click", function(event) {
// imgBtnClick(listItem.imagePath);
// });
// jq("#videoBtn" + index).bind("click", function(event) {
// videoBtnClick(listItem.videoPath);
// });
// jq("#showOnMapBtn" + index).bind("click", function(event) {
// showOnMapBtnClick(listItem.name, listItem.latitude, listItem.longitude);
// });
// });
//					
//								
// jq("#list").fadeIn("slow"); //fade in the created list
//					
// var script = document.createElement("script");
// script.type = "text/javascript";
// script.src =
// "http://maps.googleapis.com/maps/api/js?key=AIzaSyCBgeK9uzOoF0DNa69eXp6i-nd9N7EZczg&sensor=false&callback=initialize";
// document.body.appendChild(script);
// jq("#multimedia").fadeIn("slow");
// });
// },
// error: function (data) {
// jq("#list").replaceWith('<div id="list">Error</div>');
// }
// });
// });
