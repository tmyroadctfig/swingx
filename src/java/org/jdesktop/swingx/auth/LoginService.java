package org.jdesktop.swingx.auth;

/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */


import java.util.*;
import java.awt.EventQueue;
/**
 * <b>LoginService</b> is the abstract base class for all classes implementing
 * a login mechanism. It allows you to customize the threading behaviour
 * used to perform the login. Subclasses need to override the <b>authenticate</b>
 * method.
 *
 * @author Bino George
 */
public abstract class LoginService {
    
     private Vector<LoginListener> listenerList = new Vector<LoginListener>();

     private Thread loginThread;
    
     /*
      * Controls the authentication behaviour to be either
      * synchronous or asynchronous
      */
     private boolean synchronous;
     private boolean canceled;
     private String server;
     
     
     public LoginService(String serverName) {
     	setServer(serverName);
     }
     
    /**
     * This method is intended to be implemented by clients
     * wishing to authenticate a user with a given password.
     * Clients should implement the authentication in a 
     * manner that the authentication can be cancelled at
     * any time.
     *
     * @param name username
     * @param password password
     * @param server server (optional)
     */
    public abstract boolean authenticate(String name, char[] password, String server);
    
    /**
     * Notifies the LoginService that an already running
     * authentication request should be cancelled. This
     * method is intended to be used by clients who want
     * to provide user with control over cancelling a long
     * running authentication request.
     */
    public void cancelAuthentication() {
    	canceled = true;
    	EventQueue.invokeLater(new Runnable() {
            public void run() {
                fireLoginCanceled(new EventObject(this)); 
            } 
         });
    }
  
    /**
     * This method is intended to be overridden by subclasses
     * to customize the threading to use pooling etc. The default
     * implementation just creates a new Thread with the given runnable.
     *
     * @param runnable runnable
     */
    public Thread getLoginThread(Runnable runnable) {
      return new Thread(runnable);  
    }
    
    /**
     * This method starts the authentication process and is either
     * synchronous or asynchronous based on the synchronous property
     * 
     * @param user user
     * @param password password
     * @param server server
     */
    public void startAuthentication(final String user, final char[] password, final String server) {
       canceled = false;
       if (getSynchronous()) {
         if (authenticate(user,password,server)) {
           fireLoginSucceeded(new EventObject(this));  
         } else {
           fireLoginFailed(new EventObject(this));  
         }  
       } else {
         Runnable runnable = new Runnable() {
           public void run() {
             final boolean result = authenticate(user,password,server);
             if (!canceled) {
             EventQueue.invokeLater(new Runnable() {
                public void run() {
                   if (result) {
                      fireLoginSucceeded(new EventObject(this));  
                   }
                   else {
                      fireLoginFailed(new EventObject(this)); 
                   }
                } 
             });
             }
           }  
         };  
         loginThread = getLoginThread(runnable) ; 
         loginThread.start();
       }
    }
    
    /**
     * Returns the synchronous property
     */
    public boolean getSynchronous() {
        return synchronous;
    }
    /**
     * Sets the synchronous property
     * 
     * @param synchronous synchronous property
     */
    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }
    
    /**
     * Adds a <strong>LoginListener</strong> to the list of listeners
     *
     * @param listener listener
     */
    
    public void addLoginListener(LoginListener listener) {
        listenerList.add(listener);
    }
    
    /**
     * Removes a <strong>LoginListener</strong> from the list of listeners
     *
     * @param listener listener
     */
    public void removeLoginListener(LoginListener listener) {
        listenerList.remove(listener);
    }
    
    
    void fireLoginSucceeded(final EventObject source) {
        Iterator iter = listenerList.iterator();
        while (iter.hasNext()) {
            LoginListener listener = (LoginListener) iter.next();
            listener.loginSucceeded(source);
        }
    }
    
    void fireLoginFailed(final EventObject source) {
        Iterator iter = listenerList.iterator();
        while (iter.hasNext()) {
            LoginListener listener = (LoginListener) iter.next();
            listener.loginFailed(source);
        }
    }
    
    void fireLoginCanceled(final EventObject source) {
        Iterator iter = listenerList.iterator();
        while (iter.hasNext()) {
            LoginListener listener = (LoginListener) iter.next();
            listener.loginCanceled(source);
        }
    }
 
    
	/**
	 * @return Returns the server.
	 */
	public String getServer() {
		return server;
	}
	/**
	 * @param server The server to set.
	 */
	public void setServer(String server) {
		this.server = server;
	}
}
