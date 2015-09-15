import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;

public class XMLData {

	
	private static XMLData instance = null;
	private static NodeList nodeList;
	private static Document document = null;
	private static File file;
	
	
	/** This function creates File chooser which opens only .xml files. 
	 * Xml files is locked setting (filter) so no other data type can be open with it.
	 * Ater the file is selected method parses the file and returns instance of the class (sigleton)
	 */
	public static XMLData getInstance() {
		if(instance == null) {

			final JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(null);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();

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
					document = builder.parse(file);
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				document.getDocumentElement().normalize();
				nodeList = document.getElementsByTagName(document.getDocumentElement().getChildNodes().item(1).getNodeName());
			}
		}
			return instance;
		}
	
	
	/**
	 * getLength, getItem(int i) and getDocumet are getters methods used to return same thing.
	 */
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
	
	public File getFile()
	{
		return file;
	}
}
