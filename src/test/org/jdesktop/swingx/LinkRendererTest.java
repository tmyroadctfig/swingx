/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Point;
import java.net.URL;

import javax.swing.table.TableCellRenderer;

import junit.framework.TestCase;

import org.jdesktop.swingx.util.Link;

/**
 * @author Jeanette Winzenburg
 */
public class LinkRendererTest extends TestCase {

    private Link link;


    public void testRolloverRecognition() {
        JXTable table = new JXTable(1, 2);
        TableCellRenderer linkRenderer = new LinkRenderer();
        table.getColumnModel().getColumn(0).setCellRenderer(linkRenderer);
        JXHyperlink hyperlink = (JXHyperlink) linkRenderer
            .getTableCellRendererComponent(table, link, false, false, 1, 0);
        assertFalse("renderer must not be rollover", hyperlink.getModel().isRollover());
        table.putClientProperty(RolloverProducer.ROLLOVER_KEY, new Point(0, 1));
        hyperlink = (JXHyperlink) linkRenderer
        .getTableCellRendererComponent(table, link, false, false, 1, 0);
        assertTrue("renderer must be rollover", hyperlink.getModel().isRollover());
    }
    
    
    protected void setUp() throws Exception {
        super.setUp();
        URL url = getClass().getResource("resources/test.html");

        link = new Link("a resource", null, url);
    }
}
