/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.action.ActionContainerFactory;
import org.jdesktop.swingx.action.BoundAction;

/**
 * Simple FindDialog.
 * 
 * 
 * PENDING: need to extract a common dialog.
 * PENDING: the base search widget need not be a dialog!
 * 
 * @author ??
 * @author Jeanette Winzenburg
 */
public class JXFindDialog extends JDialog {

    public static final String MATCH_WRAP_ACTION_COMMAND = "wrapSearch";
    public static final String MATCH_BACKWARDS_ACTION_COMMAND = "backwardsSearch";
    public static final String EXECUTE_FIND_ACTION_COMMAND = "executeSearch";
    public static final String CLOSE_ACTION_COMMAND = "close";
//    private static final Object ENTER_ACTION_COMMAND = null;
//    private static final Object CANCEL_ACTION_COMMAND = null;
    
    private Searchable searchable;

    private JTextField searchField;
    private JCheckBox matchCheck;
    private JCheckBox wrapCheck;
    private JCheckBox backCheck;


    private PatternModel patternModel;

    public JXFindDialog() {
        this(null, null);
    }
    
    public JXFindDialog(Searchable searchable) {
        this(searchable, 
            (searchable instanceof Component) ? (Component) searchable : null);
     }
    public JXFindDialog(Searchable searchable, Component component) {
        super(component != null ? 
              (Frame)SwingUtilities.getWindowAncestor(component) : JOptionPane.getRootFrame(),
              "Find in this component");
        setSearchable(searchable);

        locate();

        init();
        pack();
    }
    
    /**
     * Sets the Searchable targeted with this dialog.
     * 
     * @param searchable 
     */
    public void setSearchable(Searchable searchable) {
        if ((this.searchable != null) && this.searchable.equals(searchable)) return;
        Object old = this.searchable;
        this.searchable = searchable;
        setLastIndex(-1);
        firePropertyChange("searchable", old, this.searchable);
    }
    
    /**
     * 
     */
    private void locate() {
        GraphicsConfiguration gc =
            GraphicsEnvironment.getLocalGraphicsEnvironment().
            getDefaultScreenDevice().getDefaultConfiguration();
        Rectangle bounds = gc.getBounds();
        int x = bounds.x+bounds.width/3;
        int y = bounds.y+bounds.height/3;

        setLocation(x, y);
    }

    private void init() {
        initActions();
        initComponents();
        build();
        bind();

    }
    //------------------ support synch the model <--> components
    
    /**
     * 
     */
    private PatternModel getPatternModel() {
        if (patternModel == null) {
            patternModel = new PatternModel();
            patternModel.addPropertyChangeListener(getPatternModelListener());
        }
        return patternModel;
    }

    /**
     * creates and returns a PropertyChangeListener to the PatternModel.
     * 
     * NOTE: the patternModel is totally under control of this class - currently
     * there's no need to keep a reference to the listener.
     * 
     * @return
     */
    private PropertyChangeListener getPatternModelListener() {
        PropertyChangeListener l = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ("pattern".equals(evt.getPropertyName())) {
                    refreshPatternMatchersFromModel();
                }

            }

        };
        return l;
    }

    /**
     * callback method from listening to PatternModel.
     *
     */
    protected void refreshPatternMatchersFromModel() {
    }

    private DocumentListener getSearchFieldListener() {
        DocumentListener l = new DocumentListener() {
            public void changedUpdate(DocumentEvent ev) {
                // JW - really?? we've a PlainDoc without Attributes
                refreshModelFromDocument();
            }

            public void insertUpdate(DocumentEvent ev) {
                refreshModelFromDocument();
            }

            public void removeUpdate(DocumentEvent ev) {
                refreshModelFromDocument();
            }

        };
        return l;
    }

    /**
     * callback method from listening to searchField.
     *
     */
    protected void refreshModelFromDocument() {
        getPatternModel().setRawText(searchField.getText());
    }


    private void bind() {
        searchField.getDocument().addDocumentListener(getSearchFieldListener());
        ActionContainerFactory factory = new ActionContainerFactory(null);
        factory.configureButton(matchCheck, 
                (AbstractActionExt) getAction(JXSearchPanel.MATCH_CASE_ACTION_COMMAND),
                null);
        factory.configureButton(wrapCheck, 
                (AbstractActionExt) getAction(MATCH_WRAP_ACTION_COMMAND),
                null);
        factory.configureButton(backCheck, 
                (AbstractActionExt) getAction(MATCH_BACKWARDS_ACTION_COMMAND),
                null);
    }

