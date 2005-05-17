/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jdesktop.swingx.util.PropertyChangeReport;

/**
 * @author Jeanette Winzenburg
 */
public class JXTitledPanelTest extends InteractiveTestCase {

    public JXTitledPanelTest() {
        super("JXTitledPane interactive test");
    }

    /**
     * Issue ??: notifications missing on all "title"XX properties.
     *
     */
    public void testTitlePropertiesNotify() {
        String title = "starting title";
        final JXTitledPanel panel = new JXTitledPanel(title);
        PropertyChangeReport report = new PropertyChangeReport();
        panel.addPropertyChangeListener(report);
        assertTrue("panel must notify on titleFont change", report.hasEvents("titleFont"));
    }
    
    /**
     * SwingX Issue #9: missing notification on title change.
     * happens if a generic property change listener (== one who 
     * wants to get all property changes) is registered.
     */
    public void testTitleNotify() {
        String title = "starting title";
        final JXTitledPanel panel = new JXTitledPanel(title);
        PropertyChangeReport report = new PropertyChangeReport();
        panel.addPropertyChangeListener(report);
        panel.setTitle("new title");
        assertTrue("panel must have fired propertyChange", report.hasEvents());
        
    }
    /**
     * SwingX Issue #9: missing notification on title change.
     * Notification is correct, if a named propertyChangeListener is
     * registered.
     */
    public void testTitleNotifyNamed() {
        String title = "starting title";
        final JXTitledPanel panel = new JXTitledPanel(title);
        PropertyChangeReport report = new PropertyChangeReport();
        panel.addPropertyChangeListener( "title", report);
        panel.setTitle("new title");
        assertTrue("panel must have fired propertyChange", report.hasEvents());
        
    }
    
    /**
     * incorrect propertyChangeEvent on setTitle(null).
     *
     */
    public void testTitleNotifyPropertyValue() {
        String title = "starting title";
        final JXTitledPanel panel = new JXTitledPanel(title);
        PropertyChangeReport report = new PropertyChangeReport();
        panel.addPropertyChangeListener( "title", report);
        panel.setTitle(null);
        assertTrue("panel must have fired propertyChange", report.hasEvents());
        assertEquals("new property value must be equal to getTitle", panel.getTitle(),
                report.getLastNewValue("title"));
        
    }

//--------------------- interactive tests
    
    public  void interactiveIconAndHtmlTest() {
        String title = "<html><u>starting title </u></html>";
        final JXTitledPanel panel = new JXTitledPanel(title);
        Icon icon = new ImageIcon(getClass().getResource("resources/images/wellBottom.gif"));
        panel.addLeftDecoration(new JLabel(icon));
        panel.getContentContainer().setLayout(new BoxLayout(panel.getContentContainer(), BoxLayout.Y_AXIS));
        panel.getContentContainer().add(new JLabel(title));
        JFrame frame = wrapInFrame(panel, "toggle Title");
        frame.setVisible(true);

    }
    
    /**
     * trying to set divers TitledPanel properties interactively.
     * can't set titlefont.
     */
    public void interactiveTitleTest() {
        String title = "starting title";
        final JXTitledPanel panel = new JXTitledPanel(title);
        panel.getContentContainer().setLayout(new BoxLayout(panel.getContentContainer(), BoxLayout.Y_AXIS));
        Action toggleLight = new AbstractAction("toggle lightBackground") {
            public void actionPerformed(ActionEvent e) {
                Color oldFont = panel.getTitleLightBackground();
                panel.setTitleLightBackground(oldFont.darker());
                
            }
            
        };

        panel.getContentContainer().add(new JButton(toggleLight));
        panel.getContentContainer().setLayout(new BoxLayout(panel.getContentContainer(), BoxLayout.Y_AXIS));
        Action toggleDark = new AbstractAction("toggle darkbackground") {
            public void actionPerformed(ActionEvent e) {
                Color oldFont = panel.getTitleDarkBackground();
                panel.setTitleDarkBackground(oldFont.darker());
                
            }
            
        };

        panel.getContentContainer().add(new JButton(toggleDark));
        Action toggleForeground = new AbstractAction("toggle Foreground") {
            public void actionPerformed(ActionEvent e) {
                Color oldColor = panel.getTitleForeground();
                panel.setTitleForeground(oldColor.darker());
                
            }
            
        };

        panel.getContentContainer().add(new JButton(toggleForeground));
        Action toggleFont = new AbstractAction("toggle Font") {
            public void actionPerformed(ActionEvent e) {
                Font oldFont = panel.getTitleFont();
                System.out.println("oldfont size: " + oldFont.getSize());
                panel.setTitleFont(oldFont.deriveFont(oldFont.getSize()*2.f));
                
            }
            
        };

        panel.getContentContainer().add(new JButton(toggleFont));
        Action toggleTitle = new AbstractAction("toggle title") {
            int count = 0;
            public void actionPerformed(ActionEvent e) {
                panel.setTitle(" * " + count++ + " title");
                
            }
            
        };

        panel.getContentContainer().add(new JButton(toggleTitle));
        JFrame frame = wrapInFrame(panel, "toggle Title");
        frame.setVisible(true);
    }
    
    public static void main(String args[]) {
        JXTitledPanelTest test = new JXTitledPanelTest();
        try {
          test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }
}
