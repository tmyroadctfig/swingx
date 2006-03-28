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
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JButton;

import org.jdesktop.swingx.action.LinkAction;
import org.jdesktop.swingx.plaf.JXHyperlinkAddon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;

/**
 * A hyperlink component that derives from JButton to provide compatibility
 * mostly for binding actions enabled/disabled behavior accesilibity i18n etc...
 * <p>
 *
 * This button has visual state related to a notion of "clicked": 
 * foreground color is unclickedColor or clickedColor depending on 
 * its boolean bound property clicked being false or true, respectively.
 * <p>
 * 
 * 
 * 
 * 
 * @author Richard Bair
 * @author Shai Almog
 * @author Jeanette Winzenburg
 */
public class JXHyperlink extends JButton {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    public static final String uiClassID = "HyperlinkUI";

    // ensure at least the default ui is registered
    static {
      LookAndFeelAddons.contribute(new JXHyperlinkAddon());
    }

    private boolean hasBeenVisited = false;

    /**
     * Color for the hyper link if it has not yet been clicked. This color can
     * be set both in code, and through the UIManager with the property
     * "JXHyperlink.unclickedColor".
     */
    private Color unclickedColor = new Color(0, 0x33, 0xFF);

    /**
     * Color for the hyper link if it has already been clicked. This color can
     * be set both in code, and through the UIManager with the property
     * "JXHyperlink.clickedColor".
     */
    private Color clickedColor = new Color(0x99, 0, 0x99);

    private boolean overrulesActionOnClick;

    /**
     * Creates a new instance of JXHyperlink with default parameters
     */
    public JXHyperlink() {
        this(null);
    }

    /**
     * Creates a new instance of JHyperLink and configures it from provided Action.
     *
     * @param action Action whose parameters will be borrowed to configure newly 
     *        created JXHyperLink
     */
    public JXHyperlink(Action action) {
        super();
        setAction(action);
        init();
    }

    /**
     * @return Color for the hyper link if it has not yet been clicked.
     */
    public Color getUnclickedColor() {
        return unclickedColor;
    }

    /**
     * Sets the color for the previously not visited link. This value will override the one
     * set by the "JXHyperlink.unclickedColor" UIManager property and defaults.
     *
     * @param color Color for the hyper link if it has not yet been clicked.
     */
    public void setClickedColor(Color color) {
        Color old = getClickedColor();
        clickedColor = color;
        if (isClicked()) {
            setForeground(getClickedColor());
        }
        firePropertyChange("clickedColor", old, getClickedColor());
    }

    /**
     * @return Color for the hyper link if it has already been clicked.
     */
    public Color getClickedColor() {
        return clickedColor;
    }

    /**
     * Sets the color for the previously visited link. This value will override the one
     * set by the "JXHyperlink.clickedColor" UIManager property and defaults.
     *
     * @param color Color for the hyper link if it has already been clicked.
     */
    public void setUnclickedColor(Color color) {
        Color old = getUnclickedColor();
        unclickedColor = color;
        if (!isClicked()) {
            setForeground(getUnclickedColor());
        }
        firePropertyChange("unclickedColor", old, getUnclickedColor());
    }

    /**
     * Sets the clicked property and updates visual state depending
     * on clicked.
     * Here: the dependent visual state is the foreground color.
     * NOTE: as with all  button's visual properties, this will not update 
     * the backing action's "visited" state.
     * 
     * @param clicked flag to indicate if the button should be regarded
     *    as having been clicked or not.
     */
    public void setClicked(boolean clicked) {
        boolean old = isClicked();
        hasBeenVisited = clicked;
        setForeground(isClicked() ? getClickedColor() : getUnclickedColor());
        firePropertyChange("clicked", old, isClicked());
   }

    /**
     * @return <code>true</code> if hyper link has already been clicked.
     */
    public boolean isClicked() {
        return hasBeenVisited;
    }

    /**
     * Control auto-click property. 
     * 
     * @param overrule if true, fireActionPerformed will set clicked to true
     *   independent of action.
     * 
     */
    public void setOverrulesActionOnClick(boolean overrule) {
        boolean old = getOverrulesActionOnClick();
        this.overrulesActionOnClick = overrule;
        firePropertyChange("overrulesActionOnClick", old, getOverrulesActionOnClick());
    }
    
    /**
     * Returns whether the clicked property should be set always on clicked.
     * 
     * Defaults to false.
     * 
     * @return overrulesActionOnClick 
     */
    public boolean getOverrulesActionOnClick() {
        return overrulesActionOnClick;
    }

    /**
     * override to control auto-clicked. 
     */
    @Override
    protected void fireActionPerformed(ActionEvent event) {
        super.fireActionPerformed(event);
        if (isAutoSetClicked()) {
            setClicked(true);
        }
    }

    /**
     * Decides auto-setting of clicked property after firing action events.
     * Here: true if no action or overrulesAction property is true.
     * @return true if fireActionEvent should force a clicked, false if not.
     */
    protected boolean isAutoSetClicked() {
        return getAction() == null || getOverrulesActionOnClick();
    }

    /**
     * Create listener that will watch the changes of the provided <code>Action</code>
     * and will update JXHyperlink's properties accordingly.
     */
    protected PropertyChangeListener createActionPropertyChangeListener(
            final Action a) {
        final PropertyChangeListener superListener = super
                .createActionPropertyChangeListener(a);
        // JW: need to do something better - only weak refs allowed!
        // no way to hook into super
        PropertyChangeListener l = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (LinkAction.VISITED_KEY.equals(evt.getPropertyName())) {
                    configureClickedPropertyFromAction(a);
                } else {
                    superListener.propertyChange(evt);
                }

            }

        };
        return l;
    }

    /**
     * Read all the essentional properties from the provided <code>Action</code>
     * and apply it to the <code>JXHyperlink</code>
     */
    @Override
    protected void configurePropertiesFromAction(Action a) {
        super.configurePropertiesFromAction(a);
        configureClickedPropertyFromAction(a);
    }

    private void configureClickedPropertyFromAction(Action a) {
        boolean clicked = false;
        if (a != null) {
            clicked = Boolean.TRUE.equals(a.getValue(LinkAction.VISITED_KEY));
            
        }
        setClicked(clicked);
    }

    private void init() {
        setForeground(isClicked() ? getClickedColor() : getUnclickedColor());
    }

    /**
     * Returns a string that specifies the name of the L&F class
     * that renders this component.
     */
    public String getUIClassID() {
        return uiClassID;
    }
}
