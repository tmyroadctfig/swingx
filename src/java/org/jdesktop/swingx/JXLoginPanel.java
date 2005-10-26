/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.jdesktop.swingx.auth.DefaultUserNameStore;
import org.jdesktop.swingx.auth.LoginEvent;
import org.jdesktop.swingx.auth.LoginListener;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.auth.PasswordStore;
import org.jdesktop.swingx.auth.UserNameStore;

/**
 *  <p>JXLoginPanel is a JPanel that implements a Login dialog with
 *  support for saving passwords supplied for future use in a secure
 *  manner. It is intended to work with <strong>LoginService</strong>
 *  and <strong>PasswordStore</strong> to implement the
 *  authentication.</p>
 *
 *  <p> In order to perform the authentication, <strong>JXLoginPanel</strong>
 *  calls the <code>authenticate</code> method of the <strong>LoginService
 *  </strong>. In order to perform the persistence of the password,
 *  <strong>JXLoginPanel</strong> calls the put method of the
 *  <strong>PasswordStore</strong> object that is supplied. If
 *  the <strong>PasswordStore</strong> is <code>null</code>, then the password
 *  is not saved. Similarly, if a <strong>PasswordStore</strong> is
 *  supplied and the password is null, then the <strong>PasswordStore</strong>
 *  will be queried for the password using the <code>get</code> method.
 *
 * Changes by Shai:
 * Clarified the save mode a little bit including hiding the save checkbox when there
 * is no password store.
 * Changed the class to derive from JXImagePanel to make customization easier (need to
 * check my ImagePanel which has some technical advantages).
 * Removed the static keyword from the ok/cancel buttons since this can cause an issue
 * with more than one login dialogs (yes its an unlikely situation but documenting this
 * sort of behavior or dealing with one bug resulting from this can be a real pain!).
 * Allowed the name field to be represented as a text field when there is no password store.
 * Rewrote the layout code to mostly work with a single container.
 * Removed additional dialogs for progress and error messages and incorporated their 
 * functionality into the main dialog.
 * Allowed for an IOException with a message to be thrown by the login code. This message
 * is displayed to the user when the login is stopped.
 * Removed repetetive code and moved it to a static block.
 * i18n converted some of the strings that were not localized.
 *
 * @author Bino George
 * @author Shai Almog
 * @author rbair
 */

public class JXLoginPanel extends JXImagePanel {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3544949969896288564L;
    /**
     * The JXLoginPanel can attempt to save certain user information such as
     * the username, password, or both to their respective stores.
     * This type specifies what type of save should be performed.
     */
    public static enum SaveMode { NONE, USER_NAME, PASSWORD, BOTH};
    /**
     * Used as a prefix when pulling data out of UIManager for i18n
     */
    private static String CLASS_NAME;
    /**
     * This is the area in which the name field is placed. That way it can toggle on the fly 
     * between text field and a combo box depending on the situation.
     */
    private JXPanel namePanel;
    /**
	 * If a UserNameStore is used, then this combo box is presented allowing the user
	 * to select a previous login name, or type in a new login name
	 */
	private JComboBox nameCombo;
    /**
	 * If a UserNameStore is not used, then this text field is presented allowing the user
	 * to simply enter their user name
	 */
	private JTextField nameField;
    /**
     * The password field presented allowing the user to enter their password
     */
	private JPasswordField passwordField;
    /**
     * Check box presented if a PasswordStore is used, allowing the user to decide whether to
     * save their password
     */
	private JCheckBox saveCB;
    /**
     * The LoginService to use. This must be specified for the login dialog to operate.
     * If no LoginService is defined, a default login service is used that simply
     * allows all users access. This is useful for demos or prototypes where a proper login
     * server is not available.
     */
	private LoginService loginService;
    /**
     * Optional: a PasswordStore to use for storing and retrieving passwords for a specific
     * user.
     */
	private PasswordStore passwordStore;
    /**
	 * Optional: a UserNameStore to use for storing user names and retrieving them
	 */
	private UserNameStore userNameStore;
	/**
	 *  Whether to save password or username or both
	 */
	private SaveMode saveMode;
    /**
     * Listens to save events
     */
    private SaveListener saveListener;
    /**
     * Tracks the cursor at the time that authentication was started, and restores to that
     * cursor after authentication ends, or is cancelled;
     */
    private Cursor oldCursor;
    
