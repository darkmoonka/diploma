window.setInterval(refresh, 10000);

var chartData = [];
var isLoaded = false;
var attributes = [];
var address;
var showAttributes = [];

function refresh()
{
	jq.ajax(
	{
		url : "../agent/" + address,
		type : "GET",
		success : function(data)
		{
			table = jq.parseJSON(data);

			var reportTable = jq(".reports");
			reportTable.text("");

			var k = 0;
			reportTable.append("<table><tr>");
			for ( var key in table[0])
			{
				if (isLoaded && attributes[k] == key && showAttributes[k][key] == true)
					reportTable.append("<th>" + key + "</th>");
				else if(!isLoaded)
					reportTable.append("<th>" + key + "</th>");
				k++;
			}

			reportTable.append("</tr>");

			for ( var j = 0; j < table.length - 1; j++)
			{
				reportTable.append("<tr>");
				var i = 0;
				for ( var key in table[j])
				{
					if (isLoaded && attributes[i] == key && showAttributes[i][key] == true)
					{
						reportTable.append("<td>" + table[j][key] + "</td>");
					}
					else if(!isLoaded)
						reportTable.append("<td>" + table[j][key] + "</td>");
					
					i++;
				}
				reportTable.append("</tr>");

			}

			reportTable.append("</table>");
			reportTable.append("</br>");

			// chart
			if (!isLoaded)
			{
				for ( var j = 0; j < table.length; j++)
				{
					var temp =
					{};
					for ( var key in table[j])
					{
						if (j == 0)
							attributes.push(key);

						temp[key] = table[j][key];

					}
					chartData.push(temp);
				}
				isLoaded = true;
				drawChart();
				setShownAttributesDefault();
			}
		}
	});

}

function setShownAttributesDefault()
{
	var form = jq("#checkboxes");
	for ( var attr in attributes)
	{
		var temp =
		{};

		temp[attributes[attr]] = true;
		showAttributes.push(temp);
		var checked = "";
		if (temp[attributes[attr]])
			checked = "checked";
		form.append("<input type=\"checkbox\" " +
							"class=\"checkbox-instance\" " +
							"id=\"" + attributes[attr] + "\" "
							+ checked + 
							" onclick=\"onChange(" + attr + ")\">"
					+ "<span class=\"checkbox-attribute\">  " + attributes[attr] + "</span>" + "<br />");
	}
}

function onChange(key)
{
	if (showAttributes[key][attributes[key]] == false)
	{
		showAttributes[key][attributes[key]] = true;
	} else
	{
		showAttributes[key][attributes[key]] = false;
	}
	
	refresh();
}

jq(document).ready(refresh());

var chart;

function drawChart()
{
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
		categoryAxis.axisColor = "#DADADA";

		var offset = 0;
		for ( var i = 0; i < chartData.length - 1; i++)
		{
			var item = attributes[i];
			var testData = chartData[i][item];
			if (testData % 1 === 0 && item != "id" && testData != 0)
			{
				var color = getRandomColor();

				valueAxis = new AmCharts.ValueAxis();
				valueAxis.offset = offset;
				valueAxis.integersOnly = true;
				valueAxis.gridAlpha = 0;
				valueAxis.axisColor = color;
				valueAxis.axisThickness = 2;
				chart.addValueAxis(valueAxis);

				var graph = new AmCharts.AmGraph();
				graph.valueAxis = valueAxis; 
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

function zoomChart()
{
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
