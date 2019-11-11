package Networking;

import java.io.File;
import java.io.InputStream;

public class DelGameserver implements Runnable
{
	public String path;
	public String gamekürzel;
	public String gameserverconfigfile;
	public String file;
	
	public Writer writer = new Writer();
	
	public DelGameserver(String path, String gamekürzel, String gameserverconfigfile, String file)
	{
		this.path = path;
		this.gamekürzel = gamekürzel;
		this.gameserverconfigfile = gameserverconfigfile;
		this.file = file;
	}
	
	public void run()
	{
		try 
		{
			Process deletewindowsgameserver = Runtime.getRuntime().exec(new String[]{"cmd", "/C", "cd "+ path + " && rmdir /s /q " + gamekürzel + ""});
			InputStream indeletewindowsgameserver = deletewindowsgameserver.getInputStream();
			
			byte[] ReadGamepath = new byte[1024];
			int readgamepath = 0;
			
			while((readgamepath = indeletewindowsgameserver.read(ReadGamepath, 0, 1024)) >- 1)
			{
				writer.appendData("Server", new String(ReadGamepath));
			}
			
			if(deletewindowsgameserver != null)
			{
				indeletewindowsgameserver.close();
			}
			
			Process deletewindowsgameserverconfigfile = Runtime.getRuntime().exec(new String[]{"cmd", "/C", "cd "+ gameserverconfigfile + " && del /s /q " + file + ""});
			InputStream indeletegameserverconfig = deletewindowsgameserverconfigfile.getInputStream();
			byte[] ReadConfig = new byte[1024];
			int readconfig = 0;
			
			while((readconfig = indeletegameserverconfig.read(ReadConfig, 0, 1024)) >- 1)
			{
				writer.appendData("Server", new String(ReadConfig));
			}
			
			if(indeletegameserverconfig != null)
			{
				indeletegameserverconfig.close();
			}
			
		}	
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
