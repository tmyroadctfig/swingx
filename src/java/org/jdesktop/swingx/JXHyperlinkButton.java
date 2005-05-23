package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.util.Link;

/**
 * A hyperlink component that derives from JButton to provide 
 * compatibility mostly for binding actions
 * enabled/disabled behavior accesility i18n etc...
 *
 * @author Richard Bair
 * @author Shai Almog
 * @author Jeanette Winzenburg
 */
public class JXHyperlinkButton extends JButton {
    private boolean hasBeenClicked = false;

	/**
	 * Indicates whether the mouse has entered this hyper link
	 */
	private boolean hasBeenEntered = false;

    /**
	 * Color for the hyper link if it has not yet been clicked.
	 * This color can be set both in code, and through the UIManager
	 * with the property "JXHyperlink.unclickedColor".
	 */
	private Color unclickedColor = new Color(0, 0x33, 0xFF);
	
    /**
	 * Color for the hyper link if it has already been clicked.
	 * This color can be set both in code, and through the UIManager
	 * with the property "JXHyperlink.clickedColor".
	 */
	private Color clickedColor = new Color(0x99, 0, 0x99);

    private Cursor oldCursor = null;
    

    /** Creates a new instance of JHyperlink */
    public JXHyperlinkButton() {
        super();
    }
    
    public JXHyperlinkButton(Action action) {
        super(action);
        init();
    }

//    public JHyperlink(Icon icon) {
//        super(icon);
//        init();
//    }
//
//    public JHyperlink(String text, Icon icon) {
//        super(text, icon);
//        init();
//    }
//    
//    public JHyperlink(String text) {
//        super(text);
//        init();
//    }

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
        hasBeenClicked = visited;
        setForeground(isVisited() ? getClickedColor() : getUnclickedColor());
        firePropertyChange("visited", old, isVisited());
    }

    protected boolean isVisited() {
        return hasBeenClicked;
    }


    protected void configurePropertiesFromAction(Action a) {
        super.configurePropertiesFromAction(a);
        setVisitedFromActionProperty(a);
    }

    private void setVisitedFromActionProperty(Action a) {
        Boolean visited = (Boolean) a.getValue(Link.VISITED_PROPERTY);
        setVisited(visited != null ? visited.booleanValue() : false);
    }
    
    
    protected PropertyChangeListener createActionPropertyChangeListener(final Action a) {
        final PropertyChangeListener superListener = super.createActionPropertyChangeListener(a);
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
    
    private void init() {
        // null can't be used since a L&F might replace it
        setBorder(BorderFactory.createEmptyBorder());
        setContentAreaFilled(false);
        setFocusPainted(false);
        setRolloverEnabled(true);
        addChangeListener(new RolloverListener());
//        setHorizontalAlignment(LEADING);
//		addMouseListener(new HyperlinkMouseListener());
        addFocusListener(new HyperlinkFocusListener());
        setForeground(isVisited() ? getClickedColor() : getUnclickedColor());
    }

    
    private class RolloverListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            if (getModel().isRollover() == hasBeenEntered) return;
            hasBeenEntered = getModel().isRollover();
            if (hasBeenEntered) {
                entered(true);
            } else {
                exited(true);
            }

        }

    }

    private final class HyperlinkFocusListener implements FocusListener {

        public void focusGained(FocusEvent e) {
            entered(false);
            
        }

        public void focusLost(FocusEvent e) {
            exited(false);
            
        }
        
    }
    
    /**
     * 
     * @param fromMouse
     */
    public void entered(boolean fromMouse) {
         //change the cursor
        // should happen as reaction to state change of rollover?
        if (fromMouse) {
            oldCursor = getCursor();
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        setText("<html><u>" + getActionText() + "</u></html>");
    }

    public void exited(boolean fromMouse) {
        if (fromMouse) {
            setCursor(oldCursor);
            oldCursor = null;
            if (hasFocus()) return;
        } else {
            if (getModel().isRollover()) return;
        }
        setText(getActionText());
    }

    private String getActionText() {
        return getAction() != null ? 
                String.valueOf(getAction().getValue(Action.NAME)) : "linktext";
    }


}
