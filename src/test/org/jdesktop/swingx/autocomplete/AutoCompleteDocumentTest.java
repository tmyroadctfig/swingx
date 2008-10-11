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

package org.jdesktop.swingx.autocomplete;

import static org.jdesktop.swingx.autocomplete.ObjectToStringConverter.DEFAULT_IMPLEMENTATION;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.awt.GraphicsEnvironment;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.jdesktop.test.EDTRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EDTRunner.class)
public class AutoCompleteDocumentTest {
    /**
     * Ensure that Swing can run correctly.
     */
    @BeforeClass
    public static void startUp() {
        assumeTrue(!GraphicsEnvironment.isHeadless());
        assumeTrue(SwingUtilities.isEventDispatchThread());
    }
    
    /**
     * Ensure that adaptor cannot be null. 
     */
    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullAdaptor() {
        new AutoCompleteDocument(null, true);
    }
    
    /**
     * Ensure that a null converter is the same as the default implementation. 
     */
    @Test
    public void testConstructorWithNullConverter() {
        AutoCompleteDocument document = new AutoCompleteDocument(
                new ComboBoxAdaptor(new JComboBox(new String[] {
                    "A"
                })), true, null);
        assertSame(DEFAULT_IMPLEMENTATION, document.stringConverter);
    }
    
    @Test
    public void testPreferExactMatchOverCurrentlySelected() throws Exception {
        String[] items = new String[]{"exact", "exacter", "exactest"};

        JTextComponent textComponent = new JTextField();
        TextComponentAdaptor adaptor = new TextComponentAdaptor(textComponent, Arrays.asList(items));
        Document document = new AutoCompleteDocument(adaptor, true);
        textComponent.setDocument(document);
        
        textComponent.setText("exacter");
        assertTrue(adaptor.getSelectedItem().equals("exacter"));
        
        document.remove(4, 3);
        assertTrue(adaptor.getSelectedItem().equals("exacter"));
        
        document.insertString(4, "t", null);
        assertTrue(adaptor.getSelectedItem().equals("exact"));
    }
}
