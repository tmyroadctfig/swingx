/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.util.UIManagerUtils;

/**
 * This widget has a title, it has a little "Scroll/Unscroll" button, and a 
 * content area. This content area can contain any components.
 * <br>
 * The following property is registered with the UIManager and may be overridden by
 * any look and feel: JXScrollUp.background.
 * <br>
 * This property will specify the color to be used for the background of the JXScrollUp.
 *
 * @author rbair
 */
public class JXScrollUp extends JXTitledPanel {
	/**
	 * The amount of time in milliseconds to wait between calls to the animation thread
	 */
	private static final int WAIT_TIME = 5;
	/**
	 * The delta in the Y direction to inc/dec the size of the scroll up by
	 */
	private static final int DELTA_Y = 10;
    /**
     * The starting alpha transparency level
     */
    private static final float ALPHA_START = 0.01f;
    /**
     * The ending alpha transparency level
     */
    private static final float ALPHA_END = 1.0f;
	/**
	 * The button that is used to collapse &amp; expand the component
	 */
	private JButton collapseButton;
    /**
     * The icon to show when the component is collapsed
     */
    private Icon collapsedIcon = new ImageIcon("/usr/local/src/swingx/swingx/src/java/org/jdesktop/swingx/downarrow.gif");//(ImageIcon)RES.get16x16Icon("nav_down_blue.png", false);
    /**
     * The icon to show when the component is expanded
     */
    private Icon expandedIcon = new ImageIcon("/usr/local/src/swingx/swingx/src/java/org/jdesktop/swingx/uparrow.gif");//(ImageIcon)RES.get16x16Icon("nav_up_blue.png", false);
    /**
     * Indicates whether the component is collapsed or expanded
     */
    private boolean collapsed = false;
    /**
     * Timer used for doing the transparency animation (fade-in)
     */
    private Timer animateTimer;
    private AnimationListener animator;
    private int currentHeight = -1;

	/**
	 * Create a new JXScrollUp.
	 */
	public JXScrollUp() {
		this("");
	}

	/**
	 * Create a new JXScrollUp with the given title.
	 * @param title
	 */
	public JXScrollUp(String title) {
		super(title);
        try {
//		UIManagerUtils.initDefault("JScrollUp.background", "primary2", UIManager.getColor("ComboBox.selectionBackground"));
		initGui();
		animator = new AnimationListener();
		animateTimer = new Timer(WAIT_TIME, animator);
        } catch (Error e) {
            System.err.println(e);
            e.printStackTrace();
        }  
	}	
    
	/**
	 * Utility method that initializes the gui
	 */
	private void initGui() {
		//draw my beautiful self.
		//the widget has a title bar, an expansion/contraction icon, and
		//a content area.  The content area can in theory contain any
		//component.

		//in reality, I'd like to have a peer that does all the drawing
		//(as in swing).
//		JPanel contentPanel = createPanelContentContainer();

		collapseButton = new JButton(collapsed ? collapsedIcon : expandedIcon);
        collapseButton.setBorderPainted(false);
        collapseButton.setMargin(new Insets(0,0,0,0));
        collapseButton.setFocusable(false);
        collapseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
                setCollapsed(!collapsed);
			}
        });
        collapseButton.setOpaque(false);
		addRightDecoration(collapseButton);
//		setContentContainer(contentPanel);
		
//		this.setBorder(BorderFactory.createLineBorder(UIManager.getColor("JScrollUp.background")));
	}

    public boolean isCollapsed() {
        return collapsed;
    }
    
	/**
     * If the component is collapsed and <code>val</code> is false, then this call
	 * scrolls down the JScrollUp, such that the entire JXScrollUp will be visible.
	 * This also causes the content container to be faded in. However, if the
     * component is expanded and <code>val</code> is true, then this call
	 * scrolls up the JXScrollUp, such that only the title bar will be left
	 * visible. Also, this will cause the content container to be faded out.
	 */
    public void setCollapsed(boolean val) {
        if (collapsed != val) {
            collapsed = val;
            collapseButton.setIcon(collapsed ? collapsedIcon : expandedIcon);
            if (collapsed) {
                animator.reinit(getContentContainer().getHeight(), 0);
                animateTimer.start();
            } else {
                animator.reinit(getContentContainer().getHeight(), getContentContainer().getPreferredSize().height);
                animateTimer.start();
            }
            repaint();
            firePropertyChange("collapsed", !collapsed, collapsed);
        }
    }
    
	/**
	 * Factory method that will create a JPanel content creator designed to "look right" in a JXScrollUp.
	 * That is, the colors and border will be set correctly.
	 * @return
	 */
//	public static JPanel createPanelContentContainer() {
//		JPanel contentPanel = new ContentContainer();
//		contentPanel.setBorder(BorderFactory.createEmptyBorder());
//		contentPanel.setBackground(UIManager.getColor("JScrollUp.background"));
//		return contentPanel;
//	}

	/* (non-Javadoc)
	 * @see com.jgui.swing.JTitledPanel#setContentContainer(java.awt.Container)
	 */
