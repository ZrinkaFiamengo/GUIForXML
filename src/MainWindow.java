import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

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
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class MainWindow { 



	protected Shell shell;

	/**
	 *  declaration of variables
	 */
	Label [] labels = new Label[0];
	Text [] texts = new Text[0];
	Button [] buttons = new Button[0];
	Group [] groups = new Group[0];
	int numberOfBlocks = 0;


	/**
	 *  Method that launches the application.
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
	 * Method used for opening the window. 
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}


	/**
	 * Method that creates java form after XML document is parsed. Method automatically generate labels for each node
	 * in XML document. In every label there are text fields equal the number of tags in each node. For each text field 
	 * user is allowed to write his values. After user set values and click Set button, all values from text fields will 
	 * be stored in appropriate tags in .xml document 
	 */

	protected void createContents() {
		shell = new Shell();
		shell.setText("XML Settings");
		shell.setSize(270, 100);

		/**
		 * Definition of scrolled composite with its properties 
		 */
		FillLayout fl= new FillLayout();
		shell.setLayout(fl);

		final ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setBounds(shell.getBounds());

		final Composite composite = new Composite(scrolledComposite, SWT.NONE);

		/**
		 * Creation of "Choose XML file" button with dimension properties
		 */

		Button openFile = new Button(composite, SWT.NONE);
		openFile.setBounds(20, 20, 200, 25);
		openFile.setText("Choose XML file");

		/**
		 * When application starts, window with "Chose XML File" pops up. That window allow user to select .xml file
		 * from computer. 
		 */
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

							/**
							 * Dynamic allocation of group. The number of groups is not known in the start of program
							 * so dynamic allocation is used
							 */
							Group[] tempGroups = new Group[groups.length + 1];
							System.arraycopy(groups, 0, tempGroups, 0, groups.length);
							groups = tempGroups;

							/**
							 * Creates new group and sets name of the group with the text in node. Group
							 * will contain text fields, labels and button
							 */

							groups[i] = new Group(composite, SWT.NONE);
							groups[i].setText(node.getNodeName() +" " + i);


							/**
							 * Puts all children node in a list and works with them
							 */
							NodeList children = node.getChildNodes();
							int num = 0;
							for (int j=0; j<children.getLength(); j++)
							{
								Node child = children.item(j);

								/**
								 *  "#text" is a node that is automatically generated in .xml file and it is hidden, 
								 *  but still it needs to be skipped so document is parsed properly. MakeBlock method is called
								 *  and 
								 */
								if (!child.getNodeName().contains("#text")){
									MakeBlock(i, num*30, elem, child.getNodeName());
									numberOfBlocks++;
									num++;
								}
							}

							/**
							 * if checks is group on position 0 (first group) because first group has
							 * different (and static) settings for its size and position values
							 */
							if(i==0)
							{
								groups[i].setBounds(10, 10, 230, num*30 + 60);
								shell.setSize(270, num*30 + 60);
							}

							/**
							 *  For every other group that is not on position 0, size, location on form and distance between
							 *  other groups needs to be set. Also scrollbar is set to be always shown
							 */
							else
							{
								groups[i].setBounds(10, groups[i-1].getLocation().y + groups[i-1].getBounds().height +20 , 230, num*30 + 60);
								if(shell.getSize().y<700)
								{
									shell.setSize(270, shell.getSize().y + num*30 + 120);
									scrolledComposite.setAlwaysShowScrollBars(true);
								}
							}

							/**
							 * Dynamic allocation of button. The number of buttons is not known in the start of program
							 * so dynamic allocation is used.
							 */

							Button[] tempButtons = new Button[buttons.length + 1];
							System.arraycopy(buttons, 0, tempButtons, 0, buttons.length);
							buttons = tempButtons;

							/**
							 * Definition of various properties set for every button in each group 
							 */
							buttons[i] = new Button(groups[i], SWT.PUSH);
							buttons[i].setBounds(10, ++num*30, 200, 25);
							buttons[i].setText("Send");
							buttons[i].addMouseListener(new MouseAdapter() {
								@Override
								public void mouseDown(MouseEvent e) {



									/**
									 * When button is clicked, for loop goes through whole form and checks which button is
									 * clicked. When button is found, all text written from text fields is selected and written
									 * on appropriate fields in .xml file.
									 */
									int currentGroup = 0;
									Button b = (Button)e.getSource();
									for(int i=0; i < XMLData.getInstance().getLength(); i++)
										if(buttons[i] == b)
											currentGroup = i;

									Node node = XMLData.getInstance().getItem(currentGroup);

									
									/**
									 * Gets all available text fields in selected group and save all text in .xml file written 
									 * by user.
									 */
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

									for (int j=0; j<children.getLength(); j++)
									{
										Node child = children.item(j);
										if (!child.getNodeName().contains("#text"))
										{
											child.setTextContent(textFieldsOfTheGroup[num].getText());
											num++;
										}
									}



									/**
									 * A TransformerFactory instance can be used to create Transformer and Templates objects.
									 */
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
					System.out.println("Error");
					exception.printStackTrace();
				}
			}

		}); 


		/**
		 * A ScrolledComposite provides scrollbars and will scroll its content when the user uses the scrollbars
		 */
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

	/**
	 * @param i counter
	 * @param move integer that sets value of distance between every tag
	 * @param elem variable type of Element 
	 * @param labelText String that is text of label
	 */
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
