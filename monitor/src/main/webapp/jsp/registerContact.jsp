<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<!-- <link rel="stylesheet" href="css/bootstrap.css" type="text/css" /> -->
<!-- <link rel="stylesheet" href="css/bootstrap.min.css" type="text/css" /> -->
<!-- <link rel="stylesheet" href="css/bootstrap-theme.css" type="text/css" /> -->
<!-- <link rel="stylesheet" href="css/bootstrap-theme.min.css" type="text/css" /> -->
<!-- <link rel="stylesheet" href="css/design.css" type="text/css" /> -->
<!-- <link rel="stylesheet" href="css/list.css" type="text/css" /> -->

</head>
<body>
		<div id="register">
		<h1>Please provide credentials to register new contact</h1>
		<form action="registerContact" method="post">
			<p>
				<label>Username</label>
				<input type="text" id="username"  name="username"/> 
			</p>
			
			<p>
				<label>E-mail</label>
				<input type="text" id="email" name="email" /> 
			</p>
			
			<p>
				<label>Password</label>
				<input type="password" id="password" name="password" />  
			</p>
			
			<p>
				<label>Retype password</label>
				<input type="password" id="password2" name="password2"/>  
			</p>
			
			<p>
				<input type="submit" value="Register user"/> 
			</p>
		</form>
	</div>
</body>
</html>
