package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;

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
//	private boolean hasBeenEntered = false;

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
//        setHorizontalAlignment(LEADING);
		addMouseListener(new HyperlinkMouseListener());
        addFocusListener(new HyperlinkFocusListener());
//        addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                setVisited(true);
//            }
//        });
        setForeground(isVisited() ? getClickedColor() : getUnclickedColor());
    }

    private final class HyperlinkFocusListener implements FocusListener {

        public void focusGained(FocusEvent e) {
            entered();
            
        }

        public void focusLost(FocusEvent e) {
            exited(false);
            
        }
        
    }
	/**
	 * Inner utility class that provides MouseListener capabilities for this
	 * hyperlink.  Changes the color of the hyperlink when selected, the cursor
	 * for the hyperlink when the mouse is over it, and so forth.
	 * @author Richard Bair
	 */
	private final class HyperlinkMouseListener extends MouseAdapter {
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		public void mouseEntered(MouseEvent e) {
            entered();
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		public void mouseExited(MouseEvent e) {
            exited(true);
		}
	}

	protected void paintComponent(Graphics g) {
        // JW: _never ever_ change component properties in a paintXX!
//        Color c = hasBeenClicked ? clickedColor : unclickedColor;
//        
//        //set the foreground color based on the click state
//        setForeground(c);

        super.paintComponent(g);

        //add an underline to the font if necessary
//        if(isEnabled() && isVisible()) {
//            String text = getText();
//            if (text != null && text.length() > 0) {
//                if (hasBeenEntered) {
////                    g.setColor(c);        
//                    Icon ico = getIcon();
//                    int y = getHeight();
//                    int x = ico == null ? 0 : ico.getIconWidth() + 1;
//                    // JW: causes #5 - instead need to get hold of textRect somehow
//                    // probably possible only in UIDelegate?
//                    g.drawLine(x, getHeight()-1, SwingUtilities.computeStringWidth(g.getFontMetrics(), text) + x, getHeight()-1);
//                }
//            }
//        }
	}

    private void entered() {
    //    repaint();
        //change the cursor
        oldCursor = SwingUtilities.getWindowAncestor(JXHyperlinkButton.this).getCursor();
        SwingUtilities.getWindowAncestor(JXHyperlinkButton.this).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setText("<html><u>" + getActionText() + "</u></html>");
    }

    protected void exited(boolean fromMouse) {
        if (fromMouse && hasFocus()) return;
     //   repaint();
        if (oldCursor != null) {
        	SwingUtilities.getWindowAncestor(JXHyperlinkButton.this).setCursor(oldCursor);
        }
        setText(getActionText());
    }

    private String getActionText() {
        return getAction() != null ? 
                String.valueOf(getAction().getValue(Action.NAME)) : "linktext";
    }


}
