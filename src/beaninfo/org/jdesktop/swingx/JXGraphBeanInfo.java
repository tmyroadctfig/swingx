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
 */
package org.jdesktop.swingx;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;
import org.jdesktop.swingx.editors.Point2DPropertyEditor;
import org.jdesktop.swingx.editors.Rectangle2DPropertyEditor;

/**
 * Bean info for {@link org.jdesktop.swingx.JXGraph} component.
 *
 * @author Romain Guy <romain.guy@mac.com>
 */
public class JXGraphBeanInfo extends BeanInfoSupport {
    // maps property names to their category
    private static final Map<String, String> categories =
        new HashMap<String, String>();
    static {
        categories.put("majorX", "Graph View");
        categories.put("majorY", "Graph View");
        categories.put("minorCountX", "Graph View");
        categories.put("minorCountY", "Graph View");
        categories.put("origin", "Graph View");
        categories.put("view", "Graph View");
        categories.put("axisColor", "Graph Appearance");
        categories.put("axisPainted", "Graph Appearance");
        categories.put("backgroundPainted", "Graph Appearance");
        categories.put("gridPainted", "Graph Appearance");
        categories.put("majorGridColor", "Graph Appearance");
        categories.put("minorGridColor", "Graph Appearance");
        categories.put("textPainted", "Graph Appearance");
        categories.put("inputEnabled", "Graph Input");
    }
    
    // maps property names to their property editor
    private static final Map<String, Class<? extends PropertyEditor>> editors =
        new HashMap<String, Class<? extends PropertyEditor>>();
    static {
        editors.put("origin", Point2DPropertyEditor.class);
        editors.put("view", Rectangle2DPropertyEditor.class);
    }
    
    // defines display names for some properties
    private static final Map<String, String> names = new HashMap<String, String>();
    static {
        names.put("majorX", "vertical lines spacing");
        names.put("majorY", "horizontal lines spacing");
        names.put("minorCountX", "sub-vertical lines count");
        names.put("minorCountY", "sub-horizontal lines count");
        names.put("majorGridColor", "major grid lines color");
        names.put("minorGridColor", "minor grid lines color");
    }
    
    public JXGraphBeanInfo() {
        super(JXGraph.class);
    }

    protected void initialize() {
        PropertyDescriptor[] array = getPropertyDescriptors();
        for (int i = 0; i < array.length; i++) {
            PropertyDescriptor pd = array[i];
            if (categories.containsKey(pd.getName())) {
                pd.setValue("category", categories.get(pd.getName()));
            }
            if (editors.containsKey(pd.getName())) {
                pd.setPropertyEditorClass(editors.get(pd.getName()));
            }
            if (names.containsKey(pd.getName())) {
                pd.setDisplayName(names.get(pd.getName()));
            }
            
            if (pd.getName().equals("viewAndOrigin")) {
                pd.setHidden(true);
            }
        }
        
        iconNameC16 = "jxgraph16.png";
        iconNameC32 = "jxgraph32.png";
    }
}
