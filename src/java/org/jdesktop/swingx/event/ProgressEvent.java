/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.event;

/**
 * A MessageEvent that represents the cycle of a long running operation.
 * Use the constructors to indicate the stage of the operation.
 *
 * @author Mark Davidson
 */
public class ProgressEvent extends MessageEvent  {

    private int minimum;
    private int maximum;
    private int progress;

    private boolean indeterminate = true;

    /**
     * Constructs an indeterminate  progress event.
     */
    public ProgressEvent(Object source) {
	super(source);
    }

    /**
     * Constructs a progress event used to indicate an increment of progress.
     *
     * @param source the object which orignated the event
     * @param progress the value between min and max which indicates 
     *        the progression of the operation.
     */
    public ProgressEvent(Object source, int progress) {
	super(source);
	this.progress = progress;
	setIndeterminate(false);
    }

    /**
     * Constructs a ProgressEvent which indicates the beginning of a long operation.
     * For a determinite progress operation, the minimum value should be less than
     * the maximum value. For indterminate operations, set minimum equal to maximum.
     *
     * @param source the object which orignated the event
     * @param min the minimum value of the progress operation
     * @param max the maximum value of the progress operation
     */
    public ProgressEvent(Object source, int min, int max) {
	super(source);
	setMaximum(max);
	setMinimum(min);
	setIndeterminate(max == min);
    }

    private void setMaximum(int max) {
	this.maximum = max;
    }

    public int getMaximum() {
	return maximum;
    }

    private void setMinimum(int min) {
	this.minimum = min;
    }

    public int getMinimum() {
	return minimum;
    }

    private void setIndeterminate(boolean indeterminate) {
	this.indeterminate = indeterminate;
    }

    public boolean isIndeterminate() {
	return indeterminate;
    }

    public int getProgress() {
	return progress;
    }
}
