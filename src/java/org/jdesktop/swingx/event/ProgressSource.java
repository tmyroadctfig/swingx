/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.event;

/**
 * Interface for ProgressListener registrations methods and indicates that the
 * implementation class is a source of ProgressEvents. 
 * ProgressListeners which are interested in ProgressEvents from this class can
 * register themselves as listeners. 
 * 
 * @see ProgressEvent
 * @see ProgressListener
 * @author Mark Davidson
 */
public interface ProgressSource  {

    /**
     * Register the ProgressListener. 
     * 
     * @param l the listener to register
     */
    void addProgressListener(ProgressListener l);

    /**
     * Unregister the ProgressListener from the ProgressSource.
     * 
     * @param l the listener to unregister
     */
    void removeProgressListener(ProgressListener l);

    /**
     * Returns an array of listeners.
     *
     * @return an non null array of ProgressListeners.
     */
    ProgressListener[] getProgressListeners();
}
