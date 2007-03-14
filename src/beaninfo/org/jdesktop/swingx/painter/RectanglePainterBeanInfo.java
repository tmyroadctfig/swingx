/*
 * RectanglePainterBeanInfo.java
 *
 * Created on July 18, 2006, 3:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import org.jdesktop.swingx.EnumerationValue;
import org.jdesktop.swingx.editors.EnumPropertyEditor;
import org.jdesktop.swingx.editors.EnumerationValuePropertyEditor;
import org.jdesktop.swingx.editors.InsetsPropertyEditor;
import org.jdesktop.swingx.editors.Paint2PropertyEditor;

/**
 *
 * @author joshy
 */
public class RectanglePainterBeanInfo extends AbstractPainterBeanInfo {
    
    /** Creates a new instance of RectanglePainterBeanInfo */
    public RectanglePainterBeanInfo() {
        super(RectanglePainter.class);
    }
    
    protected void initialize() {
        super.initialize();
        setPropertyEditor(Paint2PropertyEditor.class, "fillPaint", "borderPaint");
        setPropertyEditor(InsetsPropertyEditor.class,"insets");
        setPropertyEditor(StylePropertyEditor.class,"style");
    }
    
    public static final class StylePropertyEditor extends EnumPropertyEditor {
        public StylePropertyEditor() {
            super(RectanglePainter.Style.class);
        }
    }
    
    
}
