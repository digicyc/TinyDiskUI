
/**
 * TinyDiskIO - This file handles creating and retrieving file clusters from TinyURL
 *
 *@author Acidus
 */


package org.msblabs.tinydisk;

import org.antitech.gui.Transaction;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.zip.CRC32;
import org.msblabs.http.HTTPMessage;
import org.msblabs.http.HTTPPostMessage;
import org.msblabs.http.MyByteArray;


public class TinyDiskIO {
    
    /** Creates a new instance of TinyDiskIO */

    private static TinyDiskConfig config;
    
    
    //cannot init this class, all stack methods
    private TinyDiskIO() {
    }
    
    public static boolean loadConfig(String c) {
        config = TinyDiskConfig.fromFile(c);
        return config.isValid();
    }
    
    
    //==================================================== User funcs
    //2 functions that the public will use
    
    //Saves fileToSave into TinyURL and create the meta file needed 
    //to recreate the file in tdFile
    public static void saveFileInTU(String fileToSave, String tdFile) {
        Transaction.updateConsole("Reading " + fileToSave + "...");
        byte [] theFile = TinyDiskIO.loadFile(fileToSave);
        
        Transaction.clearConsole();
        
        if(theFile.length > config.maxSize) {
            fatalError("Trying to upload a file larger than the max size (" + config.maxSize + ")\nChange your TinyDisk.config to allow larger files");
        }
        
        CRC32 crc = new CRC32();
        crc.update(theFile);
    
        long origSize = theFile.length;
        Transaction.updateConsole("Generating encryption key...");
        byte [] key = AES.generateKey();
        Transaction.updateConsole("Compressing using Deflate...");
        theFile = Compression.deflateBytes(theFile);
        Transaction.updateConsole("Encrypting using AES, 128 bit");
        theFile = AES.encrypt(theFile, key);
        Transaction.updateConsole("Base64 encoding...");
        theFile = Radix64.encode(theFile);
     
        TinyDiskFile tdf = new TinyDiskFile();
        tdf.version = "1.0";
        tdf.fileSize = origSize;
        tdf.filename = extractFilename(fileToSave);
        tdf.checksumAlgo = "CRC32";
        tdf.checksum = String.valueOf(crc.getValue());
        tdf.compressionAlgo = "Deflate";
        tdf.encryptionAlgo = "AES, 128bit";
        tdf.encryptionKey = key;

        Transaction.updateConsole("Uploading to " + config.writeServer);
        tdf.clusters = writeIntoClusters(theFile, 4096);
        Transaction.updateConsole("");
        tdf.toFile(tdFile);
	Transaction.updateConsole("Upload Complete!");
  }

