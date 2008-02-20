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

import java.awt.Dimension;
import java.text.DateFormat;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.ShapePainter;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.test.XTestUtils;

/**
 * Tests swingx rendering infrastructure: ComponentProvider, CellContext, 
 * ..
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class RenderingTest extends TestCase {
    private static final Logger LOG = Logger.getLogger(RenderingTest.class
            .getName());


    /**
     * Issue #768-swingx: cleanup access to string representation of provider.
     * 
     */
    public void testLabelProviderGetString() {
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                return "funnyconstant ... haha";
            }
            
        };
        CellContext context =  new TableCellContext();
        ComponentProvider<JLabel> provider = new LabelProvider(sv);
        JLabel label = provider.getRendererComponent(context);
        assertEquals(sv.getString(context.getValue()), label.getText());
        assertEquals(sv.getString(context.getValue()), provider.getString(context.getValue()));
    }
    
    /**
     * Issue #768-swingx: cleanup access to string representation of provider.
     * 
     */
    public void testButtonProviderGetString() {
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                return "funnyconstant ... haha";
            }
            
        };
        CellContext context =  new TableCellContext();
        ComponentProvider<AbstractButton> provider = new ButtonProvider(sv);
        AbstractButton label = provider.getRendererComponent(context);
        assertEquals(sv.getString(context.getValue()), label.getText());
        assertEquals(sv.getString(context.getValue()), provider.getString(context.getValue()));
    }
    
    /**
     * Issue #768-swingx: cleanup access to string representation of provider.
     * 
     */
    public void testWrappingProviderGetString() {
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                return "funnyconstant ... haha";
            }
            
        };
        CellContext context =  new TableCellContext();
        ComponentProvider<WrappingIconPanel> provider = new WrappingProvider(sv);
        assertEquals(sv.getString(context.getValue()), provider.getString(context.getValue()));
    }
    
    /**
     * Issue #768-swingx: cleanup access to string representation of provider.
     * 
     */
    public void testWrappingProviderGetStringNotNullValue() {
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                return String.valueOf(value) + "added ... ";
            }
            
        };
        CellContext context =  new TableCellContext();
        context.value = "dummy";
        ComponentProvider<WrappingIconPanel> provider = new WrappingProvider(sv);
        assertEquals(sv.getString(context.getValue()), provider.getString(context.getValue()));
    }
    
    /**
     * Issue #769-swingx: support null icons.
     * 
     * enhance WrappingProvider to allow real null values. 
     * 
     */
    public void testWrappingProviderNullIcon() {
       CellContext context = new TreeCellContext();
       WrappingProvider provider = new WrappingProvider(IconValue.NONE);
       WrappingIconPanel comp = provider.getRendererComponent(context);
       assertEquals(null, comp.getIcon());
    }
    

    /**
     * Added pref/min/max size to list of properties which 
     * must be reset by the DefaultVisuals.
     */
    public void testResetPreferredSize() {
        DefaultVisuals<JComponent> visuals = new DefaultVisuals<JComponent>();
        JComponent label = new  JLabel("somevalue");
        visuals.configureVisuals(label, new TableCellContext());
        Dimension prefSize = label.getPreferredSize();
        Dimension newPrefSize = new Dimension(prefSize.width + 100, prefSize.height + 100);
        label.setPreferredSize(newPrefSize);
        visuals.configureVisuals(label, new TableCellContext());
        assertEquals("default visual config must reset prefSize", prefSize, label.getPreferredSize());
    }
    
    /**
     * Test provider respect converter. 
     * 
     * Here: must show the
     * description instead of setting the icon.
     *
     */
    public void testLabelProviderRespectStringValueNoIcon() {
        ImageIcon icon = (ImageIcon) XTestUtils.loadDefaultIcon();
        icon.setDescription("description");
        LabelProvider provider = new LabelProvider(
                new MappedValue(StringValue.TO_STRING, IconValue.EMPTY));
        TableCellContext context = new TableCellContext();
        context.value = icon;
        JLabel label = provider.getRendererComponent(context);
        assertNull("icon must be null", label.getIcon());
        assertEquals("label text must be default to-string", StringValue.TO_STRING.getString(icon), label.getText());
    }
    
    /**
     * Test provider respect converter. 
     * 
     * Here: must show the icon and empty text.
     *
     */
    public void testLabelProviderRespectIconValueNoString() {
        ImageIcon icon = (ImageIcon) XTestUtils.loadDefaultIcon();
        icon.setDescription("description");
        LabelProvider provider = new LabelProvider(
                new MappedValue(StringValue.EMPTY, IconValue.ICON));
        TableCellContext context = new TableCellContext();
        context.value = icon;
        JLabel label = provider.getRendererComponent(context);
        assertEquals(icon, label.getIcon());
        assertEquals("label text must be empty", StringValue.EMPTY.getString(icon), label.getText());
    }
    
    /**
     * Test provider respect converter. 
     * 
     * Here: must show both description and icon.
     *
     */
    public void testLabelProviderRespectStringIconValueBoth() {
        ImageIcon icon = (ImageIcon) XTestUtils.loadDefaultIcon();
        icon.setDescription("description");
        LabelProvider provider = new LabelProvider(
                new MappedValue(StringValue.TO_STRING, IconValue.ICON));
        TableCellContext context = new TableCellContext();
        context.value = icon;
        JLabel label = provider.getRendererComponent(context);
        assertEquals(icon, label.getIcon());
        assertEquals(StringValue.TO_STRING.getString(icon), label.getText());
    }

    /**
     * WrappingProvider: test custom icon
     */
    public void testWrappingProviderIcon() {
        final Icon icon = XTestUtils.loadDefaultIcon();
        IconValue iv = new IconValue() {
            public Icon getIcon(Object value) {
                return icon;
            }};
        WrappingProvider provider = new WrappingProvider(iv);
        CellContext context = new TreeCellContext();
        WrappingIconPanel iconPanel = provider.getRendererComponent(context);
        assertEquals(icon, iconPanel.getIcon());
    }
    
    /**
     * WrappingProvider: test custom icon
     */
    public void testWrappingProviderIconAndContent() {
        final Icon icon = XTestUtils.loadDefaultIcon();
        IconValue iv = new IconValue() {
            public Icon getIcon(Object value) {
                return icon;
            }};
            
        WrappingProvider provider = new WrappingProvider(iv, FormatStringValue.DATE_TO_STRING);
        CellContext context = new TreeCellContext();
        WrappingIconPanel iconPanel = provider.getRendererComponent(context);
        assertEquals(icon, iconPanel.getIcon());
        
    }
    
    /**
     * WrappingProvider: 
     * test wrappee and its state after instantiation.
     */
    public void testWrappingProviderWrappee() {
        WrappingProvider provider = new WrappingProvider();
        // sanity ...
        assertTrue(provider.getWrappee() instanceof LabelProvider);
        assertEquals("wrappee's StringValue must be default", 
                new LabelProvider().getToStringConverter(),
                provider.getWrappee().getToStringConverter()); 
        StringValue sv = FormatStringValue.DATE_TO_STRING;
        WrappingProvider customStringValue = new WrappingProvider(sv);
        assertEquals("wrappee's StringValue must be configured to given", sv, 
                customStringValue.getWrappee().getToStringConverter());
        WrappingProvider iconValueAndCustomStringValue = new WrappingProvider(null, sv);
        assertEquals("wrappee's StringValue must be configured to given", sv, 
                iconValueAndCustomStringValue.getWrappee().getToStringConverter());
    }
    
    /**
     * WrappingProvider: 
     * test provider's own state after instantiation.
     */
    public void testWrappingProviderDefaults() {
        WrappingProvider provider = new WrappingProvider();
        assertTrue("default wrappee must be LabelProvider but was " + 
                     provider.getWrappee().getClass(), 
                provider.getWrappee() instanceof LabelProvider);
        assertEquals("default StringValue must be empty", StringValue.EMPTY, 
                provider.getToStringConverter());
    }
    
    /**
     * Test text and boolean taken from MappedValue
     */
    public void testButtonProviderCustomValue() {
        // some object to map
        String identifier = "dummyID";
        final TableColumnExt column = new TableColumnExt();
        column.setTitle(identifier);
        BooleanValue bv = new BooleanValue(){

            public boolean getBoolean(Object value) {
                return column.isVisible();
            }
            
        };
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                return column.getTitle();
            }
            
        };
        ButtonProvider provider = new ButtonProvider(new MappedValue(sv, null, bv));
        TableCellContext context = new TableCellContext();
        context.value = column;
        AbstractButton button = provider.getRendererComponent(context);
        assertEquals(column.isVisible(), button.isSelected());
        assertEquals(column.getTitle(), button.getText());
    }
    
    /**
     * safety net for addition of BooleanValue. Defaults to
     * selected from boolean value, text empty.
     * 
     * here: constructor with null stringValue and align
     *
     */
    public void testButtonProviderDefaultsTwoConstructor() {
        ButtonProvider provider = new ButtonProvider(null, JLabel.RIGHT);
        TableCellContext context = new TableCellContext();
        AbstractButton button = provider.getRendererComponent(context);
        // empty context
        assertFalse(button.isSelected());
        assertEquals("", button.getText());
        // boolean true
        context.value = true;
        provider.getRendererComponent(context);
        assertEquals(context.value, button.isSelected());
        assertEquals("", button.getText());
        // non-boolean
        context.value = "dummy";
        provider.getRendererComponent(context);
        assertFalse(button.isSelected());
        assertEquals("", button.getText());
    }
    

    /**
     * safety net for addition of BooleanValue. Defaults to
     * selected from boolean value, text empty.
     * 
     * here: constructor with null stringValue
     *
     */
    public void testButtonProviderDefaultsOneConstructor() {
        ButtonProvider provider = new ButtonProvider(null);
        TableCellContext context = new TableCellContext();
        AbstractButton button = provider.getRendererComponent(context);
        // empty context
        assertFalse(button.isSelected());
        assertEquals("", button.getText());
        // boolean true
        context.value = true;
        provider.getRendererComponent(context);
        assertEquals(context.value, button.isSelected());
        assertEquals("", button.getText());
        // non-boolean
        context.value = "dummy";
        provider.getRendererComponent(context);
        assertFalse(button.isSelected());
        assertEquals("", button.getText());
    }
    

    /**
     * safety net for addition of BooleanValue. Defaults to
     * selected from boolean value, text empty.
     * 
     * here: parameterless constructor
     *
     */
    public void testButtonProviderDefaultsEmptyConstructor() {
        ButtonProvider provider = new ButtonProvider();
        TableCellContext context = new TableCellContext();
        AbstractButton button = provider.getRendererComponent(context);
        // empty context
        assertFalse(button.isSelected());
        assertEquals("", button.getText());
        // boolean true
        context.value = true;
        provider.getRendererComponent(context);
        assertEquals(context.value, button.isSelected());
        assertEquals("", button.getText());
        // non-boolean
        context.value = "dummy";
        provider.getRendererComponent(context);
        assertFalse(button.isSelected());
        assertEquals("", button.getText());
    }

    /**
     * test ButtonProvider default constructor and properties.
     *
     */
    public void testButtonProviderConstructor() {
        ComponentProvider provider = new ButtonProvider();
        assertEquals(JLabel.CENTER, provider.getHorizontalAlignment());
        assertEquals(StringValue.EMPTY, provider.getToStringConverter());
       
    }

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
    public void testResetTooltip() {
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
     * with null context. Here: test getStringValue.
     */
    public void testStringValueNullContext() {
        ComponentProvider controller = new LabelProvider();
        controller.getStringValue(null);
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
