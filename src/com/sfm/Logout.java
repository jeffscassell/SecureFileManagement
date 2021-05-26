package com.sfm;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/Logout")
public class Logout extends HttpServlet
{
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// get current logged-in session and its user
		HttpSession session = request.getSession();
		
		// check for logged-in status
		if(session.getAttribute("username") != null)
		{
			UserDAO dao = new UserDAO();
			User user = dao.getUser((String) session.getAttribute("username"));

			// clear session objects and logged-in status
			session.invalidate(); // clear session

			// add logout to audit log
			user.logLogout();
		}
		
		response.sendRedirect("home.jsp"); // return to home page
	}

}
