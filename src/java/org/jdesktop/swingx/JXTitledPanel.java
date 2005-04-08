/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import org.jdesktop.swingx.util.UIManagerUtils;


/**
 * A special type of Panel that has a Title section and a
 * Content section.<br>
 * The following 4 properties can be set with the UIManager to
 * change the look and feel of the JTitledPanel:
 * <ul>
 * 	<li>JTitledPanel.title.foreground</li>
 * 	<li>JTitledPanel.title.background</li>
 * 	<li>JTitledPanel.title.font</li>
 * </ul>
 * @author Richard Bair
 * @author Nicola Ken Barozzi
 */
public class JXTitledPanel extends JPanel{
	private static final ColorUIResource BLACK = new ColorUIResource(Color.BLACK);
	private static final ColorUIResource WHITE = new ColorUIResource(Color.WHITE);
	/**
	 * JLabel used for the title in the Title section of the JTitledPanel.
	 */
	protected JLabel caption;
	/**
	 * The text to use for the title
	 */
	private String title = "";
	/**
	 * The ContentPanel.  Whatever this container is will be displayed in the
	 * Content section
	 */
	private Container contentPanel;
	/**
	 * The Title section panel.
	 */
	protected JPanel topPanel;

	/**
	 * Create a new JTitledPanel with an empty string for the title.
	 */
	public JXTitledPanel() {
		this("");
	}

	/**
	 * Create a new JTitledPanel with the given title as the title for the panel.
	 * @param title
	 */
	public JXTitledPanel(String title) {
		this.title = (title == null ? "" : title);
		
		//set the UIManager colors for this component to some defaults if they are not already set
		UIManagerUtils.initDefault("JTitledPanel.title.foreground", "black", BLACK);
		Color c = UIManager.getColor("ComboBox.selectionBackground");
		//TOTAL HACK. TODO What should I do about default colors?
		if (c == null) {
		    c = UIManager.getColor("ProgressBar.background");
		    if (c == null) {
		        c = Color.decode("0x80b2ea");
		    }
		}
		UIManagerUtils.initDefault("JTitledPanel.title.darkBackground", "primary2", c);
		UIManagerUtils.initDefault("JTitledPanel.title.lightBackground", "white", WHITE);
		UIManagerUtils.initDefault("JTitledPanel.title.font", UIManager.getFont("Button.font"));
		
		initGui();
	}

	/**
	 * Create a new JTitledPanel with the given String as the title, and the
	 * given Container as the content panel.
	 * @param title
	 * @param content
	 */
	public JXTitledPanel(String title, Container content) {
		this(title);
		setContentContainer(content);
	}

	/**
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param string
	 */
	public void setTitle(String title) {
		String oldTitle = this.title;
		this.title = (title == null ? "" : title);
		caption.setText(title);
		PropertyChangeEvent event = new PropertyChangeEvent(this, "title", oldTitle, title);
		PropertyChangeListener[] listeners = this.getPropertyChangeListeners("title");
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].propertyChange(event);
		}
	}
	
	/**
	 * Utility method for initializing the gui
	 */
	private void initGui() {
		//draw my beautiful self.
		//the widget has a title bar, and a content area.  
		//The content area can in theory contain any component.

		//in reality, I'd like to have a peer that does all the drawing
		//(as in swing).
		Color titleColor = UIManager.getColor("JTitledPanel.title.foreground");
		
		
		this.setLayout(new BorderLayout());
		
		contentPanel = new JPanel();
		((JPanel)contentPanel).setBorder(BorderFactory.createEmptyBorder());
		this.add(contentPanel, BorderLayout.CENTER);

		caption = new JLabel(title);
		caption.setFont(UIManager.getFont("JTitledPanel.title.font"));
		topPanel = new JGradientPanel();
		topPanel.setBorder(BorderFactory.createEmptyBorder());
		topPanel.setLayout(new GridBagLayout());
		caption.setForeground(titleColor);
		topPanel.add(caption, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 12, 2, 4), 0, 0));
		this.add(topPanel, BorderLayout.NORTH);
		
		this.setBorder(BorderFactory.createRaisedBevelBorder());
		
		setOpaque(false);
	}

	/**
	 * A special inner class who's background is painted as a gradient.  This is used
	 * as the Title section of the JTitledPanel
	 * @author Richard Bair
	 * date: Jan 13, 2004
	 */
	private static final class JGradientPanel extends JPanel {
		private GradientPaint gp;
		private double oldWidth = -1;
		private double oldHeight = -1;
		private ImageIcon helper = new ImageIcon();
		public JGradientPanel() {
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
			if (oldWidth != getWidth() || oldHeight != getHeight()) {
				gp = new GradientPaint(0, 0, UIManager.getColor("JTitledPanel.title.darkBackground"), getWidth(), 0, UIManager.getColor("JTitledPanel.title.lightBackground"));
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

	}
	
	/**
	 * @return
	 */
	public Container getContentContainer() {
		return contentPanel;
	}
	
	public void setContentContainer(Container contentPanel) {
		remove(this.contentPanel);
		add(contentPanel, BorderLayout.CENTER);
		this.contentPanel = contentPanel;
	}
	
	/**
	 * Adds the given JComponent as a decoration on the right of the title
	 * @param decoration
	 */
	public void addRightDecoration(JComponent decoration) {
		topPanel.add(decoration, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	}

	/**
	 * @return Returns the topPanel.
	 */
	protected JPanel getTopPanel() {
		return topPanel;
	}
	
	/**
	 * Adds the given JComponent as a decoration on the left of the title
	 * @param decoration
	 */
	public void addLeftDecoration(JComponent decoration) {
		topPanel.add(decoration, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	}

}