/*
 * Created on 16.03.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class JXTitledSeparatorTest extends InteractiveTestCase {
    private static final Logger LOG = Logger
            .getLogger(JXTitledSeparatorTest.class.getName());
    
    public static void main(String[] args) {
        setSystemLF(true);
        JXTitledSeparatorTest test = new JXTitledSeparatorTest();
        try {
            test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*Highligh.*");
        } catch (Exception ex) {

        }
    }

    /**
     * Issue #305-swingx: JXTitledSeparator prefSize should depend on title length.
     * 
     * (to solve: never-ever call setPrefSize() - override getPrefSize() instead)
     *
     */
    public void testPrefSize() {
        JXTitledSeparator separator = new JXTitledSeparator();
        Dimension dim = separator.getPreferredSize();
        LOG.info("initial " + dim);
        separator.setTitle("some title definitely longer than empty");
        LOG.info("later " + separator.getPreferredSize());
        assertTrue(dim.width < separator.getPreferredSize().width);
    }
    
    /**
     * Issue #304-swingx: JXTitledSeparator should have same orientation
     * dependent behaviour as TitledBorder.
     * 
     * Looking at bidi-compliance: LEFT/RIGHT should be absolute,
     * as opposed to LEADING/TRAILING which is orientation dependent.
     *  
     * weird (unrelated to the titledSeparator) the dynamic update
     * of orientation isn't taken - only after resize. Problem of
     * panel, frame, ?? An equivalent check for jxtable which resides
     * in a scrollpane is okay. Revalidate doesn't help for border.
     *
     */
    public void interactiveRToL() {
        JComponent box = Box.createVerticalBox();
        JXTitledSeparator defaultAlign = new JXTitledSeparator();
        // default is LEADING
       defaultAlign.setTitle("default");
        // default is LEADING
       Border lineBorder = BorderFactory.createLineBorder(Color.MAGENTA);
        Border defaultBorder = new TitledBorder(lineBorder, "default");
        defaultAlign.setBorder(defaultBorder);
        box.add(defaultAlign);

 
        JXTitledSeparator leading = new JXTitledSeparator();
        leading.setTitle("leading");
        leading.setHorizontalAlignment(SwingConstants.LEADING);
        Border leadingBorder = new TitledBorder(lineBorder, "leading", TitledBorder.LEADING, TitledBorder.TOP);
        leading.setBorder(leadingBorder);
        box.add(leading);

        JXTitledSeparator trailing = new JXTitledSeparator();
        trailing.setTitle("trailing");
        trailing.setHorizontalAlignment(SwingConstants.TRAILING);
        Border trailingBorder = new TitledBorder(lineBorder, "trailing", TitledBorder.TRAILING, TitledBorder.TOP);
        trailing.setBorder(trailingBorder);
        box.add(trailing);
        
        JXTitledSeparator left = new JXTitledSeparator();
        left.setTitle("left");
        left.setHorizontalAlignment(SwingConstants.LEFT);
        Border leftBorder = new TitledBorder(lineBorder, "left", TitledBorder.LEFT, TitledBorder.TOP);
        left.setBorder(leftBorder);
        box.add(left);
        
        JXTitledSeparator right = new JXTitledSeparator();
        right.setTitle("right");
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        Border rightBorder = new TitledBorder(lineBorder, "right", TitledBorder.RIGHT, TitledBorder.TOP);
        right.setBorder(rightBorder);
        box.add(right);
        
        final JXFrame frame = wrapInFrame(box, "Bidi-compliance");
        Action toggleComponentOrientation = new AbstractAction("toggle orientation") {

            public void actionPerformed(ActionEvent e) {
                ComponentOrientation current = frame.getComponentOrientation();
                if (current == ComponentOrientation.LEFT_TO_RIGHT) {
                    frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                } else {
                    frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                }
                
                // needed to make the change show 
                // looks like a problem somewhere along the container hierarchy...
                frame.getRootPaneExt().revalidate();
                // this is to make the borders take up the new orientation
                frame.repaint();
//                frame.getRootPaneExt().repaint();
//                frame.setSize(frame.getSize());
            }

        };
        addAction(frame, toggleComponentOrientation);
        // titledSeparator freaks with prefSize, need to set fixed
        frame.setSize(200, 400);
        frame.setVisible(true);
        
    }
    
}
