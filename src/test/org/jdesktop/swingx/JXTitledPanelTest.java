/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import org.jdesktop.test.util.PropertyChangeReport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

/**
 * @author Jeanette Winzenburg
 */
public class JXTitledPanelTest extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXTitledPanelTest.class
            .getName());
    
    // flag used in setup to explicitly choose LF
    private boolean defaultToSystemLF;

    protected void setUp() throws Exception {
        super.setUp();
        // make sure we have the same default for each test
        defaultToSystemLF = false;
        setSystemLF(defaultToSystemLF);
    }

    public JXTitledPanelTest() {
        super("JXTitledPane interactive test");
    }

    
    public void testLayoutOnLFChange() {
        JXTitledPanel titledPanel = new JXTitledPanel();
        assertNotNull(titledPanel.getContentContainer());
        titledPanel.getContentContainer().setLayout(new BorderLayout());
        String lf = UIManager.getLookAndFeel().getName();
        setSystemLF(!defaultToSystemLF);
        if (lf.equals(UIManager.getLookAndFeel().getName())) {
            LOG.info("cannot run layoutOnLFChange - equal LF" + lf);
            return;
        }
        SwingUtilities.updateComponentTreeUI(titledPanel);
        assertTrue(titledPanel.getContentContainer().getLayout() instanceof BorderLayout);
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
        Font deriveFont = panel.getTitleFont().deriveFont(27f);
        panel.setTitleFont(deriveFont);
        assertTrue("panel must notify on titleFont change", report.hasEvents("titleFont"));
// TODO: Whoever owns this please fix this.  The tests should run clean.        
//        fail("JXTitledPanelTest must be updated to new api");
//        panel.setTitleForeground(Color.black);
//        assertTrue("panel must notify on titleForeground change", report.hasEvents("titleForeground"));
//        panel.setTitleDarkBackground(Color.black);
//        assertTrue("panel must notify on titleDarkBackground change", report.hasEvents("titleDarkBackground"));
//        panel.setTitleLightBackground(Color.black);
//        assertTrue("panel must notify on titleLightBackground change", report.hasEvents("titleLightBackground"));
        
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

    public  void interactiveRToL() {
        String title = "starting title";
        JXTitledPanel titledPane = new JXTitledPanel(title);
        titledPane.addLeftDecoration(new JLabel("Leading"));
        titledPane.addRightDecoration(new JLabel("Trailing"));
//        panel.getContentContainer().setLayout(new BoxLayout(panel.getContentContainer(), BoxLayout.PAGE_AXIS));
        Icon icon = new ImageIcon(getClass().getResource("resources/images/wellBottom.gif"));
        final JLabel label = new JLabel(title);
        label.setIcon(icon);
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(titledPane, BorderLayout.NORTH);
        panel.add(label);
        JXFrame frame = wrapInFrame(panel, "toggle Title");
        Action toggleCO = new AbstractAction("toggle orientation") {


                public void actionPerformed(ActionEvent e) {
                    ComponentOrientation current = panel.getComponentOrientation();
                    if (current == ComponentOrientation.LEFT_TO_RIGHT) {
                        panel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//                        panel.revalidate();
//                        panel.repaint();
                        label.setText("RightToLeft");
                    } else {
                        panel.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
//                      panel.revalidate();
//                      panel.repaint();
                        label.setText("LeftToRight");

                    }

                }
                
            
        };
        addAction(frame, toggleCO);
        frame.pack();
        frame.setVisible(true);

    }
    

    public  void interactiveIconAndHtmlTest() {
        String title = "<html><u>starting title </u></html>";
        final JXTitledPanel panel = new JXTitledPanel(title);
        Icon icon = new ImageIcon(getClass().getResource("resources/images/wellBottom.gif"));
        panel.addLeftDecoration(new JLabel(icon));
        panel.getContentContainer().setLayout(new BoxLayout(panel.getContentContainer(), BoxLayout.Y_AXIS));
        panel.getContentContainer().add(new JLabel(title));
        JXFrame frame = wrapInFrame(panel, "toggle Title");
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
                LOG.info("JXTitledPanelText must be updated to new api");

//                Color oldFont = panel.getTitleLightBackground();
//                panel.setTitleLightBackground(oldFont.darker());
                
            }
            
        };

        panel.getContentContainer().add(new JButton(toggleLight));
        panel.getContentContainer().setLayout(new BoxLayout(panel.getContentContainer(), BoxLayout.Y_AXIS));
        Action toggleDark = new AbstractAction("toggle darkbackground") {
            public void actionPerformed(ActionEvent e) {
                LOG.info("JXTitledPanelText must be updated to new api");
//                Color oldFont = panel.getTitleDarkBackground();
//                panel.setTitleDarkBackground(oldFont.darker());
                
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
