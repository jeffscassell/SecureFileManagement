package com.sfm;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;


@WebServlet("/UploadServlet")
@MultipartConfig(maxFileSize=1024*64) // 64 KB | 1024*1024*15 = 15 MB
public class UploadServlet extends HttpServlet
{
	HttpSession session;
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		// retrieve current user and file list
		session = request.getSession();
		
		// check for logged-in status
		if(session.getAttribute("username") != null)
		{
			UserDAO dao = new UserDAO();
			User user = dao.getUser((String) session.getAttribute("username"));
			Collection<Part> fileParts = null;
			
			// error checking
			if(!user.hasUploadPermission())
			{
				setFeedback("UPLOAD PERMISSION DENIED");
				user.logError("attempted to download without permission");
			}

			// check file size first
			try
			{
				fileParts = request.getParts(); // receive all uploading files' parts (each file is a "part")
			}
			catch (Exception e)
			{
				setFeedback("File size limited exceeded (64 KB)");
			}

				// if no errors were found, process request
			if(session.getAttribute("feedback") == null)
			{
				session.setAttribute("fileList", user.getFileList()); // attach user's file list to session, so modifying the user object modifies the session attribute
				LinkedList<String> filesUploaded = new LinkedList<>(); // linked list to store file names that are uploaded successfully
				session.setAttribute("filesUploaded", filesUploaded); // attach list of successfully uploaded files to session

				// attach returned feedback to session feedback attribute
				setFeedback(dao.uploadFile(user, fileParts, filesUploaded));
			} // end if no feedback
		} // end if logged-in

		response.sendRedirect("dashboard.jsp");
	}
	
	
	
	private void setFeedback(String feedback)
	{ session.setAttribute("feedback", feedback); }
}
