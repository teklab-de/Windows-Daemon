package Networking;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class ImageServerDownload implements Runnable
{
	public String servertyp;
	public String user;
	public String path;
	public String name;
	public String imageserverurl;
	public String kundenpfad;
	
	public Writer writer = new Writer();
	public Handler handler;
	public BufferedInputStream in;
	public FileOutputStream fileout;
	
	public ImageServerDownload(Handler handler, String servertyp, String user, String path, String name, String imageserverurl, String kundenpfad)
	{
		this.handler = handler;
		this.servertyp = servertyp;
		this.user = user;
		this.path = path;
		this.name = name;
		this.imageserverurl = imageserverurl;
		this.kundenpfad = kundenpfad;
	}

	public void run() 
	{
		try
		{
			downloadImage();
		}
		catch(Exception ex)
		{
			writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage());
		}
	}

	public void downloadImage() throws IOException 
	{
		try
		{
			File downloadfile = new File(""+kundenpfad+"//"+user+"//"+servertyp+"//"+path+"");
			if(downloadfile.exists())
			{
				URL url = new URL(""+imageserverurl+"//"+name+".zip");
				URLConnection conn = url.openConnection();
				InputStream in = conn.getInputStream();
				
				writer.appendData("Server", "Verzeichnis "+kundenpfad+"//"+user+"//"+servertyp+"//"+path+"// wurde erstellt.");
				writer.appendData("Server", "Imageserverhost " + imageserverurl + "");
				
				FileOutputStream out = new FileOutputStream(""+kundenpfad+"//"+user+"//"+servertyp+"//"+path+"//"+path+".zip");
				File zipfile = new File(""+kundenpfad+"//"+user+"//"+servertyp+"//"+path+"//"+path+".zip");
				
				byte[] b = new byte[4086];
				int count = 0;
				 
				while ((count = in.read(b)) >= 0) 
				{  
					out.write(b, 0, count);  
					out.flush();
				}  
				
				if(out != null)
				{
					out.close();
				}
				
				if(in != null)
				{
					in.close();
				}
			}
			else
			{
				if(downloadfile.mkdirs())
				{
					URL url = new URL(""+imageserverurl+"//"+name+".zip");
					URLConnection conn = url.openConnection();
					InputStream in = conn.getInputStream();
					
					writer.appendData("Server", "Verzeichnis "+kundenpfad+"//"+user+"//"+servertyp+"//"+path+"// wurde erstellt.");
					writer.appendData("Server", "Imageserverhost " + imageserverurl + "");
					
					FileOutputStream out = new FileOutputStream(""+kundenpfad+"//"+user+"//"+servertyp+"//"+path+"//"+path+".zip");
					File zipfile = new File(""+kundenpfad+"//"+user+"//"+servertyp+"//"+path+"//"+path+".zip");
					
					byte[] b = new byte[4086];
					int count = 0;
					 
					while ((count = in.read(b)) >= 0) 
					{  
						out.write(b, 0, count);  
						out.flush();
					}  
					
					if(out != null)
					{
						out.close();
					}
					
					if(in != null)
					{
					    in.close();
					}
				}
				else
				{
					writer.appendData("Server", "Konnte Verzeichnis "+kundenpfad+"//"+user+"//"+servertyp+"//"+path+"//"+name+" nicht erstellen.");	
				}
			}
		}
		catch(Exception ex)
		{
			writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
		}
	}
}
