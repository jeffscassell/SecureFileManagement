package com.sfm;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


// performs all the error checking before proceeding into the program


@WebServlet("/Login")
public class Login extends HttpServlet 
{
	HttpSession session;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// retrieve login credentials from form
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		// retrieve current session
		session = request.getSession();

		// debug any SSL/TLS issues
		//System.setProperty("javax.net.debug", "ssl");

		// force lower-case for proper passing to methods
		username = username.toLowerCase();
		
		// instantiate LDAP, Logger, Config and DAO objects for service checks
		// critical services checking is performed during login, with more routine checking performed as user interacts with the database
		LDAP ldap = new LDAP();
		UserDAO dao = new UserDAO();
		Config config = Config.getInstance();
		Logger logger = new Logger();

		// perform critical services check -- basically a POST
		if(!isValidInput(username) || !isValidInput(password))
			setFeedback("Username or password may only contain letters, numbers and underscore for 3-20 characters.");
		else if(!config.isLoaded())
			setFeedback("Configuration file error. Check console.");
		else if(!ldap.isAvailable())
			setFeedback("LDAP connection failed. Check console for details.");
		else if(!logger.isAvailable())
			setFeedback("Audit logs parent directory cannot be found. Ensure directory is correct in config file.");
		else if(!dao.isAvailable())
			setFeedback("Error connecting to SQL database. Check console for details.");
		else if(!ldap.isAuthenticated(username, password))
			setFeedback("Incorrect username/password.");
		
		// if critical checks pass, proceed
		if(session.getAttribute("feedback") == null)
		{
			User user = dao.getUser(username);

			if (user != null) // check if user was found in SQL database
			{
				session.setAttribute("username", user.getUsername()); // assign username to current session (this is the logged-in status verification)
				
				// user is assumed to have permission for a particular action. if they do not have a permission, it is altered. their permissions are attached to
				// session either way
				String upload = "Granted", download = "Granted", delete = "Granted";
				String denied = "<a style=\"color:red\">Denied</a>";
				
				if(!user.hasUploadPermission())
					upload = denied;
				if(!user.hasDownloadPermission())
					download = denied;
				if(!user.hasDeletePermission())
					delete = denied;
				
				session.setAttribute("upload", upload);
				session.setAttribute("download", download);	
				session.setAttribute("delete", delete);

				// retrieve user's list of files and assign to session
				session.setAttribute("fileList", user.getFileList()); // attach user's fileList to session so files can be interacted with in JSP pages with an import of the LinkedList class

				// add logged in entry to audit log
				user.logLogin();
			}
			else
				setFeedback("Error retrieving user from SQL database.");
		} // end if critical checks passed

		// if user is logged in, home.jsp will redirect them to dashboard
		response.sendRedirect("home.jsp");
	} // end doPost()
	
	
	
	// very basic input validation -- could improve with more involved regex
	private boolean isValidInput(String input)
	{
		String regex = "^\\w{3,20}$"; // input is 4-20 chars -- a-z, numbers, and underscore
		
		return input.matches(regex);
	}

	
	
	private void setFeedback(String feedback)
	{
		session.setAttribute("feedback", feedback);
	}
}
