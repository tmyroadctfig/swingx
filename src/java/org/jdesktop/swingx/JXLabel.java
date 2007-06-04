/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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

package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SizeRequirements;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.CompositeView;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.GlyphView;
import javax.swing.text.IconView;
import javax.swing.text.JTextComponent;
import javax.swing.text.LabelView;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.ParagraphView;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.TabExpander;
import javax.swing.text.TabableView;
import javax.swing.text.Utilities;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.Position.Bias;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.Painter;

/**
 * <p>
 * A {@link javax.swing.JLabel} subclass which supports {@link org.jdesktop.swingx.painter.Painter}s, multi-line text,
 * and text rotation.
 * </p>
 * 
 * <p>
 * Painter support consists of the <code>foregroundPainter</code> and <code>backgroundpainter</code> properties. The
 * <code>backgroundPainter</code> refers to a painter responsible for painting <i>beneath</i> the text and icon. This
 * painter, if set, will paint regardless of the <code>opaque</code> property. If the background painter does not
 * fully paint each pixel, then you should make sure the <code>opaque</code> property is set to false.
 * </p>
 * 
 * <p>
 * The <code>foregroundPainter</code> is responsible for painting the icon and the text label. If no foregroundPainter
 * is specified, then the look and feel will paint the label. Note that if opaque is set to true and the look and feel
 * is rendering the foreground, then the foreground <i>may</i> paint over the background. Most look and feels will
 * paint a background when <code>opaque</code> is true. To avoid this behavior, set <code>opaque</code> to false.
 * </p>
 * 
 * <p>
 * Since JXLabel is not opaque by default (<code>isOpaque()</code> returns false), neither of these problems
 * typically present themselves.
 * </p>
 * 
 * <p>
 * Multi-line text is enabled via the <code>lineWrap</code> property. Simply set it to true. By default, line wrapping
 * occurs on word boundaries.
 * </p>
 * 
 * <p>
 * The text (actually, the entire foreground and background) of the JXLabel may be rotated. Set the
 * <code>rotation</code> property to specify what the rotation should be.
 * </p>
 * TODO not yet determined what API this will use.
 * 
 * @author joshua.marinacci@sun.com
 * @author rbair
 * @author rah
 * @author mario_cesar
 */
public class JXLabel extends JLabel {
    // textOrientation value declarations...
    public static final double NORMAL = 0;

    public static final double INVERTED = Math.PI;

    public static final double VERTICAL_LEFT = 3 * Math.PI / 2;

    public static final double VERTICAL_RIGHT = Math.PI / 2;

    private double textRotation = NORMAL;

    private boolean painting = false;

    private Painter foregroundPainter;

    private Painter backgroundPainter;

    private boolean multiLine;

    private int pWidth;

    private int pHeight;

    private boolean ignoreRepaint;

    private static final String oldRendererKey = "was" + BasicHTML.propertyKey;

    /**
     * Create a new JXLabel. This has the same semantics as creating a new JLabel.
     */
    public JXLabel() {
        super();
        initPainterSupport();
        initLineWrapSupport();
    }

    public JXLabel(Icon image) {
        super(image);
        initPainterSupport();
        initLineWrapSupport();
    }

