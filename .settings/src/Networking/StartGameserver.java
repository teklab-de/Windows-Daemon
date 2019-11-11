package Networking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.FileSystems;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

public class StartGameserver implements Runnable 
{
	public String data;
	public String startbefehl;
	public String exename;
	public String path;
	public String pathk;
	public String username;
	
	public Writer writer = new Writer();
	
	public StartGameserver(String data, String startbefehl, String exename, String path, String pathk, String username)
	{
		this.data = data;
		this.startbefehl = startbefehl;
		this.exename = exename;
		this.path = path;
		this.pathk = pathk;
		this.username = username;
	}
	
	public void run() 
	{		
		try 
		{
			Process startbat = Runtime.getRuntime().exec(new String[]{"cmd", "/C", startbefehl}, null, new File(path));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
