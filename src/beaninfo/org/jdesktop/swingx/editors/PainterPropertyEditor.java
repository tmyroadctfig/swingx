/*
 * PainterPropertyEditor.java
 *
 * Created on March 21, 2006, 11:26 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.editors;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.Map;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.gradient.BasicGradientPainter;

/**
 * Two parts to this property editor. The first part is a simple dropdown.
 * The second part is a complicated UI for constructing multiple "layers" of
 * various different Painters, including gradient painters.
 *
 * @author Richard
 */
public class PainterPropertyEditor extends PropertyEditorSupport {
    private static Map<Painter, String> defaultPainters = new HashMap<Painter, String>();
    static {
        //add the default painters
//        defaultPainters.put(
//                new BasicGradientPainter(
//                    BasicGradientPainter.WHITE_TO_CONTROL_HORZONTAL), "White->Control (horizontal)");
//        defaultPainters.put(
//                new BasicGradientPainter(
//                    BasicGradientPainter.WHITE_TO_CONTROL_VERTICAL), "White->Control (vertical)");
    }
    
    /** Creates a new instance of PainterPropertyEditor */
    public PainterPropertyEditor() {
    }
    
    public String[] getTags() {
        String[] names = defaultPainters.values().toArray(new String[0]);
        String[] results = new String[names.length+1];
        results[0] = "<none>";
        System.arraycopy(names, 0, results, 1, names.length);
        return results;
    }
    
    public Painter getValue() {
        return (Painter)super.getValue();
    }

    public String getJavaInitializationString() {
        Painter painter = getValue();
        //TODO!!!
        return painter == null ? "null" : 
            "new org.jdesktop.swingx.painter.CheckerboardPainter()";
    }

    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null || text.trim().equals("") || text.trim().equalsIgnoreCase("none")
                || text.trim().equalsIgnoreCase("<none>")
                || text.trim().equalsIgnoreCase("[none]")) {
            setValue(null);
            return;
        }
        
        if (text.trim().equalsIgnoreCase("<custom>")) {
            //do nothing
        }
        
        for (Map.Entry<Painter, String> entry : defaultPainters.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(text)) {
                setValue(entry.getKey());
                return;
            }
        }
        
        throw new IllegalArgumentException("The input value " + text + " does" +
                " not match one of the names of the standard painters");
    }

    public String getAsText() {
        Painter p = getValue();
        if (p == null) {
            return null;
        } else if (defaultPainters.containsKey(p)) {
            return defaultPainters.get(p);
        } else {
            return "<custom>";
        }
    }

    public void paintValue(Graphics gfx, Rectangle box) {
        Painter p = getValue();
        if (p == null) {
            //do nothing -- in the future draw the checkerboard or something
        } else {
            JXPanel panel = new JXPanel();
            panel.setBounds(box);
            p.paint((Graphics2D) gfx, panel);
        }
    }

    public boolean isPaintable() {
        return true;
    }
}
