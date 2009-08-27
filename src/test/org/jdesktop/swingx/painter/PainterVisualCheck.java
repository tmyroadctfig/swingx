/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx.painter;

import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.image.FastBlurFilter;

/**
 * JW: renamed from PainterInteractiveTest to fix the build failure. Revisit!
 * @author rbair
 */
public class PainterVisualCheck extends RichInteractiveTestCase {

    public void testJXButtonTextChangeWithBlur() {
        //Creates a button with a blur on the text. On click events, the
        //button changes its text. If things are working, then the text
        //on the button will be changed, and reblurred. In other words,
        //the painter will be invalid (cache cleared), and updated on the
        //next paint.
        final JXButton button = new JXButton();
        AbstractPainter<?> p = (AbstractPainter<?>)button.getForegroundPainter();
        p.setFilters(new FastBlurFilter());
        button.addActionListener(new ActionListener(){
            private String[] values = new String[] {"Hello", "Goodbye", "SwingLabs", "Turkey Bowl"};
            private int index = 1;
            public void actionPerformed(ActionEvent ae) {
                button.setText(values[index]);
                index++;
                if (index >= values.length) {
                    index = 0;
                }
            }
        });

        JPanel pa = new JPanel();
        pa.add(button);
        assertTrue(showTest(pa, "JXButton text-change with a blur",
                "On click events, the button changes its text. " +
                "If things are working, then the text on the button " +
                "will be changed, and reblurred."));
    }

    public void testCacheWithBlurAndAnimation() {
        //This test is also covered (more or less) by the regression tests.
        //This is a second line of defense test, because if the regression test
        //is messed up, it will be easy to notice here.

        //I simply have a rectangle painter and a text painter, and the text changes
        //over time. This should cause the text painter to be invalidated. Likewise,
        //there is a drop-shadow like blur applied to the whole thing.

        final JXPanel p = new JXPanel();
        final String[] messages = new String[] {
                "These are the times",
                "That try men's souls",
                "And something else",
                "I can't quite remember"
        };
        final TextPainter text = new TextPainter();
        text.setText(messages[0]);
        CompoundPainter<?> cp = new CompoundPainter<Object>(
                new RectanglePainter<Object>(),
                text
        );
        cp.setFilters(new FastBlurFilter());
        p.setBackgroundPainter(cp);

        Timer t = new Timer(1000, new ActionListener() {
            int index = 1;
            public void actionPerformed(ActionEvent ae) {
                text.setText(messages[index]);
                index++;
                if (index >= messages.length) {
                    index = 0;
                }
                p.repaint();
            }
        });

        t.start();

        try {
            assertTrue(showTest(p, "Test cache works with blur and animation",
                    "In this setup, there is a TextPainter within a CompoundPainter. " +
                    "The CompoundPainter has a filter applied, and uses caching. Thus " +
                    "when the text changes, it is invalidating itself, and the " +
                    "CompoundPainter should detect that and invalidate its cache. " +
                    "If you see the text changing, then this test is passing (or the " +
                    "cache isn't working, but whatever :-))"));
        } finally {
            t.stop();
        }
    }

    public void testCacheWorks() {
        JXPanel p = new JXPanel();
        JLabel label1 = new JLabel();
        JLabel label2 = new JLabel();
        SlowPainter painter1 = new SlowPainter();
        painter1.setCacheable(true);
        SlowPainter painter2 = new SlowPainter();
        painter2.setCacheable(false);

        p.setLayout(new GridBagLayout());
        p.add(label1, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        p.add(label2, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        //fire off two background threads, and let them run until the test is over
        Thread t1 = new SlowTestThread(painter1, label1, "Cached Painter FPS: ");
        t1.start();

        Thread t2 = new SlowTestThread(painter2, label2, "Normal Painter FPS: ");
        t2.start();

        try {
            assertTrue(showTest(p, "Test cache works",
                    "Simply tests that rendering speed for a cached painter is faster than " +
                    "rendering speed of a non cached painter. Simply compare the two FPS counters. " +
                    "(Note, I introduce a purposeful 1 second delay on " +
                    "the non-cached version, to ensure that whenever painting occurs, it will " +
                    "be slower than using the cache. Also, both painters are run on background " +
                    "threads so as not to block the GUI)"));
        } finally {
            t1.interrupt();
            t2.interrupt();
        }
    }

    private static final class SlowPainter extends AbstractPainter<Object> {
        @Override
        protected void doPaint(Graphics2D g, Object component, int width, int height) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {}
        }
        @Override
        protected boolean shouldUseCache() {
            return isCacheable();
        }
    }

    private static final class SlowTestThread extends Thread {
        private Graphics2D g = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB).createGraphics();
        private Painter<?> p = null;
        private JLabel label = null;
        private String prefix = null;

        public SlowTestThread(SlowPainter p, JLabel l, String s) {
            this.p = p;
            this.label = l;
            this.prefix = s;
        }

        @Override
        public void run() {
            while(true) {
                double start = System.currentTimeMillis();
                p.paint(g, null, 10, 10);
                double stop = System.currentTimeMillis();
                final double fps = 1000.0/(stop - start);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        label.setText(prefix + fps);
                    }
                });
            }
        }
    }
}
