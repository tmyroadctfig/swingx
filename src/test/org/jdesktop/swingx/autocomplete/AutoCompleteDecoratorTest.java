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

import java.awt.Component;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextPane;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;


import junit.framework.TestCase;

/**
 * @author Karl George Schaefer
 *
 */
@RunWith(JUnit4.class)
public class AutoCompleteDecoratorTest extends TestCase {
    private JComboBox combo;
    
    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    
    protected void setUp() {
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
        assertEquals(expectedFocusListenerCount, editor.getFocusListeners().length);
        
        //redecorating should not increase listener count
        AutoCompleteDecorator.decorate(combo);
        assertEquals(expectedFocusListenerCount, editor.getFocusListeners().length);
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
        assertEquals(expectedKeyListenerCount, editor.getKeyListeners().length);
        
        //redecorating should not increase listener count
        AutoCompleteDecorator.decorate(combo);
        assertEquals(expectedKeyListenerCount, editor.getKeyListeners().length);
    }
    
    /**
     * SwingX Issue #299.
     */
    @Test
    public void testDecorationPropertyListeners() {
        //current count 1 from AutoComplete
        int expectedPropListenerCount = combo.getPropertyChangeListeners("editor").length + 1;
        AutoCompleteDecorator.decorate(combo);
        assertEquals(expectedPropListenerCount, combo.getPropertyChangeListeners("editor").length);
        
        //redecorating should not increase listener count
        AutoCompleteDecorator.decorate(combo);
        assertEquals(expectedPropListenerCount, combo.getPropertyChangeListeners("editor").length);
    }
    
    @Test
    public void testDecoratingJTextPane() {
        List<String> strings = Arrays.asList("Alpha", "Bravo", "Charlie", "Delta");
        AutoCompleteDecorator.decorate(new JTextPane(), strings, true);
    }
}
