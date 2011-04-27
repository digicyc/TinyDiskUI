/**
 * Specical POST specific stuff
 */

package org.msblabs.http;

import java.util.ArrayList;

/**
 *
 * @author acidus
 */
public class HTTPPostMessage extends HTTPMessage{

    
    private ArrayList<PostVariable> variables;

    private String getHost(String url) {
        int i=0;
        if((i =  url.indexOf("://")) > 0)
            i += 3;
        //i is start of host
        int j;
        if((j =  url.indexOf("/", i)) < 0)
            j = url.length();
        return url.substring(i, j);
    }
    
    private String getResource(String url) {
        int i=0;
        if((i =  url.indexOf("://")) > 0)
            i += 3;
        //i is start of host
        int j;
        if((j =  url.indexOf("/", i)) < 0)
            return "/";
        return url.substring(j, url.length());
    }    
    
    
    /** Creates a new instance of HTTPPostMessage */
    public HTTPPostMessage(String url) {
        super();
        StringBuilder sb = new StringBuilder();
        sb.append("POST ");
        sb.append(getResource(url));
        sb.append(" HTTP/1.1");
        action = sb.toString();
        addHeader("Host: " + getHost(url));
        
        variables = new ArrayList<PostVariable>();
    }

    public void addVariable(String name, String value) {
        variables.add(new PostVariable(name, value));
    }
    
    public void finalize() {
        //convert all the variables into a body byte array
        StringBuffer sb = new StringBuffer();
        for(int i =0; i < variables.size(); i++) {
            if(i> 0)
                sb.append("&");
            sb.append(variables.get(i).toHTMLString());
        }
        MyByteArray mba = new MyByteArray();
        mba.add(sb.toString());
        body = mba.getBytes();
        addHeader("Content-Type: application/x-www-form-urlencoded");
        addHeader("Content-Length: " + body.length);
    }
    
    
}
