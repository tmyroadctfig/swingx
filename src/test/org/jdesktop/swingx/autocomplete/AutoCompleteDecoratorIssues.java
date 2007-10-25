/**
 * 
 */
package org.jdesktop.swingx.autocomplete;

import java.awt.Component;

import javax.swing.JComboBox;

import junit.framework.TestCase;

/**
 * @author Karl George Schaefer
 *
 */
public class AutoCompleteDecoratorIssues extends TestCase {
    private JComboBox combo;
    
    protected void setUp() {
        combo = new JComboBox(new String[]{"Alpha", "Bravo", "Charlie", "Delta"});
    }
    
    /**
     * SwingX Issue #299.
     */
    public void testDecorationFocusListeners() {
        Component editor = combo.getEditor().getEditorComponent();
        int focusListenerCount = editor.getFocusListeners().length;
        AutoCompleteDecorator.decorate(combo);
        assertEquals(++focusListenerCount, editor.getFocusListeners().length);
        
        //redecorating should not increase listener count
        AutoCompleteDecorator.decorate(combo);
        assertEquals(focusListenerCount, editor.getFocusListeners().length);
    }
    
    /**
     * SwingX Issue #299.
     */
    public void testDecorationKeyListeners() {
        Component editor = combo.getEditor().getEditorComponent();
        int keyListenerCount = editor.getKeyListeners().length;
        AutoCompleteDecorator.decorate(combo);
        assertEquals(++keyListenerCount, editor.getKeyListeners().length);
        
        //redecorating should not increase listener count
        AutoCompleteDecorator.decorate(combo);
        assertEquals(keyListenerCount, editor.getKeyListeners().length);
    }
    
    /**
     * SwingX Issue #299.
     */
    public void testDecorationPropertyListeners() {
        int propListenerCount = combo.getPropertyChangeListeners("editor").length;
        AutoCompleteDecorator.decorate(combo);
        assertEquals(++propListenerCount, combo.getPropertyChangeListeners("editor").length);
        
        //redecorating should not increase listener count
        AutoCompleteDecorator.decorate(combo);
        assertEquals(propListenerCount, combo.getPropertyChangeListeners("editor").length);
    }
}
