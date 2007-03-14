/*
 * BusySpinner.java
 *
 * Created on February 13, 2007, 5:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;
import org.jdesktop.swingx.painter.BusyPainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.PainterIcon;
/**
 *
 * @author joshy
 */

public class JXBusyLabel extends JLabel {
    
    private BusyPainter busyPainter;
    private Timer busy;
    
    /** Creates a new instance of BusySpinner */
    public JXBusyLabel() {
        busyPainter = new BusyPainter();
        busyPainter.setBaseColor(Color.LIGHT_GRAY);
        busyPainter.setHighlightColor(getForeground());
        Dimension dim = new Dimension(26,26);
        PainterIcon icon = new PainterIcon(dim);
        icon.setPainter(busyPainter);
        this.setIcon(icon);
    }
    
    
    public void startAnimation() {
        if(busy != null) {
            stopAnimation();
        }
        
        busy = new Timer(100, new ActionListener() {
            int frame = 8;
            public void actionPerformed(ActionEvent e) {
                frame = (frame+1)%8;
                busyPainter.setFrame(frame);
                repaint();
            }
        });
        //busy = PropertySetter.createAnimator(1500,busyPainter,"frame",8,16);
        //busy.addTarget(new TimingTargetAdapter() {
       //     public void timingEvent(float fraction) { repaint(); }
        //});
        //busy.setRepeatBehavior(Animator.RepeatBehavior.LOOP);
        //busy.setRepeatCount(Animator.INFINITE);
        busy.start();
    }
    
    public void stopAnimation() {
        busy.stop();
        busyPainter.setFrame(-1);
        repaint();
        busy = null;
    }
    
    public void setHighlightColor(Color hi) {
        if(busyPainter != null) {
            busyPainter.setHighlightColor(hi);
        }
    }

    public void setBaseColor(Color color) {
        if(busyPainter != null) {
            busyPainter.setBaseColor(color);
        }
    }
    
    public static void main(String ... args) {
        JFrame frame = new JFrame("test");
        JXBusyLabel label = new JXBusyLabel();
        //label.setBaseColor(Color.BLUE);
        //label.setHighlightColor(Color.GREEN);
        frame.add(label);
        frame.pack();
        frame.setVisible(true);
        label.startAnimation();
        
    }
}
