package com.sfm;

/*
 * database connection steps
 * 1. import java.sql.*
 * 2. a. load the driver (mySQL connector)	- Class.forName("Driver") 
 *    b. register the driver				- 
 * 3. create a Connection					- Connection con = DriverManager.getConnection(database credentials)
 * 4. create a Statement (container)		- PreparedStatement st = con.PrepareStatement(sql statement)
 * 5. execute the query						- ResultSet rs = st.executeQuery()
 * 6. process the results					- rs.getString("column")
 * 7. close (connection?)
 * 
 */

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;


// provides static helper methods
public class UserDAO // data-access object
{
	// load config file
	private SqlConfig config = Config.getInstance();
	
	// database settings
	private final String url = config.getSqlUrl();
	private final String dbUsername = config.getSqlUsername();
	private final String dbPassword = config.getSqlPassword();
	private Connection con;
	
	
	
	public User getUser(String username)
	{
		User user = new User();
		String sql = "select * from permissions where username = '" + username + "'"; // SQL statement to send to db
		
		try
		{
			connect();
			
			ResultSet rs = sendQuery(sql);
			
			if(rs.next()) // if user exists in permission table, build user
			{
				user.setUsername(rs.getString("username"));
				user.setUpload(rs.getBoolean("upload"));
				user.setDownload(rs.getBoolean("download"));
				user.setDelete(rs.getBoolean("del"));
				user.setFileList(getFiles(user));
			}
			else
				user = addUser(username); // add to permissions table and create user table
			
			rs.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
		return user; // success condition
	} // end getUser()
	
	
	
	public LinkedList<User> getUserList()
	{
		LinkedList<User> userList = new LinkedList<User>();
		String sql = "select * from permissions";
		
		try
		{
			connect();
			
			ResultSet rs = sendQuery(sql);
			
			while(rs.next())
			{
				User user = new User();
				
				if(rs.getString("username").equals("admin"))
					continue;
				
				user.setUsername(rs.getString("username"));
				user.setUpload(rs.getBoolean("upload"));
				user.setDownload(rs.getBoolean("download"));
				user.setDelete(rs.getBoolean("del"));
				user.setFileList(getFiles(user));
				
				userList.add(user);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
		return userList;
	}
	
	
	
	public void setUserUpload(User user, boolean permission)
	{
		setPermission(user, "upload", permission);
	}
	
	public void setUserDownload(User user, boolean permission)
	{
		setPermission(user, "download", permission);
	}
	
	public void setUserDelete(User user, boolean permission)
	{
		setPermission(user, "del", permission);
	}
	
	
	
	public String uploadFile(User user, Collection<Part> fileParts, LinkedList<String> filesUploaded)
	{
		String sql; // prepare string for use in enhanced for loop
		InputStream inputStream = null; // prepare stream for the uploading files
		String fileName = ""; // outside of the try clause so that the catch can use it if it needs to report the problem file

		try
		{
			connect();
			
			// send files to user's database table
			for(Part part : fileParts)
			{
				sql = "insert into " + user + " value(?, ?)"; // ??? does this need to be re-blanked every time? prepare a blank sql command
				fileName = part.getSubmittedFileName();
				
				// make sure user has selected something -- can't figure out a way to do this before DAO is called :(
				if(part.getSubmittedFileName() == "")
					return "No file was selected to upload.";
				
				PreparedStatement st = con.prepareStatement(sql); // prepare a blank statement (container for sql command) and affix sql command
				st.setString(1, fileName); // getName() retrieves parameter name (files) and getSubmittedFileName() retrieves the passed file name

				inputStream = part.getInputStream(); // grab input stream from uploading file
				st.setBlob(2, inputStream); // attach to SQL statement

				if(st.executeUpdate() > 0) // if file was successfully added to the database table (returns number of rows affected)
				{
					// add entry to audit log
					user.logUpload(fileName);

					filesUploaded.add(fileName); // add new filename to successful upload list
					user.addFile(fileName); // add to user's file list too
				}
				else
				{
					return "</a>" + fileName + "<a style=\"color:red\"> was not uploaded."; // I've never seen this reached, but who knows
				}

				st.close(); // close statement container
			} // end for
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "</a>" + fileName + "<a style=\"color:red\"> already exists in the database.";
		}
			
		return null; // success condition
	} // end uploadFile()
	
	
	
	public String downloadFile(User user, String fileName, HttpServletResponse response, ServletContext context)
	{
		String sql = "select userFile from " + user + " where fileName = '" + fileName + "'"; // select the file from the user's table by the supplied filename

		try
		{
			connect();
			
			ResultSet rs = sendQuery(sql); // send SQL query and store resulting table
			
			if(rs.next()) // if the query returns a matching file
			{
				Blob blob = rs.getBlob(1); // retrieve file from query -- first (and only) column
				InputStream inputStream = blob.getBinaryStream(); // get binary stream from file
				int fileLength = inputStream.available(); // retrieve file size for response header
				
				String mimeType = context.getMimeType(fileName); // parse the MIME type (what kind of media we're handling) for the browser, again in the header

				if(mimeType == null) // if no MIME type is returned, give a default "application" type
					mimeType = "application/octet-stream";
				
				// set the header attributes for the upcoming download
				response.setContentType(mimeType); // we're sending a ___ file type to the browser for download
				response.setContentLength(fileLength); // the file is ___ bytes big
				String headerKey = "Content-Disposition"; // specifies to the browser exactly what is being sent (a page to render, or something to download). not a standard.
				String headerValue = String.format("attachment; filename=\"%s\"", fileName); // tells the browser it is a download, and its optional filename
				response.setHeader(headerKey, headerValue);

				OutputStream outputStream = response.getOutputStream(); // open output stream to client
				byte[] buffer = new byte[1024*4]; // 4 kb buffer - the bigger the buffer, the more memory is used by the program, but the faster it goes
				int bytesRead = -1; // start at eof (-1). this is where the chunks of data are stored between reading/writing them

				while((bytesRead = inputStream.read(buffer)) != -1) // read and write in buffer-size chunks of bytes until eof (-1)
					outputStream.write(buffer, 0, bytesRead);

				// close streams
				outputStream.close();
				inputStream.close();

				// add downloaded file to audit log
				user.logDownload(fileName);
			} // end if query returns matching file
			else
			{
				return "</a>" + fileName + "<a style=\"color:red\"> could not be found in SQL database for download."; // download failed
			}
			
			rs.close();
		} // end try
		catch (Exception e)
		{
			e.printStackTrace();
			return "</a>" + fileName + "<a style=\"color:red\"> experienced an error during download.";
		}
				
		return null; // success condition
	} // end downloadFile()
	
	
	
	public String deleteFile(User user, String fileName)
	{
		String sql = "delete from " + user + " where fileName = '" + fileName + "'";

		try
		{
			connect();
			
			if(sendUpdate(sql) > 0) // send SQL command and check if file was successfully deleted from table
			{
				// add entry to audit log
				user.logDelete(fileName);

				// remove from  user's file list
				user.removeFile(fileName);
				
				return "</a>" + fileName + "<a style=\"color:red\"> was deleted."; // success condition
			}
			else
			{
				return "</a>" + fileName + "<a style=\"color:red\"> could not be found for deletion."; // file not found in database
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "</a>" + fileName + "<a style=\"color:red\"> experienced an error when deleting from the database.";
		}
	} // end deleteFile()
	
	
	
	public boolean isAvailable()
	{
		try
		{
			connect();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
		return true; // success condition
	} // end connect()
	
	
	
	
	
	
	private void connect() throws Exception
	{
		Class.forName("com.mysql.cj.jdbc.Driver"); // load MySQL driver
		
		Properties props = new Properties(); // connection properties
		props.setProperty("user", dbUsername);
		props.setProperty("password", dbPassword);
		props.setProperty("sslMode", "VERIFY_CA"); // forces SSL from db and checks db's certificate

		con = DriverManager.getConnection(url, props); // connect to MySql
		//con = DriverManager.getConnection(url, dbUser, dbPass); // connect to MySql
	} // end connect()
	
	
	
	private void setPermission(User user, String field, boolean permission)
	{
		int value;
		
		if(permission)
			value = 1;
		else
			value = 0;
		
		String record = field + " = " + value;
		
		String sql = "update permissions set " + record + " where username = '" + user + "'";
				
		try
		{
			connect();
			
			sendUpdate(sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
//	private void checkSSL() throws Exception
//	{
//		String sql = "show status like 'ssl_cipher'";
//			
//		ResultSet rs = sendQuery(sql);
//
//		if(rs.next())
//		{
//			System.out.println("ssl cipher status: " + rs.getString("Value"));
//		}
//		else
//			System.out.println("ssl cipher status: null");
//	}
	
	
	
	// add new user to SQL database -- originally planned for use in conjunction with LDAP account creation, but now automatically called
	// when attempting to retrieve user credentials that don't exist in SQL db
	private User addUser(String username) throws SQLException
	{
		//add user to permissions table
		String sql = "insert into permissions value('" + username + "', 1, 1, 1)";
		sendUpdate(sql);

		// create new user table for file storage
		sql = "create table " + username + "(fileName varchar(150) primary key, userFile blob)";
		sendUpdate(sql);
		
		User user = getUser(username);
		
		return user;
	} // end addUser()
	
	
	
	// builds the user's file list
	private LinkedList<String> getFiles(User user) throws SQLException
	{
		LinkedList<String> fileList = new LinkedList<>(); // store file names
		String sql = "select fileName from " + user; // retrieve only the file names from a user's table
			
		ResultSet rs = sendQuery(sql);
		
		// process results if any are returned
		while(rs.next()) // loop while there are more files to process
		{
			fileList.add(rs.getString("fileName"));
		}

		return fileList;
	} // end getFiles()
	
	
	
	private int sendUpdate(String sql) throws SQLException
	{
		Statement st = con.createStatement();
		return st.executeUpdate(sql); // send SQL query and store resulting table
	} // end sendUpdate()
	
	
	
	private ResultSet sendQuery(String sql) throws SQLException
	{
		Statement st = con.createStatement();
		return st.executeQuery(sql); // send SQL query and store resulting table
	} // end sendQuery()
	
	
	
//	private void close()
//	{
//		try
//		{
//			if(con != null)
//				con.close();
//		}
//		catch (SQLException e)
//		{
//			e.printStackTrace();
//		}
//	} // end close()
}
