/*
 * IconPainterBeanInfo.java
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
public class IconPainterBeanInfo extends BeanInfoSupport {
    
    /** Creates a new instance of IconPainterBeanInfo */
    public IconPainterBeanInfo() {
        super(IconPainter.class);
    }

    protected void initialize() {
        setPreferred(true, "icon");
        setHidden(true, "class", "propertyChangeListeners", "image");
    }
}
