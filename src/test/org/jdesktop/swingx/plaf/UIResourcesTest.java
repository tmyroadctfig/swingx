/*
 * $Id$
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.plaf.UIResource;

import junit.framework.TestCase;

import org.jdesktop.swingx.icon.EmptyIcon;
import org.jdesktop.swingx.plaf.basic.BasicLookAndFeelAddons;
import org.jdesktop.swingx.plaf.linux.LinuxLookAndFeelAddons;
import org.jdesktop.swingx.plaf.macosx.MacOSXLookAndFeelAddons;
import org.jdesktop.swingx.plaf.metal.MetalLookAndFeelAddons;
import org.jdesktop.swingx.plaf.motif.MotifLookAndFeelAddons;
import org.jdesktop.swingx.plaf.nimbus.NimbusLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsClassicLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsLookAndFeelAddons;

/**
 * This test ensures that all values that should be {@code UIResouce}s are.
 * 
 * @author Karl George Schaefer
 */
public class UIResourcesTest extends TestCase {
    /**
     * {@inheritDoc}
     */
    protected void setUp() {
        System.setProperty("swingx.enableStrictResourceChecking", "true");
        LookAndFeelAddons.contribute(new ColumnHeaderRendererAddon());
        LookAndFeelAddons.contribute(new DatePickerAddon());
        LookAndFeelAddons.contribute(new ErrorPaneAddon());
        LookAndFeelAddons.contribute(new HeaderAddon());
        LookAndFeelAddons.contribute(new HyperlinkAddon());
        LookAndFeelAddons.contribute(new LoginPaneAddon());
        LookAndFeelAddons.contribute(new MonthViewAddon());
        LookAndFeelAddons.contribute(new MultiThumbSliderAddon());
        LookAndFeelAddons.contribute(new StatusBarAddon());
        LookAndFeelAddons.contribute(new TaskPaneAddon());
        LookAndFeelAddons.contribute(new TaskPaneContainerAddon());
        LookAndFeelAddons.contribute(new TipOfTheDayAddon());
        LookAndFeelAddons.contribute(new TitledPanelAddon());
        LookAndFeelAddons.contribute(new UIColorHighlighterAddon());
    }
    
    /**
     * Ensures that all basic values are {@code UIResource}s where appropriate.
     * 
     * @throws Exception
     *                 if an error occurs
     */
    public void testBasicLookAndFeelAddonsForUIResources() throws Exception {
        LookAndFeelAddons.setAddon(BasicLookAndFeelAddons.class);
    }
    
    /**
     * Ensures that all linux values are {@code UIResource}s where appropriate.
     * 
     * @throws Exception
     *                 if an error occurs
     */
    public void testLinuxLookAndFeelAddonsForUIResources() throws Exception {
        LookAndFeelAddons.setAddon(LinuxLookAndFeelAddons.class);
    }
    
    /**
     * Ensures that all Mac OSX values are {@code UIResource}s where appropriate.
     * 
     * @throws Exception
     *                 if an error occurs
     */
    public void testMacOSXLookAndFeelAddonsForUIResources() throws Exception {
        LookAndFeelAddons.setAddon(MacOSXLookAndFeelAddons.class);
    }
    
   /**
    * Ensures that all metal values are {@code UIResource}s where appropriate.
    * 
    * @throws Exception
    *                 if an error occurs
    */
    public void testMetalLookAndFeelAddonsForUIResources() throws Exception {
        LookAndFeelAddons.setAddon(MetalLookAndFeelAddons.class);
    }
    
    /**
     * Ensures that all Motif values are {@code UIResource}s where appropriate.
     * 
     * @throws Exception
     *                 if an error occurs
     */
    public void testMotifLookAndFeelAddonsForUIResources() throws Exception {
        LookAndFeelAddons.setAddon(MotifLookAndFeelAddons.class);
    }
    
    /**
     * Ensures that all Nimbus values are {@code UIResource}s where appropriate.
     * 
     * @throws Exception
     *                 if an error occurs
     */
    public void testNimbusLookAndFeelAddonsForUIResources() throws Exception {
        LookAndFeelAddons.setAddon(NimbusLookAndFeelAddons.class);
    }
    
    /**
     * Ensures that all Windows values are {@code UIResource}s where appropriate.
     * 
     * @throws Exception
     *                 if an error occurs
     */
    public void testWindowsLookAndFeelAddonsForUIResources() throws Exception {
        LookAndFeelAddons.setAddon(WindowsLookAndFeelAddons.class);
    }
    
    /**
     * Ensures that all Windows classic values are {@code UIResource}s where appropriate.
     * 
     * @throws Exception
     *                 if an error occurs
     */
    public void testWindowsClassicLookAndFeelAddonsForUIResources() throws Exception {
        LookAndFeelAddons.setAddon(WindowsClassicLookAndFeelAddons.class);
    }
    
    /**
     * Ensure that the {@code getSafeXXX} methods always return {@code UIResource}.
     */
    public void testGetSafeMethodsReturnUIResource() {
        assertTrue(UIManagerExt.getSafeBorder("", BorderFactory
                .createEmptyBorder()) instanceof UIResource);
        assertTrue(UIManagerExt.getSafeColor("", Color.RED) instanceof UIResource);
        assertTrue(UIManagerExt.getSafeDimension("",
                new Dimension()) instanceof UIResource);
        assertTrue(UIManagerExt.getSafeFont("",
               new Font("Dialog", Font.BOLD, 12)) instanceof UIResource);
        assertTrue(UIManagerExt.getSafeIcon("",
                new EmptyIcon()) instanceof UIResource);
        assertTrue(UIManagerExt.getSafeInsets("",
                new Insets(0, 0, 0, 0)) instanceof UIResource);
    }
}
