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
import javax.swing.JList;
import org.jdesktop.swingx.JXList;
import org.netbeans.modules.jdnc.beaninfo.editors.HighlighterPropertyEditor;

/**
 *
 * @author rbair
 */
public class JXListBeanInfo extends BeanInfoSupport {
    
    /** Creates a new instance of JXListBeanInfo */
    public JXListBeanInfo() {
        super(JXList.class);
    }

    protected void initialize() {
        PropertyDescriptor[] array = getPropertyDescriptors();
        for (int i=0; i<array.length; i++) {
            PropertyDescriptor pd = array[i];
            if (pd.getName().equals("highlighters")) {
                pd.setPreferred(true);
                pd.setPropertyEditorClass(HighlighterPropertyEditor.class);
            }
        }
        setIconsBasedOn(JList.class);
    }
}
