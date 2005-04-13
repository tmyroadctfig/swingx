/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package com.jgui.swing;

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
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
	 * Originally a chevron, this button is used to scroll and unscroll the JXScrollUp.
	 */
	private RoundButton chevron;
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
//		UIManagerUtils.initDefault("JScrollUp.background", "primary2", UIManager.getColor("ComboBox.selectionBackground"));
		initGui();
		animator = new AnimationListener();
		animateTimer = new Timer(WAIT_TIME, animator);
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
		JPanel contentPanel = createPanelContentContainer();

		chevron = new RoundButton(this);
		addRightDecoration(chevron);
		setContentContainer(contentPanel);
		
		this.setBorder(BorderFactory.createLineBorder(UIManager.getColor("JScrollUp.background")));
	}

	/**
	 * Factory method that will create a JPanel content creator designed to "look right" in a JXScrollUp.
	 * That is, the colors and border will be set correctly.
	 * @return
	 */
	public static JPanel createPanelContentContainer() {
		JPanel contentPanel = new ContentContainer();
		contentPanel.setBorder(BorderFactory.createEmptyBorder());
		contentPanel.setBackground(UIManager.getColor("JScrollUp.background"));
		return contentPanel;
	}

	/* (non-Javadoc)
	 * @see com.jgui.swing.JTitledPanel#setContentContainer(java.awt.Container)
	 */
	public void setContentContainer(Container contentPanel) {
		if (contentPanel instanceof ContentContainer) {
			((ContentContainer)contentPanel).sup = this;
		}
		//need to readjust the RoundButton so it will work with the new content panel
		super.setContentContainer(contentPanel);
		chevron.cp = contentPanel;
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#getMinimumSize()
	 */
	public Dimension getMinimumSize() {
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
	 * Scrolls up the JXScrollUp, such that only the title bar will be left
	 * visible. Also, this will cause the content container to be faded out.
	 */
	public void scrollUp() {
		animator.reinit(getContentContainer().getHeight(), 0);
		animateTimer.start();
	}
	
	/**
	 * Scrolls down the JScrollUP, such that the entire JXScrollUp will be visible.
	 * This also causes the content container to be faded in.
	 */
	public void scrollDown() {
		animator.reinit(getContentContainer().getHeight(), getContentContainer().getPreferredSize().height);
		animateTimer.start();
	}

	/**
	 * RoundButton is always the same height and width.  It is the height and
	 * width of the image. <future>A single background thread is shared among
	 * all RoundButtons.<br>
	 * This background thread will manage calling the paint method etc on the
	 * content panes.</future>
	 * @author Richard Bair
	 * date: Jun 27, 2003
	 */
	private static final class RoundButton extends JButton {
		private static ImageIcon pressedIcon = new ImageIcon(RoundButton.class.getResource("table/resources/downarrow.gif"));//(ImageIcon)RES.get16x16Icon("nav_down_blue.png", false);
		private static ImageIcon unpressedIcon = new ImageIcon(RoundButton.class.getResource("table/resources/uparrow.gif"));//(ImageIcon)RES.get16x16Icon("nav_up_blue.png", false);
		private static Insets EMPTY_INSETS = new Insets(0,0,0,0);
		private boolean pressed = false;
		private Container cp;
		private JXScrollUp scrollUp;
		
		/**
		 * Constructs the RoundButton. It will not be opaque, and will respond to click events
		 * by scrolling up/down the given JXScrollUp.
		 */
		public RoundButton(JXScrollUp scrollUp) {
			super(unpressedIcon);
			cp = scrollUp.getContentContainer();
			this.scrollUp = scrollUp;
			super.setBorderPainted(false);
			super.setMargin(EMPTY_INSETS);
			super.setFocusable(false);
			super.addActionListener(new ClickEvent());
			super.setOpaque(false);
		}

		/**
		 * Specialized method to handling scrolling up and down of the JXScrollUp content pane.
		 * Uses the Animation thread for groovy animation. If the content pane is also a JGlassBox,
		 * then the content pane will fade in/out as it is scrolled down/up.
		 * @author John Bair
		 */
		private final class ClickEvent implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
				pressed = !pressed;
				if(pressed) {
					setIcon(pressedIcon);
					scrollUp.scrollUp();
				} else {
					setIcon(unpressedIcon);
					scrollUp.scrollDown();
				}
				repaint();
			}
		}
				
		/**
		 * @return
		 */
		public boolean isPressed() {
			return pressed;
		}
		
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
            	int newHeight = getContentContainer().getHeight();
            	newHeight += delta_y;
				if (contracting) {
					newHeight = newHeight < finalHeight ? finalHeight : newHeight;
				} else {
					newHeight = newHeight > finalHeight ? finalHeight : newHeight;
				}
            	animateAlpha = (float)newHeight/(float)getContentContainer().getPreferredSize().getHeight();
            	
            	Rectangle bounds = getContentContainer().getBounds();
            	int oldHeight = bounds.height;
            	bounds.height = newHeight;
            	getContentContainer().setBounds(bounds);
            	bounds = getBounds();
            	bounds.height = (bounds.height - oldHeight) + newHeight;
            	currentHeight = bounds.height;
            	setBounds(bounds);
            	startHeight = newHeight;
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
        	}
        }
	}
	
	private static final class ContentContainer extends JPanel {
		private JXScrollUp sup;
		
		public ContentContainer() {
			super();
		}
		
		/* (non-Javadoc)
		 * @see java.awt.Component#paint(java.awt.Graphics)
		 */
		public void paint(Graphics g) {
			if (sup != null && sup.animateTimer.isRunning()) {
				Graphics2D g2d = (Graphics2D)g;
				Composite oldComp = g2d.getComposite();
		        Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sup.animator.animateAlpha);
	            g2d.setComposite(alphaComp);
	            super.paint(g2d);
	            g2d.setComposite(oldComp);
			} else {
				super.paint(g);
			}
		}
	}
}
