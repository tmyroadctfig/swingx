/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.auth;

/**
 * <b>UsernameStore</b> is a class that implements persistence of usernames
 * 
 * @author Bino George
 * @author rbair
 */
public abstract class UserNameStore {
	/**
	 * Gets the current list of users.
	 */
	public abstract String[] getUserNames();
    /**
     */
    public abstract void setUserNames(String[] names);
    /**
     * lifecycle method for loading names from persistent storage
     */
    public abstract void loadUserNames();
    /**
     * lifecycle method for saving name to persistent storage
     */
    public abstract void saveUserNames();
    /**
     */
    public abstract boolean containsUserName(String name);

    /**
	 * Add a username to the store.
	 * @param userName
	 */
	public abstract void addUserName(String userName);
	
	/**
	 * Removes a username from the list.
	 * @param userName
	 */
	public abstract void removeUserName(String userName);
}
