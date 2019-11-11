package Networking;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class StopGameserver implements Runnable
{
	public Process getpid;
	public InputStream input;
	
	public String data;
	public String gamepath;
	
	public Writer writer = new Writer();
	
	public StopGameserver(String data, String gamepath) 
	{
		this.data = data;
		this.gamepath = gamepath;
	}
	
	public void run()
	{
		try
		{
			getpid = Runtime.getRuntime().exec(new String[]{"cmd", "/C", "my_stop.bat"}, null, new File(gamepath));
			input = getpid.getInputStream();
			
			byte[] DataBuffer = new byte[2048];
			int i = 0;
		
			while((i = input.read(DataBuffer, 0, DataBuffer.length)) > -1)
			{
				writer.appendData("Server", new String(DataBuffer));
			}
		} 
		catch(IOException ex)
		{
			writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
		}
	}
}
