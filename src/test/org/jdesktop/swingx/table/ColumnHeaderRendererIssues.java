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
package org.jdesktop.swingx.table;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalBorders;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.test.XTestUtils;
import org.jdesktop.swingx.util.JVM;
import org.jdesktop.swingx.util.OS;

/**
 * Test for known issues with ColumnHeaderRenderer.
 * 
 * @author Jeanette Winzenburg
 */
public class ColumnHeaderRendererIssues extends InteractiveTestCase {
    private static final Logger LOG = Logger
            .getLogger(ColumnHeaderRendererIssues.class.getName());
    public static void main(String args[]) {
        ColumnHeaderRendererIssues test = new ColumnHeaderRendererIssues();
        setSystemLF(true);
        try {
          test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*LF.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }

    /**
     * Issue #337-swingx: test hack around Metals tableHeaderBorder not respecting
     * insets.
     * 
     * ColumnHeaderRendererAddon should install a  custom border.
     * 
     * Here: test that the addon installs/uninstalls a border as uiresource.
     * 
     * 
     */
    public void testMetalHeaderBorderDefault() throws Exception {
        LookAndFeel old = UIManager.getLookAndFeel();
        // force addon loading
        @SuppressWarnings("unused")
        ColumnHeaderRenderer renderer = new ColumnHeaderRenderer();
        // be sure to install cross-platform LF 
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            if (hasMetalBordersTableHeaderBorder()) {
                Border border = UIManager.getBorder(ColumnHeaderRenderer.METAL_BORDER_HACK);
                assertNotNull("metal hack border must be installed", border);
                assertTrue("metal hack border must be UIResource", border instanceof UIResource);
                // be sure to install crossplatform LF
                // not really safe - blows if system == cross-platform
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                Border noborder = UIManager.getBorder(ColumnHeaderRenderer.METAL_BORDER_HACK);
//                assertNull("metal hack border must be un-installed", noborder);
            } else {
                LOG.info("metalHaderBorderHack not run: cross-platform does not default the MetalBorders");
            }
        } finally {
            // revert to default LF
            UIManager.setLookAndFeel(old);
        }
    }

    /**
     * @return
     */
    private boolean hasMetalBordersTableHeaderBorder() {
        return UIManager.getBorder("TableHeader.cellBorder") instanceof MetalBorders.TableHeaderBorder;
    }

