<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<link rel="stylesheet" href="css/design.css" type="text/css" />
</head>
<body>
	<h1>Reports</h1>
<body>

	<div class="reports">

		<c:if test="${!empty reportList}">
			<table>
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
</body>

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<script type="text/javascript" src="resources/JSController.js"></script>
<script type="text/javascript">
	var jq = jQuery.noConflict();
</script>
</html>
