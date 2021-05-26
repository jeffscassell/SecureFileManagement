<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.util.LinkedList, java.util.ListIterator, com.sfm.User, com.sfm.UserDAO" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>User Management</title>
</head>
<body>


<%
// tells browser to prevent page from caching (prevents being able to press the back button once logged out)
	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
	response.setHeader("Pragma", "no-cache"); // HTTP 1.0
	response.setHeader("Expires", "0"); // proxies
	
	// attempt to get username from session. if it's empty, user is not logged in and returns to home page
	if(session.getAttribute("username") == null)
		response.sendRedirect("home.jsp");
	
	UserDAO dao = new UserDAO();
	LinkedList<User> userList = dao.getUserList();
	ListIterator<User> iterator = userList.listIterator();
%>

<form action="Logout" method="post">
	<button type="submit" value="Logout">Logout</button>
</form><br />

<table>
<thead>
<tr>
	<th></th>
	<th>Upload</th>
	<th>Download</th>
	<th>Delete</th>
</tr>
</thead>

<tbody>
<%
	while(iterator.hasNext())
	{
		User user = iterator.next();
		String upload = "Granted", download = "Granted", delete = "Granted";
		String denied = "<a style=\"color:red\">Denied</a>";
		
		if(!user.hasUploadPermission())
			upload = denied;
		if(!user.hasDownloadPermission())
			download = denied;
		if(!user.hasDeletePermission())
			delete = denied;
%>

<tr>
<form action="SetPermission" method="post">
	<input type=hidden name=username value="<%=user.getUsername()%>">
	<td><%=user.getUsername()%></td>
	<td><button type="submit" name="buttonUpload" value="<%=user.hasUploadPermission()%>"><%=upload%></button></td>
	<td><button type="submit" name="buttonDownload" value="<%=user.hasDownloadPermission()%>"><%=download%></button></td>
	<td><button type="submit" name="buttonDelete" value="<%=user.hasDeletePermission()%>"><%=delete%></button></td>
</form>
</tr>

<%
	}
%>
</tbody>
</table>

</body>
</html>