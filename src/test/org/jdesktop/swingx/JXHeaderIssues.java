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

import java.awt.Font;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Test to expose known issues of <code>JXHeader</code>.
 * <p>
 * 
 * Ideally, there would be at least one failing test method per open issue in
 * the issue tracker. Plus additional failing test methods for not fully
 * specified or not yet decided upon features/behaviour.
 * <p>
 * 
 * If an issue is fixed and the corresponding methods are passing, they
 * should be moved over to the XXTest.
 * 
 * @author Jeanette Winzenburg
 */
public class JXHeaderIssues extends InteractiveTestCase {
    
    
//    private JXHeader header;

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
        label = getTitleLabel(header);
        assertEquals(font, label.getFont());
    }
    /**
     * Issue #925-swingx: custom properties lost on updateUI
     */
    public void testUpdateUICustomTitleLabelFontUpdateComponentTreeUI() {
        JXHeader header = new JXHeader();
        Font font = new Font("serif", Font.BOLD, 36);
        header.setTitleFont(font);
        JLabel label = getTitleLabel(header);
        assertEquals(font, label.getFont());
        SwingUtilities.updateComponentTreeUI(header);
        label = getTitleLabel(header);
        assertEquals(font, label.getFont());
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

    public void interactiveHeaderEmpty() {
        JXHeader header = new JXHeader();
        showInFrame(header, "empty constructor");
    }
    
    public static void main(String args[]) {
        JXHeaderIssues test = new JXHeaderIssues();
        try {
          test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }

    @Override
    protected void setUp() throws Exception {
        setSystemLF(true);
        // forcing load of headerAddon
        new JXHeader();
    }
    
    /**
     * Dummy empty test just to keep it from whining.
     */
    public void testDummy() {
        // do nothing
    }
    
}
