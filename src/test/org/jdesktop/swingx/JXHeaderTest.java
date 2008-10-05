/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
 *
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Font;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

import junit.framework.TestCase;

/**
 * Unit test for <code>JXHeader</code>.
 * <p>
 *
 * All test methods in this class are expected to pass.
 *
 * @author Jeanette Winzenburg
 */
public class JXHeaderTest extends TestCase {

    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Header property set to uimanager setting.
     */
    public void testUpdateUIDescriptionFont() {
        Font color = UIManager.getFont("JXHeader.descriptionFont");
        assertNotNull("sanity: title font available", color);
        JXHeader header = new JXHeader();
        assertEquals(color, header.getDescriptionFont());
    }

    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Description label property set to uimanager setting.
     */
    public void testUpdateUIDescriptionLabelFont() {
        Font color = UIManager.getFont("JXHeader.descriptionFont");
        assertNotNull("sanity: title font available", color);
        JXHeader header = new JXHeader();
        assertEquals(color, getDescriptionLabel(header).getFont());
    }
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Description label custom foreground kept on LAF change.
     */
    public void testUpdateUICustomDescriptionLabelFont() {
        Font color = new Font("serif", Font.BOLD, 36);
        JXHeader header = new JXHeader();
        header.setDescriptionFont(color);
        assertEquals("sanity: title color taken", color, getDescriptionLabel(header).getFont());
        SwingUtilities.updateComponentTreeUI(header);
        assertEquals(color, header.getDescriptionFont());
        assertEquals(color, getDescriptionLabel(header).getFont());
    }
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Title label property updated to ui default on laf change.
     */
    public void testUpdateUIDefaultDescriptionLabelFont() {
        Font uiDefault = new FontUIResource("serif", Font.PLAIN, 36);
        Font color = new FontUIResource("serif", Font.ITALIC, 20);
        UIManager.put("JXHeader.descriptionFont", uiDefault);
        JXHeader header = new JXHeader();
        header.setDescriptionFont(color);
        try {
            assertEquals(color, header.getDescriptionFont());
            assertEquals(color, getDescriptionLabel(header).getFont());
            SwingUtilities.updateComponentTreeUI(header);
            
            assertEquals(uiDefault, header.getDescriptionFont());
            assertEquals(uiDefault, getDescriptionLabel(header).getFont());
        } finally {
            // reset custom property
           UIManager.put("JXHeader.descriptionFont", null);   
        }
    }


    
    
//--------------    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Header property set to uimanager setting.
     */
    public void testUpdateUIDescriptionForeground() {
        Color color = UIManager.getColor("JXHeader.titleForeground");
        assertNotNull("sanity: title font available", color);
        JXHeader header = new JXHeader();
        assertEquals(color, header.getDescriptionForeground());
    }

    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Description label property set to uimanager setting.
     */
    public void testUpdateUIDescriptionLabelForeground() {
        Color color = UIManager.getColor("JXHeader.titleForeground");
        assertNotNull("sanity: title font available", color);
        JXHeader header = new JXHeader();
        assertEquals(color, getDescriptionLabel(header).getForeground());
    }
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Description label custom foreground kept on LAF change.
     */
    public void testUpdateUICustomDescriptionLabelForeground() {
        Color color = Color.PINK;
        JXHeader header = new JXHeader();
        header.setDescriptionForeground(color);
        assertEquals("sanity: title color taken", color, getDescriptionLabel(header).getForeground());
        SwingUtilities.updateComponentTreeUI(header);
        assertEquals(color, header.getDescriptionForeground());
        assertEquals(color, getDescriptionLabel(header).getForeground());
    }
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Title label property updated to ui default on laf change.
     */
    public void testUpdateUIDefaultDescriptionLabelForeground() {
        Color uiDefault = new ColorUIResource(Color.BLUE);
        Color color = new ColorUIResource(Color.PINK);
        UIManager.put("JXHeader.descriptionForeground", uiDefault);
        JXHeader header = new JXHeader();
        header.setDescriptionForeground(color);
        SwingUtilities.updateComponentTreeUI(header);
        try {
            
            assertEquals(uiDefault, header.getDescriptionForeground());
            assertEquals(uiDefault, getDescriptionLabel(header).getForeground());
        } finally {
            // reset custom property
           UIManager.put("JXHeader.descriptionForeground", null);   
        }
    }

