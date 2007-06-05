/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.auth.DefaultUserNameStore;
import org.jdesktop.swingx.auth.LoginAdapter;
import org.jdesktop.swingx.auth.LoginEvent;
import org.jdesktop.swingx.auth.LoginListener;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.auth.PasswordStore;
import org.jdesktop.swingx.auth.UserNameStore;
import org.jdesktop.swingx.plaf.JXLoginPanelAddon;
import org.jdesktop.swingx.plaf.LoginPanelUI;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
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
 * @author Shai Almog
 * @author rbair
 * @author Karl Schaefer
 */

public class JXLoginPanel extends JXImagePanel {
    /**
     * The Logger
     */
    private static final Logger LOG = Logger.getLogger(JXLoginPanel.class.getName());
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3544949969896288564L;
    /**
     * UI Class ID
     */
    public final static String uiClassID = "LoginPanelUI";
    /**
     * Action key for an Action in the ActionMap that initiates the Login
     * procedure
     */
    public static final String LOGIN_ACTION_COMMAND = "login";
    /** 
     * Action key for an Action in the ActionMap that cancels the Login
     * procedure
     */
    public static final String CANCEL_LOGIN_ACTION_COMMAND = "cancel-login";
    /**
     * The JXLoginPanel can attempt to save certain user information such as
     * the username, password, or both to their respective stores.
     * This type specifies what type of save should be performed.
     */
    public static enum SaveMode {NONE, USER_NAME, PASSWORD, BOTH}
    /**
     * Returns the status of the login process
     */
    public enum Status {NOT_STARTED, IN_PROGRESS, FAILED, CANCELLED, SUCCEEDED}
    /**
     * Used as a prefix when pulling data out of UIManager for i18n
     */
    private static String CLASS_NAME;

    /**
     * The current login status for this panel
     */
    private Status status = Status.NOT_STARTED;
    /**
     * An optional banner at the top of the panel
     */
    private JXImagePanel banner;
    /**
     * Text that should appear on the banner
     */
    private String bannerText;
    /**
     * Custom label allowing the developer to display some message to the user
     */
    private JLabel messageLabel;
    /**
     * Shows an error message such as "user name or password incorrect" or
     * "could not contact server" or something like that if something
     * goes wrong
     */
    private JLabel errorMessageLabel;
    /**
     * A Panel containing all of the input fields, check boxes, etc necessary
     * for the user to do their job. The items on this panel change whenever
     * the SaveMode changes, so this panel must be recreated at runtime if the
     * SaveMode changes. Thus, I must maintain this reference so I can remove
     * this panel from the content panel at runtime.
     */
    private JXPanel loginPanel;
    /**
     * The panel on which the input fields, messageLabel, and errorMessageLabel
     * are placed. While the login thread is running, this panel is removed
     * from the dialog and replaced by the progressPanel
     */
    private JXPanel contentPanel;
    /**
     * This is the area in which the name field is placed. That way it can toggle on the fly
     * between text field and a combo box depending on the situation, and have a simple
     * way to get the user name
     */
    private NameComponent namePanel;
    /**
     * The password field presented allowing the user to enter their password
     */
    private JPasswordField passwordField;
    /**
     * A combo box presenting the user with a list of servers to which they
     * may log in. This is an optional feature, which is only enabled if
     * the List of servers supplied to the JXLoginPanel has a length greater
     * than 1.
     */
    private JComboBox serverCombo;
    /**
     * Check box presented if a PasswordStore is used, allowing the user to decide whether to
     * save their password
     */
    private JCheckBox saveCB;
    /**
     * A special panel that displays a progress bar and cancel button, and
     * which notify the user of the login process, and allow them to cancel
     * that process.
     */
    private JXPanel progressPanel;
    /**
     * A JLabel on the progressPanel that is used for informing the user
     * of the status of the login procedure (logging in..., cancelling login...)
     */
    private JLabel progressMessageLabel;
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
     * A list of servers where each server is represented by a String. If the
     * list of Servers is greater than 1, then a combo box will be presented to
     * the user to choose from. If any servers are specified, the selected one
     * (or the only one if servers.size() == 1) will be passed to the LoginService
     */
    private List<String> servers;
    /**
     *  Whether to save password or username or both
     */
    private SaveMode saveMode;
    /**
     * Tracks the cursor at the time that authentication was started, and restores to that
     * cursor after authentication ends, or is cancelled;
     */
    private Cursor oldCursor;
    