//	public void setContentContainer(Container contentPanel) {
//		if (contentPanel instanceof ContentContainer) {
//			((ContentContainer)contentPanel).sup = this;
//		}
//		//need to readjust the RoundButton so it will work with the new content panel
//		super.setContentContainer(contentPanel);
//		chevron.cp = contentPanel;
//	}

	/* (non-Javadoc)
	 * @see java.awt.Component#getMinimumSize()
	 */
	public Dimension getMinimumSize() {
        //TODO There is an error in this calculation if there is padding
        //added to the component in GridBagLayout, probably others as well!
        
		//calculate the minimum size that this scrollup can be and still display all of its data
		Dimension dim = new Dimension(super.getContentContainer().getMinimumSize());
//		Dimension dim2 = super.getTopPanel().getMinimumSize();
        Dimension dim2 = new Dimension(dim.width, 20);
		dim.height += dim2.height;
		dim.width = dim.width > dim2.width ? dim.width : dim2.width;
		if (currentHeight != -1) {
			dim.height = currentHeight;
		}
		return dim;
	}
	/* (non-Javadoc)
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
	
	/**
	 * 
	 * This class actual provides the animation support for scrolling up/down this component.
	 * This listener is called whenever the animateTimer fires off. It fires off in response
	 * to scroll up/down requests. This listener is responsible for modifying the size of the
	 * content container and causing it to be repainted.
	 * @author Richard Bair
	 */
	private final class AnimationListener implements ActionListener {
		/**
		 * Mutex used to ensure that the startHeight/finalHeight are not changed
		 * during a repaint operation.
		 */
		private final Object ANIMATION_MUTEX = "Animation Synchronization Mutex";
		/**
		 * This is the starting height when animating. If > finalHeight, then
		 * the animation is going to be to scroll up the component. If it is <
		 * then finalHeight, then the animation will scroll down the component.
		 */
		private int startHeight = 0;
		/**
		 * This is the final height that the content container is going to be
		 * when scrolling is finished.
		 */
		private int finalHeight = 0;
	    /**
	     * The current alpha setting used during "animation" (fade-in/fade-out)
	     */
	    private float animateAlpha = ALPHA_START;

        public void actionPerformed(ActionEvent e) {
        	/*
        	 * Pre-1) If startHeight == finalHeight, then we're done so stop the timer
        	 * 1) Calculate whether we're contracting or expanding.
        	 * 2) Calculate the delta (which is either positive or negative, depending on the results of (1))
        	 * 3) Calculate the alpha value
        	 * 4) Resize the ContentContainer
        	 * 5) Revalidate/Repaint the content container
        	 */
        	synchronized (ANIMATION_MUTEX) {
            	if (startHeight == finalHeight) {
            		animateTimer.stop();
            		animateAlpha = 1.0f;
            		return;
            	}
            	
            	final boolean contracting = startHeight > finalHeight;
            	final int delta_y = contracting ? -1 * DELTA_Y : DELTA_Y;
                final Container container = getContentContainer();
            	int newHeight = container.getHeight();
            	newHeight += delta_y;
				if (contracting) {
					newHeight = newHeight < finalHeight ? finalHeight : newHeight;
				} else {
					newHeight = newHeight > finalHeight ? finalHeight : newHeight;
				}
            	animateAlpha = (float)newHeight/(float)container.getPreferredSize().getHeight();
            	
            	Rectangle bounds = container.getBounds();
            	int oldHeight = bounds.height;
            	bounds.height = newHeight;
            	container.setBounds(bounds);
            	bounds = getBounds();
            	bounds.height = (bounds.height - oldHeight) + newHeight;
            	currentHeight = bounds.height;
            	setBounds(bounds);
            	startHeight = newHeight;
                if (container instanceof JXPanel) {
                    ((JXPanel)container).setAlpha(animateAlpha);
                }
        		getParent().validate();
        	}
        }

        /**
         * Reinitializes the timer for scrolling up/down the component. This method is properly
         * synchronized, so you may make this call regardless of whether the timer is currently
         * executing or not.
         * @param startHeight
         * @param stopHeight
         */
        public void reinit(int startHeight, int stopHeight) {
        	synchronized (ANIMATION_MUTEX) {
        		this.startHeight = startHeight;
        		this.finalHeight = stopHeight;
        		animateAlpha = startHeight < finalHeight ? ALPHA_START : ALPHA_END;
                System.out.println("[startHeight=" + startHeight + ", finalHeight=" + stopHeight + ", animateAlpha=" + animateAlpha + "]");
        	}
        }
	}
//	
//	private static final class ContentContainer extends JPanel {
//		private JXScrollUp sup;
//		
//		public ContentContainer() {
//			super();
//		}
//		
//		/* (non-Javadoc)
//		 * @see java.awt.Component#paint(java.awt.Graphics)
//		 */
//		public void paint(Graphics g) {
//			if (sup != null && sup.animateTimer.isRunning()) {
//				Graphics2D g2d = (Graphics2D)g;
//				Composite oldComp = g2d.getComposite();
//		        Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sup.animator.animateAlpha);
//	            g2d.setComposite(alphaComp);
//	            super.paint(g2d);
//	            g2d.setComposite(oldComp);
//			} else {
//				super.paint(g);
//			}
//		}
//	}
}
