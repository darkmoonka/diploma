<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<body>
	<h2>Hello Worl3d!</h2>
<body>

	<c:if test="${!empty reportList}">
		<table>
			<c:forEach items="${reportList}" var="report">
				<tr>
					<th>Id</th>
					<th>Agent id</th>
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
		    <tr>
					<td>${report.id}</td>
					<td>${report.agent}</td>
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

</body>
</html>
