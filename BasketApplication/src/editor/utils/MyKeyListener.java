package editor.utils;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;



public class MyKeyListener implements KeyListener{

	public SuggestionPanel suggestion;
	public JTextPane txtpnEditor;
	
	public MyKeyListener(JTextPane txtpnEditor){
		this.txtpnEditor = txtpnEditor;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		 if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                if (suggestion != null) {
                    if (suggestion.insertSelection()) {
                        e.consume();
                        final int position = txtpnEditor.getCaretPosition();
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                	txtpnEditor.getDocument().remove(position - 1, 1);
                                } catch (BadLocationException e) {
                                    e.printStackTrace();
                                }
                            }                            
                        });
                        suggestion.hide();
                        suggestion=null;                        
                    }
                }
            }				
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyCode() == KeyEvent.VK_DOWN && suggestion != null) {
            suggestion.moveDown();
        } else if (e.getKeyCode() == KeyEvent.VK_UP && suggestion != null) {
            suggestion.moveUp();
        } else if (Character.isLetterOrDigit(e.getKeyChar())) {
            showSuggestionLater();
        } else if (Character.isWhitespace(e.getKeyChar())) {
            hideSuggestion();
        }
	}	            

	
	private void hideSuggestion() {
        if (suggestion != null) {
            suggestion.hide();
        }
    }
	
	 protected void showSuggestionLater() {
	        SwingUtilities.invokeLater(new Runnable() {
	            @Override
	            public void run() {
	                showSuggestion();
	            }

	        });
	    }

	    protected void showSuggestion() {
	        hideSuggestion();
	        int position = txtpnEditor.getCaretPosition();
	        Point location;
	        try {
	            location = txtpnEditor.modelToView(position).getLocation();
	        } catch (BadLocationException e2) {
	            e2.printStackTrace();
	            return;
	        }
	        String text = txtpnEditor.getText();
	       
	        int start = Math.max(0, position - 1);
	        int counter=0;
	        
	        if(text.length()>2)
	        	for(int i=0; i<text.length(); i++){	
	        		if(i>0){
	        		String a="" +text.charAt(i-1)+""+text.charAt(i);
		        		if((a.equals(System.getProperty("line.separator")))){
		        			counter=counter+1;
		        			
		        		}
	        		}
	        	}
	        while (start > 0) {
	            if (!Character.isWhitespace(text.charAt(start)))      	        	
	            {
	                start--;
	            }         
	            else {
	                start++;
	                //System.out.println("whtespace - " + text.charAt(start));
	                break;
	            }
	            
	        }
	        
	        
	       position = position + counter;
	               
	        if (start > position) {
	            return;
	        }
	        String subWord = text.substring(start, position);
	        subWord = subWord.replace('\n', ' ');
	        subWord = subWord.replace('\r', ' ');
	        subWord = subWord.replaceAll("\\s","");
	        subWord = subWord.replaceAll("\r\n","");
	        if (subWord.length() < 2 && (subWord.contains("\n") || subWord.contains("\r") || subWord.contains("\r\n"))) {
	            return;
	        }
	        suggestion = new SuggestionPanel(txtpnEditor, position, subWord, location, counter);
	        SwingUtilities.invokeLater(new Runnable() {
	            @Override
	            public void run() {
	            	txtpnEditor.requestFocusInWindow();
	            }
	        });
	    }
	    
	    
}