    /**
     * The default login listener used by this panel.
     */
    private LoginListener defaultLoginListener;
    
    /**
     * Creates a default JXLoginPanel instance
     */
    static {
        LookAndFeelAddons.contribute(new JXLoginPanelAddon());
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
    
    //--------------------------------------------------------- Constructors
    /**
     * Create a {@code JXLoginPanel} that always accepts the user, never stores
     * passwords or user ids, and has no target servers.
     * <p>
     * This constructor should <i>NOT</i> be used in a real application. It is
     * provided for compliance to the bean specification and for use with visual
     * editors.
     */
    public JXLoginPanel() {
        this(null);
    }
    
    /**
     * Create a {@code JXLoginPanel} with the specified {@code LoginService}
     * that does not store user ids or passwords and has no target servers.
     * 
     * @param service
     *            the {@code LoginService} to use for logging in
     */
    public JXLoginPanel(LoginService service) {
        this(service, null, null);
    }
    
    /**
     * Create a {@code JXLoginPanel} with the specified {@code LoginService},
     * {@code PasswordStore}, and {@code UserNameStore}, but without a server
     * list.
     * <p>
     * If you do not want to store passwords or user ids, those parameters can
     * be {@code null}. {@code SaveMode} is autoconfigured from passed in store
     * parameters.
     * 
     * @param service
     *            the {@code LoginService} to use for logging in
     * @param passwordStore
     *            the {@code PasswordStore} to use for storing password
     *            information
     * @param userStore
     *            the {@code UserNameStore} to use for storing user information
     */
    public JXLoginPanel(LoginService service, PasswordStore passwordStore, UserNameStore userStore) {
        this(service, passwordStore, userStore, null);
    }
    
    /**
     * Create a {@code JXLoginPanel} with the specified {@code LoginService},
     * {@code PasswordStore}, {@code UserNameStore}, and server list.
     * <p>
     * If you do not want to store passwords or user ids, those parameters can
     * be {@code null}. {@code SaveMode} is autoconfigured from passed in store
     * parameters.
     * <p>
     * Setting the server list to {@code null} will unset all of the servers.
     * The server list is guaranteed to be non-{@code null}.
     * 
     * @param service
     *            the {@code LoginService} to use for logging in
     * @param passwordStore
     *            the {@code PasswordStore} to use for storing password
     *            information
     * @param userStore
     *            the {@code UserNameStore} to use for storing user information
     * @param servers
     *            a list of servers to authenticate against
     */
    public JXLoginPanel(LoginService service, PasswordStore passwordStore, UserNameStore userStore, List<String> servers) {
        setLoginService(service);
        setPasswordStore(passwordStore);
        setUserNameStore(userStore);
        setServers(servers);
        
        //create the login and cancel actions, and add them to the action map
        getActionMap().put(LOGIN_ACTION_COMMAND, createLoginAction());
        getActionMap().put(CANCEL_LOGIN_ACTION_COMMAND, createCancelAction());
        
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

        updateUI();
        
        // initialize banner text
        banner = new JXImagePanel();
        setBannerText(UIManager.getString(CLASS_NAME + ".bannerString"));
        
        initComponents();
    }

    //------------------------------------------------------------- UI Logic
    
    /**
     * {@inheritDoc}
     */
    public LoginPanelUI getUI() {
        return (LoginPanelUI)super.getUI();
    }

    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return the string {@link #uiClassID}
     * @see javax.swing.JComponent#getUIClassID
     * @see javax.swing.UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Recreates the login panel, and replaces the current one with the new one
     */
    protected void recreateLoginPanel() {
        contentPanel.remove(loginPanel);
        loginPanel = createLoginPanel();
        loginPanel.setBorder(BorderFactory.createEmptyBorder(0, 36, 7, 11));
        contentPanel.add(loginPanel, 1);
    }
    
    /**
     * Creates and returns a new LoginPanel, based on the SaveMode state of
     * the login panel. Whenever the SaveMode changes, the panel is recreated.
     * I do this rather than hiding/showing components, due to a cleaner
     * implementation (no invisible components, components are not sharing
     * locations in the LayoutManager, etc).
     */
    private JXPanel createLoginPanel() {
        JXPanel loginPanel = new JXPanel();
        
        //create the NameComponent
        if (saveMode == SaveMode.NONE) {
            namePanel = new SimpleNamePanel();
        } else {
            namePanel = new ComboNamePanel(userNameStore);
        }
        JLabel nameLabel = new JLabel(UIManager.getString(CLASS_NAME + ".nameString"));
        nameLabel.setLabelFor(namePanel.getComponent());
        
        //create the password component
        passwordField = new JPasswordField("", 15);
        JLabel passwordLabel = new JLabel(UIManager.getString(CLASS_NAME + ".passwordString"));
        passwordLabel.setLabelFor(passwordField);
        
        //create the server combo box if necessary
        JLabel serverLabel = new JLabel(UIManager.getString(CLASS_NAME + ".serverString"));
        if (servers.size() > 1) {
            serverCombo = new JComboBox(servers.toArray());
            serverLabel.setLabelFor(serverCombo);
        } else {
            serverCombo = null;
        }
        
        //create the save check box. By default, it is not selected
        saveCB = new JCheckBox(UIManager.getString(CLASS_NAME + ".rememberPasswordString"));
        saveCB.setSelected(false); //TODO should get this from prefs!!! And, it should be based on the user
        //determine whether to show/hide the save check box based on the SaveMode
        saveCB.setVisible(saveMode == SaveMode.PASSWORD || saveMode == SaveMode.BOTH);
        
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(0, 0, 5, 11);
        loginPanel.add(nameLabel, gridBagConstraints);
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        loginPanel.add(namePanel.getComponent(), gridBagConstraints);
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(0, 0, 5, 11);
        loginPanel.add(passwordLabel, gridBagConstraints);
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        loginPanel.add(passwordField, gridBagConstraints);
        
        if (serverCombo != null) {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.anchor = GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new Insets(0, 0, 5, 11);
            loginPanel.add(serverLabel, gridBagConstraints);

            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.anchor = GridBagConstraints.LINE_START;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(0, 0, 5, 0);
            loginPanel.add(serverCombo, gridBagConstraints);

            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(6, 0, 0, 0);
            loginPanel.add(saveCB, gridBagConstraints);
        } else {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(6, 0, 0, 0);
            loginPanel.add(saveCB, gridBagConstraints);
        }
        return loginPanel;
    }
    
    /**
     * This method adds functionality to support bidi languages within this 
     * component
     */
    public void setComponentOrientation(ComponentOrientation orient) {
        // this if is used to avoid needless creations of the image
        if(orient != super.getComponentOrientation()) {
            super.setComponentOrientation(orient);
            banner.setImage(createLoginBanner());
            progressPanel.applyComponentOrientation(orient);
        }
    }
    
    /**
     * Create all of the UI components for the login panel
     */
    private void initComponents() {
        //create the default banner
        banner.setImage(createLoginBanner());

        //create the default label
        messageLabel = new JLabel(" ");
        messageLabel.setOpaque(true);
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.BOLD));

        //create the main components
        loginPanel = createLoginPanel();
        
        //create the message and hyperlink and hide them
        errorMessageLabel = new JLabel(UIManager.getString(CLASS_NAME + ".errorMessage")); 
        errorMessageLabel.setIcon(UIManager.getIcon("JXLoginDialog.error.icon"));
        errorMessageLabel.setVerticalTextPosition(SwingConstants.TOP);
        errorMessageLabel.setOpaque(true);
        errorMessageLabel.setBackground(new Color(255, 215, 215));//TODO get from UIManager
        errorMessageLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(errorMessageLabel.getBackground().darker()),
                BorderFactory.createEmptyBorder(5, 7, 5, 5))); //TODO get color from UIManager
        errorMessageLabel.setVisible(false);
        
        //aggregate the optional message label, content, and error label into
        //the contentPanel
        contentPanel = new JXPanel(new VerticalLayout());
        messageLabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 7, 11));
        contentPanel.add(messageLabel);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(0, 36, 7, 11));
        contentPanel.add(loginPanel);
        errorMessageLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 36, 0, 11, contentPanel.getBackground()),
                errorMessageLabel.getBorder()));
        contentPanel.add(errorMessageLabel);
        
        //create the progress panel
        progressPanel = new JXPanel(new GridBagLayout());
        progressMessageLabel = new JLabel(UIManager.getString(CLASS_NAME + ".pleaseWait"));
        progressMessageLabel.setFont(progressMessageLabel.getFont().deriveFont(Font.BOLD)); //TODO get from UIManager
        JProgressBar pb = new JProgressBar();
        pb.setIndeterminate(true);
        JButton cancelButton = new JButton(getActionMap().get(CANCEL_LOGIN_ACTION_COMMAND));
        progressPanel.add(progressMessageLabel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(12, 12, 11, 11), 0, 0));
        progressPanel.add(pb, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 24, 11, 7), 0, 0));
        progressPanel.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 11, 11), 0, 0));
        
        //layout the panel
        setLayout(new BorderLayout());
        add(banner, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Create and return an image to use for the Banner. This may be overridden
     * to return any image you like
     */
    protected Image createLoginBanner() {
        return getUI() == null ? null : getUI().getBanner();
    }
    
    /**
     * Create and return an Action for logging in
     */
    protected Action createLoginAction() {
	return new LoginAction(this);
    }
    
    /**
     * Create and return an Action for canceling login
     */
    protected Action createCancelAction() {
	return new CancelAction(this);
    }
    
    //------------------------------------------------------ Bean Properties
    //TODO need to fire property change events!!!
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
        if (this.saveMode != saveMode) {
            SaveMode oldMode = getSaveMode();
            this.saveMode = saveMode;
            recreateLoginPanel();
            firePropertyChange("saveMode", oldMode, getSaveMode());
        }
    }
    
    /**
     * @return the List of servers
     */
    public List<String> getServers() {
        return Collections.unmodifiableList(servers);
    }
    
    /**
     * Sets the list of servers. See the servers field javadoc for more info
     */
    public void setServers(List<String> servers) {
        //only at startup
        if (this.servers == null) {
            this.servers = servers == null ? new ArrayList<String>() : servers;
        } else if (this.servers != servers) {
            List<String> old = getServers();
            this.servers = servers == null ? new ArrayList<String>() : servers;
            recreateLoginPanel();
            firePropertyChange("servers", old, getServers());
        }
    }
    
    private LoginListener getDefaultLoginListener() {
        if (defaultLoginListener == null) {
            defaultLoginListener = new LoginListenerImpl();
        }
        
        return defaultLoginListener;
    }
    
    /**
     * Sets the {@code LoginService} for this panel. Setting the login service
     * to {@code null} will actually set the service to use
     * {@code NullLoginService}.
     * 
     * @param service
     *            the service to set. If {@code service == null}, then a
     *            {@code NullLoginService} is used.
     */
    public void setLoginService(LoginService service) {
        LoginService oldService = getLoginService();
        LoginService newService = service == null ? new NullLoginService() : service;
        
        //newService is guaranteed to be nonnull
        if (!newService.equals(oldService)) {
            if (oldService != null) {
                oldService.removeLoginListener(getDefaultLoginListener());
            }
            
            loginService = newService;
            this.loginService.addLoginListener(getDefaultLoginListener());
            
            firePropertyChange("loginService", oldService, getLoginService());
        }
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
        PasswordStore oldStore = getPasswordStore();
        PasswordStore newStore = store == null ? new NullPasswordStore() : store;
        
        //newStore is guaranteed to be nonnull
        if (!newStore.equals(oldStore)) {
            passwordStore = newStore;
            
            firePropertyChange("passwordStore", oldStore, getPasswordStore());
        }
    }
    
    /**
     * Gets the {@code UserNameStore} for this panel.
     * 
     * @return the {@code UserNameStore}
     */
    public UserNameStore getUserNameStore() {
        return userNameStore;
    }

    /**
     * Sets the user name store for this panel.
     * @param store
     */
    public void setUserNameStore(UserNameStore store) {
        UserNameStore oldStore = getUserNameStore();
        UserNameStore newStore = store == null ? new DefaultUserNameStore() : store;
        
        //newStore is guaranteed to be nonnull
        if (!newStore.equals(oldStore)) {
            userNameStore = newStore;
            
            firePropertyChange("userNameStore", oldStore, getUserNameStore());
        }
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
        if (namePanel != null) {
            namePanel.setUserName(username);
        }
    }
    
    /**
     * Gets the <strong>User name</strong> for this panel.
     * @return the user name
     */
    public String getUserName() {
        return namePanel == null ? null : namePanel.getUserName();
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
     * Return the image used as the banner
     */
    public Image getBanner() {
        return banner.getImage();
    }
    
    /**
     * Set the image to use for the banner. If the {@code img} is {@code null},
     * then no image will be displayed.
     * 
     * @param img
     *            the image to display
     */
    public void setBanner(Image img) {
        // we do not expose the ImagePanel, so we will produce property change
        // events here
        Image oldImage = getBanner();
        
        if (oldImage != img) {
            banner.setImage(img);
            firePropertyChange("banner", oldImage, getBanner());
        }
    }
    
    /**
     * Set the text to use when creating the banner. If a custom banner image is
     * specified, then this is ignored. If {@code text} is {@code null}, then
     * no text is displayed.
     * 
     * @param text
     *            the text to display
     */
    public void setBannerText(String text) {
        if (text == null) {
            text = "";
        }

        if (!text.equals(this.bannerText)) {
            String oldText = this.bannerText;
            this.bannerText = text;
            //fix the login banner
            banner.setImage(createLoginBanner());
            firePropertyChange("bannerText", oldText, text);
        }
    }

    /**
     * Returns text used when creating the banner
     */
    public String getBannerText() {
        return bannerText;
    }

    /**
     * Returns the custom message for this login panel
     */
    public String getMessage() {
        return messageLabel.getText();
    }
    
    /**
     * Sets a custom message for this login panel
     */
    public void setMessage(String message) {
        messageLabel.setText(message);
    }
    
    /**
     * Returns the error message for this login panel
     */
    public String getErrorMessage() {
        return errorMessageLabel.getText();
    }
    
    /**
     * Sets the error message for this login panel
     */
    public void setErrorMessage(String errorMessage) {
        errorMessageLabel.setText(errorMessage);
    }
    
    /**
     * Returns the panel's status
     */
    public Status getStatus() {
        return status;
    }
    
    /**
     * Change the status
     */
    protected void setStatus(Status newStatus) {
	if (status != newStatus) {
	    Status oldStatus = status;
	    status = newStatus;
	    firePropertyChange("status", oldStatus, newStatus);
	}
    }
    //-------------------------------------------------------------- Methods
    
    /**
     * Initiates the login procedure. This method is called internally by
     * the LoginAction. This method handles cursor management, and actually
     * calling the LoginService's startAuthentication method.
     */
    protected void startLogin() {
        oldCursor = getCursor();
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            progressMessageLabel.setText(UIManager.getString(CLASS_NAME + ".pleaseWait"));
            String name = getUserName();
            char[] password = getPassword();
            String server = servers.size() == 1 ? servers.get(0) : serverCombo == null ? null : (String)serverCombo.getSelectedItem();
            loginService.startAuthentication(name, password, server);
        } catch(Exception ex) {
	    //The status is set via the loginService listener, so no need to set
	    //the status here. Just log the error.
	    LOG.log(Level.WARNING, "Authentication exception while logging in", ex);
        } finally {
            setCursor(oldCursor);
        }
    }
    
    /**
     * Cancels the login procedure. Handles cursor management and interfacing
     * with the LoginService's cancelAuthentication method
     */
    protected void cancelLogin() {
        progressMessageLabel.setText(UIManager.getString(CLASS_NAME + ".cancelWait"));
        getActionMap().get(CANCEL_LOGIN_ACTION_COMMAND).setEnabled(false);
        loginService.cancelAuthentication();
        setCursor(oldCursor);
    }
    
    /**
     * TODO
     */
    protected void savePassword() {
        if (saveCB.isSelected() 
            && (saveMode == SaveMode.BOTH || saveMode == SaveMode.PASSWORD)
            && passwordStore != null) {
            passwordStore.set(getUserName(),getLoginService().getServer(),getPassword());
        }
    }
    
    //--------------------------------------------- Listener Implementations
    /*
     
     For Login (initiated in LoginAction):
        0) set the status
        1) Immediately disable the login action
        2) Immediately disable the close action (part of enclosing window)
        3) initialize the progress pane
          a) enable the cancel login action
          b) set the message text
        4) hide the content pane, show the progress pane
     
     When cancelling (initiated in CancelAction):
         0) set the status
         1) Disable the cancel login action
         2) Change the message text on the progress pane
     
     When cancel finishes (handled in LoginListener):
         0) set the status
         1) hide the progress pane, show the content pane
         2) enable the close action (part of enclosing window)
         3) enable the login action
     
     When login fails (handled in LoginListener):
         0) set the status
         1) hide the progress pane, show the content pane
         2) enable the close action (part of enclosing window)
         3) enable the login action
         4) Show the error message
         5) resize the window (part of enclosing window)
     
     When login succeeds (handled in LoginListener):
         0) set the status
         1) close the dialog/frame (part of enclosing window)
     */
    /**
     * Listener class to track state in the LoginService
     */
    protected class LoginListenerImpl extends LoginAdapter {
        public void loginSucceeded(LoginEvent source) {
            //save the user names and passwords
            String userName = namePanel.getUserName();
            savePassword();
            if (getSaveMode() == SaveMode.USER_NAME
                    && userName != null && !userName.trim().equals("")) {
                userNameStore.addUserName(userName);
                userNameStore.saveUserNames();
            }
            setStatus(Status.SUCCEEDED);
        }
            
        public void loginStarted(LoginEvent source) {
	    getActionMap().get(LOGIN_ACTION_COMMAND).setEnabled(false);
            getActionMap().get(CANCEL_LOGIN_ACTION_COMMAND).setEnabled(true);
            remove(contentPanel);
            add(progressPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
            setStatus(Status.IN_PROGRESS);
        }

        public void loginFailed(LoginEvent source) {
            remove(progressPanel);
            add(contentPanel, BorderLayout.CENTER);
	    getActionMap().get(LOGIN_ACTION_COMMAND).setEnabled(true);
            errorMessageLabel.setVisible(true);
            revalidate();
            repaint();
            setStatus(Status.FAILED);
        }

        public void loginCanceled(LoginEvent source) {
            remove(progressPanel);
            add(contentPanel, BorderLayout.CENTER);
            getActionMap().get(LOGIN_ACTION_COMMAND).setEnabled(true);
            errorMessageLabel.setVisible(false);
            revalidate();
            repaint();
            setStatus(Status.CANCELLED);
        }
    }
    
    //---------------------------------------------- Default Implementations
    /**
     * Action that initiates a login procedure. Delegates to JXLoginPanel.startLogin
     */
    private static final class LoginAction extends AbstractActionExt {
	private JXLoginPanel panel;
	public LoginAction(JXLoginPanel p) {
	    super(UIManager.getString(CLASS_NAME + ".loginString"), LOGIN_ACTION_COMMAND); 
	    this.panel = p;
	}
	public void actionPerformed(ActionEvent e) {
	    panel.startLogin();
	}
	public void itemStateChanged(ItemEvent e) {}
    }
    
    /**
     * Action that cancels the login procedure. 
     */
    private static final class CancelAction extends AbstractActionExt {
	private JXLoginPanel panel;
	public CancelAction(JXLoginPanel p) {
	    super(UIManager.getString(CLASS_NAME + ".cancelLogin"), CANCEL_LOGIN_ACTION_COMMAND); 
	    this.panel = p;
	    this.setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
	    panel.cancelLogin();
	}
	public void itemStateChanged(ItemEvent e) {}
    }
    
    /**
     * Simple login service that allows everybody to login. This is useful in demos and allows
     * us to avoid having to check for LoginService being null
     */
    private static final class NullLoginService extends LoginService {
        public boolean authenticate(String name, char[] password, String server) throws Exception {
            return true;
        }

        public boolean equals(Object obj) {
            return obj instanceof NullLoginService;
        }

        public int hashCode() {
            return 7;
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

        public boolean equals(Object obj) {
            return obj instanceof NullPasswordStore;
        }

        public int hashCode() {
            return 7;
        }
    }
    
    //--------------------------------- Default NamePanel Implementations
    public static interface NameComponent {
        public String getUserName();
        public void setUserName(String userName);
        public JComponent getComponent();
    }
    
    /**
     * If a UserNameStore is not used, then this text field is presented allowing the user
     * to simply enter their user name
     */
    public static final class SimpleNamePanel extends JTextField implements NameComponent {
        public SimpleNamePanel() {
            super("", 15);
        }
        public String getUserName() {
            return getText();
        }
        public void setUserName(String userName) {
            setText(userName);
        }
        public JComponent getComponent() {
            return this;
        }
    }
    
    /**
     * If a UserNameStore is used, then this combo box is presented allowing the user
     * to select a previous login name, or type in a new login name
     */
    public static final class ComboNamePanel extends JComboBox implements NameComponent {
        private UserNameStore userNameStore;
        public ComboNamePanel(UserNameStore userNameStore) {
            super();
            this.userNameStore = userNameStore;
            setModel(new NameComboBoxModel());
            setEditable(true);
        }
        public String getUserName() {
            Object item = getModel().getSelectedItem();
            return item == null ? null : item.toString();
        }
        public void setUserName(String userName) {
            getModel().setSelectedItem(userName);
        }
        public void setUserNames(String[] names) {
            setModel(new DefaultComboBoxModel(names));
        }
        public JComponent getComponent() {
            return this;
        }
        private final class NameComboBoxModel extends AbstractListModel implements ComboBoxModel {
            private Object selectedItem;
            public void setSelectedItem(Object anItem) {
                selectedItem = anItem;
                fireContentsChanged(this, -1, -1);
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
        }
    }

    //------------------------------------------ Static Construction Methods
    /**
     * Shows a login dialog. This method blocks.
     * @return The status of the login operation
     */
    public static Status showLoginDialog(Component parent, LoginService svc) {
        return showLoginDialog(parent, svc, null, null);
    }

    /**
     * Shows a login dialog. This method blocks.
     * @return The status of the login operation
     */
    public static Status showLoginDialog(Component parent, LoginService svc, PasswordStore ps, UserNameStore us) {
        return showLoginDialog(parent, svc, ps, us, null);
    }
    
    /**
     * Shows a login dialog. This method blocks.
     * @return The status of the login operation
     */
    public static Status showLoginDialog(Component parent, LoginService svc, PasswordStore ps, UserNameStore us, List<String> servers) {
        JXLoginPanel panel = new JXLoginPanel(svc, ps, us, servers);
        return showLoginDialog(parent, panel);
    }
    
    /**
     * Shows a login dialog. This method blocks.
     * @return The status of the login operation
     */
    public static Status showLoginDialog(Component parent, JXLoginPanel panel) {
        Window w = WindowUtils.findWindow(parent);
        JXLoginDialog dlg =  null;
        if (w == null) {
            dlg = new JXLoginDialog((Frame)null, panel);
        } else if (w instanceof Dialog) {
            dlg = new JXLoginDialog((Dialog)w, panel);
        } else if (w instanceof Frame) {
            dlg = new JXLoginDialog((Frame)w, panel);
        } else {
            throw new AssertionError("Shouldn't be able to happen");
        }
        dlg.setVisible(true);
        return dlg.getStatus();
    }
    
    /**
     * Shows a login frame. A JFrame is not modal, and thus does not block
     */
    public static JXLoginFrame showLoginFrame(LoginService svc) {
        return showLoginFrame(svc, null, null);
    }

    /**
     */
    public static JXLoginFrame showLoginFrame(LoginService svc, PasswordStore ps, UserNameStore us) {
        return showLoginFrame(svc, ps, us, null);
    }
    
    /**
     */
    public static JXLoginFrame showLoginFrame(LoginService svc, PasswordStore ps, UserNameStore us, List<String> servers) {
        JXLoginPanel panel = new JXLoginPanel(svc, ps, us, servers);
        return showLoginFrame(panel);
    }

    /**
     */
    public static JXLoginFrame showLoginFrame(JXLoginPanel panel) {
        return new JXLoginFrame(panel);
    }

    public static final class JXLoginDialog extends JDialog {
        private JXLoginPanel panel;
        
        public JXLoginDialog(Frame parent, JXLoginPanel p) {
            super(parent, true);
            init(p);
        }
        
        public JXLoginDialog(Dialog parent, JXLoginPanel p) {
            super(parent, true);
            init(p);
        }
        
	protected void init(JXLoginPanel p) {
	    setTitle(UIManager.getString(CLASS_NAME + ".titleString")); 
            this.panel = p;
            initWindow(this, panel);
	}
	
	public JXLoginPanel.Status getStatus() {
	    return panel.getStatus();
	}
    }
    
    public static final class JXLoginFrame extends JFrame {
	private JXLoginPanel panel;
	
	public JXLoginFrame(JXLoginPanel p) {
	    super(UIManager.getString(CLASS_NAME + ".titleString")); 
	    this.panel = p;
            initWindow(this, panel);
	}
	
	public JXLoginPanel.Status getStatus() {
	    return panel.getStatus();
	}
        
        public JXLoginPanel getPanel() {
            return panel;
        }
    }
    
    /**
     * Utility method for initializing a Window for displaying a LoginDialog.
     * This is particularly useful because the differences between JFrame and
     * JDialog are so minor.
     *
     * Note: This method is package private for use by JXLoginDialog (proper, 
     * not JXLoginPanel.JXLoginDialog). Change to private if JXLoginDialog is
     * removed.
     */
    static void initWindow(final Window w, final JXLoginPanel panel) {
        w.setLayout(new BorderLayout());
        w.add(panel, BorderLayout.CENTER);
        JButton okButton = new JButton(panel.getActionMap().get(LOGIN_ACTION_COMMAND));
        final JButton cancelButton = new JButton(UIManager.getString(CLASS_NAME + ".cancelString"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //change panel status to cancelled!
                panel.status = JXLoginPanel.Status.CANCELLED;
                w.setVisible(false);
                w.dispose();
            }
        });
        panel.addPropertyChangeListener("status", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JXLoginPanel.Status status = (JXLoginPanel.Status)evt.getNewValue();
                switch (status) {
                    case NOT_STARTED:
                        break;
                    case IN_PROGRESS:
                        cancelButton.setEnabled(false);
                        break;
                    case CANCELLED:
                        cancelButton.setEnabled(true);
                        w.pack();
                        break;
                    case FAILED:
                        cancelButton.setEnabled(true);
                        w.pack();
                        break;
                    case SUCCEEDED:
                        w.setVisible(false);
                        w.dispose();
                }
                for (PropertyChangeListener l : w.getPropertyChangeListeners("status")) {
                    PropertyChangeEvent pce = new PropertyChangeEvent(w, "status", evt.getOldValue(), evt.getNewValue());
                    l.propertyChange(pce);
                }
            }
        });
        cancelButton.setText(UIManager.getString(CLASS_NAME + ".cancelString"));
        int prefWidth = Math.max(cancelButton.getPreferredSize().width, okButton.getPreferredSize().width);
        cancelButton.setPreferredSize(new Dimension(prefWidth, okButton.getPreferredSize().height));
        okButton.setPreferredSize(new Dimension(prefWidth, okButton.getPreferredSize().height));
        JXPanel buttonPanel = new JXPanel(new GridBagLayout());
        buttonPanel.add(okButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(17, 12, 11, 5), 0, 0));
        buttonPanel.add(cancelButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(17, 0, 11, 11), 0, 0));
        w.add(buttonPanel, BorderLayout.SOUTH);            
        w.addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                panel.cancelLogin();
            }
        });

        if (w instanceof JFrame) {
            final JFrame f = (JFrame)w;
            f.getRootPane().setDefaultButton(okButton);
            f.setResizable(false);
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
            ActionListener closeAction = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    f.setVisible(false);
                    f.dispose();
                }
            };
            f.getRootPane().registerKeyboardAction(closeAction, ks, JComponent.WHEN_IN_FOCUSED_WINDOW);
        } else if (w instanceof JDialog) {
            final JDialog d = (JDialog)w;
            d.getRootPane().setDefaultButton(okButton);
            d.setResizable(false);
            KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
            ActionListener closeAction = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    d.setVisible(false);
                }
            };
            d.getRootPane().registerKeyboardAction(closeAction, ks, JComponent.WHEN_IN_FOCUSED_WINDOW);
        }
        w.pack();
        w.setLocation(WindowUtils.getPointForCentering(w));
    }
}
