/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.auth;

import java.io.IOException;
import java.util.EventObject;

/**
 * This is an event object that is passed to login listener methods
 *
 * @author Shai Almog
 */
public class LoginEvent extends EventObject {
    private IOException cause;
    
    public LoginEvent(Object source) {
        this(source, null);
    }
    
    /** Creates a new instance of LoginEvent */
    public LoginEvent(Object source, IOException cause) {
        super(source);
        this.cause = cause;
    }
    
    public IOException getCause() {
        return cause;
    }
}