    /**
     * @return
     */
    private JLabel getDescriptionLabel(JXHeader header) {
        for (int i = 0; i < header.getComponentCount(); i++) {
            if (header.getComponent(i) instanceof JXLabel) {
                return (JLabel) header.getComponent(i);
            }
         }
        return null;
    }

    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Header property set to uimanager setting.
     */
    public void testUpdateUITitleForeground() {
        Color color = UIManager.getColor("JXHeader.titleForeground");
        assertNotNull("sanity: title font available", color);
        JXHeader header = new JXHeader();
        assertEquals(color, header.getTitleForeground());
    }

    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Title label property set to uimanager setting.
     */
    public void testUpdateUITitleLabelForeground() {
        Color color = UIManager.getColor("JXHeader.titleForeground");
        assertNotNull("sanity: title font available", color);
        JXHeader header = new JXHeader();
        assertEquals(color, getTitleLabel(header).getForeground());
    }
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Title label custom foreground kept on LAF change.
     */
    public void testUpdateUICustomTitleLabelForeground() {
        Color color = Color.PINK;
        JXHeader header = new JXHeader();
        header.setTitleForeground(color);
        assertEquals("sanity: title color taken", color, getTitleLabel(header).getForeground());
        SwingUtilities.updateComponentTreeUI(header);
        assertEquals(color, header.getTitleForeground());
        assertEquals(color, getTitleLabel(header).getForeground());
    }
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Title label property updated to ui default on laf change.
     */
    public void testUpdateUIDefaultTitleLabelForeground() {
        Color uiDefault = new ColorUIResource(Color.BLUE);
        Color color = new ColorUIResource(Color.PINK);
        UIManager.put("JXHeader.titleForeground", uiDefault);
        JXHeader header = new JXHeader();
        header.setTitleForeground(color);
        SwingUtilities.updateComponentTreeUI(header);
        try {
            
            assertEquals(uiDefault, header.getTitleForeground());
            assertEquals(uiDefault, getTitleLabel(header).getForeground());
        } finally {
            // reset custom property
           UIManager.put("JXHeader.titleForeground", null);   
        }
    }
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Header property set to uimanager setting.
     */
    public void testUpdateUITitleFont() {
        Font font = UIManager.getFont("JXHeader.titleFont");
        assertNotNull("sanity: title font available", font);
        JXHeader header = new JXHeader();
        assertEquals(font, header.getTitleFont());
    }

    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Test title label property set to UIManager prop
     */
    public void testUpdateUITitleLabelFont() {
        Font font = UIManager.getFont("JXHeader.titleFont");
        assertNotNull("sanity: title font available", font);
        JXHeader header = new JXHeader();
        JLabel label = getTitleLabel(header);
        assertEquals(font, label.getFont());
    }
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * 
     * Test title label font kept same as UIManager property after
     * updateUI
     */
    public void testUpdateUITitleLabelFontUpdateUI() {
        Font font = UIManager.getFont("JXHeader.titleFont");
        assertNotNull("sanity: title font available", font);
        JXHeader header = new JXHeader();
        header.updateUI();
        JLabel label = getTitleLabel(header);
        assertEquals(font, label.getFont());
    }

    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Test custom header property set on title label and
     * kept on updateUI
     */
    public void testUpdateUICustomTitleLabelFontUpdateUI() {
        JXHeader header = new JXHeader();
        Font font = new Font("serif", Font.BOLD, 36);
        header.setTitleFont(font);
        JLabel label = getTitleLabel(header);
        assertEquals(font, label.getFont());
        header.updateUI();
        assertEquals("sanity: header's titleFont kept", font, header.getTitleFont());
        label = getTitleLabel(header);
        assertEquals(font, label.getFont());
    }
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI
     * Test custom header property set on title label and
     * kept on updateComponentTreeUI (aka LAF change)
     */
    public void testUpdateUICustomTitleLabelFontUpdateComponentTreeUI() {
        JXHeader header = new JXHeader();
        Font font = new Font("serif", Font.BOLD, 36);
        header.setTitleFont(font);
        JLabel label = getTitleLabel(header);
        assertEquals(font, label.getFont());
        SwingUtilities.updateComponentTreeUI(header);
        assertEquals("sanity: header's titleFont kept", font, header.getTitleFont());
        label = getTitleLabel(header);
        assertEquals(font, label.getFont());
    }
    
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI
     * Simulate update of default font (from ui) on LAF change.
     */
    public void testUpdateUIREsourceTitleLabelFontUpdateComponentTreeUI() {
        JXHeader header = new JXHeader();
        Font uiDefault = header.getTitleFont();
        // we set a uiResource which we expect to be overwritten on lafChange
        // to the default setting 
        Font font = new FontUIResource("serif", Font.BOLD, 36);
        header.setTitleFont(font);
        SwingUtilities.updateComponentTreeUI(header);
        assertEquals("sanity: header's titleFont updated to default", uiDefault, header.getTitleFont());
        JLabel label = getTitleLabel(header);
        assertEquals(uiDefault, label.getFont());
    }
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI
      * Test title label font kept same as UIManager property after
     * updateComponentTreeUI (aka: toggle LAF)
    */
    public void testUpdateUITitleLabelFontUpdateComponentTreeUI() {
        Font font = UIManager.getFont("JXHeader.titleFont");
        assertNotNull("sanity: title font available", font);
        JXHeader header = new JXHeader();
        SwingUtilities.updateComponentTreeUI(header);
        JLabel label = getTitleLabel(header);
        assertEquals(font, label.getFont());
    }
    
