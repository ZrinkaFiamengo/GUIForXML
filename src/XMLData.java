import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

public class XMLData {

	
	private static XMLData instance = null;
	private static NodeList nodeList;
	private static Document document = null;
	
	
	public static XMLData getInstance() {
		if(instance == null) {
			instance = new XMLData();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {
				builder = factory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				document = builder.parse("C:\\Users\\ezrifia\\Desktop\\configuration.xml");
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			document.getDocumentElement().normalize();
			nodeList = document.getElementsByTagName("Configuration");
		}
		return instance;
	}
	
	public int getLength()
	{
		return nodeList.getLength();
	}
	
	public Node getItem(int i)
	{
		return nodeList.item(i);
	}
	
	public Document getDocument()
	{
		return document;
	}
	
	public void getNodeNames()
	{
		for (int i=0; i<nodeList.getLength();i++)
		{
			Node node = nodeList.item(i);
			System.out.println(node.getNodeName());
			NodeList children = node.getChildNodes();
			int num = 0;
			for (int j=0; j<children.getLength(); j++)
			{
				Node child = children.item(j);
				if (!child.getNodeName().contains("#text")){
					System.out.println(num +"--->"+child.getNodeName());
					num++;
				}
			}

		}
	}
}
