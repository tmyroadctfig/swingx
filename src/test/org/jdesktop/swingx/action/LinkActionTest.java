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
     * test if auto-installed visited property is respected.
     *
     */
    public void testConstructorsAndCustomTargetInstall() {
        Object target = new Object();
        final boolean visitedIsTrue = true;
        LinkAction linkAction = new LinkAction(target) {

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
        LinkAction linkAction = new LinkAction(target) {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        };
        assertEquals(target, linkAction.getTarget());
        assertFalse(linkAction.isVisited());
        // removed convenience constructor - issues with class invariant
//        LinkAction linkAction2 = new LinkAction(target, visitedIsTrue) {
//
//            public void actionPerformed(ActionEvent e) {
//                // TODO Auto-generated method stub
//                
//            }
//            
//        };
//        assertEquals(target, linkAction2.getTarget());
//        assertEquals(visitedIsTrue, linkAction2.isVisited());
    }
    /**
     * test visited/target properties of LinkAction.
     *
     */
    public void testLinkAction() {
       LinkAction linkAction = new LinkAction() {

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