    /**
     * Issue #695-swingx: not-null default values break class invariant.
     * Here initial empty constructor.
     */
    public void testTitleSynchInitialEmpty() {
        JXHeader header = new JXHeader();
        // fishing in the internals ... not really safe, there are 2 labels
        JLabel label = getTitleLabel(header);
        assertEquals(header.getTitle(), label.getText());
    }

    /**
     * @return
     */
    private JLabel getTitleLabel(JXHeader header) {
        for (int i = 0; i < header.getComponentCount(); i++) {
            if (header.getComponent(i) instanceof JLabel) {
                return (JLabel) header.getComponent(i);
            }
         }
        return null;
    }


    /**
     * Issue #695-swingx: not-null default values break invariant.
     * Here: initial not-null explicitly set to null and updateUI (to
     * simulate LF toggle).
     */
    public void testTitleSynchUpdateUI() {
        JXHeader header = new JXHeader("dummy", null);
        header.setTitle(null);
        header.updateUI();
        // fishing in the internals ... not really safe, there are 2 labels
        JLabel label = null;
        for (int i = 0; i < header.getComponentCount(); i++) {
           if (header.getComponent(i) instanceof JLabel) {
               label = (JLabel) header.getComponent(i);
               break;
           }
        }
        assertEquals(header.getTitle(), label.getText());
    }

    /**
     * Issue #695-swingx: not-null default values break invariant.
     * Here: initial null params constructor.
     */
    public void testTitleSynchInitialNull() {
        JXHeader header = new JXHeader(null, null);
        header.setTitle(null);
        // fishing in the internals ... not really safe, there are 2 labels
        JLabel label = null;
        for (int i = 0; i < header.getComponentCount(); i++) {
           if (header.getComponent(i) instanceof JLabel) {
               label = (JLabel) header.getComponent(i);
               break;
           }
        }
        assertEquals(header.getTitle(), label.getText());
    }


    /**
     * Issue #403-swingx: JXHeader doesn't show custom values.
     *
     */
    public void testIconSet() {
        URL url = getClass().getResource("resources/images/wellTop.gif");
        Icon icon = new ImageIcon(url);
        assertNotNull(url);
        JXHeader header = new JXHeader();
        header.setIcon(icon);
        // sanity: the property is set
        assertEquals(icon, header.getIcon());
        // fishing in the internals ... not really safe, there are 2 labels and 1 jxlabel ... indeed not safe!
        JLabel label = null;
        for (int i = 0; i < header.getComponentCount(); i++) {
           if (header.getComponent(i) instanceof JLabel && !(header.getComponent(i) instanceof JXLabel)) {
               boolean second = label != null;
               label = (JLabel) header.getComponent(i);
               if (second) {
                   break;
               }
           }
        }
        assertEquals("the label's text must be equal to the headers title",
                header.getIcon(), label.getIcon());
    }

    /**
     * Issue #403-swingx: JXHeader doesn't show custom values.
     *
     */
    public void testTitleSet() {
        JXHeader header = new JXHeader();
        String title = "customTitle";
        header.setTitle(title);
        // sanity: the property is set
        assertEquals(title, header.getTitle());
        // fishing in the internals ... not really safe, there are 2 labels
        JLabel label = null;
        for (int i = 0; i < header.getComponentCount(); i++) {
           if (header.getComponent(i) instanceof JLabel) {
               label = (JLabel) header.getComponent(i);
               break;
           }
        }
        assertEquals("the label's text must be equal to the headers title",
                header.getTitle(), label.getText());
    }

    /**
     * Issue #403-swingx: JXHeader doesn't show custom values.
     * <p>
     *
     * Breaking if values are passed in the constructor.
     */
    public void testTitleInContructor() {
        String title = "customTitle";
        JXHeader header = new JXHeader(title, null);
        // sanity: the property is set
        assertEquals(title, header.getTitle());
        // fishing in the internals ... not really safe, there are 2 labels
        JLabel label = null;
        for (int i = 0; i < header.getComponentCount(); i++) {
            if (header.getComponent(i) instanceof JLabel) {
                label = (JLabel) header.getComponent(i);
                break;
            }
        }
        assertEquals("the label's text must be equal to the headers title",
                header.getTitle(), label.getText());
    }

    /**
     * Issue swingx-900 NPE when top level ancestor is not available, while "some" ancestor is.
     */
    public void testNPE() {
        JXHeader header = new JXHeader();
        JPanel panel = new JPanel();
        panel.add( header );
        panel.setBounds( 0, 0, 200, 200 );
    }
    
    @Override
    protected void setUp() throws Exception {
        // forcing load of headerAddon
        new JXHeader();
    }

}
