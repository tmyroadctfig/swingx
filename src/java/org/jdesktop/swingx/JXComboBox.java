/*
 * $Id$
 *
 * Copyright 2010 Sun Microsystems, Inc., 4150 Network Circle,
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

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * An enhanced {@code JComboBox} that provides the following additional functionality:
 * <p>
 * Auto-starts edits correctly for AutoCompletion when inside a {@code JTable}. A normal {@code
 * JComboBox} fails to recognize the first key stroke when it has been
 * {@link org.jdesktop.swingx.autocomplete.AutoCompleteDecorator#decorate(JComboBox) decorated}.
 * 
 * @author Karl Schaefer
 * @author Jeanette Winzenburg
 */
@SuppressWarnings("serial")
public class JXComboBox extends JComboBox {
    private List<KeyEvent> pendingEvents = new ArrayList<KeyEvent>();

    private boolean isDispatching;

    /**
     * Creates a <code>JXComboBox</code> with a default data model. The default data model is an
     * empty list of objects. Use <code>addItem</code> to add items. By default the first item in
     * the data model becomes selected.
     * 
     * @see DefaultComboBoxModel
     */
    public JXComboBox() {
    }

    /**
     * Creates a <code>JXComboBox</code> that takes its items from an existing
     * <code>ComboBoxModel</code>. Since the <code>ComboBoxModel</code> is provided, a combo box
     * created using this constructor does not create a default combo box model and may impact how
     * the insert, remove and add methods behave.
     * 
     * @param aModel
     *            the <code>ComboBoxModel</code> that provides the displayed list of items
     * @see DefaultComboBoxModel
     */
    public JXComboBox(ComboBoxModel aModel) {
        super(aModel);
    }

    /**
     * Creates a <code>JXComboBox</code> that contains the elements in the specified array. By
     * default the first item in the array (and therefore the data model) becomes selected.
     * 
     * @param items
     *            an array of objects to insert into the combo box
     * @see DefaultComboBoxModel
     */
    public JXComboBox(Object[] items) {
        super(items);
    }

    /**
     * Creates a <code>JXComboBox</code> that contains the elements in the specified Vector. By
     * default the first item in the vector (and therefore the data model) becomes selected.
     * 
     * @param items
     *            an array of vectors to insert into the combo box
     * @see DefaultComboBoxModel
     */
    public JXComboBox(Vector<?> items) {
        super(items);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean processKeyBinding(KeyStroke ks, final KeyEvent e, int condition,
            boolean pressed) {
        boolean retValue = super.processKeyBinding(ks, e, condition, pressed);

        if (!retValue && editor != null) {
            if (isStartingCellEdit(e)) {
                pendingEvents.add(e);
            } else if (pendingEvents.size() == 2) {
                pendingEvents.add(e);
                isDispatching = true;

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            for (KeyEvent event : pendingEvents) {
                                editor.getEditorComponent().dispatchEvent(event);
                            }

                            pendingEvents.clear();
                        } finally {
                            isDispatching = false;
                        }
                    }
                });
            }
        }
        return retValue;
    }

    private boolean isStartingCellEdit(KeyEvent e) {
        if (isDispatching) {
            return false;
        }

        JTable table = (JTable) SwingUtilities.getAncestorOfClass(JTable.class, this);
        boolean isOwned = table != null
                && !Boolean.FALSE.equals(table.getClientProperty("JTable.autoStartsEdit"));

        return isOwned && e.getComponent() == table;
    }

}
