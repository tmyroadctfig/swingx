/*
 * SimpleLoginService.java
 *
 * Created on June 4, 2005, 1:58 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx.auth;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of LoginService that simply matches
 * the username/password against a list of known users and their passwords.
 * This is useful for demos or prototypes where a proper login server is not available.
 *
 * <em>This Implementation is NOT secure. DO NOT USE this in a real application</em>
 * To make this implementation more secure, the passwords should be passed in and
 * stored as the result of a one way hash algorithm. That way an attacker cannot 
 * simply read the password in memory to crack into the system.
 *
 * @author rbair
 */
public final class SimpleLoginService extends LoginService {
    private Map<String,char[]> passwordMap;
    
    /**
     * Creates a new SimpleLoginService based on the given password map.
     */
    public SimpleLoginService(Map<String,char[]> passwordMap) {
        if (passwordMap == null) {
            passwordMap = new HashMap<String,char[]>();
        }
        this.passwordMap = passwordMap;
    }

    /**
     * Attempts to authenticate the given username and password against the password map
     */
    public boolean authenticate(String name, char[] password, String server) throws IOException {
        char[] p = passwordMap.get(name);
        return Arrays.equals(password, p);
    }
}
