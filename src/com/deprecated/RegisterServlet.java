package com.deprecated;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet
{
	private String serverUrl = "";
	private String serverUser = "";
	private String serverPass = "";
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		//retrieve credentials
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		// connect to server
		
		// check to see if user is already registered
		
		// add new user
		
		// create new table for user
	}
}
