package Networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.apache.commons.io.IOUtils;

public class Handler implements Runnable
{
	public DataInputStream in;
	public DataOutputStream out;
	public Writer writer;
	public Socket client;
	
	public int header;
	public int read;
	public byte[] DataBuffer;
	
	public FTPServer ftpserver;
    public String daemonpasswort;
    public String kundenpfad;
	
	public Handler(Socket client, String daemonpasswort, String kundenpfad, FTPServer ftpserver) throws IOException
	{
		this.client = client;
		this.writer = new Writer();
		this.ftpserver = ftpserver;
		
		this.daemonpasswort = daemonpasswort;
		this.kundenpfad = kundenpfad;
		this.header = 0;
		this.read = 0;
		this.DataBuffer = new byte[4096];
	}
	
	public void run()
	{
		try 
		{
			this.in = new DataInputStream(client.getInputStream());
			while((read = in.read(DataBuffer)) >= -1)
			{
				if(read <= 0)
				{
					break;
				}

				String convert = IOUtils.toString(DataBuffer, "UTF-8");
				PacketHandler handler = new PacketHandler(this, ftpserver, convert, daemonpasswort, kundenpfad);
				Thread phandlerthread = new Thread(handler);
				phandlerthread.start();
			}
		}
		catch(Exception ex)
		{
			writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
		}
	}
	
	public void SendData(String D) throws IOException 
	{
		try
		{
			this.out = new DataOutputStream(client.getOutputStream());
			
			if(D.length() <= 0)
			{
				/* gar nichts machen */
			}
			
			out.write(D.getBytes(), 0, D.getBytes().length);
			out.flush();
		}

		catch(IOException ex)
		{
			writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
		}
	}
}
