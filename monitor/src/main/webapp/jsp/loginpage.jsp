<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Login</title>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<script type="text/javascript">
	var jq = jQuery.noConflict();
</script>
<!-- 	<link href="css/index.css" rel="stylesheet" type="text/css"/> -->
<link href="../css/bootstrap.css" rel="stylesheet" type="text/css" />
</head>
<body>
	<div class="container">
		<div class="row"></div>
		<div class="row">
			<div class="col-lg-4 col-md-3 col-sm-8">
				<h2 class="form-signin-heading">Login</h2>
				<div class="col-lg-10 col-md-12 col-sm-12">
					<div id="login-error">${error}</div>
					<form action="/monitor/j_spring_security_check" method="post">

						<div class="form-group">
							<input class="form-control" id="j_username" name="j_username" type="text" value="" placeholder="Username">
						</div>
						<div class="form-group">
							<input class="form-control" id="j_password" name="j_password" type="password" value="" placeholder="Password">
						</div>

						<input type="submit" value="Login" class="btn btn-primary" />
						<!-- 					<a href="register">Register</a> -->
					</form>
				</div>
			</div>
			<div class="col-lg-1 col-md-0 col-sm-0"></div>
			<div class="col-lg-7 col-md-8 col-sm-12">
				<div id="register">
					<h2 class="form-signin-heading">Please provide credentials to register new contact</h2>
					<div class="col-lg-8 col-md-8 col-sm-10">
						<form action="registerContact" method="post">
							<div class="form-group">
								<input class="form-control" type="text" id="name" name="name" placeholder="Name">
							</div>

							<div class="form-group">
								<input class="form-control" type="text" id="username" name="username" placeholder="Username">
							</div>

							<div class="form-group">
								<input class="form-control" type="text" id="email" name="email" placeholder="E-mail">
							</div>

							<div class="form-group">
								<input class="form-control" type="text" id="phone" name="phone" placeholder="Phone number">
							</div>
							<div class="form-group">
								<input class="form-control" type="password" id="password" name="password" placeholder="Password">
							</div>

							<div class="form-group">
								<input class="form-control" type="password" id="password2" name="password2" placeholder="Retype password">
							</div>

							<input type="submit" value="Register user" class="btn btn-info" />

						</form>
					</div>
				</div>

			</div>
		</div>




	</div>
</body>

</html>