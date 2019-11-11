package Networking;

import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class StopGameserver implements Runnable
{
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
			Process getpid = Runtime.getRuntime().exec(new String[]{"cmd", "/C", "my_stop.bat"}, null, new File(gamepath));
			InputStream input = getpid.getInputStream();
			
			byte[] DataBuffer = new byte[2048];
			int i = 0;
		
			while((i = input.read(DataBuffer, 0, DataBuffer.length)) > -1)
			{
				writer.appendData("Server", new String(DataBuffer));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
