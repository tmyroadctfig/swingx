/*
 * ShapePainterBeanInfo.java
 *
 * Created on August 1, 2006, 5:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import org.jdesktop.swingx.editors.EnumPropertyEditor;
import org.jdesktop.swingx.editors.InsetsPropertyEditor;
import org.jdesktop.swingx.editors.Paint2PropertyEditor;
import org.jdesktop.swingx.editors.ShapePropertyEditor;

/**
 *
 * @author joshy
 */
public class ShapePainterBeanInfo extends PositionedPainterBeanInfo {
    
    /** Creates a new instance of ShapePainterBeanInfo */
    public ShapePainterBeanInfo() {
        super(ShapePainter.class);
    }
    
    protected void initialize() {
        super.initialize();
        setPropertyEditor(Paint2PropertyEditor.class, "fillPaint", "borderPaint");
        setPropertyEditor(InsetsPropertyEditor.class,"insets");
        setPropertyEditor(StylePropertyEditor.class,"style");
        setPropertyEditor(ShapePropertyEditor.class,"shape");
    }
    
    public static final class StylePropertyEditor extends EnumPropertyEditor {
        public StylePropertyEditor() {
            super(RectanglePainter.Style.class);
        }
    }
}
