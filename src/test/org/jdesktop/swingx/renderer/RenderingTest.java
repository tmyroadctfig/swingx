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

import javax.swing.JLabel;

import junit.framework.TestCase;

/**
 * Tests swingx rendering infrastructure: RenderingXController, CellContext, 
 * ..
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class RenderingTest extends TestCase {

    /**
     * Test doc'ed constructor behaviour of default tree renderer.
     *
     */
    public void testDefaultTreeRendererConstructors() {
        DefaultTreeRenderer renderer = new DefaultTreeRenderer();
        assertTrue(renderer.componentController instanceof WrappingIconController);
        renderer = new DefaultTreeRenderer(FormatToStringConverter.DATE_TO_STRING);
        assertTrue(renderer.componentController instanceof WrappingIconController);
        // wrong assumption - we are wrapping...
//        assertSame(FormatToStringConverter.DATE_TO_STRING, renderer.componentController.formatter);
        assertSame(FormatToStringConverter.DATE_TO_STRING, ((WrappingIconController) renderer.componentController).wrappee.formatter);
        RenderingComponentController controller = new RenderingButtonController();
        renderer = new DefaultTreeRenderer(controller);
        assertSame(controller, renderer.componentController);
    }

    /**
     * Test doc'ed constructor behaviour of default list renderer.
     *
     */
    public void testDefaultListRendererConstructors() {
        DefaultListRenderer renderer = new DefaultListRenderer();
        assertTrue(renderer.componentController instanceof RenderingLabelController);
        renderer = new DefaultListRenderer(FormatToStringConverter.DATE_TO_STRING);
        assertTrue(renderer.componentController instanceof RenderingLabelController);
        assertSame(FormatToStringConverter.DATE_TO_STRING, renderer.componentController.formatter);
        RenderingComponentController controller = new RenderingButtonController();
        renderer = new DefaultListRenderer(controller);
        assertSame(controller, renderer.componentController);
    }

    /**
     * Test doc'ed constructor behaviour of default table renderer.
     *
     */
    public void testDefaultTableRendererConstructors() {
        DefaultTableRenderer renderer = new DefaultTableRenderer();
        assertTrue(renderer.componentController instanceof RenderingLabelController);
        renderer = new DefaultTableRenderer(FormatToStringConverter.DATE_TO_STRING);
        assertTrue(renderer.componentController instanceof RenderingLabelController);
        assertSame(FormatToStringConverter.DATE_TO_STRING, renderer.componentController.formatter);
        RenderingComponentController controller = new RenderingButtonController();
        renderer = new DefaultTableRenderer(controller);
        assertSame(controller, renderer.componentController);
    }
    /**
     * public methods of <code>RenderingComponentController</code> must cope
     * with null context. Here: test getRenderingComponent.
     */
    public void testGetComponentNullContext() {
        RenderingComponentController controller = new RenderingLabelController();
        assertEquals(controller.rendererComponent, controller.getRendererComponent(null));
    }
    /**
     * public methods of <code>RenderingComponentController</code> must cope
     * with null context. Here: test getRenderingComponent.
     */
    public void testStringValueNullContext() {
        RenderingComponentController controller = new RenderingLabelController();
        controller.getStringValue(null);
    }
    
    /**
     * test doc'ed behaviour on rendererController configure:
     * NPE on null context.
     *
     */
    public void testConfigureVisualsNullContext() {
        RendererController<JLabel> controller = new RendererController<JLabel>();
        try {
            controller.configureVisuals(new JLabel(), null);
            fail("renderer controller must throw NPE on null context");
        } catch (NullPointerException e) {
            // this is what we expect
        } catch (Exception e) {
            fail("renderer controller must throw NPE on null context - instead: " + e);
        }
    }
    /**
     * test doc'ed behaviour on rendererController configure:
     * NPE on null component.
     *
     */
    public void testConfigureVisualsNullComponent() {
        RendererController<JLabel> controller = new RendererController<JLabel>();
        try {
            controller.configureVisuals(null, new TableCellContext());
            fail("renderer controller must throw NPE on null component");
        } catch (NullPointerException e) {
            // this is what we expect
        } catch (Exception e) {
            fail("renderer controller must throw NPE on null component - instead: " + e);
        }
    }
}
