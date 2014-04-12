window.setInterval(refresh, 100000); //TODO 10 secs

var chartData = [];
var isLoaded = false;
var attributes = [];
var address;
var showAttributes = [];
var table;
var reportTable;

function refresh()
{
	jq.ajax(
	{
		url : "../agent/" + address,
		type : "GET",
		success : function(data)
		{
			table = jq.parseJSON(data);
//			console.log(table);
			var reportTableHead = jq("#reportTableHead");
			var reportTableBody = jq("#reportTableBody");
			
			reportTableHead.empty();
			reportTableBody.empty();
			
			var k = 0;
			reportTableHead.append("<tr id='reportTableHeadRow'>");
			
			var reportTableHeadRow = jq("#reportTableHeadRow");
			for ( var key in table[0])
			{
				if (isLoaded && attributes[k] == key && showAttributes[k][key] == true)
					reportTableHeadRow.append("<th>" + key + "</th>");
				else if(!isLoaded)
					reportTableHeadRow.append("<th>" + key + "</th>");
				k++;
			}

			reportTableHead.append("</tr>");

			for ( var j = 0; j < table.length - 1; j++)
			{
				reportTableBody.append("<tr id='reportTableBodyRow" + table[j]['id'] + "'>");
				var reportTableBodyRow = jq("#reportTableBodyRow" + table[j]['id']);
				
				var i = 0;
				for ( var key in table[j])
				{
					if (isLoaded && attributes[i] == key && showAttributes[i][key] == true)
					{
						reportTableBodyRow.append("<td>" + table[j][key] + "</td>");
					}
					else if(!isLoaded)
						reportTableBodyRow.append("<td>" + table[j][key] + "</td>");
					
					i++;
				}
				reportTableBody.append("</tr>");
			}

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
			
			//TODO elcsesződik a header, ha kiveszünk egy oszlopot.
			//Valami olyasmi van mögötte, hogy nem kapunk mindig teljesen új datatable-t, hanem cacheli?
			//Ilyenkor nem kerülnek rá a th tag-ekre a datatable class-jai
//			if(reportTable != null) {
//				reportTable.fnClearTable();
//			}
			reportTable = null;
			reportTable = jq("#reportTable").dataTable();
			reportTable.fnDraw();
			//------------------------------------------
		}
	});
}

function setShownAttributesDefault()
{
	var form = jq("#checkboxdiv");
	for ( var attr in attributes)
	{
		var temp =
		{};

		temp[attributes[attr]] = true;
		showAttributes.push(temp);
		var checked = "";
		if (temp[attributes[attr]])
			checked = "checked";
			form.append(
				"<label class='checkbox-inline'>" +
				"<input type='checkbox' id='" + attributes[attr] + "' "
							+ checked + 
							" onclick='onChange(" + attr + ")'>"
					+ "" + attributes[attr] + "</label>");
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