	/**
	 * Creates a default JXLoginPanel instance
	 */
	static {
		// Popuplate UIDefaults with the localizable Strings we will use
		// in the Login panel.
		CLASS_NAME = JXLoginPanel.class.getCanonicalName();
		String lookup;
        ResourceBundle res = ResourceBundle.getBundle("org.jdesktop.swingx.auth.resources.resources");
        Enumeration<String> keys = res.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
			lookup = CLASS_NAME + "." + key;
			if (UIManager.getString(lookup) == null) {
				UIManager.put(lookup, res.getString(key));
			}
        }
	}

	public JXLoginPanel() {
        this(null);
	}
    
    public JXLoginPanel(LoginService service) {
        this(service, null, null);
    }
    
    public JXLoginPanel(LoginService service, PasswordStore passwordStore, UserNameStore userStore) {
		this.loginService = service == null ? new NullLoginService() : service;
		this.passwordStore = passwordStore == null ? new NullPasswordStore() : passwordStore;
        this.userNameStore = userStore == null ? new DefaultUserNameStore() : userStore;
        
        //initialize the save mode
        if (passwordStore != null && userStore != null) {
            saveMode = SaveMode.BOTH;
        } else if (passwordStore != null) {
            saveMode = SaveMode.PASSWORD;
        } else if (userStore != null) {
            saveMode = SaveMode.USER_NAME;
        } else {
            saveMode = SaveMode.NONE;
        }
        
        saveListener = new SaveListener();
		this.loginService.addLoginListener(saveListener);
		setLayout(new BorderLayout());
		add(createLoginPanel(), BorderLayout.CENTER);
    }
    
