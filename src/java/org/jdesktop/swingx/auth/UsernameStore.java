package org.jdesktop.swingx.auth;

/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */


import java.util.Vector;
import java.util.prefs.Preferences;


/**
 * <b>UsernameStore</b> is a class that implements persistence of usernames
 * using preferences.
 * 
 * @author Bino George
 */
class UsernameStore {

	private Preferences prefs = Preferences
			.userNodeForPackage(JXLoginPanel.class);

	private String appNameForPreferences = "default";

	private static final String USER_KEY = "usernames";

	private static final String NUM_KEY = "usernames.length";

	private Vector<String> usernames = null;

	private static UsernameStore INSTANCE = null;
	
	private UsernameStore() {
		usernames = new Vector<String>();
		String numPrefix = getNumPrefix();
		int n = prefs.getInt(numPrefix, 0);
		String valuePrefix = getValuePrefix();
		String value;
		for (int i = 0; i < n; i++) {
			value = prefs.get(valuePrefix + "." + i, null);
			if (value != null) {
				usernames.add(value);
			}
		}
	}
	
	public static UsernameStore getUsernameStore() {
		synchronized (UsernameStore.class)
		{
			if (INSTANCE == null) {
				INSTANCE = new UsernameStore();
			}
		}
		return INSTANCE;
	}
	
	String getNumPrefix() {
		return this.getClass().getName() + "." + getAppNameForPreferences()
				+ "." + NUM_KEY;
	}

	String getValuePrefix() {
		return this.getClass().getName() + "." + getAppNameForPreferences()
				+ "." + USER_KEY;
	}

	/**
	 * Gets the current list of users.
	 * 
	 */
	public Vector getUsernames() {
		return usernames;
	}

	/**
	 * Saves the current list of usernames
	 *
	 */
	public void saveUsernames() {
		if (prefs != null) {
			String numPrefix = getNumPrefix();
			String valuePrefix = getValuePrefix();
			prefs.putInt(numPrefix, usernames.size());
			for (int i = 0; i < usernames.size(); i++) {
				prefs.put(valuePrefix + "." + i, usernames.get(i));
			}
		}
	}
	
	/**
	 * Add a username to the store.
	 * @param username
	 */
	public void addUsername(String username) {
		if (!usernames.contains(username)) {
			usernames.add(username);
		}
	}
	
	/**
	 * Removes a username from the list.
	 * 
	 * @param username
	 */
	public void removeUsername(String username) {
		if (usernames.contains(username)) {
			usernames.remove(username);
		}
	}
	
	/**
	 * @return Returns the appNameForPreferences.
	 */
	public String getAppNameForPreferences() {
		return appNameForPreferences;
	}
	/**
	 * @param appNameForPreferences The appNameForPreferences to set.
	 */
	public void setAppNameForPreferences(String appNameForPreferences) {
		this.appNameForPreferences = appNameForPreferences;
	}
}
