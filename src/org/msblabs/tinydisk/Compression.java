/**
 * Easy interface to Java's implementation of zlib.
 */

package org.msblabs.tinydisk;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 *
 * @author acidus
 */
public class Compression {
    
    
    public static byte [] inflateBytes(byte [] input) {

        // Create the compressor with highest level of compression
        Inflater decompressor = new Inflater();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
        try {
            decompressor.setInput(input);
            // Compress the data
            byte[] buf = new byte[1024];
            while (!decompressor.finished()) {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            }

            bos.close();
        } catch (Exception e) {
        }

        // Get the compressed data
        return bos.toByteArray();
    }
    
    public static byte [] deflateBytes(byte [] input) {

        // Create the compressor with highest level of compression
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.DEFLATED);

        // Give the compressor the data to compress
        compressor.setInput(input);
        compressor.finish();

        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);

        // Compress the data
        byte[] buf = new byte[1024];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);

            bos.write(buf, 0, count);
        }
        try {
            bos.close();
        } catch (IOException e) {
        }

        // Get the compressed data
        return bos.toByteArray();
    }
    
}
