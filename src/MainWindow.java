import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

import java.io.File;


import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class MainWindow {

	protected Shell shell;

	Label [] labels = new Label[100];
	Text [] texts = new Text[100];
	Button [] buttons = new Button[20];
	Group [] groups = new Group[20];
	int numberOfBlocks = 0;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setText("XML Settings");
		shell.setSize(280, 900);

		try {
			for(int i=0; i<XMLData.getInstance().getLength(); i++)
			{
				Node node = XMLData.getInstance().getItem(i);

				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element elem =(Element)node;

					groups[i] = new Group(shell, SWT.NONE);
					int place = 10 + numberOfBlocks*55;
					groups[i].setBounds(10, place, 230, 90);
					groups[i].setText("Configuration " + i);
					
					
					NodeList children = node.getChildNodes();
					int num = 0;
					for (int j=0; j<children.getLength(); j++)
					{
						Node child = children.item(j);
						if (!child.getNodeName().contains("#text")){
							MakeBlock(i, num*30, elem, child.getNodeName());
							numberOfBlocks++;
							groups[i].setBounds(10, place, 230, 90 + num*30);
							num++;
						}
					}

					buttons[i] = new Button(groups[i], SWT.PUSH);
					buttons[i].setBounds(10, ++num*30, 200, 25);
					buttons[i].setText("Send");
					buttons[i].addMouseListener(new MouseAdapter() {
						@Override
						public void mouseDown(MouseEvent e) {
							
							int currentGroup = 0;
							Button b = (Button)e.getSource();
							for(int i=0; i <XMLData.getInstance().getLength(); i++)
								if(buttons[i] == b)
									currentGroup = i;
							
							Node node = XMLData.getInstance().getItem(currentGroup);
							
							Element elem =(Element)node;
							
							Node [] nodes = new Node [10];
							
							nodes[0] = elem.getElementsByTagName("host").item(0);
							nodes[0].setTextContent(texts[currentGroup*3].getText());
							
							nodes[1] = elem.getElementsByTagName("username").item(0);
							nodes[1].setTextContent(texts[currentGroup*3+1].getText());
							
							nodes[2] = elem.getElementsByTagName("password").item(0);
							nodes[2].setTextContent(texts[currentGroup*3+2].getText());
							
							TransformerFactory transformerFactory = TransformerFactory.newInstance();
							Transformer transformer = null;
							try {
								transformer = transformerFactory.newTransformer();
							} catch (TransformerConfigurationException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							DOMSource source = new DOMSource(XMLData.getInstance().getDocument());
							StreamResult result = new StreamResult(new File("C:\\Users\\ezrifia\\Desktop\\configuration.xml"));
							try {
								transformer.transform(source, result);
							} catch (TransformerException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					});
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Erorr");
			e.printStackTrace();
		}
		
		XMLData.getInstance().getNodeNames();
	}

	private void MakeBlock(int i, int move, Element elem, String labelText)
	{
		labels[numberOfBlocks] = new Label(groups[i], SWT.PUSH);
		labels[numberOfBlocks].setBounds(10, 30 + move, 55, 15);
		labels[numberOfBlocks].setText(labelText);

		texts[numberOfBlocks] = new Text(groups[i], SWT.BORDER);
		texts[numberOfBlocks].setBounds(130, 30 + move , 76, 21);
		texts[numberOfBlocks].setText(elem.getElementsByTagName(labelText).item(0).getTextContent());  
	}
}
