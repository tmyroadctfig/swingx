/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.jdesktop.swingx.action.LinkAction;
import org.jdesktop.test.PropertyChangeReport;

/**
 * Test of JXHyperlink. Raw usage and as hyperlinkRenderer.
 * <p>
 * 
 * @author Jeanette Winzenburg
 */
public class JXHyperlinkTest extends TestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(JXHyperlinkTest.class
            .getName());
    
    private PropertyChangeReport report;

    
    /**
     * test control of the clicked property.
     * 
     * Default behaviour
     * 
     *
     */
    public void testAutoClicked() {
       // no action 
       JXHyperlink hyperlink = new JXHyperlink();
       hyperlink.doClick();
       assertTrue("hyperlink autoClicks if it has no action", hyperlink.isClicked());
       
       LinkAction<Object> emptyAction = createEmptyLinkAction();
       JXHyperlink hyperlink2 = new JXHyperlink(emptyAction);
       hyperlink2.doClick();
       assertFalse(emptyAction.isVisited());
       assertFalse("hyperlink does nothing if has action", hyperlink2.isClicked());
       
       LinkAction emptyAction3 = createEmptyLinkAction();
       JXHyperlink hyperlink3 = new JXHyperlink(emptyAction3);
       hyperlink3.setOverrulesActionOnClick(true);
       hyperlink3.doClick();
       assertFalse(emptyAction.isVisited());
       assertTrue("hyperlink overrules action", hyperlink3.isClicked());
       
    }
    
    public void testOverrulesActionOnClick() {
        JXHyperlink hyperlink = new JXHyperlink();
        assertFalse(hyperlink.getOverrulesActionOnClick());
        hyperlink.addPropertyChangeListener(report);
        hyperlink.setOverrulesActionOnClick(true);
        assertTrue(hyperlink.getOverrulesActionOnClick()); 
        assertEquals(1, report.getEventCount("overrulesActionOnClick"));
    }
    /**
     * sanity (duplicate of LinkActionTest method) to
     * guarantee that hyperlink is updated as expected.
     *
     */
    public void testLinkActionSetTarget() {
        LinkAction<Object> linkAction = createEmptyLinkAction();
        linkAction.setVisited(true);
        JXHyperlink hyperlink = new JXHyperlink(linkAction);
        Object target = new Object();
        linkAction.setTarget(target);
        assertEquals(linkAction.getName(), hyperlink.getText());
        assertFalse(hyperlink.isClicked());
    }
    /**
     * test that hyperlink.setClicked doesn't change action.isVisited();
     *
     */
    public void testSetClickedActionUnchanged() {
        LinkAction<Object> linkAction = createEmptyLinkAction();
        linkAction.setVisited(true);
        JXHyperlink hyperlink = new JXHyperlink(linkAction);
        // sanity assert..
        assertTrue(hyperlink.isClicked());
        hyperlink.setClicked(false);
        // action state must be unchanged;
        assertTrue(linkAction.isVisited());
        
    }
    /**
     * test hyperlink's clicked property.
     *
     */
    public void testClicked() {
        JXHyperlink hyperlink = new JXHyperlink();
        boolean isClicked = hyperlink.isClicked();
        assertFalse(isClicked);
        hyperlink.addPropertyChangeListener(report);
        hyperlink.setClicked(!isClicked);
        assertEquals(1, report.getEventCount("clicked"));
    }
    
    /**
     * JXHyperlink must handle null action gracefully.
     * 
     * Was NPE in configureFromAction
     *
     */
    public void testInitNullAction() {
        JXHyperlink hyperlink = new JXHyperlink();
        assertNull(hyperlink.getAction());
        
    }

    /**
     * JXHyperlink must handle null action gracefully.
     * 
     * Was NPE in configureFromAction
     *
     */
    public void testSetNullAction() {
        LinkAction action = createEmptyLinkAction();
        JXHyperlink hyperlink = new JXHyperlink(action);
        assertEquals("hyperlink action must be equal to linkAction", action, hyperlink.getAction());
        hyperlink.setAction(null);
        assertNull(hyperlink.getAction());
    }
    /**
     * JXHyperlink must handle null action gracefully.
     * 
     * Was NPE in configureFromAction
     *
     */
    public void testSetAction() {
        JXHyperlink hyperlink = new JXHyperlink();
        LinkAction action = createEmptyLinkAction();
        hyperlink.setAction(action);
        assertEquals("hyperlink action must be equal to linkAction", 
                action, hyperlink.getAction());
    }

    /**
     * test that JXHyperlink visited state keeps synched 
     * to LinkAction.
     *
     */
    public void testListeningVisited() {
       LinkAction<Object> linkAction = createEmptyLinkAction();
       JXHyperlink hyperlink = new JXHyperlink(linkAction);
       // sanity: both are expected to be false
       assertEquals(linkAction.isVisited(), hyperlink.isClicked());
       assertFalse(linkAction.isVisited());
       linkAction.setVisited(!linkAction.isVisited());
       assertEquals(linkAction.isVisited(), hyperlink.isClicked());
    }
    
    /**
     * test initial visited state in JXHyperlink is synched to
     * linkAction given in constructor.
     * 
     * There was the usual "init" problem with the constructor.
     * Solved by chaining.
     * 
     */
    public void testInitialVisitedSynched() {
        LinkAction<Object> linkAction = createEmptyLinkAction();
       linkAction.setVisited(true);
       // sanity: linkAction is changed to true
       assertTrue(linkAction.isVisited());
       JXHyperlink hyperlink = new JXHyperlink(linkAction);
       assertEquals(linkAction.isVisited(), hyperlink.isClicked());
    }


    public static class Player {
        String name;
        int score;
        public Player(String name, int score) {
            this.name = name;
            this.score = score;
        }
        @Override
        public String toString() {
            return name + " has score: " + score;
        }
    }

    protected LinkAction<Object> createEmptyLinkAction() {
        LinkAction<Object> linkAction = new LinkAction<Object>(null) {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
               
       };
        return linkAction;
    }

    protected LinkAction createEmptyLinkAction(String name) {
        LinkAction linkAction = createEmptyLinkAction();
        linkAction.setName(name);
        return linkAction;
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        report = new PropertyChangeReport();
    }

}
