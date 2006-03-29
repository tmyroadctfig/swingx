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
package org.jdesktop.swingx.color;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import org.jdesktop.swingx.JXGradientChooser;

/**
 * TODO May want to refactor and move to another package -- this is mostly 
 * reusable and doesn't really belong in the color package
 *
 * @author joshy
 */
public class EnumComboBoxModel extends AbstractListModel implements ComboBoxModel {
    Object selected = null;

    public EnumComboBoxModel() {
	selected = JXGradientChooser.GradientStyle.values()[0];
    }

    public int getSize() {
	return JXGradientChooser.GradientStyle.values().length;
    }

    public Object getElementAt(int index) {
	return JXGradientChooser.GradientStyle.values()[index];
    }

    public void setSelectedItem(Object anItem) {
	selected = anItem;
    }
    
    public Object getSelectedItem() {
	return selected;
    }
}