//--------------------- action callbacks
    /**
     * Action callback for Find action.
     */
    public void doFind() {
        doFind(getPatternModel().isBackwards());
    }

    public void doFind(boolean backwards) {
        if (searchable == null) return;
        setLastIndex(searchable.search(getPattern(), getLastIndex(), backwards));
        if (getLastIndex() == -1) {
            boolean notFound = true;
            if (isWrapping()) {
                setLastIndex(searchable.search(getPattern(), -1, backwards));
                notFound = getLastIndex() == -1;
            } 
            if (notFound) {
                JOptionPane.showMessageDialog(this, "Value not found");
            }
        }
    }


    private boolean isWrapping() {
        return getPatternModel().isWrapping();
    }
    /**
     * Action callback for Close action.
     */
    public void doClose() {
        JXFindDialog.this.dispose();
    }


    private void setLastIndex(int i) {
        getPatternModel().setFoundIndex(i);
        
    }

    private int getLastIndex() {
        return getPatternModel().getFoundIndex();
    }

    private Pattern getPattern() {
        return getPatternModel().getPattern();
    }

    //-------------------------- initial
    
    private void initActions() {
        putAction(JXSearchPanel.MATCH_CASE_ACTION_COMMAND, createMatchCaseAction());
        putAction(MATCH_WRAP_ACTION_COMMAND, createWrapAction());
        putAction(MATCH_BACKWARDS_ACTION_COMMAND, createBackwardsAction());
        // PENDING: factor a common dialog containing the following
        putAction(EXECUTE_FIND_ACTION_COMMAND, createFindAction());
        putAction(CLOSE_ACTION_COMMAND, createCloseAction());
    }

    /**
     * 
     * @return
     */
    private AbstractActionExt createMatchCaseAction() {
        String actionName = getUIString(JXSearchPanel.MATCH_CASE_ACTION_COMMAND);
        BoundAction action = new BoundAction(actionName,
                JXSearchPanel.MATCH_CASE_ACTION_COMMAND);
        action.setStateAction();
        action.registerCallback(getPatternModel(), "setCaseSensitive");
        action.setSelected(getPatternModel().isCaseSensitive());
        return action;
    }

    /**
     * 
     * @return
     */
    private AbstractActionExt createWrapAction() {
        String actionName = getUIString(MATCH_WRAP_ACTION_COMMAND);
        BoundAction action = new BoundAction(actionName,
                MATCH_WRAP_ACTION_COMMAND);
        action.setStateAction();
        action.registerCallback(getPatternModel(), "setWrapping");
        action.setSelected(getPatternModel().isWrapping());
        return action;
    }
    /**
     * 
     * @return
     */
    private AbstractActionExt createBackwardsAction() {
        String actionName = getUIString(MATCH_BACKWARDS_ACTION_COMMAND);
        BoundAction action = new BoundAction(actionName,
                MATCH_BACKWARDS_ACTION_COMMAND);
        action.setStateAction();
        action.registerCallback(getPatternModel(), "setBackwards");
        action.setSelected(getPatternModel().isWrapping());
        return action;
    }
    
    /**
     * 
     * @return
     */
    private AbstractActionExt createFindAction() {
        String actionName = getUIString(EXECUTE_FIND_ACTION_COMMAND);
        BoundAction action = new BoundAction(actionName,
                EXECUTE_FIND_ACTION_COMMAND);
        action.registerCallback(this, "doFind");
        return action;
    }
    
    /**
     * 
     * @return
     */
    private AbstractActionExt createCloseAction() {
        String actionName = getUIString(CLOSE_ACTION_COMMAND);
        BoundAction action = new BoundAction(actionName,
                CLOSE_ACTION_COMMAND);
        action.registerCallback(this, "doClose");
        return action;
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
     * @return
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
        String text = UIManager.getString(PatternModel.SEARCH_PREFIX + key);
        return text != null ? text : key;
    }

   