//		if (username != null) {
//			userNameStore.addUsername(username);
//		}
    
    private JXPanel createNamePanel() {
        JXPanel namePanel = new JXPanel(new BorderLayout());
        nameField = new JTextField("", 15);
        nameField.setEditable(true);
        nameCombo = new JComboBox(new ComboBoxModel() {
            private Object selectedItem;
            public void setSelectedItem(Object anItem) {
                selectedItem = anItem;
            }
            public Object getSelectedItem() {
                return selectedItem;
            }
            public Object getElementAt(int index) {
                return userNameStore.getUserNames()[index];
            }
            public int getSize() {
                return userNameStore.getUserNames().length;
            }
            public void removeListDataListener(javax.swing.event.ListDataListener l) {
                //TODO
            }
            public void addListDataListener(javax.swing.event.ListDataListener l) {
                //TODO
            }
        });
        nameCombo.setEditable(true);
        if (saveMode == SaveMode.NONE) {
            namePanel.add(nameField, BorderLayout.CENTER);
        } else {
            namePanel.add(nameCombo, BorderLayout.CENTER);
        }
        return namePanel;
    }
    
	private JXPanel createLoginPanel() {
        JXPanel loginPanel = new JXPanel();
        JLabel nameLabel = new JLabel(UIManager.getString(CLASS_NAME + ".nameString"));
        namePanel = createNamePanel();
        JLabel passwordLabel = new JLabel(UIManager.getString(CLASS_NAME + ".passwordString"));
		passwordField = new JPasswordField("", 15);
        saveCB = new JCheckBox(UIManager.getString(CLASS_NAME + ".rememberPasswordString"));
        saveCB.setVisible(saveMode == SaveMode.PASSWORD || saveMode == SaveMode.BOTH);
        
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 0, 5, 11);
        loginPanel.add(nameLabel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        loginPanel.add(namePanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 0, 11, 11);
        loginPanel.add(passwordLabel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 11, 0);
        loginPanel.add(passwordField, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        loginPanel.add(saveCB, gridBagConstraints);

        return loginPanel;
    }
    
	/**
	 * Listener class to implement saving of passwords and usernames.
	 * 
	 * 
	 */
	class SaveListener implements LoginListener {
		public void loginFailed(LoginEvent source) {
		}

		public void loginSucceeded(LoginEvent source) {
			if (getSaveMode() == SaveMode.PASSWORD || getSaveMode() == SaveMode.BOTH) {
				savePassword();
			} else if (getSaveMode() == SaveMode.USER_NAME) {
				userNameStore.addUserName(nameField.getText());
				userNameStore.saveUserNames();
			}
		}

		public void loginStarted(LoginEvent source) {
		}

		public void loginCanceled(LoginEvent source) {
		}
	}

	void savePassword() {
		if (passwordStore != null) {
			passwordStore.set(getUserName(),getLoginService().getServer(),getPassword());
		}
	}

	/**
	 * @return Returns the saveMode.
	 */
	public SaveMode getSaveMode() {
		return saveMode;
	}
    
    /**
     * The save mode indicates whether the "save" password is checked by default. This method
     * makes no difference if the passwordStore is null.
     *
	 * @param saveMode The saveMode to set either SAVE_NONE, SAVE_PASSWORD or SAVE_USERNAME
	 */
	public void setSaveMode(SaveMode saveMode) {
        namePanel.removeAll();
		this.saveMode = saveMode;
        switch (saveMode) {
            case USER_NAME:
            case PASSWORD:
            case BOTH:
                namePanel.add(nameCombo, BorderLayout.CENTER);
                saveCB.setVisible(!(passwordStore instanceof NullPasswordStore));
                revalidate();
                break;
            default:
                namePanel.add(nameField, BorderLayout.CENTER);
                saveCB.setSelected(false);
                saveCB.setVisible(false);
                revalidate();
        }
	}

	/**
	 * Sets the <strong>LoginService</strong> for this panel.
	 *
	 * @param service service
	 */
	public void setLoginService(LoginService service) {
		loginService = service;
	}

	/**
	 * Gets the <strong>LoginService</strong> for this panel.
	 *
	 * @return service service
	 */
	public LoginService getLoginService() {
		return loginService;
	}

	/**
	 * Sets the <strong>PasswordStore</strong> for this panel.
	 *
	 * @param store PasswordStore
	 */
	public void setPasswordStore(PasswordStore store) {
		passwordStore = store;
	}

	/**
	 * Gets the <strong>PasswordStore</strong> for this panel.
	 *
	 * @return store PasswordStore
	 */
	public PasswordStore getPasswordStore() {
		return passwordStore;
	}

	/**
	 * Sets the <strong>User name</strong> for this panel.
	 *
	 * @param username User name
	 */
	public void setUserName(String username) {
        if(saveMode == SaveMode.NONE) {
            nameField.setText(username);
        } else {
            nameCombo.setSelectedItem(username);
        }
	}

	/**
	 * Gets the <strong>User name</strong> for this panel.
	 * @return the user name
	 */
	public String getUserName() {
        if(saveMode != SaveMode.NONE) {
            return (String)nameCombo.getSelectedItem();
        } else {
            return nameField.getText();
        }
	}

	/**
	 * Sets the <strong>Password</strong> for this panel.
	 *
	 * @param password Password
	 */
	public void setPassword(char[] password) {
		passwordField.setText(new String(password));
	}

	/**
	 * Gets the <strong>Password</strong> for this panel.
	 *
	 * @return password Password
	 */
	public char[] getPassword() {
		return passwordField.getPassword();
	}

    /**
     *
     */
    public void startLogin() {
        oldCursor = getCursor();
        try {
//            loginPanel.progressIndicator.setIndeterminate(true);
//            loginPanel.cancelLogin.setEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            String name = getUserName();
            char[] password = getPassword();
            loginService.startAuthentication(name, password, null);
            if (saveMode != SaveMode.NONE && !userNameStore.containsUserName(name)) {
                userNameStore.addUserName(name);
                userNameStore.saveUserNames();
            }
            if (saveCB.isSelected() && (saveMode == SaveMode.BOTH || saveMode == SaveMode.PASSWORD)) {
                passwordStore.set(name, loginService.getServer(), password);
            }
//            UserPermissions.getInstance().setRoles(service.getUserRoles());
        } catch(IOException ioerr) {
//            loginPanel.loginProgress.setText(ioerr.getMessage());
//            finishedLogin(false);
        } finally {
            setCursor(oldCursor);
        }
    }
    
    public void cancelLogin() {
        loginService.cancelAuthentication();
        setCursor(oldCursor);
    }
    
    /**
     * Simple login service that allows everybody to login. This is useful in demos and allows
     * us to avoid having to check for LoginService being null
     */
    private static final class NullLoginService extends LoginService {
        public boolean authenticate(String name, char[] password, String server) throws IOException {
            return true;
        }
    }
    
    /**
     * Simple PasswordStore that does not remember passwords
     */
    private static final class NullPasswordStore extends PasswordStore {
        private static final char[] EMPTY = new char[0];
        public boolean set(String username, String server, char[] password) {
            //null op
            return false;
        }
        public char[] get(String username, String server) {
            return EMPTY;
        }
    }
}