/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.jdnc.beaninfo;
import java.beans.PropertyDescriptor;
import org.jdesktop.swingx.JXPanel;



/**
 *
 * @author rbair
 */
public class JXPanelBeanInfo extends BeanInfoSupport {
    
    public JXPanelBeanInfo() {
        super(JXPanel.class);
    }

    protected void initialize() {
        PropertyDescriptor[] array = getPropertyDescriptors();
        for (int i=0; i<array.length; i++) {
            PropertyDescriptor pd = array[i];
            if (pd.getName().equals("opaque")
                || pd.getName().equals("alpha")
                || pd.getName().equals("inheritAlpha")
                || pd.getName().equals("drawGradient")
                || pd.getName().equals("gradientPaint")) {
                pd.setPreferred(true);
            }
        }
        iconNameC16 = "jxpanel16.png";
        iconNameC32 = "jxpanel32.png";
    }
}
