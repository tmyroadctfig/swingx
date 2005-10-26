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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.event.MessageEvent;
import org.jdesktop.swingx.event.MessageListener;
import org.jdesktop.swingx.event.ProgressEvent;
import org.jdesktop.swingx.event.ProgressListener;


/**
 * A component which is a container for displaying messages. There
 * are several regions in which information about the running application
 * may be placed.
 * <p>
 * You may set the messages directly using <code>setText</code>,
 * <code>setTrailingMessage</code> or <code>setLeadingMessage</code>.
 * Alternatively, you can register the status bar as a
 * <code>MessageListener</code> on a <code>MessageSource</code>
 * and messages will be placed according to type.
 *
 * @author Mark Davidson
 */
public class JXStatusBar extends JPanel implements MessageListener,
						  ProgressListener {
    private JLabel leadingLabel;
    private JLabel trailingLabel;
    private JProgressBar progressBar;

    private Dimension preferredSize;

    public JXStatusBar() {
        super();
	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createLoweredBevelBorder());

	leadingLabel = (JLabel) add(new JLabel("", SwingConstants.LEADING));
	add(Box.createHorizontalGlue());

	progressBar = (JProgressBar)add(new JProgressBar());
	progressBar.setVisible(false);

	trailingLabel = (JLabel) add(new JLabel("", SwingConstants.TRAILING));

        Font font = leadingLabel.getFont().deriveFont(Font.PLAIN);
        leadingLabel.setFont(font);
        trailingLabel.setFont(font);
	this.setFont(font);

        preferredSize = new Dimension(getWidth("    "), 2 * getFontHeight());
    }

    /**
     * Sets non-transient message text in leading message position on
     * status bar.
     * @param messageText the message to display on the status bar
     */
    public void setText(String messageText) {
	setLeadingMessage(messageText);
    }

    public String getText() {
	return getLeadingMessage();
    }

    /**
     * Places the message in the leading area.
     *
     * @param messageText the text to place
     */
    public void setLeadingMessage(String messageText) {
	leadingLabel.setText(messageText);
    }

    public String getLeadingMessage() {
	return leadingLabel.getText();
    }

    /**
     * Places the message in the trailing area.
     *
     * @param messageText the text to place
     */
    public void setTrailingMessage(String messageText) {
	trailingLabel.setText(messageText);
    }

    public String getTrailingMessage() {
	return trailingLabel.getText();
    }


    /**
     * Returns the string width
     * @param s the string
     * @return the string width
     */
    protected int getWidth(String s) {
	FontMetrics fm = this.getFontMetrics(this.getFont());
	if (fm == null) {
	    return 0;
	}
	return fm.stringWidth(s);
    }

    /**
     * Returns the height of a line of text
     * @return the height of a line of text
     */
    protected int getFontHeight() {
	FontMetrics fm = this.getFontMetrics(this.getFont());
	if (fm == null) {
	    return 0;
	}
	return fm.getHeight();
    }

    /**
     * Returns the perferred size
     * @return the preferred size
     */
    public Dimension getPreferredSize() {
	return preferredSize;
    }

    /**
     * MessageListener implementation. This handles many of the message types.
     */
    public void message(MessageEvent evt) {
	Level level = evt.getLevel();

	if (level == Level.FINE) {
	    // transient messages are sent to the leading label.
	    setLeadingMessage(evt.getMessage());
	}
	else /*if (level == Level.INFO)*/ {
	    // persisent messages are sent to the trailing label.
	    setTrailingMessage(evt.getMessage());
	}

	// Message categories like SEVERE, WARNING and exceptions should
	// probably be logged. Perhap even INFO messages should be passed to
	// to the logger.
	/*
	Throwable t = evt.getThrowable();
	if (t != null) {
	    // how do we want to handle exceptions?
	}
	*/
    }

    // ProgressListener implementation

    /**
     * Indicates the begining of a long operation.
     */
    public void progressStarted(ProgressEvent evt) {
	// Set up the progress bar to handle a new progress event.
	boolean indeterminate = evt.isIndeterminate();
	progressBar.setIndeterminate(indeterminate);
	if (indeterminate == false) {
	    progressBar.setValue(evt.getMinimum());
	    progressBar.setMinimum(evt.getMinimum());
	    progressBar.setMaximum(evt.getMaximum());
	}
	progressBar.setVisible(true);
    }

    /**
     * Handles a ProgressEvent
     */
    public void progressIncremented(ProgressEvent evt) {
	progressBar.setValue(evt.getProgress());
    }

    /**
     * Indicates the end of a long operation.
     */
    public void progressEnded(ProgressEvent evt) {
	progressBar.setVisible(false);
    }
}
