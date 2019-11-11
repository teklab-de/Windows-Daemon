package Networking;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class ZipEntpacker implements Runnable
{
	public String filename;
	public String pfad;
	public Writer writer = new Writer();
	
	public ZipEntpacker(String filename, String pfad)
	{
		this.filename = filename;
		this.pfad = pfad;
	}
	
	public void run()
	{
		try
		{
		    ZipFile zipFile = new ZipFile(filename);
		    if (zipFile.isEncrypted()) 
		    {
		    	zipFile.setPassword("test");
		    }
		     
		    zipFile.extractAll(pfad);
		} 
		catch (ZipException e) 
		{
			writer.appendData("Server", "Eine Exception wurde ausgelösst " + e.getMessage() + "");
		}
	}
}
