/**
 * HTTPHeader.java
 *
 * Base file to handle and parse HTTP Headers
 *
 */

package org.msblabs.http;


public class HTTPHeader {
    
    private String name;
    private String value;
            
   
    //parse these 2
    public HTTPHeader(String pair) {
        name = null;
        value = null;
        
        //we cann't split on an :, because header could have : in it
        int i = pair.indexOf(':');
        //first half of string
        if(i > 1) {
            name = pair.substring(0, i).trim();
            value = pair.substring(i+1).trim();
        } else {
            name = pair.trim();
        }
        
    }

    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(": ");
        sb.append(value);
        return sb.toString();
    }
    
}
