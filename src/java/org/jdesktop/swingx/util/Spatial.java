/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.util;

/**
 * <p>
 * Represents a <b>location</b>, and a <b>size</b> (magnitude).<br>
 * In general, the <code>Spatial</code> class is intended to track the
 * size and position of windows in space.  Hence the term.
 * </p>
 * <p>
 * NOTE: This is an immutable object
 * </p>
 * @author Richard Bair
 */
public final class Spatial {
	/**
	 * measure of the distance from the top of space (ie: top of the screen)
	 * to the top of the object
	 */
	private int top;
	/**
	 * measure of the distance from the left of space (ie: left of the screen)
	 * to the left side of the object
	 */
	private int left;
	/**
	 * measure of the width of the object
	 */
	private int width;
	/**
	 * measure of the height of the object
	 */
	private int height;
	private int cachedHash;
	private String cachedAsString;
	
	/**
	 * Create a new Spatial object.
	 * @param top @see top
	 * @param left @see left
	 * @param width @see width
	 * @param height @see height
	 */
	public Spatial(int top, int left, int width, int height) {
		this.top = top;
		this.left = left;
		this.width = width;
		this.height = height;
		
		cachedHash = (top << 4) + (left << 3) + (width << 2) + (height << 1);
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		buffer.append(top);
		buffer.append(",");
		buffer.append(left);
		buffer.append(",");
		buffer.append(width);
		buffer.append(",");
		buffer.append(height);
		buffer.append("}");
		cachedAsString = buffer.toString();
	}
	
	/**
	 * Get the height of the spatial object
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the distance from the left of the space for the spatial object
	 * @return
	 */
	public int getLeft() {
		return left;
	}

	/**
	 * Get the distance from the top of the space for the spatial object
	 * @return
	 */
	public int getTop() {
		return top;
	}

	/**
	 * Get the width of the spatial object
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Spatial) {
			Spatial s = (Spatial)obj;
			return height == s.height && left == s.left && top == s.top && width == s.width;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return cachedHash;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return cachedAsString;
	}
}
