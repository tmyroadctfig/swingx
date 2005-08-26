/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
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
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.plaf.basic.BasicOptionPaneUI;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.action.ActionContainerFactory;
import org.jdesktop.swingx.action.BoundAction;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;

/**
 * Simple FindPanel for usage in a JXDialog.
 * 
 * 
 * @author ??
 * @author Jeanette Winzenburg
 */
public class JXFindPanel extends JXPanel {

    static {
        // Hack to enforce loading of SwingX framework ResourceBundle
        LookAndFeelAddons.getAddon();
    }
    
    public static final String MATCH_WRAP_ACTION_COMMAND = "wrapSearch";
    public static final String MATCH_BACKWARDS_ACTION_COMMAND = "backwardsSearch";
    public static final String EXECUTE_FIND_ACTION_COMMAND = "executeSearch";
    public static final String SEARCH_FIELD_LABEL = "searchFieldLabel";
    public static final String SEARCH_FIELD_MNEMONIC = SEARCH_FIELD_LABEL + ".mnemonic";
    public static final String SEARCH_TITLE = "searchTitle";
    
    protected Searchable searchable;

    protected JTextField searchField;
    private JCheckBox matchCheck;
    private JCheckBox wrapCheck;
    private JCheckBox backCheck;


    protected PatternModel patternModel;
    protected boolean incrementalSearch;

    public JXFindPanel() {
        this(null);
    }
    
    public JXFindPanel(Searchable searchable) {
        setSearchable(searchable);
        init();
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
    

    private void init() {
        initActions();
        initComponents();
        build();
        bind();
        setName(getUIString(SEARCH_TITLE));
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
                    refreshSearchableFromModel();
                }

            }

        };
        return l;
    }

    /**
     * callback method from listening to PatternModel.
     *
     */
    protected void refreshSearchableFromModel() {
        if (isIncrementalSearch()) {
            doFind();
        }
    }

    public void setIncrementalSearch(boolean incremental) {
        boolean old = isIncrementalSearch();
        this.incrementalSearch = incremental;
        firePropertyChange("incrementalSearch", old, isIncrementalSearch());
    }
    
    public boolean isIncrementalSearch() {
        return incrementalSearch;
    }

    private DocumentListener getSearchFieldListener() {
        DocumentListener l = new DocumentListener() {
            public void changedUpdate(DocumentEvent ev) {
//                // JW - really?? we've a PlainDoc without Attributes
//                refreshModelFromDocument();
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
        putAction(JXDialog.EXECUTE_ACTION_COMMAND, createFindAction());
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
     * convenience wrapper to access rootPane's actionMap.
     * @param key
     * @param action
     */
    private void putAction(Object key, Action action) {
        getActionMap().put(key, action);
    }
    
    /**
     * convenience wrapper to access rootPane's actionMap.
     * 
     * @param key
     * @return
     */
    private Action getAction(Object key) {
        return getActionMap().get(key);
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
    protected void initComponents() {
        searchField = new JTextField(30) {
            public Dimension getMaximumSize() {
                Dimension superMax = super.getMaximumSize();
                superMax.height = getPreferredSize().height;
                return superMax;
            }
        };
        matchCheck = new JCheckBox();
        wrapCheck = new JCheckBox();
        backCheck = new JCheckBox();

    }



    private void build() {
        JLabel label = new JLabel(getUIString(SEARCH_FIELD_LABEL));
        String mnemonic = getUIString(SEARCH_FIELD_MNEMONIC);
        if (mnemonic != SEARCH_FIELD_MNEMONIC) {
            label.setDisplayedMnemonic(mnemonic.charAt(0));
        }
        label.setLabelFor(searchField);

        Box lBox = new Box(BoxLayout.LINE_AXIS); 
        lBox.add(label);
        lBox.add(new JLabel(":"));
        lBox.add(new JLabel("  "));
        lBox.setAlignmentY(Component.TOP_ALIGNMENT);
        Box rBox = new Box(BoxLayout.PAGE_AXIS); 
        rBox.add(searchField);
        rBox.add(matchCheck);
        rBox.add(wrapCheck);
        rBox.add(backCheck);
        rBox.setAlignmentY(Component.TOP_ALIGNMENT);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        
        add(lBox);
        add(rBox);
    }

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
