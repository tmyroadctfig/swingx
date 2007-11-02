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

import java.awt.Component;
import java.awt.Font;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.test.XTestUtils;

/**
 * Test around known issues of SwingX renderers. <p>
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 * 
 * @author Jeanette Winzenburg
 */
public class RendererIssues extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(RendererIssues.class
            .getName());


    /**
     * test if renderer properties are updated on LF change. <p>
     * Note: this can be done examplary only. Here: we use the 
     * font of a rendererComponent returned by a HyperlinkProvider for
     * comparison. There's nothing to test if the font are equal
     * in System and crossplattform LF. <p>
     * 
     * There are spurious problems when toggling UI (since when?) 
     * with LinkRenderer
     * "no ComponentUI class for: org.jdesktop.swingx.LinkRenderer$1"
     * that's the inner class JXHyperlink which overrides updateUI.
     * 
     * PENDING: this was moved from tableUnitTest - had been passing with
     * LinkRenderer but with HyperlinkProvider
     * now is failing (on server with defaultToSystem == false, locally win os 
     * with true), probably due to slightly different setup now 
     * in renderer defaultVisuals? It resets the font to table's which
     * LinkRenderer didn't. Think whether to change the provider go back
     * to hyperlink font? 
     */
    public void testUpdateRendererOnLFChange() {
        boolean defaultToSystemLF = true;
        setSystemLF(defaultToSystemLF);
        TableCellRenderer comparison = new DefaultTableRenderer(new HyperlinkProvider());
        TableCellRenderer linkRenderer = new DefaultTableRenderer(new HyperlinkProvider());
        JXTable table = new JXTable(2, 3);
        Component comparisonComponent = comparison.getTableCellRendererComponent(table, null, false, false, 0, 0);
        Font comparisonFont = comparisonComponent.getFont();
        table.getColumnModel().getColumn(0).setCellRenderer(linkRenderer);
        setSystemLF(!defaultToSystemLF);
        SwingUtilities.updateComponentTreeUI(comparisonComponent);
        if (comparisonFont.equals(comparisonComponent.getFont())) {
            LOG.info("cannot run test - equal font " + comparisonFont);
            return;
        }
        SwingUtilities.updateComponentTreeUI(table);
        Component rendererComp = table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0);
        assertEquals("renderer font must be updated", 
                comparisonComponent.getFont(), rendererComp.getFont());
        
    }

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



}
