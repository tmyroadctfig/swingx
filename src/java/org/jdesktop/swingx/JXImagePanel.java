/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Cursor;

import java.io.File;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.border.BevelBorder;


public class JXImagePanel extends JPanel {

    private JLabel imageLabel;

    private String TEXT = "<html><i><b>Click on the pane<br>To set the image</b></i></html>";

    public JXImagePanel() {
	setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

	imageLabel = new JLabel(TEXT);
	imageLabel.setPreferredSize(new Dimension(200,200));
	imageLabel.addMouseListener(new MouseHandler());
	imageLabel.setToolTipText(TEXT);
	add(imageLabel);
    }

    public void setIcon(Icon icon) {
	imageLabel.setIcon(icon);
	if (icon != null) {
	    imageLabel.setText(null);
	}
	else {
	    imageLabel.setText(TEXT);
	}
    }

    public Icon getIcon() {
	return imageLabel.getIcon();
    }

    public void setImage(Image image) {
	if (image != null) {
	    setIcon(new ImageIcon(image));
	} else {
	    setIcon(null);
	}
    }

    public Image getImage() {
	ImageIcon icon = (ImageIcon)imageLabel.getIcon();
	if (icon != null) {
	    return icon.getImage();
	}
	return null;
    }

    private class MouseHandler extends MouseAdapter {
	private Cursor oldCursor;
	private JFileChooser chooser;

	public void mouseClicked(MouseEvent evt) {
	    if (chooser == null) {
		chooser = new JFileChooser();
	    }
	    int retVal = chooser.showOpenDialog(JXImagePanel.this);
	    if (retVal == JFileChooser.APPROVE_OPTION) {
		File file = chooser.getSelectedFile();
		try {
		    setIcon(new ImageIcon(file.toURL()));
		} catch (Exception ex) {
		}
	    }
	}

	public void mouseEntered(MouseEvent evt) {
	    JLabel label = (JLabel)evt.getSource();
	    if (oldCursor == null) {
		oldCursor = label.getCursor();
		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    }
	}

	public void mouseExited(MouseEvent evt) {
	    JLabel label = (JLabel)evt.getSource();
	    if (oldCursor != null) {
		label.setCursor(oldCursor);
		oldCursor = null;
	    }
	}
    }
}

