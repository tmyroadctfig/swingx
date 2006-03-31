/*
 * Created on 28.03.2006
 *
 */
package org.jdesktop.swingx.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import junit.framework.TestCase;

import org.jdesktop.swingx.util.PropertyChangeReport;

/**
 * 
 * @author Jeanette Winzenburg, Berlin
 */
public class LinkActionTest extends TestCase {

    
    private PropertyChangeReport report;

    /**
     * test exception as documented.
     *
     */
    public void testConstructorSubclass() {
        try {
            LinkAction linkAction = new LinkAction(new Integer(10), Number.class) {
    
                public void actionPerformed(ActionEvent e) {
                    // TODO Auto-generated method stub
                    
                }
                
            };
        } catch (IllegalArgumentException e) {
            fail("instantiate LinkAction with subtypes of targetclass must not throw any exception " +
                e);
        } 
        
    }

    /**
     * test exception as documented.
     *
     */
    public void testConstructorException() {
        try {
            LinkAction linkAction = new LinkAction<Number> (new Integer(10), Number.class) {
    
                public void actionPerformed(ActionEvent e) {
                    // TODO Auto-generated method stub
                    
                }
                
            };
            fail("instantiate LinkAction with non-matching target and targetClass must throw" +
                        "IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // this is what to expect
        } catch (Exception e) {
            fail("unexpected exception type - " + e);
        }
        
    }
    /**
     * test if auto-installed visited property is respected.
     *
     */
    public void testConstructorsAndCustomTargetInstall() {
        Object target = new Object();
        final boolean visitedIsTrue = true;
        LinkAction linkAction = new LinkAction<Object>(target, Object.class) {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            protected void installTarget() {
                super.installTarget();
                setVisited(visitedIsTrue);
            }
            
            
            
        };
        assertEquals(visitedIsTrue, linkAction.isVisited());
        
    }
    /**
     * test constructors with parameters
     *
     */
    public void testConstructors() {
        Object target = new Object();
        boolean visitedIsTrue = true;
        LinkAction linkAction = new LinkAction(target, null) {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        };
        assertEquals(target, linkAction.getTarget());
        assertFalse(linkAction.isVisited());
    }
    /**
     * test visited/target properties of LinkAction.
     *
     */
    public void testLinkAction() {
       LinkAction linkAction = new LinkAction(null) {

        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            
        }
           
       };
       linkAction.addPropertyChangeListener(report);
       
       boolean visited = linkAction.isVisited();
       assertFalse(visited);
       linkAction.setVisited(!visited);
       assertEquals(!visited, linkAction.isVisited());
       assertEquals(1, report.getEventCount(LinkAction.VISITED_KEY));
       
       report.clear();
       // testing target property
       assertNull(linkAction.getTarget());
       Object target = new Object();
       linkAction.setTarget(target);
       assertEquals(target, linkAction.getTarget());
       assertEquals(1, report.getEventCount("target"));
       // testing documented default side-effects of un/installTarget
       assertEquals(target.toString(), linkAction.getName());
       assertFalse(linkAction.isVisited());
       assertEquals(1, report.getEventCount(Action.NAME));
       assertEquals(1, report.getEventCount(LinkAction.VISITED_KEY));
       // fired the expected events only.
       assertEquals(3, report.getEventCount());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        report = new PropertyChangeReport();
    }

    
}
