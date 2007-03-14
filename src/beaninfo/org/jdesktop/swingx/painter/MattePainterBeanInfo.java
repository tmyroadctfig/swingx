/*
 * MattePainterBeanInfo.java
 *
 * Created on July 18, 2006, 3:15 PM
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
public class MattePainterBeanInfo extends AbstractPainterBeanInfo{
    
    /** Creates a new instance of MattePainterBeanInfo */
    public MattePainterBeanInfo() {
        super(MattePainter.class);
    }
    
    protected void initialize() {
        super.initialize();
        setPropertyEditor(Paint2PropertyEditor.class, "fillPaint");
    }
    
}
