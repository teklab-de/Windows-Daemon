package Forms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.TransferHandler;
import javax.swing.text.DefaultCaret;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import Enviroment.Enviroment;
import Networking.Daemon;
import Networking.FTPServer;
import Networking.Writer;

public class Form {
	public int width;
	public int height;
	
	public String usage;
	public String ftpip;
	public String ftpport;
	public String ftpusername;
	public String ftppassword;
	public String ftppfad;
	public String daemonport;
	public String daemonpasswort;
	public String kundenpfad;
	
	public boolean serverconfigexists = false;
	
	public JFrame frame;
	public JFrame framef;
	public JPanel panel;
	public FlowLayout layout;
	
	public JProgressBar loadconfigbar;
	public JTextArea logbox;
	public JScrollPane scrollpane;
	
	public JLabel daemonportlabel;
	public JLabel ftpserverlabel;
	public JLabel ftpserverportlabel;
	public JLabel ftpserverusernamelabel;
	public JLabel ftpserverpasswordlabel;
	public JLabel ftpserverpfadlabel;
	public JLabel daemonpasswortpfadlabel;
	public JLabel kundenpfadlabel;
	
	public JTextField daemonportbox;
	public JTextField ftpserverusernamebox;
	public JTextField ftpserverportbox;
	public JTextField ftpserverbox;
	public JPasswordField ftpserverpasswordbox;
	public JTextField ftpserverpfadbox;
	public JPasswordField daemonpasswortbox;
	public JTextField kundenpfadbox;
	
	public JButton start;
	public JButton stop;
	public JButton clear;
	
	public Writer writer = new Writer();
	public FTPServer ftpserver;
	public Daemon daemonserver;
	
	public Form(int width, int height) throws IOException 
	{
		this.width = width;
		this.height = height;
		this.usage = readableFileSize(Long.parseLong(getUsage()));
		this.Run(true);
	}
	
	public String getCopyright()
	{
		return "Copyright by Steekarlkani - Tekbase Daemon v1 Business Customers";
	}
	
	public Runtime getRuntime()
	{
		return Runtime.getRuntime();
	}
	
	public String getUsage()
	{
		return getRuntime().totalMemory() - getRuntime().freeMemory() + "";
	}
	
