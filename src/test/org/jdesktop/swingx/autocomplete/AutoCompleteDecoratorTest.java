/**
 * 
 */
package org.jdesktop.swingx.autocomplete;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextPane;

import junit.framework.TestCase;

/**
 * @author Karl George Schaefer
 *
 */
public class AutoCompleteDecoratorTest extends TestCase {
    private JComboBox combo;
    
    protected void setUp() {
        combo = new JComboBox(new String[]{"Alpha", "Bravo", "Charlie", "Delta"});
    }
    
    /**
     * SwingX Issue #299.
     */
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
    public void testDecorationPropertyListeners() {
        //current count 1 from AutoComplete
        int expectedPropListenerCount = combo.getPropertyChangeListeners("editor").length + 1;
        AutoCompleteDecorator.decorate(combo);
        assertEquals(expectedPropListenerCount, combo.getPropertyChangeListeners("editor").length);
        
        //redecorating should not increase listener count
        AutoCompleteDecorator.decorate(combo);
        assertEquals(expectedPropListenerCount, combo.getPropertyChangeListeners("editor").length);
    }
    
    public void testDecoratingJTextPane() {
        List<String> strings = Arrays.asList("Alpha", "Bravo", "Charlie", "Delta");
        AutoCompleteDecorator.decorate(new JTextPane(), strings, true);
    }
}
