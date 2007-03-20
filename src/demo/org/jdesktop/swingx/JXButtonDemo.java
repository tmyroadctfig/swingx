package org.jdesktop.swingx;

import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.image.StackBlurFilter;

import javax.swing.*;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImageOp;

/**
 * Created by IntelliJ IDEA.
 * User: richardallenbair
 * Date: Mar 20, 2007
 * Time: 10:16:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class JXButtonDemo {
    private JXButtonDemo() {}

    public static void main(String[] args) {
        JXFrame f = new JXFrame("JXButton Demo", true);
        JPanel p = new JPanel();
        f.add(p);

        //simple demo that blurs the button's text
        final JXButton b = new JXButton("Execute");
        final AbstractPainter fgPainter = (AbstractPainter)b.getForegroundPainter();
        final StackBlurFilter filter = new StackBlurFilter();
        fgPainter.setFilters(filter);

        b.addMouseListener(new MouseAdapter() {
            boolean entered = false;
            public void mouseEntered(MouseEvent mouseEvent) {
                if (!entered) {
                    fgPainter.setFilters(new BufferedImageOp[0]);
                    b.repaint();
                    entered = true;
                }
            }
            public void mouseExited(MouseEvent mouseEvent) {
                if (entered) {
                    fgPainter.setFilters(filter);
                    b.repaint();
                    entered = false;
                }
            }
        });

        p.add(b);
        f.setSize(400, 300);
        f.setStartPosition(JXFrame.StartPosition.CenterInScreen);
        f.setVisible(true);
    }
}
