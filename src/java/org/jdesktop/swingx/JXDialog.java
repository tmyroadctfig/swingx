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

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicOptionPaneUI;

import org.jdesktop.swingx.action.BoundAction;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;

/**
 * First cut for enhanced Dialog.
 * 
 * <ul>
 * <li> registers stand-in actions for close/execute with the dialog's RootPane
 * <li> registers keyStrokes for esc/enter to trigger the close/execute actions
 * <li> takes care of building the button panel using the close/execute actions.
 * <li> accepts a content and configures itself from content's properties - 
 *  replaces the execute action from the appropriate action in content's action map (if any)
 *  and set's its title from the content's name. 
 * </ul> 
 * 
 * 
 * PENDING: add support for vetoing the close.
 * PENDING: add complete set of constructors
 * PENDING: add windowListener to delegate to close action
 * 
 * @author Jeanette Winzenburg
 */
public class JXDialog extends JDialog {

    static {
        // Hack to enforce loading of SwingX framework ResourceBundle
        LookAndFeelAddons.getAddon();
    }
    
    public static final String EXECUTE_ACTION_COMMAND = "execute";
    public static final String CLOSE_ACTION_COMMAND = "close";
    public static final String UIPREFIX = "XDialog.";

    protected JComponent content;
    
    public JXDialog(Frame frame, JComponent content) {
        super(frame);
        setContent(content);
    }
    
    /**
     * @param dialog
     * @param sharedFindPanel
     */
    public JXDialog(Dialog dialog, JComponent content) {
        super(dialog);
        setContent(content);
    }

    /**
     * @param panel
     */
    public JXDialog(JComponent content) {
        super();
        setContent(content);
    }

    private void setContent(JComponent content) {
        if (this.content != null) {
            throw new IllegalStateException("content must not be set more than once");
        }
        initActions();
        Action contentCloseAction = content.getActionMap().get(CLOSE_ACTION_COMMAND);
        if (contentCloseAction != null) {
            putAction(CLOSE_ACTION_COMMAND, contentCloseAction);
        }
        Action contentExecuteAction = content.getActionMap().get(EXECUTE_ACTION_COMMAND);
        if (contentExecuteAction != null) {
            putAction(EXECUTE_ACTION_COMMAND, contentExecuteAction);
        }
        this.content = content;
        build();
        setTitle(content.getName());
    }

    /**
     * pre: content != null.
     *
     */
    private void build() {
        JComponent contentBox = new Box(BoxLayout.PAGE_AXIS); 
        contentBox.add(content);
        JComponent buttonPanel = createButtonPanel();
        contentBox.add(buttonPanel);
        contentBox.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
//        content.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
//        fieldPanel.setAlignmentX();
//      buttonPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        add(contentBox);
        
    }

//    /**
//     * 
//     */
//    private void locate() {
//        GraphicsConfiguration gc =
//            GraphicsEnvironment.getLocalGraphicsEnvironment().
//            getDefaultScreenDevice().getDefaultConfiguration();
//        Rectangle bounds = gc.getBounds();
//        int x = bounds.x+bounds.width/3;
//        int y = bounds.y+bounds.height/3;
//
//        setLocation(x, y);
//    }

    public void setVisible(boolean visible) {
        if (content == null) throw 
            new IllegalStateException("content must be built before showing the dialog");
        super.setVisible(visible);
    }

    public void doClose() {
        dispose();
    }
    
    private void initActions() {
        // PENDING: factor a common dialog containing the following
        Action defaultAction = createCloseAction();
        putAction(CLOSE_ACTION_COMMAND, defaultAction);
        putAction(EXECUTE_ACTION_COMMAND, defaultAction);
    }

    private Action createCloseAction() {
        String actionName = getUIString(CLOSE_ACTION_COMMAND);
        BoundAction action = new BoundAction(actionName,
                CLOSE_ACTION_COMMAND);
        action.registerCallback(this, "doClose");
        return action;
    }

    /**
     * create the dialog button controls.
     * 
     * 
     * @return panel containing button controls
     */
    protected JComponent createButtonPanel() {
        // PENDING: this is a hack until we have a dedicated ButtonPanel!
        JPanel panel = new JPanel(new BasicOptionPaneUI.ButtonAreaLayout(true, 6))
        {
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };

        panel.setBorder(BorderFactory.createEmptyBorder(9, 0, 0, 0));
        Action findAction = getAction(EXECUTE_ACTION_COMMAND);
        Action closeAction = getAction(CLOSE_ACTION_COMMAND);

        JButton findButton = new JButton(findAction);
        panel.add(findButton);
        if (findAction != closeAction) {
            panel.add(new JButton(closeAction));
        }


        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);

        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(enterKey, EXECUTE_ACTION_COMMAND);
        inputMap.put(escapeKey, CLOSE_ACTION_COMMAND);

        getRootPane().setDefaultButton(findButton);
        return panel;
    }

    /**
     * convenience wrapper to access rootPane's actionMap.
     * @param key
     * @param action
     */
    private void putAction(Object key, Action action) {
        getRootPane().getActionMap().put(key, action);
    }
    
    /**
     * convenience wrapper to access rootPane's actionMap.
     * 
     * @param key
     * @return root pane's <code>ActionMap</code>
     */
    private Action getAction(Object key) {
        return getRootPane().getActionMap().get(key);
    }
    /**
     * tries to find a String value from the UIManager, prefixing the
     * given key with the UIPREFIX. 
     * 
     * TODO: move to utilities?
     * 
     * @param key 
     * @return the String as returned by the UIManager or key if the returned
     *   value was null.
     */
    private String getUIString(String key) {
        String text = UIManager.getString(UIPREFIX + key);
        return text != null ? text : key;
    }


}
