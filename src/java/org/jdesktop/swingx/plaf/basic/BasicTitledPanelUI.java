/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.plaf.TitledPanelUI;


/**
 * All TitledPanels contain a title section and a content section. The default
 * implementation for the title section relies on a Gradient background. All
 * title sections can have components embedded to the &quot;left&quot; or
 * &quot;right&quot; of the Title.
 *
 * @author rbair
 */
public abstract class BasicTitledPanelUI extends TitledPanelUI {
	/**
	 * JLabel used for the title in the Title section of the JTitledPanel.
	 */
	private JLabel caption;
	/**
	 * The Title section panel.
	 */
	private JGradientPanel topPanel;
    /**
     * Listens to changes in the title of the JXTitledPanel component
     */
    private PropertyChangeListener titleChangeListener;
    /**
     * The JXTitledPanel peered with this UI
     */
    protected JXTitledPanel titledPanel;
    
    /** Creates a new instance of BasicTitledPanelUI */
    public BasicTitledPanelUI() {
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
    public void installUI(JComponent c) {
        assert c instanceof JXTitledPanel;
        JXTitledPanel panel = (JXTitledPanel)c;
        
        installProperty(panel, "titleForeground", UIManager.getColor("JXTitledPanel.title.foreground"));
        installProperty(panel, "titleDarkBackground", UIManager.getColor("JXTitledPanel.title.darkBackground"));
        installProperty(panel, "titleLightBackground", UIManager.getColor("JXTitledPanel.title.lightBackground"));
        installProperty(panel, "titleFont", UIManager.getFont("JXTitledPanel.title.font"));
        
        panel.setLayout(new BorderLayout());
        JXPanel contentPanel = new JXPanel();
		contentPanel.setBorder(BorderFactory.createEmptyBorder());
		panel.add(contentPanel, BorderLayout.CENTER);
		caption = new JLabel(panel.getTitle());
		caption.setFont(panel.getTitleFont());
		topPanel = createTopPanel();
		topPanel.setBorder(BorderFactory.createEmptyBorder());
		topPanel.setLayout(new GridBagLayout());
		caption.setForeground(panel.getTitleForeground());
		topPanel.add(caption, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4, 12, 4, 12), 0, 0));
		panel.add(topPanel, BorderLayout.NORTH);
		panel.setBorder(BorderFactory.createRaisedBevelBorder());
		panel.setOpaque(false);
        titledPanel = panel;
        
        installListeners();
        
    }

    protected void installListeners() {
        titleChangeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("title")) {
                    caption.setText((String)evt.getNewValue());
                } else if (evt.getPropertyName().equals("titleForeground")) {
                    caption.setForeground((Color)evt.getNewValue());
                } else if (evt.getPropertyName().equals("titleFont")) {
                    caption.setFont((Font)evt.getNewValue());
                } else if ("titleDarkBackground".equals(evt.getPropertyName())) {
                    topPanel.revalidateGradient();
               
            } else if ("titleLightBackground".equals(evt.getPropertyName())) {
                topPanel.revalidateGradient();
            }
            }
        };
//        titledPanel.addPropertyChangeListener("title", titleChangeListener);
//        titledPanel.addPropertyChangeListener("titleForeground", titleChangeListener);
//        titledPanel.addPropertyChangeListener("titleFont", titleChangeListener);
        titledPanel.addPropertyChangeListener(titleChangeListener);
    }

    protected void uninstallListeners(JXTitledPanel panel) {
        titledPanel.removePropertyChangeListener(titleChangeListener);
        //TODO
//        panel.removePropertyChangeListener("title", titleChangeListener);
//        panel.removePropertyChangeListener("titleForeground", titleChangeListener);
//        panel.removePropertyChangeListener("titleFont", titleChangeListener);
    }

    protected void installProperty(JComponent c, String propName, Object value) {
        try {
            BeanInfo bi = Introspector.getBeanInfo(c.getClass());
            for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
                if (pd.getName().equals(propName)) {
                    Method m = pd.getReadMethod();
                    Object oldVal = m.invoke(c);
                    if (oldVal == null || oldVal instanceof UIResource) {
                        m = pd.getWriteMethod();
                        m.invoke(c, value);
                    }
                }
            }
        } catch (Exception e) {
            //should log this...TODO
            e.printStackTrace();
        }
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
    public void uninstallUI(JComponent c) {
        assert c instanceof JXTitledPanel;
        JXTitledPanel panel = (JXTitledPanel)c;
        uninstallListeners(panel);
    }


    /**
     * Paints the specified component appropriate for the look and feel.
     * This method is invoked from the <code>ComponentUI.update</code> method when 
     * the specified component is being painted.  Subclasses should override 
     * this method and use the specified <code>Graphics</code> object to 
     * render the content of the component.
     *
     * @param g the <code>Graphics</code> context in which to paint
     * @param c the component being painted;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components
     *
     * @see #update
     */ 
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
    }

	/**
	 * Adds the given JComponent as a decoration on the right of the title
	 * @param decoration
	 */
	public void addRightDecoration(JComponent decoration) {
		topPanel.add(decoration, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	}

	/**
	 * Adds the given JComponent as a decoration on the left of the title
	 * @param decoration
	 */
	public void addLeftDecoration(JComponent decoration) {
		topPanel.add(decoration, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	}

    /**
     * @return the Container acting as the title bar for this component
     */
    public Container getTitleBar() {
        return topPanel;
    }

    protected JGradientPanel createTopPanel() {
        return new JGradientPanel();
    }
    
    /**
	 * A special inner class who's background is painted as a gradient.  This is used
	 * as the Title section of the JTitledPanel
	 * @author Richard Bair
	 * date: Jan 13, 2004
	 */
	protected class JGradientPanel extends JXPanel {
		private GradientPaint gp;
		private double oldWidth = -1;
		private double oldHeight = -1;
		private ImageIcon helper = new ImageIcon();
		public JGradientPanel() {
		}

        public void revalidateGradient() {
            gp = null;
            repaint();
        }
		//override the background color to provide for a gradient
		
		/* (non-Javadoc)
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 * 
		 * There is some special optimization code in here that is kind of...er..shall we say, tricky.
		 * First off, the gradient panels are taking forever to draw. Therefore, I have resorted to caching the
		 * gradient'ed paint job so that I don't have to repaint it all the time.
		 */
		protected void paintComponent(Graphics g) {
			//draw the gradient background
			if (gp == null || oldWidth != getWidth() || oldHeight != getHeight()) {
				gp = createGradientPaint();
				Image savedImg = createImage(getWidth(), getHeight());
				Graphics2D imgg = (Graphics2D)savedImg.getGraphics();
				imgg.setPaint(gp);
				imgg.fillRect(0, 0, getWidth(), getHeight());
				oldWidth = getWidth();
				oldHeight = getHeight();
				helper.setImage(savedImg);
			}
			//draw the image
			g.drawImage(helper.getImage(), 0, 0, getWidth(), getHeight(), helper.getImageObserver());
		}
        
        protected GradientPaint createGradientPaint() {
            return new GradientPaint(0, 
                                     0, 
                                     titledPanel.getTitleDarkBackground(), 
                                     getWidth(), 
                                     0, 
                                     titledPanel.getTitleLightBackground());
        }
	}
}
