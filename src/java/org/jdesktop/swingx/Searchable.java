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

package org.jdesktop.swingx;

import java.util.regex.Pattern;

/**
 * Searchable
 *
 * @author Ramesh Gupta
 */
public interface Searchable {

    /**
     * Search from the beginning of a document
     */
    public int search(String searchString);
    public int search(String searchString, int startIndex);
    public int search(String searchString, int startIndex, boolean backward);
    
    public int search(Pattern pattern);

    /**
     * Search for the pattern from the start index.
     * @param pattern Pattern for search
     * @param startIndex starting index of search. If -1 then start from the beginning
     * @return index of matched pattern. -1 if a match cannot be found.
     */
    public int search(Pattern pattern, int startIndex);

    /**
     * Search for the pattern from the start index.
     * @param pattern Pattern for search
     * @param startIndex starting index of search. If -1 then start from the beginning
     * @param backward indicates the direction if true then search is backwards
     * @return index of matched pattern. -1 if a match cannot be found.
     */
    public int search(Pattern pattern, int startIndex, boolean backward);
}
