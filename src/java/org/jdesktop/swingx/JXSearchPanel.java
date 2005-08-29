/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.action.ActionContainerFactory;
import org.jdesktop.swingx.action.BoundAction;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.PatternHighlighter;
import org.jdesktop.swingx.decorator.PatternMatcher;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.plaf.SearchPanelAddon;

/**
 * Rudimentary search panel.
 * 
 * Updates PatternMatchers from user input.
 * 
 * Supports 
 * 
 * <ol>
 * <li> text input to match
 * <li> match rules like contains/equals/... 
 * <li> toggle case sensitive match
 * </ol>
 * 
 * TODO: allow custom PatternModel and/or access 
 * to configuration of bound PatternModel. 
 * 
 * TODO: fully support control of multiple PatternMatchers.
 * 
 * @author Ramesh Gupta
 * @author Jeanette Winzenburg
 * 
 */
public class JXSearchPanel extends AbstractPatternPanel {


    public static final String MATCH_RULE_ACTION_COMMAND = "selectMatchRule";

    private JComboBox searchCriteria;

    private List<PatternMatcher> patternMatchers;
    

    public JXSearchPanel() {
        initActions();
        initComponents();
        build();
        bind();
    }

//----------------- accessing public properties

    /**
     * sets the PatternFilter control.
     * 
     * PENDING: change to do a addPatternMatcher to enable multiple control.
     * 
     */
    public void setPatternFilter(PatternFilter filter) {
        getPatternMatchers().add(filter);
        updateFieldName(filter);
    }

    /**
     * sets the PatternHighlighter control.
     * 
     * PENDING: change to do a addPatternMatcher to enable multiple control.
     * 
     */
    public void setPatternHighlighter(PatternHighlighter highlighter) {
        getPatternMatchers().add(highlighter);
        updateFieldName(highlighter);
    }



    /**
     * set the label of the search combo.
     * 
     * @param name
     */
    public void setFieldName(String name) {
        searchLabel.setText(name);
    }

    /**
     * returns the label of the search combo.
     * 
     */
    public String getFieldName() {
        return searchLabel.getText();
    }

    /**
     * returns the current compiled Pattern.
     * 
     * @return
     */
    public Pattern getPattern() {
        return patternModel.getPattern();
    }

    /**
     * @param filter
     */
    protected void updateFieldName(PatternMatcher matcher) {
        
        if (matcher instanceof PatternFilter) {
            PatternFilter filter = (PatternFilter) matcher;
            if (filter == null) {
                searchLabel.setText("Field");
            } else {
                searchLabel.setText(filter.getColumnName());
            }
        } else {
            if (searchLabel.getText().length() == 0) { // ugly hack
                searchLabel.setText("Field");
                /** @todo Remove this hack!!! */
            }

        }
    }

    // ---------------- action callbacks

    /**
     * set's the PatternModel's MatchRule to the selected in combo. 
     * 
     * NOTE: this
     * is public as an implementation side-effect! 
     * No need to ever call directly.
     */
    public void updateMatchRule() {
        getPatternModel().setMatchRule(
                (String) searchCriteria.getSelectedItem());
    }

    //---------------- init actions
    
    protected void initActions() {
        initPatternActions();
        getActionMap().put(MATCH_RULE_ACTION_COMMAND,
                createBoundAction(MATCH_RULE_ACTION_COMMAND, "updateMatchRule"));
    }

    
    /**
     * callback method from listening to PatternModel.
     *
     */
    protected void refreshPatternFromModel() {
        for (Iterator<PatternMatcher> iter = getPatternMatchers().iterator(); iter.hasNext();) {
            iter.next().setPattern(getPattern());
            
        }
    }


    private List<PatternMatcher> getPatternMatchers() {
        if (patternMatchers == null) {
            patternMatchers = new ArrayList<PatternMatcher>();
        }
        return patternMatchers;
    }

    //--------------------- binding support
    
    /**
     * bind the components to the patternModel/actions.
     */
    protected void bind() {
        super.bind();
        List matchRules = getPatternModel().getMatchRules();
        // PENDING: map rules to localized strings
        ComboBoxModel model = new DefaultComboBoxModel(matchRules.toArray());
        model.setSelectedItem(getPatternModel().getMatchRule());
        searchCriteria.setModel(model);
        searchCriteria.setAction(getAction(MATCH_RULE_ACTION_COMMAND));
        
    }
    


    //------------------------ init ui
    
    /**
     * build container by adding all components.
     * PRE: all components created.
     */
    private void build() {
        add(searchLabel);
        add(searchCriteria);
        add(searchField);
        add(matchCheck);
    }

    /**
     * create contained components.
     * 
     *
     */
    protected void initComponents() {
        super.initComponents();
        searchCriteria = new JComboBox();
    }


}
