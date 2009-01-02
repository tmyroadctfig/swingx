package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager2;
import java.awt.geom.AffineTransform;

public class JXRotatingPanel extends JXPanel {

    private static final long serialVersionUID = 5658793551106862537L;

    public static class RotaLayout implements LayoutManager2 {

        private Component child;
        private double angle;
        private boolean invalidated = true;
        private int width;
        private int height;
        private double pw;
        private double ph;
        private int x;
        private int y;

        public void addLayoutComponent(Component comp, Object constraints) {
            child = comp;
            angle = (Double) constraints;
        }

        public float getLayoutAlignmentX(Container target) {
            return 0;
        }

        public float getLayoutAlignmentY(Container target) {
            return 0;
        }

        public void invalidateLayout(Container target) {
            invalidated = true;
        }

        public Dimension maximumLayoutSize(Container target) {
            return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
        }

        public void addLayoutComponent(String name, Component comp) {
            throw new UnsupportedOperationException("addLayoutComponent(String name, Component comp) is not supported");

        }

        public void layoutContainer(Container parent) {
            if (invalidated) {
                recalculate(parent);
                invalidated = false;
            }
            child.setBounds(x, y, width, height);
        }

        private void recalculate(Container parent) {
            Dimension size = parent.getSize();
            Dimension ch = child.getPreferredSize();
            double sinA = Math.sin(angle);
            double cosA = Math.sin(angle);
            double ph2 = cosA * ch.getHeight();
            double ph1 = sinA * ch.getWidth();;
            ph = ph1 + ph2;
            if (ph > size.getHeight()) {
                // preferred height of child is too big, fit it inside
                // TODO: find out maximum fitable size
            }
            double pw1 = cosA * ch.getWidth();
            double pw2 = sinA * ch.getHeight();
            pw = pw1 + pw2;
            if (pw > size.getWidth()) {
                // preferred width is too big
                // TODO: find out maximum fitable size
            }
            double difH = size.getHeight() - ph;
            double difW = size.getWidth() - pw;
            double xnt = difW/2;
            double ynt = ph1 + difH/2;
            x = (int)Math.floor(xnt);
            y = (int) Math.floor(ynt);
            height = (int)Math.floor(ch.getHeight());
            width = (int)Math.floor(ch.getWidth());

//            double c = Math.sqrt(xnt * xnt + ynt * ynt);
//            double tau = Math.tan(xnt/ynt);
//            double xt = Math.sin(tau - angle) * c;
//            double yt = Math.cos(tau - angle) * c;
//            x = (int) Math.floor(xt);
//            y = (int) Math.floor(yt);
            System.out.println("pw1: " + pw1 + ", pw2: " + pw2 + ", difw: " + difW + " xnt: " + xnt);
            System.out.println("x:" + x + ", y:" + y + ", w:" + width + ", h:" + height + ", || ps:" + size);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension (0,0);
        }

        public Dimension preferredLayoutSize(Container parent) {
            if (invalidated) {
                recalculate(parent);
                invalidated = false;
            }
            return new Dimension(width, height);
        }

        public void removeLayoutComponent(Component comp) {
            child = null;
            angle = 0;
        }

        protected double getAngle() {
            return angle;
        }

        protected void setAngle(double angle) {
            this.angle = angle;
        }
    }

    private double angle;

    public JXRotatingPanel() {
        super(new RotaLayout());
    }

    public JXRotatingPanel(boolean isDoubleBuffered) {
        super(new RotaLayout(), isDoubleBuffered);
    }

    @Override
    protected void paintChildren(Graphics g) {
        RotaLayout rl = ((RotaLayout)getLayout());
        int wh = getWidth()/2;
        int hh = getHeight()/2;
        System.out.println("wh: " + wh + ", hh: " + hh);
        ((Graphics2D)g).setTransform(AffineTransform.getRotateInstance(-angle));
        super.paintChildren(g);
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
        ((RotaLayout)getLayout()).setAngle(angle);
    }

}
