/*
 * JXInsets.java
 *
 * Created on March 5, 2007, 9:26 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx;

import java.awt.Insets;

/**
 *
 * @author joshy
 */
public class JXInsets extends Insets {
    
    /** Creates a new instance of JXInsets */
    public JXInsets() {
        super(0,0,0,0);
    }
    public JXInsets(int width) {
        super(width, width, width, width);
    }
    
    public JXInsets(int top, int left, int bottom, int right) {
        super(top,left,bottom,right);
    }
    
    public JXInsets(Insets ... insets) {
        this(0,0,0,0);
        
        for(Insets ins : insets) {
            top+=ins.top;
            left+=ins.left;
            bottom+=ins.bottom;
            right+=ins.right;
        }
        
    }
    
}
