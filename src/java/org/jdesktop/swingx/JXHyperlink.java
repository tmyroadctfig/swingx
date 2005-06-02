package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JButton;

import org.jdesktop.swingx.plaf.JXHyperlinkAddon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.util.Link;

/**
 * A hyperlink component that derives from JButton to provide compatibility
 * mostly for binding actions enabled/disabled behavior accesility i18n etc...
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

    /**
     * Initialization that would ideally be moved into various look and feel
     * classes.
     */
//    static {
//        loadDefaults();
//    }
//
//    static void loadDefaults() {
//        UIDefaults defaults = UIManager.getDefaults();
//        defaults.put(uiClassID,
//                "org.jdesktop.swingx.plaf.basic.BasicHyperlinkUI");
//    }

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

    /** Creates a new instance of JXHyperlink */
    public JXHyperlink() {
        super();
    }

    public JXHyperlink(Action action) {
        super(action);
        init();
    }

    /**
     * @return
     */
    public Color getUnclickedColor() {
        return unclickedColor;
    }

    /**
     * @param color
     */
    public void setClickedColor(Color color) {
        Color old = getClickedColor();
        clickedColor = color;
        if (isVisited()) {
            setForeground(getClickedColor());
        }
        firePropertyChange("clickedColor", old, getClickedColor());
    }

    /**
     * @return
     */
    public Color getClickedColor() {
        return clickedColor;
    }

    /**
     * @param color
     */
    public void setUnclickedColor(Color color) {
        Color old = getUnclickedColor();
        unclickedColor = color;
        if (!isVisited()) {
            setForeground(getUnclickedColor());
        }
        firePropertyChange("unclickedColor", old, getUnclickedColor());
    }

    protected void setVisited(boolean visited) {
        boolean old = isVisited();
        hasBeenVisited = visited;
        setForeground(isVisited() ? getClickedColor() : getUnclickedColor());
        firePropertyChange("visited", old, isVisited());
    }

    protected boolean isVisited() {
        return hasBeenVisited;
    }

    protected PropertyChangeListener createActionPropertyChangeListener(
            final Action a) {
        final PropertyChangeListener superListener = super
                .createActionPropertyChangeListener(a);
        // JW: need to do something better - only weak refs allowed!
        // no way to hook into super
        PropertyChangeListener l = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (Link.VISITED_PROPERTY.equals(evt.getPropertyName())) {
                    setVisitedFromActionProperty(a);
                } else {
                    superListener.propertyChange(evt);
                }

            }

        };
        return l;
    }

    protected void configurePropertiesFromAction(Action a) {
        super.configurePropertiesFromAction(a);
        setVisitedFromActionProperty(a);
    }

    private void setVisitedFromActionProperty(Action a) {
        Boolean visited = (Boolean) a.getValue(Link.VISITED_PROPERTY);
        setVisited(visited != null ? visited.booleanValue() : false);
    }

    private void init() {
        setForeground(isVisited() ? getClickedColor() : getUnclickedColor());
    }

    public String getUIClassID() {
        return uiClassID;
    }

}
