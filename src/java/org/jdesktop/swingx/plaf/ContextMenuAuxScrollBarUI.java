/*
 * Created on 02.08.2005
 *
 */
package org.jdesktop.swingx.plaf;

import java.awt.Graphics;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollBarUI;

/**
 * @author Jeanette Winzenburg
 */
public class ContextMenuAuxScrollBarUI extends ScrollBarUI {
    
    private MouseListener mouseHandler;
    private JScrollBar scrollBar;
    
    public static ComponentUI createUI(JComponent c) {
//        if (auxTextFieldUI == null) {
//            auxTextFieldUI = new ContextMenuAuxTextUI();
//        }
        return new ContextMenuAuxScrollBarUI(); //auxTextFieldUI;
    }

    // PENDING: need to listen to orientation changes
    // 
    public void installUI(JComponent comp) {
        this.scrollBar = (JScrollBar) comp;
        comp.addMouseListener(getMouseListener());
    }

    // PENDING: need to cleanup references - 
    // DelegateAction holds a reference to the comp!
    public void uninstallUI(JComponent comp) {
        comp.removeMouseListener(getMouseListener());
        this.scrollBar = null;
    }



    private MouseListener getMouseListener() {
        if (mouseHandler == null) {
            mouseHandler = createPopupHandler();
        }
        return mouseHandler;
    }

    private MouseListener createPopupHandler() {
        return new ContextMenuHandler(createContextSource());
    }

    private ContextMenuSource createContextSource() {
        return new ScrollBarContextMenuSource(scrollBar.getOrientation());
    }

    public void update(Graphics g, JComponent c) {
    }
    

}
