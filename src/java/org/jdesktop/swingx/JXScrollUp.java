/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.LookAndFeel;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import org.jdesktop.swingx.plaf.TitledPanelUI;
import org.jdesktop.swingx.plaf.metal.MetalScrollUpUI;


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
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ScrollUpUI";
    private static final PropertyChangeListener LAF_LISTENER;
    
    /**
     * Initialization that would ideally be moved into various look and feel
     * classes.
     */
    static {
        loadDefaults();
        LAF_LISTENER = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("lookAndFeel")) {
                    loadDefaults();
                }
            }
        };
        UIManager.addPropertyChangeListener(LAF_LISTENER);
    }
    
    private static void loadDefaults() {
        LookAndFeel laf = UIManager.getLookAndFeel();
        if (laf.getID().equals("GTK")) {
            UIDefaults defaults = UIManager.getDefaults();
            defaults.put(uiClassID, "org.jdesktop.swingx.plaf.metal.MetalScrollUpUI");
    		defaults.put("JXScrollUp.background", UIManager.getColor("ComboBox.selectionBackground"));
        } else if (laf.getID().equals("Motif")) {
            UIDefaults defaults = UIManager.getDefaults();
            defaults.put(uiClassID, "org.jdesktop.swingx.plaf.metal.MetalScrollUpUI");
    		defaults.put("JXScrollUp.background", UIManager.getColor("ComboBox.selectionBackground"));
        } else if (laf.getID().equals("Mac")) {
            UIDefaults defaults = UIManager.getDefaults();
            defaults.put(uiClassID, "org.jdesktop.swingx.plaf.metal.MetalScrollUpUI");
    		defaults.put("JXScrollUp.background", UIManager.getColor("ComboBox.selectionBackground"));
        } else if (laf.getID().equals("Windows")) {
            UIDefaults defaults = UIManager.getDefaults();
            defaults.put(uiClassID, "org.jdesktop.swingx.plaf.windows.WindowsScrollUpUI");
    		defaults.put("JXScrollUp.background", UIManager.getColor("ComboBox.selectionBackground"));
        } else if (laf.getID().equals("Plastic")) {
            UIDefaults defaults = UIManager.getDefaults();
            defaults.put(uiClassID, "org.jdesktop.swingx.plaf.metal.MetalScrollUpUI");
    		defaults.put("JXScrollUp.background", UIManager.getColor("ComboBox.selectionBackground"));
        } else if (laf.getID().equals("Metal")) {
            UIDefaults defaults = UIManager.getDefaults();
            defaults.put(uiClassID, "org.jdesktop.swingx.plaf.metal.MetalScrollUpUI");
    		defaults.put("JXScrollUp.background", UIManager.getColor("ComboBox.selectionBackground"));
        } else {
            UIDefaults defaults = UIManager.getDefaults();
            defaults.put(uiClassID, "org.jdesktop.swingx.plaf.metal.MetalScrollUpUI");
    		defaults.put("JXScrollUp.background", UIManager.getColor("ComboBox.selectionBackground"));
        }
    }

    /**
	 * The amount of time in milliseconds to wait between calls to the animation thread
	 */
	private static final int WAIT_TIME = 30;
	/**
	 * The delta in the Y direction to inc/dec the size of the scroll up by
	 */
	private static final int DELTA_Y = 50;
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
    private Icon collapsedIcon = new ImageIcon(getClass().getResource("/org/jdesktop/swingx/table/resources/downarrow.gif"));
    /**
     * The icon to show when the component is expanded
     */
    private Icon expandedIcon = new ImageIcon(getClass().getResource("/org/jdesktop/swingx/table/resources/uparrow.gif"));
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
    private WrapperContainer wrapper;

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
            initGui();
            animator = new AnimationListener();
            animateTimer = new Timer(WAIT_TIME, animator);
        } catch (Error e) {
            System.err.println(e);
            e.printStackTrace();
        }  
	}	
    
    /**
     * Resets the UI property with a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        //this check was added because apparently loadDefaults is not called
        //in netbeans?? Maybe I just needed to close & restart netbeans???
        Object ui = UIManager.getUI(this);
        if (!(ui instanceof TitledPanelUI)) {
            loadDefaults();
//            ui = UIManager.getUI(this);
            ui = MetalScrollUpUI.createUI(this);
        }
        setUI((TitledPanelUI)ui);
    }

    /**
     * Returns a string that specifies the name of the L&F class
     * that renders this component.
     *
     * @return "TitledPanelUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     * @beaninfo
     *        expert: true
     *   description: A string that specifies the name of the L&F class.
     */
    public String getUIClassID() {
        return uiClassID;
    }

	/**
	 * Utility method that initializes the gui
	 */
	private void initGui() {
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
        //wrap the content container in the wrapper
        wrapper = new WrapperContainer(super.getContentContainer());
        super.setContentContainer(wrapper);
        //do the border
		this.setBorder(BorderFactory.createLineBorder(UIManager.getColor("JXScrollUp.background")));
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
                animator.reinit(wrapper.getHeight(), 0);
                animateTimer.start();
            } else {
                animator.reinit(wrapper.getHeight(), getContentContainer().getPreferredSize().height);
                animateTimer.start();
            }
            repaint();
            firePropertyChange("collapsed", !collapsed, collapsed);
        }
    }
    
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
    
	/* (non-Javadoc)
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
        /*
         * The preferred size is calculated based on the current position of
         * the component in its animation sequence. If the Component is expanded,
         * then the preferred size will be the preferred size of the top component
         * plus the preferred size of the embedded content container.
         *
         * <p>However, if the scroll up is in any state of animation, the height
         * component of the preferred size will be the current height of the
         * component (as contained in the currentHeight variable)
         */
		Dimension dim = new Dimension(getContentContainer().getPreferredSize());
		Dimension dim2 = getUI().getTitleBar().getPreferredSize();
		dim.width = dim.width > dim2.width ? dim.width : dim2.width;
		if (currentHeight != -1) {
			dim.height = currentHeight;
		} else {
    		dim.height += dim2.height;
        }
