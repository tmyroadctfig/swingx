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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;

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


    public JXFindPanel() {
        this(null);
    }
    
    public JXFindPanel(Searchable searchable) {
        setSearchable(searchable);
        initActions();
    }
    
    /**
     * Sets the Searchable targeted of this find widget.
     * Triggers a search with null pattern to release the old
     * searchable, if any.
     * 
     * @param searchable 
     */
    public void setSearchable(Searchable searchable) {
        if ((this.searchable != null) && this.searchable.equals(searchable)) return;
        Searchable old = this.searchable;
        if (old != null) {
            old.search((Pattern) null);
        }
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
        doFind();
    }
    
    /**
     * Action callback for FindPrevious action.
     * Sets direction flag to previous and calls find.
     */
    public void findPrevious() {
        getPatternModel().setBackwards(true);
        doFind();
    }
    
    protected void doFind() {
        if (searchable == null)
            return;
        int foundIndex = doSearch();
        boolean notFound = (foundIndex == -1) && !getPatternModel().isEmpty();
        if (notFound) {
            if (getPatternModel().isWrapping()) {
                notFound = doSearch() == -1;
            }

        }
        if (notFound) {
            showNotFoundMessage();
        } else {
            showFoundMessage();
        }

    }

    /**
     * @return
     */
    protected int doSearch() {
        int foundIndex = searchable.search(getPatternModel().getPattern(), 
                getPatternModel().getFoundIndex(), getPatternModel().isBackwards());
        getPatternModel().setFoundIndex(foundIndex);
        return getPatternModel().getFoundIndex();
    }

    protected void showFoundMessage() {
        
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
        rBox.setAlignmentY(Component.TOP_ALIGNMENT);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        
        add(lBox);
        add(rBox);
    }


}
