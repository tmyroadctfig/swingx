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
 */
public class JXSearchPanel extends JPanel implements DocumentListener, ActionListener {
    //public final static String	MEDIATOR_KEY = "jfc:searchPanel";

    // colleagues mediated by this JXSearchPanel
    private final JLabel				fieldName = new JLabel();
    private final JCheckBox				matchCase = new JCheckBox();
    private final JComboBox				searchCriteria = new JComboBox();
    private	final JButton				searchButton = new JButton();
    private	final JTextField			searchField = new JTextField();
    // JXSearchPanel is the mediator for all colleagues
    private final JComponent[]			colleagues = {
	matchCase,
	searchCriteria,
	searchButton,
	searchField
    };

    private PatternFilter				patternFilter = null;
    private PatternHighlighter			patternHighlighter = null;
    private JComponent					targetComponent = null;

    public JXSearchPanel() {
        //super(new FlowLayout());
        /** @todo Remove hard-coded strings */
        searchButton.setText("Search");
        matchCase.setText("Match Case");
        ComboBoxModel	model = new DefaultComboBoxModel(new String[] {
            "begins with",
            "contains",
            "ends with",
            "equals"
        });
        searchCriteria.setModel(model);
	searchField.setPreferredSize(new Dimension(80, 20));
	/*
	  for (int i = 0; i < colleagues.length; i++) {
	  colleagues[i].putClientProperty(MEDIATOR_KEY, this);
	  }
	*/
        add(fieldName);
        add(searchCriteria);
        add(searchField);
        add(matchCase);
        //add(searchButton);	/** @todo remove searchButton entirely! */
        addActionListener();
        addEditListener();
    }

    public int getMatchFlags() {
        return matchCase.isSelected() ? 0 : Pattern.CASE_INSENSITIVE;
    }

    public Pattern getPattern() {
        String	searchString = searchField.getText();
        if (searchString.length() == 0) {
            return Pattern.compile(".*", getMatchFlags());
        }

        String	patternString;
        int criteria = searchCriteria.getSelectedIndex();
        /** @todo Remove hard-coded integers */
        switch (criteria) {
	case	0: {
	    patternString = new String(searchString + ".*");
	    break;
	}
	case	1: {
	    patternString = new String(".*" + searchString + ".*");
	    break;
	}
	case	2: {
	    patternString = new String(".*" + searchString);
	    break;
	}
	default: {
	    patternString = searchString;
	    break;
	}
        }
        return Pattern.compile(patternString, getMatchFlags());
    }

    private void addActionListener() {
        /** @todo Define ActionSource interface for all components that
         * support addActionListener and removeActionListener
         * for (int i = 0; i < colleagues.length; i++) {
         * 		colleagues[i].addActionListener(this);
         * }
         */
        // In the absence of ActionSource, add listener to each colleage...
        matchCase.addActionListener(this);
        searchButton.addActionListener(this);
        searchField.addActionListener(this);
        searchCriteria.addActionListener(this);
    }

    private void removeActionListener() {
        /** @todo Define ActionSource interface for all components that
         * support addActionListener and removeActionListener
         * for (int i = 0; i < colleagues.length; i++) {
         * 		colleagues[i].removeActionListener(listener);
         * }
         */
        // In the absence of ActionSource, remove listener from each colleage...
        matchCase.removeActionListener(this);
        searchButton.removeActionListener(this);
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

    /*
      JTextField getSearchTextField() {
      return searchField;
      }

      JComboBox getSearchCriteriaComboBox() {
      return searchCriteria;
      }

      JCheckBox getCaseCheckBox() {
      return matchCase;
      }
    */

    public void changedUpdate(DocumentEvent ev) {
        refresh();
    }

    public void insertUpdate(DocumentEvent ev) {
        refresh();
    }

    public void removeUpdate(DocumentEvent ev) {
        refresh();
    }

    public void actionPerformed(ActionEvent ev) {
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
            highlighter.setPattern(pattern);

	    if (filter == null) {
                // Repaint explicitly only if there is no filter
                JComponent target = getTargetComponent();
                if (target != null) {
                    target.repaint();
                }
            }
        }
    }

    /*
      public static JXSearchPanel getMediator(Object colleague) {
      JXSearchPanel	searchPanel = null;
      try {
      // colleague could be a JTextField, JButton, JCheckBox, or JComboBox
      JComponent	component = (JComponent) colleague;
      //searchPanel = (JXSearchPanel) component.getClientProperty(MEDIATOR_KEY);
      }
      catch (ClassCastException ex) {
      // Perhaps colleague is not a JComponent?
      }
      return searchPanel;	// OK to return null
      }
    */
}

