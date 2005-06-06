/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.auth;

/**
 *  PasswordStore specifies a mechanism to store passwords used to authenticate
 *  using the <strong>LoginService</strong>. The actual mechanism used
 *  to store the passwords is left up to the implementation.
 *
 *	@author Bino George
 */
public abstract class PasswordStore {
    /**
     *  Saves a password for future use. 
     *
     *  @param username username used to authenticate.
     *  @param server server used for authentication
     *  @param password password to save
     */
    public abstract boolean set(String username, String server, char[] password);
    
    /** 
     * Fetches the password for a given server and username
     *  @param username username
     *  @param server server
     *  @return password 
     */
    public abstract char[] get(String username, String server);
}
