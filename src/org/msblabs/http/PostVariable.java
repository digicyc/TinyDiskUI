/**
 *Some stuff for variables in a POST content body. Mainlly it self-encodes
 *
 * damn you GATECH for teaching me so much abstraction!
 */

package org.msblabs.http;

import java.net.URLEncoder;

/**
 *
 * @author acidus
 */
public class PostVariable {
    
    private String name;
    private String value;    

    
    //create an attribute from a name=value pair
    //removes the quotes name="value" if they exist
    public PostVariable(String n, String v) {
        name = n;
        value = v;
    }
    
    public String getName() {
        return name;
    }    
    
    public String getValue() {
        return value;
    }

    public String toHTMLString() {
        StringBuilder sb;
        try {
            sb = new StringBuilder();
            sb.append(URLEncoder.encode(name, "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(value, "UTF-8"));
        } catch (Exception e) {
            sb = new StringBuilder();            
            sb.append(name);
            sb.append("=");
            sb.append(value);
        }
        return sb.toString();
    }
    
}
