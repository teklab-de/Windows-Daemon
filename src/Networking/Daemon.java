
package Networking;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JTextArea;

import Enviroment.Enviroment;

public class Daemon implements Runnable {
	public JTextArea logbox;
	public Writer writer = new Writer();
	public ServerSocket server;
	public Socket client;
	public BufferedReader reader;
	public FTPServer ftpserver;
	
	public int port;
	public String daemonpasswort;	
	public String kundenpfad;
	
	public Daemon(int port, String daemonpasswort, String kundenpfad, JTextArea logbox, FTPServer ftpserver) throws IOException
	{
		this.port = port;
		this.daemonpasswort = daemonpasswort;
		this.kundenpfad = kundenpfad;
		this.logbox = logbox;
		this.ftpserver = ftpserver;
	}
	
    public void run() 
    {	
    	try 
    	{
    		server = new ServerSocket(port);
    		writer.appendData("Server", "\nStarte Server auf Port " + String.valueOf(port));
    		
    		while(true)
    		{
    			client = server.accept();
    			writer.appendData("Server", "\nAkzeptiere Verbindung von IP " + client.getInetAddress() +" und Port " + client.getLocalPort() + "");
    			
    			Handler handler = new Handler(client, daemonpasswort, kundenpfad, ftpserver);
    			Thread thread = new Thread(handler);
    			thread.start();
    		}
    	}
    	catch(Exception ex)
    	{
    		writer.appendData("Server", "Eine Exception wurde ausgelösst.");
    	}
    }
    
    public void closeServer()
    {
    
    	try
    	{
    		client.close();
    		server.close();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
}
