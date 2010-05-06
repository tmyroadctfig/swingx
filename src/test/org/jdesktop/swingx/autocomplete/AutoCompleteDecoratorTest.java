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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextPane;

import org.jdesktop.test.EDTRunner;
import org.junit.Before;
import org.junit.Ignore;
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
    public void testDecorationFocusListeners() {
        Component editor = combo.getEditor().getEditorComponent();
        //current count plus 2 from UI delegate and 1 from AutoComplete
        int expectedFocusListenerCount = editor.getFocusListeners().length + 3;
        AutoCompleteDecorator.decorate(combo);
        assertThat(editor.getFocusListeners().length, is(expectedFocusListenerCount));
        
        //redecorating should not increase listener count
        AutoCompleteDecorator.decorate(combo);
        assertThat(editor.getFocusListeners().length, is(expectedFocusListenerCount));
    }
    
    /**
     * SwingX Issue #299.
     */
    @Test
    public void testDecorationKeyListeners() {
        Component editor = combo.getEditor().getEditorComponent();
        //current count 1 from AutoComplete
        int expectedKeyListenerCount = editor.getKeyListeners().length + 1;
        AutoCompleteDecorator.decorate(combo);
        assertThat(editor.getKeyListeners().length, is(expectedKeyListenerCount));
        
        //redecorating should not increase listener count
        AutoCompleteDecorator.decorate(combo);
        assertThat(editor.getKeyListeners().length, is(expectedKeyListenerCount));
    }
    
    /**
     * SwingX Issue #299.
     */
    @Test
    public void testDecorationPropertyListeners() {
        //current count 1 from AutoComplete
        int expectedPropListenerCount = combo.getPropertyChangeListeners("editor").length + 1;
        AutoCompleteDecorator.decorate(combo);
        assertThat(combo.getPropertyChangeListeners("editor").length, is(expectedPropListenerCount));
        
        //redecorating should not increase listener count
        AutoCompleteDecorator.decorate(combo);
        assertThat(combo.getPropertyChangeListeners("editor").length, is(expectedPropListenerCount));
    }
    
    /**
     * SwingX Issue #299.
     */
    @Test
    @Ignore("Fixed c&p error in test, exposed problem with #299 solution")
    public void testDecorationActionListeners() {
        //current count 1 from AutoComplete
        int expectedActionListenerCount = combo.getActionListeners().length + 1;
        AutoCompleteDecorator.decorate(combo);
        assertThat(combo.getActionListeners().length, is(expectedActionListenerCount));
        
        //redecorating should not increase listener count
        AutoCompleteDecorator.decorate(combo);
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