    //using the meta file, extract the file from TinyURL
    public static void loadFileFromTU(String filename) {

        TinyDiskFile tdf = TinyDiskFile.fromFile(filename);
        
        ByteArrayOutputStream bos =  new ByteArrayOutputStream(65536);
        
        Transaction.clearConsole();
        Transaction.updateConsole("Retrieving clusters from " + config.readServer);
        try {
            for(String hash : tdf.clusters) {
                //Transaction.updateConsole(".");
                bos.write(readEntry(hash).getBytes());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        Transaction.updateConsole("");
        byte [] theFile = bos.toByteArray();
        byte [] bkey = tdf.encryptionKey;
        Transaction.updateConsole("Base64 decoding...");
        theFile = Radix64.decode(theFile);
        Transaction.updateConsole("Decyrpting with " + tdf.encryptionAlgo + "...");
        if(!tdf.encryptionAlgo.equals("AES, 128bit")) {
            fatalError("Unknown encryption algorithm \"" + tdf.encryptionAlgo +"\"");
        }
        theFile = AES.decrypt(theFile, bkey);
        Transaction.updateConsole("Decompressing with " + tdf.compressionAlgo + "...");        
        theFile = Compression.inflateBytes(theFile);
        
        if(theFile.length != tdf.fileSize) {
            fatalError("Filesize is different!");
        }
        
        CRC32 crc = new CRC32();
        crc.update(theFile);
        if(tdf.checksumAlgo.equals("CRC32")) {
            if(crc.getValue() != Long.valueOf(tdf.checksum)) {
                fatalError("Checksum is wrong!");
            }
        } else {
            Transaction.updateConsole("TinyDisk doesn\'t recognize Checksum Algorithm \"" + tdf.checksumAlgo + "\"");
            Transaction.updateConsole("Cannot confirm validity of orignal file");
        }
        Transaction.updateConsole(tdf.filename + ": \n\tSUCCESSFULLY RETRIEVED!");
        saveFile(tdf.filename, theFile);
    }
    
    
    
    
    

    //extract the filename from a path+filename
    //handles both Unix and Windows style paths
    private static String extractFilename(String path) {
        int i=0;
        if(path.lastIndexOf('\\') > 0) {
            //Windows style path
            return path.substring(path.lastIndexOf('\\')+1);
        }
        if(path.lastIndexOf('/') > 0) {
            //Unix style path
            return path.substring(path.lastIndexOf('/')+1);
        }
        //no path that I recognize, return the whole thing
        return path;
    }
    
    
    /**
     * Given an array of binary data, divide it into clusters of
     * clusterSize, and upload each cluster into TinyURL.
     * @param data - byte array of data representing a file
     * @param clusterSize - size of each cluster to write
     * @return array of hashes of the clusters for the file, in order.
     */
    
    private static String [] writeIntoClusters(byte [] data, int clusterSize) {
        ArrayList<String> clusters = new ArrayList<String>();

      boolean flag = true;
      int curr =0;
      int len = clusterSize;

      while(flag) {
          if(curr + len >= data.length) {
              len = data.length - curr;
              flag = false;
          }
          //create an entry for this sub set of data
          System.out.print('.');
          String hash =createEntry(new String(data, curr, len) );
          clusters.add(hash);
          curr +=len;
      }
      return (String []) clusters.toArray(new String[0]);
      
  }
  
  
  
    
  //==================================================== File level funcs
   
    private static byte [] loadFile(String fn) {
        ByteArrayOutputStream bos =  new ByteArrayOutputStream(65536);
        try {
            InputStream is = new FileInputStream(fn);

            // Compress the data
            byte[] buf = new byte[1024];
            int count =0;
            while ( (count = is.read(buf)) != -1) {
                bos.write(buf, 0, count);
            }
        
            bos.close();
        } catch (IOException e) {
            fatalError("File error with file \"" + fn +"\"");
        }

        // Get the compressed data
        return bos.toByteArray();
    }

    
    private static void saveFile(String fn, byte [] data) {
        try {
            OutputStream os = new FileOutputStream(fn);
            os.write(data);
            os.flush();
            os.close();
        } catch (IOException e) {
        }

    }
    
//======================================================= Entry Level Funcs    

    
    //checks if creating a TinyURL entry worked
    private static boolean createSucceed(String fullreply) {
        return (fullreply.indexOf(config.createSig) > 0);
    }
    
    //extracts the hash of the created entry
    private static String extractEntryHash(String fullreply) {
        int i=fullreply.indexOf(config.hashSig);
        if(i < 0) {
            fatalError("Found the creation signature in the response, but couldn't extract the hash?\nYour HashSig in the config file is wrong");
        }
        i += config.hashSig.length();
        int j = fullreply.indexOf(34, i); //search for "
        if(j < 0)
            fatalError("Found the creation signature in the response, but couldn't extract the hash?\nYour HashSig in the config file is wrong");
        return fullreply.substring(i,j);
    }


    private static String createEntry(String data) {
        try {
            StringBuilder sb = new StringBuilder(); //used for contructing stuff
            sb.append("http://");
            sb.append(config.writeServer);
            sb.append(':');
            sb.append(config.writePort);
            sb.append(config.writeURL);
            
            HTTPPostMessage msg = new HTTPPostMessage(sb.toString());
           msg.addHeader("Connection: close");
           msg.addVariable("url", data);
           msg.addVariable("submit", "Make TinyURL!");
           msg.finalize();
            
           Socket sock = new Socket(config.writeServer, Integer.parseInt(config.writePort));           
           OutputStream os = sock.getOutputStream();
           InputStream is = sock.getInputStream();
            
            os.write(msg.getMessage());
            os.flush();
            //================================================== HANDLE RESPONSE            
            //Read in the entire response from the server
            int count =0;
            byte [] buffer = new byte[4096];
            MyByteArray mba = new MyByteArray();
            while ((count = is.read(buffer)) != -1) {
                mba.add(buffer, count);
            }
            sock.close();
            String fullreply = new String(mba.getBytes());
            if(createSucceed(fullreply)) {
                
                return extractEntryHash(fullreply);
            } else {
                fatalError("Couldn't create a hash for a cluster? Inconceivable!");
            }
        } catch (NumberFormatException ne) {
            fatalError("Cannot parse the port for the Write Server. Port was \""+ config.writePort + "\"");
        } catch (Exception e) {
            e.printStackTrace();
            fatalError("Error of badness +1/+3, AC 10, THACO 5!");            
        }
        return null;
    }

    //read the tinyurl entry with this hash
    private static String readEntry(String hash) {
        try {
            //make a request
            MyByteArray mba = new MyByteArray();
            mba.add("GET ");
            mba.add(config.readURL);
            mba.add(hash);
            mba.add(" HTTP/1.1");
            mba.add("\r\n");
            mba.add("Host: ");
            mba.add(config.readServer);
            mba.add("\r\n");
            mba.add("Connection: close");
            mba.add("\r\n");
            mba.add("\r\n");            
            
           
            Socket sock = new Socket(config.readServer, Integer.parseInt(config.readPort));           
            OutputStream os = sock.getOutputStream();
            InputStream is = sock.getInputStream();
            
            //send it
            os.write(mba.getBytes());
            os.flush();
            
            //================================================== HANDLE RESPONSE            

            //Read in the entire response from the server
            int count =0;
            byte [] buffer = new byte[4096];
            mba = new MyByteArray();
            while ((count = is.read(buffer)) != -1) {
                mba.add(buffer, count);
            }
            sock.close();
            
            byte[] reply = mba.getBytes();
            
            HTTPMessage response = new HTTPMessage(reply);
            //do we have a Location header?
            String loc = response.getHeader("Location");
            if(loc.equals("")) {
                //doesn't have a Location header?
               fatalError("Reading cluster from hash " + hash + " didn't return a 302 redirect! Inconceivable!");
            }
            if(loc.startsWith("http://"))
                loc = loc.substring(7);
            
            return loc;
        } catch (NumberFormatException ne) {
            fatalError("Cannot parse the port for the Read Server. Port was \""+ config.readPort + "\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }    
    
    private static void fatalError(String e) {
        Transaction.updateConsole(e);
        Transaction.updateConsole("Aborting...");
    }
    
    public static void printUsage() {
        System.out.println("TinyDisk (c) 2005 Acidus (acidus@msblabs.org)\nMost Significant Bit Labs. http://www.msblabs.org\n");
        System.out.println("To write a file:");
        System.out.println("\tjava -jar TinyDisk.jar write [writeFile] [metaFile]");
        System.out.println("\tThis creates a meta file to later retrieve the file");
        System.out.println();
        System.out.println("To read a file:");
        System.out.println("\tjava -jar TinyDisk.jar read [metaFile]");
    }
    
}