//----------------------------- init ui
    
    /** create components.
     * 
     */
    private void initComponents() {
        searchField = new JTextField();
        matchCheck = new JCheckBox();
        wrapCheck = new JCheckBox();
        backCheck = new JCheckBox();

    }



    private void build() {
        getContentPane().add(createFieldPanel(), BorderLayout.CENTER);
        getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);

        // Implementation of incremental search
        /*
          findText.getDocument().addDocumentListener(new DocumentListener() {
          public void insertUpdate(DocumentEvent evt) {
          doFind();
          }

          public void removeUpdate(DocumentEvent evt) {
          doFind(true);
          }

          public void changedUpdate(DocumentEvent evt) {
          }
          });
        */
    }

    /**
     * TODO: Strings should be removed from the UI
     */
    private JComponent createFieldPanel() {

        // Create components
        JLabel label = new JLabel("Find Text: ");
        label.setDisplayedMnemonicIndex(2);
        label.setLabelFor(searchField);

        Box lBox = Box.createVerticalBox();
        lBox.add(label);
        lBox.add(Box.createGlue());

        Box rBox = Box.createVerticalBox();
        rBox.add(searchField);
        rBox.add(matchCheck);
        rBox.add(wrapCheck);
        rBox.add(backCheck);

        Box box = Box.createHorizontalBox();
        box.add(lBox);
        box.add(rBox);

        box.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        return box;
    }

    /**
     * create the dialog button controls.
     * 
     * PENDING: this should be factored to a common dialog support.
     * 
     * @return
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();

        Action findAction = getAction(EXECUTE_FIND_ACTION_COMMAND);
        Action closeAction = getAction(CLOSE_ACTION_COMMAND);

        JButton findButton;
        panel.add(findButton = new JButton(findAction));
        panel.add(new JButton(closeAction));


        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);

        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(enterKey, EXECUTE_FIND_ACTION_COMMAND);
        inputMap.put(escapeKey, CLOSE_ACTION_COMMAND);

        getRootPane().setDefaultButton(findButton);


        return panel;
    }


//    public boolean getMatchFlag() {
//        return matchCheck.isSelected();
//    }

//    /**
//     * Public method for testing.
//     * <p>
//     * TODO: The state should probably be encapsulated by a model rather
//     * that within the UI components.
//     */
//    public void setMatchFlag(boolean flag) {
//        matchCheck.setSelected(flag);
//    }

//    public boolean getWrapFlag() {
//        return wrapCheck.isSelected();
//    }
//
//    public void setWrapFlag(boolean flag) {
//        wrapCheck.setSelected(flag);
//    }

//    public boolean getBackwardsFlag() {
//        return backCheck.isSelected();
//    }
//
//    public void setBackwardsFlag(boolean flag) {
//        backCheck.setSelected(flag);
//    }


//    private class FindAction extends AbstractAction {
//        public FindAction() {
//            super("Find");
//        }
//        public void actionPerformed(ActionEvent evt) {
//            doFind();
//        }
//    }

//    private class CloseAction extends AbstractAction {
//        public CloseAction() {
//            super("Close");
//        }
//
//        public void actionPerformed(ActionEvent evt) {
//            doClose();
//        }
//    }

    //----------------------- obsolete actions - no longer use
    //----------------------- kept here to remember adding names etc to resources
    private abstract class CheckAction extends AbstractAction {

        public CheckAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent evt) {
        }
    }

    private class MatchAction extends CheckAction {
        public MatchAction() {
            super("Match upper/lower case");
            putValue(Action.MNEMONIC_KEY, new Integer('M'));
        }
    }

    private class WrapAction extends CheckAction {
        public WrapAction() {
            super("Wrap around");
            putValue(Action.MNEMONIC_KEY, new Integer('W'));
        }
    }

    private class BackwardAction extends CheckAction {
        public BackwardAction() {
            super("Search Backwards");
            putValue(Action.MNEMONIC_KEY, new Integer('B'));
        }
    }

}
