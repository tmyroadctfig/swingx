package org.jdesktop.swingx.auth;

/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import org.jdesktop.swingx.util.WindowUtils;

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
 * @author Bino George
 */

public class JXLoginPanel extends JPanel {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3544949969896288564L;

	private JLabel nameLabel;

	private JLabel passwordLabel;

	private JComboBox nameField;

	private String username;

	private JPasswordField passwordField;

	private JCheckBox save;

	private JComponent header;

	private JComponent label;

	private static JButton okButton;

	// Cancel Button on the login dialog
	private static JButton cancelButton;

	private JPanel loginPanel;

	private JPanel savePanel;

	private JPanel headerPanel;

	private LoginService loginService;

	private PasswordStore passwordStore;

	private UsernameStore usernameStore;

	private Handler handler = new Handler();

	//TODO This belongs in Application or some other class.
	private static HashMap<String, Object> _resources;

	/**
	 *  Whether to save password or username ?
	 */
	private int saveMode;

	/**
	 * Dont save anything.
	 */
	public static final int SAVE_NONE = 0;

	/**
	 * Save the password using PasswordStore
	 */
	public static final int SAVE_PASSWORD = 1;

	/**
	 * Save the username in the Preferences node for this class.
	 */
	public static final int SAVE_USERNAME = 2;

	/**
	 * Creates a default JXLoginPanel instance
	 */
	
	static {
	
		// Popuplate UIDefaults with the localisable Strings we will use
		// in the Login panel.
		
		String keys[] = {  "okString", "okString.mnemonic", 
                                   "cancelString", "cancelString.mnemonic",
                                    "nameString", "loginString",
				"passwordString","rememberUserString", 
				"rememberPasswordString", "loggingInString",  };
		
		String className = JXLoginPanel.class.getCanonicalName();
		String lookup;
		for (String key : keys) {
			lookup = className + "." + key;
			if (UIManager.getString(lookup) == null) {
				UIManager.put(lookup, getResourceAsObject(key));
			}
		}
	}

	public JXLoginPanel() {
		this(null, null, null, null, null, null);
	}

	/**
	 * Creates a JXLoginPanel with the supplied parameters
	 *
	 * @param name Name, can be null.
	 * @param password Password, can be null.
	 * @param service an Object that implements LoginService
	 * @param store an Object that implements Password store, can be null.
	 */
	public JXLoginPanel(String name, String password, LoginService service,
			PasswordStore store) {
		this(name, password, service, store, null, null);
	}

	/**
	 * Creates a JXLoginPanel with the supplied parameters
	 *
	 * @param name Name, can be null.
	 * @param password Password, can be null.
	 * @param service an Object that implements LoginService
	 * @param store an Object that implements Password store, can be null.
	 * @param header a JComponent to use as the header, can be null.
	 * @paran label a JComponent to use as the label, can be null.
	 */
	public JXLoginPanel(String name, String password, LoginService service,
			PasswordStore store, JComponent header, JComponent label) {
		loginService = service;
		passwordStore = store;
		this.username = name;
		this.header = header;
		this.label = label;
		service.addLoginListener(new SaveListener());
		init(name, password);
	}

	void init(String nameStr, String passwordStr) {
		usernameStore = UsernameStore.getUsernameStore();
		if (username != null) {
			usernameStore.addUsername(username);
		}
		setLayout(new BorderLayout(5, 5));
		createLoginPanel(nameStr, passwordStr);
		add(loginPanel, BorderLayout.CENTER);
		add(headerPanel, BorderLayout.NORTH);
	}

