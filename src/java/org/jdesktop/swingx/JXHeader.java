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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JSeparator;

/**
 * <p>A simple component that consists of a title, a description, and an icon.
 * An example of such a component can be seen on
 * <a href="http://jext.free.fr/header.png">Romain Guys ProgX website</a></p>
 *
 * <p>The JXHeader is sufficiently configurable to be useable. The description area
 * accepts HTML (the same set of HTML that {@link org.jdesktop.swingx.JXEditorPane} 
 * supports), and thus allows a great deal of flexibility. The icon and the
 * various bits of text is configurable. The JXHeader itself extends JXPanel, and
 * so allows a gradient to be painted in the background.</p>
 *
 * <p>If I were to reconstruct the JXHeader shown in the above screenshot, I might
 * do so like this:<br/>
 * <pre><code>
 *      JXHeader header = new JXHeader();
 *      header.setTitle("Timing Framework Spline Editor");
 *      header.setDescription("<html><body>" +
 *          "Drag control points in the display to change the shape of the spline<br/>" +
 *          "Click the Copy Code button to generate the corrosponding Java code." +
 *          "</body></html>");
 *      Icon icon = new ImageIcon(getClass().getResource("tools.png"));
 *      header.setIcon(icon);
 *      header.setDrawGradient(true);
 * </code></pre></p>
 * 
 * @author rbair
 */
public class JXHeader extends JXPanel {
    private JLabel titleLabel;
    private JXEditorPane descriptionPane;
    private JLabel imagePanel;
    
    /** Creates a new instance of JXHeader */
    public JXHeader() {
        initGui();
    }
    
    private void initGui() {
        setLayout(new GridBagLayout());
        
        titleLabel = new JLabel("Title");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(12, 12, 0, 11), 0, 0));
        
        descriptionPane = new JXEditorPane();
        descriptionPane.setContentType("text/html");
        descriptionPane.setEditable(false);
        descriptionPane.setOpaque(false);
        descriptionPane.setText("<html><body><p> </p><p> </p></body></html>");
        add(descriptionPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 24, 0, 11), 0, 0));

        imagePanel = new JLabel();
        add(imagePanel, new GridBagConstraints(1, 0, 1, 2, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(12, 0, 11, 11), 0, 0));

        add(new JSeparator(), new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        
        setDrawGradient(true);
    }
    
    /**
     * @inheritDoc
     * Passes enabled state on to the child components
     */
    @Override
    public void setEnabled(boolean enabled) {
        titleLabel.setEnabled(enabled);
        descriptionPane.setEnabled(enabled);
        imagePanel.setEnabled(enabled);
        super.setEnabled(enabled);
    }
    
    public void setTitle(String title) {
        String old = getTitle();
        titleLabel.setText(title);
        firePropertyChange("title", old, getTitle());
    }
    
    public String getTitle() {
        return titleLabel.getText();
    }
    
    public void setDescription(String description) {
        String old = getDescription();
        descriptionPane.setText(description);
        firePropertyChange("description", old, getDescription());
    }
    
    public String getDescription() {
        return descriptionPane.getText();
    }
    
    public void setIcon(Icon icon) {
        Icon old = getIcon();
        imagePanel.setIcon(icon);
        firePropertyChange("icon", old, getIcon());
    }
    
    public Icon getIcon() {
        return imagePanel.getIcon();
    }
}
