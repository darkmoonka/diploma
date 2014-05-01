<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<link rel="stylesheet" href="../css/bootstrap.css" type="text/css" />
<!-- <link rel="stylesheet" href="../css/design.css" type="text/css" /> -->
<link rel="stylesheet" href="../resources/dataTable/css/demo_table.css" type="text/css" />
<link rel="stylesheet" href="../css/reports.css" type="text/css" />
</head>
<body>
	<!-- 	<header> -->
	<%-- 	<h1>Reports for ${name}</h1> --%>
	<!-- 	</header> -->
	<div class="container">
		<div class="row">
			<div class="col-lg-12 col-md-12 col-sm-12 panel panel-default">
				<div class="panel-heading">Reports for ${name}</div>
				<div class="panel-body">
					<div class="scrollDiv">
						<div id="chartdiv"></div>
					</div>
					<div id="checkboxdiv"></div>
					<div id="reportTableDiv" class="scrollDiv">
						<table class="table" id="reportTable">
							<thead id="reportTableHead"></thead>
							<tbody id="reportTableBody"></tbody>
						</table>
					</div>
				</div>
			</div>
		</div>

		<!-- 		<div class="row"> -->
		<!-- 			<div class="col-lg-12 col-md-12 col-sm-12 panel panel-default" id="checkboxdiv"> -->
		<!-- 			</div> -->

		<!-- 			<div class="col-lg-12 col-md-12 col-sm-12 scrollDiv" id="reportTableDiv"> -->
		<!-- 								<div class="panel panel-default"> -->
		<!-- 									<div class="panel-body"> -->
		<!-- 				<table class="table" id="reportTable"> -->
		<!-- 					<thead id="reportTableHead"></thead> -->
		<!-- 					<tbody id="reportTableBody"></tbody> -->
		<!-- 				</table> -->
		<!-- 			</div> -->
		<!-- 		</div> -->
	</div>

</body>

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<script type="text/javascript">
	var address = '${address}';
	console.log("Refresh reports for: " + address);
</script>
<script type="text/javascript">
	var jq = jQuery.noConflict();
</script>
<script src="../resources/amcharts.js" type="text/javascript"></script>
<script src="../resources/serial.js" type="text/javascript"></script>
<script src="../resources/dataTable/js/jquery.dataTables.min.js" type="text/javascript"></script>
<script src="../resources/report.js" type="text/javascript"></script>
</html>



<%-- 				<c:if test="${!empty reportList}"> --%>
<!-- 					<table> -->
<!-- 						<thead> -->
<!-- 							<tr> -->
<!-- 								<th>Id</th> -->
<!-- 								<th>Size of memory(MB)</th> -->
<!-- 								<th>Free memory(%)</th> -->
<!-- 								<th>Number of cpu cores</th> -->
<!-- 								<th>Cpu frequency</th> -->
<!-- 								<th>Vendor of Cpu</th> -->
<!-- 								<th>Size of storage(GB)</th> -->
<!-- 								<th>Size of free storage(GB)</th> -->
<!-- 								<th>Architecture</th> -->
<!-- 								<th>OS name</th> -->
<!-- 								<th>OS version</th> -->
<!-- 								<th>Process count</th> -->
<!-- 								<th>Time stamp</th> -->
<!-- 							</tr> -->
<!-- 						</thead> -->
<%-- 					<c:forEach items="${reportList}" var="report"> --%>
<!-- 		    			<tr> -->
<%-- 							<td>${report.id}</td> --%>
<%-- 							<td>${report.memorySizeMb}</td> --%>
<%-- 							<td>${report.memoryFreePercent}</td> --%>
<%-- 							<td>${report.cpuCoreNumber}</td> --%>
<%-- 							<td>${report.cpuFrequency}</td> --%>
<%-- 							<td>${report.cpuVendor}</td> --%>
<%-- 							<td>${report.storageSizeGb}</td> --%>
<%-- 							<td>${report.storageFreeGb}</td> --%>
<%-- 							<td>${report.architecture}</td> --%>
<%-- 							<td>${report.osName}</td> --%>
<%-- 							<td>${report.osVersion}</td> --%>
<%-- 							<td>${report.processCount}</td> --%>
<%-- 							<td>${report.timeStamp}</td> --%>
<!-- 						</tr> -->
<%-- 					</c:forEach> --%>
<!-- 					</table> -->
<%-- 				</c:if> --%>
