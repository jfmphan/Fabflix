import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMLParser 
{
	private String file;
	private Document dom;
	private LinkedList<Works> works; 
	
	public XMLParser(String file)
	{
		this.file = file;
		works = new LinkedList<Works>();
	}
	
	public void parse()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			dom = builder.parse(file);
			parseDom();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void parseDom()
	{
		Element root = dom.getDocumentElement();
		
		NodeList list = root.getChildNodes();
		if(list != null)
		{	
			for(int i = 0; i < list.getLength(); i++)
			{
				Node e = list.item(i);
				addWork(e);
			}
		}
		else
		{
			System.out.println("Empty DOM List");
		}
		
	}
	
	private void addWork(Node e)
	{
	
		if(!e.getNodeName().equals("#text"))
		{
			Works work = new Works();
		
			NodeList list = e.getChildNodes();
	
			work.setGenre(e.getNodeName());
	
	//		String mdate = e.getAttributes().getNamedItem("mdate").getNodeValue();
	//		String key = e.getAttributes().getNamedItem("key").getNodeValue();
		
			
			for(int i = 0; i < list.getLength(); i++)
			{
				String tag  = list.item(i).getNodeName().replace("\n", "");
				String value = list.item(i).getTextContent().replace("\n", "");
			
				if(!tag.equals("#text"))
				{
					
					
					if(tag.equalsIgnoreCase("editor"))
					{
						work.setEditor(value);
					}
					else if(tag.equalsIgnoreCase("author"))
					{
						work.setAuthor(value);
					}
					else if(tag.equalsIgnoreCase("booktitle"))
					{
						work.setBookTitle(value);
					}
					else if(tag.equalsIgnoreCase("publisher"))
					{	
						work.setPublisher(value);
					}
					else
					{	
						work.addData(tag, value);
					}
				}
			}
			
			works.add(work);
		}

	}
	
	public LinkedList<Works> getDocs()
	{
		return works;
	}
	
	public void printDocs()
	{
		for(Works w: works)
		{
			Iterator<Entry<String, String>> it = w.getData().entrySet().iterator();
			
			while(it.hasNext())
			{
				Entry<String, String> entry = it.next();
				
				System.out.println(entry.getKey() + entry.getValue());
				
				it.remove();
			}
		}
	}
}
