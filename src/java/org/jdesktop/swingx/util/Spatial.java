/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the distance from the left of the space for the spatial object
	 * @return the left distance
	 */
	public int getLeft() {
		return left;
	}

	/**
	 * Get the distance from the top of the space for the spatial object
	 * @return the top distance
	 */
	public int getTop() {
		return top;
	}

	/**
	 * Get the width of the spatial object
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Spatial) {
			Spatial s = (Spatial)obj;
			return height == s.height && left == s.left && top == s.top && width == s.width;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return cachedHash;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return cachedAsString;
	}
}
