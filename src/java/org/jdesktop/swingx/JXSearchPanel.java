/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.util.regex.Pattern;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.PatternHighlighter;
import org.jdesktop.swingx.decorator.PatternMatcher;


/**
 * Search panel.
 *
 * @author Ramesh Gupta
 * @author Jeanette Winzenburg
 * 
 */
public class JXSearchPanel extends JPanel implements DocumentListener, ActionListener {
    private final JLabel				fieldName = new JLabel();
    private final JCheckBox				matchCase = new JCheckBox();
    private final JComboBox				searchCriteria = new JComboBox();
    private	final JTextField			searchField = new JTextField();

    private PatternFilter				patternFilter = null;
    private PatternHighlighter			patternHighlighter = null;
    private JComponent					targetComponent = null;

    private PatternModel patternModel;
    
    public JXSearchPanel() {
        //super(new FlowLayout());
        /** @todo Remove hard-coded strings */
        patternModel = new PatternModel();
        matchCase.setText("Match Case");
        ComboBoxModel	model = new DefaultComboBoxModel(new String[] {
//            "begins with",
//            "contains",
//            "ends with",
//            "equals"
              PatternModel.SEARCH_CATEGORY_CONTAINS,
              PatternModel.SEARCH_CATEGORY_STARTSWITH,
              PatternModel.SEARCH_CATEGORY_ENDSWITH,
              PatternModel.SEARCH_CATEGORY_EQUALS
        });
        model.setSelectedItem(patternModel.getSearchCategory());
        searchCriteria.setModel(model);
	searchField.setPreferredSize(new Dimension(80, 20));
        add(fieldName);
        add(searchCriteria);
        add(searchField);
        add(matchCase);
        addActionListener();
        addEditListener();
    }


    public Pattern getPattern() {
        return patternModel.getPattern();
    }

    private void addActionListener() {
        matchCase.addActionListener(this);
        searchField.addActionListener(this);
        searchCriteria.addActionListener(this);
    }

    private void removeActionListener() {
        matchCase.removeActionListener(this);
        searchField.removeActionListener(this);
        searchCriteria.removeActionListener(this);
    }

    private void addEditListener() {
        searchField.getDocument().addDocumentListener(this);
    }

    private void removeEditListener() {
        searchField.getDocument().removeDocumentListener(this);
    }

    public void setPatternFilter(PatternFilter filter) {
        patternFilter = filter;
        if (filter == null) {
            fieldName.setText("Field");
        }
        else {
            fieldName.setText(filter.getColumnName());
        }
    }

    public PatternFilter getPatternFilter() {
        return patternFilter;
    }

    public void setPatternHighlighter(PatternHighlighter highlighter) {
        patternHighlighter = highlighter;
        if (fieldName.getText().length() == 0) {	// ugly hack
            fieldName.setText("Field");	/** @todo Remove this hack!!! */
        }
    }

    public PatternHighlighter getPatternHighlighter() {
        return patternHighlighter;
    }

    public void setTargetComponent(JComponent target) {
        /** @todo Obsolete this method.
         * Use event listener to target more than one component. */
        this.targetComponent = target;
    }

    public JComponent getTargetComponent() {
        return targetComponent;
    }

    public void setFieldName(String name) {
        fieldName.setText(name);
    }

    public String getFieldName() {
        return fieldName.getText();
    }


    public void changedUpdate(DocumentEvent ev) {
        refreshFromDocument();
    }

    public void insertUpdate(DocumentEvent ev) {
        refreshFromDocument();
    }

    public void removeUpdate(DocumentEvent ev) {
        refreshFromDocument();
    }

    public void actionPerformed(ActionEvent ev) {
        if (matchCase.equals(ev.getSource())) {
            patternModel.setCaseSensitive(matchCase.isSelected());
        } else if (searchCriteria.equals(ev.getSource())) {
            patternModel.setSearchCategory((String)searchCriteria.getSelectedItem());
        }
        refresh();
    }

    protected void refreshFromDocument() {
        patternModel.setRawText(searchField.getText());
        refresh();
    }
    
    protected void refresh() {
        Pattern			pattern = getPattern();

        PatternMatcher	filter = getPatternFilter();
        if (filter != null) {
            filter.setPattern(pattern);	// will repaint target automatically!
        }

        PatternMatcher	highlighter = getPatternHighlighter();
        if (highlighter != null) {
            highlighter.setPattern(pattern); // will repaint target automatically
        }

//	    if (filter == null) {
//                // Repaint explicitly only if there is no filter
//                JComponent target = getTargetComponent();
//                if (target != null) {
//                    target.repaint();
//                }
//            }
//        }
    }

}

