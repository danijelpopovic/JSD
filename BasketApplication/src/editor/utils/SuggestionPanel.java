package editor.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Highlighter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;



public class SuggestionPanel {
    private JList list;
    private JPopupMenu popupMenu;
    private String subWord;
    private int insertionPosition;
    public SuggestionPanel suggestion;
	public JTextPane txtpnEditor;
	public int newLines;

    public SuggestionPanel(JTextPane txtpnEditor, int position, String subWord, Point location, int newLines) {
        this.insertionPosition = position;
        this.subWord = subWord;
        popupMenu = new JPopupMenu();
        popupMenu.removeAll();
        popupMenu.setOpaque(false);
        popupMenu.setBorder(null);
        popupMenu.add(list = createSuggestionList(position, subWord), BorderLayout.CENTER);
        popupMenu.show(txtpnEditor, location.x, txtpnEditor.getBaseline(0, 0) + location.y+20);
        this.txtpnEditor=txtpnEditor;
        this.newLines=newLines;
    }

    public void hide() {
        popupMenu.setVisible(false);
        if (suggestion == this) {
            suggestion = null;
        }
    }
    
    
    

    private JList createSuggestionList(final int position, final String subWord) {
    	
    	ArrayList<String> reservedWords = new ArrayList<String>();
    	try {
			FileInputStream fstream = new FileInputStream("resources/reserved_words.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String strLine;

			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
			  // Print the content on the console
			  //System.out.println (strLine);
			  if(!strLine.isEmpty() && strLine!="")
				  reservedWords.add(strLine);
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
    	
    	//reservedWords.get(0).contains(s)
    	
        Object[] data = new Object[10];
        int counter=0;
        for(String s: reservedWords){
        	String reserved = s;
        	if(reserved.toLowerCase().contains(subWord.toLowerCase())){
        		data[counter]=s;
        		System.out.println(s);
        		counter++;
        	}
        }
        
        
        /*for (int i = 0; i < data.length; i++) {
            data[i] = subWord + i+"test";
        }*/
        JList list = new JList(data);
        list.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	//autocomplete after step
            	System.out.println(e);
                if (e.getClickCount() == 2) {
                    insertSelection();
                }
            }
        });
        return list;
    }
    private Highlighter.HighlightPainter cyanPainter;
    private Highlighter.HighlightPainter redPainter;
    public boolean insertSelection() {
        if (list.getSelectedValue() != null) {
            try {
                final String selectedSuggestion = ((String) list.getSelectedValue());//.substring(subWord.length());
               /* cyanPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.cyan);
                redPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.red);*/
                System.out.println((String) list.getSelectedValue());
                /*txtpnEditor.getHighlighter().addHighlight(0, 3, DefaultHighlighter.DefaultPainter);
                txtpnEditor.getHighlighter().addHighlight(8, 14, cyanPainter);*/
                //txtpnEditor.getText().substring(insertionPosition-subWord.length(), insertionPosition);
                insertionPosition = insertionPosition-newLines;
                System.out.println(insertionPosition);
                int offset = insertionPosition-subWord.length();
                int pos = subWord.length();
                
                txtpnEditor.getDocument().remove(offset, pos);
                txtpnEditor.getDocument().insertString(insertionPosition-subWord.length(), selectedSuggestion, null);
                
                return true;
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
            hideSuggestion();
        }
        return false;
    }

    private void hideSuggestion() {
        if (suggestion != null) {
            suggestion.hide();
        }
    }
    public void moveUp() {
        int index = Math.min(list.getSelectedIndex() - 1, 0);
        selectIndex(index);
    }

    public void moveDown() {
        int index = Math.min(list.getSelectedIndex() + 1, list.getModel().getSize() - 1);
        selectIndex(index);
    }

    private void selectIndex(int index) {
        final int position = txtpnEditor.getCaretPosition();
        list.setSelectedIndex(index);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	txtpnEditor.setCaretPosition(position);
            };
        });
    }
}