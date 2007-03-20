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

package org.jdesktop.swingx.plaf.basic;

import org.jdesktop.swingx.JXEditorPane;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.plaf.HeaderUI;
import org.jdesktop.swingx.plaf.PainterUIResource;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.CompositeView;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.GlyphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.WrappedPlainView;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author rbair
 */
public class BasicHeaderUI extends HeaderUI {
    protected JLabel titleLabel;
    protected JXEditorPane descriptionPane;
    protected JLabel imagePanel;
    private PropertyChangeListener propListener;

    /** Creates a new instance of BasicHeaderUI */
    public BasicHeaderUI() {
    }

    /**
     * Returns an instance of the UI delegate for the specified component.
     * Each subclass must provide its own static <code>createUI</code>
     * method that returns an instance of that UI delegate subclass.
     * If the UI delegate subclass is stateless, it may return an instance
     * that is shared by multiple components.  If the UI delegate is
     * stateful, then it should return a new instance per component.
     * The default implementation of this method throws an error, as it
     * should never be invoked.
     */
    public static ComponentUI createUI(JComponent c) {
        return new BasicHeaderUI();
    }

    /**
     * Configures the specified component appropriate for the look and feel.
     * This method is invoked when the <code>ComponentUI</code> instance is being installed
     * as the UI delegate on the specified component.  This method should
     * completely configure the component for the look and feel,
     * including the following:
     * <ol>
     * <li>Install any default property values for color, fonts, borders,
     *     icons, opacity, etc. on the component.  Whenever possible,
     *     property values initialized by the client program should <i>not</i>
     *     be overridden.
     * <li>Install a <code>LayoutManager</code> on the component if necessary.
     * <li>Create/add any required sub-components to the component.
     * <li>Create/install event listeners on the component.
     * <li>Create/install a <code>PropertyChangeListener</code> on the component in order
     *     to detect and respond to component property changes appropriately.
     * <li>Install keyboard UI (mnemonics, traversal, etc.) on the component.
     * <li>Initialize any appropriate instance data.
     * </ol>
     * @param c the component where this UI delegate is being installed
     *
     * @see #uninstallUI
     * @see javax.swing.JComponent#setUI
     * @see javax.swing.JComponent#updateUI
     */
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        assert c instanceof JXHeader;
        JXHeader header = (JXHeader)c;

        installDefaults(header);

        titleLabel = new JLabel("Title For Header Goes Here");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        descriptionPane = new JXEditorPane();
        String type = "text/plain";
        descriptionPane.setEditorKitForContentType(type, new WrappingPlainEditorKit());
        descriptionPane.setContentType(type);
        descriptionPane.setEditable(false);
        descriptionPane.setOpaque(false);
        
        descriptionPane.setText("The description for the header goes here.\nExample: Click the Copy Code button to generate the corresponding Java code.");
        imagePanel = new JLabel();
        imagePanel.setIcon(UIManager.getIcon("Header.defaultIcon"));

