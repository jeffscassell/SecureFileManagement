package com.sfm;
import java.io.FileInputStream;
import java.util.Properties;



public final class Config implements SqlConfig, LdapConfig, LoggerConfig
{
	// make sure this points to config file
	private final String CONFIG_FILE = "C:\\Users\\Jeff\\eclipse-workspace\\SecureFileManager\\config.ini";
	
	// create the only instance of this object that will exist (a singleton pattern)
	// so that the config file will be read only once
	private static Config config = new Config();
	
	// LDAP
	private String ldapUrl;
	private String ldapAdminUsername;
	private String ldapAdminPassword;
	
	// SQL
	private String sqlUrl;
	private String sqlUsername;
	private String sqlPassword;
	
	// logger
	private String loggerParentDir;
	
	// if settings are successfully loaded, this becomes true
	private boolean loaded = false;
	
	
	
	public static Config getInstance()
	{
		return config;
	}
	
	
	
	// constructor returns early if there is a problem with the config file, leaving loaded field set to false
	private Config()
	{
		Properties props;
		
		try
		{
			props = getSettings();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		
		// LDAP fields
		ldapUrl = props.getProperty("ldap.url");
		ldapAdminUsername = props.getProperty("ldap.admin.username");
		ldapAdminPassword = props.getProperty("ldap.admin.password");
		
		// SQL fields
		sqlUrl = props.getProperty("sql.url");
		sqlUsername = props.getProperty("sql.username");
		sqlPassword = props.getProperty("sql.password");
		
		// Logger field
		loggerParentDir = props.getProperty("logger.parentDir");
		
		// misc
		System.setProperty("javax.net.ssl.trustStore", props.getProperty("trustStore.file"));
		System.setProperty("javax.net.ssl.trustStorePassword", props.getProperty("trustStore.password"));
		
		loaded = true;
	}
	
	
	
	private Properties getSettings() throws Exception
	{
		Properties props = new Properties();
		
		FileInputStream file = new FileInputStream(CONFIG_FILE);
		props.load(file);
		file.close();
		
		return props;
	}
	
	
	
	// if settings were loaded
	// POST method
	public boolean isLoaded() { return loaded; }
	
	
	// LDAP
	@Override
	public String getLdapUrl() { return ldapUrl; }
	@Override
	public String getLdapAdminUsername() { return ldapAdminUsername; }
	@Override
	public String getLdapAdminPassword() { return ldapAdminPassword; }
	
	
	// SQL
	@Override
	public String getSqlUrl() { return sqlUrl; }
	@Override
	public String getSqlUsername() { return sqlUsername; }
	@Override
	public String getSqlPassword() { return sqlPassword; }
	
	
	// Logger
	@Override
	public String getLoggerParentDir() { return loggerParentDir; }
}



// creating separate interfaces for each group of settings allows more granular control, and limits each using-class to only its necessary methods and fields
// also, if there is ever a need for a specific testing configuration, creating a new test-specific class with the desired settings would be as simple as writing a new class implementing
// the same interface. the only thing that would need to change in the classes using the testing configuration would be a single line calling the new test-class config instead of this
// run-class config



interface SqlConfig
{
	String getSqlUrl();
	String getSqlUsername();
	String getSqlPassword();
}



interface LdapConfig
{
	String getLdapUrl();
	String getLdapAdminUsername();
	String getLdapAdminPassword();
}



interface LoggerConfig
{
	String getLoggerParentDir();
}