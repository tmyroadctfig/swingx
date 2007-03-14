/*
 * PositionedPainterBeanInfo.java
 *
 * Created on July 31, 2006, 3:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import org.jdesktop.swingx.EnumerationValue;
import org.jdesktop.swingx.editors.EnumerationValuePropertyEditor;
import org.jdesktop.swingx.editors.InsetsPropertyEditor;

/**
 *
 * @author joshy
 */
public class PositionedPainterBeanInfo extends AbstractPainterBeanInfo {
    
    /** Creates a new instance of PositionedPainterBeanInfo */
    public PositionedPainterBeanInfo(Class clazz) {
        super(clazz);
    }
    protected void initialize() {
        super.initialize();
        setPropertyEditor(InsetsPropertyEditor.class,"insets");
        setPropertyEditor(VerticalPropertyEditor.class,"vertical");
        setPropertyEditor(HorizontalPropertyEditor.class,"horizontal");
        setHidden(true,"pathEffects");
    }

    public static final class HorizontalPropertyEditor extends EnumerationValuePropertyEditor {
        public HorizontalPropertyEditor() {
            super(null, new EnumerationValue[]  {new EnumerationValue("Left", ImagePainter.HorizontalAlignment.LEFT, "HorizontalAlignment.LEFT"), new EnumerationValue("Center", ImagePainter.HorizontalAlignment.CENTER, "HorizontalAlignment.CENTER"), new EnumerationValue("Right", ImagePainter.HorizontalAlignment.RIGHT, "HorizontalAlignment.RIGHT")});
        }
    }

    
    public static final class VerticalPropertyEditor extends EnumerationValuePropertyEditor {
        public VerticalPropertyEditor() {
            super(null, new EnumerationValue[]  {new EnumerationValue("Top", ImagePainter.VerticalAlignment.TOP, "VerticalAlignment.TOP"), new EnumerationValue("Center", ImagePainter.VerticalAlignment.CENTER, "VerticalAlignment.CENTER"), new EnumerationValue("Bottom", ImagePainter.VerticalAlignment.BOTTOM, "VerticalAlignment.BOTTOM")});
        }
    }
    
}
