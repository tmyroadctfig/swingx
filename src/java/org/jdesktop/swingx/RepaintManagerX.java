/*
 * RepaintManagerX.java
 *
 * Created on April 12, 2005, 10:13 AM
 */

package org.jdesktop.swingx;

import java.awt.Container;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.RepaintManager;

/**
 * This repaint manager is used by Swingx for translucency. The default implementation
 * of JXPanel (which supports translucency) will replace the current RepaintManager
 * with a RepaintManagerX *unless* the current RepaintManager is
 * tagged by the "TranslucentRepaintManager" interface. 
 * <p>TODO: Add this to the main documentation (make it visible) so that people
 * don't bump into it accidently and spend time debugging</p>
 *
 * @author zixle
 * @author rbair
 */
public class RepaintManagerX extends RepaintManager implements TranslucentRepaintManager {
    public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
        Rectangle dirtyRegion = getDirtyRegion(c);
        if (dirtyRegion.width == 0 && dirtyRegion.height == 0) {
            int lastDeltaX = c.getX();
            int lastDeltaY = c.getY();
            Container parent = c.getParent();
            while (parent instanceof JComponent) {
                if (!parent.isVisible() || (parent.getPeer() == null)) {
                    return;
                }
                if (parent instanceof JXPanel && ((JXPanel)parent).getAlpha() < 1f) {
                    x += lastDeltaX;
                    y += lastDeltaY;
                    lastDeltaX = lastDeltaY = 0;
                    c = (JComponent)parent;
                }
                lastDeltaX += parent.getX();
                lastDeltaY += parent.getY();
                parent = parent.getParent();
            }
        }
        super.addDirtyRegion(c, x, y, w, h);
    }
}