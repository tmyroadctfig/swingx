/*
 * PinstripePainterBeanInfo.java
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
public class PinstripePainterBeanInfo extends BeanInfoSupport {
    
    /** Creates a new instance of PinstripePainterBeanInfo */
    public PinstripePainterBeanInfo() {
        super(PinstripePainter.class);
    }

    protected void initialize() {
        setPreferred(true, "angle", "spacing", "paint");
        setHidden(true, "class", "propertyChangeListeners", "image");
        setPropertyEditor(PaintPropertyEditor.class, "paint");
    }
}
