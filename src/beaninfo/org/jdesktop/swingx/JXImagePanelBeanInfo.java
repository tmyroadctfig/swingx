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
package org.jdesktop.swingx;

import java.beans.PropertyDescriptor;
import org.jdesktop.swingx.JXImagePanel;

/**
 *
 * @author rbair
 */
public class JXImagePanelBeanInfo extends BeanInfoSupport {
    
    public JXImagePanelBeanInfo() {
        super(JXImagePanel.class);
    }

    protected void initialize() {
        PropertyDescriptor[] array = getPropertyDescriptors();
        for (int i=0; i<array.length; i++) {
            PropertyDescriptor pd = array[i];
//            if (pd.getName().equals("dataPath")) {
//                pd.setPreferred(true);
//                pd.setValue("category", "Data Binding");
//                pd.setPropertyEditorClass(DataPathSelectedEditor.class);
            if (pd.getName().equals("icon") 
                || pd.getName().equals("opaque")
                || pd.getName().equals("alpha")
                || pd.getName().equals("inheritAlpha")
                || pd.getName().equals("drawGradient")
                || pd.getName().equals("gradientPaint")) {
                pd.setPreferred(true);
            }
        }
        iconNameC16 = "jximagepanel16.png";
        iconNameC32 = "jximagepanel32.png";
    }
}
