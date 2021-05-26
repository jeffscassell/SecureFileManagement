package com.sfm;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/DownloadServlet")
public class DownloadServlet extends HttpServlet
{
	HttpSession session;


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		session = request.getSession(); // retrieve session

		// check for logged-in status
		if(session.getAttribute("username") != null)
		{
			UserDAO dao = new UserDAO();
			User user = dao.getUser((String) session.getAttribute("username"));
			String fileName = request.getParameter("downloadName"); // retrieve EXACT filename from text field

			// check for errors
			if(!user.hasDownloadPermission())
			{
				setFeedback("DOWNLOAD PERMISSION DENIED");
				user.logError("attempted to download without permission");
			}
			else if(!isValidInput(fileName))
				setFeedback("Input cannot contain: \\ / : * ? < > |");
			else if(fileName.equals(""))
				setFeedback("No file was selected to download.");

			// if everything passed without errors, process request
			if(session.getAttribute("feedback") == null)
			{
				// upcoming method needs context to parse file type to browser
				ServletContext context = getServletContext();

				// assign returned feedback to session feedback attribute
				setFeedback(dao.downloadFile(user, fileName, response, context));
			} // end if no feedback
		} // end if logged-in

		try
		{
			response.sendRedirect("dashboard.jsp");
		}
		catch (IllegalStateException e)
		{
			// if file is downloaded successfully, this redirect will throw an exception, but it doesn't hurt anything
		}
	}



	// very basic input validation -- could improve with more involved regex
	private boolean isValidInput(String input)
	{
		String regex = "^.*[\\/:\\*?<>\\|\"].*$"; // searches for the characters: \ / : * ? < > |

		return !input.matches(regex);
	}


	private void setFeedback(String feedback)
	{ session.setAttribute("feedback", feedback); }
}