    public JXLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
        initPainterSupport();
        initLineWrapSupport();
    }

    /**
     * Create a new JXLabel with the given text as the text for the label. This is shorthand for:
     * 
     * <pre><code>
     * JXLabel label = new JXLabel();
     * label.setText(&quot;Some Text&quot;);
     * </code></pre>
     * 
     * @param text the text to set.
     */
    public JXLabel(String text) {
        super(text);
        initPainterSupport();
        initLineWrapSupport();
    }

    public JXLabel(String text, Icon image, int horizontalAlignment) {
        super(text, image, horizontalAlignment);
        initPainterSupport();
        initLineWrapSupport();
    }

    public JXLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        initPainterSupport();
        initLineWrapSupport();
    }

    private void initPainterSupport() {
        foregroundPainter = new AbstractPainter() {
            protected void doPaint(Graphics2D g, Object object, int width, int height) {
                Insets i = getInsets();
                g = (Graphics2D) g.create(-i.left, -i.top, width, height);
                JXLabel.super.paintComponent(g);
                g.dispose();
            }
        };
    }

    /**
     * Helper method for initializing multiline support.
     */
    private void initLineWrapSupport() {
        addPropertyChangeListener(new MultiLineSupport());
    }

    /**
     * Returns the current foregroundPainter. This is a bound property. By default the foregroundPainter will be an
     * internal painter which executes the standard painting code (paintComponent()).
     * 
     * @return the current foreground painter.
     */
    public final Painter getForegroundPainter() {
        return foregroundPainter;
    }

    /**
     * Sets a new foregroundPainter on the label. This will replace the existing foreground painter. Existing painters
     * can be wrapped by using a CompoundPainter.
     * 
     * @param painter
     */
    public void setForegroundPainter(Painter painter) {
        Painter old = this.getForegroundPainter();
        this.foregroundPainter = painter;
        firePropertyChange("foregroundPainter", old, getForegroundPainter());
        repaint();
    }

    /**
     * Sets a Painter to use to paint the background of this component By default there is already a single painter
     * installed which draws the normal background for this component according to the current Look and Feel. Calling
     * <CODE>setBackgroundPainter</CODE> will replace that existing painter.
     * 
     * @param p the new painter
     * @see #getBackgroundPainter()
     */
    public void setBackgroundPainter(Painter p) {
        Painter old = getBackgroundPainter();
        backgroundPainter = p;
        firePropertyChange("backgroundPainter", old, getBackgroundPainter());
        repaint();
    }

    /**
     * Returns the current background painter. The default value of this property is a painter which draws the normal
     * JPanel background according to the current look and feel.
     * 
     * @return the current painter
     * @see #setBackgroundPainter(Painter)
     */
    public final Painter getBackgroundPainter() {
        return backgroundPainter;
    }

    /**
     * Gets current value of text rotation in rads.
     * 
     * @return
     * @see #setTextRotation(double)
     */
    public double getTextRotation() {
        return textRotation;
    }

    /**
     * Sets new value for text rotation. The value can be anything in range <0,2PI>. Note that although property name
     * suggests only text rotation, the whole foreground painter is rotated in fact. Due to various reasons it is
     * strongly discouraged to access any size related properties of the label from other threads then EDT when this
     * property is set.
     * 
     * @param textOrientation Value for text rotation in range <0,2PI>
     * @see #getTextRotation()
     */
    public void setTextRotation(double textOrientation) {
        double old = getTextRotation();
        this.textRotation = textOrientation;
        if (old != getTextRotation()) {
            firePropertyChange("textRotation", old, getTextRotation());
        }
        repaint();
    }

    /**
     * Enables line wrapping support for plain text. By default this support is disabled to mimic default of the JLabel.
     * Value of this property has no effect on HTML text.
     * 
     * @param b the new value
     * @see #isMultiLine()
     */
    public void setLineWrap(boolean b) {
        boolean old = isLineWrap();
        this.multiLine = b;
        if (isLineWrap() != old) {
            firePropertyChange("lineWrap", old, isLineWrap());
            if (getForegroundPainter() != null) {
                // XXX There is a bug here. In order to make painter work with this, caching has to be disabled
                ((AbstractPainter) getForegroundPainter()).setCacheable(!b);
            }
            repaint();
        }
    }

    /**
     * Returns the current status of line wrap support. The default value of this property is false to mimic default
     * JLabel behavior. Value of this property has no effect on HTML text.
     * 
     * @return the current multiple line splitting status
     * @see #setMultiLine(boolean)
     */
    public boolean isLineWrap() {
        return this.multiLine;
    }

    /**
     * @param g graphics to paint on
     */
    @Override
    protected void paintComponent(Graphics g) {
        // resizing the text view causes recursive callback to the paint down the road. In order to prevent such
        // computationaly intensive series of repiants every call to paint is skipped while top most call is being
        // executed.
        if (ignoreRepaint) {
            return;
        }
        if (backgroundPainter == null && foregroundPainter == null) {
            super.paintComponent(g);
        } else {
            Graphics2D g2 = (Graphics2D) g.create();
            Insets i = getInsets();
            g2.translate(i.left, i.top);
            pWidth = getWidth() - i.left - i.right;
            pHeight = getHeight() - i.top - i.bottom;
            if (backgroundPainter != null) {
                backgroundPainter.paint(g2, this, pWidth, pHeight);
            }
            if (foregroundPainter != null) {
                double tx = (double) getWidth();
                double ty = (double) getHeight();

                // orthogonal cases are most likely the most often used ones, so give them preferential treatment.
                if ((textRotation > 4.697 && textRotation < 4.727) || (textRotation > 1.555 && textRotation < 1.585)) {
                    // vertical
                    int tmp = pHeight;
                    pHeight = pWidth;
                    pWidth = tmp;
                    tx = pWidth;
                    ty = pHeight;
                } else if ((textRotation > -0.015 && textRotation < 0.015)
                        || (textRotation > 3.140 && textRotation < 3.1430)) {
                    // normal & inverted
                    pHeight = getHeight();
                    pWidth = getWidth();
                } else {
                    // the rest of it. Calculate best rectangle that fits the bounds. "Best" is considered one that
                    // allows whole text to fit in, spanned on preferred axis (X). If that doesn't work, fit the text
                    // inside square with diagonal equal min(height, width) (Should be the largest rectangular area that
                    // fits in, math proof available upon request)

                    ignoreRepaint = true;
                    double square = Math.min(getHeight(), getWidth()) * Math.cos(Math.PI / 4d);

                    View v = (View) getClientProperty(BasicHTML.propertyKey);
                    if (v == null) {
                        // no html and no wrapline enabled means no view
                        // ... find another way to figure out the heigh
                        ty = getFontMetrics(getFont()).getHeight();
                        double cw = (getWidth() - Math.abs(ty * Math.sin(textRotation)))
                                / Math.abs(Math.cos(textRotation));
                        double ch = (getHeight() - Math.abs(ty * Math.cos(textRotation)))
                                / Math.abs(Math.sin(textRotation));
                        // min of whichever is above 0 (!!! no min of abs values)
                        tx = cw < 0 ? ch : ch > 0 ? Math.min(cw, ch) : cw;
                    } else {
                        float w = v.getPreferredSpan(View.X_AXIS);
                        float h = v.getPreferredSpan(View.Y_AXIS);
                        double c = w;
                        double alpha = textRotation;// % (Math.PI/2d);
                        boolean ready = false;
                        while (!ready) {
                            // shorten the view len until line break is forced
                            while (h == v.getPreferredSpan(View.Y_AXIS)) {
                                w -= 10;
                                v.setSize(w, h);
                            }
                            if (w < square || h > square) {
                                // text is too long to fit no matter what. Revert shape to square since that is the
                                // best option (1st derivation for area size of rotated rect in rect is equal 0 for
                                // rotated rect with equal w and h i.e. for square)
                                w = h = (float) square;
                                // set view height to something big to prevent recursive resize/repaint requests
                                v.setSize(w, 100000);
                                break;
                            }
                            // calc avail width with new view height
                            h = v.getPreferredSpan(View.Y_AXIS);
                            double cw = (getWidth() - Math.abs(h * Math.sin(alpha))) / Math.abs(Math.cos(alpha));
                            double ch = (getHeight() - Math.abs(h * Math.cos(alpha))) / Math.abs(Math.sin(alpha));
                            // min of whichever is above 0 (!!! no min of abs values)
                            c = cw < 0 ? ch : ch > 0 ? Math.min(cw, ch) : cw;
                            // make it one pix smaller to ensure text is not cut on the left
                            c--;
                            if (c > w) {
                                v.setSize((float) c, 10 * h);
                                ready = true;
                            } else {
                                v.setSize((float) c, 10 * h);
                                if (v.getPreferredSpan(View.Y_AXIS) > h) {
                                    // set size back to figure out new line break and height after
                                    v.setSize(w, 10 * h);
                                } else {
                                    w = (float) c;
                                    ready = true;
                                }
                            }
                        }

                        tx = Math.floor(w);// xxx: watch out for first letter on each line missing some pixs!!!
                        ty = h;
                    }
                    pWidth = (int) tx;
                    pHeight = (int) ty;
                    ignoreRepaint = false;
                }
                double wx = Math.sin(textRotation) * ty + Math.cos(textRotation) * tx;
                double wy = Math.sin(textRotation) * tx + Math.cos(textRotation) * ty;
                double x = (getWidth() - wx) / 2 + Math.sin(textRotation) * ty;
                double y = (getHeight() - wy) / 2;
                g2.translate(x, y);
                g2.rotate(textRotation);

                painting = true;
                // uncomment to highlight text area
                // Color c = g2.getColor();
                // g2.setColor(Color.RED);
                // g2.fillRect(0, 0, getWidth(), getHeight());
                // g2.setColor(c);
                foregroundPainter.paint(g2, this, pWidth, pHeight);
                painting = false;
                pWidth = 0;
                pHeight = 0;
            }
            g2.dispose();
        }
    }

    @Override
    public void repaint() {
        if (ignoreRepaint) {
            return;
        }
        super.repaint();
    }

    @Override
    public void repaint(int x, int y, int width, int height) {
        if (ignoreRepaint) {
            return;
        }
        super.repaint(x, y, width, height);
    }

    @Override
    public void repaint(long tm) {
        if (ignoreRepaint) {
            return;
        }
        super.repaint(tm);
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        if (ignoreRepaint) {
            return;
        }
        super.repaint(tm, x, y, width, height);
    }

    // ----------------------------------------------------------
    // textOrientation magic
    @Override
    public int getHeight() {
        int retValue = super.getHeight();
        if (painting) {
            retValue = pHeight;
        }
        return retValue;
    }

    @Override
    public int getWidth() {
        int retValue = super.getWidth();
        if (painting) {
            retValue = pWidth;
        }
        return retValue;
    }

    // ----------------------------------------------------------
    // WARNING:
    // Anything below this line is related to lineWrap support and can be safely ignored unless
    // in need to mess around with the implementation details.
    // ----------------------------------------------------------
    // FYI: This class doesn't reinvent line wrapping. Instead it makes use of existing support
    // made for JTextComponent/JEditorPane.
    // All the classes below named Alter* are verbatim copy of swing.text.* classes made to
    // overcome package visibility of some of the code. All other classes here, when their name
    // matches corresponding class from swing.text.* package are copy of the class with removed
    // support for highlighting selection. In case this is ever merged back to JDK all of this
    // can be safely removed as long as corresponding swing.text.* classes make appropriate checks
    // before casting JComponent into JTextComponent to find out selected region since
    // JLabel/JXLabel does not support selection of the text.

    private static class MultiLineSupport implements PropertyChangeListener {

        private static final String HTML = "<html>";

        private static ViewFactory basicViewFactory;

        private static BasicEditorKit basicFactory;

        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            JXLabel src = (JXLabel) evt.getSource();
            if (src.isLineWrap()) {
                if ("font".equals(name) || "foreground".equals(name)) {
                    updateRenderer(src);
                } else if ("text".equals(name)) {
                    if (isHTML((String) evt.getOldValue()) && evt.getNewValue() != null
                            && !isHTML((String) evt.getNewValue())) {
                        // was html , but is not
                        if (src.getClientProperty(oldRendererKey) == null
                                && src.getClientProperty(BasicHTML.propertyKey) != null) {
                            src.putClientProperty(oldRendererKey, src.getClientProperty(BasicHTML.propertyKey));
                        }
                        src.putClientProperty(BasicHTML.propertyKey, createView(src));
                    } else if (!isHTML((String) evt.getOldValue()) && evt.getNewValue() != null
                            && !isHTML((String) evt.getNewValue())) {
                        // wasn't html and isn't
                        updateRenderer(src);
                    } else {
                        // either was html and is html or wasn't html, but is html
                        restoreHtmlRenderer(src);
                    }
                } else if ("lineWrap".equals(name) && !isHTML(src.getText())) {
                    src.putClientProperty(BasicHTML.propertyKey, createView(src));
                }
            } else if ("lineWrap".equals(name)) {
                restoreHtmlRenderer(src);
            }
        }

        private static void restoreHtmlRenderer(JXLabel src) {
            Object current = src.getClientProperty(BasicHTML.propertyKey);
            if (current == null || current instanceof Renderer) {
                src.putClientProperty(BasicHTML.propertyKey, src.getClientProperty(oldRendererKey));
            }
        }

        private static boolean isHTML(String s) {
            return s != null && s.toLowerCase().startsWith(HTML);
        }

        private static View createView(JXLabel c) {
            BasicEditorKit kit = getFactory();
            Document doc = kit.createDefaultDocument(c.getFont(), c.getForeground());
            Reader r = new StringReader(c.getText());
            try {
                kit.read(r, doc, 0);
            } catch (Throwable e) {
            }
            ViewFactory f = kit.getViewFactory();
            View hview = f.create(doc.getDefaultRootElement());
            View v = new Renderer(c, f, hview, true);
            return v;
        }

        public static void updateRenderer(JXLabel c) {
            View value = null;
            View oldValue = (View) c.getClientProperty(BasicHTML.propertyKey);
            if (oldValue == null || oldValue instanceof Renderer) {
                value = createView(c);
            }
            if (value != oldValue && oldValue != null) {
                for (int i = 0; i < oldValue.getViewCount(); i++) {
                    oldValue.getView(i).setParent(null);
                }
            }
            c.putClientProperty(BasicHTML.propertyKey, value);
        }

        private static BasicEditorKit getFactory() {
            if (basicFactory == null) {
                basicViewFactory = new BasicViewFactory();
                basicFactory = new BasicEditorKit();
            }
            return basicFactory;
        }

        private static class BasicEditorKit extends StyledEditorKit {
            public Document createDefaultDocument(Font defaultFont, Color foreground) {
                BasicDocument doc = new BasicDocument(defaultFont, foreground);
                doc.setAsynchronousLoadPriority(Integer.MAX_VALUE);
                return doc;
            }

            public ViewFactory getViewFactory() {
                return basicViewFactory;
            }
        }
    }

    private static class BasicViewFactory implements ViewFactory {
        public View create(Element elem) {

            String kind = elem.getName();
            View view = null;
            if (kind == null) {
                // default to text display
                view = new LabelView(elem);
            } else if (kind.equals(AbstractDocument.ContentElementName)) {
                view = new LabelView(elem);
            } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                view = new ParagraphView(elem);
            } else if (kind.equals(AbstractDocument.SectionElementName)) {
                view = new BoxView(elem, View.Y_AXIS);
            } else if (kind.equals(StyleConstants.ComponentElementName)) {
                view = new ComponentView(elem);
            } else if (kind.equals(StyleConstants.IconElementName)) {
                view = new IconView(elem);
            }
            return view;
        }
    }

    static class BasicDocument extends DefaultStyledDocument {
        BasicDocument(Font defaultFont, Color foreground) {
            setFontAndColor(defaultFont, foreground);
        }

        private void setFontAndColor(Font font, Color fg) {
            if (fg != null) {

                MutableAttributeSet attr = new SimpleAttributeSet();
                StyleConstants.setForeground(attr, fg);
                getStyle("default").addAttributes(attr);
            }

            if (font != null) {
                MutableAttributeSet attr = new SimpleAttributeSet();
                StyleConstants.setFontFamily(attr, font.getFamily());
                getStyle("default").addAttributes(attr);

                attr = new SimpleAttributeSet();
                StyleConstants.setFontSize(attr, font.getSize());
                getStyle("default").addAttributes(attr);

                attr = new SimpleAttributeSet();
                StyleConstants.setBold(attr, font.isBold());
                getStyle("default").addAttributes(attr);

                attr = new SimpleAttributeSet();
                StyleConstants.setItalic(attr, font.isItalic());
                getStyle("default").addAttributes(attr);
            }

            MutableAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setSpaceAbove(attr, 0f);
            getStyle("default").addAttributes(attr);

            // TODO: add rest of the style stuff
            // ... if anyone ever want's this (stuff like justification, etc.)
            // attr = new SimpleAttributeSet();
            // StyleConstants.setLeftIndent(attr,5f);
            // getStyle("default").addAttributes(attr);

            // MutableAttributeSet attr = new SimpleAttributeSet();
            // StyleConstants.setAlignment(attr, StyleConstants.ALIGN_JUSTIFIED);
            // getStyle("default").addAttributes(attr);

        }
    }

    /**
     * Root text view that acts as an renderer.
     */
    static class Renderer extends AlterWrappedPlainView {

        JXLabel host;

        Renderer(JXLabel c, ViewFactory f, View v, boolean wordWrap) {
            super(null, wordWrap);
            factory = f;
            view = v;
            view.setParent(this);
            host = c;
            // initially layout to the preferred size
            setSize(view.getPreferredSpan(X_AXIS), view.getPreferredSpan(Y_AXIS));
        }

        /**
         * Fetches the attributes to use when rendering. At the root level there are no attributes. If an attribute is
         * resolved up the view hierarchy this is the end of the line.
         */
        public AttributeSet getAttributes() {
            return null;
        }

        /**
         * Renders the view.
         * 
         * @param g the graphics context
         * @param allocation the region to render into
         */
        public void paint(Graphics g, Shape allocation) {
            Rectangle alloc = allocation.getBounds();
            view.setSize(alloc.width, alloc.height);
            if (g.getClipBounds() == null) {
                g.setClip(alloc);
                view.paint(g, allocation);
                g.setClip(null);
            } else {
                view.paint(g, allocation);
            }
        }

        /**
         * Sets the view parent.
         * 
         * @param parent the parent view
         */
        public void setParent(View parent) {
            throw new Error("Can't set parent on root view");
        }

        /**
         * Returns the number of views in this view. Since this view simply wraps the root of the view hierarchy it has
         * exactly one child.
         * 
         * @return the number of views
         * @see #getView
         */
        public int getViewCount() {
            return 1;
        }

        /**
         * Gets the n-th view in this container.
         * 
         * @param n the number of the view to get
         * @return the view
         */
        public View getView(int n) {
            return view;
        }

        /**
         * Returns the document model underlying the view.
         * 
         * @return the model
         */
        public Document getDocument() {
            return view == null ? null : view.getDocument();
        }

        /**
         * Sets the view size.
         * 
         * @param width the width
         * @param height the height
         */
        public void setSize(float width, float height) {
            // this.width = (int) width;
            view.setSize(width, height);
        }

        /**
         * Fetches the container hosting the view. This is useful for things like scheduling a repaint, finding out the
         * host components font, etc. The default implementation of this is to forward the query to the parent view.
         * 
         * @return the container
         */
        public Container getContainer() {
            return host;
        }

        /**
         * Fetches the factory to be used for building the various view fragments that make up the view that represents
         * the model. This is what determines how the model will be represented. This is implemented to fetch the
         * factory provided by the associated EditorKit.
         * 
         * @return the factory
         */
        public ViewFactory getViewFactory() {
            return factory;
        }

        private View view;

        private ViewFactory factory;

    }

    /**
     * <code>AnotherAbstractDocument</code>
     * 
     * @inheritDoc
     */
    private static abstract class AlterAbstractDocument extends AbstractDocument implements Document, Serializable {

        /**
         * Document property that indicates whether internationalization functions such as text reordering or reshaping
         * should be performed. This property should not be publicly exposed, since it is used for implementation
         * convenience only. As a side effect, copies of this property may be in its subclasses that live in different
         * packages (e.g. HTMLDocument as of now), so those copies should also be taken care of when this property needs
         * to be modified.
         */
        static final String I18NProperty = "i18n";

        public AlterAbstractDocument(Content data, AttributeContext context) {
            super(data, context);
        }

        public AlterAbstractDocument(Content data) {
            super(data);
        }

    }

    /**
     * Internally created view that has the purpose of holding the views that represent the children of the paragraph
     * that have been arranged in rows.
     */
    class Row extends AlterBoxView {

        private int justification;

        private float lineSpacing;

        /** Indentation for the first line, from the left inset. */
        protected int firstLineIndent = 0;

        Row(Element elem) {
            super(elem, View.X_AXIS);
        }

        /**
         * This is reimplemented to do nothing since the paragraph fills in the row with its needed children.
         */
        protected void loadChildren(ViewFactory f) {
        }

        /**
         * Fetches the attributes to use when rendering. This view isn't directly responsible for an element so it
         * returns the outer classes attributes.
         */
        public AttributeSet getAttributes() {
            View p = getParent();
            return (p != null) ? p.getAttributes() : null;
        }

        public float getAlignment(int axis) {
            if (axis == View.X_AXIS) {
                switch (justification) {
                case StyleConstants.ALIGN_LEFT:
                    return 0;
                case StyleConstants.ALIGN_RIGHT:
                    return 1;
                case StyleConstants.ALIGN_CENTER:
                    return 0.5f;
                case StyleConstants.ALIGN_JUSTIFIED:
                    float rv = 0.5f;
                    // if we can justifiy the content always align to
                    // the left.
                    if (isJustifiableDocument()) {
                        rv = 0f;
                    }
                    return rv;
                }
            }
            return super.getAlignment(axis);
        }

        /**
         * Provides a mapping from the document model coordinate space to the coordinate space of the view mapped to it.
         * This is implemented to let the superclass find the position along the major axis and the allocation of the
         * row is used along the minor axis, so that even though the children are different heights they all get the
         * same caret height.
         * 
         * @param pos the position to convert
         * @param a the allocated region to render into
         * @return the bounding box of the given position
         * @exception BadLocationException if the given position does not represent a valid location in the associated
         *            document
         * @see View#modelToView
         */
        public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
            Rectangle r = a.getBounds();
            View v = getViewAtPosition(pos, r);
            if ((v != null) && (!v.getElement().isLeaf())) {
                // Don't adjust the height if the view represents a branch.
                return super.modelToView(pos, a, b);
            }
            r = a.getBounds();
            int height = r.height;
            int y = r.y;
            Shape loc = super.modelToView(pos, a, b);
            r = loc.getBounds();
            r.height = height;
            r.y = y;
            return r;
        }

        /**
         * Range represented by a row in the paragraph is only a subset of the total range of the paragraph element.
         * 
         * @see View#getRange
         */
        public int getStartOffset() {
            int offs = Integer.MAX_VALUE;
            int n = getViewCount();
            for (int i = 0; i < n; i++) {
                View v = getView(i);
                offs = Math.min(offs, v.getStartOffset());
            }
            return offs;
        }

        public int getEndOffset() {
            int offs = 0;
            int n = getViewCount();
            for (int i = 0; i < n; i++) {
                View v = getView(i);
                offs = Math.max(offs, v.getEndOffset());
            }
            return offs;
        }

        /**
         * Perform layout for the minor axis of the box (i.e. the axis orthoginal to the axis that it represents). The
         * results of the layout should be placed in the given arrays which represent the allocations to the children
         * along the minor axis.
         * <p>
         * This is implemented to do a baseline layout of the children by calling BoxView.baselineLayout.
         * 
         * @param targetSpan the total span given to the view, which whould be used to layout the children.
         * @param axis the axis being layed out.
         * @param offsets the offsets from the origin of the view for each of the child views. This is a return value
         *        and is filled in by the implementation of this method.
         * @param spans the span of each child view. This is a return value and is filled in by the implementation of
         *        this method.
         * @return the offset and span for each child view in the offsets and spans parameters
         */
        protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
            baselineLayout(targetSpan, axis, offsets, spans);
        }

        protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
            return baselineRequirements(axis, r);
        }

        private boolean isLastRow() {
            View parent;
            return ((parent = getParent()) == null || this == parent.getView(parent.getViewCount() - 1));
        }

        private boolean isBrokenRow() {
            boolean rv = false;
            int viewsCount = getViewCount();
            if (viewsCount > 0) {
                View lastView = getView(viewsCount - 1);
                if (lastView.getBreakWeight(X_AXIS, 0, 0) >= ForcedBreakWeight) {
                    rv = true;
                }
            }
            return rv;
        }

        private boolean isJustifiableDocument() {
            return (!Boolean.TRUE.equals(getDocument().getProperty(AlterAbstractDocument.I18NProperty)));
        }

        /**
         * Whether we need to justify this {@code Row}. At this time (jdk1.6) we support justification on for non 18n
         * text.
         * 
         * @return {@code true} if this {@code Row} should be justified.
         */
        private boolean isJustifyEnabled() {
            boolean ret = (justification == StyleConstants.ALIGN_JUSTIFIED);

            // no justification for i18n documents
            ret = ret && isJustifiableDocument();

            // no justification for the last row
            ret = ret && !isLastRow();

            // no justification for the broken rows
            ret = ret && !isBrokenRow();

            return ret;
        }

        // Calls super method after setting spaceAddon to 0.
        // Justification should not affect MajorAxisRequirements
        @Override
        protected SizeRequirements calculateMajorAxisRequirements(int axis, SizeRequirements r) {
            int oldJustficationData[] = justificationData;
            justificationData = null;
            SizeRequirements ret = super.calculateMajorAxisRequirements(axis, r);
            if (isJustifyEnabled()) {
                justificationData = oldJustficationData;
            }
            return ret;
        }

        @Override
        protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
            int oldJustficationData[] = justificationData;
            justificationData = null;
            super.layoutMajorAxis(targetSpan, axis, offsets, spans);
            if (!isJustifyEnabled()) {
                return;
            }

            int currentSpan = 0;
            for (int span : spans) {
                currentSpan += span;
            }
            if (currentSpan == targetSpan) {
                // no need to justify
                return;
            }

            // we justify text by enlarging spaces by the {@code spaceAddon}.
            // justification is started to the right of the rightmost TAB.
            // leading and trailing spaces are not extendable.
            //
            // GlyphPainter1 uses
            // justificationData
            // for all painting and measurement.

            int extendableSpaces = 0;
            int startJustifiableContent = -1;
            int endJustifiableContent = -1;
            int lastLeadingSpaces = 0;

            int rowStartOffset = getStartOffset();
            int rowEndOffset = getEndOffset();
            int spaceMap[] = new int[rowEndOffset - rowStartOffset];
            Arrays.fill(spaceMap, 0);
            for (int i = getViewCount() - 1; i >= 0; i--) {
                View view = getView(i);
                if (view instanceof AlterGlyphView) {
                    AlterGlyphView.JustificationInfo justificationInfo = ((AlterGlyphView) view)
                            .getJustificationInfo(rowStartOffset);
                    final int viewStartOffset = view.getStartOffset();
                    final int offset = viewStartOffset - rowStartOffset;
                    for (int j = 0; j < justificationInfo.spaceMap.length(); j++) {
                        if (justificationInfo.spaceMap.get(j)) {
                            spaceMap[j + offset] = 1;
                        }
                    }
                    if (startJustifiableContent > 0) {
                        if (justificationInfo.end >= 0) {
                            extendableSpaces += justificationInfo.trailingSpaces;
                        } else {
                            lastLeadingSpaces += justificationInfo.trailingSpaces;
                        }
                    }
                    if (justificationInfo.start >= 0) {
                        startJustifiableContent = justificationInfo.start + viewStartOffset;
                        extendableSpaces += lastLeadingSpaces;
                    }
                    if (justificationInfo.end >= 0 && endJustifiableContent < 0) {
                        endJustifiableContent = justificationInfo.end + viewStartOffset;
                    }
                    extendableSpaces += justificationInfo.contentSpaces;
                    lastLeadingSpaces = justificationInfo.leadingSpaces;
                    if (justificationInfo.hasTab) {
                        break;
                    }
                }
            }
            if (extendableSpaces <= 0) {
                // there is nothing we can do to justify
                return;
            }
            int adjustment = (targetSpan - currentSpan);
            int spaceAddon = (extendableSpaces > 0) ? adjustment / extendableSpaces : 0;
            int spaceAddonLeftoverEnd = -1;
            for (int i = startJustifiableContent - rowStartOffset, leftover = adjustment - spaceAddon
                    * extendableSpaces; leftover > 0; leftover -= spaceMap[i], i++) {
                spaceAddonLeftoverEnd = i;
            }
            if (spaceAddon > 0 || spaceAddonLeftoverEnd >= 0) {
                justificationData = (oldJustficationData != null) ? oldJustficationData : new int[END_JUSTIFIABLE + 1];
                justificationData[SPACE_ADDON] = spaceAddon;
                justificationData[SPACE_ADDON_LEFTOVER_END] = spaceAddonLeftoverEnd;
                justificationData[START_JUSTIFIABLE] = startJustifiableContent - rowStartOffset;
                justificationData[END_JUSTIFIABLE] = endJustifiableContent - rowStartOffset;
                super.layoutMajorAxis(targetSpan, axis, offsets, spans);
            }
        }

        // for justified row we assume the maximum horizontal span
        // is MAX_VALUE.
        @Override
        public float getMaximumSpan(int axis) {
            float ret;
            if (View.X_AXIS == axis && isJustifyEnabled()) {
                ret = Float.MAX_VALUE;
            } else {
                ret = super.getMaximumSpan(axis);
            }
            return ret;
        }

        /**
         * Fetches the child view index representing the given position in the model.
         * 
         * @param pos the position >= 0
         * @return index of the view representing the given position, or -1 if no view represents that position
         */
        protected int getViewIndexAtPosition(int pos) {
            // This is expensive, but are views are not necessarily layed
            // out in model order.
            if (pos < getStartOffset() || pos >= getEndOffset())
                return -1;
            for (int counter = getViewCount() - 1; counter >= 0; counter--) {
                View v = getView(counter);
                if (pos >= v.getStartOffset() && pos < v.getEndOffset()) {
                    return counter;
                }
            }
            return -1;
        }

        /**
         * Gets the left inset.
         * 
         * @return the inset
         */
        protected short getLeftInset() {
            View parentView;
            int adjustment = 0;
            if ((parentView = getParent()) != null) { // use firstLineIdent for the first row
                if (this == parentView.getView(0)) {
                    adjustment = firstLineIndent;
                }
            }
            return (short) (super.getLeftInset() + adjustment);
        }

        protected short getBottomInset() {
            return (short) (super.getBottomInset() + ((minorRequest != null) ? minorRequest.preferred : 0)
                    * lineSpacing);
        }

        final static int SPACE_ADDON = 0;

        final static int SPACE_ADDON_LEFTOVER_END = 1;

        final static int START_JUSTIFIABLE = 2;

        // this should be the last index in justificationData
        final static int END_JUSTIFIABLE = 3;

        int justificationData[] = null;
    }

    /**
     * A collection of methods to deal with various text related activities.
     * 
     * @author Timothy Prinzing
     * @version 1.52 03/01/06
     */
    static class AlterUtilities extends Utilities {

        private static Method drawChars, drawString, getFontMetrics;

        private static Class clz = null;

        // --------- 1.5 x 1.6 incompatibility handling ....
        static {
            String j5 = "com.sun.java.swing.SwingUtilities2";
            String j6 = "sun.swing.SwingUtilities2";
            try {
                // assume 1.6
                clz = Class.forName(j6);
            } catch (ClassNotFoundException e) {
                // or maybe not ..
                try {
                    clz = Class.forName(j5);
                } catch (ClassNotFoundException e1) {
                    throw new RuntimeException("Failed to find SwingUtilities2. Check the classpath.");
                }
            }
            try {
                drawChars = clz.getMethod("drawChars", new Class[] { JComponent.class, Graphics.class, char[].class,
                        int.class, int.class, int.class, int.class });
                drawString = clz.getMethod("drawString", new Class[] { JComponent.class, Graphics.class,
                        AttributedCharacterIterator.class, int.class, int.class });
                getFontMetrics = clz.getMethod("getFontMetrics", new Class[] { JComponent.class, Graphics.class });
            } catch (Exception e) {
                throw new RuntimeException("Failed to use SwingUtilities2. Check the permissions and class version.");
            }
        }

        private static FontMetrics getFontMetrics(JComponent component, Graphics g) {
            try {
                return (FontMetrics) getFontMetrics.invoke(null, new Object[] { component, g });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private static int drawChars(JComponent component, Graphics g, char[] txt, int flushIndex, int flushLen, int x,
                int y) {
            try {
                return (Integer) drawChars.invoke(null, new Object[] { component, g, txt, flushIndex, flushLen, x, y });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private static float drawString(JComponent c, Graphics2D g2d, AttributedCharacterIterator aci, int x, int y) {
            try {
                return (Float) drawString.invoke(null, new Object[] { c, g2d, aci, x, y });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // --------- EO 1.5 x 1.6 incompatibility handling ....

        /**
         * If <code>view</code>'s container is a <code>JComponent</code> it is returned, after casting.
         */
        static JComponent getJComponent(View view) {
            if (view != null) {
                Component component = view.getContainer();
                if (component instanceof JComponent) {
                    return (JComponent) component;
                }
            }
            return null;
        }

        /**
         * Draws the given text, expanding any tabs that are contained using the given tab expansion technique. This
         * particular implementation renders in a 1.1 style coordinate system where ints are used and 72dpi is assumed.
         * 
         * @param view View requesting rendering, may be null.
         * @param s the source of the text
         * @param x the X origin >= 0
         * @param y the Y origin >= 0
         * @param g the graphics context
         * @param e how to expand the tabs. If this value is null, tabs will be expanded as a space character.
         * @param startOffset starting offset of the text in the document >= 0
         * @return the X location at the end of the rendered text
         */
        static final int drawTabbedText(View view, Segment s, int x, int y, Graphics g, TabExpander e, int startOffset) {
            return drawTabbedText(view, s, x, y, g, e, startOffset, null);
        }

        // In addition to the previous method it can extend spaces for
        // justification.
        //
        // all params are the same as in the preious method except the last
        // one:
        // @param justificationData justificationData for the row.
        // if null not justification is needed
        static final int drawTabbedText(View view, Segment s, int x, int y, Graphics g, TabExpander e, int startOffset,
                int[] justificationData) {
            JComponent component = getJComponent(view);
            FontMetrics metrics = getFontMetrics(component, g);
            int nextX = x;
            char[] txt = s.array;
            int txtOffset = s.offset;
            int flushLen = 0;
            int flushIndex = s.offset;
            int spaceAddon = 0;
            int spaceAddonLeftoverEnd = -1;
            int startJustifiableContent = 0;
            int endJustifiableContent = 0;
            if (justificationData != null) {
                int offset = -startOffset + txtOffset;
                View parent = null;
                if (view != null && (parent = view.getParent()) != null) {
                    offset += parent.getStartOffset();
                }
                spaceAddon = justificationData[Row.SPACE_ADDON];
                spaceAddonLeftoverEnd = justificationData[Row.SPACE_ADDON_LEFTOVER_END] + offset;
                startJustifiableContent = justificationData[Row.START_JUSTIFIABLE] + offset;
                endJustifiableContent = justificationData[Row.END_JUSTIFIABLE] + offset;
            }
            int n = s.offset + s.count;
            for (int i = txtOffset; i < n; i++) {
                if (txt[i] == '\t'
                        || ((spaceAddon != 0 || i <= spaceAddonLeftoverEnd) && (txt[i] == ' ')
                                && startJustifiableContent <= i && i <= endJustifiableContent)) {
                    if (flushLen > 0) {
                        nextX = drawChars(component, g, txt, flushIndex, flushLen, x, y);
                        flushLen = 0;
                    }
                    flushIndex = i + 1;
                    if (txt[i] == '\t') {
                        if (e != null) {
                            nextX = (int) e.nextTabStop((float) nextX, startOffset + i - txtOffset);
                        } else {
                            nextX += metrics.charWidth(' ');
                        }
                    } else if (txt[i] == ' ') {
                        nextX += metrics.charWidth(' ') + spaceAddon;
                        if (i <= spaceAddonLeftoverEnd) {
                            nextX++;
                        }
                    }
                    x = nextX;
                } else if ((txt[i] == '\n') || (txt[i] == '\r')) {
                    if (flushLen > 0) {
                        nextX = drawChars(component, g, txt, flushIndex, flushLen, x, y);
                        flushLen = 0;
                    }
                    flushIndex = i + 1;
                    x = nextX;
                } else {
                    flushLen += 1;
                }
            }
            if (flushLen > 0) {
                nextX = drawChars(component, g, txt, flushIndex, flushLen, x, y);
            }
            return nextX;
        }

        // private static FontMetrics getFontMetrics(JComponent c, Graphics g) {
        // if (c != null) {
        // return c.getFontMetrics(g.getFont());
        // }
        // return Toolkit.getDefaultToolkit().getFontMetrics(g.getFont()); }

        // In addition to the previous method it can extend spaces for
        // justification.
        //
        // all params are the same as in the preious method except the last
        // one:
        // @param justificationData justificationData for the row.
        // if null not justification is needed
        static final int getTabbedTextWidth(View view, Segment s, FontMetrics metrics, int x, TabExpander e,
                int startOffset, int[] justificationData) {
            int nextX = x;
            char[] txt = s.array;
            int txtOffset = s.offset;
            int n = s.offset + s.count;
            int charCount = 0;
            int spaceAddon = 0;
            int spaceAddonLeftoverEnd = -1;
            int startJustifiableContent = 0;
            int endJustifiableContent = 0;
            if (justificationData != null) {
                int offset = -startOffset + txtOffset;
                View parent = null;
                if (view != null && (parent = view.getParent()) != null) {
                    offset += parent.getStartOffset();
                }
                spaceAddon = justificationData[Row.SPACE_ADDON];
                spaceAddonLeftoverEnd = justificationData[Row.SPACE_ADDON_LEFTOVER_END] + offset;
                startJustifiableContent = justificationData[Row.START_JUSTIFIABLE] + offset;
                endJustifiableContent = justificationData[Row.END_JUSTIFIABLE] + offset;
            }

            for (int i = txtOffset; i < n; i++) {
                if (txt[i] == '\t'
                        || ((spaceAddon != 0 || i <= spaceAddonLeftoverEnd) && (txt[i] == ' ')
                                && startJustifiableContent <= i && i <= endJustifiableContent)) {
                    nextX += metrics.charsWidth(txt, i - charCount, charCount);
                    charCount = 0;
                    if (txt[i] == '\t') {
                        if (e != null) {
                            nextX = (int) e.nextTabStop((float) nextX, startOffset + i - txtOffset);
                        } else {
                            nextX += metrics.charWidth(' ');
                        }
                    } else if (txt[i] == ' ') {
                        nextX += metrics.charWidth(' ') + spaceAddon;
                        if (i <= spaceAddonLeftoverEnd) {
                            nextX++;
                        }
                    }
                } else if (txt[i] == '\n') {
                    // Ignore newlines, they take up space and we shouldn't be
                    // counting them.
                    nextX += metrics.charsWidth(txt, i - charCount, charCount);
                    charCount = 0;
                } else {
                    charCount++;
                }
            }
            nextX += metrics.charsWidth(txt, n - charCount, charCount);
            return nextX - x;
        }

        static final int getTabbedTextOffset(View view, Segment s, FontMetrics metrics, int x0, int x, TabExpander e,
                int startOffset, int[] justificationData) {
            return getTabbedTextOffset(view, s, metrics, x0, x, e, startOffset, true, justificationData);
        }

        // In addition to the previous method it can extend spaces for
        // justification.
        //
        // all params are the same as in the preious method except the last
        // one:
        // @param justificationData justificationData for the row.
        // if null not justification is needed
        static final int getTabbedTextOffset(View view, Segment s, FontMetrics metrics, int x0, int x, TabExpander e,
                int startOffset, boolean round, int[] justificationData) {
            if (x0 >= x) {
                // x before x0, return.
                return 0;
            }
            int currX = x0;
            int nextX = currX;
            // s may be a shared segment, so it is copied prior to calling
            // the tab expander
            char[] txt = s.array;
            int txtOffset = s.offset;
            int txtCount = s.count;
            int spaceAddon = 0;
            int spaceAddonLeftoverEnd = -1;
            int startJustifiableContent = 0;
            int endJustifiableContent = 0;
            if (justificationData != null) {
                int offset = -startOffset + txtOffset;
                View parent = null;
                if (view != null && (parent = view.getParent()) != null) {
                    offset += parent.getStartOffset();
                }
                spaceAddon = justificationData[Row.SPACE_ADDON];
                spaceAddonLeftoverEnd = justificationData[Row.SPACE_ADDON_LEFTOVER_END] + offset;
                startJustifiableContent = justificationData[Row.START_JUSTIFIABLE] + offset;
                endJustifiableContent = justificationData[Row.END_JUSTIFIABLE] + offset;
            }
            int n = s.offset + s.count;
            for (int i = s.offset; i < n; i++) {
                if (txt[i] == '\t'
                        || ((spaceAddon != 0 || i <= spaceAddonLeftoverEnd) && (txt[i] == ' ')
                                && startJustifiableContent <= i && i <= endJustifiableContent)) {
                    if (txt[i] == '\t') {
                        if (e != null) {
                            nextX = (int) e.nextTabStop((float) nextX, startOffset + i - txtOffset);
                        } else {
                            nextX += metrics.charWidth(' ');
                        }
                    } else if (txt[i] == ' ') {
                        nextX += metrics.charWidth(' ') + spaceAddon;
                        if (i <= spaceAddonLeftoverEnd) {
                            nextX++;
                        }
                    }
                } else {
                    nextX += metrics.charWidth(txt[i]);
                }
                if ((x >= currX) && (x < nextX)) {
                    // found the hit position... return the appropriate side
                    if ((round == false) || ((x - currX) < (nextX - x))) {
                        return i - txtOffset;
                    } else {
                        return i + 1 - txtOffset;
                    }
                }
                currX = nextX;
            }

            // didn't find, return end offset
            return txtCount;
        }

        /**
         * Finds the next word in the given elements text. The first parameter allows searching multiple paragraphs
         * where even the first offset is desired. Returns the offset of the next word, or BreakIterator.DONE if there
         * are no more words in the element.
         */
        static int getNextWordInParagraph(JTextComponent c, Element line, int offs, boolean first)
                throws BadLocationException {
            if (line == null) {
                throw new BadLocationException("No more words", offs);
            }
            Document doc = line.getDocument();
            int lineStart = line.getStartOffset();
            int lineEnd = Math.min(line.getEndOffset(), doc.getLength());
            if ((offs >= lineEnd) || (offs < lineStart)) {
                throw new BadLocationException("No more words", offs);
            }
            Segment seg = SegmentCache.getSharedSegment();
            doc.getText(lineStart, lineEnd - lineStart, seg);
            BreakIterator words = BreakIterator.getWordInstance(c.getLocale());
            words.setText(seg);
            if ((first && (words.first() == (seg.offset + offs - lineStart)))
                    && (!Character.isWhitespace(seg.array[words.first()]))) {

                return offs;
            }
            int wordPosition = words.following(seg.offset + offs - lineStart);
            if ((wordPosition == BreakIterator.DONE) || (wordPosition >= seg.offset + seg.count)) {
                // there are no more words on this line.
                return BreakIterator.DONE;
            }
            // if we haven't shot past the end... check to
            // see if the current boundary represents whitespace.
            // if so, we need to try again
            char ch = seg.array[wordPosition];
            if (!Character.isWhitespace(ch)) {
                return lineStart + wordPosition - seg.offset;
            }

            // it was whitespace, try again. The assumption
            // is that it must be a word start if the last
            // one had whitespace following it.
            wordPosition = words.next();
            if (wordPosition != BreakIterator.DONE) {
                offs = lineStart + wordPosition - seg.offset;
                if (offs != lineEnd) {
                    return offs;
                }
            }
            SegmentCache.releaseSharedSegment(seg);
            return BreakIterator.DONE;
        }

        /**
         * Finds the previous word in the given elements text. The first parameter allows searching multiple paragraphs
         * where even the first offset is desired. Returns the offset of the next word, or BreakIterator.DONE if there
         * are no more words in the element.
         */
        static int getPrevWordInParagraph(JTextComponent c, Element line, int offs) throws BadLocationException {
            if (line == null) {
                throw new BadLocationException("No more words", offs);
            }
            Document doc = line.getDocument();
            int lineStart = line.getStartOffset();
            int lineEnd = line.getEndOffset();
            if ((offs > lineEnd) || (offs < lineStart)) {
                throw new BadLocationException("No more words", offs);
            }
            Segment seg = SegmentCache.getSharedSegment();
            doc.getText(lineStart, lineEnd - lineStart, seg);
            BreakIterator words = BreakIterator.getWordInstance(c.getLocale());
            words.setText(seg);
            if (words.following(seg.offset + offs - lineStart) == BreakIterator.DONE) {
                words.last();
            }
            int wordPosition = words.previous();
            if (wordPosition == (seg.offset + offs - lineStart)) {
                wordPosition = words.previous();
            }

            if (wordPosition == BreakIterator.DONE) {
                // there are no more words on this line.
                return BreakIterator.DONE;
            }
            // if we haven't shot past the end... check to
            // see if the current boundary represents whitespace.
            // if so, we need to try again
            char ch = seg.array[wordPosition];
            if (!Character.isWhitespace(ch)) {
                return lineStart + wordPosition - seg.offset;
            }

            // it was whitespace, try again. The assumption
            // is that it must be a word start if the last
            // one had whitespace following it.
            wordPosition = words.previous();
            if (wordPosition != BreakIterator.DONE) {
                return lineStart + wordPosition - seg.offset;
            }
            SegmentCache.releaseSharedSegment(seg);
            return BreakIterator.DONE;
        }

        static boolean isComposedTextElement(Document doc, int offset) {
            Element elem = doc.getDefaultRootElement();
            while (!elem.isLeaf()) {
                elem = elem.getElement(elem.getElementIndex(offset));
            }
            return isComposedTextElement(elem);
        }

        static boolean isComposedTextElement(Element elem) {
            AttributeSet as = elem.getAttributes();
            return isComposedTextAttributeDefined(as);
        }

        static boolean isComposedTextAttributeDefined(AttributeSet as) {
            return ((as != null) && (as.isDefined(StyleConstants.ComposedTextAttribute)));
        }

        /**
         * Draws the given composed text passed from an input method.
         * 
         * @param view View hosting text
         * @param attr the attributes containing the composed text
         * @param g the graphics context
         * @param x the X origin
         * @param y the Y origin
         * @param p0 starting offset in the composed text to be rendered
         * @param p1 ending offset in the composed text to be rendered
         * @return the new insertion position
         */
        static int drawComposedText(View view, AttributeSet attr, Graphics g, int x, int y, int p0, int p1)
                throws BadLocationException {
            Graphics2D g2d = (Graphics2D) g;
            AttributedString as = (AttributedString) attr.getAttribute(StyleConstants.ComposedTextAttribute);
            as.addAttribute(TextAttribute.FONT, g.getFont());

            if (p0 >= p1)
                return x;

            AttributedCharacterIterator aci = as.getIterator(null, p0, p1);
            return x + (int) drawString(getJComponent(view), g2d, aci, x, y);
        }

        /**
         * Paints the composed text in a GlyphView
         */
        static void paintComposedText(Graphics g, Rectangle alloc, AlterGlyphView v) {
            if (g instanceof Graphics2D) {
                Graphics2D g2d = (Graphics2D) g;
                int p0 = v.getStartOffset();
                int p1 = v.getEndOffset();
                AttributeSet attrSet = v.getElement().getAttributes();
                AttributedString as = (AttributedString) attrSet.getAttribute(StyleConstants.ComposedTextAttribute);
                int start = v.getElement().getStartOffset();
                int y = alloc.y + alloc.height - (int) v.getGlyphPainter().getDescent(v);
                int x = alloc.x;

                // Add text attributes
                as.addAttribute(TextAttribute.FONT, v.getFont());
                as.addAttribute(TextAttribute.FOREGROUND, v.getForeground());
                if (StyleConstants.isBold(v.getAttributes())) {
                    as.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                }
                if (StyleConstants.isItalic(v.getAttributes())) {
                    as.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
                }
                if (v.isUnderline()) {
                    as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                }
                if (v.isStrikeThrough()) {
                    as.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                }
                if (v.isSuperscript()) {
                    as.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
                }
                if (v.isSubscript()) {
                    as.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
                }

                // draw
                AttributedCharacterIterator aci = as.getIterator(null, p0 - start, p1 - start);
                drawString(getJComponent(v), g2d, aci, x, y);
            }
        }

        /*
         * Convenience function for determining ComponentOrientation. Helps us avoid having Munge directives throughout
         * the code.
         */
        static boolean isLeftToRight(java.awt.Component c) {
            return c.getComponentOrientation().isLeftToRight();
        }

        /**
         * Provides a way to determine the next visually represented model location that one might place a caret. Some
         * views may not be visible, they might not be in the same order found in the model, or they just might not
         * allow access to some of the locations in the model.
         * <p>
         * This implementation assumes the views are layed out in a logical manner. That is, that the view at index x +
         * 1 is visually after the View at index x, and that the View at index x - 1 is visually before the View at x.
         * There is support for reversing this behavior only if the passed in <code>View</code> is an instance of
         * <code>CompositeView</code>. The <code>CompositeView</code> must then override the
         * <code>flipEastAndWestAtEnds</code> method.
         * 
         * @param v View to query
         * @param pos the position to convert >= 0
         * @param a the allocated region to render into
         * @param direction the direction from the current position that can be thought of as the arrow keys typically
         *        found on a keyboard; this may be one of the following:
         *        <ul>
         *        <li><code>SwingConstants.WEST</code>
         *        <li><code>SwingConstants.EAST</code>
         *        <li><code>SwingConstants.NORTH</code>
         *        <li><code>SwingConstants.SOUTH</code>
         *        </ul>
         * @param biasRet an array contain the bias that was checked
         * @return the location within the model that best represents the next location visual position
         * @exception BadLocationException
         * @exception IllegalArgumentException if <code>direction</code> is invalid
         */
        static int getNextVisualPositionFrom(View v, int pos, Position.Bias b, Shape alloc, int direction,
                Position.Bias[] biasRet) throws BadLocationException {
            if (v.getViewCount() == 0) {
                // Nothing to do.
                return pos;
            }
            boolean top = (direction == SwingConstants.NORTH || direction == SwingConstants.WEST);
            int retValue;
            if (pos == -1) {
                // Start from the first View.
                int childIndex = (top) ? v.getViewCount() - 1 : 0;
                View child = v.getView(childIndex);
                Shape childBounds = v.getChildAllocation(childIndex, alloc);
                retValue = child.getNextVisualPositionFrom(pos, b, childBounds, direction, biasRet);
                if (retValue == -1 && !top && v.getViewCount() > 1) {
                    // Special case that should ONLY happen if first view
                    // isn't valid (can happen when end position is put at
                    // beginning of line.
                    child = v.getView(1);
                    childBounds = v.getChildAllocation(1, alloc);
                    retValue = child.getNextVisualPositionFrom(-1, biasRet[0], childBounds, direction, biasRet);
                }
            } else {
                int increment = (top) ? -1 : 1;
                int childIndex;
                if (b == Position.Bias.Backward && pos > 0) {
                    childIndex = v.getViewIndex(pos - 1, Position.Bias.Forward);
                } else {
                    childIndex = v.getViewIndex(pos, Position.Bias.Forward);
                }
                View child = v.getView(childIndex);
                Shape childBounds = v.getChildAllocation(childIndex, alloc);
                retValue = child.getNextVisualPositionFrom(pos, b, childBounds, direction, biasRet);
                if ((direction == SwingConstants.EAST || direction == SwingConstants.WEST)
                        && (v instanceof AlterCompositeView) && ((AlterCompositeView) v).flipEastAndWestAtEnds(pos, b)) {
                    increment *= -1;
                }
                childIndex += increment;
                if (retValue == -1 && childIndex >= 0 && childIndex < v.getViewCount()) {
                    child = v.getView(childIndex);
                    childBounds = v.getChildAllocation(childIndex, alloc);
                    retValue = child.getNextVisualPositionFrom(-1, b, childBounds, direction, biasRet);
                    // If there is a bias change, it is a fake position
                    // and we should skip it. This is usually the result
                    // of two elements side be side flowing the same way.
                    if (retValue == pos && biasRet[0] != b) {
                        return getNextVisualPositionFrom(v, pos, biasRet[0], alloc, direction, biasRet);
                    }
                } else if (retValue != -1
                        && biasRet[0] != b
                        && ((increment == 1 && child.getEndOffset() == retValue) || (increment == -1 && child
                                .getStartOffset() == retValue)) && childIndex >= 0 && childIndex < v.getViewCount()) {
                    // Reached the end of a view, make sure the next view
                    // is a different direction.
                    child = v.getView(childIndex);
                    childBounds = v.getChildAllocation(childIndex, alloc);
                    Position.Bias originalBias = biasRet[0];
                    int nextPos = child.getNextVisualPositionFrom(-1, b, childBounds, direction, biasRet);
                    if (biasRet[0] == b) {
                        retValue = nextPos;
                    } else {
                        biasRet[0] = originalBias;
                    }
                }
            }
            return retValue;
        }
    }

    /**
     * <code>AnotherGlyphView</code>
     * 
     * @inheritDoc
     */
    static class AlterGlyphView extends GlyphView implements TabableView, Cloneable {

        /**
         * Constructs a new view wrapped on an element.
         * 
         * @param elem the element
         */
        public AlterGlyphView(Element elem) {
            super(elem);
        }

        /**
         * Class to hold data needed to justify this GlyphView in a PargraphView.Row
         */
        static class JustificationInfo {
            // justifiable content start
            final int start;

            // justifiable content end
            final int end;

            final int leadingSpaces;

            final int contentSpaces;

            final int trailingSpaces;

            final boolean hasTab;

            final BitSet spaceMap;

            JustificationInfo(int start, int end, int leadingSpaces, int contentSpaces, int trailingSpaces,
                    boolean hasTab, BitSet spaceMap) {
                this.start = start;
                this.end = end;
                this.leadingSpaces = leadingSpaces;
                this.contentSpaces = contentSpaces;
                this.trailingSpaces = trailingSpaces;
                this.hasTab = hasTab;
                this.spaceMap = spaceMap;
            }
        }

        JustificationInfo getJustificationInfo(int rowStartOffset) {
            if (justificationInfo != null) {
                return justificationInfo;
            }
            // states for the parsing
            final int TRAILING = 0;
            final int CONTENT = 1;
            final int SPACES = 2;
            int startOffset = getStartOffset();
            int endOffset = getEndOffset();
            Segment segment = getText(startOffset, endOffset);
            int txtOffset = segment.offset;
            int txtEnd = segment.offset + segment.count - 1;
            int startContentPosition = txtEnd + 1;
            int endContentPosition = txtOffset - 1;
            int trailingSpaces = 0;
            int contentSpaces = 0;
            int leadingSpaces = 0;
            boolean hasTab = false;
            BitSet spaceMap = new BitSet(endOffset - startOffset + 1);

            // we parse conent to the right of the rightmost TAB only.
            // we are looking for the trailing and leading spaces.
            // position after the leading spaces (startContentPosition)
            // position before the trailing spaces (endContentPosition)
            for (int i = txtEnd, state = TRAILING; i >= txtOffset; i--) {
                if (' ' == segment.array[i]) {
                    spaceMap.set(i - txtOffset);
                    if (state == TRAILING) {
                        trailingSpaces++;
                    } else if (state == CONTENT) {
                        state = SPACES;
                        leadingSpaces = 1;
                    } else if (state == SPACES) {
                        leadingSpaces++;
                    }
                } else if ('\t' == segment.array[i]) {
                    hasTab = true;
                    break;
                } else {
                    if (state == TRAILING) {
                        if ('\n' != segment.array[i] && '\r' != segment.array[i]) {
                            state = CONTENT;
                            endContentPosition = i;
                        }
                    } else if (state == CONTENT) {
                        // do nothing
                    } else if (state == SPACES) {
                        contentSpaces += leadingSpaces;
                        leadingSpaces = 0;
                    }
                    startContentPosition = i;
                }
            }

            SegmentCache.releaseSharedSegment(segment);

            int startJustifiableContent = -1;
            if (startContentPosition < txtEnd) {
                startJustifiableContent = startContentPosition - txtOffset;
            }
            int endJustifiableContent = -1;
            if (endContentPosition > txtOffset) {
                endJustifiableContent = endContentPosition - txtOffset;
            }
            justificationInfo = new JustificationInfo(startJustifiableContent, endJustifiableContent, leadingSpaces,
                    contentSpaces, trailingSpaces, hasTab, spaceMap);
            return justificationInfo;
        }

        private JustificationInfo justificationInfo = null;

    }

    /**
     * View of plain text (text with only one font and color) that does line-wrapping. This view expects that its
     * associated element has child elements that represent the lines it should be wrapping. It is implemented as a
     * vertical box that contains logical line views. The logical line views are nested classes that render the logical
     * line as multiple physical line if the logical line is too wide to fit within the allocation. The line views draw
     * upon the outer class for its state to reduce their memory requirements.
     * <p>
     * The line views do all of their rendering through the <code>drawLine</code> method which in turn does all of its
     * rendering through the <code>drawSelectedText</code> and <code>drawUnselectedText</code> methods. This enables
     * subclasses to easily specialize the rendering without concern for the layout aspects.
     * 
     * @author Timothy Prinzing
     * @version 1.41 05/05/06
     * @see View
     */
    static class AlterWrappedPlainView extends AlterBoxView implements TabExpander {

        /**
         * Creates a new WrappedPlainView. Lines will be wrapped on character boundaries.
         * 
         * @param elem the element underlying the view
         */
        public AlterWrappedPlainView(Element elem) {
            this(elem, false);
        }

        /**
         * Creates a new WrappedPlainView. Lines can be wrapped on either character or word boundaries depending upon
         * the setting of the wordWrap parameter.
         * 
         * @param elem the element underlying the view
         * @param wordWrap should lines be wrapped on word boundaries?
         */
        public AlterWrappedPlainView(Element elem, boolean wordWrap) {
            super(elem, Y_AXIS);
            this.wordWrap = wordWrap;
        }

        /**
         * Returns the tab size set for the document, defaulting to 8.
         * 
         * @return the tab size
         */
        protected int getTabSize() {
            Integer i = (Integer) getDocument().getProperty(PlainDocument.tabSizeAttribute);
            int size = (i != null) ? i.intValue() : 8;
            return size;
        }

        /**
         * Renders a line of text, suppressing whitespace at the end and expanding any tabs. This is implemented to make
         * calls to the methods <code>drawUnselectedText</code> and <code>drawSelectedText</code> so that the way
         * selected and unselected text are rendered can be customized.
         * 
         * @param p0 the starting document location to use >= 0
         * @param p1 the ending document location to use >= p1
         * @param g the graphics context
         * @param x the starting X position >= 0
         * @param y the starting Y position >= 0
         * @see #drawUnselectedText
         * @see #drawSelectedText
         */
        protected void drawLine(int p0, int p1, Graphics g, int x, int y) {
            Element lineMap = getElement();
            Element line = lineMap.getElement(lineMap.getElementIndex(p0));
            Element elem;

            try {
                if (line.isLeaf()) {
                    drawText(line, p0, p1, g, x, y);
                } else {
                    // this line contains the composed text.
                    int idx = line.getElementIndex(p0);
                    int lastIdx = line.getElementIndex(p1);
                    for (; idx <= lastIdx; idx++) {
                        elem = line.getElement(idx);
                        int start = Math.max(elem.getStartOffset(), p0);
                        int end = Math.min(elem.getEndOffset(), p1);
                        x = drawText(elem, start, end, g, x, y);
                    }
                }
            } catch (BadLocationException e) {
                throw new Error("Can't render: " + p0 + "," + p1);
            }
        }

        private int drawText(Element elem, int p0, int p1, Graphics g, int x, int y) throws BadLocationException {
            p1 = Math.min(getDocument().getLength(), p1);
            AttributeSet attr = elem.getAttributes();

            if (AlterUtilities.isComposedTextAttributeDefined(attr)) {
                g.setColor(unselected);
                x = AlterUtilities.drawComposedText(this, attr, g, x, y, p0 - elem.getStartOffset(), p1
                        - elem.getStartOffset());
            } else {
                if (sel0 == sel1 || selected == unselected) {
                    // no selection, or it is invisible
                    x = drawUnselectedText(g, x, y, p0, p1);
                } else if ((p0 >= sel0 && p0 <= sel1) && (p1 >= sel0 && p1 <= sel1)) {
                    x = drawSelectedText(g, x, y, p0, p1);
                } else if (sel0 >= p0 && sel0 <= p1) {
                    if (sel1 >= p0 && sel1 <= p1) {
                        x = drawUnselectedText(g, x, y, p0, sel0);
                        x = drawSelectedText(g, x, y, sel0, sel1);
                        x = drawUnselectedText(g, x, y, sel1, p1);
                    } else {
                        x = drawUnselectedText(g, x, y, p0, sel0);
                        x = drawSelectedText(g, x, y, sel0, p1);
                    }
                } else if (sel1 >= p0 && sel1 <= p1) {
                    x = drawSelectedText(g, x, y, p0, sel1);
                    x = drawUnselectedText(g, x, y, sel1, p1);
                } else {
                    x = drawUnselectedText(g, x, y, p0, p1);
                }
            }

            return x;
        }

        /**
         * Renders the given range in the model as normal unselected text.
         * 
         * @param g the graphics context
         * @param x the starting X coordinate >= 0
         * @param y the starting Y coordinate >= 0
         * @param p0 the beginning position in the model >= 0
         * @param p1 the ending position in the model >= p0
         * @return the X location of the end of the range >= 0
         * @exception BadLocationException if the range is invalid
         */
        protected int drawUnselectedText(Graphics g, int x, int y, int p0, int p1) throws BadLocationException {
            g.setColor(unselected);
            Document doc = getDocument();
            Segment segment = SegmentCache.getSharedSegment();
            doc.getText(p0, p1 - p0, segment);
            int ret = AlterUtilities.drawTabbedText(this, segment, x, y, g, this, p0);
            SegmentCache.releaseSharedSegment(segment);
            return ret;
        }

        /**
         * Renders the given range in the model as selected text. This is implemented to render the text in the color
         * specified in the hosting component. It assumes the highlighter will render the selected background.
         * 
         * @param g the graphics context
         * @param x the starting X coordinate >= 0
         * @param y the starting Y coordinate >= 0
         * @param p0 the beginning position in the model >= 0
         * @param p1 the ending position in the model >= p0
         * @return the location of the end of the range.
         * @exception BadLocationException if the range is invalid
         */
        protected int drawSelectedText(Graphics g, int x, int y, int p0, int p1) throws BadLocationException {
            g.setColor(selected);
            Document doc = getDocument();
            Segment segment = SegmentCache.getSharedSegment();
            doc.getText(p0, p1 - p0, segment);
            int ret = AlterUtilities.drawTabbedText(this, segment, x, y, g, this, p0);
            SegmentCache.releaseSharedSegment(segment);
            return ret;
        }

        /**
         * Gives access to a buffer that can be used to fetch text from the associated document.
         * 
         * @return the buffer
         */
        protected final Segment getLineBuffer() {
            if (lineBuffer == null) {
                lineBuffer = new Segment();
            }
            return lineBuffer;
        }

        /**
         * This is called by the nested wrapped line views to determine the break location. This can be reimplemented to
         * alter the breaking behavior. It will either break at word or character boundaries depending upon the break
         * argument given at construction.
         */
        protected int calculateBreakPosition(int p0, int p1) {
            int p;
            Segment segment = SegmentCache.getSharedSegment();
            loadText(segment, p0, p1);
            int currentWidth = getWidth();
            if (wordWrap) {
                p = p0 + AlterUtilities.getBreakLocation(segment, metrics, tabBase, tabBase + currentWidth, this, p0);
            } else {
                p = p0
                        + AlterUtilities.getTabbedTextOffset(segment, metrics, tabBase, tabBase + currentWidth, this,
                                p0, false);
            }
            SegmentCache.releaseSharedSegment(segment);
            return p;
        }

        /**
         * Loads all of the children to initialize the view. This is called by the <code>setParent</code> method.
         * Subclasses can reimplement this to initialize their child views in a different manner. The default
         * implementation creates a child view for each child element.
         * 
         * @param f the view factory
         */
        protected void loadChildren(ViewFactory f) {
            Element e = getElement();
            int n = e.getElementCount();
            if (n > 0) {
                View[] added = new View[n];
                for (int i = 0; i < n; i++) {
                    added[i] = new WrappedLine(e.getElement(i));
                }
                replace(0, 0, added);
            }
        }

        /**
         * Update the child views in response to a document event.
         */
        void updateChildren(DocumentEvent e, Shape a) {
            Element elem = getElement();
            DocumentEvent.ElementChange ec = e.getChange(elem);
            if (ec != null) {
                // the structure of this element changed.
                Element[] removedElems = ec.getChildrenRemoved();
                Element[] addedElems = ec.getChildrenAdded();
                View[] added = new View[addedElems.length];
                for (int i = 0; i < addedElems.length; i++) {
                    added[i] = new WrappedLine(addedElems[i]);
                }
                replace(ec.getIndex(), removedElems.length, added);

                // should damge a little more intelligently.
                if (a != null) {
                    preferenceChanged(null, true, true);
                    getContainer().repaint();
                }
            }

            // update font metrics which may be used by the child views
            updateMetrics();
        }

        /**
         * Load the text buffer with the given range of text. This is used by the fragments broken off of this view as
         * well as this view itself.
         */
        final void loadText(Segment segment, int p0, int p1) {
            try {
                Document doc = getDocument();
                doc.getText(p0, p1 - p0, segment);
            } catch (BadLocationException bl) {
                throw new StateInvariantError("Can't get line text");
            }
        }

        final void updateMetrics() {
            Component host = getContainer();
            Font f = host.getFont();
            metrics = host.getFontMetrics(f);
            tabSize = getTabSize() * metrics.charWidth('m');
        }

        // --- TabExpander methods ------------------------------------------

        /**
         * Returns the next tab stop position after a given reference position. This implementation does not support
         * things like centering so it ignores the tabOffset argument.
         * 
         * @param x the current position >= 0
         * @param tabOffset the position within the text stream that the tab occurred at >= 0.
         * @return the tab stop, measured in points >= 0
         */
        public float nextTabStop(float x, int tabOffset) {
            if (tabSize == 0)
                return x;
            int ntabs = ((int) x - tabBase) / tabSize;
            return tabBase + ((ntabs + 1) * tabSize);
        }

        // --- View methods -------------------------------------

        /**
         * Renders using the given rendering surface and area on that surface. This is implemented to stash the
         * selection positions, selection colors, and font metrics for the nested lines to use.
         * 
         * @param g the rendering surface to use
         * @param a the allocated region to render into
         * 
         * @see View#paint
         */
        public void paint(Graphics g, Shape a) {
            Rectangle alloc = (Rectangle) a;
            tabBase = alloc.x;
            JComponent host = (JComponent) getContainer();
            unselected = host.getForeground();
            selected = unselected;
            g.setFont(host.getFont());

            // superclass paints the children
            super.paint(g, a);
        }

        /**
         * Sets the size of the view. This should cause layout of the view along the given axis, if it has any layout
         * duties.
         * 
         * @param width the width >= 0
         * @param height the height >= 0
         */
        public void setSize(float width, float height) {
            updateMetrics();
            if ((int) width != getWidth()) {
                // invalidate the view itself since the childrens
                // desired widths will be based upon this views width.
                preferenceChanged(null, true, true);
                widthChanging = true;
            }
            super.setSize(width, height);
            widthChanging = false;
        }

        /**
         * Determines the preferred span for this view along an axis. This is implemented to provide the superclass
         * behavior after first making sure that the current font metrics are cached (for the nested lines which use the
         * metrics to determine the height of the potentially wrapped lines).
         * 
         * @param axis may be either View.X_AXIS or View.Y_AXIS
         * @return the span the view would like to be rendered into. Typically the view is told to render into the span
         *         that is returned, although there is no guarantee. The parent may choose to resize or break the view.
         * @see View#getPreferredSpan
         */
        public float getPreferredSpan(int axis) {
            updateMetrics();
            return super.getPreferredSpan(axis);
        }

        /**
         * Determines the minimum span for this view along an axis. This is implemented to provide the superclass
         * behavior after first making sure that the current font metrics are cached (for the nested lines which use the
         * metrics to determine the height of the potentially wrapped lines).
         * 
         * @param axis may be either View.X_AXIS or View.Y_AXIS
         * @return the span the view would like to be rendered into. Typically the view is told to render into the span
         *         that is returned, although there is no guarantee. The parent may choose to resize or break the view.
         * @see View#getMinimumSpan
         */
        public float getMinimumSpan(int axis) {
            updateMetrics();
            return super.getMinimumSpan(axis);
        }

        /**
         * Determines the maximum span for this view along an axis. This is implemented to provide the superclass
         * behavior after first making sure that the current font metrics are cached (for the nested lines which use the
         * metrics to determine the height of the potentially wrapped lines).
         * 
         * @param axis may be either View.X_AXIS or View.Y_AXIS
         * @return the span the view would like to be rendered into. Typically the view is told to render into the span
         *         that is returned, although there is no guarantee. The parent may choose to resize or break the view.
         * @see View#getMaximumSpan
         */
        public float getMaximumSpan(int axis) {
            updateMetrics();
            return super.getMaximumSpan(axis);
        }

        /**
         * Gives notification that something was inserted into the document in a location that this view is responsible
         * for. This is implemented to simply update the children.
         * 
         * @param e the change information from the associated document
         * @param a the current allocation of the view
         * @param f the factory to use to rebuild if the view has children
         * @see View#insertUpdate
         */
        public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
            updateChildren(e, a);

            Rectangle alloc = ((a != null) && isAllocationValid()) ? getInsideAllocation(a) : null;
            int pos = e.getOffset();
            View v = getViewAtPosition(pos, alloc);
            if (v != null) {
                v.insertUpdate(e, alloc, f);
            }
        }

        /**
         * Gives notification that something was removed from the document in a location that this view is responsible
         * for. This is implemented to simply update the children.
         * 
         * @param e the change information from the associated document
         * @param a the current allocation of the view
         * @param f the factory to use to rebuild if the view has children
         * @see View#removeUpdate
         */
        public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
            updateChildren(e, a);

            Rectangle alloc = ((a != null) && isAllocationValid()) ? getInsideAllocation(a) : null;
            int pos = e.getOffset();
            View v = getViewAtPosition(pos, alloc);
            if (v != null) {
                v.removeUpdate(e, alloc, f);
            }
        }

        /**
         * Gives notification from the document that attributes were changed in a location that this view is responsible
         * for.
         * 
         * @param e the change information from the associated document
         * @param a the current allocation of the view
         * @param f the factory to use to rebuild if the view has children
         * @see View#changedUpdate
         */
        public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
            updateChildren(e, a);
        }

        // --- variables -------------------------------------------

        FontMetrics metrics;

        Segment lineBuffer;

        boolean widthChanging;

        int tabBase;

        int tabSize;

        boolean wordWrap;

        int sel0;

        int sel1;

        Color unselected;

        Color selected;

        /**
         * Simple view of a line that wraps if it doesn't fit withing the horizontal space allocated. This class tries
         * to be lightweight by carrying little state of it's own and sharing the state of the outer class with it's
         * sibblings.
         */
        class WrappedLine extends View {

            WrappedLine(Element elem) {
                super(elem);
                lineCount = -1;
            }

            /**
             * Determines the preferred span for this view along an axis.
             * 
             * @param axis may be either X_AXIS or Y_AXIS
             * @return the span the view would like to be rendered into. Typically the view is told to render into the
             *         span that is returned, although there is no guarantee. The parent may choose to resize or break
             *         the view.
             * @see View#getPreferredSpan
             */
            public float getPreferredSpan(int axis) {
                switch (axis) {
                case View.X_AXIS:
                    float width = getWidth();
                    if (width == Integer.MAX_VALUE) {
                        // We have been initially set to MAX_VALUE, but we don't
                        // want this as our preferred.
                        return 100f;
                    }
                    return width;
                case View.Y_AXIS:
                    if (lineCount < 0 || widthChanging) {
                        breakLines(getStartOffset());
                    }
                    int h = lineCount * metrics.getHeight();
                    return h;
                default:
                    throw new IllegalArgumentException("Invalid axis: " + axis);
                }
            }

            /**
             * Renders using the given rendering surface and area on that surface. The view may need to do layout and
             * create child views to enable itself to render into the given allocation.
             * 
             * @param g the rendering surface to use
             * @param a the allocated region to render into
             * @see View#paint
             */
            public void paint(Graphics g, Shape a) {
                Rectangle alloc = (Rectangle) a;
                int y = alloc.y + metrics.getAscent();
                int x = alloc.x;

                int start = getStartOffset();
                int end = getEndOffset();
                int p0 = start;
                int[] lineEnds = getLineEnds();
                for (int i = 0; i < lineCount; i++) {
                    int p1 = (lineEnds == null) ? end : start + lineEnds[i];
                    drawLine(p0, p1, g, x, y);

                    p0 = p1;
                    y += metrics.getHeight();
                }
            }

            /**
             * Provides a mapping from the document model coordinate space to the coordinate space of the view mapped to
             * it.
             * 
             * @param pos the position to convert
             * @param a the allocated region to render into
             * @return the bounding box of the given position is returned
             * @exception BadLocationException if the given position does not represent a valid location in the
             *            associated document
             * @see View#modelToView
             */
            public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
                Rectangle alloc = a.getBounds();
                alloc.height = metrics.getHeight();
                alloc.width = 1;

                int p0 = getStartOffset();
                if (pos < p0 || pos > getEndOffset()) {
                    throw new BadLocationException("Position out of range", pos);
                }

                int testP = (b == Position.Bias.Forward) ? pos : Math.max(p0, pos - 1);
                int line = 0;
                int[] lineEnds = getLineEnds();
                if (lineEnds != null) {
                    line = findLine(testP - p0);
                    if (line > 0) {
                        p0 += lineEnds[line - 1];
                    }
                    alloc.y += alloc.height * line;
                }

                if (pos > p0) {
                    Segment segment = SegmentCache.getSharedSegment();
                    loadText(segment, p0, pos);
                    alloc.x += AlterUtilities.getTabbedTextWidth(segment, metrics, alloc.x, AlterWrappedPlainView.this,
                            p0);
                    SegmentCache.releaseSharedSegment(segment);
                }
                return alloc;
            }

            /**
             * Provides a mapping from the view coordinate space to the logical coordinate space of the model.
             * 
             * @param fx the X coordinate
             * @param fy the Y coordinate
             * @param a the allocated region to render into
             * @return the location within the model that best represents the given point in the view
             * @see View#viewToModel
             */
            public int viewToModel(float fx, float fy, Shape a, Position.Bias[] bias) {
                // PENDING(prinz) implement bias properly
                bias[0] = Position.Bias.Forward;

                Rectangle alloc = (Rectangle) a;
                int x = (int) fx;
                int y = (int) fy;
                if (y < alloc.y) {
                    // above the area covered by this icon, so the the position
                    // is assumed to be the start of the coverage for this view.
                    return getStartOffset();
                } else if (y > alloc.y + alloc.height) {
                    // below the area covered by this icon, so the the position
                    // is assumed to be the end of the coverage for this view.
                    return getEndOffset() - 1;
                } else {
                    // positioned within the coverage of this view vertically,
                    // so we figure out which line the point corresponds to.
                    // if the line is greater than the number of lines contained, then
                    // simply use the last line as it represents the last possible place
                    // we can position to.
                    alloc.height = metrics.getHeight();
                    int line = (y - alloc.y) / alloc.height;
                    if (line >= lineCount) {
                        return getEndOffset() - 1;
                    } else {
                        int p0 = getStartOffset();
                        int p1;
                        if (lineCount == 1) {
                            p1 = getEndOffset();
                        } else {
                            int[] lineEnds = getLineEnds();
                            p1 = p0 + lineEnds[line];
                            if (line > 0) {
                                p0 += lineEnds[line - 1];
                            }
                        }

                        if (x < alloc.x) {
                            // point is to the left of the line
                            return p0;
                        } else if (x > alloc.x + alloc.width) {
                            // point is to the right of the line
                            return p1 - 1;
                        } else {
                            // Determine the offset into the text
                            Segment segment = SegmentCache.getSharedSegment();
                            loadText(segment, p0, p1);
                            int n = AlterUtilities.getTabbedTextOffset(segment, metrics, alloc.x, x,
                                    AlterWrappedPlainView.this, p0);
                            SegmentCache.releaseSharedSegment(segment);
                            return Math.min(p0 + n, p1 - 1);
                        }
                    }
                }
            }

            public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
                update(e, a);
            }

            public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
                update(e, a);
            }

            private void update(DocumentEvent ev, Shape a) {
                int oldCount = lineCount;
                breakLines(ev.getOffset());
                if (oldCount != lineCount) {
                    AlterWrappedPlainView.this.preferenceChanged(this, false, true);
                    // have to repaint any views after the receiver.
                    getContainer().repaint();
                } else if (a != null) {
                    Component c = getContainer();
                    Rectangle alloc = (Rectangle) a;
                    c.repaint(alloc.x, alloc.y, alloc.width, alloc.height);
                }
            }

            /**
             * Returns line cache. If the cache was GC'ed, recreates it. If there's no cache, returns null
             */
            final int[] getLineEnds() {
                if (lineCache == null) {
                    return null;
                } else {
                    int[] lineEnds = lineCache.get();
                    if (lineEnds == null) {
                        // Cache was GC'ed, so rebuild it
                        return breakLines(getStartOffset());
                    } else {
                        return lineEnds;
                    }
                }
            }

            /**
             * Creates line cache if text breaks into more than one physical line.
             * 
             * @param startPos position to start breaking from
             * @return the cache created, ot null if text breaks into one line
             */
            final int[] breakLines(int startPos) {
                int[] lineEnds = (lineCache == null) ? null : lineCache.get();
                int[] oldLineEnds = lineEnds;
                int start = getStartOffset();
                int lineIndex = 0;
                if (lineEnds != null) {
                    lineIndex = findLine(startPos - start);
                    if (lineIndex > 0) {
                        lineIndex--;
                    }
                }

                int p0 = (lineIndex == 0) ? start : start + lineEnds[lineIndex - 1];
                int p1 = getEndOffset();
                while (p0 < p1) {
                    int p = calculateBreakPosition(p0, p1);
                    p0 = (p == p0) ? ++p : p; // 4410243

                    if (lineIndex == 0 && p0 >= p1) {
                        // do not use cache if there's only one line
                        lineCache = null;
                        lineEnds = null;
                        lineIndex = 1;
                        break;
                    } else if (lineEnds == null || lineIndex >= lineEnds.length) {
                        // we have 2+ lines, and the cache is not big enough
                        // we try to estimate total number of lines
                        double growFactor = ((double) (p1 - start) / (p0 - start));
                        int newSize = (int) Math.ceil((lineIndex + 1) * growFactor);
                        newSize = Math.max(newSize, lineIndex + 2);
                        int[] tmp = new int[newSize];
                        if (lineEnds != null) {
                            System.arraycopy(lineEnds, 0, tmp, 0, lineIndex);
                        }
                        lineEnds = tmp;
                    }
                    lineEnds[lineIndex++] = p0 - start;
                }

                lineCount = lineIndex;
                if (lineCount > 1) {
                    // check if the cache is too big
                    int maxCapacity = lineCount + lineCount / 3;
                    if (lineEnds.length > maxCapacity) {
                        int[] tmp = new int[maxCapacity];
                        System.arraycopy(lineEnds, 0, tmp, 0, lineCount);
                        lineEnds = tmp;
                    }
                }

                if (lineEnds != null && lineEnds != oldLineEnds) {
                    lineCache = new SoftReference<int[]>(lineEnds);
                }
                return lineEnds;
            }

            /**
             * Binary search in the cache for line containing specified offset (which is relative to the beginning of
             * the view). This method assumes that cache exists.
             */
            private int findLine(int offset) {
                int[] lineEnds = lineCache.get();
                if (offset < lineEnds[0]) {
                    return 0;
                } else if (offset > lineEnds[lineCount - 1]) {
                    return lineCount;
                } else {
                    return findLine(lineEnds, offset, 0, lineCount - 1);
                }
            }

            private int findLine(int[] array, int offset, int min, int max) {
                if (max - min <= 1) {
                    return max;
                } else {
                    int mid = (max + min) / 2;
                    return (offset < array[mid]) ? findLine(array, offset, min, mid)
                            : findLine(array, offset, mid, max);
                }
            }

            int lineCount;

            SoftReference<int[]> lineCache = null;
        }
    }

    /**
     * SegmentCache caches <code>Segment</code>s to avoid continually creating and destroying of <code>Segment</code>s.
     * A common use of this class would be:
     * 
     * <pre>
     *   Segment segment = segmentCache.getSegment();
     *   // do something with segment
     *   ...
     *   segmentCache.releaseSegment(segment);
     * </pre>
     * 
     * @version 1.6 11/17/05
     */
    static class SegmentCache {
        /**
         * A global cache.
         */
        private static SegmentCache sharedCache = new SegmentCache();

        /**
         * A list of the currently unused Segments.
         */
        private List<Segment> segments;

        /**
         * Returns the shared SegmentCache.
         */
        public static SegmentCache getSharedInstance() {
            return sharedCache;
        }

        /**
         * A convenience method to get a Segment from the shared <code>SegmentCache</code>.
         */
        public static Segment getSharedSegment() {
            return getSharedInstance().getSegment();
        }

        /**
         * A convenience method to release a Segment to the shared <code>SegmentCache</code>.
         */
        public static void releaseSharedSegment(Segment segment) {
            getSharedInstance().releaseSegment(segment);
        }

        /**
         * Creates and returns a SegmentCache.
         */
        public SegmentCache() {
            segments = new ArrayList<Segment>(11);
        }

        /**
         * Returns a <code>Segment</code>. When done, the <code>Segment</code> should be recycled by invoking
         * <code>releaseSegment</code>.
         */
        public Segment getSegment() {
            synchronized (this) {
                int size = segments.size();

                if (size > 0) {
                    return segments.remove(size - 1);
                }
            }
            return new CachedSegment();
        }

        /**
         * Releases a Segment. You should not use a Segment after you release it, and you should NEVER release the same
         * Segment more than once, eg:
         * 
         * <pre>
         * segmentCache.releaseSegment(segment);
         * segmentCache.releaseSegment(segment);
         * </pre>
         * 
         * Will likely result in very bad things happening!
         */
        public void releaseSegment(Segment segment) {
            if (segment instanceof CachedSegment) {
                synchronized (this) {
                    segment.array = null;
                    segment.count = 0;
                    segments.add(segment);
                }
            }
        }

        /**
         * CachedSegment is used as a tagging interface to determine if a Segment can successfully be shared.
         */
        private static class CachedSegment extends Segment {
        }
    }

    /**
     * <code>AnotherCompositeView</code>
     * 
     * @inheritDoc
     */
    abstract class AlterCompositeView extends CompositeView {

        /**
         * Constructs a <code>CompositeView</code> for the given element.
         * 
         * @param elem the element this view is responsible for
         */
        public AlterCompositeView(Element elem) {
            super(elem);
        }

        @Override
        protected boolean flipEastAndWestAtEnds(int position, Bias bias) {
            return super.flipEastAndWestAtEnds(position, bias);
        }

    }

    /**
     * <code>AnotherBoxView</code>
     * 
     * @inheritDoc
     */
    static class AlterBoxView extends BoxView {

        /** used in paint. */
        Rectangle tempRect;

        boolean majorAllocValid;

        SizeRequirements minorRequest;

        public AlterBoxView(Element elem, int axis) {
            super(elem, axis);
        }

        @Override
        protected short getRightInset() {
            return super.getRightInset();
        }

        // TODO: override following 2 methods and Renderer.paint() to make this a ShapeView rather then BoxView ;)
        @Override
        public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
            if (!isAllocationValid()) {
                Rectangle alloc = a.getBounds();
                setSize(alloc.width, alloc.height);
            }
            return super.viewToModel(x, y, a, bias);
        }

        @Override
        public void setSize(float width, float height) {
            layout((int) (width - getLeftInset() - getRightInset()), (int) (height - getTopInset() - getBottomInset()));
        }
    }

    /**
     * This exception is to report the failure of state invarient assertion that was made. This indicates an internal
     * error has occurred.
     * 
     * @author Timothy Prinzing
     * @version 1.18 11/17/05
     */
    static class StateInvariantError extends Error {
        /**
         * Creates a new StateInvariantFailure object.
         * 
         * @param s a string indicating the assertion that failed
         */
        public StateInvariantError(String s) {
            super(s);
        }

    }
}
