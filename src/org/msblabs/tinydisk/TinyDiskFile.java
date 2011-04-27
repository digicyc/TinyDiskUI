/** TinyDiskFile - Represents the meta file used to get info back
 * Will also have secure version of a meta file that looks just like a
 * PGP/GPG public key so TinyURL cannot learn what clusters are disk clusters
 * and delete them
 */

package org.msblabs.tinydisk;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author acidus
 */
public class TinyDiskFile {
    
    public String version;
    
    public String filename;

    public long fileSize;
    
    public String checksumAlgo;

    public String checksum;
    
    public String compressionAlgo;

    public String encryptionAlgo;

    public byte [] encryptionKey;
    
    public String [] clusters;
    
    /** Creates a new instance of TinyDiskFile */
    public TinyDiskFile() {
        
    }
    
    public void toFile(String filename) {
        try {
            PrintWriter pw = new PrintWriter(filename);
            pw.println(this.toString());
            pw.flush();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //haven't finishedthis funcitonallity yet.
    public void toProtectedFile(String filename) {
        try {
            PrintWriter pw = new PrintWriter(filename);
            
            byte [] data = this.toString().getBytes();
            //compress it
            data = Compression.deflateBytes(data);
            //encrypt it
            data = AES.encrypt(data, this.encryptionKey);
            //base64 it
            data = Radix64.encode(data);
            pw.println("-----BEGIN PGP PRIVATE KEY BLOCK-----");
            pw.println("Version: GnuPG v1.2.3 (GNU/Linux)");
            pw.println();
            pw.println(new String(data));
            pw.println();
            
            pw.flush();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
    
    
    public String toString() {
        StringBuilder sb= new StringBuilder();
        sb.append("# TinyDiskFile - used to retrieve a file that has been stored in TinyUrl.com!\n");
        sb.append("#\n");
        sb.append("# By Acidus - Most Significant Bit Labs - Acidus@msblabs.org\n");
        sb.append("Version: ");
        sb.append(version);
        sb.append('\n');
        sb.append("Filename: ");
        sb.append(this.filename);
        sb.append('\n');
        sb.append("Size: ");
        sb.append(fileSize);
        sb.append('\n');
        sb.append("Checksum Algorithm: ");
        sb.append(checksumAlgo);
        sb.append('\n');
        sb.append("Checksum: ");
        sb.append(checksum);
        sb.append('\n');
        sb.append("Compression Algorithm: ");
        sb.append(compressionAlgo);
        sb.append('\n');
        sb.append("Encryption Algorithm: ");
        sb.append(encryptionAlgo);
        sb.append('\n');
        sb.append("Encryption Key: ");
        byte [] t = Radix64.encode(encryptionKey);
        String y = new String(t);
        sb.append(y);
        sb.append('\n');

        sb.append("#\n");
        sb.append("# Cluster Hashes\n");
        sb.append("#\n");
        sb.append("Clusters: ");
        sb.append(clusters.length);
        sb.append('\n');
        for(String s : clusters) {
            sb.append("Cluster: ");
            sb.append(s);
            sb.append('\n');
        }
        return sb.toString();
    }    
    
    
    private static void parseLine(String line, TinyDiskFile tdf, ArrayList<String> clusters) {
        int index;
        if((index = line.indexOf("Version: ")) >= 0) {
            tdf.version = line.substring(9);
        } else if((index = line.indexOf("Filename: ")) >= 0) {
            tdf.filename = line.substring(10);
        } else if((index = line.indexOf("Size: ")) >= 0) {
            tdf.fileSize = Long.parseLong(line.substring(6));
        } else if((index = line.indexOf("Checksum Algorithm: ")) >= 0) {
            tdf.checksumAlgo = line.substring(20);
        } else if((index = line.indexOf("Checksum: ")) >= 0) {
            tdf.checksum = line.substring(10);
        } else if((index = line.indexOf("Compression Algorithm: ")) >= 0) {
            tdf.compressionAlgo = line.substring(23);
        } else if((index = line.indexOf("Encryption Algorithm: ")) >= 0) {
            tdf.encryptionAlgo = line.substring(22);
        } else if((index = line.indexOf("Encryption Key: ")) >= 0) {
            String y = new String(line.substring(16));
            tdf.encryptionKey = Radix64.decode(y.getBytes());
        } else if((index = line.indexOf("Cluster: ")) >= 0) {
            clusters.add(line.substring(9));
        }
    }
    
    
    
    public static TinyDiskFile fromFile(String filename) {
        BufferedReader fin; 
        try {
            fin = new BufferedReader(new FileReader(filename));
            String inputLine ="";
            
        
            TinyDiskFile tdf = new TinyDiskFile();
            ArrayList<String> clusters = new ArrayList<String>();
            
            while( (inputLine = fin.readLine()) != null ) {
                if(inputLine.length() > 0 && !inputLine.startsWith("#")) {
                    parseLine(inputLine, tdf, clusters);
                }
            }
            tdf.clusters = (String []) clusters.toArray(new String[0]);
            return tdf;
        } catch(Exception e) {
            System.out.println("Error with meta file " +filename);            
        }
        return null;
    }
    
    
}
