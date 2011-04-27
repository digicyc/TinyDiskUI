/**
 * HTTPMessage.java
 *
 * Part of the stuff from Toocon. Basically handle HTTP messages, parsing 
 * requests/response resources, etc.
 */

package org.msblabs.http;
import java.util.ArrayList;

public class HTTPMessage {

    private static final int START = 0;
    private static final int CR =1;
    private static final int CRLF=2;
    private static final int CRLFCR=3;
    
    protected String action;
    
    protected ArrayList<HTTPHeader> msgHeaders;
            
    protected byte [] body;
    
    public HTTPMessage() {
        msgHeaders = new ArrayList<HTTPHeader>();
        body = null;
    }
   
    public HTTPMessage(byte [] message) throws Exception {

        int curr = 0;
        int state = START;
        boolean stopFlag = false;
        msgHeaders = new ArrayList<HTTPHeader>();
        //-------------------------- Find the start of the body
        while(curr < message.length && !stopFlag) {
            char c = (char) message[curr];
            
            switch(state) {
                case START:
                    switch(c) {
                        case '\r':
                            state = CR;
                            curr++;
                            break;
                        default:
                            curr++;
                    }
                    break;
                case CR:
                    switch(c) {
                        case '\n':
                            state = CRLF;
                            curr++;
                            break;
                        default:
                            state = START;
                            curr++;
                    }
                    break;
                case CRLF:
                    switch(c) {
                        case '\r':
                            state = CRLFCR;
                            curr++;
                            break;
                        default:
                            state = START;
                            curr++;
                    }
                    break;
                case CRLFCR:
                    switch(c) {
                        case '\n':
                            stopFlag = true;
                            curr++; //curr now points to the 1st byte of the
                                    //message body
                            break;
                        default:
                            state = START;
                            curr++;
                    }
                    break;
            }
            
        } // end while
        //if we didn't actually stop before the end of the message, its bad
        if(!stopFlag) {
            throw new Exception("NOT A VALID HTTP MESSAGE!");
        }
        body = new byte[message.length - curr];
        System.arraycopy(message, curr, body, 0, message.length - curr);
        //grab the headers
        byte [] headerBytes = new byte[curr];
        System.arraycopy(message, 0, headerBytes, 0, curr);
        
        String [] headers = (new String(headerBytes)).split("\r\n");
        
        action = headers[0];
        if(headers.length > 1) {
            for(int i = 1; i < headers.length; i++) {
                msgHeaders.add(new HTTPHeader(headers[i]));
            }
        }

    }
   
    public boolean setAction(String s) {
        action = s;
        return true; //assume this is valid, overriding classes take care of
                     //specifics
    }
    
    public String getAction() {
        return action;
    }
    
    
    public byte [] getMessage() {
        //create it
        MyByteArray mba = new MyByteArray();
        mba.add(action);
        mba.add("\r\n");
        for(HTTPHeader h: msgHeaders) {
            mba.add(h.toString());
            mba.add("\r\n");
        }
        mba.add("\r\n");
        if(body != null) {
            mba.add(body);
        }
        
        return mba.getBytes();
    }

    public String getHeader(String name) {
        for(HTTPHeader h : msgHeaders) {
            if(h.getName().equalsIgnoreCase(name))
                return h.getValue();
        }
        return "";
    }

    public HTTPHeader [] getHeaders() {
        return (HTTPHeader []) msgHeaders.toArray(new HTTPHeader[0]);
    }
    
    public void addHeader(String s) {
        msgHeaders.add(new HTTPHeader(s));
    }
    
    public void removeHeader(String name) {
        for(int i=0; i < msgHeaders.size(); i++) {
            if(msgHeaders.get(i).getName().equalsIgnoreCase(name))
                msgHeaders.remove(i);
        }
    }
    
}
