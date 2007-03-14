/*
 * TextPainterBeanInfo.java
 *
 * Created on July 18, 2006, 3:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import org.jdesktop.swingx.editors.Paint2PropertyEditor;
import org.jdesktop.swingx.editors.Point2DPropertyEditor;

/**
 *
 * @author joshy
 */
public class TextPainterBeanInfo extends PositionedPainterBeanInfo {
    
    /** Creates a new instance of TextPainterBeanInfo */
    public TextPainterBeanInfo() {
        super(TextPainter.class);
    }
    
    protected void initialize() {
        super.initialize();
        setDisplayName("Text","text");
        //inherited setPropertyEditor(Paint2PropertyEditor.class,"fillPaint");
    }
    
}
