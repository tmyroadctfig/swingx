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

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.action.ActionContainerFactory;
import org.jdesktop.swingx.action.BoundAction;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;

/**
 * Common base class of ui clients.
 * 
 * Implements basic synchronization between PatternModel state and
 * actions bound to it.
 * 
 * 
 * 
 * PENDING: extending JXPanel is a convenience measure, should be extracted
 *   into a dedicated controller.
 * PENDING: should be re-visited when swingx goes binding-aware
 * 
 * @author Jeanette Winzenburg
 */
public abstract class AbstractPatternPanel extends JXPanel {

    public static final String SEARCH_FIELD_LABEL = "searchFieldLabel";
    public static final String SEARCH_FIELD_MNEMONIC = SEARCH_FIELD_LABEL + ".mnemonic";
    public static final String SEARCH_TITLE = "searchTitle";
    public static final String MATCH_ACTION_COMMAND = "match";

    static {
        // Hack to enforce loading of SwingX framework ResourceBundle
        LookAndFeelAddons.getAddon();
    }

    protected JLabel searchLabel;
    protected JTextField searchField;
    protected JCheckBox matchCheck;
    
    protected PatternModel patternModel;
    private ActionContainerFactory actionFactory;


//------------------------ actions

    /**
     * Callback action bound to MATCH_ACTION_COMMAND. 
     *  
     */
    public abstract void match();
    
    /** 
     * convenience method for type-cast to AbstractActionExt.
     * 
     *  
     * @param key
     * @return
     */
    protected AbstractActionExt getAction(String key) {
        // PENDING: outside clients might add different types?
        return (AbstractActionExt) getActionMap().get(key);
    }

    /**
     * creates and registers all actions with the actionMap.
     *
     */
    protected void initActions() {
        initPatternActions();
        initExecutables();
    }
    
    /**
     * creates and registers all "executable" actions.
     * Meaning: the actions bound to a callback method on this.
     * 
     * PENDING: not quite correctly factored? Name?
     *
     */
    protected void initExecutables() {
        Action execute = createBoundAction(MATCH_ACTION_COMMAND, "match");
        getActionMap().put(JXDialog.EXECUTE_ACTION_COMMAND, 
                execute);
        getActionMap().put(MATCH_ACTION_COMMAND, execute);
        refreshEmptyFromModel();
        
        
    }
    
