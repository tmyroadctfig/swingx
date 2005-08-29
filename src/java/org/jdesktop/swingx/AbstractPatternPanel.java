/*
 * Created on 26.08.2005
 *
 */
package org.jdesktop.swingx;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.action.ActionContainerFactory;
import org.jdesktop.swingx.action.BoundAction;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;

public abstract class AbstractPatternPanel extends JXPanel {

    public static final String SEARCH_FIELD_LABEL = "searchFieldLabel";
    public static final String SEARCH_FIELD_MNEMONIC = SEARCH_FIELD_LABEL + ".mnemonic";
    public static final String SEARCH_TITLE = "searchTitle";

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

    protected AbstractActionExt getAction(String key) {
        return (AbstractActionExt) getActionMap().get(key);
    }

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
     * called after receiving a "pattern" propertyChange from PatternModel.
     */
    protected abstract void refreshPatternFromModel();


    /**
     * 
     */
    protected PatternModel getPatternModel() {
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
    protected PropertyChangeListener getPatternModelListener() {
        PropertyChangeListener l = new PropertyChangeListener() {
    
            public void propertyChange(PropertyChangeEvent evt) {
                String property = evt.getPropertyName();
                if ("pattern".equals(property)) {
                    refreshPatternFromModel();
                } else if ("caseSensitive".equals(property)){
                    getAction(PatternModel.MATCH_CASE_ACTION_COMMAND).
                        setSelected(((Boolean) evt.getNewValue()).booleanValue());
                } else if ("wrapping".equals(property)) {
                    getAction(PatternModel.MATCH_WRAP_ACTION_COMMAND).
                    setSelected(((Boolean) evt.getNewValue()).booleanValue());
                } else if ("backwards".equals(property)) {
                    getAction(PatternModel.MATCH_BACKWARDS_ACTION_COMMAND).
                    setSelected(((Boolean) evt.getNewValue()).booleanValue());
                }
    
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
      searchLabel.setText(getUIString(SEARCH_FIELD_LABEL));
      String mnemonic = getUIString(SEARCH_FIELD_MNEMONIC);
      if (mnemonic != SEARCH_FIELD_MNEMONIC) {
          searchLabel.setDisplayedMnemonic(mnemonic.charAt(0));
      }
      searchLabel.setLabelFor(searchField);
        searchField.getDocument().addDocumentListener(getSearchFieldListener());
        ActionContainerFactory factory = new ActionContainerFactory(null);
        getActionContainerFactory().configureButton(matchCheck, 
                (AbstractActionExt) getActionMap().get(PatternModel.MATCH_CASE_ACTION_COMMAND),
                null);
        
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
        return 20;
    }
}
