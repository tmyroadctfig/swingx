package org.jdesktop.swingx.plaf;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;

import org.jdesktop.swingx.JXTextField;
import org.junit.Test;

public class PromptTextFieldUITest extends PromptTextUITest {
    JTextField txtField;
    
    public void setup() {
        textComponent = txtField = new JXTextField();
    }
    
    @Test
    public void testGetLabelComponent() {
        super.testGetLabelComponent();
        txtField.setHorizontalAlignment(JTextField.CENTER);
        JTextField lbl = (JTextField) ui.getPromptComponent(txtField);
        
        assertEquals(txtField.getHorizontalAlignment(), lbl.getHorizontalAlignment());
        assertEquals(txtField.getColumns(), lbl.getColumns());
    }
}
