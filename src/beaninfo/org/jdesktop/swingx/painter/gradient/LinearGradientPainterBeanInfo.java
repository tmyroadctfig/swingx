/*
 * LinearGradientPainterBeanInfo.java
 *
 * Created on March 21, 2006, 12:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter.gradient;

import org.jdesktop.swingx.BeanInfoSupport;

/**
 *
 * @author Richard
 */
public class LinearGradientPainterBeanInfo extends BeanInfoSupport {
    
    /** Creates a new instance of LinearGradientPainterBeanInfo */
    public LinearGradientPainterBeanInfo() {
        super(LinearGradientPainter.class);
    }

    protected void initialize() {
        setHidden(true, "class", "propertyChangeListeners");
    }
    
//    protected Class getCustomizerClass() {
//        return TestCustomizer.class;
//    }
}
