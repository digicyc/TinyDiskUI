/**
 * TinyDiskConfig - holds the config info for TinyDisk
 *
 *
 */
package org.msblabs.tinydisk;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author acidus
 */
public class TinyDiskConfig {

    //used to cehck for valid files
    private int validCount;
  
    public int maxSize;
    
    //What hostname is used to connect to the writing server?
    public String writeServer;
    
    //what is the port for the write server?
    public String writePort;
    
    //url to write to
    public String writeURL;
    
    //sig of a successful creation
    public String createSig;
    
    //string that prefixes the hash returned when a hash is created
    public String hashSig;

    //what hostname is used to connect to the reading server?
    public String readServer;
    
    //What port on the reading server
    public String readPort;

    //path and the filename to read a hash from
    //this should return an HTTP 302 redirect with the contents of the hash
    public String readURL;
        
    /** Creates a new instance of TinyDiskConfig */
    public TinyDiskConfig() {
        validCount = 0;
    }
    
    
    //extract the sig which is between sets of quotes
    private static String extractSig(String s) {
        int i = s.indexOf('\"');
        return s.substring(i+1, s.length() -1);
    }
    
    
    private static void parseLine(String line, TinyDiskConfig tdc) {
        int index;
        if((index = line.indexOf("WriteServer:")) >= 0) {
            tdc.addValid();
            tdc.writeServer = line.substring(12).trim();
        } else if((index = line.indexOf("WritePort:")) >= 0) {
            tdc.addValid();
            tdc.writePort = line.substring(10).trim();
        } else if((index = line.indexOf("WriteURL:")) >= 0) {
            tdc.addValid();          
            tdc.writeURL = line.substring(9).trim();
        } else if((index = line.indexOf("CreateSig:")) >= 0) {
            tdc.addValid();
            tdc.createSig = extractSig(line);
        } else if((index = line.indexOf("HashSig:")) >= 0) {
            tdc.addValid();
            tdc.hashSig = extractSig(line);
        } else if((index = line.indexOf("ReadServer:")) >= 0) {
            tdc.addValid();
            tdc.readServer = line.substring(11).trim();
        } else if((index = line.indexOf("ReadPort:")) >= 0) {
            tdc.addValid();
            tdc.readPort = line.substring(9).trim();
        } else if((index = line.indexOf("MaxSize:")) >= 0) {
            tdc.addValid();
            tdc.maxSize = Integer.parseInt(line.substring(9).trim());
        
        } else if((index = line.indexOf("ReadURL:")) >= 0) {
            tdc.addValid();
            tdc.readURL = line.substring(8).trim();
        }
    }
    
    public static TinyDiskConfig fromFile(String filename) {
        BufferedReader fin; 
        try {
            fin = new BufferedReader(new FileReader(filename));
            String inputLine ="";
            
        
            TinyDiskConfig tdc = new TinyDiskConfig();
            
            while( (inputLine = fin.readLine()) != null ) {
                if(inputLine.length() > 0 && !inputLine.startsWith("#")) {
                    parseLine(inputLine, tdc);
                }
            }
            return tdc;
        } catch(Exception e) {
            System.out.println("Error with config file \'filename\'");
        }
        return null;
    }
    
    public void addValid() {
        validCount++;
    }
    
    public boolean isValid() {
        if(validCount == 9)
            return true;
        return false;
    }
    
}
