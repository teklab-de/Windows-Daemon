package Networking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JTextArea;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.DbUserManagerFactory;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.apache.ftpserver.usermanager.UserFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;


public class FTPServer implements Runnable {
	public int port;
	public int randomInt;
	public String ip;
	public String username;
	public String password;
	public String pfad;
	
	public FtpServer server;
	public FtpServerFactory  factory;
	public ListenerFactory lfactory;
	public PropertiesUserManagerFactory userManagerFactory;
	public UserManager um;
	public BaseUser user;
	public Writer writer = new Writer();
	
	public FTPServer(String ip, String username, String password, int port, String pfad) 
	{ 
		this.ip = ip;
		this.username = username;
		this.password = password;
		this.port = port;
		this.pfad = pfad;
	}
	
	@Override
	public void run() 
	{
		try {
			factory = new FtpServerFactory();
			lfactory = new ListenerFactory();
			lfactory.setPort(port);
			
			File file = new File("Configs\\FTP\\user.properties");
			if(!file.exists())
		    {
				file.createNewFile();
		    }
			
		    factory.addListener("default", lfactory.createListener());
		    userManagerFactory = new PropertiesUserManagerFactory();
		    userManagerFactory.setFile(new File("Configs\\FTP\\user.properties"));
		    userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());
			
		    user = new BaseUser();
		    user.setName(username);
		    user.setPassword(password);
		    user.setHomeDirectory(pfad);
		    user.setEnabled(true);
		    
		    List<Authority> authorities = new ArrayList<Authority>();
		    authorities.add(new WritePermission());
		    user.setAuthorities(authorities);
		    
		    um = userManagerFactory.createUserManager();
	        um.save(user);
	        factory.setUserManager(um);
		    
	        Map<String, Ftplet> m = new HashMap<String, Ftplet>();
	        m.put("miaFtplet", new Ftplet()
	        {
				@Override
				public void init(FtpletContext arg0) throws FtpException 
				{
					writer.appendData("FTPServer", "Herzlich Willkommen auf einem Privaten FTP-Server");
				}
				
				@Override
				public FtpletResult afterCommand(FtpSession arg0, FtpRequest arg1, FtpReply arg2) throws FtpException, IOException 
				{
					writer.appendData("FTPServer",  "Command wurde ausgeführt " + arg1.getCommand()+" von der IP "+arg0.getClientAddress()+"");
					return null;
				}

				@Override
				public FtpletResult beforeCommand(FtpSession arg0, FtpRequest arg1) throws FtpException, IOException 
				{
					writer.appendData("FTPServer", "Command wird ausgeführt " + arg1.getCommand()+" von der IP "+arg0.getClientAddress()+"");
					return null;
				}

				@Override
				public void destroy() 
				{
					writer.appendData("FTPServer", "Verbindung getrennt");
				}

				@Override
				public FtpletResult onConnect(FtpSession arg0) throws FtpException, IOException 
				{
					writer.appendData("FTPServer", "Neue Verbindung mit IP "+arg0.getClientAddress()+" verbindet");
					return null;
				}

				@Override
				public FtpletResult onDisconnect(FtpSession arg0) throws FtpException, IOException 
				{
					writer.appendData("FTPServer", "Verbindung mit IP "+arg0.getClientAddress()+" getrennt");
					return null;
				} 
	        });
	        
	        factory.setFtplets(m);
	        server = factory.createServer();
	        server.start();
	        writer.appendData("FTPServer", "Starte Ftp-Server auf IP " + ip +" und Port " + port+"");
		
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void closeFTPServer()
	{
		try 
		{
			server.stop();
		}
		catch(Exception ex)
		{
			writer.appendData("FTPServer", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
		}
	}
	
	public String createFTPUsers(String username, String password, String pfad)
	{
		try
		{
		    factory.addListener("default", lfactory.createListener());
		    userManagerFactory = new PropertiesUserManagerFactory();
		    userManagerFactory.setFile(new File("Configs\\FTP\\user.properties"));
		    userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());
			
		    user = new BaseUser();
		    user.setName(username);
		    user.setPassword(password);
		    user.setHomeDirectory(pfad);
		    user.setEnabled(true);
		    
		    List<Authority> authorities = new ArrayList<Authority>();
		    authorities.add(new WritePermission());
		    user.setAuthorities(authorities);
		    
		    um = userManagerFactory.createUserManager();
	        um.save(user);
	        factory.setUserManager(um);
		    
		    return "OK1";
		}
		catch(Exception ex)
		{
			writer.appendData("FTPServer", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
			return ex.getMessage();
		}
	}
}
