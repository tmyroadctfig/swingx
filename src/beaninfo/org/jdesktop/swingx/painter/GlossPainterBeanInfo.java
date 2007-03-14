/*
 * GlossPainterBeanInfo.java
 *
 * Created on July 18, 2006, 3:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import org.jdesktop.swingx.editors.Paint2PropertyEditor;

/**
 *
 * @author joshy
 */
public class GlossPainterBeanInfo extends AbstractPainterBeanInfo {
    
    /** Creates a new instance of GlossPainterBeanInfo */
    public GlossPainterBeanInfo() {
        super(GlossPainter.class);
    }
    
    protected void initialize() {
        super.initialize();
        setPropertyEditor(Paint2PropertyEditor.class,"paint");
    }
}
