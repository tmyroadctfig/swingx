/*
 * JXTitledPanelBeanInfo.java
 *
 * Created on March 16, 2006, 12:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx;

import java.beans.BeanDescriptor;

/**
 *
 * @author Richard
 */
public class JXTitledPanelBeanInfo extends BeanInfoSupport {
    
    /** Creates a new instance of JXTitledPanelBeanInfo */
    public JXTitledPanelBeanInfo() {
        super(JXTitledPanel.class);
    }
    
    protected void initialize() {
        BeanDescriptor bd = getBeanDescriptor();
        bd.setValue("containerDelegate", "getContentContainer");
    }
}
