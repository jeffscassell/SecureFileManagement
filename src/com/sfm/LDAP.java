package com.sfm;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;


public class LDAP
{
	private DirContext connection;
	private String driver = "com.sun.jndi.ldap.LdapCtxFactory";
	private LdapConfig config = Config.getInstance();
	
	// configuration
	private String url = config.getLdapUrl();
	private String admin = config.getLdapAdminUsername();
	private String password = config.getLdapAdminPassword();

	
	// check LDAP connection
	public boolean isAvailable()
	{
		try
		{
			Properties env = new Properties();
			env.put(Context.INITIAL_CONTEXT_FACTORY, driver);
			env.put(Context.PROVIDER_URL, url);
			env.put(Context.SECURITY_PRINCIPAL, admin);  // check admin's DN for unique user ID
			env.put(Context.SECURITY_CREDENTIALS, password);
			
			connection = new InitialDirContext(env);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			close();
		}
			
		return true;
	}
	
	
	
	// authenticate users
	public boolean isAuthenticated(String username, String password)
	{
		try
		{
			Properties env = new Properties();
			env.put(Context.INITIAL_CONTEXT_FACTORY, driver);
			env.put(Context.PROVIDER_URL, url);
			env.put(Context.SECURITY_PRINCIPAL, "cn=" + username + ",ou=users,ou=system");  // check the DN for unique user ID
			env.put(Context.SECURITY_CREDENTIALS, password);
			
			connection = new InitialDirContext(env);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			close();
		}
			
		return true;
	}
	
	// isn't needed for some reason ??? just throws SocketException because the socket was closed already
	private void close()
	{
		try
		{
			if(connection != null)
				connection.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