//        System.out.println("PreferredSize: " + dim);
		return dim;
	}

    public void setContentContainer(Container contentPanel) {
        wrapper = new WrapperContainer(contentPanel);
        super.setContentContainer(wrapper);
    }

    public Container getContentContainer() {
        Container c = super.getContentContainer();
        if (c == wrapper) {
            return wrapper.c;
        } else {
            return c;
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
                    wrapper.showContent();
            	}
            	
            	final boolean contracting = startHeight > finalHeight;
            	final int delta_y = contracting ? -1 * DELTA_Y : DELTA_Y;
            	int newHeight = wrapper.getHeight() + delta_y;
				if (contracting) {
					newHeight = newHeight < finalHeight ? finalHeight : newHeight;
				} else {
					newHeight = newHeight > finalHeight ? finalHeight : newHeight;
				}
            	animateAlpha = (float)newHeight/(float)wrapper.c.getPreferredSize().height;
            	
            	Rectangle bounds = wrapper.getBounds();
            	int oldHeight = bounds.height;
            	bounds.height = newHeight;
            	wrapper.setBounds(bounds);
            	bounds = getBounds();
            	bounds.height = (bounds.height - oldHeight) + newHeight;
            	currentHeight = bounds.height;
            	setBounds(bounds);
            	bounds = getBounds();
            	startHeight = newHeight;
                wrapper.setAlpha(animateAlpha);
                Container parent = getParent();
        		parent.invalidate();
                parent.doLayout();
                parent.repaint();
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
//                System.out.println("[startHeight=" + startHeight + ", finalHeight=" + stopHeight + ", animateAlpha=" + animateAlpha + "]");
                wrapper.showImage();
        	}
        }
	}
    
    private final class WrapperContainer extends JXPanel {
        private BufferedImage img;
        private Container c;
        
        public WrapperContainer(Container c) {
            super(new BorderLayout());
            this.c = c;
            add(c, BorderLayout.CENTER);
        }
        
        public void showImage() {
            //render c into the img
            if (getHeight() >= c.getPreferredSize().height || img == null) {
                img = getGraphicsConfiguration().createCompatibleImage(c.getWidth(), c.getPreferredSize().height);
                c.paint(img.getGraphics());
            }
            c.setVisible(false);
        }
        
        public void showContent() {
            c.setVisible(true);
        }
        
        public void paintComponent(Graphics g) {
            if (c.isVisible()) {
                super.paintComponent(g);
            } else {
                //draw the image with y being height - imageHeight
                g.drawImage(img, 0, getHeight() - img.getHeight(), null);
            }
        }
    }
}
