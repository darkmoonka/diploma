<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<link rel="stylesheet" href="../css/bootstrap.min.css" type="text/css" />
<link rel="stylesheet" href="../css/design.css" type="text/css" />
<link rel="stylesheet" href="../css/reports.css" type="text/css" />
</head>
<body>
	<header>
	<h1>Reports for ${name}</h1>
	</header>
	<div class="container">
		<div class="content">

			<div id="chartdiv"></div>
<!-- 			<div id="reportsdiv"></div> -->
			<div id="checkboxdiv">
				<form id="checkboxes"></form>
			</div>

			<div class="reports">
				<c:if test="${!empty reportList}">
					<table>
						<thead>
							<tr>
								<th>Id</th>
								<th>Size of memory(MB)</th>
								<th>Free memory(%)</th>
								<th>Number of cpu cores</th>
								<th>Cpu frequency</th>
								<th>Vendor of Cpu</th>
								<th>Size of storage(GB)</th>
								<th>Size of free storage(GB)</th>
								<th>Architecture</th>
								<th>OS name</th>
								<th>OS version</th>
								<th>Process count</th>
								<th>Time stamp</th>
							</tr>
						</thead>
					<c:forEach items="${reportList}" var="report">
		    			<tr>
							<td>${report.id}</td>
							<td>${report.memorySizeMb}</td>
							<td>${report.memoryFreePercent}</td>
							<td>${report.cpuCoreNumber}</td>
							<td>${report.cpuFrequency}</td>
							<td>${report.cpuVendor}</td>
							<td>${report.storageSizeGb}</td>
							<td>${report.storageFreeGb}</td>
							<td>${report.architecture}</td>
							<td>${report.osName}</td>
							<td>${report.osVersion}</td>
							<td>${report.processCount}</td>
							<td>${report.timeStamp}</td>
						</tr>
					</c:forEach>
					</table>
				</c:if>
			</div>
		</div>
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
<script src="../resources/report.js" type="text/javascript"></script>

</html>
