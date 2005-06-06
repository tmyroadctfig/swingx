/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.auth;

import java.util.EventObject;

/*
 * <b>LoginListener</b> provides a listener for the actual login
 * process.
 *
 * @author Bino George
 * @author Shai Almog
 */
public interface LoginListener {
    
    /**
     *  Called by the <strong>JXLoginPanel</strong> in the event of a login failure
     *
     * @param source panel that fired the event
     */
    public void loginFailed(LoginEvent source);
    /**
     *  Called by the <strong>JXLoginPanel</strong> when the Authentication
     *  operation is started.
     * @param source panel that fired the event
     */
    public void loginStarted(LoginEvent source);
    /**
     *  Called by the <strong>JXLoginPanel</strong> in the event of a login
     *  cancellation by the user.
     *
     * @param source panel that fired the event
     */
    public void loginCanceled(LoginEvent source);
    /**
     *  Called by the <strong>JXLoginPanel</strong> in the event of a
     *  successful login.
     *
     * @param source panel that fired the event
     */
    public void loginSucceeded(LoginEvent source);
}
