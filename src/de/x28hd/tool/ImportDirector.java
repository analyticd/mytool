package de.x28hd.tool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ImportDirector implements ActionListener {
	
    Runnable fileChooserMac = new Runnable() {

		@Override 
        public void run() {
            fd = new JFileChooser(System.getProperty("user.home") + File.separator + "Desktop");
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
            		extDescription[importType], extension[importType]);
    		if (!System.getProperty("os.name").equals("Mac OS X")) {
    			Action details = fd.getActionMap().get("viewTypeDetails");
    			details.actionPerformed(null);
    		}
            fd.setFileFilter(filter);        
            fd.setApproveButtonText("Select");
    		fd.setDialogType(FileDialog.LOAD);
    		frame.remove(radioPanel);
    		frame.remove(descriptionsPanel);
    		continueButton.setEnabled(false);
            frame.add(fd);
            frame.pack();
            fd.addActionListener(ImportDirector.this);
        }
    };
    
	GraphPanelControler controler;
	JFrame frame = null;
	JFileChooser fd = null;
	int knownFormat = -1;
	JPanel radioPanel = null;
    JPanel descriptionsPanel;
	int importType = 0;
	JButton continueButton = new JButton("Next >");
//	boolean lastStep = false;	// needed if more steps for layout erc
	
	
	String [] importTypes = {
			"Evernote", 
			"iMapping", 
			"DWZ", 
			"Cmap", 
			"TheBrain",
			"Word",
			"(Old Format)"
			};
	String [] knownFormats = {
			"en-export", 
			"(not relevant)", 
			"kgif", 
			"cmap", 
			"BrainData",
			"w:document",
			"topicmap"
			};
	String [] extension = {
			"enex", 
			"iMap", 
			"xml", 
			"cxl", 
			"xml",
			"docx", 
			"zip"
			};
	String [] extDescription = {
			"enex (Evernote Export file)", 
			"iMap (iMapping file)", 
			"xml (DenkWerkZeug KGIF file)", 
			"cxl (Cmap CXL file)", 
			"xml (TheBrain \"Brain XML\" file)",
			"docx (Word Document)",
			"zip (Zipped XML Document)"
			};
	String [] longDescription = {
			"<html>If you have an \"ENEX\" export file exported from the Evernote note taking application</html>)", 
			"<html>A map from the <a href=\"http://imapping.info\">iMapping.info</a> think tool application</html>", 
			"<html>If you have a \"KGIF\" Knowledge Graph Interchange Format file exported from the <br><a href=\"http://denkwerkzeug.org\">DenkWerkZeug.org</a> think tool application</html>", 
			"<html>If you have a \"CXL\" export file exported from the CmapTools concept mapping application</html>", 
			"<html>If you have a \"Brain XML\" file exported from the TheBrain note management application</html>",
			"<html>A Microsoft Word Document (we take the plain text from each paragraph)</html>",
			"Old versions of this tool and its precursor DeepaMehta"
			};
	
	//	Nothing given => Launch wizard
	public ImportDirector(GraphPanelControler controler) {
		this.controler = controler;
			launchWizard();
		}
	
	//	XML given
	public ImportDirector(int knownFormat, Document doc, GraphPanelControler controler) {
		this.controler = controler;
		this.knownFormat = knownFormat;
//		if (this.knownFormat < 0) {
//			launchWizard();
//		} else if (this.knownFormat == 0) {
		if (this.knownFormat == 0) {
			new EnexImport(doc, controler);
		} else if (this.knownFormat == 2) {
			new DwzImport(doc, controler);
		} else if (this.knownFormat == 3) {
			new CmapImport(doc, controler);
		} else if (this.knownFormat == 4) {
			new BrainImport(doc, controler);
		}
	}

	//	File given
	public ImportDirector(int knownFormat, File file, GraphPanelControler controler) {
		this.controler = controler;
		this.knownFormat = knownFormat;
		if (this.knownFormat == 1) {
			new ImappingImport(file, controler);
		}
	}

	//	Zip Input stream given
	public ImportDirector(int knownFormat, InputStream stream, GraphPanelControler controler) {
		this.controler = controler;
		this.knownFormat = knownFormat;
		if (this.knownFormat == 5 || this.knownFormat == 6) {
			step4(stream);
		}
	}

//
//	Select import format
	
	public void launchWizard() {
		frame = new JFrame("Import Wizard");
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2 - 298, 
				dim.height/2-frame.getSize().height/2 - 209);		
		
		ButtonGroup buttonGroup = new ButtonGroup();
        radioPanel = new JPanel(new GridLayout(0, 1));
		radioPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		radioPanel.add(new JLabel("<html>Choose a format:"));

		for (int i = 0; i < importTypes.length - 1; i++) {
			JRadioButton radio = new JRadioButton(importTypes[i]);
			radio.setActionCommand("type-" + i);
			radio.addActionListener(this);
			buttonGroup.add(radio);
	        radioPanel.add(radio);
		}
		radioPanel.add(new JLabel(" "));
		frame.add(radioPanel, BorderLayout.WEST);
		
		descriptionsPanel = new JPanel();
        descriptionsPanel = new JPanel(new GridLayout(0, 1));
		descriptionsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		descriptionsPanel.add(new JLabel(" "));
		for (int i = 0; i < importTypes.length - 1; i++) {
			JLabel descr = new JLabel();
	        descr.setText(longDescription[i]);
	        descriptionsPanel.add(descr);
		}
