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

package org.jdesktop.swingx.editors;

import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingConstants;

/**
 *
 * @author rbair
 */
public class HorizontalAlignmentPropertyEditor extends PropertyEditorSupport {
    private Map<Integer,String> values = new HashMap<Integer,String>();

    /** Creates a new instance of Point2DPropertyEditor */
    public HorizontalAlignmentPropertyEditor() {
        values.put(SwingConstants.LEADING, "Leading");
        values.put(SwingConstants.LEFT, "Left");
        values.put(SwingConstants.CENTER, "Center");
        values.put(SwingConstants.RIGHT, "Right");
        values.put(SwingConstants.TRAILING, "Trailing");
    }

    public Integer getValue() {
        Object obj = super.getValue();
        return obj instanceof Integer ? (Integer)obj : SwingConstants.CENTER;
    }

    public String getJavaInitializationString() {
        return "SwingConstants." + values.get(getValue()).toUpperCase();
    }

    public String[] getTags() {
        return values.values().toArray(new String[0]);
    }
    
    public void setAsText(String text) throws IllegalArgumentException {
        Integer value = -1;
        
        if (text == null) {
            value = SwingConstants.CENTER;
        } else {
            for(Map.Entry<Integer,String> entry : values.entrySet()) {
                if (entry.getValue().equals(text)) {
                    value = entry.getKey();
                    break;
                }
            }
        }
        
        if (values.containsKey(value)) {
            setValue(value);
        } else {
            throw new IllegalArgumentException("Text " + text + " is not one of the valid values: " + values.keySet());
        }
    }

    public String getAsText() {
        return values.get(getValue());
    }
} 
