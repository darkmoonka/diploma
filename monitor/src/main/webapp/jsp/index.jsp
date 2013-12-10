<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<link rel="stylesheet" href="css/list.css" type="text/css" />
</head>
<body>
	<h1>Reports</h1>
<body>

	<div id="content">


		<%-- 		<c:forEach var="agent" items=${agents}> --%>
		<ul>
			<c:forEach var="agent" items="${agents}">
				<li>
					<a href="reports/${agent.address}" class="listItem" onclick="setAddress('${agent.address}')">
<%-- 					<div class="listItem" onclick="getReport('${agent.address}')"> --%>
						<c:out value="${agent.name}"></c:out>
						<c:out value="${agent.address}"></c:out>
					</a>
				</li>
			</c:forEach>
		</ul>
	</div>

</body>

<!-- <script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script> -->
<script src="resources/jquery-1.9.1.min.js" type="text/javascript"></script>
<script type="text/javascript">
	var jq = jQuery.noConflict();
</script>

<script src="resources/json2.min.js" type="text/javascript"></script>
<script src="resources/index.js" type="text/javascript"></script>
<!-- <script src="resources/JSController.js" type="text/javascript"></script> -->

<!-- <script src="resources/json2.min.js" type="text/javascript"></script> -->
<!-- <script src="resources/amcharts.js" type="text/javascript"></script> -->
<!-- <script src="resources/serial.js" type="text/javascript"></script> -->
<!-- <script src="resources/init.js" type="text/javascript"></script> -->
</html>