	public static String readableFileSize(long size)
	{
	    if(size <= 0) return "0";
	   
	    final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	
	public boolean existsFile(String filename)
	{
		File file = new File(filename);
		
		if(file.exists())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean Run(boolean bool) throws IOException
	{
		if(bool == true)
		{
			if(existsFile("Configs/Daemon/daemonconfig.xml"))
			{
				serverconfigexists = true;
			}
			else
			{
				serverconfigexists = false;
			}
			
			if(serverconfigexists == false)
			{
				frame = new JFrame("[Windows Daemon by Steekarlkani] - [Ram-Verbrauch: " + this.usage + "]");
			      
			    JPanel panel = new JPanel();
			    panel.setPreferredSize(new Dimension(768, 40));
			    panel.setBackground(Color.darkGray);
			    panel.setLayout(new FlowLayout(FlowLayout.LEADING));
				
				JLabel copyrightlabel = new JLabel(getCopyright());
			    copyrightlabel.setForeground(Color.WHITE);
			      
			    ftpserverlabel = new JLabel("FTP-Serverip:");
			    ftpserverbox = new JTextField();
			    ftpserverbox.setPreferredSize(new Dimension(770, 25));
			      
			    ftpserverportlabel = new JLabel("FTP-Serverport:");
			    ftpserverportbox = new JTextField();
			    ftpserverportbox.setPreferredSize(new Dimension(770, 25));
			      
			    ftpserverusernamelabel = new JLabel("FTP-Username:");
			    ftpserverusernamebox = new JTextField();
			    ftpserverusernamebox.setPreferredSize(new Dimension(770, 25));
			      
			    ftpserverpasswordlabel = new JLabel("FTP-Password:");
			    ftpserverpasswordbox = new JPasswordField();
			    ftpserverpasswordbox.setPreferredSize(new Dimension(770, 25));
			      
			    ftpserverpfadlabel = new JLabel("FTP-Homeverzeichnis:");
			    ftpserverpfadbox = new JTextField();
			    ftpserverpfadbox.setPreferredSize(new Dimension(770, 25));
			    
			    daemonportlabel = new JLabel("Daemonport:");
			    daemonportbox = new JTextField();
			    daemonportbox.setPreferredSize(new Dimension(770, 25));
			    
			    daemonpasswortpfadlabel = new JLabel("Daemonpassword:");
			    daemonpasswortbox = new JPasswordField();
			    daemonpasswortbox.setPreferredSize(new Dimension(770, 25));
				
			    kundenpfadlabel = new JLabel("Kundenspeicherort:");
			    kundenpfadbox = new JTextField();
			    kundenpfadbox.setPreferredSize(new Dimension(770, 25));
			    
			    start = new JButton("Config erstellen");
			    clear = new JButton("Form schliessen");
				
				start.addActionListener(new ActionListener(){
		  	 	public void actionPerformed(ActionEvent arg0) {
		  	 		if(ftpserverbox.getText().isEmpty() || ftpserverportbox.getText().isEmpty() || ftpserverusernamebox.getText().isEmpty() || ftpserverpasswordbox.getText().isEmpty() || ftpserverpfadbox.getText().isEmpty() || daemonportbox.getText().isEmpty() || daemonpasswortbox.getText().isEmpty() || kundenpfadbox.getText().isEmpty())
		  	 		{
		  	 			JOptionPane.showMessageDialog(null, "Felder nicht ausgefühlt.", "Bitte alle Felder ausfühlen...", JOptionPane.OK_CANCEL_OPTION);	
		  	 		}
		  	 		else
		  	 		{
		  	 			File daemonconfig = new File("Configs/Daemon/daemonconfig.xml");
		  	 			try {
		  	 				if(daemonconfig.createNewFile())
		  	 				{
		  	 					DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		  	 					DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
									
		  	 					Document doc = docBuilder.newDocument();
		  	 					Element rootElement = doc.createElement("Server");
								doc.appendChild(rootElement);
							
								Element config = doc.createElement("Config");
								rootElement.appendChild(config);
							 
								Element ftpip = doc.createElement("ftpip");
								ftpip.appendChild(doc.createTextNode(ftpserverbox.getText()));
								config.appendChild(ftpip);
										
								Element ftpport = doc.createElement("ftpport");
								ftpport.appendChild(doc.createTextNode(ftpserverportbox.getText()));
								config.appendChild(ftpport);
							 
								Element ftpusername = doc.createElement("ftpusername");
								ftpusername.appendChild(doc.createTextNode(ftpserverusernamebox.getText()));
								config.appendChild(ftpusername);
							 
								Element ftppassword = doc.createElement("ftppassword");
								ftppassword.appendChild(doc.createTextNode(ftpserverpasswordbox.getText()));
								config.appendChild(ftppassword);
								
								Element ftppfad = doc.createElement("ftppfad");
								ftppfad.appendChild(doc.createTextNode(ftpserverpfadbox.getText()));
								config.appendChild(ftppfad);
								
								Element daemonport = doc.createElement("daemonport");
								daemonport.appendChild(doc.createTextNode(daemonportbox.getText()));
								config.appendChild(daemonport);
									
								Element daemonpfad = doc.createElement("daemonpasswort");
								daemonpfad.appendChild(doc.createTextNode(daemonpasswortbox.getText()));
								config.appendChild(daemonpfad);
								
								Element kundenpfad = doc.createElement("kundenpfad");
								kundenpfad.appendChild(doc.createTextNode(kundenpfadbox.getText()));
								config.appendChild(kundenpfad);
						
								Element status = doc.createElement("status");
								status.appendChild(doc.createTextNode("0"));
								config.appendChild(status);
								
								TransformerFactory transformerFactory = TransformerFactory.newInstance();
								Transformer transformer = transformerFactory.newTransformer();
								DOMSource source = new DOMSource(doc);
								StreamResult result = new StreamResult(new File("Configs/Daemon/daemonconfig.xml"));
								transformer.transform(source, result);
										
					  	 		JOptionPane.showMessageDialog(null, "Configfile wurde erstellt, bitte starten sie denn Daemon einmal neu.", "Daemon neustarten!", JOptionPane.OK_CANCEL_OPTION);	
					  	 		
					  	 		frame.setVisible(false);
					  	 		frame.dispose();
		  	 				}
		  	 				else
		  	 				{
		  	 					writer.appendData("Server", "Daemonconfigfile konnte nicht erstellt werden!");
		  	 				}
		  	 			} 
		  	 			
		  	 			catch (IOException e) 
		  	 			{
		  	 				writer.appendData("Server", "Exception wurde ausgelösst " + e.getMessage()+"");
						} 
							    
		  	 			catch (ParserConfigurationException e) 
		  	 			{
		  	 				writer.appendData("Server", "Exception wurde ausgelösst " + e.getMessage()+"");
		  	 			} 
						    
		  	 			catch (TransformerConfigurationException e) 
		  	 			{
		  	 				writer.appendData("Server", "Exception wurde ausgelösst " + e.getMessage()+"");
		  	 			} 
						    
		  	 			catch (TransformerException e) 
		  	 			{
		  	 				writer.appendData("Server", "Exception wurde ausgelösst " + e.getMessage()+"");
		  	 			}
		  	 		}
		  	 	 }
		  	});	
		  	 	
		   clear.addActionListener(new ActionListener()
		   {
		  	  public void actionPerformed(ActionEvent e)
		  	  {
		  		  JOptionPane.showMessageDialog(null, "Programm schliessen", "Der Frame wird geschlossen, und das Programm gestoppt", JOptionPane.OK_CANCEL_OPTION);	
		  	 	  frame.dispose();
		  	  }
		   });
		   
		   frame.getContentPane().add(ftpserverlabel);
		   frame.getContentPane().add(ftpserverbox);
		      
		   frame.getContentPane().add(ftpserverportlabel);
		   frame.getContentPane().add(ftpserverportbox);
		      
		   frame.getContentPane().add(ftpserverusernamelabel);
		   frame.getContentPane().add(ftpserverusernamebox);
		      
		   frame.getContentPane().add(ftpserverpasswordlabel);
		   frame.getContentPane().add(ftpserverpasswordbox);
		      
		   frame.getContentPane().add(ftpserverpfadlabel);
		   frame.getContentPane().add(ftpserverpfadbox);
		   
		   frame.getContentPane().add(daemonportlabel);
		   frame.getContentPane().add(daemonportbox);
		   
		   frame.getContentPane().add(daemonpasswortpfadlabel);
		   frame.getContentPane().add(daemonpasswortbox);
		   
		   frame.getContentPane().add(kundenpfadlabel);
		   frame.getContentPane().add(kundenpfadbox);
		     
		   frame.getContentPane().add(panel);
				      
		   panel.add(start);
		   panel.add(clear);
		   panel.add(copyrightlabel);
				    
		   frame.getContentPane().setLayout(new FlowLayout(FlowLayout.LEADING));
		   frame.setSize(new Dimension(width, height));
		   frame.setVisible(bool);
		   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    
		   return true;
		}
		else
		{
			framef = new JFrame("[Windows Daemon by Steekarlkani] - [Ram-Verbrauch: " + this.usage + "]");
			
			loadconfigbar = new JProgressBar(0, 100);
			loadconfigbar.setStringPainted(true);
			loadconfigbar.setPreferredSize(new Dimension(767, 25));
			
			logbox = new JTextArea();
			logbox.setPreferredSize(new Dimension(750, 250));
			scrollpane = new JScrollPane(logbox);
			scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			
			File imageFile = new File("logo.png");
			BufferedImage image = null;
			
			try 
			{
			   image = ImageIO.read(imageFile);
			}
			catch(Exception ex)
			{
				writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage()+"");
			}
			
			new Thread(new Runnable()
			{
				public void run()
				{
					int x = 0;
					
					while(x <= 100)
					{
						x++;
						loadconfigbar.setValue(x);
						
						if(x == 10)
						{
							loadconfigbar.setString("Configfile wird gesucht, bitte warten sie...");
						}
						
						if(x == 50)
						{
							loadconfigbar.setString("Configfile wird gelesen, bitte warten sie...");
						}
						
						
						if(x == 100)
						{
							try 
							{
								loadconfigbar.setString("Configfile wurde gelesen!");
								File file = new File("Configs/Daemon/daemonconfig.xml");
								DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
								DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
								Document doc = dBuilder.parse(file);
								doc.getDocumentElement().normalize();
						
								NodeList nList = doc.getElementsByTagName("Server");
								for(int temp = 0; temp < nList.getLength(); temp++)
								{
									Node nNode = nList.item(temp);
									if (nNode.getNodeType() == Node.ELEMENT_NODE)
									{
										Element xElement = (Element) nNode;
										ftpip =  xElement.getElementsByTagName("ftpip").item(0).getTextContent();
										ftpport =  xElement.getElementsByTagName("ftpport").item(0).getTextContent();
										ftpusername =  xElement.getElementsByTagName("ftpusername").item(0).getTextContent();
										ftppassword =  xElement.getElementsByTagName("ftppassword").item(0).getTextContent();
										ftppfad =  xElement.getElementsByTagName("ftppfad").item(0).getTextContent();
										daemonport =  xElement.getElementsByTagName("daemonport").item(0).getTextContent();
										daemonpasswort =  xElement.getElementsByTagName("daemonpasswort").item(0).getTextContent();
										kundenpfad =  xElement.getElementsByTagName("kundenpfad").item(0).getTextContent();
										
										ftpserver = new FTPServer(ftpip, ftpusername, ftppassword, Integer.parseInt(ftpport), ftppfad);
										Thread runftpserver = new Thread(ftpserver);
										runftpserver.start();
														
										daemonserver = new Daemon(Integer.parseInt(daemonport), daemonpasswort, kundenpfad, logbox, ftpserver);
										Thread runserver = new Thread(daemonserver);
										runserver.start();
										framef.setState(1);						
										
										logbox.append("Serverkonfiguration");
										logbox.append("\n--------------------------------------------------------------------------");
										logbox.append("\nFTP-IP: " + ftpip + "");
										logbox.append("\nFTP-Port: " + ftpport + "");
										logbox.append("\nFTP-Username: " + ftpusername + "");
										logbox.append("\nFTP-Password: " + ftppassword + "");
										logbox.append("\nFTP-Homeverzeichnis: " + ftppfad + "");
										logbox.append("\nDaemon-Port: " + daemonport + "");
										logbox.append("\nDaemon-Password: "  + daemonpasswort +"");
										logbox.append("\nKunden-Pfad: "  + kundenpfad +"");
										
										logbox.append("\n--------------------------------------------------------------------------");
										logbox.append("\nDaemon minimiert sich sobald sie auf Starten klicken, die Logs finden sie ihm Ordner Logs.");
										logbox.append("\n--------------------------------------------------------------------------");
									}
								}
							}
							
							catch(Exception ex)
							{
								writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage()+"");
							}
						}
						
						try 
						{
							Thread.sleep(20);
						}
						catch(Exception ex)
						{
							writer.appendData("Server", "Eine Exception wurde ausgelösst " + ex.getMessage()+"");
						}
					}
				}
			}).start();
		      
		    FlowLayout buttonlayout = new FlowLayout();
		    buttonlayout.setAlignment(0);
		      
		    JPanel panel = new JPanel();
		    panel.setPreferredSize(new Dimension(768, 40));
		    panel.setBackground(Color.darkGray);
		    panel.setLayout(buttonlayout);
			
			JLabel copyrightlabel = new JLabel(getCopyright());
		    copyrightlabel.setForeground(Color.WHITE);
		    
		    stop = new JButton("Server stoppen");
		    clear = new JButton("Clear Form");
		    
		    stop.addActionListener(new ActionListener()
		    {
				public void actionPerformed(ActionEvent arg0) 
				{
	  	 			JOptionPane.showMessageDialog(null, "Daemon/FTP-Server wurden erfolgreich gestopppt", "Daemon/FTP-Server gestoppt", JOptionPane.OK_CANCEL_OPTION);	
					ftpserver.closeFTPServer();
					daemonserver.closeServer();
				}
		    });
		    
		    clear.addActionListener(new ActionListener()
		    {
			  	  public void actionPerformed(ActionEvent e)
			  	  {
			  	 	  logbox.setText("");
			  	  }
			});
		   
		    framef.getContentPane().add(new JLabel(new ImageIcon(image)));
		    framef.getContentPane().add(loadconfigbar);
		    framef.getContentPane().add(scrollpane);
		    framef.getContentPane().add(panel);
		    
		   	panel.add(stop);
		   	panel.add(clear);
		   	panel.add(copyrightlabel);
	
		   	framef.getContentPane().setLayout(new FlowLayout(FlowLayout.LEADING));
		   	framef.setSize(new Dimension(800, 430));
		   	framef.setVisible(bool);
		   	framef.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		   	
		    return true;
		  }
	   }
	   else
	   {
		    return false;
	   }
    } 

}
