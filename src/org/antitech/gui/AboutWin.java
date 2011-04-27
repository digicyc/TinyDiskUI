package org.antitech.gui;
/**
 * This just shows an about screen
 * with information about the application.
 *
 * @author cyclone
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AboutWin extends JFrame implements ActionListener {
    private JPanel paneOne;
    private JPanel paneTwo;
    private JButton okButton;
    protected static boolean winflag = false;
    private String imgFile = "images/about.jpg";
    private Image frameImage = Standard.getImage("saveImg.gif");
    
    
    public AboutWin() {
        super("TinyDisk Interface");
        
        addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                AboutWin.winflag = false;
                dispose();
            }
        });
        
        JLabel aboutWin = new JLabel(Standard.getIcon(imgFile));
        
        paneOne = new JPanel(new BorderLayout());
        paneTwo = new JPanel();
        
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        paneTwo.setLayout(gridbag);
        
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        
        c.weighty = 1.5;
        c.gridwidth = 4;
        c.anchor = GridBagConstraints.CENTER;
        gridbag.setConstraints(okButton, c);
        paneTwo.setBackground(Color.WHITE);
        paneOne.setBackground(Color.WHITE);
        paneTwo.add(okButton);
        
        paneOne.add(aboutWin, BorderLayout.CENTER);
        paneOne.add(paneTwo, BorderLayout.SOUTH);
        add(paneOne);
        setIconImage(frameImage);
        pack();
        setSize(350, 300);
        setLocation(200, 200);
        setVisible(true);
        
        AboutWin.winflag = true;
    }
    
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == okButton) {
            AboutWin.winflag = false;
            dispose();
        }
    }
    
}
