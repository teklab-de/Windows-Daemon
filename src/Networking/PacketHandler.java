package Networking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.net.ftp.FTPClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PacketHandler implements Runnable
{
	public String data;
	public String daemonpasswort;
	public String version;
	public String kundenpfad;
	public String imageserverurl;
	public String path;
	public String name;
	public String user;
	public String servertyp;
	
	public ImageServerDownload downloadimage;
	public ZipEntpacker unpackzip;
	public Handler handler;
	public FTPServer ftpserver;
	public Writer writer = new Writer();
	public Thread downloadthread;
	public Thread unpackthread;
	public Document doc;
	
	public PacketHandler(Handler handler, FTPServer ftpserver, String data, String daemonpasswort, String kundenpfad) 
	{
		this.data = data;
		this.daemonpasswort = daemonpasswort;
		this.kundenpfad = kundenpfad;
		this.version = "version";
		this.handler = handler;
		this.ftpserver = ftpserver;
	}
	
	public void run() 
	{
		System.out.println("\nEmpfange: " + data + "");
		if(data.contains(daemonpasswort))
		{
			try
			{
				handler.SendData("ID1");
			}
			catch(IOException ex)
			{
				writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
			}
		}

		if(data.contains(version))
		{
			try
			{
				handler.SendData("7112");
			}
			catch(IOException ex)
			{
				writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
			}
		}
		
		if(data.startsWith("'useradd'"))
		{
			String replace = data.replace("|", "");
			String endreplace = replace.replace("'", " ");
			String[] split = endreplace.split(" ");
			
			if(split[1].contains("useradd"))
			{
				String ftpusername = split[3];
				String ftppassword = split[5];
				
				String replaceftpusername = ftpusername.replace(" ", "");
				String ftppfad = ""+kundenpfad+"\\"+replaceftpusername+"";
				
				String replacepfad = ftppfad.replace(" ", "");
				String replaceusername = ftpusername.replace(" ", "");
				String replacepassword = ftppassword.replace(" ", "");
				
				File checkordner = new File(ftppfad);
				if(checkordner.exists())
				{
					if(ftpserver.createFTPUsers(replaceusername, replacepassword, replacepfad) == true)
					{
						try
						{
							handler.SendData("ID1");
							writer.appendData("Server", "FTP-User wurde erstellt mit Username " + ftpusername + " und Password " + ftppassword + " und Verzeichnis " + ftpusername + ".");
						}
						catch(IOException ex)
						{
							writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
						}
					}
					else
					{
						try
						{
							handler.SendData("ID100");
							writer.appendData("Server", "FTP-User konnte nicht erstellt werden.");
						}
						catch(IOException ex)
						{
							writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
						}
					}
				}
				else
				{
					if(checkordner.mkdirs())
					{
						if(ftpserver.createFTPUsers(replaceusername, replacepassword, replacepfad) == true)
						{
							try
							{
								handler.SendData("ID1");
								writer.appendData("Server", "FTP-User wurde erstellt mit Username " + ftpusername + " und Password " + ftppassword + " und Verzeichnis " + ftpusername + ".");
							}
							catch(IOException ex)
							{
								writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
							}
						}
						else
						{
							try 
							{
								handler.SendData("ID100");
							} 
							catch (IOException ex) 
							{
								writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
							}
								
							writer.appendData("Server", "FTP-User konnte nicht erstellt werden.");
						}
					}	
					else
					{
						if(ftpserver.createFTPUsers(replaceusername, replacepassword, replacepfad) == true)
						{
							try
							{
								handler.SendData("ID1");
								writer.appendData("Server", "FTP-User wurde erstellt mit Username " + ftpusername + " und Password " + ftppassword + " und Verzeichnis " + ftpusername + ".");
								writer.appendData("Server", "Der Ordner für Kunde " + ftpusername + " konnte nicht erstellt werden, dieser existiert wahrscheinlich schon.");
							}
							catch(IOException ex)
							{
								writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
							}
						}
						else
						{
							try
							{
								handler.SendData("ID100");
								writer.appendData("Server", "FTP-User konnte nicht erstellt werden.");
							}
							catch(IOException ex)
							{
								writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
							}
						}
					}
				}
			}
		}
		
		if(data.startsWith("'install'"))
		{
			String replace = data.replace("|", "");
			String endreplace = replace.replace("'", " ");
			String[] split = endreplace.split(" ");
						
			if(split[13].startsWith("delete"))
			{
				String user = split[5];
				String path = split[7];
				String gamekürzel = path;
				
				String gameserverconfigfile = "Configs\\Gameserver";
				String gamepath = ""+kundenpfad+"\\"+user+"\\server";
				String gamepathstop = ""+kundenpfad+"\\"+user+"\\server\\"+path+"";
				String file = ""+user+""+path+".xml";
				
				String gamepathexists = ""+kundenpfad+"\\"+user+"\\server\\"+gamekürzel+"";
				String gameconfigexists = ""+gameserverconfigfile+"\\"+file+"";
							
				try
				{
					StopGameserver stopgameserver = new StopGameserver(data, gamepathstop);
					Thread thread = new Thread(stopgameserver);
					thread.start();
					thread.join();
					
					if(thread.getState() == Thread.State.TERMINATED)
					{
						writer.appendData("Server", "Der Gameserver wurde gestoppt.");
						
						try
						{
							File gpathdeleted = new File(gamepathexists);
							File gpathdeletedconfig = new File(gameconfigexists);						
							
							if(gpathdeleted.exists() || gpathdeletedconfig.exists())
							{
								DelGameserver deletegameserver = new DelGameserver(gamepath, gamekürzel, gameserverconfigfile, file);
								Thread deletethread = new Thread(deletegameserver);
								deletethread.start();
								deletethread.join();
								
								if(deletethread.getState() == Thread.State.TERMINATED)
								{
									writer.appendData("Server", "Der Gameserver wurde gelöscht.");
								
									servertyp = split[3].replace(" ", "");
									user = split[5].replace(" ", "");
									path = split[7].replace(" ", "");
									name = split[9].replace(" ", "");
									imageserverurl = split[11].replace(" ", "");
			
									File daemonconfig = new File("Configs/Gameserver/"+user+""+path+".xml");
						 			try 
						 			{
						 				if(daemonconfig.createNewFile())
						 				{
						 					DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
						 					DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
											
						 					doc = docBuilder.newDocument();
						 					Element rootElement = doc.createElement("Gameserver");
						 					doc.appendChild(rootElement);
									
						 					Element config = doc.createElement("Config");
						 					rootElement.appendChild(config);
									 
						 					Element servertypchild = doc.createElement("servertyp");
						 					servertypchild.appendChild(doc.createTextNode(servertyp));
						 					config.appendChild(servertypchild);
												
						 					Element userchild = doc.createElement("user");
						 					userchild.appendChild(doc.createTextNode(user));
											config.appendChild(userchild);
									 
											Element pathchild = doc.createElement("path");
											pathchild.appendChild(doc.createTextNode(path));
											config.appendChild(pathchild);
									 
											Element namechild = doc.createElement("name");
											namechild.appendChild(doc.createTextNode(name));
											config.appendChild(namechild);
										
											Element imageserverurlchild = doc.createElement("imageserverurl");
											imageserverurlchild.appendChild(doc.createTextNode(imageserverurl));
											config.appendChild(imageserverurlchild);
										
											Element statuschild = doc.createElement("status");
											statuschild.appendChild(doc.createTextNode("0"));
											config.appendChild(statuschild);
										
											Element exename = doc.createElement("exename");
											exename.appendChild(doc.createTextNode("0"));
											config.appendChild(exename);
										
											TransformerFactory transformerFactory = TransformerFactory.newInstance();
											Transformer transformer = transformerFactory.newTransformer();
											DOMSource source = new DOMSource(doc);
											StreamResult result = new StreamResult(new File("Configs/Gameserver/"+user+""+path+".xml"));
											transformer.transform(source, result);
						 				}
						 				else
						 				{	
						 					writer.appendData("Server", "Daemonconfigfile konnte nicht erstellt werden!");
						 				}
						 			}
						 			catch(Exception ex)
						 			{
						 				writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
						 			}
						 			
						 			try
						 			{
						 				handler.SendData("ID1");
						 				downloadimage = new ImageServerDownload(handler, servertyp, user, path, name, imageserverurl, kundenpfad);
						 				downloadthread = new Thread(downloadimage);
						 				downloadthread.start();
						 				downloadthread.join();
									
						 				unpackzip = new ZipEntpacker(""+kundenpfad+"\\"+user+"\\"+servertyp+"\\"+path+"\\"+path+".zip", ""+kundenpfad+"//"+user+"//"+servertyp+"//"+path+"");
						 				unpackthread = new Thread(unpackzip);
									
						 				if(downloadthread.getState() == Thread.State.RUNNABLE)
						 				{
						 					writer.appendData("Server", "Das Image "+name+".zip wird noch heruntergeladen.");
						 				}
									
						 				if(downloadthread.getState() == Thread.State.TERMINATED)
						 				{
						 					writer.appendData("Server", "Das Image "+name+".zip wurde heruntergeladen.");
						 					unpackthread.start();
						 					unpackthread.join();
						 				}
									
						 				if(unpackthread.getState() == Thread.State.RUNNABLE)
						 				{
						 					writer.appendData("Server", "Das Image "+name+".zip wird noch entpackt.");
						 				}
									
						 				if(unpackthread.getState() == Thread.State.TERMINATED)
						 				{
						 					writer.appendData("Server", "Das Image "+name+".zip wurde entpackt.");	 					
						 					
						 					File delete = new File(""+kundenpfad+"\\"+user+"\\"+servertyp+"\\"+path+"\\"+path+".zip");
						 					if(delete.delete())
						 					{
						 						writer.appendData("Server", "Da das Image gedownloadet und entpackt wurde, wird das File gelöscht.");
						 					}
						 					else
						 					{
						 						writer.appendData("Server", "Das File konnte nicht gelöscht werden, ist es nicht vorhanden oder wurde schon gelöscht?");
						 					}
										
						 					File xml = new File("Configs/Gameserver/"+user+""+path+".xml");
						 					try
						 					{
						 						if(xml.exists())
						 						{
						 							DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
						 							DocumentBuilder builder = factory.newDocumentBuilder();
						 							Document document = builder.parse(xml);
									        
						 							document.getElementsByTagName("status").item(0).setTextContent("1");
						 							TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(new FileOutputStream(xml)));
						 							writer.appendData("Server", "Der Gameserverstatus wurde von 0 auf 1 gesetzt.");						 							
						 						}
						 						else
						 						{
						 							writer.appendData("Server", "Das Configfile von den Gameservern ist nicht vorhanden.");
						 						}
						 					}
						 					catch(Exception ex)
						 					{
						 						writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
						 					}
						 				}
						 			}
						 			catch(Exception ex)
						 			{
						 				writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
						 			}
								}
								else
								{
									writer.appendData("Server", "Der Gameserver konnte nicht gelöscht werden.");
									handler.SendData("ID2");
								}
							}
							else
							{
								writer.appendData("Server", "Das Verzeichnis " + gpathdeleted + " konnte nicht gelöscht werden da es existiert.");
								writer.appendData("Server", "Die Gameserverconfig von " + user +" konnte nicht gelöscht werden da sie nicht existiert.");
								handler.SendData("ID2");
							}
						}
						catch(Exception ex)
						{
							writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
						}
					}
					else
					{
						handler.SendData("ID2");
						writer.appendData("Server", "Der Gameserver konnte nicht gestoppt werden, da der Exename benötigt wird um die PID zu finden.");
					}
				}
				catch(Exception ex)
				{
					writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
				}
			}
			else
			{
				servertyp = split[3].replace(" ", "");
				user = split[5].replace(" ", "");
				path = split[7].replace(" ", "");
				name = split[9].replace(" ", "");
				imageserverurl = split[11].replace(" ", "");
				
				File daemonconfig = new File("Configs/Gameserver/"+user+""+path+".xml");
	 			try 
	 			{
	 				if(daemonconfig.createNewFile())
	 				{
	 					DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	 					DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
						
	 					doc = docBuilder.newDocument();
	 					Element rootElement = doc.createElement("Gameserver");
	 					doc.appendChild(rootElement);
				
	 					Element config = doc.createElement("Config");
	 					rootElement.appendChild(config);
				 
	 					Element servertypchild = doc.createElement("servertyp");
	 					servertypchild.appendChild(doc.createTextNode(servertyp));
	 					config.appendChild(servertypchild);
							
	 					Element userchild = doc.createElement("user");
	 					userchild.appendChild(doc.createTextNode(user));
						config.appendChild(userchild);
				 
						Element pathchild = doc.createElement("path");
						pathchild.appendChild(doc.createTextNode(path));
						config.appendChild(pathchild);
				 
						Element namechild = doc.createElement("name");
						namechild.appendChild(doc.createTextNode(name));
						config.appendChild(namechild);
					
						Element imageserverurlchild = doc.createElement("imageserverurl");
						imageserverurlchild.appendChild(doc.createTextNode(imageserverurl));
						config.appendChild(imageserverurlchild);
					
						Element statuschild = doc.createElement("status");
						statuschild.appendChild(doc.createTextNode("0"));
						config.appendChild(statuschild);
					
						Element exename = doc.createElement("exename");
						exename.appendChild(doc.createTextNode("0"));
						config.appendChild(exename);
					
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						DOMSource source = new DOMSource(doc);
						StreamResult result = new StreamResult(new File("Configs/Gameserver/"+user+""+path+".xml"));
						transformer.transform(source, result);
	 				}
	 				else
	 				{	
	 					writer.appendData("Server", "Daemonconfigfile konnte nicht erstellt werden!");
	 				}
	 			}
	 			catch(Exception ex)
	 			{
	 				writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
	 			}
	 			
	 			try
	 			{
	 				handler.SendData("ID1");
	 				downloadimage = new ImageServerDownload(handler, servertyp, user, path, name, imageserverurl, kundenpfad);
	 				downloadthread = new Thread(downloadimage);
	 				downloadthread.start();
	 				downloadthread.join();
				
	 				unpackzip = new ZipEntpacker(""+kundenpfad+"\\"+user+"\\"+servertyp+"\\"+path+"\\"+path+".zip", ""+kundenpfad+"//"+user+"//"+servertyp+"//"+path+"");
	 				unpackthread = new Thread(unpackzip);
				
	 				if(downloadthread.getState() == Thread.State.RUNNABLE)
	 				{
	 					writer.appendData("Server", "Das Image "+name+".zip wird noch heruntergeladen.");
	 				}
				
	 				if(downloadthread.getState() == Thread.State.TERMINATED)
	 				{
	 					writer.appendData("Server", "Das Image "+name+".zip wurde heruntergeladen.");
	 					unpackthread.start();
	 					unpackthread.join();
	 				}
				
	 				if(unpackthread.getState() == Thread.State.RUNNABLE)
	 				{
	 					writer.appendData("Server", "Das Image "+name+".zip wird noch entpackt.");
	 				}
				
	 				if(unpackthread.getState() == Thread.State.TERMINATED)
	 				{
	 					writer.appendData("Server", "Das Image "+name+".zip wurde entpackt.");
					
	 					File delete = new File(""+kundenpfad+"\\"+user+"\\"+servertyp+"\\"+path+"\\"+path+".zip");
	 					if(delete.delete())
	 					{
	 						writer.appendData("Server", "Da das Image gedownloadet und entpackt wurde, wird das File gelöscht.");
	 					}
	 					else
	 					{
	 						writer.appendData("Server", "Das File konnte nicht gelöscht werden, ist es nicht vorhanden oder wurde schon gelöscht?");
	 					}
					
	 					File xml = new File("Configs/Gameserver/"+user+""+path+".xml");
	 					
	 					try
	 					{
	 						if(xml.exists())
	 						{
	 							DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	 							DocumentBuilder builder = factory.newDocumentBuilder();
	 							Document document = builder.parse(xml);
				        
	 							document.getElementsByTagName("status").item(0).setTextContent("1");
	 							TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(new FileOutputStream(xml)));
	 							writer.appendData("Server", "Der Gameserverstatus wurde von 0 auf 1 gesetzt.");	 						
	 						}
	 						else
	 						{
	 							writer.appendData("Server", "Das Configfile von den Gameservern ist nicht vorhanden.");
	 						}
	 					}
	 					catch(Exception ex)
	 					{
	 						writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
	 					}
	 				}
	 			}
	 			catch(Exception ex)
	 			{
	 				writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
	 			}
			}
		}
		
		if(data.startsWith("'games'"))
		{
			String replace = data.replace("|", "");
			String endreplace = replace.replace("'", " ");
			String[] split = endreplace.split(" ");
			
			if(split[3].startsWith("status")) 
			{
				String username = split[5].replace(" ", "");
				String pathk = split[9].replace(" ", "");
				
				try
				{
					File xml = new File("Configs/Gameserver/"+username+""+pathk+".xml");
					if(xml.exists())
					{
						DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = factory.newDocumentBuilder();
						Document document = builder.parse(xml);
			        
						String status = document.getElementsByTagName("status").item(0).getTextContent();
						TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(new FileOutputStream(xml)));
						
						if(Integer.parseInt(status) == 1)
						{
							writer.appendData("Server", "Der Gameserver ist startklar.");
							handler.SendData("ID1");
						}
						else
						{
							writer.appendData("Server", "Der Downloadthread / Entpack Thread ist noch nicht fertig, bitte haben sie etwas Geduld.");
							handler.SendData("ID2");
						}
					}
					else
					{
						writer.appendData("Server", "Das Configfile von den Gameservern ist nicht vorhanden.");
					}
				}
				catch(Exception ex)
				{
					writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
				}
			}
			
			if(split[3].startsWith("start"))
			{
				String user = split[5];
				String data = split[13];
				String startbefehl = split[13];
				String path = split[9];
				String exename = split[13];
				
				String replacepath = path.replace("/", "\\");
				String pathgmo = ""+kundenpfad+"\\"+user+"\\server\\"+replacepath+"";
				
				
				if(split[13].contains(".bat"))
				{
					startbefehl = " "+split[13]+" "+split[14]+" "+split[15]+" "+split[16]+" "+split[17]+" "+split[18]+" "+split[19]+" "+split[20]+" "+split[21]+" "+split[22]+"";
				}
				
				try
				{				
					StartGameserver startgameserver = new StartGameserver(startbefehl, pathgmo, path, user);
					Thread thread = new Thread(startgameserver);
					thread.start();
					thread.join();	
					
					if(thread.getState() == Thread.State.TERMINATED)
					{
						handler.SendData("ID1");
						writer.appendData("Server", "Der Gameserver wurde gestartet.");
					}
					else
					{
						handler.SendData("ID2");
						writer.appendData("Server", "Der Gameserver konnte nicht gestartet werden, da der Exename benötigt wird um die PID zu finden.");
					}
				}
				catch(Exception ex)
				{
					writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
				}
			}	
			
			if(split[3].startsWith("stop"))
			{
				String user = split[5];
				String data = split[13];
				String startbefehl = split[13];
				String path = split[9];
				String gamepath = ""+kundenpfad+"\\"+user+"\\server\\"+path+"";
				
				try
				{
					StopGameserver stopgameserver = new StopGameserver(data, gamepath);
					Thread thread = new Thread(stopgameserver);
					thread.start();
					thread.join();
					
					if(thread.getState() == Thread.State.TERMINATED)
					{
						handler.SendData("ID1");
						writer.appendData("Server", "Der Gameserver wurde gestoppt.");
					}
					else
					{
						handler.SendData("ID2");
						writer.appendData("Server", "Der Gameserver konnte nicht gestoppt werden, da der Exename benötigt wird um die PID zu finden.");
					}
				}
				catch(Exception ex)
				{
					writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
				}
			}
			
			if(split[3].startsWith("check"))
			{
				String user = split[5];
				String pathk = split[9];
				
				try
				{
					File file = new File(""+kundenpfad+"\\"+user+"\\server\\"+pathk+"");
					if(file.exists())
					{
						handler.SendData("ID1");
						writer.appendData("Server", "Der Verzeichnischeck war erfolgreich.");
					}
					else
					{
						handler.SendData("ID2");
						writer.appendData("Server", "Der Verzeichnischeck war nicht erfolgreich.");
					}
				}
				catch(Exception ex)
				{
					writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
				}
			}
		}
		
		if(data.startsWith("'delete'"))
		{
			String replace = data.replace("|", "");
			String endreplace = replace.replace("'", " ");
			String[] split = endreplace.split(" ");
			
			String user = split[5];
			String path = split[7];
			String gamekürzel = path;
			
			String gameserverconfigfile = "Configs\\Gameserver";
			String gamepath = ""+kundenpfad+"\\"+user+"\\server";
			String file = ""+user+""+path+".xml";
			
			String gamepathexists = ""+kundenpfad+"\\"+user+"\\server\\"+gamekürzel+"";
			String gameconfigexists = ""+gameserverconfigfile+"\\"+file+"";
						
			try
			{
				StopGameserver stopgameserver = new StopGameserver(data, gamepath);
				Thread thread = new Thread(stopgameserver);
				thread.start();
				thread.join();
				
				if(thread.getState() == Thread.State.TERMINATED)
				{
					writer.appendData("Server", "Der Gameserver wurde gestoppt.");
					
					try
					{
						File gpathdeleted = new File(gamepathexists);
						File gpathdeletedconfig = new File(gameconfigexists);		
						
						if(gpathdeleted.exists() || gpathdeletedconfig.exists())
						{
							DelGameserver deletegameserver = new DelGameserver(gamepath, gamekürzel, gameserverconfigfile, file);
							Thread deletethread = new Thread(deletegameserver);
							deletethread.start();
							deletethread.join();
							
							if(deletethread.getState() == Thread.State.TERMINATED)
							{
								writer.appendData("Server", "Der Gameserver wurde gelöscht.");
								handler.SendData("ID1");
							}
							else
							{
								writer.appendData("Server", "Der Gameserver konnte nicht gelöscht werden.");
								handler.SendData("ID2");
							}
						}
						else
						{
							writer.appendData("Server", "Das Verzeichnis " + gpathdeleted + " konnte nicht gelöscht werden da es existiert.");
							writer.appendData("Server", "Die Gameserverconfig von " + user +" konnte nicht gelöscht werden da sie nicht existiert.");
							handler.SendData("ID2");
						}
					}
					catch(Exception ex)
					{
						writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
					}
				}
				else
				{
					handler.SendData("ID2");
					writer.appendData("Server", "Der Gameserver konnte nicht gestoppt werden, da der Exename benötigt wird um die PID zu finden.");
				}
			}
			catch(Exception ex)
			{
				writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
			}
		}
		
		if(data.startsWith("'userdel'"))
		{
			String replace = data.replace("|", "");
			String endreplace = replace.replace("'", " ");
			String[] split = endreplace.split(" ");
			
			String user = split[5];
			String ftpdeleteconfigfile = "Configs\\FTP";
			String filename = "user.properties";
			
			try
			{
				DelFTPUser delftpuser = new DelFTPUser(ftpdeleteconfigfile, filename);
				File deleteconfigfile = new File(ftpdeleteconfigfile);
				
				if(deleteconfigfile.exists())
				{
					Thread deluser = new Thread(delftpuser);
					deluser.start();
					deluser.join();
					
					if(deluser.getState() == Thread.State.TERMINATED)
					{
						writer.appendData("Server", "Der FTP user wurde erfolgreich gelöscht.");
						handler.SendData("ID1");
					}
					else
					{
						writer.appendData("Server", "Der FTP User konnte nicht gelöscht werden.");
						handler.SendData("ID2");
					}
				}
				else
				{
					handler.SendData("ID2");
					writer.appendData("Server", "Das FTP Configfile wurde nicht gelöscht, da es nicht existiert.");
				}
			}
			catch(Exception ex)
			{
				writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
			}
		}
		
		if(data.startsWith("'usermod'"))
		{
			String replace = data.replace("|", "");
			String endreplace = replace.replace("'", " ");
			String[] split = endreplace.split(" ");
			
			String user = split[3];
			String password = split[5];
			String pfad = ""+kundenpfad+"\\"+user+"";
			
			try
			{
				if(ftpserver.changeFTPData(user, password, pfad) == true)
				{
					writer.appendData("Server", "Das FTP Password von User " + user + " wurde geändert.");
					handler.SendData("ID1");
				}
				else
				{
					writer.appendData("Server", "Das FTP Password von User " + user + " konnte nicht geändert werden.");
					handler.SendData("ID2");
				}
				
			}
			catch(Exception ex)
			{
				writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
			}
		}
		
		if(data.contains("cd /home/skripte;sudo ./dedicated 'info'"))
		{
			try
			{	
				handler.SendData("ID1");
			}
			catch(Exception ex)
			{
				writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
			}
		}
		
		if(data.contains("cd /home/skripte;sudo ./dedicated 'reboot'"))
		{
			try
			{
				RestartGameserver restartroot = new RestartGameserver("shutdown /r /t 0");
				Thread restart = new Thread(restartroot);
				restart.start();
				restart.join();
				
				if(restart.getState() == Thread.State.TERMINATED)
				{
					handler.SendData("ID1");
				}
				else
				{
					handler.SendData("ID2");
				}
			}
			catch(Exception ex)
			{
				writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage() + "");
			}
		}
	}
}
