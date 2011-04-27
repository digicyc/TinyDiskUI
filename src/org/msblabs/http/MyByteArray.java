/** self expanding byte array I wrote a lonnnnnnnnng time ago
 * not really need as ByteArrayOutput/InputStreams can do the same
 * thing. Some of my HTTP stuff uses this while my AES stuff uses byte arrays
 */

package org.msblabs.http;

/**
 *
 * @author acidus
 */
public class MyByteArray {
    
    
    byte [] buffer;
    int curr;
    
    
    /** Creates a new instance of MyByteArray */
    public MyByteArray() {
        buffer = new byte[32768];
        curr = 0;
    }
   
    public void add(String s) {
        add(s.getBytes());
    }
    
    public void add(byte [] bytes) {
        for(byte b: bytes) {
            add(b);
        }
    }

    public void add(byte [] bytes, int count) {
        for(int i=0; i < count; i++) {
            add(bytes[i]);
        }
    }    
    
    public void add(byte b) {
        buffer[curr] = b;
        curr++;
        expandBuffer();
    }
    
    private void expandBuffer() {
        if(curr == buffer.length) {
            byte[] tmpBuffer = new byte[buffer.length + 32768]; //add 32K
            //copy into new buffer (shame we don't have realloc...)
            System.arraycopy (buffer, 0, tmpBuffer, 0, buffer.length);
            //reset reference;
            buffer = tmpBuffer;
        }
    }
    
    private void minimizeBuffer() {
        byte[] tmpBuffer = new byte[curr];
        System.arraycopy (buffer, 0, tmpBuffer, 0, curr);
        buffer = tmpBuffer;
    }           
    
    public byte [] getBytes() {
        minimizeBuffer();
        return buffer;
    }
    
}
