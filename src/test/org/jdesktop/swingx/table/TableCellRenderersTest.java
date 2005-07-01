/*
 * Created on 01.07.2005
 *
 */
package org.jdesktop.swingx.table;

import java.util.Date;

import javax.swing.Icon;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.LinkModel;
import org.jdesktop.swingx.LinkRenderer;

import junit.framework.TestCase;

/**
 * @author Jeanette Winzenburg
 */
public class TableCellRenderersTest extends TestCase {

    public void testLazyRenderersByClass() {
        assertEquals("default Boolean renderer", JXTable.BooleanRenderer.class, TableCellRenderers.getNewDefaultRenderer(Boolean.class).getClass());
        assertEquals("default Number renderer", JXTable.NumberRenderer.class, TableCellRenderers.getNewDefaultRenderer(Number.class).getClass());
        assertEquals("default Double renderer", JXTable.DoubleRenderer.class, TableCellRenderers.getNewDefaultRenderer(Double.class).getClass());
        assertEquals("default Date renderer", JXTable.DateRenderer.class, TableCellRenderers.getNewDefaultRenderer(Date.class).getClass());
        assertEquals("default LinkModel renderer", LinkRenderer.class, TableCellRenderers.getNewDefaultRenderer(LinkModel.class).getClass());
        assertEquals("default Icon renderer", JXTable.IconRenderer.class, TableCellRenderers.getNewDefaultRenderer(Icon.class).getClass());
    }

}