        installComponents(header);
        installListeners(header);
    }

    /**
     * Reverses configuration which was done on the specified component during
     * <code>installUI</code>.  This method is invoked when this
     * <code>UIComponent</code> instance is being removed as the UI delegate
     * for the specified component.  This method should undo the
     * configuration performed in <code>installUI</code>, being careful to
     * leave the <code>JComponent</code> instance in a clean state (no
     * extraneous listeners, look-and-feel-specific property objects, etc.).
     * This should include the following:
     * <ol>
     * <li>Remove any UI-set borders from the component.
     * <li>Remove any UI-set layout managers on the component.
     * <li>Remove any UI-added sub-components from the component.
     * <li>Remove any UI-added event/property listeners from the component.
     * <li>Remove any UI-installed keyboard UI from the component.
     * <li>Nullify any allocated instance data objects to allow for GC.
     * </ol>
     * @param c the component from which this UI delegate is being removed;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components
     *
     * @see #installUI
     * @see javax.swing.JComponent#updateUI
     */
    @Override
    public void uninstallUI(JComponent c) {
        assert c instanceof JXHeader;
        JXHeader header = (JXHeader)c;

        uninstallDefaults(header);
        uninstallListeners(header);
        uninstallComponents(header);

        titleLabel = null;
        descriptionPane = null;
        imagePanel = null;
    }

    protected void installDefaults(JXHeader h) {
        Painter p = h.getBackgroundPainter();
        if (p == null || p instanceof PainterUIResource) {
            h.setBackgroundPainter(createBackgroundPainter());
        }
    }

    protected void uninstallDefaults(JXHeader h) {
    }

    protected void installListeners(final JXHeader h) {
        propListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                onPropertyChange(h, evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
            }
        };
        h.addPropertyChangeListener(propListener);
    }

    protected void uninstallListeners(JXHeader h) {
        h.removePropertyChangeListener(propListener);
    }

    protected void onPropertyChange(JXHeader h, String propertyName, Object oldValue, Object newValue) {
        if ("title".equals(propertyName)) {
            titleLabel.setText(h.getTitle());
        } else if ("description".equals(propertyName)) {
            descriptionPane.setText(h.getDescription());
        } else if ("icon".equals(propertyName)) {
            imagePanel.setIcon(h.getIcon());
        } else if ("enabled".equals(propertyName)) {
            boolean enabled = h.isEnabled();
            titleLabel.setEnabled(enabled);
            descriptionPane.setEnabled(enabled);
            imagePanel.setEnabled(enabled);
        } else if ("titleFont".equals(propertyName)) {
            titleLabel.setFont((Font)newValue);
        } else if ("descriptionFont".equals(propertyName)) {
            descriptionPane.setFont((Font)newValue);
        }
    }

    protected void installComponents(JXHeader h) {
        h.setLayout(new GridBagLayout());
        h.add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(12, 12, 0, 11), 0, 0));
        h.add(descriptionPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 24, 0, 11), 0, 0));
        h.add(imagePanel, new GridBagConstraints(1, 0, 1, 2, 0.0, 1.0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(12, 0, 11, 11), 0, 0));
    }

    protected void uninstallComponents(JXHeader h) {
        h.remove(titleLabel);
        h.remove(descriptionPane);
        h.remove(imagePanel);
    }

    protected Painter createBackgroundPainter() {
        MattePainter p = new MattePainter(new GradientPaint(0, 0, Color.WHITE, 1, 0, UIManager.getColor("control")));
        p.setPaintStretched(true);
        return new PainterUIResource(p);
    }

    /**
     * <code>WrappingPlainEditorKit</code> 
     * Copy of package protected PlainEditorKit from JEditorPane with word wrapping enabled.
     */
    static class WrappingPlainEditorKit extends DefaultEditorKit implements ViewFactory {

        /**
         * Fetches a factory that is suitable for producing 
         * views of any models that are produced by this
         * kit.  The default is to have the UI produce the
         * factory, so this method has no implementation.
         *
         * @return the view factory
         */
            public ViewFactory getViewFactory() {
            return this;
        }

        /**
         * Creates a view from the given structural element of a
         * document.
         *
         * @param elem  the piece of the document to build a view of
         * @return the view
         * @see View
         */
            public View create(Element elem) {
                Document doc = elem.getDocument();
                Object i18nFlag
                    = doc.getProperty("i18n"/*AbstractDocument.I18NProperty*/);
                if ((i18nFlag != null) && i18nFlag.equals(Boolean.TRUE)) {
                    // build a view that support bidi
                    return createI18N(elem);
                } else {
                    return new WrappedPlainView(elem, true);
                }
            }

            View createI18N(Element elem) {
                String kind = elem.getName();
                if (kind != null) {
                    if (kind.equals(AbstractDocument.ContentElementName)) {
                        return new PlainParagraph(elem);
                    } else if (kind.equals(AbstractDocument.ParagraphElementName)){
                        return new BoxView(elem, View.Y_AXIS);
                    }
                }
                return null;
            }

            /**
             * Paragraph for representing plain-text lines that support
             * bidirectional text.
             */
            static class PlainParagraph extends javax.swing.text.ParagraphView {

                PlainParagraph(Element elem) {
                    super(elem);
                    layoutPool = new LogicalView(elem);
                    layoutPool.setParent(this);
                }

                protected void setPropertiesFromAttributes() {
                    Component c = getContainer();
                    if ((c != null) 
                        && (! c.getComponentOrientation().isLeftToRight()))
                    {
                        setJustification(StyleConstants.ALIGN_RIGHT);
                    } else {
                        setJustification(StyleConstants.ALIGN_LEFT);
                    }
                }

                /**
                 * Fetch the constraining span to flow against for
                 * the given child index.
                 */
                public int getFlowSpan(int index) {
                    Component c = getContainer();
                    if (c instanceof JTextArea) {
                        JTextArea area = (JTextArea) c;
                        if (! area.getLineWrap()) {
                            // no limit if unwrapped
                            return Integer.MAX_VALUE;
                        }
                    }
                    return super.getFlowSpan(index);
                }

                protected SizeRequirements calculateMinorAxisRequirements(int axis,
                                                                SizeRequirements r)
                {
                    SizeRequirements req 
                        = super.calculateMinorAxisRequirements(axis, r);
                    Component c = getContainer();
                    if (c instanceof JTextArea) {
                        JTextArea area = (JTextArea) c;
                        if (! area.getLineWrap()) {
                            // min is pref if unwrapped
                            req.minimum = req.preferred;
                        }
                    }
                    return req;
                }

                /**
                 * This class can be used to represent a logical view for 
                 * a flow.  It keeps the children updated to reflect the state
                 * of the model, gives the logical child views access to the
                 * view hierarchy, and calculates a preferred span.  It doesn't
                 * do any rendering, layout, or model/view translation.
                 */
                static class LogicalView extends CompositeView {
            
                    LogicalView(Element elem) {
                        super(elem);
                    }

                    protected int getViewIndexAtPosition(int pos) {
                        Element elem = getElement();
                        if (elem.getElementCount() > 0) {
                            return elem.getElementIndex(pos);
                        }
                        return 0;
                    }

                    protected boolean 
                    updateChildren(DocumentEvent.ElementChange ec, 
                                   DocumentEvent e, ViewFactory f)
                    {
                        return false;
                    }

                    protected void loadChildren(ViewFactory f) {
                        Element elem = getElement();
                        if (elem.getElementCount() > 0) {
                            super.loadChildren(f);
                        } else {
                            View v = new GlyphView(elem);
                            append(v);
                        }
                    }

                    public float getPreferredSpan(int axis) {
                        if( getViewCount() != 1 )
                            throw new Error("One child view is assumed.");
                    
                        View v = getView(0);
                        //((GlyphView)v).setGlyphPainter(null);
                        return v.getPreferredSpan(axis);
                    }

                    /**
                     * Forward the DocumentEvent to the given child view.  This
                     * is implemented to reparent the child to the logical view
                     * (the children may have been parented by a row in the flow
                     * if they fit without breaking) and then execute the 
                     * superclass behavior.
                     *
                     * @param v the child view to forward the event to.
                     * @param e the change information from the associated document
                     * @param a the current allocation of the view
                     * @param f the factory to use to rebuild if the view has 
                     *          children
                     * @see #forwardUpdate
                     * @since 1.3
                     */
                    protected void forwardUpdateToView(View v, DocumentEvent e, 
                                                       Shape a, ViewFactory f) {
                        v.setParent(this);
                        super.forwardUpdateToView(v, e, a, f);
                    }

                    // The following methods don't do anything useful, they
                    // simply keep the class from being abstract.

                    public void paint(Graphics g, Shape allocation) {
                    }

                    protected boolean isBefore(int x, int y, Rectangle alloc) {
                        return false;
                    }

                    protected boolean isAfter(int x, int y, Rectangle alloc) {
                        return false;
                    }

                    protected View getViewAtPoint(int x, int y, Rectangle alloc) {
                        return null;
                    }

                    protected void childAllocation(int index, Rectangle a) {
                    }
                }
            }
        }
}
