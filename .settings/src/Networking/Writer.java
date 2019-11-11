package Networking;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Writer {
	public void appendData(String name, String data)
	{
		try 
		{
			if(name.length() > 0 || data.length() > 0)
			{
				File file = new File("Logs");
				
				if(file.exists())
				{
					File receivefile = new File("Logs/daemonreceivelog.txt");
					FileWriter writer = new FileWriter(receivefile, true);
					BufferedWriter filewriter = new BufferedWriter(writer);
			
					filewriter.append("\n" + name + "");
					filewriter.append("\n------------------------------------------------------------------------------------");
					filewriter.append("\n" + data + "");
					filewriter.append("\n------------------------------------------------------------------------------------");
					filewriter.flush();
					filewriter.close();
				}
				else
				{
					if(file.mkdir())
					{
						File receivefile = new File("Logs/daemonreceivelog.txt");
						FileWriter writer = new FileWriter(receivefile, true);
						BufferedWriter filewriter = new BufferedWriter(writer);
			
						filewriter.append("\n" + name + "");
						filewriter.append("\n------------------------------------------------------------------------------------");
						filewriter.append("\n" + data + "");
						filewriter.append("\n------------------------------------------------------------------------------------");
						filewriter.flush();
						filewriter.close();
					}
					else
					{
						appendData("Server", "Das neue Verzeichnis konnte nicht erstellt werden keine Rechte?.");
					}
				}
			}
			else
			{
				appendData("Server", "Bitte geben sie ein Name und eine Nachricht ein.");
			}
		}
		catch(Exception ex)
		{
			appendData("Server", "Exception wurde ausgelösst " + ex.getMessage()+"");
		}
	}
}
