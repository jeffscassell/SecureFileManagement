<!-- this is a directive tag and can be used for imports. can have multiple --> 
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@page import="java.io.File, java.util.LinkedList, java.util.ListIterator" %>
<!DOCTYPE html>
<html>
<head>
<style type="text/css">
<%@include file="css/dashboard.css" %>
</style>
  <link href='https://fonts.googleapis.com/css?family=Pacifico' rel='stylesheet' type='text/css'>
  <link rel="stylesheet" href="css/core.css">
<meta charset="ISO-8859-1">
<title>Welcome, ${username}</title>
</head>
<body>

       <!-- Upload, Download, Delete -->
<div align="center" class="fond">
    <div class="carre_couleur base_hov" style="background-color:#f8b334;">
      <div class="retract" style="background-color:#f8b334;"><img src="https://i.imgur.com/w2gNz55.png"></div>
      <div class="acced">
        <form action="UploadServlet" method="post" enctype="multipart/form-data" autocomplete="off">
		<td><input type="file" name="files" multiple></td>
		<td><input type="submit" value="Upload (64 KB)"></td>
		</form>
      </div>
    </div>
    <div class="carre_couleur base_hov" style="background-color:#2ecc71;">
      <div class="retract" style="background-color:#2ecc71;"><img src="https://i.imgur.com/oVBfqCY.png"></div>
      <div class="acced">
        <form action="DownloadServlet" method="get" autocomplete="off">
		<td><input type="text" name="downloadName"></td>
		<td><input type="submit" value="Download"></td>
	</form>
      </div>
    </div>
    <div class="carre_couleur base_hov" style="background-color:#e74c3c;">
      <div class="retract" style="background-color:#e74c3c;"><img src="https://i.imgur.com/LjOiWcf.png"></div>
      <div class="acced">
        <form action="DeleteServlet" method="post" autocomplete="off">
		<td><input type="text" name="deleteName"></td>
		<td><input type="submit" value="Delete"></td>
	</form>
      </div>
    </div>
</div>

<!-- check for login status - must be performed on every secure page! -->
<% // this is a scriptlet tag, and anything within will be placed in the service method (doPost/doGet) of the servlet
	// tells browser to prevent page from caching (prevents being able to press the back button once logged out)
	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
	response.setHeader("Pragma", "no-cache"); // HTTP 1.0
	response.setHeader("Expires", "0"); // proxies
	
	// attempt to get username from session. if it's empty, user is not logged in and returns to home page
	if(session.getAttribute("username") == null)
		response.sendRedirect("home.jsp");
%>

 <!-- logout Button -->
<div class="action-button">
<form action="Logout" method="post">
	<button type="submit" value="Logout">
   <img src="https://i.imgur.com/htELFZs.png"/>
</button>
	</form>
</div>

 <!-- Permissions -->
<div id="page-wrapper">
  <div class="container">
          <h2 style=color:#4DC3FA>Permissions & User</h2>
          <p
			style=color:White>User: <b> ${username}</b><br />
			Upload permission: ${upload}<br />
			Download permission: ${download}<br />
			Delete permission: ${delete}<br />
          </p>
	</div>
</div>

<div id="file-wrapper">
<div class="container">
      <div class="grid">
        <div class="desc" style=color:White>           
<!-- File feedback -->            
<%
	if(session.getAttribute("filesUploaded") != null) // if files were uploaded successfully
	{
		LinkedList<String> fileList = (LinkedList<String>) session.getAttribute("filesUploaded"); // ignore weird error. it's fine.
		
		out.print("<br>");
		
		while(fileList.size() > 0) // print uploaded files
			out.print("<b>" + fileList.remove() + " <a style=\"color:red\">was uploaded</a></b><br>");
		
		session.removeAttribute("filesUploaded"); // clear uploaded files
		
		if(session.getAttribute("feedback") != null) // if there was an error during upload
		{
			out.print("<a style=\"color:red\"><b>" + session.getAttribute("feedback") + "</b></a><br>"); // report respective error
			
			session.removeAttribute("feedback"); // clear feedback
		}
	}
	
	if(session.getAttribute("feedback") != null) // if user experienced an error when attempting an action
	{
		out.print("<br><a style=\"color:red\"><b>" + session.getAttribute("feedback") + "</b></a><br>"); // report respective error
		
		session.removeAttribute("feedback"); // clear error message
	}


	if(session.getAttribute("fileList") != null) // will always exist, but prevents server errors if going straight to the page (incorrectly) before file list is built
	{
		LinkedList<String> fileList = (LinkedList<String>) session.getAttribute("fileList"); // retrieve user's files from session
		ListIterator<String> iterator = fileList.listIterator(); // iterator to cycle through the list without modifying it
		
		out.print("<br><b>You currently have the following files:</b><br>");
		
		out.print("<pre>"); // <pre> is used to maintain text formatting (i.e., double spaces, which HTML truncates)
		
		while(iterator.hasNext())
			out.print(iterator.next() + "<br>");
		
		out.print("</pre>");
	}
%>

</div>
</div>
</div>
</div>
</body>
</html>