//		descriptionsPanel.add(new JLabel("<html><em>Note:</em><br>You can also drag or paste files directly into our windows. <br>" +
//		  "Also text snippets from other applications can be pasted here, <br>" +
//		  "and from some applications you can drag and drop snippets directly, <br>" +
//		  "e.g. from editors like Word or Wordpad or from browsers (Internet Explorer <br>" +
//		  "if protected mode is disabled). Even map snippets from this application <br>" +
//		  "(press Alt + drag or middle mouse button + drag). </html>"));
		descriptionsPanel.add(new JLabel("<html><b>Note:</b> You can also drag files directly into our windows and paste text into them. Also <br>" +
		  "try to <em>drag</em> text snippets from other applications, or even <em>map</em> snippets from our windows."));
		frame.add(descriptionsPanel, BorderLayout.EAST);
		
		JToolBar continueBar = new JToolBar();
		continueBar.setLayout(new BorderLayout());
		continueBar.setBorder(new EmptyBorder(10, 10, 10, 10));
		continueBar.setBackground(Color.WHITE);
		continueButton.addActionListener(this);
		continueButton.setEnabled(false);
		continueBar.add(continueButton, BorderLayout.EAST);
		JButton cancelButton = new JButton("Cancel");
		continueBar.add(cancelButton, BorderLayout.WEST);
		cancelButton.addActionListener(this);
		frame.add(continueBar, BorderLayout.SOUTH);
		if (System.getProperty("os.name").equals("Mac OS X")) {
			frame.setMinimumSize(new Dimension(796, 417));
		} else {
			frame.setMinimumSize(new Dimension(596, 417));
		}
        frame.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent action) {
		
		//	Cancel
	    if (action.getActionCommand().equals("Cancel")) {
	        System.out.printf("Wizard canceled\n");
	        frame.setVisible(false);
	        frame.dispose();
	    }
		
		//	Import type choice
	    for (int i = 0; i < importTypes.length; i++) {
	    	if (action.getActionCommand().equals("type-" + i)) {
	    		System.out.println("Type: " + i);
	    		knownFormat = i;
	    		continueButton.setEnabled(true);
//	    		step2(knownFormat);
	    	}
	    }
	    //	File chooser response
	    if (action.getActionCommand().equals("CancelSelection")) {
	        System.out.printf("CancelSelection\n");
	        frame.setVisible(false);
	        frame.dispose();
	    }
	    if (action.getActionCommand().equals("ApproveSelection")) {
    		continueButton.setEnabled(true);
//    		lastStep = true;
	        System.out.printf("ApproveSelection\n");
//	    }
//	    
//	    if (action.getActionCommand().equals("Next >") && lastStep) {
//			String filename = fd.getSelectedFile().getPath() + File.separator + fd.getSelectedFile().getName();
			String filename = fd.getSelectedFile().getName();
			System.out.println(filename);
			if (knownFormat == 1) {
				new ImappingImport(fd.getSelectedFile(), controler);
			} else if (knownFormat == 5) {
				new WordImport(fd.getSelectedFile(), controler);
			} else if (knownFormat == 6) {
				new TopicMapImporter(fd.getSelectedFile(), controler);
			} else {
				step3(fd.getSelectedFile());
			}
	        frame.setVisible(false);
	        frame.dispose();
	    }
	    
	    if (action.getActionCommand().equals("Next >")) {
	    	System.out.println("\"Next >\" button pressed, " + knownFormat);
    		step2(knownFormat);
	    }

	}

//
//	Open file chooser
	
	public void step2(int importType) {
		
//        fd = new JFileChooser(System.getProperty("user.home") + File.separator + "Desktop");
//        FileNameExtensionFilter filter = new FileNameExtensionFilter(
//        		extDescription[importType], extension[importType]);
//        Action details = fd.getActionMap().get("viewTypeDetails");
//        details.actionPerformed(null);
//        fd.setFileFilter(filter);        
//        fd.setApproveButtonText("Next");
//		  fd.setDialogType(FileDialog.LOAD);
//		  frame.remove(radioPanel);
//        frame.add(fd);
//        frame.pack();
//        fd.addActionListener(this);
		
		this.importType = knownFormat;
		SwingUtilities.invokeLater(fileChooserMac);

	}
	
//
//	Open file
	
	public void step3(File file) {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			System.out.println("Error ID101 " + e);
		}
		step4(fileInputStream);
	}
	
//
//	Launch dedicated importer class

	public void step4(InputStream stream) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document inputXml = null;

		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e2) {
			System.out.println("Error ID102 " + e2 );
		}
		
		try {
			inputXml = db.parse(stream);
			
			Element inputRoot = null;
			inputRoot = inputXml.getDocumentElement();
			if (inputRoot.getTagName() != knownFormats[knownFormat]) {
				System.out.println("Error ID105, unexpected: " + inputRoot.getTagName() );
				stream.close();
				return;
			} 
		} catch (IOException e1) {
			System.out.println("Error ID106 " + e1 + "\n" + e1.getClass());
		} catch (SAXException e) {
			System.out.println("Error ID107 " + e );
		}
		if (knownFormat == 0) {
			new EnexImport(inputXml, controler);
		} else if (knownFormat == 2) {
			new DwzImport(inputXml, controler);
		} else if (knownFormat == 3) {
			new CmapImport(inputXml, controler);
		} else if (knownFormat == 4) {
			new BrainImport(inputXml, controler);
		} else if (knownFormat == 5) {
			new WordImport(inputXml, controler);
		} else if (knownFormat == 6) {
			new TopicMapImporter(inputXml, controler);
		}
	}
	
}
