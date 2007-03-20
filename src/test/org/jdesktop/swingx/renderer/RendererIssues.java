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
package org.jdesktop.swingx.renderer;

import java.util.logging.Logger;

import junit.framework.TestCase;

import org.jdesktop.swingx.painter.ShapePainter;

/**
 * Test around known issues of SwingX renderers. <p>
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 * 
 * @author Jeanette Winzenburg
 */
public class RendererIssues extends TestCase {
    private static final Logger LOG = Logger.getLogger(RendererIssues.class
            .getName());
    /**
     * RendererLabel NPE with null Graphics. While expected,
     * the exact location is not.
     * NPE in JComponent.paintComponent finally block 
     *
     */
    public void testLabelNPEPaintComponentOpaque() {
        JRendererLabel label = new JRendererLabel();
        label.setOpaque(true);
        label.paintComponent(null);
    }
    
    /**
     * RendererLabel NPE with null Graphics. While expected,
     * the exact location is not.
     * NPE in JComponent.paintComponent finally block 
     *
     */
    public void testLabelNPEPaintComponent() {
        JRendererLabel label = new JRendererLabel();
        label.setOpaque(false);
        label.paintComponent(null);
    }

    /**
     * RendererLabel NPE with null Graphics. 
     * Fail-fast NPE in label.paintComponentWithPainter.
     *
     */
    public void testLabelNPEPaintComponentOpaqueWithPainter() {
        JRendererLabel label = new JRendererLabel();
        label.setOpaque(true);
        label.setPainter(new ShapePainter());
        try {
            label.paintComponent(null);
            fail("invoke paintComponent with null graphics must throw NPE");
        } catch (NullPointerException e) {
            // basically the right thing - but how to test the fail-fast?
            LOG.info("got the expected NPE - " +
                        "but how to test the fail-fast impl? " + e);
        } catch (Exception e) {
            fail("unexpected exception invoke paintcomponent with null" + e);
        }
    }
    /**
     * RendererLabel NPE with null Graphics. 
     * Fail-fast NPE in paintPainter.
     *
     */
    public void testLabelNPEPaintComponentWithPainter() {
        JRendererLabel label = new JRendererLabel();
        label.setOpaque(false);
        label.setPainter(new ShapePainter());
        try {
            label.paintComponent(null);
            fail("invoke paintComponent with null graphics must throw NPE");
        } catch (NullPointerException e) {
            // basically the right thing - but how to test the fail-fast?
            LOG.info("got the expected NPE - " +
                        "but how to test the fail-fast impl? " + e);
        } catch (Exception e) {
            fail("unexpected exception invoke paintcomponent with null" + e);
        }
    }

    /**
     * RendererLabel NPE with null Graphics. 
     * NPE in label.paintComponentWithPainter finally block.
     *
     */
    public void testButtonNPEPaintComponentOpaqueWithPainter() {
        JRendererCheckBox  checkBox = new JRendererCheckBox();
        checkBox.setOpaque(true);
        checkBox.setPainter(new ShapePainter());
        try {
            checkBox.paintComponent(null);
            fail("invoke paintComponent with null graphics must throw NPE");
        } catch (NullPointerException e) {
            // basically the right thing - but how to test the fail-fast?
            LOG.info("got the expected NPE - " +
                        "but how to test the fail-fast impl? " + e);
        } catch (Exception e) {
            fail("unexpected exception invoke paintcomponent with null" + e);
        }
    }

    /**
     * RendererCheckBox NPE with null Graphics. NPE in
     * label.paintComponentWithPainter finally block.
     * 
     */
    public void testButtonNPEPaintComponentWithPainter() {
        JRendererCheckBox checkBox = new JRendererCheckBox();
        checkBox.setOpaque(false);
        checkBox.setPainter(new ShapePainter());
        try {
            checkBox.paintComponent(null);
            fail("invoke paintComponent with null graphics must throw NPE");
        } catch (NullPointerException e) {
            // basically the right thing - but how to test the fail-fast?
            LOG.info("got the expected NPE - " +
                        "but how to test the fail-fast impl? " + e);
        } catch (Exception e) {
            fail("unexpected exception invoke paintcomponent with null" + e);
        }
    }


}
