/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.jdesktop.swingx.painter.gradient.BasicGradientPainter;
import org.jdesktop.swingx.plaf.HeaderUI;
import org.jdesktop.swingx.plaf.JXHeaderAddon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;

/**
 * <p><code>JXHeader is a simple component consisting of a title, a description, 
 * and an icon. An example of such a component can be seen on
 * <a href="http://jext.free.fr/header.png">Romain Guys ProgX website</a></p>
 *
 * <p><code>JXHeader</code> is a simple component that is also sufficiently 
 * configurable to be useable. The description area
 * accepts HTML conforming to version 3.2 of the HTML standard. The icon, title,
 * and description are all configurable. <code>JXHeader</code> itself extends 
 * {@link JXPanel}, providing translucency and painting delegates.</p>
 *
 * <p>If I were to reconstruct the ui shown in the above screenshot, I might
 * do so like this:<br/>
 * <pre><code>
 *      JXHeader header = new JXHeader();
 *      header.setTitle("Timing Framework Spline Editor");
 *      header.setDescription("Drag control points in the display to change the " +
 *          "shape of the spline\n" +
 *          "Click the Copy Code button to generate the corrosponding Java code.");
 *      Icon icon = new ImageIcon(getClass().getResource("tools.png"));
 *      header.setIcon(icon);
 * </code></pre></p>
 * 
 * Note: The HTML support doesn't exist yet. The UI delegate needs to discover whether
 * the text supplied is HTML or not, and change the content type of the editor pane
 * being used. The problem is that if "text/html" is always used, the font is wrong.
 * This same situation will be found in other parts of the code (JXErrorPane, for instance),
 * so this needs to be dealt with.
 *
 * <h2>Defaults</h2>
 * <p>BasicHeaderUI uses the following UI defaults:
 *  <ul>
 *      <li><b>Header.defaultIcon:</b> The default icon to use when creating a new JXHeader.</li>
 *  </ul>
 * </p>
 * 
 * @status REVIEWED
 * @author rbair
 */
public class JXHeader extends JXPanel {
    /**
     * JXTaskPane pluggable UI key <i>swingx/TaskPaneUI</i>
     */
    public final static String uiClassID = "HeaderUI";
    
    // ensure at least the default ui is registered
    static {
        LookAndFeelAddons.contribute(new JXHeaderAddon());
    }
    
    private String title;
    private String description;
    private Icon icon;
    private Font titleFont;
    private Font descriptionFont;
    
    /** Creates a new instance of JXHeader */
    public JXHeader() {
    }
    
    /** 
     * Creates a new instance of JXHeader. PropertyChangeEvents are fired
     * when the title and description properties are set.
     * 
     * @param title specifies the title property for this JXHeader
     * @param description specifies the description property for this JXHeader
     */
    public JXHeader(String title, String description) {
        this(title, description, null);
    }
    
    /** Creates a new instance of JXHeader. PropertyChangeEvents are fired
     * when the title and description properties are set.
     * 
     * @param title specifies the title property for this JXHeader
     * @param description specifies the description property for this JXHeader
     * @param icon specifies the icon property for this JXHeader
     */
    public JXHeader(String title, String description, Icon icon) {
        setTitle(title);
        setDescription(description);
        setIcon(icon);
    }
    
    //------------------------------------------------------------- UI Logic
    
    /**
     * @inheritDoc
     */
    public HeaderUI getUI() {
        return (HeaderUI)super.getUI();
    }
    
    /**
     * Sets the look and feel (L&F) object that renders this component.
     *
     * @param ui the HeaderUI L&F object
     * @see javax.swing.UIDefaults#getUI
     */
    public void setUI(HeaderUI ui) {
        super.setUI(ui);
    }
    
    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return the string {@link #uiClassID}
     * @see javax.swing.JComponent#getUIClassID
     * @see javax.swing.UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }
    
    /**
     * Notification from the <code>UIManager</code> that the L&F has changed.
     * Replaces the current UI object with the latest version from the
     * <code>UIManager</code>.
     *
     * @see javax.swing.JComponent#updateUI
     */
    @Override
    public void updateUI() {
        setUI((HeaderUI) LookAndFeelAddons
                .getUI(this, HeaderUI.class));
    }
    
    /**
     * Sets the title to use. This may be either plain text, or a simplified
     * version of HTML, as JLabel would use.
     * 
     * @param title the Title. May be null.
     */
    public void setTitle(String title) {
        String old = getTitle();
        this.title = title;
        firePropertyChange("title", old, getTitle());
    }
    
    /**
     * Gets the title.
     * @return the title. May be null.
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the description for this header. This may use HTML, such as
     * that supported by JEditorPane (version 3.2 of the HTML spec).
     * 
     * @param description the description. May be null, may be HTML or plain text.
     */
    public void setDescription(String description) {
        String old = getDescription();
        this.description = description;
        firePropertyChange("description", old, getDescription());
    }
    
    /**
     * Gets the description.
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the icon to use for the header. It is generally recommended that this 
     * be an image 64x64 pixels in size, and that the icon have no gaps at the top.
     * 
     * @param icon may be null
     */
    public void setIcon(Icon icon) {
        Icon old = getIcon();
        this.icon = icon;
        firePropertyChange("icon", old, getIcon());
    }
    
    /**
     * Gets the icon.
     * 
     * @return the Icon being used. May be null.
     */
    public Icon getIcon() {
        return icon;
    }
    
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        setTitleFont(font);
        setDescriptionFont(font);
    }
    
    private void setTitleFont(Font font) {
        Font old = getTitleFont();
        this.titleFont = font;
        firePropertyChange("titleFont", old, getTitleFont());
    }
    
    private Font getTitleFont() {
        return titleFont;
    }
    
    private void setDescriptionFont(Font font) {
        Font old = getDescriptionFont();
        this.descriptionFont = font;
        firePropertyChange("descriptionFont", old, getDescriptionFont());
    }
    
    private Font getDescriptionFont() {
        return descriptionFont;
    }
    
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
////                try {
////                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
////                } catch (Exception ex) {
////                    ex.printStackTrace();
////                }
//                javax.swing.JFrame frame = new javax.swing.JFrame();
//                frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//                frame.add(new JXHeader());
//                frame.setSize(500, 125);
//                frame.setLocation(org.jdesktop.swingx.util.WindowUtils.getPointForCentering(frame));
//                frame.setVisible(true);
//            }
//        });
//    }
}
