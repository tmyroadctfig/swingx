/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.util.UIManagerUtils;

/**
 * This class represents a hyper link.<br>
 * There are several different behaviors possible from a hyperlink:
 * <ol>
 * <li>Link looks like normal text unless the mouse rolls over it, at
 * which time it changes colors and shows an underline</li>
 * <li>Always looks like a link (different color and underlined)</li>
 * </ol>
 * <br>
 * <br>
 * It is possible to set what color the link is before having
 * been followed, and what color the link is after having been followed.
 * <br>
 * <br>
 * There are two UIManager properties associated with this object, 
 * JXHyperlink.clickedColor and JXHyperlink.unclickedColor.
 *
 * @author Richard Bair
 */
public class JXHyperlink extends JLabel {
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
	/**
	 * Indicates whether this hyperlink looks like a hyperlink when the mouse
	 * is NOT over it.  In other words, if the hyperlink is "hidden", it appears
	 * to be normal text unles the user hovers over the link with the mouse.  Hence,
	 * the hyperlink is hiding its true nature, like Superman/Clark Kent :-)
	 */
	private boolean hidden = true;
	/**
	 * Indicates whether this hyperlink has been clicked.
	 */
	private boolean hasBeenClicked = false;
	/**
	 * Indicates whether the mouse has entered this hyper link
	 */
	private boolean hasBeenEntered = false;
	/**
	 * ActionListeners for handling hyperlink click events
	 */
	private List<ActionListener> clickListeners = new ArrayList<ActionListener>();
    /**
     * The Action, if any, associated with this JXHyperlink. It is possible for there
     * to be more than one ActionListener associated with this JXHyperlink. It is also
     * possible for the text & icon of the JXHyperlink to be set independently of this
     * Action. However, this action, if it exists takes precedence over the Title, enabled
     * state, icon, and tooltip. It is gauranteed to be the first ActionListener fired
     * upon a click event.
     */
    private Action action;
	
	/**
	 * Create a new Hyperlink.  Pass in the text and the actionListener for
	 * hyperlink click events.
	 * @param text
	 * @param clickListener
	 */
	public JXHyperlink(String text, ActionListener clickListener) {
		this(text, JLabel.LEADING, clickListener);
	}
	
	/**
	 * Create a new Hyperlink.  Pass in the text and the actionListener for
	 * hyperlink click events.
	 * @param text
	 * @param clickListener
	 */
	public JXHyperlink(ActionListener clickListener) {
		super();
		addMouseListener(new HyperlinkMouseListener(this));
		addActionListener(clickListener);
	}
	
	/**
	 * Create a new Hyperlink.  Pass in the text, the alignment, and 
	 * the actionListener for hyperlink click events.
	 * @param text
	 * @param horizontalAlignment
	 * @param clickListener
	 */
	public JXHyperlink(String text, int horizontalAlignment, ActionListener clickListener) {
		super();
		setOpaque(false);
		setText(text);
		setHorizontalAlignment(horizontalAlignment);
		addMouseListener(new HyperlinkMouseListener(this));
		addActionListener(clickListener);
	}
	
	/**
	 * Create a new JXHyperlink based on the given action.  The click event
	 * handler is the action's handler, and so forth.
	 * @param a
	 */
	public JXHyperlink(Action a) {
		this();
        setAction(a);
	}
	
	/**
	 * Default Constructor.  Has no text, and does nothing on click events.
	 */
	public JXHyperlink() {
		this(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//does nothing
			}
		});
	}
	
	/**
	 * Method for changing the action associated with this JXHyperlink.
	 * @param action
	 */
	public void setAction(Action action) {
        if (this.action != action) {
            this.action = action;
            if (action != null) {
                setText((String)action.getValue(Action.NAME));
        		setIcon((Icon)action.getValue(Action.SMALL_ICON));
                setToolTipText((String)action.getValue(Action.SHORT_DESCRIPTION));
            }
        }
	}
	
	/**
	 * Inner utility class that provides MouseListener capabilities for this
	 * hyperlink.  Changes the color of the hyperlink when selected, the cursor
	 * for the hyperlink when the mouse is over it, and so forth.
	 * @author Richard Bair
	 */
	private static final class HyperlinkMouseListener extends MouseAdapter {
		/**
		 * the parent JXHyperlink component
		 */
		private JXHyperlink parent;
		private Cursor oldCursor = null;
		
		private HyperlinkMouseListener(JXHyperlink parent) {
			assert parent != null;
			this.parent = parent;
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent e) {
			//construct an ActionEvent and pass it to the
			//parent's clickListener
			ActionEvent ae = new ActionEvent(parent, ActionEvent.ACTION_PERFORMED, "");
            if (parent.action != null) {
                parent.action.actionPerformed(ae);
            }
            for (ActionListener clickListener : parent.clickListeners) {
    			clickListener.actionPerformed(ae);
            }
			parent.hasBeenClicked = true;
			parent.repaint();
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		public void mouseEntered(MouseEvent e) {
			if (parent.hidden) {
				//need to change the label's appearance so it looks like a hyperlink
				/*
				 * TODO really need to check and make sure this label isn't
				 * already using html and that it doesn't already have a font tag...
				 * if I want to support html text in the hyper link, that is
				 */
				parent.hasBeenEntered = true;
				parent.repaint();
			}
			//change the cursor
			oldCursor = SwingUtilities.getWindowAncestor(parent).getCursor();
			SwingUtilities.getWindowAncestor(parent).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		public void mouseExited(MouseEvent e) {
			if (parent.hidden) {
				//need to change the label's appearance so it looks like a normal label
				parent.hasBeenEntered = false;
				parent.repaint();
			}
			if (oldCursor != null) {
				SwingUtilities.getWindowAncestor(parent).setCursor(oldCursor);
			}
		}
	}

	/**
	 * @return
	 */
	public Color getClickedColor() {
		return clickedColor;
	}

	/**
	 * @return
	 */
	public boolean isHasBeenClicked() {
		return hasBeenClicked;
	}

	/**
	 * @return
	 */
	public boolean isHidden() {
		return hidden;
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
		clickedColor = color;
	}

	/**
	 * @param b
	 */
	public void setHidden(boolean b) {
		if (b) {
			hidden = true;
		} else if (!b && hidden) {
			hidden = false;
		}
	}

	/**
	 * @param color
	 */
	public void setUnclickedColor(Color color) {
		unclickedColor = color;
	}
    
    public void addActionListener(ActionListener listener) {
        if (!clickListeners.contains(listener)) {
            clickListeners.add(listener);
        }
    }
    
    public void removeActionListener(ActionListener listener) {
        clickListeners.remove(listener);
    }
    
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
        //set the foreground color based on the click state
        setForeground(hasBeenClicked ? clickedColor : unclickedColor);

        super.paintComponent(g);
        
        //add an underline to the font if necessary
        String text = getText();
        if (text != null && text.length() > 0) {
            if (hasBeenEntered || !hidden) {
                Icon ico = getIcon();
                int y = getHeight();
                int x = ico == null ? 0 : ico.getIconWidth() + 1;
                g.drawLine(x, getHeight()-1, SwingUtilities.computeStringWidth(g.getFontMetrics(), text) + x, getHeight()-1);
            }
        }
	}

}
