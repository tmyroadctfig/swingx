/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.event;

import java.util.EventListener;

/**
 * The listener interface for recieving message events.
 * The class interested in handling {@link MessageEvent}s should implement
 * this interface. The complementary interface would be {@link MessageSource}
 * 
 * @see MessageEvent
 * @see MessageSource
 * @author Mark Davidson
 */
public interface MessageListener extends java.util.EventListener {

    /**
     * Invoked to send a message to a listener. The {@link MessageEvent}
     * is qualified depending on context. It may represent a simple
     * transient messages to be passed to the ui or it could
     * represent a serious exception which has occured during 
     * processing. 
     * <p>
     * The implementation of this interface should come up 
     * with a consistent policy to reflect the business logic
     * of the application.
     * 
     * @param evt an object which describes the message
     */
    void message(MessageEvent evt);
}
