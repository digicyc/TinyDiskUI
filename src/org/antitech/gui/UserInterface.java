package org.antitech.gui;
/**
 * User Interface for tinydisk.
 *
 * @author cyclone
 */
import org.msblabs.tinydisk.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import java.io.*;
import java.util.*;
import java.net.URL;

public class UserInterface extends JFrame implements ActionListener {
    public static final boolean DEBUG = false;
    private JPanel paneOne;
    private JPanel paneTwo;
    private JButton sendButton;
    private JButton getButton;
    private JButton exitButton;
    private JProgressBar progressBar;
    private String dirName = "";
    private Image frameImage = Standard.getImage("saveImg.gif");
    private String configFile = "TinyDisk.config";
    public String path;
    public boolean panelFlag = false;
    private final static String separator = System.getProperty("file.separator");
    
    public UserInterface() {
        super("TinyDisk UI");
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        paneOne = new JPanel(new BorderLayout());
        
        TinyDiskIO.loadConfig(configFile);
        
        // ProgressBAR
        progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
        
        buildMenu();
        
        sendButton = new JButton("Send");
        getButton = new JButton("Get");
        sendButton.addActionListener(this);
        getButton.addActionListener(this);
        
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(sendButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(getButton);
        
        path = System.getProperty("user.dir");
        
        if(DEBUG) {
            System.out.println("CONFIG: " + configFile.toString());
        }
        
        JPanel conPane = new Transaction();
        
        paneOne.add(progressBar, BorderLayout.NORTH);
        paneOne.add(conPane, BorderLayout.CENTER);
        paneOne.add(buttonPane, BorderLayout.SOUTH);
        
        getContentPane().add(paneOne);
        
        setIconImage(frameImage);
        setDefaultLookAndFeelDecorated(true);
        pack();
        setSize(360, 260);
        setForeground(Color.white);
        setLocationRelativeTo(null);
        setVisible(true);
        
    }
    
    
    private void buildMenu() {
        JMenuBar mb = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        
        exitItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.exit(0);
            }
        });
        
        
        aboutItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( AboutWin.winflag == false ) {
                    AboutWin aw = new AboutWin();
                }
            }
        });
        
        fileMenu.add(exitItem);
        helpMenu.add(aboutItem);
        mb.add(fileMenu);
        mb.add(helpMenu);
        setJMenuBar(mb);
        
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == sendButton) {
            
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(UserInterface.this);
            
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                final String fileToUpload = file.toString();
                int marker = fileToUpload.lastIndexOf(".");
                String tdfFile = fileToUpload.substring(0, marker);
                
                marker = tdfFile.lastIndexOf(separator);
                
                tdfFile = tdfFile.substring(marker, tdfFile.length());
                final String tdfDir = System.getProperty("user.dir");
                
                path = tdfDir+tdfFile+".tdf";
                
                sendButton.setEnabled(false);
                getButton.setEnabled(false);
                
                Thread sendThread = new Thread(new Runnable() {
                    public void run() {
                        progressBar.setIndeterminate(true);
                        TinyDiskIO.saveFileInTU(fileToUpload, path);
                        progressBar.setIndeterminate(false);
                        sendButton.setEnabled(true);
                        getButton.setEnabled(true);
                        Transaction.updateConsole("File: "+ path);
                    }
                });
                sendThread.start();
                
                
            }
        } else if(e.getSource() == getButton) {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(UserInterface.this);
            
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                final String filePath = file.getAbsolutePath();
                
                getButton.setEnabled(false);
                sendButton.setEnabled(false);
                
                Thread getThread = new Thread(new Runnable() {
                    public void run() {
                        progressBar.setIndeterminate(true);
                        TinyDiskIO.loadFileFromTU(filePath);
                        progressBar.setIndeterminate(false);
                        getButton.setEnabled(true);
                        sendButton.setEnabled(true);
                    }
                });
                getThread.start();
                
            }
        }
    }
    
    public static void main(String[] args) {
        
        UserInterface ui = new UserInterface();
    }
}