    /**
     * creates actions bound to PatternModel's state.
     *
     */
    protected void initPatternActions() {
        ActionMap map = getActionMap();
        map.put(PatternModel.MATCH_CASE_ACTION_COMMAND, 
                createModelStateAction(PatternModel.MATCH_CASE_ACTION_COMMAND, 
                        "setCaseSensitive", getPatternModel().isCaseSensitive()));
        map.put(PatternModel.MATCH_WRAP_ACTION_COMMAND, 
                createModelStateAction(PatternModel.MATCH_WRAP_ACTION_COMMAND, 
                        "setWrapping", getPatternModel().isWrapping()));
        map.put(PatternModel.MATCH_BACKWARDS_ACTION_COMMAND, 
                createModelStateAction(PatternModel.MATCH_BACKWARDS_ACTION_COMMAND, 
                        "setBackwards", getPatternModel().isBackwards()));
        map.put(PatternModel.MATCH_INCREMENTAL_ACTION_COMMAND, 
                createModelStateAction(PatternModel.MATCH_INCREMENTAL_ACTION_COMMAND, 
                        "setIncremental", getPatternModel().isIncremental()));
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
    protected String getUIString(String key) {
        String text = UIManager.getString(PatternModel.SEARCH_PREFIX + key);
        return text != null ? text : key;
    }


    /**
     * creates, configures and returns a bound state action on a boolean property
     * of the PatternModel.
     * 
     * @param command the actionCommand - same as key to find localizable resources
     * @param methodName the method on the PatternModel to call on item state changed
     * @param initial the initial value of the property
     * @return
     */
    protected AbstractActionExt createModelStateAction(String command, String methodName, boolean initial) {
        String actionName = getUIString(command);
        BoundAction action = new BoundAction(actionName,
                command);
        action.setStateAction();
        action.registerCallback(getPatternModel(), methodName);
        action.setSelected(initial);
        return action;
    }

    /**
     * creates, configures and returns a bound action to the given method of 
     * this.
     * 
     * @param actionCommand the actionCommand, same as key to find localizable resources
     * @param methodName the method to call an actionPerformed.
     * @return
     */
    protected AbstractActionExt createBoundAction(String actionCommand, String methodName) {
        String actionName = getUIString(actionCommand);
        BoundAction action = new BoundAction(actionName,
                actionCommand);
        action.registerCallback(this, methodName);
        return action;
    }


    //---------------------- synch patternModel <--> components

    /**
     * called from listening to pattern property of PatternModel.
     * 
     * This implementation calls match() if the model is in
     * incremental state.
     *
     */
    protected void refreshPatternFromModel() {
        if (getPatternModel().isIncremental()) {
            match();
        }
    }


    /**
     * returns the patternModel. Lazyly creates and registers a
     * propertyChangeListener if null.
     * 
     */
    protected PatternModel getPatternModel() {
        if (patternModel == null) {
            patternModel = createPatternModel();
            patternModel.addPropertyChangeListener(getPatternModelListener());
        }
        return patternModel;
    }


    /**
     * factory method to create the PatternModel.
     * Hook for subclasses to install custom models.
     * @return
     */
    protected PatternModel createPatternModel() {
        PatternModel l = new PatternModel();
        return l;
    }

    /**
     * creates and returns a PropertyChangeListener to the PatternModel.
     * 
     * NOTE: the patternModel is totally under control of this class - currently
     * there's no need to keep a reference to the listener.
     * 
     * @return
     */
    protected PropertyChangeListener getPatternModelListener() {
        PropertyChangeListener l = new PropertyChangeListener() {
    
            public void propertyChange(PropertyChangeEvent evt) {
                String property = evt.getPropertyName();
                if ("pattern".equals(property)) {
                    refreshPatternFromModel();
                } else if ("rawText".equals(property)) {
                    refreshDocumentFromModel();
                } else if ("caseSensitive".equals(property)){
                    getAction(PatternModel.MATCH_CASE_ACTION_COMMAND).
                        setSelected(((Boolean) evt.getNewValue()).booleanValue());
                } else if ("wrapping".equals(property)) {
                    getAction(PatternModel.MATCH_WRAP_ACTION_COMMAND).
                    setSelected(((Boolean) evt.getNewValue()).booleanValue());
                } else if ("backwards".equals(property)) {
                    getAction(PatternModel.MATCH_BACKWARDS_ACTION_COMMAND).
                    setSelected(((Boolean) evt.getNewValue()).booleanValue());
                } else if ("incremental".equals(property)) {
                    getAction(PatternModel.MATCH_INCREMENTAL_ACTION_COMMAND).
                    setSelected(((Boolean) evt.getNewValue()).booleanValue());

                } else if ("empty".equals(property)) {
                    refreshEmptyFromModel();
                }   
    
            }
    
        };
        return l;
    }

    /**
     * called from listening to empty property of PatternModel.
     * 
     * this implementation synch's the enabled state of the action with
     * MATCH_ACTION_COMMAND to !empty.
     * 
     */
    protected void refreshEmptyFromModel() {
        boolean enabled = !getPatternModel().isEmpty();
        getAction(MATCH_ACTION_COMMAND).setEnabled(enabled);
        
    }

    /**
     * callback method from listening to searchField.
     *
     */
    protected void refreshModelFromDocument() {
        getPatternModel().setRawText(searchField.getText());
    }

    protected void refreshDocumentFromModel() {
        if (searchField.getText().equals(getPatternModel().getRawText())) return;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                searchField.setText(getPatternModel().getRawText());
            }
        });
    }

    protected DocumentListener getSearchFieldListener() {
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

//-------------------------- config helpers

    /**
     * configure and bind components to/from PatternModel
     */
    protected void bind() {
      bindSearchLabel();
        searchField.getDocument().addDocumentListener(getSearchFieldListener());
        ActionContainerFactory factory = new ActionContainerFactory(null);
        getActionContainerFactory().configureButton(matchCheck, 
                (AbstractActionExt) getActionMap().get(PatternModel.MATCH_CASE_ACTION_COMMAND),
                null);
        
    }

    /**
     * Configures the searchLabel.
     * Here: sets text and mnenomic properties form ui values, 
     * configures as label for searchField.
     */
    protected void bindSearchLabel() {
        searchLabel.setText(getUIString(SEARCH_FIELD_LABEL));
          String mnemonic = getUIString(SEARCH_FIELD_MNEMONIC);
          if (mnemonic != SEARCH_FIELD_MNEMONIC) {
              searchLabel.setDisplayedMnemonic(mnemonic.charAt(0));
          }
          searchLabel.setLabelFor(searchField);
    }
    
    protected ActionContainerFactory getActionContainerFactory() {
        if (actionFactory == null) {
            actionFactory = new ActionContainerFactory(null);
        }
        return actionFactory;
    }
    
    protected void initComponents() {
        searchLabel = new JLabel();
        searchField = new JTextField(getSearchFieldWidth()) {
            public Dimension getMaximumSize() {
                Dimension superMax = super.getMaximumSize();
                superMax.height = getPreferredSize().height;
                return superMax;
            }
        };
        matchCheck = new JCheckBox();
        
    }

    /**
     * @return
     */
    protected int getSearchFieldWidth() {
        return 15;
    }
}
