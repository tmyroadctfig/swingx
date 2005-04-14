/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
            defaults.put(uiClassID, "org.jdesktop.swingx.plaf.metal.MetalScrollUpUI");
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
    
    private int cachedPreferredHeight = -1;

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
            cachedPreferredHeight = collapsed ? getPreferredSize().height : -1;
            firePropertyChange("collapsed", !collapsed, collapsed);
            revalidate();
            repaint();
        }
    }
    
	/* (non-Javadoc)
	 * @see java.awt.Component#getMinimumSize()
	 */
	public Dimension getMinimumSize() {
		//calculate the minimum size that this scrollup can be and still display all of its data
		Dimension dim = super.getContentContainer().getPreferredSize();
		Dimension dim2 = super.getPreferredSize();
		return new Dimension(Math.max(dim2.width, dim.width), dim2.height - dim.height);
	}
	/* (non-Javadoc)
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
        if (collapsed) {
            Dimension dim = super.getContentContainer().getPreferredSize();
            Dimension dim2 = super.getPreferredSize();
            return new Dimension(Math.max(dim2.width, dim.width), dim2.height - dim.height);
        } else {
            return super.getPreferredSize();
        }
	}
}
