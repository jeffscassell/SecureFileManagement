package com.sfm;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/SetPermission")
public class SetPermission extends HttpServlet
{
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession session = request.getSession();
		boolean value = false;
		UserDAO dao = new UserDAO();
		User admin = dao.getUser((String) session.getAttribute("username"));
		User user = dao.getUser(request.getParameter("username"));
		
		if(request.getParameter("buttonUpload") != null)
		{
			value = Boolean.parseBoolean(request.getParameter("buttonUpload"));
			
			dao.setUserUpload(user, !value);
			admin.logChangedPermission(user, "upload", !value);
		}
		else if(request.getParameter("buttonDownload") != null)
		{
			value = Boolean.parseBoolean(request.getParameter("buttonDownload"));
			
			dao.setUserDownload(user, !value);
			admin.logChangedPermission(user, "download", !value);
		}
		else if(request.getParameter("buttonDelete") != null)
		{
			value = Boolean.parseBoolean(request.getParameter("buttonDelete"));
			
			dao.setUserDelete(user, !value);
			admin.logChangedPermission(user, "delete", !value);
		}
		
		//System.out.println((String) request.getParameter("buttonDownload"));
		
		response.sendRedirect("administrate.jsp");
	}

}
