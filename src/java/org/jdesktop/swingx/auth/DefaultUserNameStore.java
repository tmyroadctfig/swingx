/*
 * DefaultUserNameStore.java
 *
 * Created on June 4, 2005, 2:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx.auth;

import java.beans.PropertyChangeSupport;
import java.util.prefs.Preferences;
import org.jdesktop.swingx.JXLoginPanel;

/**
 * Saves the user names in Preferences. Because any string could be part
 * of the user name, for every user name that must be saved a new Preferences
 * key/value pair must be stored.
 *
 * @author Bino George
 * @author rbair
 */
public class DefaultUserNameStore extends UserNameStore {
    /**
     * The key for one of the preferences
     */
	private static final String USER_KEY = "usernames";
    /**
     */
    private static final String NUM_KEY = "usernames.length";
    /**
     * The preferences node
     */
    private Preferences prefs;
    /**
     * A name that is used when retrieving preferences. By default, the
     * app name is &quot;default&quot. This should be set by the application
     * if the application wants it&apos;s own list of user names.
     */
	private String appNameForPreferences = "default";
    /**
     * Contains the user names. Since the list of user names is not
     * frequently updated, there is no penalty in storing the values
     * in an array.
     */
    private String[] userNames;
    /**
     * Used for propogating bean changes
     */
    private PropertyChangeSupport pcs;
    
    /**
     * Creates a new instance of DefaultUserNameStore 
     */
    public DefaultUserNameStore() {
        pcs = new PropertyChangeSupport(this);
        userNames = new String[0];
    }
    
    public void loadUserNames() {
        initPrefs();
        if (prefs != null) {
            String numPrefix = getNumPrefix();
            int n = prefs.getInt(numPrefix, 0);
            String valuePrefix = getValuePrefix();
            String[] names = new String[n];
            for (int i = 0; i < n; i++) {
                names[i] = prefs.get(valuePrefix + "." + i, null);
            }
        }
    }
    
    public void saveUserNames() {
        initPrefs();
        if (prefs != null) {
            String numPrefix = getNumPrefix();
            String valuePrefix = getValuePrefix();
            prefs.putInt(numPrefix, userNames.length);
            for (int i = 0; i < userNames.length; i++) {
                prefs.put(valuePrefix + "." + i, userNames[i]);
            }
        }
    }

    public String[] getUserNames() {
        return userNames;
    }

    public void setUserNames(String[] userNames) {
        if (this.userNames != userNames) {
            String[] old = this.userNames;
            this.userNames = userNames == null ? new String[0] : userNames;
            pcs.firePropertyChange("userNames", old, this.userNames);
        }
    }
    
	/**
	 * Add a username to the store.
	 * @param username
	 */
	public void addUserName(String name) {
		if (!containsUserName(name)) {
            String[] newNames = new String[userNames.length + 1];
            for (int i=0; i<userNames.length; i++) {
                newNames[i] = userNames[i];
            }
            newNames[newNames.length - 1] = name;
            setUserNames(newNames);
		}
	}
	
	/**
	 * Removes a username from the list.
	 * 
	 * @param username
	 */
	public void removeUserName(String name) {
		if (containsUserName(name)) {
            String[] newNames = new String[userNames.length - 1];
            int index = 0;
            for (String s : userNames) {
                if (!s.equals(name)) {
                    newNames[index++] = s;
                }
            }
            setUserNames(newNames);
		}
	}
	
    /**
     *
     */
    public boolean containsUserName(String name) {
        for (String s : userNames) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
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
        if (this.appNameForPreferences != appNameForPreferences) {
            String old = this.appNameForPreferences;
            this.appNameForPreferences = appNameForPreferences;
            pcs.firePropertyChange("appNameForPreferences", old, appNameForPreferences);
            loadUserNames();
        }
	}
    
	private String getNumPrefix() {
		return this.getClass().getName() + "." + getAppNameForPreferences()
				+ "." + NUM_KEY;
	}

	private String getValuePrefix() {
		return this.getClass().getName() + "." + getAppNameForPreferences()
				+ "." + USER_KEY;
	}

    private void initPrefs() {
        if (prefs == null) {
            prefs = Preferences.userNodeForPackage(JXLoginPanel.class);
        }
    }
}