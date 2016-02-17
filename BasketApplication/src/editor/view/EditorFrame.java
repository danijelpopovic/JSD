package editor.view;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;
import editor.utils.Autocomplete;
import editor.utils.MyKeyListener;
import editor.utils.UppercaseDocumentFilter;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;


public class EditorFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	private JTextPane txtpnEditor;
	private static EditorFrame instance = null;
	public static int init = 0;
	public List<String> pressedCharacter = new ArrayList<String>();
	
	public static EditorFrame getInstance() {
		if (init == 0) {
			instance = new EditorFrame();
			init = 1;			
			return instance;
		}
		return instance;
	}
	
	public EditorFrame() {
		initGUI();
	}
	private static final String COMMIT_ACTION = "commit";
		
		
	
	public void initGUI(){
		this.setTitle("Editor");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][][][][grow][]"));
		JPanel panel = new JPanel();
		
		getContentPane().add(panel, "cell 1 4,grow");
		panel.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		txtpnEditor = new JTextPane();
		//txtpnEditor.addKeyListener(new MyKeyListener(txtpnEditor));
		// Without this, cursor always leaves text field
		//txtpnEditor.setFocusTraversalKeysEnabled(false);
		
		// Our words to complete
		List<String> keywords  = new ArrayList<String>(10);
		
		
		try {
			FileInputStream fstream = new FileInputStream("resources/reserved_words.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String strLine;

			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
			  // Print the content on the console
			  //System.out.println (strLine);
			  if(!strLine.isEmpty() && strLine!="")
				  keywords.add(strLine);
			}

			//Close the input stream
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    
		//DocumentFilter filter = new UppercaseDocumentFilter();
		 //((AbstractDocument) txtpnEditor.getDocument()).setDocumentFilter(filter);
		Autocomplete autoComplete = new Autocomplete(txtpnEditor, keywords);
		txtpnEditor.getDocument().addDocumentListener(autoComplete);

		// Maps the tab key to the commit action, which finishes the autocomplete
		// when given a suggestion
		txtpnEditor.getInputMap().put(KeyStroke.getKeyStroke("TAB"), COMMIT_ACTION);
		txtpnEditor.getActionMap().put(COMMIT_ACTION, autoComplete.new CommitAction());
		txtpnEditor.addKeyListener(new MyKeyListener(txtpnEditor));
		
		JScrollPane scrollPane = new JScrollPane(txtpnEditor);
		TextLineNumber tln = new TextLineNumber(txtpnEditor); 
		
		scrollPane.setRowHeaderView( tln );
		panel.add(scrollPane, "flowx,cell 0 0,grow");
		//panel.add(txtpnEditor, "flowx,cell 0 0,grow");
		JButton btnSave = new JButton("Save");
		getContentPane().add(btnSave, "cell 1 5,alignx right");
		pack();
		setSize(900,600);
		this.setLocationRelativeTo(null);
	}
	
	
	
	
	
	/*        ------------------------      */
	
	 

   
	
	
}
