/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.event;

/**
 * Interface for MessageListener registrations methods and indicates that the
 * implementation class is a source of MessageEvents. 
 * MessageListeners which are interested in MessageEvents from this class can
 * register themselves as listeners. 
 * 
 * @see MessageEvent
 * @see MessageListener
 * @author Mark Davidson
 */
public interface MessageSource  {

    /**
     * Register the MessageListener. 
     * 
     * @param l the listener to register
     */
    void addMessageListener(MessageListener l);

    /**
     * Unregister the MessageListener from the MessageSource.
     * 
     * @param l the listener to unregister
     */
    void removeMessageListener(MessageListener l);

    /**
     * Returns an array of listeners.
     *
     * @return an non null array of MessageListeners.
     */
    MessageListener[] getMessageListeners();
}
