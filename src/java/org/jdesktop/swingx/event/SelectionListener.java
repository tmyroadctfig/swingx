/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.event;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;

/**
 * Listener for receiving global selection change notifications.
 * @author Amy Fowler
 * @version 1.0
 */
public interface SelectionListener {

    void valueChanged(ListSelectionEvent e);
    void valueChanged(TreeSelectionEvent e);

}