	void createLoginPanel(String nameStr, String passwordStr) {

		String className = JXLoginPanel.class.getCanonicalName();
		
		nameLabel = new JLabel(UIManager.getString(className + "." + "nameString"));
          	passwordLabel = new JLabel(UIManager.getString(className + "." + "passwordString"));

		loginPanel = new JPanel();

		savePanel = new JPanel();
		savePanel.setLayout(new GridBagLayout());
		headerPanel = new JPanel();

		headerPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		nameField = new JComboBox(usernameStore.getUsernames());//new JTextField(nameStr, 15);
		nameField.setEditable(true);
		nameField.addActionListener(handler);
		nameField.addFocusListener(handler);

		passwordField = new JPasswordField(passwordStr, 15);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(12, 12, 5, 12);
		gbc.anchor = GridBagConstraints.WEST;
		loginPanel.setLayout(new GridBagLayout());
		loginPanel.add(nameLabel, gbc);

		gbc.gridx = 1;
		gbc.insets = new Insets(12, 0, 5, 11);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		loginPanel.add(nameField, gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		gbc.insets = new Insets(0, 12, 6, 12);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		loginPanel.add(passwordLabel, gbc);

		gbc.gridx = 1;
		gbc.insets = new Insets(0, 0, 5, 11);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		loginPanel.add(passwordField, gbc);

		gbc.gridy++;
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.NONE;
		loginPanel.add(savePanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 0;
		if (header != null) {
			gbc.insets = new Insets(0, 0, 0, 0);
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			headerPanel.add(header, gbc);
			gbc.gridy++;
		}
		if (label != null) {
			gbc.insets = new Insets(12, 12, 0, 0);
			headerPanel.add(label, gbc);
		}

	}

	/**
	 * Listener class to implement saving of passwords and usernames.
	 * 
	 * 
	 */
	class SaveListener implements LoginListener {
		public void loginFailed(EventObject source) {
		}

		public void loginSucceeded(EventObject source) {
			if (getSaveMode() == SAVE_PASSWORD) {
				savePassword();
			} else if (getSaveMode() == SAVE_USERNAME) {
				usernameStore.addUsername(username);
				usernameStore.saveUsernames();
			}
		}

		public void loginStarted(EventObject source) {
		}

		public void loginCanceled(EventObject source) {
		}
	}

	void savePassword() {
		if (passwordStore != null) {
			passwordStore.set(getUserName(),getLoginService().getServer(),getPassword());
		}
	}

	private class Handler implements ActionListener, FocusListener {
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() == nameField) {
				username = (String) nameField.getSelectedItem();
			}
		}

		public void focusGained(FocusEvent e) {

		}

		public void focusLost(FocusEvent e) {
			if (e.getSource() == nameField) {
				username = (String) nameField.getSelectedItem();
			}
		}
	}

	private static class DialogHelper implements LoginListener, ActionListener {
		JDialog dialog;

		JXLoginPanel loginPanel;

		JPanel buttonPanel;

		JPanel mainPanel;

		LoginService service;

		private JPanel progressPanel;

		private JProgressBar progressBar;

		private JButton loginCancelButton;

		private boolean cancelled;

		DialogHelper(Frame frame, JXLoginPanel panel, LoginService service) {
			String className = JXLoginPanel.class.getCanonicalName();
			this.loginPanel = panel;
			this.service = service;
			progressPanel = createProgressPanel();
			dialog = new JDialog(frame, UIManager.getString(className + "." + "loginString"));
			service.addLoginListener(this);
			okButton = new JButton(UIManager.getString(className + "." + "okString"));
                        okButton.setMnemonic(UIManager.getInt(className + "." + "okString.mnemonic")); 
                     	okButton.addActionListener(this);
			cancelButton = new JButton(UIManager.getString(className + "." + "cancelString"));
                        cancelButton.setMnemonic(UIManager.getInt(className + "." + "cancelString.mnemonic"));
			cancelButton.addActionListener(this);
			buttonPanel = new JPanel();
                        JPanel innerPanel = new JPanel();
                        innerPanel.setLayout(new GridLayout(1,2));
			buttonPanel.setLayout(new java.awt.GridBagLayout());

			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints.insets = new java.awt.Insets(17, 5, 11, 11);
			innerPanel.add(okButton);
                        innerPanel.add(cancelButton);
                        buttonPanel.add(innerPanel, gridBagConstraints);
                        
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			buttonPanel.add(new JLabel(), gridBagConstraints);

			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(loginPanel, BorderLayout.CENTER);
			mainPanel.add(buttonPanel, BorderLayout.SOUTH);
			dialog.add(mainPanel);
                        
                        dialog.getRootPane().setDefaultButton(okButton);
			dialog.pack();
			dialog.setModal(true);
			dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            dialog.setLocation(WindowUtils.getPointForCentering(dialog));
		}

		JPanel createProgressPanel() {
			String className = JXLoginPanel.class.getCanonicalName();
			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(8, 8, 8, 8);
			gbc.gridx = 0;
			gbc.gridy = 0;
			progressBar = new JProgressBar();
			panel.add(new JLabel(UIManager.getString(className + "." +"loggingInString")), gbc);
			gbc.gridy = 1;
			panel.add(progressBar, gbc);
			gbc.gridy = 2;
			loginCancelButton = new JButton(UIManager.getString(className + "." + "cancelString"));
			loginCancelButton.addActionListener(this);
			panel.add(loginCancelButton, gbc);
			return panel;
		}

		public void show() {
			dialog.setVisible(true);
		}

		public void loginFailed(EventObject source) {
			finishedLogin(false);
			JOptionPane.showMessageDialog(dialog, "Login failed !");

		}

		public void loginSucceeded(EventObject source) {
			finishedLogin(true);
			JOptionPane.showMessageDialog(dialog, "Login Succeeded !");
			dialog.dispose();
		}

