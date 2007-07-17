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
package org.jdesktop.swingx;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.plaf.basic.BasicComboBoxEditor;

/**
 * Known issues of <code>JXDatePicker</code>.
 * 
 * @author Jeanette Winzenburg
 */
public class JXDatePickerIssues extends InteractiveTestCase {

    /**
     * Allowed to set a null editor? No (which is reasonable) 
     * - should be documented as of SwingX doc convention.
      */
    public void testEditorNull() {
       JXDatePicker picker = new JXDatePicker();
       assertNotNull(picker.getEditor());
       picker.setEditor(null);
    }

    /**
     * Issue ??-swingx: editor value must preserve value on LF switch.
     * This is a side-effect of picker not updating the editor's value
     * on setEditor.
     *
     */
    public void testEditorUpdateOnLF() {
        JXDatePicker picker = new JXDatePicker();
        Object date = picker.getEditor().getValue();
        picker.updateUI();
        assertEquals(date, picker.getEditor().getValue());
    }

    /**
     * Issue ??-swingx: editor value not updated after setEditor
     * who should set it? ui-delegate when listening to editor property change?
     * or picker in setEditor?
     * 
     * Compare to JComboBox: BasicComboUI listens to editor change, does internal
     * wiring to editor and call's comboBox configureEditor with the value of the 
     * old editor.
     */
    public void testEditorValueOnSetEditor() {
        JXDatePicker picker = new JXDatePicker();
        Object value = picker.getEditor().getValue();
        picker.setEditor(new JFormattedTextField(new JXDatePickerFormatter()));
        assertEquals(value, picker.getEditor().getValue());
    }
    
    /**
     * For comparison: behaviour of JComboBox on setEditor.
     *
     */
    public void testEditorValueOnCombo() {
        String[] items = new String[]{"one", "two"};
        JComboBox box = new JComboBox(items);
        box.setEditable(true);
        Object value = box.getEditor().getItem();
        // sanity
        assertEquals(items[0], value);
        ComboBoxEditor editor = new BasicComboBoxEditor();
        box.setEditor(editor);
        assertEquals(value, box.getEditor().getItem());
    }
    
//    @Override
//    protected void setUp() throws Exception {
//        defaultToSystemLF = true;
//        setSystemLF(defaultToSystemLF);
//    }
//    
    
}
