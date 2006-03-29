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

    protected void initialize() {
        setHidden(true, "class", "propertyChangeListeners", "renderingHints");
        
        //set editor for the clip shape
        //set editor for the effects (not sure how to do this one)
        //TODO
        
        //set editor for resizeClip
        setPropertyEditor(ResizeClipPropertyEditor.class, "resizeClip");
        //set editor for composite (incl. Alpha composites by default)
        setPropertyEditor(CompositePropertyEditor.class, "composite");
        //set editors for the various rendering hints
        setPropertyEditor(AlphaInterpolationPropertyEditor.class, "alphaInterpolation");
        setPropertyEditor(AntialiasingPropertyEditor.class, "antialiasing");
        setPropertyEditor(ColorRenderingPropertyEditor.class, "colorRendering");
        setPropertyEditor(DitheringPropertyEditor.class, "dithering");
        setPropertyEditor(FractionalMetricsPropertyEditor.class, "fractionalMetrics");
        setPropertyEditor(InterpolationPropertyEditor.class, "interpolation");
        setPropertyEditor(RenderingPropertyEditor.class, "rendering");
        setPropertyEditor(StrokeControlPropertyEditor.class, "strokeControl");
        setPropertyEditor(TextAntialiasingPropertyEditor.class, "textAntialiasing");
        
        //move some items into "Appearance" and some into "Behavior"
        setCategory("Rendering Hints", "alphaInterpolation", "antialiasing", 
                "colorRendering", "dithering", "fractionalMetrics", "interpolation",
                "rendering", "strokeControl", "textAntialiasing");
        setCategory("Appearance", "clip", "composite", "effects");
        setCategory("Behavior", "resizeClip", "useCache");
    }
    
    public static final class ResizeClipPropertyEditor extends EnumerationValuePropertyEditor {
        public ResizeClipPropertyEditor() {
            super(null, new EnumerationValue[] {
                new EnumerationValue("None", Resize.NONE, "Resize.NONE"),
                new EnumerationValue("Horizontal", Resize.HORIZONTAL, "Resize.HORIZONTAL"),
                new EnumerationValue("Vertical", Resize.VERTICAL, "Resize.VERTICAL"),
                new EnumerationValue("Both", Resize.BOTH, "Resize.BOTH")
            });
        }
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
    public static final class AlphaInterpolationPropertyEditor extends EnumerationValuePropertyEditor {
        public AlphaInterpolationPropertyEditor() {
            super(null, new EnumerationValue[] {
                new EnumerationValue("", null, "null"),
                new EnumerationValue("Default", RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT, "RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT"),
                new EnumerationValue("Quality", RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY, "RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY"),
                new EnumerationValue("Speed", RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED, "RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED")
            });
        }
    }
    public static final class AntialiasingPropertyEditor extends EnumerationValuePropertyEditor {
        public AntialiasingPropertyEditor() {
            super(null, new EnumerationValue[] {
                new EnumerationValue("", null, "null"),
                new EnumerationValue("Default", RenderingHints.VALUE_ANTIALIAS_DEFAULT, "RenderingHints.VALUE_ANTIALIAS_DEFAULT"),
                new EnumerationValue("On", RenderingHints.VALUE_ANTIALIAS_ON, "RenderingHints.VALUE_ANTIALIAS_ON"),
                new EnumerationValue("Off", RenderingHints.VALUE_ANTIALIAS_OFF, "RenderingHints.VALUE_ANTIALIAS_OFF")
            });
        }
    }
    public static final class ColorRenderingPropertyEditor extends EnumerationValuePropertyEditor {
        public ColorRenderingPropertyEditor() {
            super(null, new EnumerationValue[] {
                new EnumerationValue("", null, "null"),
                new EnumerationValue("Default", RenderingHints.VALUE_COLOR_RENDER_DEFAULT, "RenderingHints.VALUE_COLOR_RENDER_DEFAULT"),
                new EnumerationValue("Quality", RenderingHints.VALUE_COLOR_RENDER_QUALITY, "RenderingHints.VALUE_COLOR_RENDER_QUALITY"),
                new EnumerationValue("Speed", RenderingHints.VALUE_COLOR_RENDER_SPEED, "RenderingHints.VALUE_COLOR_RENDER_SPEED")
            });
        }
    }
    public static final class DitheringPropertyEditor extends EnumerationValuePropertyEditor {
        public DitheringPropertyEditor() {
            super(null, new EnumerationValue[] {
                new EnumerationValue("", null, "null"),
                new EnumerationValue("Default", RenderingHints.VALUE_DITHER_DEFAULT, "RenderingHints.VALUE_DITHER_DEFAULT"),
                new EnumerationValue("Enable", RenderingHints.VALUE_DITHER_ENABLE, "RenderingHints.VALUE_DITHER_ENABLE"),
                new EnumerationValue("Disable", RenderingHints.VALUE_DITHER_DISABLE, "RenderingHints.VALUE_DITHER_DISABLE")
            });
        }
    }
    public static final class FractionalMetricsPropertyEditor extends EnumerationValuePropertyEditor {
        public FractionalMetricsPropertyEditor() {
            super(null, new EnumerationValue[] {
                new EnumerationValue("", null, "null"),
                new EnumerationValue("Default", RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT, "RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT"),
                new EnumerationValue("On", RenderingHints.VALUE_FRACTIONALMETRICS_ON, "RenderingHints.VALUE_FRACTIONALMETRICS_ON"),
                new EnumerationValue("Off", RenderingHints.VALUE_FRACTIONALMETRICS_OFF, "RenderingHints.VALUE_FRACTIONALMETRICS_OFF")
            });
        }
    }
    public static final class InterpolationPropertyEditor extends EnumerationValuePropertyEditor {
        public InterpolationPropertyEditor() {
            super(null, new EnumerationValue[] {
                new EnumerationValue("", null, "null"),
                new EnumerationValue("Bicubic", RenderingHints.VALUE_INTERPOLATION_BICUBIC, "RenderingHints.VALUE_INTERPOLATION_BICUBIC"),
                new EnumerationValue("Bilinear", RenderingHints.VALUE_INTERPOLATION_BILINEAR, "RenderingHints.VALUE_INTERPOLATION_BILINEAR"),
                new EnumerationValue("Nearest Neighbor", RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, "RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR")
            });
        }
    }
    public static final class RenderingPropertyEditor extends EnumerationValuePropertyEditor {
        public RenderingPropertyEditor() {
            super(null, new EnumerationValue[] {
                new EnumerationValue("", null, "null"),
                new EnumerationValue("Default", RenderingHints.VALUE_RENDER_DEFAULT, "RenderingHints.VALUE_RENDER_DEFAULT"),
                new EnumerationValue("Quality", RenderingHints.VALUE_RENDER_QUALITY, "RenderingHints.VALUE_RENDER_QUALITY"),
                new EnumerationValue("Speed", RenderingHints.VALUE_RENDER_SPEED, "RenderingHints.VALUE_RENDER_SPEED")
            });
        }
    }
    public static final class StrokeControlPropertyEditor extends EnumerationValuePropertyEditor {
        public StrokeControlPropertyEditor() {
            super(null, new EnumerationValue[] {
                new EnumerationValue("", null, "null"),
                new EnumerationValue("Default", RenderingHints.VALUE_STROKE_DEFAULT, "RenderingHints.VALUE_STROKE_DEFAULT"),
                new EnumerationValue("Normalize", RenderingHints.VALUE_STROKE_NORMALIZE, "RenderingHints.VALUE_STROKE_NORMALIZE"),
                new EnumerationValue("Pure", RenderingHints.VALUE_STROKE_PURE, "RenderingHints.VALUE_STROKE_PURE")
            });
        }
    }
    public static final class TextAntialiasingPropertyEditor extends EnumerationValuePropertyEditor {
        public TextAntialiasingPropertyEditor() {
            super(null, new EnumerationValue[] {
                new EnumerationValue("", null, "null"),
                new EnumerationValue("Default", RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT, "RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT"),
                new EnumerationValue("On", RenderingHints.VALUE_TEXT_ANTIALIAS_ON, "RenderingHints.VALUE_TEXT_ANTIALIAS_ON"),
                new EnumerationValue("Off", RenderingHints.VALUE_TEXT_ANTIALIAS_OFF, "RenderingHints.VALUE_TEXT_ANTIALIAS_OFF")
            });
        }
    }
}