		public void loginStarted(EventObject source) {

		}

		/**
		 *
		 */
		void startLogin() {
			cancelled = false;
			dialog.remove(mainPanel);
			progressBar.setIndeterminate(true);
			dialog.add(progressPanel);
			loginPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			dialog.pack();
			String name = loginPanel.getUserName();
			char[] password = loginPanel.getPassword();
			service.startAuthentication(name, password, null);
		}

		void finishedLogin(boolean result) {
			progressBar.setIndeterminate(false);
			dialog.remove(progressPanel);
			dialog.add(mainPanel);
			dialog.pack();
			loginPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}

		void cancelAuthentication() {
			service.cancelAuthentication();
			progressBar.setIndeterminate(false);
			dialog.remove(progressPanel);
			dialog.add(mainPanel);
			dialog.pack();
			loginPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}

		public void loginCanceled(EventObject source) {
			cancelled = true;
		}

		public void actionPerformed(ActionEvent ae) {
			Object source = ae.getSource();
			if (source == okButton) {
				startLogin();
			} else if (source == loginCancelButton) {
				cancelAuthentication();
			} else if (source == cancelButton) {
				dialog.dispose();
			}
		}
	}

	/**
	 * A convenience method to show a Login dialog
	 * 
	 * @param frame parent Frame
	 * @param name name
	 * @param password password
	 * @param service service
	 * @param store password store
	 * @param saveMode saveMode
	 */
	public static void showLoginDialog(Frame frame, String name,
			String password, LoginService service, PasswordStore store,
			int saveMode) {
		showLoginDialog(frame, name, password, service, store, null, null,
				saveMode);
	}

	/**
	 * A convenience method to show a Login dialog
	 * 
	 * @param frame parent Frame
	 * @param name name
	 * @param password password
	 * @param service service
	 * @param store password store
	 * @param header header component
	 * @param label label component
	 * @param saveMode saveMode
	 */
	public static void showLoginDialog(Frame frame, String name,
			String password, LoginService service, PasswordStore store,
			JComponent header, JComponent label, int saveMode) {
		JXLoginPanel loginPanel = new JXLoginPanel(name, password, service,
				store, header, label);
		loginPanel.setSaveMode(saveMode);
		DialogHelper helper = new DialogHelper(frame, loginPanel, service);
		helper.show();
	}

	/**
	 * Returns the name to use for the resource bundle for this module. The
	 * default implementation returns the
	 * <code>getClass().getName().resources.Resources</code>, subclasses
	 * wanting different behavior should override appropriately.
	 * 
	 * @return Name for resource bundle
	 */

	
	static String getResourceBundleName() {
		Package package1 = JXLoginPanel.class.getPackage();
		return package1 == null ? "resources.Resources" : package1.getName()
				+ ".resources.Resources";
	}

	static String getResourceAsString(String key) {
		Object value = getResourceAsObject(key);
		return value.toString();
	}

	static int getResourceAsInt(String key) throws NumberFormatException {
		Object value = getResourceAsObject(key);
		if (value instanceof Integer) {
			return ((Integer) value).intValue();
		}
		return Integer.parseInt(value.toString());
	}

	static Object getResourceAsObject(String key)
			throws MissingResourceException {
		synchronized (JXLoginPanel.class) {
			if (_resources == null) {
				_resources = new HashMap<String, Object>();
				ResourceBundle bundle = ResourceBundle
						.getBundle(getResourceBundleName());
				Enumeration bundleKeys = bundle.getKeys();
				while (bundleKeys.hasMoreElements()) {
					String resourceKey = (String) bundleKeys.nextElement();
					_resources.put(resourceKey, bundle.getObject(resourceKey));
				}
			}
		}
		return _resources.get(key);
	}

	/**
	 * @return Returns the saveMode.
	 */
	public int getSaveMode() {
		return saveMode;
	}

	/**
	 * @param saveMode The saveMode to set.
	 */
	public void setSaveMode(int saveMode) {
		this.saveMode = saveMode;
		savePanel.removeAll();
		String className = JXLoginPanel.class.getCanonicalName();
		if (saveMode == SAVE_PASSWORD) {
			save = new JCheckBox(UIManager.getString(className + "." + "rememberPasswordString"),
					passwordStore != null ? true : false);
			savePanel.add(save);
		} else {
			save = new JCheckBox(UIManager.getString(className + "." +"rememberUserString"),
					passwordStore != null ? true : false);
			savePanel.add(save);
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
		nameField.setSelectedItem(username);
	}

	/**
	 * Gets the <strong>User name</strong> for this panel.
	 *
	 * @param username User name
	 */
	public String getUserName() {
		if (username == null) {
			username = (String) nameField.getSelectedItem();
		}
		return username;
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

}