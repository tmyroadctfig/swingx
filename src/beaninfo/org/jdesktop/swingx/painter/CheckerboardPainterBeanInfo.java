/*
 * CheckerboardPainterBeanInfo.java
 *
 * Created on March 21, 2006, 12:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import org.jdesktop.swingx.BeanInfoSupport;
import org.jdesktop.swingx.editors.PaintPropertyEditor;

/**
 *
 * @author Richard
 */
public class CheckerboardPainterBeanInfo extends BeanInfoSupport {
    
    /** Creates a new instance of CheckerboardPainterBeanInfo */
    public CheckerboardPainterBeanInfo() {
        super(CheckerboardPainter.class);
    }

    protected void initialize() {
        setPreferred(true, "darkColor", "lightColor", "squareLength");
        setHidden(true, "class", "propertyChangeListeners");
        setPropertyEditor(PaintPropertyEditor.class, "darkColor", "lightColor");
    }
}
