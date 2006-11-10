/*
 * StringUtils.java
 *
 * Created on September 21, 2006, 12:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.util;

import java.util.Map;

/**
 *
 * @author rbair
 */
public final class StringUtils {
    
    /** Creates a new instance of StringUtils */
    private StringUtils() {}
    
    /**
     * Returns the input String after any variable substitution. The given input
     * String may contain text such as "${os.name} is not supported". Values between
     * "${" and "}" are understood to be variable names. If a variable name
     * is a key in the "state" map, then the value in that map will be used to replace
     * the variable in the input String. Unknown variables will be left intact.
     *
     * If you want the actual character sequence "${" preserved, then prefix with
     * a backslash ("\\${").
     *
     * @param input The input String that will have its variables replaced. May be null.
     * @return the substituted input String. Null only if input was null.
     */
    public static String replaceVariables(String input, Map<String,String> variables) {
        if (input == null) {
            return null;
        }
        
        StringBuffer buffer = new StringBuffer(input);
        for (int i=0; i<buffer.length(); i++) {
            if (buffer.length() < i + 3) {
                //there isn't a variable here, so just continue
                break;
            }
            
            //looking for a variable to parse
            char c = buffer.charAt(i);
            if (c == '$') {
                int indexOfDollar = i;
                
                //look ahead and see if this is a variable
                if (buffer.charAt(i+1) == '{') {
                    //now look for the closing brace
                    int indexOfClosingBrace = -1;
                    for (int n=i+1; n<buffer.length(); n++) {
                        if (buffer.charAt(n) == '}') {
                            indexOfClosingBrace = n;
                            break;
                        }
                    }
                    
                    //if I found a closing brace, I have found a variable
                    if (indexOfClosingBrace != -1) {
                        String variable = buffer.substring(indexOfDollar + 2, indexOfClosingBrace);
                        if (variables.containsKey(variable)) {
                            String value = variables.get(variable);
                            buffer.replace(indexOfDollar, indexOfClosingBrace + 1, value);
                            i = indexOfDollar + (value == null ? 0 : value.length()) - 1;
                        }
                    }
                }
            } else if (c == '\\' && buffer.charAt(i+1) == '$') {
                //eat the backslash
                buffer.replace(i, i+1, "");
                i++; //get past the dollar sign
            }
        }
        
        return buffer.toString();
    }
}
