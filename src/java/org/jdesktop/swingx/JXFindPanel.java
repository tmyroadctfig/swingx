/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * Simple FindPanel for usage in a JXDialog.
 * 
 * 
 * @author ??
 * @author Jeanette Winzenburg
 */
public class JXFindPanel extends AbstractPatternPanel {

    
    public static final String FIND_NEXT_ACTION_COMMAND = "findNext";
    public static final String FIND_PREVIOUS_ACTION_COMMAND = "findPrevious";
    

    protected Searchable searchable;

    protected JCheckBox wrapCheck;
    protected JCheckBox backCheck;
    private boolean initialized;


//    protected JButton findNext;
//    protected JButton findPrevious;

    public JXFindPanel() {
        this(null);
    }
    
    public JXFindPanel(Searchable searchable) {
        setSearchable(searchable);
        initActions();
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
        getPatternModel().setFoundIndex(-1);
        firePropertyChange("searchable", old, this.searchable);
    }
    

    public void addNotify() {
        init();
        super.addNotify();
    }
    
    protected void init() {
        if (initialized) return;
        initialized = true;
        initComponents();
        build();
        bind();
        setName(getUIString(SEARCH_TITLE));
        
    }
    
    //------------------ support synch the model <--> components
    


    protected void bind() {
        super.bind();
        getActionContainerFactory().configureButton(wrapCheck, 
                getAction(PatternModel.MATCH_WRAP_ACTION_COMMAND),
                null);
        getActionContainerFactory().configureButton(backCheck, 
                getAction(PatternModel.MATCH_BACKWARDS_ACTION_COMMAND),
                null);

//        findNext.setAction(getAction(EXECUTE_FIND_NEXT_ACTION_COMMAND));
//        findPrevious.setAction(getAction(EXECUTE_FIND_PREVIOUS_ACTION_COMMAND));
    }

    
    /**
     * called from listening to empty property of PatternModel.
     * 
     * this implementation calls super and additionally synchs the 
     * enabled state of FIND_NEXT_ACTION_COMMAND, FIND_PREVIOUS_ACTION_COMMAND
     * to !empty.
     */
    @Override
    protected void refreshEmptyFromModel() {
        super.refreshEmptyFromModel();
        boolean enabled = !getPatternModel().isEmpty();
        getAction(FIND_NEXT_ACTION_COMMAND).setEnabled(enabled);
        getAction(FIND_PREVIOUS_ACTION_COMMAND).setEnabled(enabled);
    }

    //--------------------- action callbacks
    /**
     * Action callback for Find action.
     * Find next/previous match using current setting of direction flag.
     * 
     */
    public void match() {
        doFind();
    }

    /**
     * Action callback for FindNext action.
     * Sets direction flag to forward and calls find.
     */
    public void findNext() {
        getPatternModel().setBackwards(false);
        match();
    }
    
    /**
     * Action callback for FindPrevious action.
     * Sets direction flag to previous and calls find.
     */
    public void findPrevious() {
        getPatternModel().setBackwards(true);
        match();
    }
    
    protected void doFind() {
        if (searchable == null) return;
        int foundIndex = doSearch();
        if ((foundIndex == -1) && !getPatternModel().isEmpty()){
            boolean notFound = true;
            if (getPatternModel().isWrapping()) {
                notFound = doSearch() == -1;
            } 
            if (notFound) {
                showNotFoundMessage();
            }
        }
    }

    /**
     * @return
     */
    protected int doSearch() {
        int foundIndex = searchable.search(getPatternModel().getPattern(), 
                getPatternModel().getFoundIndex(), getPatternModel().isBackwards());
        getPatternModel().setFoundIndex(foundIndex);
        return foundIndex;
    }

    /**
     * 
     */
    protected void showNotFoundMessage() {
        JOptionPane.showMessageDialog(this, "Value not found");
    }


    //-------------------------- initial
    
    /**
     * 
     */
    protected void initExecutables() {
        getActionMap().put(FIND_NEXT_ACTION_COMMAND, 
                createBoundAction(FIND_NEXT_ACTION_COMMAND, "findNext"));
        getActionMap().put(FIND_PREVIOUS_ACTION_COMMAND, 
                createBoundAction(FIND_PREVIOUS_ACTION_COMMAND, "findPrevious"));
        super.initExecutables();
    }


  
//----------------------------- init ui
    
    /** create components.
     * 
     */
    protected void initComponents() {
        super.initComponents();
        wrapCheck = new JCheckBox();
        backCheck = new JCheckBox();
//        findNext = new JButton();
//        findPrevious = new JButton();

    }



    protected void build() {

        Box lBox = new Box(BoxLayout.LINE_AXIS); 
        lBox.add(searchLabel);
        lBox.add(new JLabel(":"));
        lBox.add(new JLabel("  "));
        lBox.setAlignmentY(Component.TOP_ALIGNMENT);
        Box rBox = new Box(BoxLayout.PAGE_AXIS); 
        rBox.add(searchField);
        rBox.add(matchCheck);
        rBox.add(wrapCheck);
        rBox.add(backCheck);
        // just want to see...
//        rBox.add(findNext);
//        rBox.add(findPrevious);
        
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
