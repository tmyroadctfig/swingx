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
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jdesktop.swingx.plaf.JXHyperlinkAddon;
import org.jdesktop.swingx.plaf.JXTitledPanelAddon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.plaf.TitledPanelUI;
import org.jdesktop.swingx.plaf.metal.MetalTitledPanelUI;

/**
 * A special type of Panel that has a Title section and a
 * Content section.<br>
 * The following 3 properties can be set with the UIManager to
 * change the look and feel of the JXTitledPanel:
 * <ul>
 * 	<li>JXTitledPanel.title.foreground</li>
 * 	<li>JXTitledPanel.title.background</li>
 * 	<li>JXTitledPanel.title.font</li>
 * </ul>
 * @author Richard Bair
 * @author Nicola Ken Barozzi
 */
public class JXTitledPanel extends JXPanel {
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    static public final String uiClassID = "TitledPanelUI";
//    private static final PropertyChangeListener LAF_LISTENER;
    
    /**
     * Initialization that would ideally be moved into various look and feel
     * classes.
     */
    static {
        LookAndFeelAddons.contribute(new JXTitledPanelAddon());
//        loadDefaults();
//        LAF_LISTENER = new PropertyChangeListener() {
//            public void propertyChange(PropertyChangeEvent evt) {
//                if (evt.getPropertyName().equals("lookAndFeel")) {
//                    loadDefaults();
//                }
//            }
//        };
//        UIManager.addPropertyChangeListener(LAF_LISTENER);
    }
    
//    private static void loadDefaults() {
//        LookAndFeel laf = UIManager.getLookAndFeel();
//        if (laf.getID().equals("GTK")) {
//            UIDefaults defaults = UIManager.getDefaults();
//            defaults.put(uiClassID, "org.jdesktop.swingx.plaf.metal.MetalTitledPanelUI");
//            //adds some keys to the UIDefaults for the JXTitledPanel component
//            defaults.put("JXTitledPanel.title.foreground", UIManager.getColor("light"));
//            defaults.put("JXTitledPanel.title.darkBackground", UIManager.getColor("dark"));
//            defaults.put("JXTitledPanel.title.lightBackground", UIManager.getColor("mid"));
//            defaults.put("JXTitledPanel.title.font", UIManager.getFont("Button.font"));
//        } else if (laf.getID().equals("Motif")) {
//            UIDefaults defaults = UIManager.getDefaults();
//            defaults.put(uiClassID, "org.jdesktop.swingx.plaf.metal.MetalTitledPanelUI");
//            //adds some keys to the UIDefaults for the JXTitledPanel component
//            defaults.put("JXTitledPanel.title.foreground", UIManager.getColor("activeCaptionText"));
//            defaults.put("JXTitledPanel.title.lightBackground", UIManager.getColor("inactiveCaption"));
//            defaults.put("JXTitledPanel.title.darkBackground", UIManager.getColor("activeCaption"));
//            defaults.put("JXTitledPanel.title.font", UIManager.getFont("Button.font"));
//        } else if (laf.getID().equals("Mac")) {
//            UIDefaults defaults = UIManager.getDefaults();
//            defaults.put(uiClassID, "org.jdesktop.swingx.plaf.metal.MetalTitledPanelUI");
//            //adds some keys to the UIDefaults for the JXTitledPanel component
//            defaults.put("JXTitledPanel.title.foreground", new ColorUIResource(0, 0, 0));
//            defaults.put("JXTitledPanel.title.darkBackground", new ColorUIResource(140, 140, 140));
//            defaults.put("JXTitledPanel.title.lightBackground", new ColorUIResource(240, 240, 240));
//            defaults.put("JXTitledPanel.title.font", UIManager.getFont("Button.font"));
//        } else if (laf.getID().equals("Windows")) {
//            UIDefaults defaults = UIManager.getDefaults();
//            defaults.put(uiClassID, "org.jdesktop.swingx.plaf.metal.MetalTitledPanelUI");
//            //adds some keys to the UIDefaults for the JXTitledPanel component
//            defaults.put("JXTitledPanel.title.foreground", new ColorUIResource(33, 93, 198));
//            defaults.put("JXTitledPanel.title.darkBackground", new ColorUIResource(255, 255, 255));
//            defaults.put("JXTitledPanel.title.lightBackground", new ColorUIResource(198, 211, 247));
//            defaults.put("JXTitledPanel.title.font", UIManager.getFont("Button.font"));
//        } else if (laf.getID().equals("Plastic")) {
//            UIDefaults defaults = UIManager.getDefaults();
//            defaults.put(uiClassID, "org.jdesktop.swingx.plaf.metal.MetalTitledPanelUI");
//            //adds some keys to the UIDefaults for the JXTitledPanel component
//            defaults.put("JXTitledPanel.title.foreground", new ColorUIResource(Color.WHITE));
//            defaults.put("JXTitledPanel.title.darkBackground", new ColorUIResource(Color.GRAY));
//            defaults.put("JXTitledPanel.title.lightBackground", new ColorUIResource(Color.LIGHT_GRAY));
//            defaults.put("JXTitledPanel.title.font", UIManager.getFont("Button.font"));
//        } else if (laf.getID().equals("Metal")) {
//            UIDefaults defaults = UIManager.getDefaults();
//            defaults.put(uiClassID, "org.jdesktop.swingx.plaf.metal.MetalTitledPanelUI");
//            //adds some keys to the UIDefaults for the JXTitledPanel component
//            MetalLookAndFeel mlaf = (MetalLookAndFeel)laf;
//            defaults.put("JXTitledPanel.title.foreground", new ColorUIResource(255, 255, 255));
//            defaults.put("JXTitledPanel.title.darkBackground", mlaf.getCurrentTheme().getPrimaryControlDarkShadow());
//            defaults.put("JXTitledPanel.title.lightBackground", mlaf.getCurrentTheme().getPrimaryControl());
//            defaults.put("JXTitledPanel.title.font", UIManager.getFont("Button.font"));
//        } else {
//            UIDefaults defaults = UIManager.getDefaults();
//            defaults.put(uiClassID, "org.jdesktop.swingx.plaf.metal.MetalTitledPanelUI");
//            //adds some keys to the UIDefaults for the JXTitledPanel component
//            defaults.put("JXTitledPanel.title.foreground", new ColorUIResource(Color.WHITE));
//            defaults.put("JXTitledPanel.title.darkBackground", new ColorUIResource(Color.GRAY));
//            defaults.put("JXTitledPanel.title.lightBackground", new ColorUIResource(Color.LIGHT_GRAY));
//            defaults.put("JXTitledPanel.title.font", UIManager.getFont("Button.font"));
//        }
//    }
    
