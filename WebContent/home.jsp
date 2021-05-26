<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<style type="text/css">
<%@include file="css/home.css" %>
</style>
<meta charset="ISO-8859-1">
<title>Welcome to SFM</title>
</head>
<body>

<%
	if(session.getAttribute("username") != null) // if user is already logged in, go to dashboard
	{
		String username = (String) session.getAttribute("username");
		
		if(username.equals("admin"))
			response.sendRedirect("administrate.jsp");
		else
			response.sendRedirect("dashboard.jsp");
	}
%>

<div class="wrapper fadeInDown">
  <div id="formContent">
    <!-- Tabs Titles -->
    <h2 class="active"> Sign In </h2>

    <!-- Icon -->
    <div class="fadeIn first">
      <img src="https://i.imgur.com/sxUNTWX.png" id="icon" alt="User Icon" />
    </div>
    
    <!-- Login Form -->
    <form action="Login" method="post">
      <input type="text" id="username" class="fadeIn second" name="username" placeholder="username">
      <input type="password" id="password" class="fadeIn third" name="password" placeholder="password">
      <input type="submit" class="fadeIn fourth" value="Log In">
    </form>
    
    <!-- Login Feedback -->
    <div id="formFooter">
      <a class="underlineHover" href="#"></a>
      <%
	if(session.getAttribute("feedback") != null)
	{
		out.print("<br><a style=\"color:red\"><b>" + session.getAttribute("feedback") + "</b></a>"); // print feedback to page
		session.removeAttribute("feedback"); // clear feedback
	}
%>

</div>
</div>
</div>
</body>
</html>