    /**
     * Test hack around Vista's tableHeader too big.
     * ColumnHeaderRendererAddon should install a smaller custom border on
     * Vista and jdk5. Core issue is fixed in jdk6 (u10, but as that's final now ...)
     * 
     * Here: test that the addon installs/uninstalls a border as uiresource.
     * 
     * Hmm ... how to test against different windows versions? Classic/(XP?) under
     * the hood of Vista?
     * 
     */
    public void testVistaHeaderBorderDefault() throws Exception {
        if (!OS.isWindowsVista() || 
            !UIManager.getSystemLookAndFeelClassName().contains("Windows")) {
            LOG.info("cannot run VistaHeaderBorder - no Vista system");
            return;
        }
        if (!JVM.current().isOneDotFive()) {
            LOG.info("cannot run VistaHeaderBorder - no jvm1.5");
            return;
        }
        LookAndFeel old = UIManager.getLookAndFeel();
        // force addon loading
        @SuppressWarnings("unused")
        ColumnHeaderRenderer renderer = new ColumnHeaderRenderer();
        // be sure to install system LF
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Border border = UIManager.getBorder(ColumnHeaderRenderer.VISTA_BORDER_HACK);
            assertNotNull("vista hack border must be installed", border);
            assertTrue("vista hack border must be UIResource", border instanceof UIResource);
            // be sure to install crossplatform LF
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            Border noborder = UIManager.getBorder(ColumnHeaderRenderer.VISTA_BORDER_HACK);
            assertNull("vista hack border must be un-installed", noborder);
        } finally {
            // revert to default LF
            UIManager.setLookAndFeel(old);
        }
    }

    /**
     * test hack around Vista's tableHeader too big.
     * ColumnHeaderRendererAddon should install a smaller custom border.
     * 
     * Here: test that addon doesn't touch a custom border.
     * 
     * Hmm ... how to test against different windows versions? Classic/(XP?) under
     * the hood of Vista?
     * 
     */
    public void testVistaHeaderBorderCustom() throws Exception {
        if (!OS.isWindowsVista() || 
            !UIManager.getSystemLookAndFeelClassName().contains("Windows")) {
            LOG.info("cannot run VistaHeaderBorder - no Vista system");
            return;
        }
        if (!JVM.current().isOneDotFive()) {
            LOG.info("cannot run VistaHeaderBorder - no jvm1.5");
            return;
        }
        LookAndFeel old = UIManager.getLookAndFeel();
        // force addon loading
        @SuppressWarnings("unused")
        ColumnHeaderRenderer renderer = new ColumnHeaderRenderer();
        // be sure to install system LF
        try {
            Border customBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
            UIManager.put(ColumnHeaderRenderer.VISTA_BORDER_HACK, customBorder);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            assertEquals("custom border must be untouched", 
                    customBorder,
                    UIManager.getBorder(ColumnHeaderRenderer.VISTA_BORDER_HACK));
            // be sure to install crossplatform LF
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            assertSame("custom border must be untouched", 
                    UIManager.getBorder(ColumnHeaderRenderer.VISTA_BORDER_HACK),
                    customBorder);
        } finally {
            // revert to default LF
            UIManager.put(ColumnHeaderRenderer.VISTA_BORDER_HACK, null);
            UIManager.setLookAndFeel(old);
        }
    }
    
    /**
     * Vista border is kept when switching (on OS level) between vista
     * and classic mode.
     * 
     * TODO check on XP for core bug: 
     * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6429812
     * here no problem, JXTableHeader tries hard to update LF-provided
     * renderer.
     */
    public void interactiveLFHeaderRenderer() {
        final JXTable table = new JXTable(20, 5);
        JTable core = new JTable(20, 5);
        final JXFrame frame = wrapWithScrollingInFrame(table, core, "LF provided renderer");
        Action toggleLF = new AbstractActionExt("toggleLF") {

            public void actionPerformed(ActionEvent e) {
                try {
                    toggleLAF();
                    SwingUtilities.updateComponentTreeUI(frame);
                } catch (Exception e1) {
                    LOG.info("error when toggling LF - ignore");
                } finally {
                    LOG.info("current LF: " + UIManager.getLookAndFeel());
                    
                }
                
            }
            
        };
        Action reloadLF = new AbstractActionExt("reloadLF") {

            public void actionPerformed(ActionEvent e) {
                SwingUtilities.updateComponentTreeUI(frame);
            }
            
        };
        
        addAction(frame, toggleLF);
        addAction(frame, reloadLF);
        frame.pack();
        frame.setVisible(true);
    }
    
    private void toggleLAF() throws Exception {
        LookAndFeel laf = UIManager.getLookAndFeel();
        if (laf == null || laf.isNativeLookAndFeel()) { 
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } else {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
    }

    
    /**
     * Issue #??-jdnc: configure header/column to show icon.
     * 
     * Icon as header value not used. (Showing resource location)
     */
    public void interactiveHeaderIcon() {
        final JXTable table = new JXTable();
//        table.setAutoCreateRowSorter(false);
        ColumnFactory factory = new ColumnFactory() {

            @Override
            public void configureTableColumn(TableModel model, TableColumnExt columnExt) {
                super.configureTableColumn(model, columnExt);
                if (columnExt.getModelIndex() == 1) {
                    // default can't cope
                    columnExt.setHeaderValue(XTestUtils.loadDefaultIcon());
                }
            }
            
        };
        table.setColumnFactory(factory);
        table.setModel(new DefaultTableModel(new String[]{"first", "second", "third"}, 10));
        JScrollPane pane = new JScrollPane(table);
       table.setColumnControlVisible(true);
       final JXFrame frame = wrapInFrame(pane, "Icon as column header value (not mapped)");
       frame.setVisible(true);

    }


    /**
     * Issue #??-jdnc: configure header/column to show icon.
     * 
     * We try to set an icon on second column only. We see the icon on
     * all columns.
     * 
     * All:
     * don't handle header values of icon type by default
     * label properties not reset on each call (which produces
     * problems with the shared ui-provided renderer) 
     *  
     * Mustang:
     * duplicate sort icon (when using swingx sorting)
     * custom icon overpaints sort icon (when using 1.6 sorting) 
     * 
     * Note: this method makes sense only if 
     * compiled, run under 1.6 (and the Mustang api uncommented)
     */
    public void interactiveHeaderIconMustang() {
        final JXTable table = new JXTable();
        // un-comment to see Mustang effect 
//        table.setSortable(false);
//        table.setAutoCreateRowSorter(true);
        ColumnFactory factory = new ColumnFactory() {

            @Override
            public void configureTableColumn(TableModel model, TableColumnExt columnExt) {
                super.configureTableColumn(model, columnExt);
                if (columnExt.getModelIndex() == 1) {
                    // default can't cope
//                    columnExt.setHeaderValue(XTestUtils.loadDefaultIcon());
//                    columnExt.setHeaderValue(null);
                    // not working - column renderer doesn't reset shared renderers state!
                    ColumnHeaderRenderer renderer = new ColumnHeaderRenderer(table.getTableHeader());
                    renderer.setIcon(XTestUtils.loadDefaultIcon());
                    columnExt.setHeaderRenderer(renderer);
                }
            }
            
        };
        table.setColumnFactory(factory);
        table.setModel(new DefaultTableModel(new String[]{"first", "second", "third"}, 10));
        JScrollPane pane = new JScrollPane(table);
       table.setColumnControlVisible(true);
       showInFrame(pane, "Icon on second header - Mustang sort icon");
    }
    /**
     * Issue #79-jdnc: leading/trailing (?) doesn't work.
     *
     */
    public void interactiveHeaderAlignment() {
        final String[] alignText = {"default", "center", "leading", "left", "trailing", "right"};
        final int[] align = {-1, JLabel.CENTER, JLabel.LEADING, JLabel.LEFT, JLabel.TRAILING, JLabel.RIGHT};
        ColumnFactory factory = new ColumnFactory() {

            @Override
            public void configureTableColumn(TableModel model, TableColumnExt columnExt) {
                super.configureTableColumn(model, columnExt);
                int columnIndex = columnExt.getModelIndex();
                columnExt.setHeaderValue(alignText[columnIndex]);
                ColumnHeaderRenderer renderer = ColumnHeaderRenderer.createColumnHeaderRenderer();
                if (align[columnIndex] >= 0) {
                   renderer.setHorizontalAlignment(align[columnIndex]); 
                }
                columnExt.setHeaderRenderer(renderer);
            }
            
        };
        final JXTable table = new JXTable();
        table.setColumnFactory(factory);
        table.setModel(new DefaultTableModel(10, alignText.length));
        JScrollPane pane = new JScrollPane(table);
       table.setColumnControlVisible(true);
       final JXFrame frame = wrapInFrame(pane, "RToL and column text alignment, trailing/leading");
       addComponentOrientationToggle(frame);
       show(frame);

    }

    /**
     * Hook into the UI-provided renderer to set the
     * alignment of all columns. Note: this is not 
     * bidi compliant, as of Issue #79-jdnc: leading/trailing 
     * don't get updated on orientation toggle.
     *
     */
    public void interactiveHeaderAlignmentAllRight() {
        final JXTable table = new JXTable();
        JXTableHeader header = (JXTableHeader) table.getTableHeader();
        TableCellRenderer renderer = header.getDefaultRenderer();
        if (renderer instanceof ColumnHeaderRenderer) {
            ColumnHeaderRenderer columnRenderer = (ColumnHeaderRenderer) renderer;
            columnRenderer.setHorizontalAlignment(JLabel.RIGHT);
        }
        table.setModel(new DefaultTableModel(10, 6));
        JScrollPane pane = new JScrollPane(table);
       table.setColumnControlVisible(true);
       final JXFrame frame = wrapInFrame(pane, "RToL and column text alignment RIGHT");
       addComponentOrientationToggle(frame);
       show(frame);

    }

    /**
     * Do nothing, make the test runner happy
     * (would output a warning without a test fixture).
     *
     */
    public void testDummy() {
        
    }

}
