import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

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
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;


public class MainWindow {

	protected Shell shell;

	Label [] labels = new Label[0];
	Text [] texts = new Text[0];
	Button [] buttons = new Button[0];
	Group [] groups = new Group[0];
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
		shell = new Shell();
		createContents();
		shell.open();
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
		shell.setText("XML Settings");
		shell.setSize(270, 100);

		FillLayout fl= new FillLayout();
		shell.setLayout(fl);

		final ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setBounds(shell.getBounds());

		final Composite composite = new Composite(scrolledComposite, SWT.NONE);

		Button openFile = new Button(composite, SWT.NONE);
		openFile.setBounds(20, 20, 200, 25);
		openFile.setText("Choose XML file");


		openFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				Button openFileButton = (Button) e.getSource();
				openFileButton.setVisible(false);
				try {
					for(int i=0; i<XMLData.getInstance().getLength(); i++)
					{
						Node node = XMLData.getInstance().getItem(i);

						if (node.getNodeType() == Node.ELEMENT_NODE)
						{
							Element elem =(Element)node;
							
							
							Group[] tempGroups = new Group[groups.length + 1];
						    System.arraycopy(groups, 0, tempGroups, 0, groups.length);
						    groups = tempGroups;
						    
							groups[i] = new Group(composite, SWT.NONE);
							groups[i].setText(node.getNodeName() +" " + i);


							NodeList children = node.getChildNodes();
							int num = 0;
							for (int j=0; j<children.getLength(); j++)
							{
								Node child = children.item(j);
								if (!child.getNodeName().contains("#text")){
									MakeBlock(i, num*30, elem, child.getNodeName());
									numberOfBlocks++;
									num++;
								}
							}


							if(i==0)
							{
								groups[i].setBounds(10, 10, 230, num*30 + 60);
								shell.setSize(270, num*30 + 60);
							}
							else
							{
								groups[i].setBounds(10, groups[i-1].getLocation().y + groups[i-1].getBounds().height +20 , 230, num*30 + 60);
								if(shell.getSize().y<700)
								{
									shell.setSize(270, shell.getSize().y + num*30 + 120);
									scrolledComposite.setAlwaysShowScrollBars(true);
								}
							}
							
							Button[] tempButtons = new Button[buttons.length + 1];
						    System.arraycopy(buttons, 0, tempButtons, 0, buttons.length);
						    buttons = tempButtons;

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
									
									Control [] childrenOfTheGroup =  groups[currentGroup].getChildren();
									Text [] textFieldsOfTheGroup = new Text [0];
									for(int i=0; i<childrenOfTheGroup.length; i++)
										if(childrenOfTheGroup[i] instanceof Text)
										{
											Text[] tempTexts = new Text[textFieldsOfTheGroup.length + 1];
										    System.arraycopy(textFieldsOfTheGroup, 0, tempTexts, 0, textFieldsOfTheGroup.length);
										    textFieldsOfTheGroup = tempTexts;
										    textFieldsOfTheGroup[textFieldsOfTheGroup.length-1] = (Text) childrenOfTheGroup[i];
										}

									NodeList children = node.getChildNodes();
									int num = 0;

									System.out.println(textFieldsOfTheGroup.length);
									
									for (int j=0; j<children.getLength(); j++)
									{
										Node child = children.item(j);
										if (!child.getNodeName().contains("#text"))
										{
											child.setTextContent(textFieldsOfTheGroup[num].getText());
											num++;
										}
									}

									TransformerFactory transformerFactory = TransformerFactory.newInstance();
									Transformer transformer = null;
									try {
										transformer = transformerFactory.newTransformer();
									} catch (TransformerConfigurationException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									DOMSource source = new DOMSource(XMLData.getInstance().getDocument());
									StreamResult result = new StreamResult(XMLData.getInstance().getFile());
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
				catch (Exception exception)
				{
					System.out.println("Erorr");
					exception.printStackTrace();
				}
			}
		});

		scrolledComposite.setContent(composite);

		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);


		scrolledComposite.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				org.eclipse.swt.graphics.Rectangle r = scrolledComposite.getClientArea();
				scrolledComposite.setMinSize(composite.computeSize(r.width, SWT.DEFAULT));
			}
		});
	}

	private void MakeBlock(int i, int move, Element elem, String labelText)
	{
		
		Label[] tempLabels = new Label[labels.length + 1];
	    System.arraycopy(labels, 0, tempLabels, 0, buttons.length);
	    labels = tempLabels;
	    
		labels[numberOfBlocks] = new Label(groups[i], SWT.PUSH);
		labels[numberOfBlocks].setBounds(10, 30 + move, 55, 15);
		labels[numberOfBlocks].setText(labelText);
		
		Text[] tempTexts = new Text[texts.length + 1];
	    System.arraycopy(texts, 0, tempTexts, 0, texts.length);
	    texts = tempTexts;

		texts[numberOfBlocks] = new Text(groups[i], SWT.BORDER);
		texts[numberOfBlocks].setBounds(130, 30 + move , 76, 21);
		texts[numberOfBlocks].setText(elem.getElementsByTagName(labelText).item(0).getTextContent());  
	}
	
	

}
