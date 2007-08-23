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

import java.text.DateFormat;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.ShapePainter;

/**
 * Tests swingx rendering infrastructure: RenderingXController, CellContext, 
 * ..
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class RenderingTest extends TestCase {
    private static final Logger LOG = Logger.getLogger(RenderingTest.class
            .getName());
    
    
    /**
     * Test provider property reset: borderPainted.
     *
     */
    public void testButtonProviderBorderPainted() {
        ButtonProvider provider = new ButtonProvider();
        TableCellContext context = new TableCellContext();
        AbstractButton button = provider.getRendererComponent(context);
        assertEquals(provider.isBorderPainted(), button.isBorderPainted());
        button.setBorderPainted(!provider.isBorderPainted());
        provider.getRendererComponent(context);
        assertEquals(provider.isBorderPainted(), button.isBorderPainted());
    }
    /**
     * Test provider property reset: horizontal.
     *
     */
    public void testButtonProviderHorizontalAlignment() {
        ButtonProvider provider = new ButtonProvider();
        CellContext context = new TableCellContext();
        AbstractButton button = provider.getRendererComponent(context);
        assertEquals(provider.getHorizontalAlignment(), button.getHorizontalAlignment());
        button.setHorizontalAlignment(JLabel.TRAILING);
        provider.getRendererComponent(context);
        assertEquals(provider.getHorizontalAlignment(), button.getHorizontalAlignment());
    }
   /**
     * use convenience constructor where appropriate: 
     * test clients code (default renderers in JXTable).
     * 
     *
     */
    public void testConstructorClients() {
        JXTable table = new JXTable();
        // Number
        DefaultTableRenderer numberRenderer = (DefaultTableRenderer) table.getDefaultRenderer(Number.class);
        JLabel label = (JLabel) numberRenderer.getTableCellRendererComponent(table, null, false, false, 0, 0);
        assertEquals(JLabel.RIGHT, label.getHorizontalAlignment());
        assertEquals(FormatStringValue.NUMBER_TO_STRING, numberRenderer.componentController.getToStringConverter());
        // icon
        DefaultTableRenderer iconRenderer = (DefaultTableRenderer) table.getDefaultRenderer(Icon.class);
        JLabel iconLabel = (JLabel) iconRenderer.getTableCellRendererComponent(table, null, false, false, 0, 0);
        assertEquals(JLabel.CENTER, iconLabel.getHorizontalAlignment());
        // JW: wrong assumption after starting to fix #590-swingx
        // LabelProvider should respect formatter
//        assertEquals(StringValue.TO_STRING, iconRenderer.componentController.getToStringConverter());
    }
    
    public void testConstructorButtonProvider() {
        ComponentProvider provider = new ButtonProvider();
        assertEquals(JLabel.CENTER, provider.getHorizontalAlignment());
        assertEquals(StringValue.TO_STRING, provider.getToStringConverter());
       
    }
    /**
     * Test constructors: convenience constructor.
     */
    public void testConstructorConvenience() {
        FormatStringValue sv = new FormatStringValue(DateFormat.getTimeInstance());
        int align = JLabel.RIGHT;
        LabelProvider provider = new LabelProvider(sv, align);
        assertEquals(align, provider.getHorizontalAlignment());
        assertEquals(sv, provider.getToStringConverter());
    }
    
    /**
     * Test constructors: parameterless.
     */
    public void testConstructorDefault() {
        LabelProvider provider = new LabelProvider();
        assertEquals(JLabel.LEADING, provider.getHorizontalAlignment());
        assertEquals(StringValue.TO_STRING, provider.getToStringConverter());
    }
    
    /**
     * Test constructors: convenience constructor.
     */
    public void testConstructorAlignment() {
        int align = JLabel.RIGHT;
        LabelProvider provider = new LabelProvider(align);
        assertEquals(align, provider.getHorizontalAlignment());
        assertEquals(StringValue.TO_STRING, provider.getToStringConverter());
    }
    
    /**
     * Test constructors: convenience constructor.
     */
    public void testConstructorStringValue() {
        FormatStringValue sv = new FormatStringValue(DateFormat.getTimeInstance());
        LabelProvider provider = new LabelProvider(sv);
        assertEquals(JLabel.LEADING, provider.getHorizontalAlignment());
        assertEquals(sv, provider.getToStringConverter());
    }

    /**
     * test that default visual config clears the tooltip.
     *
     */
    public void testTooltipReset() {
        DefaultVisuals<JComponent> visuals = new DefaultVisuals<JComponent>();
        JComponent label = new  JLabel("somevalue");
        label.setToolTipText("tooltip");
        visuals.configureVisuals(label, new TableCellContext());
        assertNull("default visual config must clear tooltiptext", label.getToolTipText());
    }
    
    /**
     * Test if all collaborators can cope with null component on CellContext.
     *
     */
    public void testEmptyContext() {
        // test LabelProvider
        // same for list and table
        assertEmptyContext(new LabelProvider());
        assertEmptyContext(new ButtonProvider());
        assertEmptyContext(new HyperlinkProvider());
    }
    
    private void assertEmptyContext(ComponentProvider provider) {
        DefaultListRenderer renderer = new DefaultListRenderer(provider);
        renderer.getListCellRendererComponent(null, null, -1, false, false);
        // treeRenderer - use the same provider, can't do in real life, 
        // the providers component is added to the wrapping provider's component.
        DefaultTreeRenderer treeRenderer = new DefaultTreeRenderer(provider);
        treeRenderer.getTreeCellRendererComponent(null, null, false, false, false, -1, false);
        // had an NPE in TreeCellContext focus border 
        treeRenderer.getTreeCellRendererComponent(null, null, false, false, false, -1, true);
        // random test - the input parameters don't map to a legal state
        treeRenderer.getTreeCellRendererComponent(null, new Object(), false, true, false, 2, true);
    }
    /**
     * Test doc'ed constructor behaviour of default tree renderer.
     *
     */
    public void testDefaultTreeRendererConstructors() {
        DefaultTreeRenderer renderer = new DefaultTreeRenderer();
        assertTrue(renderer.componentController instanceof WrappingProvider);
        renderer = new DefaultTreeRenderer(FormatStringValue.DATE_TO_STRING);
        assertTrue(renderer.componentController instanceof WrappingProvider);
        // wrong assumption - we are wrapping...
//        assertSame(FormatStringValue.DATE_TO_STRING, renderer.componentController.formatter);
        assertSame(FormatStringValue.DATE_TO_STRING, ((WrappingProvider) renderer.componentController).wrappee.formatter);
        ComponentProvider controller = new ButtonProvider();
        renderer = new DefaultTreeRenderer(controller);
        assertSame(controller, renderer.componentController);
    }

    /**
     * Test doc'ed constructor behaviour of default list renderer.
     *
     */
    public void testDefaultListRendererConstructors() {
        DefaultListRenderer renderer = new DefaultListRenderer();
        assertTrue(renderer.componentController instanceof LabelProvider);
        renderer = new DefaultListRenderer(FormatStringValue.DATE_TO_STRING);
        assertTrue(renderer.componentController instanceof LabelProvider);
        assertSame(FormatStringValue.DATE_TO_STRING, renderer.componentController.formatter);
        ComponentProvider controller = new ButtonProvider();
        renderer = new DefaultListRenderer(controller);
        assertSame(controller, renderer.componentController);
    }

    /**
     * Test doc'ed constructor behaviour of default table renderer.
     *
     */
    public void testDefaultTableRendererConstructors() {
        DefaultTableRenderer renderer = new DefaultTableRenderer();
        assertTrue(renderer.componentController instanceof LabelProvider);
        renderer = new DefaultTableRenderer(FormatStringValue.DATE_TO_STRING);
        assertTrue(renderer.componentController instanceof LabelProvider);
        assertSame(FormatStringValue.DATE_TO_STRING, renderer.componentController.formatter);
        ComponentProvider controller = new ButtonProvider();
        renderer = new DefaultTableRenderer(controller);
        assertSame(controller, renderer.componentController);
    }

    /**
     * public methods of <code>ComponentProvider</code> must cope
     * with null context. Here: test getRenderingComponent in WrappingProvider.
     *
     */
    public void testGetWrappingComponentNullContext() {
        WrappingProvider provider = new WrappingProvider();
        assertEquals(provider.rendererComponent, provider.getRendererComponent(null));
    }

    /**
     * public methods of <code>ComponentProvider</code> must cope
     * with null context. Here: test getRenderingComponent in LabelProvider.
     */
    public void testGetComponentNullContext() {
        ComponentProvider controller = new LabelProvider();
        assertEquals(controller.rendererComponent, controller.getRendererComponent(null));
    }
    /**
     * public methods of <code>ComponentProvider</code> must cope
     * with null context. Here: test getRenderingComponent.
     */
    public void testStringValueNullContext() {
        ComponentProvider controller = new LabelProvider();
        controller.getValueAsString(null);
    }
    
    /**
     * test doc'ed behaviour on defaultVisuals configure:
     * NPE on null context.
     *
     */
    public void testConfigureVisualsNullContext() {
        DefaultVisuals<JLabel> controller = new DefaultVisuals<JLabel>();
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
     * test doc'ed behaviour on defaultVisuals configure:
     * NPE on null component.
     *
     */
    public void testConfigureVisualsNullComponent() {
        DefaultVisuals<JLabel> controller = new DefaultVisuals<JLabel>();
        try {
            controller.configureVisuals(null, new TableCellContext());
            fail("renderer controller must throw NPE on null component");
        } catch (NullPointerException e) {
            // this is what we expect
        } catch (Exception e) {
            fail("renderer controller must throw NPE on null component - instead: " + e);
        }
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
