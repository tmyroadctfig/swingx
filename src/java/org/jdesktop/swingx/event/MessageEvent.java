/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.event;

import java.util.EventObject;

import java.util.logging.Level;

/**
 * Represents a message passed from a MessageSource. This class contains
 * properties which indicate the level of the message, a string which represents
 * the user visible message text and an indication of when the message
 * occured.
 * <p>
 * The message could represent text messages and exceptions. If this message
 * represents an exception then the value of {@link #getThrowable} will be 
 * non null. Messages are categorized using the
 * {@link java.util.logging.Level } constants. 
 * <p>
 */
public class MessageEvent extends EventObject {

    private Object value;
    private long when;
    private Level level = Level.INFO;

    // XXX This is only defined so that subclasses can get access to
    // EventObject(Object)
    public MessageEvent(Object source) {
	super(source);
    }

    /**
     * Create a <code>Level.INFO</code> message.
     */
    public MessageEvent(Object source, Object message) {
	this(source, message, Level.INFO);
    }

    
    public MessageEvent(Object source, Object value, Level level) {
	this(source, value, level, 0L);
    }

    /**
     * Constructs a <code>MessageEvent</code>
     *
     * @param source the object that originated the event
     * @param value an object which represents the contents of the event
     * @param level indicate the level of the event
     * @param when timestamp of the message
     */
    public MessageEvent(Object source, Object value, Level level, long when) {
	super(source);
	this.value = value;
	this.level = level;
	this.when = when;
    }

    /**
     * Returns the value as a String message. If the value represents an
     * exception, then the message from the exception is returned.
     * 
     * @return the value as a String or the empty string if the value is null
     */
    public String getMessage() {
	if (value != null) {
	    Throwable t = getThrowable();
	    if (t != null) {
		return t.getMessage();
	    } else {
		return value.toString();
	    }
	} else {
	    return "";
	}
    }

    /**
     * Returns the value as a Throwable. 
     *
     * @return the exception passed as a value or null if it is not an exception
     */
    public Throwable getThrowable() {
	if (value != null && value instanceof Throwable) {
	    return (Throwable)value;
	}
	return null;
    }

    /**
     * Returns the contents of the message. This level is based on the
     * context of the message.
     */
    public Object getValue() {
	return value;
    }

    /**
     * Time in milliseconds when the event occured.
     */
    public long getWhen() {
	return when;
    }

    /**
     * Returns the level of message. This method will always return a valid
     * value. The default is set to Level.INFO.
     *
     * @return the level of the message
     */
    public Level getLevel() {
	if (level == null) {
	    level = Level.INFO;
	}
	return level;
    }

    public String toString() {
	String message = "value=" + getMessage();
	message += ", level=" + getLevel();
	message += ", when=" + getWhen();

	return message;
    }
}
