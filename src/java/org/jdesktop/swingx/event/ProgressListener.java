/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.event;

import java.util.EventListener;

/**
 * The listener interface for recieving progress events.
 * The class interested in handling {@link ProgressEvent}s should implement
 * this interface. The complementary interface would be {@link MessageSource}
 *
 * @see ProgressEvent
 * @see MessageSource
 * @author Mark Davidson
 */
public interface ProgressListener extends java.util.EventListener {

    /**
     * Indicates the start of a long operation. The <code>ProgressEvent</code>
     * will indicate if this is a determinate or indeterminate operation.
     *
     * @param evt an object which describes the event
     */
    void progressStarted(ProgressEvent evt);


    /**
     * Indicates that the operation has stopped.
     */
    void progressEnded(ProgressEvent evt);

    /**
     * Invoked when an increment of progress is sent. This may not be
     * sent if an indeterminate progress has started.
     *
     * @param evt an object which describes the event
     */
    void progressIncremented(ProgressEvent evt);
}
