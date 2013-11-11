window.setInterval(refresh, 3000);

function refresh()
{
	console.log("0");
	jq.ajax(
	{
		url : "list",
		type : "GET",
		success : function(data)
		{
			document.body.innerHTML = "";
			var table = jq.parseJSON(data);

			console.log(data);
			document.write("<table><tr>");

			for ( var key in table[0])
			{
				document.write("<th>" + key + "</th>");
			}
			
			document.write("</tr>");

			for ( var i = 0; i < table.length; i++)
			{
				document.write("<tr>");
				for ( var key in table[i])
				{
					document.write("<td>" + table[i][key] + "</td>");
				}
				document.write("</tr>");
			}
			document.write("</tr></td></table>");
		}
	});
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
