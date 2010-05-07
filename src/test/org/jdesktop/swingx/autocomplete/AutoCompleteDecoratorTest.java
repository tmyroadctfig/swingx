/*
 * $Id$
 *
 * Copyright 2008 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.autocomplete;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JTextPane;
import javax.swing.text.JTextComponent;

import org.jdesktop.test.EDTRunner;
import org.jdesktop.test.SerializableSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Karl George Schaefer
 */
@RunWith(EDTRunner.class)
public class AutoCompleteDecoratorTest  {
    private JComboBox combo;
    
    @Before
    public void setUp() {
        combo = new JComboBox(new String[]{"Alpha", "Bravo", "Charlie", "Delta"});
    }
    
    /**
     * SwingX Issue #299.
     */
    @Test
    public void testUndecorateComboBox() {
        combo.setEditable(false);
        AutoCompleteDecorator.decorate(combo);
        AutoCompleteDecorator.undecorate(combo);
        
        for (PropertyChangeListener l : combo.getPropertyChangeListeners("editor")) {
            assertThat(l, is(not(instanceOf(AutoComplete.PropertyChangeListener.class))));
        }
        
        assertThat(combo.getEditor(), is(not(instanceOf(AutoCompleteComboBoxEditor.class))));
        
        JTextComponent editorComponent = (JTextComponent) combo.getEditor().getEditorComponent();
        
        for (KeyListener l : editorComponent.getKeyListeners()) {
            assertThat(l, is(not(instanceOf(AutoComplete.KeyAdapter.class))));
        }
        
        for (InputMap map = editorComponent.getInputMap(); map != null; map = map.getParent()) {
            assertThat(map, is(not(instanceOf(AutoComplete.InputMap.class))));
        }
        
        assertThat(editorComponent.getActionMap().get("nonstrict-backspace"), is(nullValue()));
        
        for (FocusListener l : editorComponent.getFocusListeners()) {
            assertThat(l, is(not(instanceOf(AutoComplete.FocusAdapter.class))));
        }
        
        assertThat(editorComponent.getDocument(), is(not(instanceOf(AutoCompleteDocument.class))));
        
        for (ActionListener l : combo.getActionListeners()) {
            assertThat(l, is(not(instanceOf(ComboBoxAdaptor.class))));
        }
        
    }
    
    /**
     * SwingX Issue #299.
     */
    @Test
    public void testRedecorateComboBox() {
        AutoCompleteDecorator.decorate(combo);
        Component editor = combo.getEditor().getEditorComponent();
        
        int expectedFocusListenerCount = editor.getFocusListeners().length;
        int expectedKeyListenerCount = editor.getKeyListeners().length;
        int expectedPropListenerCount = combo.getPropertyChangeListeners("editor").length;
        int expectedActionListenerCount = combo.getActionListeners().length;
        
        
        AutoCompleteDecorator.decorate(combo);
        editor = combo.getEditor().getEditorComponent();
        
        assertThat(editor.getFocusListeners().length, is(expectedFocusListenerCount));
        assertThat(editor.getKeyListeners().length, is(expectedKeyListenerCount));
        assertThat(combo.getPropertyChangeListeners("editor").length, is(expectedPropListenerCount));
        assertThat(combo.getActionListeners().length, is(expectedActionListenerCount));
    }
    
    @Test
    public void testDecoratingJTextPane() {
        List<String> strings = Arrays.asList("Alpha", "Bravo", "Charlie", "Delta");
        AutoCompleteDecorator.decorate(new JTextPane(), strings, true);
    }
    
    @Test
    public void testAddingItemsAfterDecorating() {
        AutoCompleteDecorator.decorate(combo);
        combo.addItem("Echo");
    }
    
    @Test
    public void testAddingItemsAfterDecoratingEmpty() {
        JComboBox box = new JComboBox();
        AutoCompleteDecorator.decorate(box);
        box.addItem("Alhpa");
    }
    
    @Test
    public void testRemovingItemsAfterDecorating() {
        AutoCompleteDecorator.decorate(combo);
        combo.removeAll();
    }
}
