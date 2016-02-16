package editor.view;

import javax.swing.JFrame;
import javax.swing.JPanel;


import net.miginfocom.swing.MigLayout;


import javax.swing.JButton;



import javax.swing.JTextPane;
import javax.swing.JScrollPane;

public class EditorFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	private JTextPane txtpnEditor;
	private static EditorFrame instance = null;
	public static int init = 0;
	
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
		
	public void initGUI(){
		this.setTitle("Editor");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		getContentPane().setLayout(new MigLayout("", "[grow]", "[grow][]"));
		JPanel panel = new JPanel();
		
		getContentPane().add(panel, "cell 0 0,grow");
		panel.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		txtpnEditor = new JTextPane();
		
		JScrollPane scrollPane = new JScrollPane(txtpnEditor);
		TextLineNumber tln = new TextLineNumber(txtpnEditor); 
		
		scrollPane.setRowHeaderView( tln );
		panel.add(scrollPane, "flowx,cell 0 0,grow");
		
		JButton btnSave = new JButton("Save");
		getContentPane().add(btnSave, "cell 0 1,alignx right");
		pack();
		setSize(900,600);
		this.setLocationRelativeTo(null);
	}	
}