	/**
	 * The text to use for the title
	 */
	private String title = "";
    /**
     * The Font to use for the Title
     */
    private Font titleFont;
    /**
     * For the gradient, this is the background color to use for the
     * dark part of the gradient
     */
    private Color titleDarkBackground;
    /**
     * For the gradient, this is the light color to use for the light part
     * of the gradient
     */
    private Color titleLightBackground;
    /**
     * The forground color to use for the Title (particularly for the text)
     */
    private Color titleForeground;
	/**
	 * The ContentPanel.  Whatever this container is will be displayed in the
	 * Content section
	 */
	private Container contentPanel;

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
		setTitle(title);
        setContentContainer(new JXPanel());
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
     * Resets the UI property with a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
//    public void updateUI() {
//        //this check was added because apparently loadDefaults is not called
//        //in netbeans?? Maybe I just needed to close & restart netbeans???
//        Object ui = UIManager.getUI(this);
//        if (!(ui instanceof TitledPanelUI)) {
//            loadDefaults();
////            ui = UIManager.getUI(this);
//            ui = MetalTitledPanelUI.createUI(this);
//        }
//        setUI((TitledPanelUI)ui);
//    }

    /**
     * Returns the look and feel (L&F) object that renders this component.
     *
     * @return the TitledPanelUI object that renders this component
     * @since 1.4
     */
    public TitledPanelUI getUI() {
        return (TitledPanelUI)ui;
    }


    /**
     * Sets the look and feel (L&F) object that renders this component.
     *
     * @param ui  the TitledPanelUI L&F object
     * @see UIDefaults#getUI
     * @since 1.4
     * @beaninfo
     *        bound: true
     *       hidden: true
     *    attribute: visualUpdate true
     *  description: The UI object that implements the Component's LookAndFeel. 
     */
    public void setUI(TitledPanelUI ui) {
        super.setUI(ui);
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
        // JW: fix swingx #9 - missing/incorrect notification
        // let standard notification handle
        // NOTE - "getting" the new property in the fire method is 
        //   intentional: there's no way of missing any transformations
        //   on the parameter to set (like above: setting a  
        //   value depending on whether the input is null). 
        firePropertyChange("title", oldTitle, getTitle());
//		PropertyChangeEvent event = new PropertyChangeEvent(this, "title", oldTitle, title);
//		PropertyChangeListener[] listeners = this.getPropertyChangeListeners("title");
//		for (int i = 0; i < listeners.length; i++) {
//			listeners[i].propertyChange(event);
//		}
	}
	
	/**
	 * @return
	 */
	public Container getContentContainer() {
        if (contentPanel == null) {
            contentPanel = new JXPanel();
            ((JXPanel)contentPanel).setBorder(BorderFactory.createEmptyBorder());
            this.add(contentPanel, BorderLayout.CENTER);
        }
		return contentPanel;
	}
	
	public void setContentContainer(Container contentPanel) {
        if (this.contentPanel != null) {
    		remove(this.contentPanel);
        }
		add(contentPanel, BorderLayout.CENTER);
		this.contentPanel = contentPanel;
	}
	
	/**
	 * Adds the given JComponent as a decoration on the right of the title
	 * @param decoration
	 */
	public void addRightDecoration(JComponent decoration) {
        getUI().addRightDecoration(decoration);
	}

	/**
	 * Adds the given JComponent as a decoration on the left of the title
	 * @param decoration
	 */
	public void addLeftDecoration(JComponent decoration) {
        getUI().addLeftDecoration(decoration);
	}

    public Font getTitleFont() {
        return titleFont;
    }

    public void setTitleFont(Font titleFont) {
        Font old = getTitleFont();
        this.titleFont = titleFont;
        firePropertyChange("titleFont", old, getTitleFont());
    }

    public Color getTitleDarkBackground() {
        return titleDarkBackground;
    }

    public void setTitleDarkBackground(Color titleDarkBackground) {
        Color old = getTitleDarkBackground();
        this.titleDarkBackground = titleDarkBackground;
        firePropertyChange("titleDarkBackground", old, getTitleDarkBackground());
    }

    public Color getTitleLightBackground() {
        return titleLightBackground;
    }

    public void setTitleLightBackground(Color titleLightBackground) {
        Color old = getTitleLightBackground();
        this.titleLightBackground = titleLightBackground;
        firePropertyChange("titleLightBackground", old, getTitleLightBackground());
    }

    public Color getTitleForeground() {
        return titleForeground;
    }

    public void setTitleForeground(Color titleForeground) {
        Color old = getTitleForeground();
        this.titleForeground = titleForeground;
        firePropertyChange("titleForeground", old, getTitleForeground());
    }

}