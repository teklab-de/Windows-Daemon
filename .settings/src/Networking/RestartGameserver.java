package Networking;

import java.io.InputStream;

public class RestartGameserver implements Runnable
{
	public String befehl;
	public Writer writer = new Writer();
	
	public RestartGameserver(String befehl)
	{
		this.befehl = befehl;
	}
	
	public void run()
	{
		try
		{
			Process restart = Runtime.getRuntime().exec(new String[]{"cmd /C "+befehl + ""});
			InputStream in = restart.getInputStream();
			byte[] Buffer = new byte[1024];
			int read = 0;
			
			while((read = in.read(Buffer, 0, 1024)) > -1)
			{
				writer.appendData("Server", new String(Buffer));
			}
		}
		catch(Exception ex)
		{
			writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
		}
	}
}
