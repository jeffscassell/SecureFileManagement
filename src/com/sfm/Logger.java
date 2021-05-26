package com.sfm;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


// instance class that is instantiated with each user object, to be used by user objects only (except the POST method)
// currently does not have any real error handling, there's only the POST check
public class Logger
{
	private User user;
	private static FileWriter output;
	private LoggerConfig config = Config.getInstance();
	private final String logsParentDir = config.getLoggerParentDir(); // parent directory of the audit log folder
	
	
	
	public Logger(){}
	
	public Logger(User user)
	{ this.user = user; }
	
	
	public void uploaded(String fileName)
	{ writeRecord(user, buildRecord(user +  " uploaded \"" + fileName + "\"")); }
	
	
	public void downloaded(String fileName)
	{ writeRecord(user, buildRecord(user + " downloaded \"" + fileName + "\"")); }
	
	
	public void deleted(String fileName)
	{ writeRecord(user, buildRecord(user + " deleted \"" + fileName + "\"")); }
	
	
	public void error(String error)
	{ writeRecord(user, buildRecord("!!! " + user + " " + error)); }
	
	
	public void loggedIn()
	{ writeRecord(user, buildRecord(user + " logged in")); }
	
	
	public void loggedOut()
	{ writeRecord(user, buildRecord(user + " logged out")); }
	
	
	public void changedPermission(User user, String field, boolean value)
	{ writeRecord(this.user, buildRecord(this.user + " modified " + user + "'s " + field + " permission to " + value)); } 
	
	
	// check if the currently used parent directory is correct or needs to be updated
	// POST (power-on self-test) method
	public boolean isAvailable()
	{
		File dir = new File(logsParentDir);
		
		if(dir.exists())
			return true;
		else
			return false;
	} // end exists()
	
	
	
	
	
	
	private void writeRecord(User user, String entry)
	{
		openLog(user);
		
		try
		{
			output.write(entry);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		closeLog();
	} // end writeRecord()
	
	
	
	// returns formatted log entry
	private String buildRecord(String action)
	{
		return String.format(getStamp() + action + "\n");
	} // end buildRecord()
	
	
	
	// builds and returns time stamp for log
	private String getStamp()
	{
		java.util.Calendar time = java.util.Calendar.getInstance(); // create date object for time stamp
		
		return String.format("%1$tF %1$Ta %1$tT: ", time);
		
		// %1$ indicates first argument (time above)
		// %t = lowercase printing of whatever chosen format
		// %T = uppercase printing
		// %tF = YYYY-MM-DD
		// %Ta = WED, uppercase 3-letter day of the week
		// %tT = HH:MM:SS, 24-hour format
	} // end buildString()
	
	
	
	private void openLog(User user)
	{
		// directory that will hold the audit logs
		String logsDir = logsParentDir + File.separator + "logs";
		File dir = new File(logsDir);
		
		// if audit logs directory does not exist, attempt to make it (will only make the "logs" folder and not any preceding folders)
		if(!dir.exists())
		{
			dir.mkdir(); // user mkdirs() if preceding folders need to be created as well
		}
		
		// an individual user's log file
		String logFile = String.format(logsDir + File.separator + "%s.txt", user);

		try
		{
			output = new FileWriter(logFile, true); // open the log file for writing, true to append to it if it already exists
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}// end openLog()
	
	
	
	private void closeLog()
	{
		if (output != null)
		{
			try
			{
				output.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	} // end closeLog()
}
