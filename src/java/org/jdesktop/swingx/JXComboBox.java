/*
 * $Id$
 *
 * Copyright 2005 Sun Microsystems, Inc., 4150 Network Circle,
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

import java.awt.Insets;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicComboBoxEditor;

/**
 * @author rbair
 */
public class JXComboBox extends JComboBox {
    /**
     * Creates a <code>JComboBox</code> that takes it's items from an
     * existing <code>ComboBoxModel</code>.  Since the
     * <code>ComboBoxModel</code> is provided, a combo box created using
     * this constructor does not create a default combo box model and
     * may impact how the insert, remove and add methods behave.
     *
     * @param aModel the <code>ComboBoxModel</code> that provides the 
     * 		displayed list of items
     * @see DefaultComboBoxModel
     */
    public JXComboBox(ComboBoxModel aModel) {
        super(aModel);
    }

    /** 
     * Creates a <code>JComboBox</code> that contains the elements
     * in the specified array.  By default the first item in the array
     * (and therefore the data model) becomes selected.
     *
     * @param items  an array of objects to insert into the combo box
     * @see DefaultComboBoxModel
     */
    public JXComboBox(final Object items[]) {
        super(items);
    }

    /**
     * Creates a <code>JComboBox</code> that contains the elements
     * in the specified Vector.  By default the first item in the vector
     * and therefore the data model) becomes selected.
     *
     * @param items  an array of vectors to insert into the combo box
     * @see DefaultComboBoxModel
     */
    public JXComboBox(Vector<?> items) {
        super(items);
    }

    /**
     * Creates a <code>JComboBox</code> with a default data model.
     * The default data model is an empty list of objects.
     * Use <code>addItem</code> to add items.  By default the first item
     * in the data model becomes selected.
     *
     * @see DefaultComboBoxModel
     */
    public JXComboBox() {
        super();
    }
}
