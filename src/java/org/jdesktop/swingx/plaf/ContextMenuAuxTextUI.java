/*
 * Created on 01.08.2005
 *
 */
package org.jdesktop.swingx.plaf;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import javax.swing.text.Position.Bias;

/**
 * @author Jeanette Winzenburg
 */
public class ContextMenuAuxTextUI extends TextUI {

    private MouseListener mouseHandler;

    public static ComponentUI createUI(JComponent c) {
//        if (auxTextFieldUI == null) {
//            auxTextFieldUI = new ContextMenuAuxTextUI();
//        }
        return new ContextMenuAuxTextUI(); //auxTextFieldUI;
    }

    public void installUI(JComponent comp) {
        comp.addMouseListener(getMouseListener());
    }

    public void uninstallUI(JComponent comp) {
        comp.removeMouseListener(getMouseListener());
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
        return new TextContextMenuSource();
    }

    public void update(Graphics g, JComponent c) {
    }

    public Rectangle modelToView(JTextComponent t, int pos)
            throws BadLocationException {
        // TODO Auto-generated method stub
        return null;
    }

    public Rectangle modelToView(JTextComponent t, int pos, Bias bias)
            throws BadLocationException {
        // TODO Auto-generated method stub
        return null;
    }

    public int viewToModel(JTextComponent t, Point pt) {
        // TODO Auto-generated method stub
        return 0;
    }

    public int viewToModel(JTextComponent t, Point pt, Bias[] biasReturn) {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getNextVisualPositionFrom(JTextComponent t, int pos, Bias b,
            int direction, Bias[] biasRet) throws BadLocationException {
        // TODO Auto-generated method stub
        return 0;
    }

    public void damageRange(JTextComponent t, int p0, int p1) {
        // TODO Auto-generated method stub

    }

    public void damageRange(JTextComponent t, int p0, int p1, Bias firstBias,
            Bias secondBias) {
        // TODO Auto-generated method stub

    }

    public EditorKit getEditorKit(JTextComponent t) {
        // TODO Auto-generated method stub
        return null;
    }

    public View getRootView(JTextComponent t) {
        // TODO Auto-generated method stub
        return null;
    }

}
