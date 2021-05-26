package com.deprecated;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;


@WebServlet("/add") // gives the web server a place to go when form action "add" is called - annotation
public class AddServlet extends HttpServlet
{
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException // still goes through service() method
	{
		int i = Integer.parseInt(req.getParameter("num1"));
		int j = Integer.parseInt(req.getParameter("num2"));
		int k = i + j;
		
		PrintWriter writer = res.getWriter();
		writer.println("result is: " + k);
		
		// get servlet-specific initialization parameter
		ServletConfig cfg = getServletConfig();
		String specific = cfg.getInitParameter("name");
		
		// get all-servlets initialization parameter
		ServletContext ctx = getServletContext();
		String general = ctx.getInitParameter("name");
	}
}
