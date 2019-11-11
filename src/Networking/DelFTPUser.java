package Networking;

import java.io.InputStream;

public class DelFTPUser implements Runnable
{
	public String path;
	public String filename;
	public Writer writer = new Writer();
	
	public DelFTPUser(String path, String filename)
	{
		this.path = path;
		this.filename = filename;
	}
	
	public void run()
	{
		try
		{
			Process deleteftpus = Runtime.getRuntime().exec(new String[]{"cmd", "/C", "cd "+ path + " && del /s /q " + filename + ""});
			InputStream indeleteftpuser = deleteftpus.getInputStream();
			
			byte[] ReadGamepath = new byte[1024];
			int readgamepath = 0;
			
			while((readgamepath = indeleteftpuser.read(ReadGamepath, 0, 1024)) >- 1)
			{
				writer.appendData("Server", new String(ReadGamepath));
			}
			
			if(indeleteftpuser != null)
			{
				indeleteftpuser.close();
			}	
		}
		catch(Exception ex)
		{
			writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage()+ "");
		}
	}
}
