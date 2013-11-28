window.setInterval(refresh, 1000);

var chartData = [];
var isLoaded = false;
var attributes = [];

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
							for ( var j = 0; j < table[0].length; j++)
							{
								var temp = {};
								for ( var key in table[0][j])
								{
									if (j == 0)
										attributes.push(key);

									temp[key] = table[0][j][key];

								}
								chartData.push(temp);
							}
							// console.log(chartData);
							isLoaded = true;
							drawChart();
							// console.log(attributes);
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
		// var valueAxis1 = new AmCharts.ValueAxis();
		// valueAxis1.axisColor = "#FF6600";
		// valueAxis1.axisThickness = 2;
		// valueAxis1.gridAlpha = 0;
		// chart.addValueAxis(valueAxis1);
		//
		// // second value axis (on the right)
		// var valueAxis2 = new AmCharts.ValueAxis();
		// valueAxis2.position = "right"; // this line makes the axis to appear
		// on
		// // the right
		// valueAxis2.axisColor = "#FCD202";
		// valueAxis2.gridAlpha = 0;
		// valueAxis2.axisThickness = 2;
		// chart.addValueAxis(valueAxis2);

		// third value axis (on the left, detached)
		var offset = 0;
		for ( var i = 0; i < chartData.length - 1; i++)
		{
			// csak az int típusúakat jeleníti meg
			var item = attributes[i];
			var testData = chartData[i][item];
			if (testData % 1 === 0 && item != "id" && testData != 0)
			{
				var color = getRandomColor();

				valueAxis = new AmCharts.ValueAxis();
				valueAxis.offset = offset; // this line makes the axis to
				// appear
				// detached
				// from plot area
				valueAxis.integersOnly = true;
				valueAxis.gridAlpha = 0;
				valueAxis.axisColor = color;
				valueAxis.axisThickness = 2;
				chart.addValueAxis(valueAxis);

				var graph = new AmCharts.AmGraph();
				graph.valueAxis = valueAxis; // we have to indicate which
				// value
				// axis
				// should be used

				graph.title = item;
				graph.valueField = item;
				graph.bullet = "round";
				graph.hideBulletsCount = 30;
				graph.bulletBorderThickness = 1;
				graph.lineColor = color;
				graph.legendColor = color;
				chart.addGraph(graph);
				offset += 70;
			}
		}

		// console.log(chartData);

		// GRAPHS
		// first graph
		for ( var i = 0; i < chartData.length - 1; i++)
		{

		}

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

// this method is called when chart is first inited as we listen for
// "dataUpdated" event
function zoomChart()
{
	// different zoom methods can be used - zoomToIndexes, zoomToDates,
	// zoomToCategoryValues
	chart.zoomToIndexes(10, 20);
}

function getRandomColor()
{
	var letters = '0123456789ABCDEF'.split('');
	var color = '#';
	for ( var i = 0; i < 6; i++)
	{
		color += letters[Math.round(Math.random() * 15)];
	}
	return color;
}
