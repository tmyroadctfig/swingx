/*
 * CompoundPainterBeanInfo.java
 *
 * Created on March 21, 2006, 12:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import org.jdesktop.swingx.BeanInfoSupport;

/**
 *
 * @author Richard
 */
public class CompoundPainterBeanInfo extends BeanInfoSupport {
    
    /** Creates a new instance of CompoundPainterBeanInfo */
    public CompoundPainterBeanInfo() {
        super(CompoundPainter.class);
    }

    protected void initialize() {
        setPreferred(true, "painters");
        setHidden(true, "class", "propertyChangeListeners");
    }
}
