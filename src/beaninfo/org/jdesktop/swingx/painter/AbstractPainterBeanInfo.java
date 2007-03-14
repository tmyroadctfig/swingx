/*
 * BackgroundPainterBeanInfo.java
 *
 * Created on March 21, 2006, 12:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import java.awt.AlphaComposite;
import java.awt.RenderingHints;
import org.jdesktop.swingx.BeanInfoSupport;
import org.jdesktop.swingx.EnumerationValue;
import org.jdesktop.swingx.editors.EnumPropertyEditor;
import org.jdesktop.swingx.editors.EnumerationValuePropertyEditor;
import org.jdesktop.swingx.util.Resize;

/**
 *
 * @author Richard
 */
public class AbstractPainterBeanInfo extends BeanInfoSupport {
    
    /** Creates a new instance of BackgroundPainterBeanInfo */
    public AbstractPainterBeanInfo() {
        super(AbstractPainter.class);
    }
    
    public AbstractPainterBeanInfo(Class clazz) {
        super(clazz);
    }

    protected void initialize() {
        setHidden(true, "class", "propertyChangeListeners");
        
        //set editor for the clip shape
        //set editor for the effects (not sure how to do this one)
        setHidden(true,"effects");
        setDisplayName("Use Cache","useCache");        
        //set editor for composite (incl. Alpha composites by default)
        setPropertyEditor(CompositePropertyEditor.class, "composite");
        //set editors for the various rendering hints
        //setPropertyEditor(AntialiasingPropertyEditor.class, "antialiasing");
        //setPropertyEditor(FractionalMetricsPropertyEditor.class, "fractionalMetrics");
        setPropertyEditor(InterpolationPropertyEditor.class, "interpolation");
        //move some items into "Appearance" and some into "Behavior"
        //setCategory("Rendering Hints", "antialiasing", "fractionalMetrics", "interpolation");
        setExpert(true, "antialiasing","fractionalMetrics","useCache",
                "interpolation","clip","effects","composite");
    }
    
    public static final class CompositePropertyEditor extends EnumerationValuePropertyEditor {
        public CompositePropertyEditor() {
            super(null, new EnumerationValue[] {
                new EnumerationValue("", null, "null"),
                new EnumerationValue("Clear", AlphaComposite.Clear, "AlphaComposite.Clear"),
                new EnumerationValue("Destination", AlphaComposite.Dst, "AlphaComposite.Dst"),
                new EnumerationValue("Destination Atop", AlphaComposite.DstAtop, "AlphaComposite.DstAtop"),
                new EnumerationValue("Destination In", AlphaComposite.DstIn, "AlphaComposite.DstIn"),
                new EnumerationValue("Destination Out", AlphaComposite.DstOut, "AlphaComposite.DstOut"),
                new EnumerationValue("Destination Over", AlphaComposite.DstOver, "AlphaComposite.DstOver"),
                new EnumerationValue("Source", AlphaComposite.Src, "AlphaComposite.Src"),
                new EnumerationValue("Source Atop", AlphaComposite.SrcAtop, "AlphaComposite.SrcAtop"),
                new EnumerationValue("Source In", AlphaComposite.SrcIn, "AlphaComposite.SrcIn"),
                new EnumerationValue("Source Out", AlphaComposite.SrcOut, "AlphaComposite.SrcOut"),
                new EnumerationValue("Source Over", AlphaComposite.SrcOver, "AlphaComposite.SrcOver"),
                new EnumerationValue("Xor", AlphaComposite.Xor, "AlphaComposite.Xor")
            });
        }
    }
    /*
    public static final class AntialiasingPropertyEditor extends EnumPropertyEditor {
        public AntialiasingPropertyEditor() {
            super(AbstractPainter.Antialiasing.class);
        }
    }
    public static final class FractionalMetricsPropertyEditor extends EnumPropertyEditor {
        public FractionalMetricsPropertyEditor() {
            super(AbstractPainter.FractionalMetrics.class);
        }
    }*/
    public static final class InterpolationPropertyEditor extends EnumPropertyEditor {
        public InterpolationPropertyEditor() {
            super(AbstractPainter.Interpolation.class);
        }
    }
}
