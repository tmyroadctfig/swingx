/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

/**
 * A simple low-intrusion default widget for incremental search.
 * 
 * 
 * Actions registered (in addition to super):
 * <ul>
 *  <li> {@link JXDialog#CLOSE_ACTION_COMMAND} - an action bound to this component's
 *      cancel method. The method itself is an empty implementation: Subclassing
 *      clients can override the method, all clients can register a custom action.
 * </ul>
 * 
 * Key bindings:
 * <ul>
 *   <li> ESCAPE - calls action registered for {@link JXDialog#CLOSE_ACTION_COMMAND}
 * </ul>
 * 
 * @author Jeanette Winzenburg
 * 
 */
public class JXFindBar extends JXFindPanel {

    
    Color previousBackgroundColor;
    Color previousForegroundColor;
    // PENDING: need to read from UIManager
    Color notFoundBackgroundColor = Color.decode("#FF6666");
    Color notFoundForegroundColor = Color.white;
  protected JButton findNext;
  protected JButton findPrevious;

    public JXFindBar() {
        this(null);
    }

    public JXFindBar(Searchable searchable) {
        super(searchable);
        getPatternModel().setIncremental(true);
    }

    
    @Override
    protected void showFoundMessage() {
        if (previousBackgroundColor != null) {
            searchField.setBackground(previousBackgroundColor);
            searchField.setForeground(previousForegroundColor);
        }        
    }

    @Override
    protected void showNotFoundMessage() {
        previousBackgroundColor = searchField.getBackground();
        previousForegroundColor = searchField.getForeground();
        searchField.setForeground(notFoundForegroundColor);
        searchField.setBackground(notFoundBackgroundColor);    }

    //--------------------------- action call back
    /**
     * Action callback method for bound action 
     * JXDialog.CLOSE_ACTION_COMMAND.
     * 
     * Here: does nothing. Subclasses can override to define custom
     * "closing" behaviour. Alternatively, any client can register
     * a custom action with the actionMap.
     * 
     *
     */
    public void cancel() {
    }

    //-------------------- init
    
    @Override
    protected void initExecutables() {
        getActionMap().put(JXDialog.CLOSE_ACTION_COMMAND, 
                createBoundAction(JXDialog.CLOSE_ACTION_COMMAND, "cancel"));
        super.initExecutables();
    }

    @Override
    protected void bind() {
      super.bind();
      searchField.addActionListener(getAction(JXDialog.EXECUTE_ACTION_COMMAND));
      findNext.setAction(getAction(FIND_NEXT_ACTION_COMMAND));
      findPrevious.setAction(getAction(FIND_PREVIOUS_ACTION_COMMAND));
      KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
      getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, JXDialog.CLOSE_ACTION_COMMAND);
    }

    @Override
    protected void build() {
//        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(searchLabel);
        add(new JLabel(":"));
        add(new JLabel("  "));
        add(searchField);
        add(findNext);
        add(findPrevious);
    }

   
    @Override
    protected void initComponents() {
      super.initComponents();
      findNext = new JButton();
      findPrevious = new JButton();
    }

    
}
