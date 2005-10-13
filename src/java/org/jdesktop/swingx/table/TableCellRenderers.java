/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
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

package org.jdesktop.swingx.table;

import java.util.Date;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.LinkModel;

public class TableCellRenderers {

    private static HashMap typeMap;

    static {
        // load the handler map with classes designed to handle the
        // type-specific rendering
        typeMap = new HashMap();
        typeMap.put(Number.class,
            "org.jdesktop.swingx.JXTable$NumberRenderer");
        typeMap.put(Double.class,
            "org.jdesktop.swingx.JXTable$DoubleRenderer");
        typeMap.put(Float.class,
            "org.jdesktop.swingx.JXTable$DoubleRenderer");
        typeMap.put(Date.class,
                    "org.jdesktop.swingx.JXTable$DateRenderer");
        typeMap.put(Icon.class,
                    "org.jdesktop.swingx.JXTable$IconRenderer");
        typeMap.put(Boolean.class,
            "org.jdesktop.swingx.JXTable$BooleanRenderer");
        typeMap.put(LinkModel.class,
                    "org.jdesktop.swingx.LinkRenderer");

    }

    private static String getRendererClassName(Class columnClass) {
        String rendererClassName = (String) typeMap.get(columnClass);
        return rendererClassName != null ? rendererClassName :
            "javax.swing.table.DefaultTableCellRenderer";
    }

    /**
     * @see #getNewDefaultRenderer
     * @param columnClass Class of value being rendered
     * @param rendererClassName String containing the class name of the renderer which
     *        should be returned for the specified column class
     */
    public static void setDefaultRenderer(Class columnClass, String rendererClassName) {
        typeMap.put(columnClass, rendererClassName);
    }

    /**
     * Returns a new instance of the default renderer for the specified class.
     * This differs from JTable:getDefaultRenderer() in that it returns a new
     * instance each time so that the renderer may be set and customized for
     * a particular column.
     *
     * @param columnClass Class of value being rendered
     * @return TableCellRenderer instance which renders values of the specified type
     */
    public static TableCellRenderer getNewDefaultRenderer(Class columnClass) {
        TableCellRenderer renderer = null;
        String rendererClassName = getRendererClassName(columnClass);
        try {
            Class rendererClass = Class.forName(rendererClassName);
            renderer = (TableCellRenderer) rendererClass.newInstance();
        }
        catch (Exception e) {
            renderer = new DefaultTableCellRenderer();
        }
        return renderer;
    }

    TableCellRenderers() {
    }

}
