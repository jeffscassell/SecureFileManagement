package com.sfm;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/DeleteServlet")
public class DeleteServlet extends HttpServlet
{
	HttpSession session;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		session = request.getSession();

		// check for logged-in status
		if(session.getAttribute("username") != null)
		{
			String fileName = request.getParameter("deleteName"); // retrieve passed filename from form
			UserDAO dao = new UserDAO();
			User user = dao.getUser((String) session.getAttribute("username"));

			// check for errors
			if(!user.hasDeletePermission())
			{
				setFeedback("DELETE PERMISSION DENIED");
				user.logError("attempted to delete without permission");
			}
			else if(!isValidInput(fileName))
				setFeedback("Input cannot contain: \\ / : * ? < > |");
			else if(fileName.equals(""))
				setFeedback("No file was selected to delete.");

			// if no errors were found, process request
			if(session.getAttribute("feedback") == null)
			{
				// make sure user has selected something
				session.setAttribute("fileList", user.getFileList()); // re-attach user's file list to session for modification

				// assign returned feedback to session feedback attribute
				setFeedback(dao.deleteFile(user, fileName));
			} // end if no feedback
		} // end if logged-in

		response.sendRedirect("dashboard.jsp");
	}



	// very basic input validation -- could improve with more involved regex
	private boolean isValidInput(String input)
	{
		String regex = "^.*[\\/:\\*?<>\\|].*$"; // searches for the characters: \ / : * ? < > |

		return !input.matches(regex);
	}



	private void setFeedback(String feedback)
	{ session.setAttribute("feedback", feedback); }
}
