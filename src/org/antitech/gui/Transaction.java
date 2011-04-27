package org.antitech.gui;

import java.awt.*;
import javax.swing.*;

public class Transaction extends JPanel {
    public static JTextField textField;
    public static JTextArea textArea;
    
    public Transaction() {
        super(new BorderLayout());
        
        // Output area. Everything will output to this.
        textField = new JTextField();
        textArea = new JTextArea();
        
        textArea.setBorder(BorderFactory.createTitledBorder("Transaction Console."));
        textArea.setBackground(Color.white);
        textArea.setEditable(false);
        textArea.setForeground(Color.blue);
        
        JScrollPane viewText = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        add(viewText, BorderLayout.CENTER);
    }
    
    /** Update the console with any/all information. */
    public static void updateConsole(String addText) {
        final char newline = '\n';
        
        textArea.append(addText + newline);
        
        textArea.setCaretPosition(textArea.getDocument().getLength());
        
    }
    
    public static void clearConsole() {
        textArea.setText("");
    }
}

