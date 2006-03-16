/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx;

import java.beans.BeanDescriptor;

/**
 * BeanInfo class for JXCollapsiblePane.
 */
public class JXCollapsiblePaneBeanInfo extends BeanInfoSupport {
    /** Constructor for the JXCollapsiblePaneBeanInfo object */
    public JXCollapsiblePaneBeanInfo() {
        super(JXCollapsiblePane.class);        
    }
    
    protected void initialize() {
        BeanDescriptor bd = getBeanDescriptor();
        bd.setName("JXCollapsiblePane");
        bd.setShortDescription("A pane which hides its content with an animation.");
        bd.setValue("isContainer", Boolean.TRUE);
        bd.setValue("containerDelegate", "getContentPane");
        
        setPreferred(true, "animated", "collapsed");
        setBound(true, "animated", "collapsed");
        
//        BeanInfo info = Introspector.getBeanInfo(getBeanDescriptor().getBeanClass().getSuperclass());
//        String order = info.getBeanDescriptor().getValue("propertyorder") == null ? "" : (String) info.getBeanDescriptor().getValue("propertyorder");
//        PropertyDescriptor[] pd = getPropertyDescriptors();
//        for (int i = 0; i != pd.length; i++) {
//            if (order.indexOf(pd[i].getName()) == -1) {
//                order = order + (order.length() == 0 ? "" : ":") + pd[i].getName();
//            }
//        }
//        getBeanDescriptor().setValue("propertyorder", order);
    }
}
