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
 *
 */
package org.jdesktop.swingx.util;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.SwingXUtilities;
import org.jdesktop.swingx.plaf.basic.BasicDatePickerUI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test SwingXUtilities.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class SwingXUtilitiesTest extends InteractiveTestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(SwingXUtilitiesTest.class.getName());
    
    public static void main(String[] args) {
        SwingXUtilitiesTest test = new SwingXUtilitiesTest();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testDescendingNull() {
        assertFalse("both nulls are not descending", SwingXUtilities.isDescendingFrom(null, null));
        assertFalse("null comp is not descending", SwingXUtilities.isDescendingFrom(null, new JScrollPane()));
        assertFalse("comp is not descending null parent", SwingXUtilities.isDescendingFrom(new JLabel(), null));
    }
    
    @Test
    public void testDescendingSame() {
        JComponent comp = new JLabel();
        assertTrue("same component must be interpreted as descending", 
                SwingXUtilities.isDescendingFrom(comp, comp));
    }
    
    @Test
    public void testDescendingPopup() throws InterruptedException, InvocationTargetException {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless");
            return;
        }
        final JXDatePicker picker = new JXDatePicker();
        JXFrame frame = new JXFrame("showing", false);
        frame.add(picker);
        frame.pack();
        frame.setVisible(true);
        assertFalse(SwingXUtilities.isDescendingFrom(picker.getMonthView(), picker));
        Action togglePopup = picker.getActionMap().get("TOGGLE_POPUP");
        togglePopup.actionPerformed(null);
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                assertTrue("popup visible ", ((BasicDatePickerUI) picker.getUI()).isPopupVisible());
                assertTrue(SwingXUtilities.isDescendingFrom(picker.getMonthView(), picker));
                
            }
        });
        frame.dispose();
    }
}
