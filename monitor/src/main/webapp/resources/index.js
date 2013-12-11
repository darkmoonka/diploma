/**
 * Initialize list on index page
 */

//jq( document ).ready(function()
//{
////	jq.ajax(
////			{
////				url : "init",
////				type : "GET",
////				success : function(data)
////				{
////					var list = JSON.parse(data);
//////					console.log(data);
//////					console.log(list);
////					
////					var agentNamesDiv = jq("#agentNames");
////					agentNamesDiv.html("<ul>");
////					jq.each(list, function(intex, item) 
////					{
////						agentNamesDiv.append("<li><a href=\"agentDetail/" + item[1] + "\">" + item[0] + "</a></li>");
//////						agentNamesDiv.append("<li>2" + item + "</li>");
////						console.log(item[1]);
////					});
////					agentNamesDiv.append("</ul>");
////					
//////					for(var i = 0; i < data.length; i++)
//////						console.log(data[0][i]);
////				}
////			});
//				
////	console.log("Test");
////	var test = $test;
////	console.log(test);
//});

getReport = function(address) {
	
	console.log("Loading agent with mac: " + address);
	jq.ajax(
		{
			url : "home",
			type : "GET",
			success : function(data)
			{
				console.log(data);
//				var list = JSON.parse(data);
////				console.log(data);
////				console.log(list);
//				
//				var agentNamesDiv = jq("#agentNames");
//				agentNamesDiv.html("<ul>");
//				jq.each(list, function(intex, item) 
//				{
//					agentNamesDiv.append("<li><a href=\"agentDetail/" + item[1] + "\">" + item[0] + "</a></li>");
////					agentNamesDiv.append("<li>2" + item + "</li>");
//					console.log(item[1]);
//				});
//				agentNamesDiv.append("</ul>");
				
//				for(var i = 0; i < data.length; i++)
//					console.log(data[0][i]);
			}
		});
};